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

import groovy.json.JsonGenerator
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * Define a representation of a Slack Block Kit payload.
 *
 * @author Moritz E. Beber
 */
@PackageScope
@CompileStatic
class SlackMessage {

    /**
     * Define a JSON generator with appropriate converters for problematic types.
     */
    protected static JsonGenerator generator = new JsonGenerator.Options().build()

    protected String text = ''
    protected List blocks = []

    void setText(String text) {
        this.text = text
    }

    void extendBlocks(List elements) {
        this.blocks += elements
    }

    Map toMap() {
        return [text: this.text, blocks: this.blocks]
    }

    String toJson() {
        return generator.toJson(this.toMap())
    }

}
