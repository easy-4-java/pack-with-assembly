# pack-with-assembly

[English](./README.md) | [简体中文](./README.zh-CN.md)

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Features & Status](#2-features--status)
- [3. Requirements & Compatibility](#3-requirements--compatibility)
- [4. Architecture & Modules](#4-architecture--modules)
- [5. Installation](#5-installation)
- [6. Quick Start](#6-quick-start)
- [7. Configuration](#7-configuration)
- [8. Core Usage / API](#8-core-usage--api)
- [9. Testing & Build](#9-testing--build)
- [10. Versioning & Branches](#10-versioning--branches)
- [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

**pack-with-assembly** is a set of Maven packaging examples that build runnable application distributions with
the `maven-assembly-plugin` and `maven-dependency-plugin`. It is a multi-module POM project with two packaging
recipes:

| Module                      | Recipe                                                                     |
| :-------------------------- | :------------------------------------------------------------------------- |
| `pack-with-assembly-jsw`    | Bundle your application with **Java Service Wrapper** and a bundled JRE into a per-platform zip |
| `pack-with-assembly-tomcat` | Bundle your application with a full **Apache Tomcat** and a bundled JRE into a directory layout  |

Both modules ship a minimal sample application (`main.MainClass` / `main.WrapperMainClassForWindows` +
`service.FileLogger`) that writes log lines to a file, so the produced distribution can be run end-to-end.

| Is                                                       | Is not                                     |
| :------------------------------------------------------- | :----------------------------------------- |
| A ready-to-copy packaging template (JSW / Tomcat)        | A deployment tool or release manager       |
| Demonstrates assembly descriptors, JRE bundling, wrapper scripts | A Docker/container image builder          |
| Produces `dir`/`zip` artifacts for the sample app        | A runtime container for arbitrary apps     |

## 2. Features & Status

| Capability                                              | Module                  | Status      | Notes                                                          |
| :------------------------------------------------------ | :---------------------- | :---------- | :------------------------------------------------------------- |
| Java Service Wrapper packaging                          | `pack-with-assembly-jsw` | Implemented | `wrapper-delta-pack` 3.5.42, setup name `jeebiz-boot`          |
| Per-platform assembly descriptors                       | `pack-with-assembly-jsw` | Implemented | aix / all / freebsd / hpux / linux / macosx / solaris / windows |
| Bundled JRE (no local JDK required on target)           | both                    | Implemented | `com.oracle:jre` 1.8.0_202, default platform `linux-x64`       |
| Tomcat bundle                                           | `pack-with-assembly-tomcat` | Implemented | apache-tomcat 9.0.17 `-atlassian-hosted`                       |
| Sample app + log output                                 | both                    | Implemented | `service.FileLogger` writes 100 INFO lines                     |
| Unit tests                                              | both                    | Partial     | `FileLoggerTest` smoke test (JUnit 4)                          |

## 3. Requirements & Compatibility

| Requirement | Version                                                      |
| :---------- | :----------------------------------------------------------- |
| JDK         | 8+                                                           |
| Maven       | 3.6.3+                                                       |
| Wrapper     | `com.tanukisoftware.wrapper:wrapper-delta-pack:3.5.42` (see Section 7) |
| Tomcat      | `org.apache.tomcat:apache-tomcat:9.0.17-atlassian-hosted`    |

Version lines of the easy4j project:

| Branch        | JDK  | Version pattern | Notes                       |
| :------------ | :--- | :-------------- | :-------------------------- |
| `feature/1.0.x` | 8    | `1.0.x.*`       | This README, current branch |
| `feature/2.0.x` | 17   | `2.0.x.*`       | JDK 17 line                 |
| `feature/3.0.x` | 21   | `3.0.x.*`       | JDK 21 line                 |

## 4. Architecture & Modules

```text
   mvn package
        |
        v
 maven-dependency-plugin (unpack deps)
        |
        v
 pack-with-assembly-jsw          pack-with-assembly-tomcat
 wrapper-delta-pack + JRE        apache-tomcat + JRE
        |                               |
        v                               v
 per-platform zip (jeebiz-boot)   dir layout (tomcat-<ver>)
        |                               |
        +------> bin scripts + lib + conf + sample app
```

| Module                      | Packaging | Responsibility                                        |
| :-------------------------- | :-------- | :---------------------------------------------------- |
| `pack-with-assembly`        | pom       | Parent; dependency/plugin management                  |
| `pack-with-assembly-jsw`    | jar       | JSW + JRE zip distributions per platform              |
| `pack-with-assembly-tomcat` | jar       | Tomcat + JRE directory distribution                   |

Sample application classes (both modules):

| Class                            | Role                                               |
| :------------------------------- | :------------------------------------------------- |
| `main.MainClass`                 | Entry point; sets `WORKDIR` system property, logs  |
| `main.WrapperMainClassForWindows`| Windows wrapper entry point                        |
| `service.FileLogger`             | Writes 100 INFO log lines via SLF4J                |

## 5. Installation

The parent POM is an aggregator only — there is nothing to install at runtime. The two modules are packaging
templates for the sample application; if you want to consume their artifacts:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>pack-with-assembly-jsw</artifactId>
    <version>1.0.x.20260630-SNAPSHOT</version>
</dependency>
```

```groovy
implementation 'io.github.easy4j:pack-with-assembly-jsw:1.0.x.20260630-SNAPSHOT'
```

Artifacts are published to the aliyun repository and GitHub Releases; they are **not** on Maven Central yet.

## 6. Quick Start

Build both distribution recipes:

```bash
./mvnw clean package
```

Expected results:

| Module                      | Output                                                                         |
| :-------------------------- | :----------------------------------------------------------------------------- |
| `pack-with-assembly-jsw`    | `target/pack-with-assembly-jsw-1.0.x.20260630-SNAPSHOT-jsw.zip` (plus `dir` output) |
| `pack-with-assembly-tomcat` | `target/.../tomcat-<version>` directory with `bin/` scripts, `lib/`, `conf/`, bundled JRE and Tomcat |

The JSW zip contains `bin/jeebiz-boot` / `bin/jeebiz-boot.bat`, `lib/` (application + dependencies) and the
bundled JRE — start the sample app with `bin/jeebiz-boot console` (wrapper conventions), then check the
application log for the 100 `FileLogger` lines.

## 7. Configuration

There is no runtime configuration in the library sense; packaging is driven by Maven properties:

| Property                     | Module   | Default       | Purpose                                    |
| :--------------------------- | :------- | :------------ | :----------------------------------------- |
| `wrapper-delta-pack.version` | jsw      | `3.5.42`      | Wrapper distribution version               |
| `wrapper-delta-pack-setup`   | jsw      | `jeebiz-boot` | Script name inside `bin/`                  |
| `jre.version`                | both     | `1.8.0_202`   | Bundled JRE version                        |
| `jre.platform`               | both     | `linux-x64`   | Bundled JRE platform (per OS)              |
| `apache-tomcat.version`      | tomcat   | `9.0.17`      | Bundled Tomcat version (`-atlassian-hosted`) |

Platform descriptors live in `pack-with-assembly-jsw/src/main/assembly/wrapper-delta-pack-{aix,all,freebsd,hpux,linux,macosx,solaris,windows}.xml`; switch the active descriptor in the `maven-assembly-plugin` execution to build for another OS.

The `wrapper-delta-pack` and `apache-tomcat` (atlassian) artifacts are third-party zips — they must be
reachable from your Maven repository. A common setup (as used in the original example) is to download the
wrapper distribution from tanukisoftware and install it once into your Maven repository with
`mvn deploy:deploy-file` (group id `com.tanukisoftware`, packaging `zip`).

## 8. Core Usage / API

The reusable piece is the **assembly descriptor + build wiring**, not a Java API. The key wiring in the jsw
module:

| Build step            | Plugin                    | What it does                                              |
| :-------------------- | :------------------------ | :-------------------------------------------------------- |
| Unpack dependencies   | `maven-dependency-plugin` | Unpacks `wrapper-delta-pack` and the JRE into `target/dependency` |
| Create distribution   | `maven-assembly-plugin`   | Runs the platform descriptor (id `jsw`, formats `dir` + `zip`), `appendAssemblyId=true` |
| Temp dir lifecycle    | `maven-antrun-plugin`     | Creates `src/main/resources/temp` at `prepare-package`, deletes it at `install` |

Sample application behavior (`main.MainClass`):

```java
public static void main(String[] args) {
    String workDir = FileLogger.class.getResource("/").getPath();
    System.setProperty("WORKDIR", workDir);
    new FileLogger().logInfo2file(); // writes 100 INFO lines
}
```

## 9. Testing & Build

```bash
./mvnw clean verify
```

- Each module contains a JUnit 4 smoke test (`service.FileLoggerTest`).
- JaCoCo is configured with a line-coverage rule of 90% (`haltOnFailure=false`).
- Note: building the distributions requires the `wrapper-delta-pack` / `apache-tomcat` / `jre` third-party
  artifacts to be resolvable (see Section 7); without them `package` will fail at dependency resolution.

## 10. Versioning & Branches

| Branch        | JDK  | Version pattern | Maintenance                          |
| :------------ | :--- | :-------------- | :----------------------------------- |
| `feature/1.0.x` | 8    | `1.0.x.*`       | Current branch (JRE 1.8.0_202 bundle) |
| `feature/2.0.x` | 17   | `2.0.x.*`       | JDK 17 line                          |
| `feature/3.0.x` | 21   | `3.0.x.*`       | JDK 21 line                          |

Artifacts are distributed via the aliyun Maven repository and GitHub Releases. Use the branch matching your
JDK baseline.

## 11. Contributing & License

Contributions are welcome — e.g. additional assembly descriptors, CI recipes or modernizing the Tomcat/JRE
baselines. Please open an issue before larger changes.

This project is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
