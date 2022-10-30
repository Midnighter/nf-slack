# Slack Notification Nextflow Plugin

_Create notification messages from nextflow using incoming webhooks for Slack._

TODO: This is the beginning of your Slack Notification Nextflow plugin project. Please, briefly describe its purpose here.

## Plugin Assets

| File                                                                           | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| ------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `settings.gradle`                                                              | Gradle project settings.                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `plugins/nf-slack`                                       | The plugin implementation base directory.                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `plugins/nf-slack/build.gradle`                          | Plugin Gradle build file. Project dependencies should be added here.                                                                                                                                                                                                                                                                                                                                                                                                           |
| `plugins/nf-slack/src/resources/META-INF/MANIFEST.MF`    | Manifest file that defines the plugin attributes, e.g., name, version, and so on. The attribute `Plugin-Class` declares the plugin main class. This class should extend the base class `nextflow.plugin.BasePlugin` as shown by `nextflow.slack.SlackPlugin` in `plugins/nf-slack/src/main/nextflow/slack/SlackPlugin.groovy`. |
| `plugins/nf-slack/src/resources/META-INF/extensions.idx` | This file declares one or more extension classes provided by the plugin. Each line should contain the fully qualified name of a Java class that implements the `org.pf4j.ExtensionPoint` interface (or a sub-interface).                                                                                                                                                                                                                                                       |
| `plugins/nf-slack/src/main`                              | The plugin implementation sources.                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `plugins/nf-slack/src/test`                              | The plugin unit tests.                                                                                                                                                                                                                                                                                                                                                                                                                                                         |

## Extension

`ExtensionPoint` is the basic interface used by nextflow-core to integrate plugins. It's only a basic interface and serves as a starting point for more specialized extensions.

Among others, nextflow-core integrates the following sub extenions:

-   `TraceObserverFactory` to provide a list of `TraceObserver`s
-   `ChannelExtensionPoint` to enrich the `Channel` object with custom methods

It is up to you to decide which of these your plugin defines.

## Unit testing

Run the following command in the project root directory:

```bash
make test
```

## Testing and debugging

There are several tests you can use under `plugins/nf-hello/src/test` folder. You can also test yours DSL (see `HelloDslTest.groovy`) !!!

Once tested you can try your plugin in a nextflow sesion:

1. Generate required artifacts with `make build-plugins`
2. Copy `build/plugins/nf-slack-0.1.0a1.zip` to `$HOME/.nextflow/plugins`
3. Create a pipeline with your plugin and see it in action

## Publish

The project should be hosted in a GitHub repository whose name should match the name of the plugin, e.g., https://github.com/Midnighter/nf-slack.

Follow these steps to package, upload, and publish the plugin:

1. Create a file named `gradle.properties` in the project root containing the following attributes (this file should not be committed to git):

    - `github_organization`: the GitHub organisation where the plugin repository is hosted.
    - `github_username`: The GitHub username granting access to the plugin repository.
    - `github_access_token`: The GitHub access token required to upload and commit changes to the plugin repository.
    - `github_commit_email`: The email address associated with your GitHub account.

2. Use the following command to package and create a release for your plugin on GitHub:

    ```bash
    make upload-plugins
    ```

3. Create a pull request against [nextflow-io/plugins](https://github.com/nextflow-io/plugins/blob/main/plugins.json) to make the plugin accessible to Nextflow.
