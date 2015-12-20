# JIRA Plugin Converter

This is a tool to convert an Atlassian Connect add-on's descriptor to a JIRA Server plugin. The remote server is shared between both. In this way, developers can build add-ons for JIRA Cloud first, then generate plugins for JIRA Server later.

This project is originally a submission to [Atlassian's Codegeist 2015](http://devpost.com/software/plugin-generator-from-atlassian-connect-to-jira-server-srdgvc).

## Usage

1. Install Atlassian SDK. This project is currently tested under Atlassian SDK 5.1.10.
2. Check out the source code.
3. `cd` to the project's root directory.
4. Package with Atlassian SDK: `atlas-clean && atlas-package`.
5. Convert Atlassian Connect add-on:

`java -cp target\generated_artifact_id-1.0-SNAPSHOT-jar-with-dependencies.jar minhhai2209.jirapluginconverter.converter.Converter <artifact_id> <group_id> <company_name> <company_url> <description> <descriptor_url> <path_to_generated_plugin>`

The generated plugin's source code will be placed at `path_to_generated_plugin`.

Example:

`java -cp target\generated_artifact_id-1.0-SNAPSHOT-jar-with-dependencies.jar minhhai2209.jirapluginconverter.converter.Converter example-plugin com.example "Example, Inc." http://localhost:7777/homepage "This is an example plugin" http://localhost:7777/descriptor D:\tmp\plugin`

For a quick test, use [this simple Atlassian Connect add-on](https://github.com/minhhai2209/jira-plugin-converter-demo).

## Status

See [list of supported features](https://github.com/minhhai2209/jira-plugin-converter/wiki/Features).

## Contributing

Currently, many works are still left undone, including:
* Unsupported Atlassian Connect's features,
* Unit tests,
* Code comments and documentation.

Contributions are welcome and appreciated. Feel free to submit pull requests. All changes must be licensed under MIT License.

## Credits

TODO: Write credits

## License

This project is licensed under MIT License.