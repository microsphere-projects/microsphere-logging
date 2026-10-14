# User Guide

[中文版](./zh/README.md) · [Developer Guide](../developer-guide/README.md)

End-user documentation for **Microsphere Logging** — the unified logging facade that lets one API and one
management surface drive JUL, Logback, Log4j 1.x, Log4j 2.x and SLF4J at the same time.

One Markdown file per Maven module. Each file contains the module's API explanation, code examples (taken from
the module's own test cases wherever possible), version information and its compatibility range.

---

## Document sets

| Branch analyzed | Directory | Status |
|-----------------|-----------|--------|
| `release` | [`current/`](./current/overview.md) | Available |
| `release-1.x` | `1.x/` | Branch does not exist — no documents |

## Modules — `release` branch (`current`)

| # | Document | Maven module | What you learn |
|---|----------|--------------|----------------|
| 0 | [overview.md](./current/overview.md) | *(guide-level)* | Which artifact to choose, the three-way `Logging` / `Logger` / `LoggerFactory` model, quick start, version and compatibility table |
| 1 | [logging-commons.md](./current/logging-commons.md) | `microsphere-logging-commons` | The core abstraction: `Logging`, `Logger`, `LoggerFactory`, `LoggingUtils`, `Level`, SPI discovery and priority ordering, JMX management |
| 2 | [java-logging.md](./current/java-logging.md) | `microsphere-java-logging` | The `java.util.logging` adapter and its `LoggingMXBean`-backed level management |
| 3 | [logback.md](./current/logback.md) | `microsphere-logback` | The Logback adapter: logger context access, level read/write, appender inspection |
| 4 | [log4j.md](./current/log4j.md) | `microsphere-log4j` | The Log4j 1.x adapter: repository access, effective levels, `LoggerFactory` SPI registration |
| 5 | [log4j2.md](./current/log4j2.md) | `microsphere-log4j2` | The Log4j 2.x adapter: dynamic level changes, runtime appender add/remove, `InMemoryAppender`, layout utilities |
| 6 | [logging-test.md](./current/logging-test.md) | `microsphere-logging-test` | Run the same test once per logging backend and once per log level — JUnit 5 class/test templates and the JUnit 4 rule |
| 7 | [parent-and-dependencies.md](./current/parent-and-dependencies.md) | `microsphere-logging-parent`, `microsphere-logging-dependencies` | The build parent and the BOM: how to import them, what they pin, what they deliberately do **not** pin |
| 8 | [examples.md](./current/examples.md) | `microsphere-logging-examples` | A runnable BOM consumer that shows the SPI selection in practice |

## Reading order

1. **New to the library** → [overview.md](./current/overview.md), then [logging-commons.md](./current/logging-commons.md).
2. **Adding a backend** → the matching adapter document (2–5), then [examples.md](./current/examples.md).
3. **Testing a library against several backends** → [logging-test.md](./current/logging-test.md).
4. **Wiring versions into your own build** → [parent-and-dependencies.md](./current/parent-and-dependencies.md).

## Chinese mirror

Every document above has a Chinese counterpart under [`current/zh/`](./current/zh/overview.md) with the same file
names and the same structure; code blocks, diagrams and `file:line` citations are identical in both languages.
