# microsphere-logging-parent & microsphere-logging-dependencies

[中文版](./zh/parent-and-dependencies.md)

> Build infrastructure modules: the shared **parent POM** and the **BOM** (Bill of Materials) that pins every
> artifact version in the Microsphere Logging project.

---

## Version Information

| Item | Value |
|------|-------|
| Branch analyzed | `release` (document set `current`) |
| Parent POM | `io.github.microsphere-projects:microsphere-logging-parent` |
| BOM | `io.github.microsphere-projects:microsphere-logging-dependencies` |
| Version on `release` branch | `0.2.3-SNAPSHOT` |
| Latest published version | `0.2.2` |

Both are `<packaging>pom</packaging>` modules: no Java code, and they are never compile dependencies of application
logic — they only manage versions and build settings.

## What `microsphere-logging-parent` Provides

- Inherits from `io.github.microsphere-projects:microsphere-build` (build plugin management, Java 8 compiler
  baseline, publishing toolchain).
- Imports `io.github.microsphere-projects:microsphere-java-dependencies` through the property
  `microsphere-java.version = 0.3.19`, so all `microsphere-java-*` artifacts used by the logging modules are
  version-aligned.
- Acts as the `<parent>` of every module in this repository.

## What the BOM Manages

`dependencyManagement` entries, all resolved to `${revision}` (the project version):

| Managed artifact |
|------------------|
| `microsphere-logging-commons` |
| `microsphere-java-logging` |
| `microsphere-logback` |
| `microsphere-log4j` |
| `microsphere-log4j2` |
| `microsphere-logging-test` |

## Usage

Import the BOM in your root `pom.xml`, then declare logging modules **without versions**:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-logging-dependencies</artifactId>
            <version>0.2.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-log4j2</artifactId>
    </dependency>

    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-logging-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

If you contribute a module inside this repository, inherit the parent directly:

```xml
<parent>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logging-parent</artifactId>
    <version>0.2.2</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

## Compatibility and Upgrade Guidance

| microsphere-logging version | microsphere-java line | Java baseline |
|-----------------------------|-----------------------|---------------|
| 0.1.x – 0.2.x (`release` branch, `0.2.3-SNAPSHOT`) | 0.3.19 | 8 |

- Keep the BOM version equal to the backend modules' version. Mixing different versions of
  `microsphere-logging-*` artifacts is not supported.
- Upgrading `microsphere-logging` transitively moves `microsphere-java` as well; review the
  [microsphere-java](https://github.com/microsphere-projects/microsphere-java) release notes when you bump.
- The version property used by the build is `revision` (Maven flatten/CIID-friendly); published artifacts carry the
  resolved value, e.g. `0.2.2`.
