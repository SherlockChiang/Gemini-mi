# GeminiMi

在国行小米 HyperOS 上，将“长按电源键唤起超级小爱”替换为 **Gemini Overlay**。

模块会自动选择 Google 数字助理、开启屏幕内容与截图辅助，并在重启后恢复被 HyperOS
重置的助手设置。无需手动修改系统设置数据库。

## 适用范围

- 国行 HyperOS 设备
- Android 11 及以上
- 已安装并完成初始化的 Google App 和 Gemini
- 已安装 LSPosed，且能够为模块授予 `android` 系统框架作用域

本模块针对 HyperOS 的系统实现编写，不建议在非小米 ROM 上启用。

## 功能

- 长按电源键显示 Gemini Overlay，而不是打开 Gemini 普通应用界面
- 禁止超级小爱重新启用“电源键唤醒”
- 将系统语音助手、语音交互服务和语音识别服务设置为 Google
- 开启助手读取屏幕文字与截图的权限开关
- 将系统长按电源键行为设置为“数字助理”
- 在开机和用户解锁时恢复被 HyperOS 重置的助手设置
- 支持 Android 多用户分别写入助手设置

## 安装

1. 从 [Releases](https://github.com/Xposed-Modules-Repo/com.vince.geminimi/releases/latest)
   下载并安装最新 APK。
2. 在 LSPosed 中启用 **GeminiMi**。
3. 作用域勾选 `android`（系统框架）、`com.miui.voiceassist`（超级小爱）、
   `com.google.android.apps.bard`（Gemini）和 `com.google.android.googlequicksearchbox`
   （Google App）。
4. 确认 Google App 和 Gemini 已安装，并至少手动启动过一次。
5. 重启设备。
6. 解锁后长按电源键测试 Gemini Overlay。

更新模块时可直接覆盖安装。请勿混用不同来源或不同签名的 APK，否则 Android 会拒绝升级。

## 排障

长按电源键没有反应时，先执行：

```text
adb logcat -s "[GeminiMi]"
```

正常情况下应看到类似日志：

```text
PowerKeyOverlay hooked 1 method(s)
hooked PhoneWindowManager#...
assistant settings write success=true verified=true
```

常见情况：

- `hooked 0 method(s)`：当前 HyperOS 版本使用了未知的方法名，需要针对该 ROM 适配。
- `showSessionForActiveService failed`：当前 ROM 的隐藏系统接口签名不同，请在 Issue 中附上完整日志。
- 弹出 Gemini 普通应用而非 Overlay：确认 Google App 的数字助理已切换为 Gemini，并重启设备。
- 重启后仍是小爱：确认 LSPosed 作用域包含 `android` 和 `com.miui.voiceassist`。
- Gemini 仍提示地区不可用：确认 Gemini 与 Google App 都在作用域中，并强制停止后重试。

反馈问题时请提供设备型号、HyperOS/Android 版本、Google App/Gemini 版本和上述日志。

## 已知限制

- HyperOS 不同版本会修改 `PhoneWindowManager` 方法名，不能保证所有设备无需适配。
- Android 16 / HyperOS 3 已适配带 attribution tag 的助手会话接口。
- Overlay 依赖 Google App 的 VoiceInteractionService；仅安装 Gemini、不安装 Google App
  无法工作。
- 模块会在用户解锁时重新写入 Google 助手设置。系统完成启动后，仍允许用户手动切换
  其他数字助理。
- 模块只在 Gemini 与 Google App 进程内将 SIM/网络国家伪装为美国（`us` / `310030`），
  同时覆盖 `SubscriptionInfo` 与对应的 `gsm.*operator.*` 系统属性读取；不会修改真实
  SIM，也不会影响其他应用。
- 模块运行在系统框架中。启用前请确保设备具备可用的 LSPosed 救砖/安全模式方案。

## 工作原理

- `PowerKeyOverlayHook` 接管 HyperOS 的长按电源键助手入口，通过系统语音交互服务显示
  Overlay。
- `AssistantPersistHook` 设置并验证 Google 助手相关的 Secure/Global 设置。
- `XiaoAiPowerKeyDisableHook` 精确覆盖超级小爱的 `power_wakeup` 偏好。
- `SimSpoofHook` 只在 Gemini 与 Google App 进程内拦截地区检查所需的 Telephony API。

Hook 失败时会放行原系统逻辑，避免异常继续传播到 `system_server`。

## 开发

使用 JDK 17 和 Android SDK 34：

```text
gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleRelease
```

未配置签名环境变量时，release 产物为：

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

仓库通过 GitHub Actions 自动构建签名版本。LSPosed 官方仓库的 Release tag 使用
`VersionCode-VersionName` 格式，例如 `2-1.0.1`。

签名初始化和维护说明见：

```text
scripts/setup-release-signing.ps1
```

## 链接

- [LSPosed 模块页面](https://modules.lsposed.org/module/com.vince.geminimi/)
- [官方模块仓库](https://github.com/Xposed-Modules-Repo/com.vince.geminimi)
- [开发仓库](https://github.com/SherlockChiang/Gemini-mi)
