# microsphere-logging-parent 与 microsphere-logging-dependencies

[English version](../parent-and-dependencies.md)

> 构建基础设施模块：共享**父 POM** 与统一锁定全项目构件版本的 **BOM**（Bill of Materials）。

---

## 版本信息

| 项目 | 值 |
|------|-----|
| 分析分支 | `release`（文档集 `current`） |
| 父 POM | `io.github.microsphere-projects:microsphere-logging-parent` |
| BOM | `io.github.microsphere-projects:microsphere-logging-dependencies` |
| `release` 分支版本 | `0.2.3-SNAPSHOT` |
| 最新已发布版本 | `0.2.2` |

两个模块都是 `<packaging>pom</packaging>`：不含 Java 代码，也不会成为应用逻辑的编译依赖，只负责版本与构建设置。

## POM 结构

三个 POM 构成一条继承链：

```
microsphere-build（外部）                  <- Java 8 编译基线、插件、发布工具链
   ^
microsphere-logging（根聚合 POM，pom.xml）   <- 声明 <revision>0.2.3-SNAPSHOT</revision> 与全部模块
   ^
microsphere-logging-parent                 <- 导入 microsphere-java-dependencies 0.3.19
   ^
microsphere-logging-dependencies           <- BOM
```

根聚合 POM（`io.github.microsphere-projects:microsphere-logging`）声明的 reactor 成员为：
`microsphere-logging-parent`、`microsphere-logging-dependencies`、`microsphere-logging-commons`、
`microsphere-java-logging`、`microsphere-log4j`、`microsphere-log4j2`、`microsphere-logback`、
`microsphere-logging-test`、`microsphere-logging-examples`。

## `microsphere-logging-parent` 提供的能力

- 继承根 POM `microsphere-logging`，后者再继承 `io.github.microsphere-projects:microsphere-build`
  （构建插件管理、Java 8 编译基线、发布工具链）。
- 通过属性 `microsphere-java.version = 0.3.19` 导入
  `io.github.microsphere-projects:microsphere-java-dependencies`，使本仓库各日志模块使用的
  `microsphere-java-*` 构件版本对齐。
- 作为 BOM 与各库模块的 `<parent>`。

## BOM 管理的构件

以下 `dependencyManagement` 条目的版本统一为 `${revision}`（即项目版本）：

| 被管构件 |
|----------|
| `microsphere-logging-commons` |
| `microsphere-java-logging` |
| `microsphere-logback` |
| `microsphere-log4j` |
| `microsphere-log4j2` |
| `microsphere-logging-test` |

## 使用方式

在根 `pom.xml` 中 import BOM，随后声明日志模块时**无需写版本号**：

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

若是在本仓库内新增模块，则直接继承父 POM：

```xml
<parent>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logging-parent</artifactId>
    <version>0.2.2</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

## 兼容性与升级建议

| microsphere-logging 版本 | microsphere-java 线 | Java 基线 |
|--------------------------|---------------------|-----------|
| 0.1.x – 0.2.x（`release` 分支，`0.2.3-SNAPSHOT`） | 0.3.19 | 8 |

- BOM 版本应与后端模块版本保持一致，**不支持**混用不同版本的 `microsphere-logging-*` 构件。
- 升级 `microsphere-logging` 会连带改变传递进来的 `microsphere-java` 版本，升级时请一并查阅
  [microsphere-java](https://github.com/microsphere-projects/microsphere-java) 的发布说明。
- 构建使用的版本属性是 `revision`（便于 flatten / CI 友好的版本管理），发布产物中为该属性的解析值，如 `0.2.2`。
