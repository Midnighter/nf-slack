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

import spock.lang.Specification

class SlackMessageTest extends Specification {

    def 'Can construct an empty message'() {
        when:
        SlackMessage msg = new SlackMessage()

        then:
        with(msg) {
            toMap() == [text: '', blocks: []]
            toJson() == '{"text":"","blocks":[]}'
        }
    }

    def 'Can set the message text'() {
        given:
        SlackMessage msg = new SlackMessage()

        when:
        msg.setText('today is a good day')

        then:
        with(msg) {
            toMap() == [text: 'today is a good day', blocks: []]
            toJson() == '{"text":"today is a good day","blocks":[]}'
        }
    }

    def 'Can extend the blocks and order is maintained'() {
        given:
        SlackMessage msg = new SlackMessage()
        Map header = [type: 'header',
                      text: [type: 'plain_text',
                             text: 'Headline']]
        Map divider = [type: 'divider']

        when:
        msg.extendBlocks([header, divider])

        then:
        with(msg) {
            toMap() == [text: '', blocks: [[type: 'header',
                                            text: [type: 'plain_text',
                                                   text: 'Headline']],
                                           [type: 'divider']]]
            toJson() == '{"text":"","blocks":[{"type":"header","text":{"type":"plain_text","text":"Headline"}},{"type":"divider"}]}'
        }
    }
}
