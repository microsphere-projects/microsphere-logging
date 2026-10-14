# 用户文档（User Guide）

[English version](../README.md) · [开发者文档](../../developer-guide/zh/README.md)

**Microsphere Logging** 面向使用者的文档——这是一个统一的日志门面，用一套 API、一套管理界面同时驱动 JUL、
Logback、Log4j 1.x、Log4j 2.x 与 SLF4J。

每个 Maven 模块对应一篇 Markdown 文档，内容包含该模块的 API 说明、代码示例（尽可能取自模块自身的测试用例）、
版本信息以及版本兼容范围。

---

## 文档集

| 分析分支 | 目录 | 状态 |
|----------|------|------|
| `release` | [`current/zh/`](../current/zh/overview.md) | 可用 |
| `release-1.x` | `1.x/` | 该分支不存在——无文档 |

## 模块文档 —— `release` 分支（`current`）

| # | 文档 | Maven 模块 | 你能学到什么 |
|---|------|------------|--------------|
| 0 | [overview.md](../current/zh/overview.md) | *（总览）* | 该选哪个构件、`Logging` / `Logger` / `LoggerFactory` 三者模型、快速上手、版本与兼容表 |
| 1 | [logging-commons.md](../current/zh/logging-commons.md) | `microsphere-logging-commons` | 核心抽象：`Logging`、`Logger`、`LoggerFactory`、`LoggingUtils`、`Level`、SPI 发现与优先级排序、JMX 管理 |
| 2 | [java-logging.md](../current/zh/java-logging.md) | `microsphere-java-logging` | `java.util.logging` 适配器，以及基于 `LoggingMXBean` 的级别管理 |
| 3 | [logback.md](../current/zh/logback.md) | `microsphere-logback` | Logback 适配器：logger context 访问、级别读写、Appender 检视 |
| 4 | [log4j.md](../current/zh/log4j.md) | `microsphere-log4j` | Log4j 1.x 适配器：repository 访问、生效级别、`LoggerFactory` SPI 注册 |
| 5 | [log4j2.md](../current/zh/log4j2.md) | `microsphere-log4j2` | Log4j 2.x 适配器：动态改级别、运行时增删 Appender、`InMemoryAppender`、Layout 工具 |
| 6 | [logging-test.md](../current/zh/logging-test.md) | `microsphere-logging-test` | 让同一个测试在每个日志后端、每个日志级别下各跑一次——JUnit 5 类/方法模板与 JUnit 4 Rule |
| 7 | [parent-and-dependencies.md](../current/zh/parent-and-dependencies.md) | `microsphere-logging-parent`、`microsphere-logging-dependencies` | 构建父工程与 BOM：如何引入、它们锁定了什么、刻意不锁定什么 |
| 8 | [examples.md](../current/zh/examples.md) | `microsphere-logging-examples` | 一个可运行的 BOM 消费方示例，展示 SPI 在运行时的实际选择 |

## 推荐阅读顺序

1. **初次接触本库** → [overview.md](../current/zh/overview.md)，然后 [logging-commons.md](../current/zh/logging-commons.md)。
2. **接入某个后端** → 对应的适配器文档（2–5），再看 [examples.md](../current/zh/examples.md)。
3. **库需要在多个后端下测试** → [logging-test.md](../current/zh/logging-test.md)。
4. **在自己项目中统一管理版本** → [parent-and-dependencies.md](../current/zh/parent-and-dependencies.md)。

## 英文原文

以上每篇文档都有对应的英文版，位于 [`current/`](../current/overview.md)，文件名一致、结构一致。
