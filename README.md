# JIRA Plugin Converter

This is a tool to convert an Atlassian Connect add-on's descriptor to a JIRA Server plugin. The remote server is shared between both. In this way, developers can build add-ons for JIRA Cloud first, then generate plugins for JIRA Server later.

This project is originally a submission to [Atlassian's Codegeist 2015](http://devpost.com/software/plugin-generator-from-atlassian-connect-to-jira-server-srdgvc).

## Usage

1. Install Atlassian SDK. This project is currently tested with Atlassian SDK 6.2.2.
2. Check out the source code. Branch 6.x and 7.x are for JIRA 6.x and JIRA 7.x respectively. The generated plugins are tested against JIRA 6.4.11 and JIRA 7.0.5.
3. `cd` to the project's root directory.
4. Package with Atlassian SDK: `atlas-clean && atlas-package`.
5. Convert Atlassian Connect add-on:

`java -cp target\generated_artifact_id-generated_artifact_version-jar-with-dependencies.jar minhhai2209.jirapluginconverter.converter.Converter <group_id> <descriptor_url> <path_to_generated_plugin>`

The generated plugin's source code will be placed at `path_to_generated_plugin`.

Example:

`java -cp target\generated_artifact_id-generated_artifact_version-jar-with-dependencies.jar minhhai2209.jirapluginconverter.converter.Converter com.example http://localhost:7777/descriptor D:\tmp\plugin`

For a quick test, use [this simple Atlassian Connect add-on](https://github.com/minhhai2209/jira-plugin-converter-demo).

## Status

See [list of supported features](https://github.com/minhhai2209/jira-plugin-converter/wiki/Features).

## Contributing

Currently, many works are still left undone, including:
* Unsupported Atlassian Connect's features,
* Unit tests,
* Code comments and documentation.

Contributions are welcome and appreciated. Feel free to submit pull requests. All changes must be licensed under MIT License.

## Contributors

* gtzenky
* kaushalye
* liulikun
* minhhai2209
* tuanvle

## License

This project is licensed under MIT License.
