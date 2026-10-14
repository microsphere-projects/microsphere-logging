# microsphere-logging-examples

[English version](../examples.md)

> 可运行示例模块，演示在后端全部就位时如何使用统一的 `Logging` API。

---

## 版本信息

| 项目 | 值 |
|------|-----|
| 分析分支 | `release`（文档集 `current`） |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-logging-examples` |
| `release` 分支版本 | `0.2.3-SNAPSHOT` |
| 最新已发布版本 | `0.2.2` |
| 起始版本 | `1.0.0`（对应 0.1.x 系列） |

## 兼容范围

| 依赖要求 | 版本 |
|----------|------|
| Java | 8 及以上 |

本模块同时依赖**全部**后端适配模块（`microsphere-java-logging`、`microsphere-logback`、`microsphere-log4j`、
`microsphere-log4j2`）以及 `microsphere-java-core`，并 import `microsphere-logging-dependencies` BOM，
因此它也演示了多后端共存与按优先级解析的行为。

## 运行方式

```bash
# 在仓库根目录执行（Windows 使用 mvnw.cmd）
./mvnw -pl microsphere-logging-examples -am package

# 生成运行时 classpath 后启动
CP=microsphere-logging-examples/target/classes:$(./mvnw -q -pl microsphere-logging-examples \
  dependency:build-classpath -Dmdep.outputFile=/dev/stdout)
java -cp "$CP" io.microsphere.logging.examples.LoggingExample
```

最简单的方式是直接在 IDE 中运行 `LoggingExample.main(...)`。

## 演示内容

`io.microsphere.logging.examples.LoggingExample`：

```java
public class LoggingExample {

    private static final Logger logger = getLogger(LoggingExample.class);

    public static void main(String[] args) {
        // 加载优先级最高的 Logging 实现
        Logging logging = load();
        logger.info("The name of Logging : {}", logging.getName());

        // 该后端当前管理的全部 logger 名
        logger.info("All logger names of Logging : {}", logging.getLoggerNames());
    }
}
```

要点：

1. `LoggingUtils.load()` 依据 `Prioritized` 优先级（数值**升序**比较）选择唯一一个 `Logging` 实现。四个适配模块
   都在 classpath 上时，**`Log4j2Logging` 胜出**（优先级 `-5`，`MAX_PRIORITY` 即 `Integer.MIN_VALUE`）——见
   [logging-commons.md](./logging-commons.md#后端优先级汇总)。因此示例打印 `The name of Logging : Log4j2`。
2. `LoggerFactory.getLogger(...)`（来自 `microsphere-java-core`，通过 Log4j 与 Log4j2 适配模块注册的
   `LoggerFactory` SPI 解析）提供与框架无关的 `io.microsphere.logging.Logger`；同一套优先级数值也作用于它，
   所以示例里的日志输出同样走 Log4j2。
3. 打印出的后端名称与实时 logger 清单，正是运行时级别管理与 JMX 注册的起点。

## 扩展示例

可在自己的 `main` 中尝试以下变体：

```java
// 不止取优先级最高者，而是遍历所有后端
LoggingUtils.loadAll().forEach(l ->
        System.out.printf("%s (priority %d): %d loggers, levels %s%n",
                l.getName(), l.getPriority(), l.getLoggerNames().size(), l.getSupportedLoggingLevels()));

// 对所有后端调整级别，观察输出详细程度变化
LoggingUtils.loadAll().forEach(l -> l.setLoggerLevel("io.microsphere", "DEBUG"));

// 注册 JMX，然后用 JConsole 连接
io.microsphere.logging.jmx.LoggingMXBeanRegistrar.registerAll();
```

> 多后端共存时，某个后端合法的级别名可能对另一个后端无效（`DEBUG` 不是 JUL 级别名）。
> 这种情况下请按 `getName()` 过滤后分别调用，或对每个后端使用其自身支持的级别名。
