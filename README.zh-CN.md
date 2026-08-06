# pack-with-assembly

[English](./README.md) | [简体中文](./README.zh-CN.md)

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 功能与状态](#2-功能与状态)
- [3. 环境要求与兼容性](#3-环境要求与兼容性)
- [4. 架构与模块](#4-架构与模块)
- [5. 安装](#5-安装)
- [6. 快速开始](#6-快速开始)
- [7. 配置](#7-配置)
- [8. 核心用法 / API](#8-核心用法--api)
- [9. 测试与构建](#9-测试与构建)
- [10. 版本线与分支](#10-版本线与分支)
- [11. 贡献与许可](#11-贡献与许可)

## 1. 项目概述

**pack-with-assembly** 是一组使用 `maven-assembly-plugin` 与 `maven-dependency-plugin` 制作可运行应用发行包的 Maven 打包示例，为多模块 POM 项目，包含两套打包方案：

| 模块                        | 方案                                                                     |
| :-------------------------- | :------------------------------------------------------------------------- |
| `pack-with-assembly-jsw`    | 将应用与 **Java Service Wrapper**、内置 JRE 一起打成各平台 zip 发行包       |
| `pack-with-assembly-tomcat` | 将应用与完整的 **Apache Tomcat**、内置 JRE 一起打成目录结构发行包           |

两个模块都内置一个最小示例应用（`main.MainClass` / `main.WrapperMainClassForWindows` + `service.FileLogger`），向文件输出日志，便于端到端验证发行包。

| 是                                                     | 不是                                     |
| :----------------------------------------------------- | :--------------------------------------- |
| 可直接复制的打包模板（JSW / Tomcat）                    | 部署工具或发布管理器                       |
| 演示 assembly 描述符、JRE 打包、wrapper 脚本            | Docker / 容器镜像构建器                   |
| 为示例应用产出 `dir`/`zip` 制品                         | 通用应用的运行时容器                       |

## 2. 功能与状态

| 能力                                                    | 模块                      | 状态       | 说明                                                            |
| :------------------------------------------------------ | :------------------------ | :--------- | :-------------------------------------------------------------- |
| Java Service Wrapper 打包                               | `pack-with-assembly-jsw`  | 已实现     | `wrapper-delta-pack` 3.5.42，脚本名 `jeebiz-boot`               |
| 多平台 assembly 描述符                                   | `pack-with-assembly-jsw`  | 已实现     | aix / all / freebsd / hpux / linux / macosx / solaris / windows  |
| 内置 JRE（目标机器无需本地 JDK）                         | 两个模块                  | 已实现     | `com.oracle:jre` 1.8.0_202，默认平台 `linux-x64`                 |
| Tomcat 打包                                             | `pack-with-assembly-tomcat` | 已实现   | apache-tomcat 9.0.17 `-atlassian-hosted`                         |
| 示例应用与日志输出                                       | 两个模块                  | 已实现     | `service.FileLogger` 输出 100 行 INFO 日志                       |
| 单元测试                                                | 两个模块                  | 部分       | `FileLoggerTest` 冒烟测试（JUnit 4）                             |

## 3. 环境要求与兼容性

| 要求   | 版本                                                           |
| :----- | :------------------------------------------------------------- |
| JDK    | 8+                                                             |
| Maven  | 3.6.3+                                                         |
| Wrapper| `com.tanukisoftware.wrapper:wrapper-delta-pack:3.5.42`（见第 7 节） |
| Tomcat | `org.apache.tomcat:apache-tomcat:9.0.17-atlassian-hosted`      |

easy4j 项目的版本线：

| 分支           | JDK  | 版本模式   | 说明                            |
| :------------- | :--- | :--------- | :------------------------------ |
| `feature/1.0.x` | 8    | `1.0.x.*`  | 本文档对应分支                   |
| `feature/2.0.x` | 17   | `2.0.x.*`  | JDK 17 版本线                   |
| `feature/3.0.x` | 21   | `3.0.x.*`  | JDK 21 版本线                   |

## 4. 架构与模块

```text
   mvn package
        |
        v
 maven-dependency-plugin (解压依赖)
        |
        v
 pack-with-assembly-jsw          pack-with-assembly-tomcat
 wrapper-delta-pack + JRE        apache-tomcat + JRE
        |                               |
        v                               v
 各平台 zip (jeebiz-boot)         目录结构 (tomcat-<版本>)
        |                               |
        +------> bin 脚本 + lib + conf + 示例应用
```

| 模块                        | 打包方式 | 职责                                              |
| :-------------------------- | :------- | :------------------------------------------------ |
| `pack-with-assembly`        | pom      | 父 POM；依赖与插件版本管理                         |
| `pack-with-assembly-jsw`    | jar      | 各平台的 JSW + JRE zip 发行包                      |
| `pack-with-assembly-tomcat` | jar      | Tomcat + JRE 目录结构发行包                        |

示例应用类（两个模块共用）：

| 类                              | 职责                                             |
| :------------------------------ | :----------------------------------------------- |
| `main.MainClass`                | 程序入口；设置 `WORKDIR` 系统属性并输出日志       |
| `main.WrapperMainClassForWindows` | Windows 下的 wrapper 入口类                     |
| `service.FileLogger`            | 通过 SLF4J 输出 100 行 INFO 日志                  |

## 5. 安装

父 POM 仅为聚合工程——运行时无需安装任何东西。两个模块是示例应用的打包模板；如需作为依赖使用：

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>pack-with-assembly-jsw</artifactId>
    <version>2.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

```groovy
implementation 'io.github.easy4j:pack-with-assembly-jsw:2.0.x.x.20260630-SNAPSHOT'
```

制品发布在阿里云私服与 GitHub Releases，**尚未发布到 Maven Central**。

## 6. 快速开始

构建两套发行包：

```bash
./mvnw clean package
```

预期产物：

| 模块                        | 产物                                                                             |
| :-------------------------- | :------------------------------------------------------------------------------- |
| `pack-with-assembly-jsw`    | `target/pack-with-assembly-jsw-2.0.x.x.20260630-SNAPSHOT-jsw.zip`（另有 `dir` 输出） |
| `pack-with-assembly-tomcat` | `target/.../tomcat-<版本>` 目录，含 `bin/` 脚本、`lib/`、`conf/`、内置 JRE 与 Tomcat |

JSW zip 内含 `bin/jeebiz-boot` / `bin/jeebiz-boot.bat`、`lib/`（应用与依赖）及内置 JRE——按 wrapper 惯例用 `bin/jeebiz-boot console` 启动示例应用，然后在应用日志中查看 100 行 `FileLogger` 输出。

## 7. 配置

该库没有运行期配置；打包行为由 Maven 属性驱动：

| 属性                         | 模块   | 默认值         | 用途                                    |
| :--------------------------- | :----- | :------------- | :-------------------------------------- |
| `wrapper-delta-pack.version` | jsw    | `3.5.42`       | wrapper 发行包版本                       |
| `wrapper-delta-pack-setup`   | jsw    | `jeebiz-boot`  | `bin/` 下的脚本名                        |
| `jre.version`                | 两个模块 | `1.8.0_202`   | 内置 JRE 版本                            |
| `jre.platform`               | 两个模块 | `linux-x64`   | 内置 JRE 平台（随操作系统调整）          |
| `apache-tomcat.version`      | tomcat | `9.0.17`       | 内置 Tomcat 版本（`-atlassian-hosted`）  |

平台描述符位于 `pack-with-assembly-jsw/src/main/assembly/wrapper-delta-pack-{aix,all,freebsd,hpux,linux,macosx,solaris,windows}.xml`；在 `maven-assembly-plugin` 执行配置中切换激活的描述符即可为其他系统打包。

`wrapper-delta-pack` 与 `apache-tomcat`（atlassian 版）是第三方 zip 制品——必须能从你的 Maven 仓库解析。原示例的做法是：从 tanukisoftware 下载 wrapper 发行包后，用 `mvn deploy:deploy-file` 将其一次性安装到 Maven 仓库（groupId 为 `com.tanukisoftware`，packaging 为 `zip`）。

## 8. 核心用法 / API

可复用的是**assembly 描述符 + 构建接线**，而非 Java API。jsw 模块的关键构建步骤：

| 构建步骤       | 插件                        | 作用                                                          |
| :------------- | :-------------------------- | :------------------------------------------------------------ |
| 解压依赖       | `maven-dependency-plugin`   | 将 `wrapper-delta-pack` 与 JRE 解压到 `target/dependency`     |
| 生成发行包     | `maven-assembly-plugin`     | 执行平台描述符（id `jsw`，格式 `dir` + `zip`），`appendAssemblyId=true` |
| 临时目录生命周期 | `maven-antrun-plugin`      | `prepare-package` 阶段创建 `src/main/resources/temp`，`install` 阶段删除 |

示例应用行为（`main.MainClass`）：

```java
public static void main(String[] args) {
    String workDir = FileLogger.class.getResource("/").getPath();
    System.setProperty("WORKDIR", workDir);
    new FileLogger().logInfo2file(); // 输出 100 行 INFO 日志
}
```

## 9. 测试与构建

```bash
./mvnw clean verify
```

- 每个模块包含一个 JUnit 4 冒烟测试（`service.FileLoggerTest`）。
- 已配置 JaCoCo 行覆盖率 90% 门禁（`haltOnFailure=false`）。
- 注意：构建发行包要求 `wrapper-delta-pack` / `apache-tomcat` / `jre` 等第三方制品可被解析（见第 7 节）；否则 `package` 阶段会在依赖解析处失败。

## 10. 版本线与分支

| 分支           | JDK  | 版本模式   | 维护说明                                      |
| :------------- | :--- | :--------- | :-------------------------------------------- |
| `feature/1.0.x` | 8    | `1.0.x.*`  | 当前分支（内置 JRE 1.8.0_202）                 |
| `feature/2.0.x` | 17   | `2.0.x.*`  | JDK 17 版本线                                 |
| `feature/3.0.x` | 21   | `3.0.x.*`  | JDK 21 版本线                                 |

制品通过阿里云 Maven 私服与 GitHub Releases 分发。请按 JDK 基线选择对应分支。

## 11. 贡献与许可

欢迎贡献——例如补充 assembly 描述符、CI 构建脚本或升级 Tomcat/JRE 基线。较大改动请先提交 issue 讨论。

本项目基于 [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0) 许可。
