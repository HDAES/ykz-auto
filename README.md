# YKZ Auto

Android 自动化应用基础工程，面向“收到目标应用通知后，在允许的时间段内启动并执行页面操作”的场景。

## 当前状态

- Kotlin + Jetpack Compose 单模块工程
- `applicationId`: `com.hdaes.ykzauto`
- `minSdk`: 26（Android 8.0）
- `targetSdk` / `compileSdk`: 36
- JDK 17
- Gradle 8.13
- Android Gradle Plugin 8.13.2

## 后续模块

1. NotificationListenerService：识别目标应用通知。
2. AccessibilityService：执行点击、滚动和页面状态判断。
3. Schedule：限制任务只在指定时间段运行。
4. Execution log：记录成功、失败、超时和重试。
5. Xiaomi/HyperOS 适配：自启动、后台运行、电池优化和锁屏限制引导。

## 本地运行

使用支持当前 Android Gradle Plugin 的 Android Studio 打开项目，安装 Android SDK 36，并使用 JDK 17。

已安装 Gradle 8.13 时，可以直接构建：

```bash
gradle assembleDebug
```

也可以先生成标准 Gradle Wrapper，再使用项目内命令构建：

```bash
gradle wrapper --gradle-version 8.13
./gradlew assembleDebug
```
