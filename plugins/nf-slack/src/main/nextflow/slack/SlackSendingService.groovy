/*
 * Copyright (c) 2022 Moritz E. Beber
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package nextflow.slack

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.cache.CacheConfig
import org.apache.hc.client5.http.impl.cache.CachingHttpClientBuilder
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.client5.http.impl.schedule.ExponentialBackOffSchedulingStrategy
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.HttpEntity
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import org.apache.hc.core5.util.TimeValue

/**
 * Define a service for sending Slack messages.
 *
 * @author Moritz E. Beber
 */
@PackageScope
@Slf4j
@CompileStatic
class SlackSendingService {

    static Long backoffRate = 2
    static TimeValue initialWait = TimeValue.ofSeconds(1)
    static TimeValue maxWait = TimeValue.ofSeconds(30)
    static Integer maxTries = 5

    static void sendMessage(final URL hook, final SlackMessage message) {
        final StringEntity body = new StringEntity(
            message.toJson(), ContentType.APPLICATION_JSON)
        final URI uri = hook.toURI()
        final CloseableHttpClient httpclient = makeClient()
        final HttpPost request = new HttpPost(uri)
        request.setEntity(body)
        for (attempt in 1..maxTries) {
            final CloseableHttpResponse response = httpclient.execute(request)
            final HttpEntity entity = response.getEntity()
            EntityUtils.consume(entity)
            log.debug("${response.getCode()} ${response.getReasonPhrase()}")
            log.debug(entity.toString())
            if (response.getCode() < 200 || response.getCode() >= 300) {
                log.warn("Sending Slack message; attempt ${attempt}/${maxTries} failed.")
                if (attempt == maxTries) {
                    throw new IOException('Failed to send message to Slack.')
                }
            } else {
                break
            }
        }
    }

    protected static CloseableHttpClient makeClient() {
        return CachingHttpClientBuilder
            .create()
            .setCacheConfig(CacheConfig.DEFAULT)
            .setSchedulingStrategy(
                new ExponentialBackOffSchedulingStrategy(
                    backoffRate, initialWait, maxWait
                )
            )
            .setUserAgent("nf-slack ${SlackSendingService.class.getPackage().implementationVersion}")
            .build()
    }

}
