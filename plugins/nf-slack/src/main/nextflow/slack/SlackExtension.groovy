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
import groovy.util.logging.Slf4j
import nextflow.Session
import nextflow.plugin.extension.Function
import nextflow.plugin.extension.PluginExtensionPoint

/**
 * Define your extension.
 *
 * @author Moritz E. Beber
 */
@Slf4j
@CompileStatic
class SlackExtension extends PluginExtensionPoint {

    /**
     * A session holds information about current nextflow execution.
     */
    protected Session session

    /**
     * A custom configuration extracted from the slack scope.
     */
    protected SlackConfig config

    /**
     * An extension that is automatically initialized by nextflow when the session is ready.
     *
     * @param session A nextflow session instance.
     */
    @Override
    protected void init(Session session) {
        this.session = session
        try {
            this.config = new SlackConfig(session)
        } catch (MalformedURLException | AssertionError exc) {
            log.error("${exc.getMessage()}")
            log.error("Aborting.")
            session.abort(exc)
        }
        if (this.config.onStart) {
            notifySlack()
        }
    }

    @Function
    void notifySlack(final Map extra = null) {
        SlackSendingService.sendMessage(this.config.notifyURL, makeMessage(extra))
    }

    @Function
    void alertSlack(final Map extra = null) {
        SlackSendingService.sendMessage(this.config.alertURL, makeMessage(extra))
    }

    protected SlackMessage makeMessage(final Map extra) {
        final FluentSlackMessageBuilder builder = new FluentSlackMessageBuilder()
            .summarizePipeline(this.session.workflowMetadata)
            .addText()
            .addPipelineInfo()
            .addNextflowInfo()

        if (this.config.includeManifest) {
            builder.addManifest()
        }
        if (extra) {
            builder.addExtra(extra)
        }
        return builder.getMessage()
    }

}
