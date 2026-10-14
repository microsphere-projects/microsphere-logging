# Microsphere Logging 用户指南（release 分支 / current）

[English version](../overview.md)

> 分析分支：`release` · 版本：`0.2.3-SNAPSHOT`（最新已发布：`0.2.2`）

---

## Microsphere Logging 是什么

Microsphere Logging 是跨多个 Java 日志框架的**运行时日志管理统一抽象层**。它**不替代**你现有的日志框架，
而是在其之上叠加一套与框架无关的管理 API，从而实现：

- **不重启服务**即可查询和修改日志级别；
- 一套运维代码同时适配 **JUL、Logback、Log4j 1.x 与 Log4j 2**；
- 通过 **JMX**（`io.microsphere.logging:type=<后端名>`）向运维工具暴露日志配置；
- 使用开箱即用的 JUnit 4 / JUnit Jupiter 5 支持，在**多日志级别矩阵**下验证应用行为；
- 通过 **Java SPI**（`META-INF/services/io.microsphere.logging.Logging`）扩展自定义后端。

## 模块地图

| 模块 | 文档 | 提供的能力 |
|------|------|------------|
| `microsphere-logging-commons` | [logging-commons.md](./logging-commons.md) | `Logging` 接口、`LoggingUtils`（SPI 加载）、级别解析器、JMX 适配器与注册器 |
| `microsphere-java-logging` | [java-logging.md](./java-logging.md) | `JavaLogging`——JUL 后端适配（优先级最高） |
| `microsphere-logback` | [logback.md](./logback.md) | `LogbackLogging` + `LogbackUtils` |
| `microsphere-log4j` | [log4j.md](./log4j.md) | `Log4jLogging`、`LoggerFactory` SPI、`Log4jUtils` |
| `microsphere-log4j2` | [log4j2.md](./log4j2.md) | `Log4j2Logging`、`LoggerFactory` SPI、`Log4j2Utils`、`InMemoryAppender`、Layout |
| `microsphere-logging-test` | [logging-test.md](./logging-test.md) | `@LoggingLevelsTest` / `@LoggingLevelsClass` / `LoggingLevelsRule` |
| `microsphere-logging-parent` / `-dependencies` | [parent-and-dependencies.md](./parent-and-dependencies.md) | 父 POM + BOM 版本管理 |
| `microsphere-logging-examples` | [examples.md](./examples.md) | 可运行的 `LoggingExample` |

## 快速开始（5 分钟）

1. 引入 BOM：

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
```

2. 添加与实际日志框架匹配的后端模块（以 Logback 为例）：

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logback</artifactId>
</dependency>
```

3. 运行时管理日志：

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;

Logging logging = load();                           // SPI：加载优先级最高的后端
logging.setLoggerLevel("io.microsphere", "DEBUG");  // 立即生效
logging.setLoggerLevel("io.microsphere", null);     // 恢复为继承父级级别
```

4. （可选）通过 JMX 开放给运维：

```java
io.microsphere.logging.jmx.LoggingMXBeanRegistrar.registerAll();
```

## 版本与兼容性汇总

| 项目 | 值 |
|------|-----|
| `release` 分支版本 | `0.2.3-SNAPSHOT` |
| 最新已发布版本 | `0.2.2` |
| Java 基线 | 8 及以上 |
| microsphere-java 依赖线 | 0.3.19（由 `microsphere-logging-parent` 锁定） |
| JUL | JDK 内置（`LoggingMXBean` 自 Java 6 起提供） |
| Logback | 1.2+（SLF4J 1.7+） |
| Log4j | 1.2+（上游已 EOL，仅用于遗留系统） |
| Log4j2 | 2.4+ |
| JUnit | 4+ / Jupiter 5.13+（测试模块） |

源码 javadoc 中的 `@since 1.0.0` 表示 API 世代，而非某个已发布的 `1.x` 版本线：本仓库发布的是 `0.1.x` / `0.2.x`，
**不存在 `release-1.x` 分支**，因此 `docs/` 下只有 `current` 一套文档。

## 后端优先级（`load()` 的胜出者）

| 后端 | 优先级 | 顺序 |
|------|--------|------|
| JavaLogging（JUL） | `NORMAL + 10`（即 10） | 第 1 |
| LogbackLogging | `NORMAL`（即 0） | 第 2 |
| Log4jLogging | `NORMAL − 3`（即 -3） | 第 3 |
| Log4j2Logging | `NORMAL − 5`（即 -5） | 第 4 |

若需要同时操作所有已检测到的后端（而非只取优先级最高者），请使用 `LoggingUtils.loadAll()`。

## 阅读路径建议

- **新用户**：本页 → 你所用后端的文档 → [logging-commons.md](./logging-commons.md)。
- **运维场景**：JMX 注册见 [logging-commons.md](./logging-commons.md)；运行时级别调整见各后端文档示例。
- **测试场景**：多级别测试见 [logging-test.md](./logging-test.md)；日志抓取见
  [log4j2.md](./log4j2.md) 中的 `InMemoryAppender`。
