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

import groovy.transform.PackageScope
import groovy.transform.CompileStatic
import nextflow.Session

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Define the plugin configuration values.
 *
 * The configuration values can be extracted from the map and will be stored as
 * on the instance.
 *
 * Annotating the class with `@PackageScope` restricts access to its attributes and
 * methods to other members of the package.
 *
 * TODO: Describe the configuration of your actual implementation.
 *
 * @author Moritz E. Beber
 */
@PackageScope
@CompileStatic
class SlackConfig {

    /**
     * Whether or not to send a Slack notification on pipeline start.
     */
    protected Boolean onStart

    /**
     * The incoming webhook URL for Slack notifications.
     */
    protected URL notifyURL

    /**
     * An optional, separate incoming webhook URL for Slack alerts.
     */
    protected URL alertURL

    /**
     * Whether or not to include pipeline manifest information in the message.
     */
    protected Boolean includeManifest

    /**
     * Construct a plugin configuration instance.
     *
     * @param map A nextflow configuration instance.
     */
    SlackConfig(final Session session) {
        final Map config = session.config?.navigate('slack') as Map ?: [:]
        this.onStart = config.on_start ?: false
        // The `notify_url` is required.
        this.notifyURL = validateWebhook(config.notify_url as String)
        if (config.alert_url) {
            this.alertURL = validateWebhook(config.alert_url as String)
        } else {
            this.alertURL = this.notifyURL
        }
        this.includeManifest = config.include_manifest ?: false
    }

    /**
     * Validate the given webhook URL.
     *
     * @param hook An incoming webhook URL for Slack.
     * @return A validated webhook URL.
     * @throws MalformedURLException If the webhook URL is malformed.
     * @throws AssertionError If the URL's protocol is neither HTTP nor HTTPS.
     */
    protected static URL validateWebhook(final String hook) {
        URL url
        try {
            url = new URL(hook)
        } catch (MalformedURLException exc) {
            throw exc
        }
        assert url.protocol ==~ /^https?/, "Only HTTP or HTTPS URLs are supported. Found ${url.protocol}."
        return url
    }

    /**
     * Getter for the onStart conditional.
     *
     * @return The onStart condition.
     */
    Boolean getOnStart() {
        return this.onStart
    }

    /**
     * Getter for the notification incoming webhook URL for Slack.
     *
     * @return The notification webhook URL if any.
     */
    URL getNotifyURL() {
        return this.notifyURL
    }

    /**
     * Getter for the alerting incoming webhook URL for Slack.
     *
     * @return The alerting webhook URL if any.
     */
    URL getAlertURL() {
        return this.alertURL
    }

    /**
     * Getter for the includeManifest conditional.
     *
     * @return The includeManifest condition.
     */
    Boolean getIncludeManifest() {
        return this.includeManifest
    }

}
