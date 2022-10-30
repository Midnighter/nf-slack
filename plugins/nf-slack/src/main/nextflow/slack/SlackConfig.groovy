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

    String key  // TODO: Replace this example attribute.

    /**
     * Construct a configuration instance.
     *
     * @param map A nextflow plugin wrapper instance.
     */
    SlackConfig(Map map) {
        final Map config = map ?: [:]
        // TODO: Replace this example assignment.
        this.key = config.key ?: 'default value'
    }

}
