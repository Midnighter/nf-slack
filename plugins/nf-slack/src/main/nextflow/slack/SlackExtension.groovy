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
    protected void init(final Session session) {
        this.session = session
        this.config = new SlackConfig(session.config?.navigate('slack') as Map)
    }

}
