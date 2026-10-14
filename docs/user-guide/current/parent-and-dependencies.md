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

## POM layout

Three POMs, one inheritance chain:

```
microsphere-build (external)              <- Java 8 compiler baseline, plugins, publishing
   ^
microsphere-logging (root aggregator, pom.xml)   <- declares <revision>0.2.3-SNAPSHOT</revision> and all modules
   ^
microsphere-logging-parent                <- imports microsphere-java-dependencies 0.3.19
   ^
microsphere-logging-dependencies          <- the BOM
```

The root aggregator (`io.github.microsphere-projects:microsphere-logging`) declares the reactor:
`microsphere-logging-parent`, `microsphere-logging-dependencies`, `microsphere-logging-commons`,
`microsphere-java-logging`, `microsphere-log4j`, `microsphere-log4j2`, `microsphere-logback`,
`microsphere-logging-test` and `microsphere-logging-examples`.

## What `microsphere-logging-parent` Provides

- Inherits the root `microsphere-logging` POM, which in turn inherits
  `io.github.microsphere-projects:microsphere-build` (build plugin management, Java 8 compiler baseline,
  publishing toolchain).
- Imports `io.github.microsphere-projects:microsphere-java-dependencies` through the property
  `microsphere-java.version = 0.3.19`, so all `microsphere-java-*` artifacts used by the logging modules are
  version-aligned.
- Acts as the `<parent>` of the BOM and of the library modules.

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
