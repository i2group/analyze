Troubleshooting the example projects
====================================

The example projects in i2 Analyze Developer Essentials are sensitive to changes or inconsistencies in the configuration of the development environment. The errors that you see can be cryptic, but there are standard approaches to resolving the problems that they identify.

-   <a href="#eclipse-reports-compilation-errors-after-it-imports-an-example-project" class="xref">There are compilation errors in your development environment after importing an example project</a>
-   <a href="#java_build_path" class="xref">There are Java Build Path Problems in your development environment after importing an example project</a>
-   <a href="#java_build_path_rest" class="xref">There are Java Build Path Problems in your development environment after importing the onyx-da-subset-rest-example project</a>

There are compilation errors in your development environment after importing an example project
-----------------------------------------------------------------------------------------------

For an unmodified example project, compilation errors usually occur when the `TOOLKIT_ROOT` variable is not set, or when it is set incorrectly.

In Eclipse, to determine whether there is a problem with `TOOLKIT_ROOT`, expand one of the example project directories. Some of the directory icons inside the project have glyphs:

-   An arrow on the lower-right corner of a directory icon indicates a link to an external directory. If you see the arrow, then the variable is set correctly.
-   An exclamation point on the lower-right corner of a directory icon indicates a broken link. You are not able to expand an icon that is in this state.

If you have a project with broken links, there are two possibilities:

1.  If only some of the links are broken, then your `TOOLKIT_ROOT` variable is set correctly, but Developer Essentials was not extracted into the same location as the deployment toolkit.
2.  If all of the links are broken, then your `TOOLKIT_ROOT` variable is set incorrectly. Ensure that the variable is set to the path of deployment toolkit directory.

There are Java Build Path Problems in your development environment after importing an example project
-----------------------------------------------------------------------------------------------------

For an unmodified example project, Java Build Path problems can occur when the `i2analyze-toolkit-libraries` project is not imported.

In Eclipse, to indicate that there is a problem with the `i2analyze-toolkit-libraries`, the example projects have an exclamation point glyph on the directory glyph.

To resolve this issue, import the `i2analyze-toolkit-libraries` project into your development environment.

There are Java Build Path Problems in your development environment after importing the onyx-da-subset-rest-example project
--------------------------------------------------------------------------------------------------------------------------

For an unmodified `onyx-da-subset-rest-example` example project, Java Build Path problems can occur when the `WLP_HOME_DIR` variable is not set, or when it is set incorrectly.

In Eclipse, to indicate that there is a problem with the `onyx-da-subset-rest-example`, the example project has a red cross glyph on the directory icon, and an exclamation point on the `onyx-da-subset-rest-example/LibertyRuntimeDevApiSpec` directory icon.

To resolve this issue, ensure that the `WLP_HOME_DIR` is set to `C:\IBM\i2analyze\deploy\wlp`.

**Parent topic:** <a href="developer_essentials_welcome.md" class="link" title="IBM i2 Analyze Developer Essentials contains tools, libraries, and examples that enable development and deployment of custom extensions to i2 Analyze. Developer Essentials also includes API documentation and guides to deploying the software and the example projects.">IBM i2 Analyze Developer Essentials</a>

------------------------------------------------------------------------

© Copyright IBM Corporation 2014, 2020.


