# microsphere-logging-commons

[English version](../logging-commons.md)

> 核心 API 模块：`Logging` 抽象、SPI 加载工具、日志级别解析与 JMX 集成。

---

## 版本信息

| 项目 | 值 |
|------|-----|
| 分析分支 | `release`（文档集 `current`） |
| Maven 坐标 | `io.github.microsphere-projects:microsphere-logging-commons` |
| `release` 分支版本 | `0.2.3-SNAPSHOT` |
| 最新已发布版本 | `0.2.2` |
| 起始版本 | `1.0.0`（对应 0.1.x 系列，见源码 `@since`） |

## 兼容范围

| 依赖要求 | 版本 |
|----------|------|
| Java | 8 及以上 |
| microsphere-java-core | 传递依赖，版本由 `microsphere-logging-parent` 锁定（`microsphere-java` 0.3.19） |
| SLF4J / Log4j / Log4j2 / Logback | 均为可选依赖（`<optional>true</optional>`），仅在你自行引用其类时才需要 |

本模块**对任何日志后端都没有强制的运行时依赖**；各后端适配类分别位于 `microsphere-java-logging`、
`microsphere-logback`、`microsphere-log4j`、`microsphere-log4j2` 模块中。

## 安装

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
        <artifactId>microsphere-logging-commons</artifactId>
    </dependency>
</dependencies>
```

> 多数应用无需直接依赖本模块——引入任一后端模块即会传递引入。

## 核心概念

### 1. `io.microsphere.logging.Logging`

日志设施的管理接口，与 `java.util.logging.LoggingMXBean` 基本兼容。它继承 `io.microsphere.lang.Prioritized`，
因此多个后端可在同一 JVM 共存并按优先级解析。

| 方法 | 约定 |
|------|------|
| `String getRootLoggerName()` | 根 logger 名称（非 null） |
| `List<String> getLoggerNames()` | 当前已注册的 logger 名称 |
| `Set<String> getSupportedLoggingLevels()` | 该后端支持的级别名集合 |
| `String getLoggerLevel(String loggerName)` | logger 的级别名；logger 不存在返回 `null`；级别为继承态时返回空字符串 |
| `void setLoggerLevel(String loggerName, String levelName)` | 按名称设置级别；`levelName` 为 `null` 表示恢复继承；logger 不存在或级别名非法时抛 `IllegalArgumentException` |
| `String getParentLoggerName(String loggerName)` | 接口默认实现：基于点号层级推导父 logger 名 |
| `String getName()` | 后端名称，如 `Java Logging`、`Logback`、`Log4j`、`Log4j2` |
| `int getPriority()` | 优先级，`LoggingUtils.load()` 取最高者 |

### 2. `io.microsphere.logging.LoggingUtils`

基于 Java SPI（`META-INF/services/io.microsphere.logging.Logging`）的加载工具。

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;
import static io.microsphere.logging.LoggingUtils.loadAll;

// classpath 上优先级最高的 Logging 实现
Logging logging = load();

// 全部已检测到的实现，按优先级排序
List<Logging> all = loadAll();

// 指定 ClassLoader（模块化 / 容器环境常用）
Logging web = load(threadContextClassLoader);
```

当 classpath 上没有任何后端模块声明 SPI 时，`load()` / `loadAll()` 抛出 `IllegalArgumentException`。
两组方法均提供可空的 `ClassLoader` 重载，具体解析由 `microsphere-java-core` 完成。

### 3. 运行时级别管理

```java
Logging logging = load();

String level = logging.getLoggerLevel("io.microsphere");   // 例如 "INFO"
logging.setLoggerLevel("io.microsphere", "DEBUG");         // 立即生效，无需重启
logging.setLoggerLevel("io.microsphere", null);            // 恢复向父 logger 继承
```

### 4. `LoggingLevelsResolver` / `DefaultLoggingLevelsResolver`

通过反射读取级别类中 `public static final` 且类型为该级别类的字段，解析出级别名集合：

```java
import io.microsphere.logging.DefaultLoggingLevelsResolver;
import ch.qos.logback.classic.Level;

Set<String> levels = DefaultLoggingLevelsResolver.INSTANCE.resolve(Level.class);
// [TRACE, DEBUG, INFO, WARN, ERROR, OFF, ALL]
```

若某后端使用非标准级别类，实现 `LoggingLevelsResolver` 自行扩展。

### 5. JMX 集成（`io.microsphere.logging.jmx`）

- `LoggingMXBeanAdapter`：把 `Logging` 实例适配为标准 `java.util.logging.LoggingMXBean`；实现
  `DelegatingWrapper`，通过 `getDelegate()` 暴露被包装对象，`equals`/`hashCode`/`toString` 均委托处理。
- `LoggingMXBeanRegistrar`：把每个已检测到的 `Logging` 后端注册为 MBean，JMX 域为 `io.microsphere.logging`，
  `ObjectName` 形如 `io.microsphere.logging:type=<后端名>`。

```java
import io.microsphere.logging.jmx.LoggingMXBeanRegistrar;

// 注册 classpath 上发现的全部后端
List<ObjectInstance> instances = LoggingMXBeanRegistrar.registerAll();

// 或显式注册某一个
LoggingMXBeanRegistrar.register(logging);

// 查看已注册的 MBean
Set<ObjectInstance> registered = LoggingMXBeanRegistrar.getRegisteredLoggingObjectInstances();
```

注册后即可用 JConsole、VisualVM、`jcmd` 或任意 JMX 客户端读取与修改日志级别。

## 后端优先级汇总

| 后端实现 | 所属模块 | 优先级 |
|----------|----------|--------|
| `JavaLogging` | microsphere-java-logging | `NORMAL_PRIORITY + 10`（最高） |
| `LogbackLogging` | microsphere-logback | `NORMAL_PRIORITY` |
| `Log4jLogging` | microsphere-log4j | `NORMAL_PRIORITY - 3` |
| `Log4j2Logging` | microsphere-log4j2 | `NORMAL_PRIORITY - 5` |

`LoggingUtils.load()` 返回优先级最高者，因此多后端共存时首先选中 JUL 适配。若要指定后端，
请用 `loadAll()` 并按 `getName()` 过滤：

```java
Logging logging = LoggingUtils.loadAll().stream()
        .filter(l -> "Logback".equals(l.getName()))
        .findFirst()
        .orElseThrow();
```

## 相关文档

- [Java Logging（JUL）适配](./java-logging.md)
- [Logback 适配](./logback.md)
- [Log4j 适配](./log4j.md)
- [Log4j2 适配](./log4j2.md)
- [测试支持](./logging-test.md)
