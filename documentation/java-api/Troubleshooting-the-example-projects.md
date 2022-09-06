# Troubleshooting the example projects

The example projects in i2 Analyze Developer Essentials are sensitive to changes or inconsistencies in the configuration of the development environment. The errors that you see can be cryptic, but there are standard approaches to resolving the problems that they identify.

## There are compilation errors in your development environment after importing an example project

For an unmodified example project, compilation errors usually occur when the `TOOLKIT_ROOT` variable is not set, or when it is set incorrectly.

In Eclipse, to determine whether there is a problem with `TOOLKIT_ROOT`, expand one of the example project directories. Some of the directory icons inside the project have glyphs:

-   An arrow on the lower-right corner of a directory icon indicates a link to an external directory. If you see the arrow, then the variable is set correctly.

-   An exclamation point on the lower-right corner of a directory icon indicates a broken link. You are not able to expand an icon that is in this state.

If you have a project with broken links, there are two possibilities:

1.  If only some of the links are broken, then your `TOOLKIT_ROOT` variable is set correctly, but Developer Essentials was not extracted into the same location as the deployment toolkit.

2.  If all of the links are broken, then your `TOOLKIT_ROOT` variable is set incorrectly. Ensure that the variable is set to the path of deployment toolkit directory.

## There are Java Build Path Problems in your development environment after importing an example project

For an unmodified example project, Java Build Path problems can occur when the `i2analyze-toolkit-libraries` project is not imported.

In Eclipse, to indicate that there is a problem with the `i2analyze-toolkit-libraries`, the example projects have an exclamation point glyph on the directory glyph.

To resolve this issue, import the `i2analyze-toolkit-libraries` project into your development environment.