# Sample AEM Project

This is a sample AEM project that illustrates topics and concepts from the [Exadel MarTech Crash Course in AEM](../README.md). It is intended as a set of examples as well as a potential starting point to develop your own AEM project

The code is based on the [AEM project archetype](https://github.com/adobe/aem-project-archetype) with several changes and reductions to reflect the experience and practices of Exadel MarTech team.

## System requirements

Java 8 or 11, Maven 3.

Requires a standalone developers' AEM server (usually at localhost, but can as well be a remote one). Target AEM version is 6.5.12. 

## Modules

The project contains the following modules that match the AEM project archetype:

* **core**: Java bundle containing all core functionality like OSGi services, as well as component-related Java code such as servlets or Sling models;
* **ui.apps**: contains the /apps (and /etc) parts of the project, mostly related to the view part of AEM pages and page components;
* **ui.content**: contains sample content using the components from the ui.apps;
* **ui.config**: contains runmode specific OSGi configs for the project;
* **ui.tests**: contains Java-based integration tests aimed at checking complex business logic; 
* **all**: a single content package that embeds all of the compiled modules (bundles and content packages). This package is usually the one that is deployed to an AEM server.

The following modules of the AEM project archetype are omitted:
* _ui.frontend_: contains samples of front-end development not covered by this course;
* _ui.tests_: contains logic for testing frontend not covered by this course;
* _ui.apps.structure_: contains settings needed for deployment to an AEM as a Cloud Service installation; not covered by this course.

## How to build

To build all the modules and deploy to an AEM instance run in the project root directory the following command:

    mvn clean install -Padobe-public -PautoInstallSinglePackage

If the AEM instance is installed in a place other that the local computer, add the following command-line option to the command above:

    -Daem.host=<some.other.host OR ip address>

If the AEM instance runs on a port other that 4502, add the following command-line option to the command above: 

    -Daem.port=<port number>

To deploy only the bundle (and not any package), run

    mvn clean install -PautoInstallBundle

To deploy only a single content package, such as ui.apps solely, run in the sub-module directory (i.e `ui.apps`)

    mvn clean install -PautoInstallPackage

## How to run tests

To run **unit tests** with Maven, execute:

    mvn clean test

To run **integration tests** that exercise the capabilities of AEM via
HTTP calls to its API, run:

    mvn clean verify -Plocal

The integration tests in this archetype use the [AEM Testing
Clients](https://github.com/adobe/aem-testing-clients) and showcase some
recommended [best
practices](https://github.com/adobe/aem-testing-clients/wiki/Best-practices) to
be put in use when writing integration tests for AEM.
