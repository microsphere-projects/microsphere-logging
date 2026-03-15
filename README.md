# Microsphere Logging Framework

> Welcome to the Microsphere Logging Framework

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/microsphere-projects/microsphere-logging)
[![Maven Build](https://github.com/microsphere-projects/microsphere-logging/actions/workflows/maven-build.yml/badge.svg)](https://github.com/microsphere-projects/microsphere-logging/actions/workflows/maven-build.yml)
[![Codecov](https://codecov.io/gh/microsphere-projects/microsphere-logging/branch/dev/graph/badge.svg)](https://app.codecov.io/gh/microsphere-projects/microsphere-logging)
![Maven](https://img.shields.io/maven-central/v/io.github.microsphere-projects/microsphere-logging.svg)
![License](https://img.shields.io/github/license/microsphere-projects/microsphere-logging.svg)

## Introduction

TODO

## Features

- TODO

## Modules

The framework is organized into several key modules:

 Module                           | Purpose                                                                                             
----------------------------------|-----------------------------------------------------------------------------------------------------
 microsphere-logging-core         | Provides the core utilities across various domains like annotations, collections, concurrency, etc. 
 microsphere-logging-test         | Provides the models and components for logging testing.                                             
 microsphere-logging-dependencies | Manages dependency versions across the project.                                                     
 microsphere-logging-parent       | Parent POM with shared configurations.                                                              

## Getting Started

The easiest way to get started is by adding the Microsphere Java BOM (Bill of Materials) to your project's pom.xml:

```xml

<dependencyManagement>
    <dependencies>
        ...
        <!-- Microsphere Logging Dependencies -->
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-logging-dependencies</artifactId>
            <version>${microsphere-logging.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        ...
    </dependencies>
</dependencyManagement>
```

Then add the specific modules you need:

```xml

<dependencies>
    <!-- Logging Core -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-logging-core</artifactId>
    </dependency>
</dependencies>
```

### Quick Examples

```java

```

## Building from Source

You don't need to build from source unless you want to try out the latest code or contribute to the project.

To build the project, follow these steps:

1. Clone the repository:

```bash
git clone https://github.com/microsphere-projects/microsphere-logging.git
```

2. Build the source:

- Linux/MacOS:

```bash
./mvnw package
```

- Windows:

```powershell
mvnw.cmd package
```

## Contributing

We welcome your contributions! Please read [Code of Conduct](./CODE_OF_CONDUCT.md) before submitting a pull request.

## Reporting Issues

* Before you log a bug, please search the [issues](https://github.com/microsphere-projects/microsphere-logging/issues)
  to
  see if someone has already reported the problem.
* If the issue doesn't already
  exist, [create a new issue](https://github.com/microsphere-projects/microsphere-logging/issues/new).
* Please provide as much information as possible with the issue report.

## Documentation

## User Guide

[DeepWiki Host](https://deepwiki.com/microsphere-projects/microsphere-logging)

[ZRead Host](https://zread.ai/microsphere-projects/microsphere-logging)

### Wiki

[Github Host](https://github.com/microsphere-projects/microsphere-logging/wiki)

### JavaDoc

- TODO

## License

The Microsphere Java is released under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
