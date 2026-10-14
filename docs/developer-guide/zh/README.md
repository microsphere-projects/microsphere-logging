# 开发者文档（Developer Guide）

[English version](../README.md) · [用户文档](../../user-guide/zh/README.md)

**Microsphere Logging** 源码级文档——门面与各后端适配器的实现方式、把它们粘合在一起的设计模式，以及那些
不那么容易看出来的流程到底如何执行。

所有结论都带有 `File.java:123-128`、测试用例或 POM 行号形式的引用，且均来自 `release` 分支。复杂流程使用
Mermaid 流程图与时序图描述；模块之间的依赖关系用图示而非列表表达。

---

## 文档集

| 分析分支 | 目录 | 状态 |
|----------|------|------|
| `release` | [`current/zh/`](../current/zh/architecture.md) | 可用 |
| `release-1.x` | `1.x/` | 该分支不存在——无文档 |

## 目录 —— `release` 分支（`current`）

| # | 文档 | 范围 |
|---|------|------|
| 1 | [architecture.md](../current/zh/architecture.md) | 整个 reactor：模块依赖图、SPI 注册与优先级解析、构建父工程、CI 矩阵，以及如何构建本项目 |
| 2 | [logging-commons.md](../current/zh/logging-commons.md) | `microsphere-logging-commons`——`Logging` 契约及其信息有损、`LoggingUtils` 委托、SPI 缓存语义、级别解析、JMX 桥接、跨后端的故障隔离 |
| 3 | [java-logging.md](../current/zh/java-logging.md) | `microsphere-java-logging`——`JavaLogging` 内部实现、根 logger 名为何是空串、`LoggingMXBean` 的使用与 JDK 版本史、继承级保护 |
| 4 | [logback.md](../current/zh/logback.md) | `microsphere-logback`——急切的 `LoggerContext` 强转、已声明级别 vs 生效级别、`String`/`Level` 重载分裂、借用来的 `"ROOT"` 名称 |
| 5 | [log4j.md](../current/zh/log4j.md) | `microsphere-log4j`——计算得出的根名称、只返回生效级别的读取、`getLoggerNames()` 的缺口、包级私有包装类与 `isAvailable()` 探测 |
| 6 | [log4j2.md](../current/zh/log4j2.md) | `microsphere-log4j2`——每次调用重新解析 context、`null` 级别 → `DEBUG`、运行时 Appender 增删时序、`InMemoryAppender` 的排序与两处陷阱、`SmartFileAppenderLayout` 的 Appender 类型选择 |
| 7 | [logging-test.md](../current/zh/logging-test.md) | `microsphere-logging-test`——一个注解如何把测试展开成「后端 × 级别」的笛卡尔积、类模板与方法模板的参数注入差异、级别保存/恢复回调、JUnit 4 `Statement` 链 |
| 8 | [parent-and-dependencies.md](../current/zh/parent-and-dependencies.md) | `microsphere-logging-parent` 与 `microsphere-logging-dependencies`——三 POM 结构、`${revision}` flatten 流程、BOM 管理什么、不管理什么 |
| 9 | [examples.md](../current/zh/examples.md) | `microsphere-logging-examples`——BOM 消费方模式、可选依赖为何迫使显式声明、示例中两处彼此独立的 SPI 决策 |

## 从哪里开始

- **理解整体系统** → [architecture.md](../current/zh/architecture.md)，然后
  [logging-commons.md](../current/zh/logging-commons.md)。其余一切都是在实现这两个文件里定义的契约。
- **新增一个后端适配器** → 先看 [logging-commons.md](../current/zh/logging-commons.md) 了解契约，再对照最接近的
  现有适配器（[log4j2.md](../current/zh/log4j2.md) 是最完整的参考）。
- **排查「级别改了不生效」** → 直接看你所用后端的适配器文档；每篇都有「级别词汇表 / 空值处理 / 继承」小节，
  因为三个后端在这三点上彼此都不一致。
- **扩展测试支持** → [logging-test.md](../current/zh/logging-test.md)。

## 英文原文

以上每篇文档都有对应的英文原版，位于 [`current/`](../current/architecture.md)，文件名与结构完全一致；
代码块、Mermaid 图、`file:line` 引用与所有标识符在两个语言版本中保持不动，只翻译正文。
