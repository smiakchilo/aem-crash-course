# Sample AEM project template

This is a sample AEM project that illustrates topics and concepts from the [Exadel MarTech Crash Course in AEM](../README.md). It is intended as a set of examples as well as a potential starting point to develop your own AEM project

The code is generally based on the [AEM project archetype](https://github.com/adobe/aem-project-archetype) but contains some changes and is reduction to reflect the experience and practices of Exadel MarTech team.

## Modules

The project contains the following modules:

* core: Java bundle containing all core functionality like OSGi services, listeners or schedulers, as well as component-related Java code such as servlets or request filters;
* ui.apps: contains the /apps (and /etc) parts of the project, ie JS&CSS clientlibs, components, and templates;
* ui.content: contains sample content using the components from the ui.apps;
* ui.config: contains runmode specific OSGi configs for the project
* all: a single content package that embeds all of the compiled modules (bundles and content packages).

## System requirements

Java 8 or 11, Maven 3.

## How to build

To build all the modules and deploy to a local AEM instance run in the project root directory the following command:

    mvn clean install -Padobe-public -PautoInstallPackage

If the AEM instance is installed in a place other that the local computer, add the following command-line option to the command above:

    -Daem.host=<some.other.host OR ip address>

If the AEM instance runs on a port other that 4502, add the following command-line option to the command above: 

    -Daem.port=<port number>