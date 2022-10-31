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

import groovy.json.JsonSlurper
import groovy.text.GStringTemplateEngine
import groovy.text.Template
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import nextflow.script.WorkflowMetadata

/**
 * Define a builder for Slack messages from templates.
 *
 * @author Moritz E. Beber
 */
@PackageScope
@CompileStatic
class FluentSlackMessageBuilder {

    protected final static GStringTemplateEngine engine = new GStringTemplateEngine()

    @Lazy static Template textTemplate = { engine.createTemplate(FluentSlackMessageBuilder.class.getResource('/slack/textTemplate.txt')) }()

    @Lazy static Template pipelineTemplate = { engine.createTemplate(FluentSlackMessageBuilder.class.getResource('/slack/pipelineTemplate.json')) }()

    @Lazy static Template nextflowTemplate = { engine.createTemplate(FluentSlackMessageBuilder.class.getResource('/slack/nextflowTemplate.json')) }()

    @Lazy static Template manifestTemplate = { engine.createTemplate(FluentSlackMessageBuilder.class.getResource('/slack/manifestTemplate.json')) }()

    @Lazy static Template extrasTemplate = { engine.createTemplate(FluentSlackMessageBuilder.class.getResource('/slack/extrasTemplate.json')) }()

    protected final static JsonSlurper slurper = new JsonSlurper()

    protected Map summary

    protected SlackMessage message

    FluentSlackMessageBuilder() {
        this.summary = [
            pipeline: null,
            nextflow: null,
            manifest: null,
            extra: null
        ]
        this.message = new SlackMessage()
    }

    FluentSlackMessageBuilder summarizePipeline(WorkflowMetadata workflow) {
        this.summary.pipeline = getPipelineSummary(workflow)
        this.summary.nextflow = getNextflowSummary(workflow)
        this.summary.manifest = getManifestSummary(workflow)
        return this
    }

    FluentSlackMessageBuilder addText() {
        this.message.setText(textTemplate.make(this.summary).toString().strip())
        return this
    }

    FluentSlackMessageBuilder addPipelineInfo() {
        this.message.extendBlocks((List)slurper.parseText(pipelineTemplate.make(this.summary).toString()))
        return this
    }

    FluentSlackMessageBuilder addNextflowInfo() {
        this.message.extendBlocks((List)slurper.parseText(nextflowTemplate.make(this.summary).toString()))
        return this
    }

    FluentSlackMessageBuilder addManifest() {
        this.message.extendBlocks((List)slurper.parseText(manifestTemplate.make(this.summary).toString()))
        return this
    }

    FluentSlackMessageBuilder addExtra(final Map extra) {
        this.summary.extra = extra
        this.message.extendBlocks((List)slurper.parseText(extrasTemplate.make(this.summary).toString()))
        return this
    }

    SlackMessage getMessage() {
        return this.message
    }

    /**
     * Collect pipeline summary information from the workflow object.
     *
     * @param workflow A nextflow workflow metadata object available for introspection.
     * @return A map of all the relevant information collected from the `workflow` object.
     */
    protected static Map getPipelineSummary(
        final WorkflowMetadata workflow,
        final Collection<String> keys = [
            'start', 'complete', 'duration', 'success', 'scriptFile', 'scriptId',
            'repository', 'commitId', 'revision', 'runName', 'commandLine'
        ]
    ) {
        return keys.collectEntries { key -> [key, workflow[key]] }
    }

    /**
     * Collect nextflow summary information from the workflow object.
     *
     * @param workflow A nextflow workflow metadata object available for introspection.
     * @return A map of all the relevant information collected from the `workflow.nextflow` object.
     */
    protected static Map getNextflowSummary(
        final WorkflowMetadata workflow,
        final Collection<String> keys = [
            'version', 'build', 'timestamp'
        ]
    ) {
        return keys.collectEntries { key -> [key, workflow.nextflow[key]] }
    }

    /**
     * Collect manifest summary information from the workflow object.
     *
     * @param workflow A nextflow workflow object available for introspection upon completion.
     * @return A map of all the relevant information collected from the `workflow.manifest` object.
     */
    protected static Map getManifestSummary(
        final WorkflowMetadata workflow,
        final Collection<String> keys = [
            'name', 'version', 'homePage', 'author', 'doi'
        ]
    ) {
        return keys.collectEntries { key -> [key, workflow.manifest[key]] }
    }
}
