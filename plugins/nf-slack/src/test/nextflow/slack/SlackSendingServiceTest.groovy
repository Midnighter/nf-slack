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

import org.apache.hc.core5.util.TimeValue
import spock.lang.Specification

class SlackSendingServiceTest extends Specification {

    def setup() {
        SlackSendingService.backoffRate = 1
        SlackSendingService.initialWait = TimeValue.ofMilliseconds(200)
        SlackSendingService.maxWait = TimeValue.ofSeconds(1)
        SlackSendingService.maxTries = 2
    }

    def cleanup() {
        SlackSendingService.setDefaultConfig()
    }

    def 'Sending a message should fail on server error'() {
        when:
        SlackSendingService.sendMessage('https://httpbin.org/status/500'.toURL(), new SlackMessage())

        then:
        IOException exc = thrown()
        exc.message.contains('Slack')

    }

    def 'Sending a message should fail on user error'() {
        when:
        SlackSendingService.sendMessage('https://httpbin.org/status/400'.toURL(), new SlackMessage())

        then:
        IOException exc = thrown()
        exc.message.contains('Slack')

    }

    def 'Sending a message can succeed'() {
        when:
        SlackSendingService.sendMessage('https://httpbin.org/status/200'.toURL(), new SlackMessage())

        then:
        notThrown(IOException)

    }

}
