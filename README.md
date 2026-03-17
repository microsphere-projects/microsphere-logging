# Microsphere Logging Framework

> Welcome to the Microsphere Logging Framework

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/microsphere-projects/microsphere-logging)
[![Maven Build](https://github.com/microsphere-projects/microsphere-logging/actions/workflows/maven-build.yml/badge.svg)](https://github.com/microsphere-projects/microsphere-logging/actions/workflows/maven-build.yml)
[![Codecov](https://codecov.io/gh/microsphere-projects/microsphere-logging/branch/main/graph/badge.svg)](https://app.codecov.io/gh/microsphere-projects/microsphere-logging)
![Maven](https://img.shields.io/maven-central/v/io.github.microsphere-projects/microsphere-logging.svg)
![License](https://img.shields.io/github/license/microsphere-projects/microsphere-logging.svg)

## Introduction

Microsphere Logging Framework, a unified abstraction layer designed to simplify logging management across multiple Java
logging frameworks. This framework provides a consistent API for dynamically managing log levels, querying logger
information, and integrating with various logging backends including Logback, Log4j2, and Java Logging.

## Features

- Runtime Log Level Management: Dynamically adjust logging levels in production without restarting services
- Framework-Agnostic Code: Write management code that works across different logging backends
- Operational Control: Expose logging configuration through JMX for operations teams
- Testing Infrastructure: Test logging-dependent behavior across multiple log configurations
- Multi-Tenant Systems: Apply different logging configurations per tenant or module

## Modules

The framework is organized into several key modules:

 Module                           | Purpose                                                                 
----------------------------------|-------------------------------------------------------------------------
 microsphere-logging-parent       | Parent POM with shared configurations.                                  
 microsphere-logging-dependencies | Manages dependency versions across the project.                         
 microsphere-logging-commons      | Provides the commons features for logging.                              
 microsphere-logging-test         | Provides the extensions of JUnit4 or JUnit Jupiter for logging testing. 
 microsphere-logging-examples     | Provides the eamples of Microshere Logging.                             
 microsphere-java-logging         | Provides the extensions features for Java Logging.                      
 microsphere-logback              | Provides the extensions features for logback.                           
 microsphere-log4j                | Provides the extensions features for log4j.                             
 microsphere-log4j2               | Provides the extensions features for log4j2.                            

## Compatibility

- Java 8+
- JUnit 4+
- JUnit Jupiter 5.13+
- SLF4J 1.7+
- Log4j 1.2+
- Logback 1.2+
- Log4j2 2.4+

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

    <!-- Microsphere Java Logging -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-java-logging</artifactId>
    </dependency>

    <!-- Microsphere Logback -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-logback</artifactId>
    </dependency>

    <!-- Logback -->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>${logback.version}</version>
    </dependency>

    <!-- Microsphere Log4j -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-log4j</artifactId>
    </dependency>

    <!-- Log4j-->
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>${log4j.version}</version>
    </dependency>

    <!-- Microsphere Log4j2 -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-log4j2</artifactId>
    </dependency>

    <!-- Log4j2 -->
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>${log4j2.version}</version>
    </dependency>

</dependencies>
```

Usually, you only need one of 'microsphere-java-logging' or 'microsphere-logback' or 'microsphere-log4j2' with their
transitive dependencies. Above dependencies just is an example, you can use the API of the
'microsphere-logging-commons' module to load the instance of `Logging` that you need:

```java
package io.microsphere.logging.examples;

import io.microsphere.logging.Logger;
import io.microsphere.logging.Logging;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.logging.LoggingUtils.load;

public class LoggingExample {

    private static final Logger logger = getLogger(LoggingExample.class);

    public static void main(String[] args) {
        // The highest priority Logging instance will be loaded
        Logging logging = load();
        logger.info("The name of Logging : {}", logging.getName());

        // The all logger names of Logging
        logger.info("All logger names of Logging : {}", logging.getLoggerNames());
    }
}
```

The run result:

```
[main] INFO io.microsphere.logging.examples.LoggingExample - The name of Logging : Log4j2
[main] INFO io.microsphere.logging.examples.LoggingExample - All logger names of Logging : ...
```

The more examples can be found in the [microsphere-logging-examples](microsphere-logging-examples) module.

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

- [microsphere-logging-commons](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-logging-commons)
- [microsphere-logging-test](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-logging-test)
- [microsphere-java-logging](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-java-logging)
- [microsphere-logback](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-logback)
- [microsphere-log4j](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-log4j)
- [microsphere-log4j2](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-log4j2)

## License

The Microsphere Java is released under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
