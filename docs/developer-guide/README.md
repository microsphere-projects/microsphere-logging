# Developer Guide

[中文版](./zh/README.md) · [User Guide](../user-guide/README.md)

Source-level documentation for **Microsphere Logging** — how the facade and its backend adapters are
implemented, which design patterns hold them together, and how the non-trivial flows actually execute.

Every claim is backed by a citation in the form `File.java:123-128`, a test case, or a POM line, all read from
the `release` branch. Complex flows are described with Mermaid flowcharts and sequence diagrams; module
dependency relationships are graphed rather than listed.

---

## Document sets

| Branch analyzed | Directory | Status |
|-----------------|-----------|--------|
| `release` | [`current/`](./current/architecture.md) | Available |
| `release-1.x` | `1.x/` | Branch does not exist — no documents |

## Contents — `release` branch (`current`)

| # | Document | Scope |
|---|----------|-------|
| 1 | [architecture.md](./current/architecture.md) | The whole reactor: module dependency graph, SPI registration and priority resolution, the build parent, the CI matrix, and how to build the project |
| 2 | [logging-commons.md](./current/logging-commons.md) | `microsphere-logging-commons` — the `Logging` contract and its lossiness, `LoggingUtils` delegation, SPI cache semantics, level resolution, the JMX bridge, fault isolation across backends |
| 3 | [java-logging.md](./current/java-logging.md) | `microsphere-java-logging` — `JavaLogging` internals, why the root logger name is the empty string, `LoggingMXBean` usage and its JDK-version history, the inheritance-level guard |
| 4 | [logback.md](./current/logback.md) | `microsphere-logback` — the eager `LoggerContext` cast, declared vs effective level, the `String`/`Level` overload split, the borrowed `"ROOT"` logger name |
| 5 | [log4j.md](./current/log4j.md) | `microsphere-log4j` — the computed root name, effective-only level reads, the `getLoggerNames()` gap, package-private wrapper and `isAvailable()` probing |
| 6 | [log4j2.md](./current/log4j2.md) | `microsphere-log4j2` — per-call context resolution, `null` level → `DEBUG`, the runtime appender add/remove sequence, `InMemoryAppender` ordering and its two sharp edges, `SmartFileAppenderLayout` appender-type selection |
| 7 | [logging-test.md](./current/logging-test.md) | `microsphere-logging-test` — how one annotation fans a test out over backends × levels, class template vs test template parameter injection, level save/restore callbacks, the JUnit 4 `Statement` chain |
| 8 | [parent-and-dependencies.md](./current/parent-and-dependencies.md) | `microsphere-logging-parent` and `microsphere-logging-dependencies` — the three-POM layout, the `${revision}` flatten flow, what the BOM does and does not manage |
| 9 | [examples.md](./current/examples.md) | `microsphere-logging-examples` — the BOM-consumer pattern, why optional dependencies force explicit declarations, and the two independent SPI decisions a running example makes |

## Where to start

- **Understanding the system** → [architecture.md](./current/architecture.md), then
  [logging-commons.md](./current/logging-commons.md). Everything else is an implementation of a contract defined
  in those two files.
- **Adding a new backend adapter** → [logging-commons.md](./current/logging-commons.md) for the contract, then the
  closest existing adapter ([log4j2.md](./current/log4j2.md) is the most complete reference) for the pattern.
- **Debugging a level that will not change** → the adapter document for your backend; each one has a
  "level vocabulary / null handling / inheritance" section, because the three backends disagree on all of them.
- **Extending the test support** → [logging-test.md](./current/logging-test.md).

## Chinese mirror

Every document above has a Chinese counterpart under [`current/zh/`](./current/zh/architecture.md) with identical
file names and structure. Code blocks, Mermaid diagrams, `file:line` citations and all identifiers are unchanged
between the two languages — only the prose is translated.
