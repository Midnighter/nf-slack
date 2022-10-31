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

import nextflow.Const
import nextflow.NextflowMeta
import nextflow.config.Manifest
import nextflow.script.WorkflowMetadata
import nextflow.util.Duration
import nextflow.util.VersionNumber
import spock.lang.Specification

import java.time.OffsetDateTime
import java.time.ZoneOffset

class FluentSlackMessageBuilderTest extends Specification {

    private WorkflowMetadata pipeline

    def setup() {
        pipeline = GroovyStub(WorkflowMetadata) {
            start >> OffsetDateTime.of(1984, 4, 1, 12, 0, 1, 0, ZoneOffset.ofHours(2))
            complete >>
                OffsetDateTime.of(1984, 4, 1, 12, 2, 1, 0, ZoneOffset.ofHours(2))
            runName >> 'lyrical_ludwig'
            success >> false
            revision >> '0.0.0'
            commitId >> 'cb1cfff'
            duration >> Duration.of('2 m')
            commandLine >> 'nextflow run umbrella-corp/nf-zombieseq'
            nextflow >> GroovyStub(NextflowMeta) {
                build >> 5708
                timestamp >> '15-07-2022 16:09 UTC'
                version >> GroovyStub(VersionNumber) {
                    toString() >> '22.04.5'
                }
            }
            manifest >> GroovyStub(Manifest) {
                author >> 'Umbrella Corporation'
                doi >> '10.5281/evil.7271740'
                homePage >> 'https://umbrella.biz'
                name >> 'ZombieSeq'
                version >> '0.0.0'
            }
        }
    }

    def 'Can construct a new builder'() {
        when:
        FluentSlackMessageBuilder builder = new FluentSlackMessageBuilder()

        then:
        builder.getMessage().toMap() == [text: '', blocks: []]
    }

    def 'Can summarize a pipeline'() {
        given:
        FluentSlackMessageBuilder builder = new FluentSlackMessageBuilder()

        when:
        builder.summarizePipeline(pipeline)

        then:
        notThrown(NullPointerException)
    }

    def 'Can add text from summary to the message'() {
        given:
        FluentSlackMessageBuilder builder = new FluentSlackMessageBuilder()

        when:
        builder.summarizePipeline(pipeline)
               .addText()

        then:
        builder
            .getMessage()
            .toMap() == [text: 'ZombieSeq 0.0.0 [lyrical_ludwig] failed.', blocks: []]
    }

    def 'Can add pipeline info from summary to the message'() {
        given:
        FluentSlackMessageBuilder builder = new FluentSlackMessageBuilder()

        when:
        builder.summarizePipeline(pipeline)
               .addPipelineInfo()

        then:
        builder
            .getMessage()
            .toMap() == [text: '', blocks: [[type: 'header', text: [type: 'plain_text', text: 'ZombieSeq [lyrical_ludwig]']],
                                            [type: 'section', fields: [[type: 'mrkdwn', text: '*Pipeline version*\n*Revision*\n*Commit Hash*'],
                                                                       [type: 'mrkdwn', text: '0.0.0\n0.0.0\n```\ncb1cfff\n```'],]],
                                            [type     : 'section', fields: [[type: 'mrkdwn', text: "*Completed on:*\n1984-04-01 12:02:01 +0200 (duration: 2m)"],
                                                                            [type: 'mrkdwn', text: '*Status:*'],],
                                             accessory: [type : 'button',
                                                         text : [type: 'plain_text',
                                                                 text: 'Failure'],
                                                         style: 'danger',
                                                         value: 'click_status']],
                                            [type: 'section',
                                             text: [type: 'mrkdwn',
                                                    text: '*Nextflow command:*\n```\nnextflow run umbrella-corp/nf-zombieseq\n```']]]]
    }

    def 'Can add nextflow info from summary to the message'() {
        given:
        FluentSlackMessageBuilder builder = new FluentSlackMessageBuilder()

        when:
        builder.summarizePipeline(pipeline)
               .addNextflowInfo()

        then:
        builder
            .getMessage()
            .toMap() == [text: '', blocks: [
            [type: 'divider'],
            [type: 'section', fields: [
                [type: 'mrkdwn', text: '*Nextflow version*'],
                [type: 'mrkdwn', text: '22.04.5, build 5708 (15-07-2022 16:09 UTC)'],
            ]]
        ]]
    }

    def 'Can add manifest from summary to the message'() {
        given:
        FluentSlackMessageBuilder builder = new FluentSlackMessageBuilder()

        when:
        builder.summarizePipeline(pipeline)
               .addManifest()

        then:
        builder
            .getMessage()
            .toMap() == [text: '', blocks: [
            [type: 'divider'],
            [type: 'header', text: [type: 'plain_text', text: 'Manifest']],
            [type: 'section', fields: [
                [type: 'mrkdwn', text: '*author*\n*doi*\n*homePage*\n*name*\n*version*'],
                [type: 'mrkdwn', text: 'Umbrella Corporation\n10.5281/evil.7271740\nhttps://umbrella.biz\nZombieSeq\n0.0.0'],
            ]]
        ]]
    }

    def 'Can add extra info to the message'() {
        given:
        final Map extra = [
            target: 'Racoon City',
            status: 'active'
        ]
        FluentSlackMessageBuilder builder = new FluentSlackMessageBuilder()

        when:
        builder.summarizePipeline(pipeline)
               .addExtra(extra)

        then:
        builder
            .getMessage()
            .toMap() == [text: '', blocks: [
            [type: 'divider'],
            [type: 'header', text: [type: 'plain_text', text: 'Extra Information']],
            [type: 'section', fields: [
                [type: 'mrkdwn', text: '*status*\n*target*'],
                [type: 'mrkdwn', text: 'active\nRacoon City'],
            ]]
        ]]
    }
}
