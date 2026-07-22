# 常驻模式引擎：Engine / UI 分离设计

日期：2026-07-22
分支：hypermodes-rewrite
状态：已获用户批准（2026-07-22）

## 背景与问题

当前所有模式激活逻辑（`ModeManager.activateMode/deactivateMode`）只从 UI 调用：

- `ModeSchedule` 存在于数据模型并可持久化，但**没有任何调度器**——自定义模式/勿扰模式的"定时自动开启"从未实现。
- 驾驶检测（`DrivingDetector`）从 manifest receiver 直接调 `ModeManager`，与 UI 逻辑耦合。
- bedtime 由 DeskClock 自身闹钟驱动，hook 推送 `BEDTIME_ACTIVE`，但 `BedtimeStateReceiver` 只同步开关状态，**不应用 bedtime 模式的附加设置**（灰度、DND policy 等在定时就寝时全部缺失）。

目标：把模式运行逻辑（引擎）与 UI 彻底分离。UI 不需要常驻；引擎在 app 被划出后台、进程被杀后仍能按时触发模式。

## 核心原则：持续执行全部代理给系统原生机制

参考官方系统应用（DeskClock 就寝模式）的行为模式：

| 能力 | 代理机制 | 执行者 |
|---|---|---|
| 通知拦截 | DND interruption filter（NONE / PRIORITY / ALARMS） | 系统框架 |
| 联系人过滤 | `NotificationManager.Policy`（PRIORITY_CATEGORY_CALLS/MESSAGES + PRIORITY_SENDERS_ANY/CONTACTS/STARRED + 重复来电者） | 系统框架 |
| 按应用放行 | 通知渠道 `setBypassDnd`（经 system_server hook 写入他应用渠道） | 系统框架 |
| 灰度 / 深色 / 暗壁纸 | `Settings.Secure` 持久写入 | 系统设置 |
| 暂停应用 | `PackageManager.setPackagesSuspended`（经 system_server hook） | 系统框架 |
| 定时触发 | `AlarmManager.setExactAndAllowWhileIdle` | 系统闹钟服务 |

因此引擎只需在**模式激活/退出的边界时刻**运行，平时零功耗、零常驻进程。

## 架构

```
[UI 进程 com.banana.hypermodes]          [系统]
  编辑模式 → ModeStore.save
       └── ACTION_RESCHEDULE 广播 ──→ ScheduleReceiver (manifest, 冷启动)
                                          │ 为每个模式算下一次 start/end
                                          ▼
                                   AlarmManager.setExactAndAllowWhileIdle
                                          │ 到点（即使 app 被杀）
                                          ▼
                                   AlarmTriggerReceiver → ModeEngine
                                          │ activate/deactivate
                                          │ 更新 ModeStore + 状态快照
                                          ▼
                                   ACTION_MODE_STATE 广播 → UI 打开即同步
```

LSPosed 钩子（system_server 侧）把本应用提升为"系统级公民"，使上述机制在 MIUI 限制下依然可靠：

- 现有 `SystemKeepAliveHook`：防 force-stop、防划卡杀进程、广播自启动豁免（已有）。
- 追加：AlarmManagerService 闹铃配额 / App Standby 桶豁免、电池优化白名单——等价于系统应用的闹钟待遇。

## 组件设计

### `engine/ModeEngine.kt`（新，取代 `manager/ModeManager.kt`）

模式激活/恢复的**唯一入口**。`activate(mode)` / `deactivate(mode)`：

- DND：`setInterruptionFilter` + `setNotificationPolicy`，把 `contactFilter`/`allowedContacts` 映射为 Policy 的 priority senders 与类别。
- 按应用放行：`allowedApps` → 各应用通知渠道 bypassDnd（经 `SystemModeHook` 的 IPC/广播桥接；hook 不可用时记失败、不影响其余步骤）。
- 显示：灰度（daltonizer，保留现有 root 回退）、深色模式（`Settings.Secure ui_night_mode`）、暗壁纸。
- 暂停应用：`pausedApps` → `setPackagesSuspended`（经 `SystemModeHook`）。
- bedtime：向 DeskClock 发 START/STOP_BEDTIME 广播（保留现有协议）。

**状态快照**：激活前把原 interruption filter、policy、daltonizer、深色模式等写入 SharedPreferences（`engine_state`）；退出时恢复快照而非写死默认值。多个模式同时激活同一项设置时按引用计数处理（激活 +1 / 退出 -1，归零才恢复），避免叠加模式互相踩状态。

每个应用步骤独立 try/catch（沿用 `StepResult` 风格），单步失败不中断其余步骤；失败写入日志广播供 UI 诊断页展示。

### `engine/ModeScheduler.kt`（新，纯 Kotlin，无 Android 依赖）

职责：给定 `ModeSchedule` + 当前时刻 → 下一次触发（start 或 end）的 epoch millis。

- 跨午夜窗口（如 23:00–07:00）。
- `repeatDays` 位掩码（bit0=周一 … bit6=周日，复用 `Protocol.daysToBitmask` 语义）。
- schedule 未启用 / 模式被删除 → 返回 null（不排闹钟）。

纯函数设计使其可用 JVM 单元测试覆盖。

### `engine/EngineReceivers.kt`（新，manifest 注册）

全部 receiver 汇入一个入口 `rescheduleAll(context)`：清掉旧闹钟 → 遍历 ModeStore 中 enabled 模式的 schedule → 设下一次 start/end 闹钟。

- `AlarmTriggerReceiver`：闹钟触发（extra: modeId + 触发类型 start/end）→ `ModeEngine` 激活/退出 → 更新 ModeStore 的 `enabled` 标志 → 排下一次闹钟 → 发 `ACTION_MODE_STATE` 广播。
- `RescheduleReceiver`：响应 UI 的 `ACTION_RESCHEDULE`（保存模式、删除模式、手动开关后调用）。
- `BootReceiver`（已存在，扩展）：开机后重排全部闹钟 + 重注册驾驶检测（保留现有职责）。
- `TimeChangedReceiver`：`ACTION_TIME_CHANGED` / `ACTION_TIMEZONE_CHANGED` → 重排。

所有 receiver manifest 注册，由现有自启动豁免钩子保证冷启动可达。

### `hook/SystemModeHook.kt`（新，system_server 侧）

- `setPackagesSuspended` 桥：接收 app 广播（signature 权限保护），在 system_server 内调 PackageManagerService 挂起/恢复应用。
- 渠道 bypassDnd 桥：为 `allowedApps` 的通知渠道写 bypass Dnd。
- 闹钟/待机豁免：`SystemKeepAliveHook` 中追加 hook AlarmManagerService 的配额检查与 AppStandbyController 的桶判定，对本包名豁免；电池优化白名单直接写 `DeviceIdleController` 或在安装时通过 hook 放行 `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`。

### `driving/DrivingDetector.kt`（改）

检测到驾驶开始/结束时改调 `ModeEngine.activate/deactivate`，不再直接实例化 `ModeManager`。

### UI（`ui/`，减法为主）

- 删除所有 `ModeManager(context).activateMode/deactivateMode` 调用；手动开关改为：更新 ModeStore 的 `enabled` → 直接调 `ModeEngine`（前台进程内即时生效）→ 发 RESCHEDULE。
- 模式保存/删除后发 `ACTION_RESCHEDULE`。
- 监听新增的 `ACTION_MODE_STATE` 广播刷新列表（现有 DeskClockState 监听模式不变）。
- 检测到 `SCHEDULE_EXACT_ALARM` 未授权且存在启用中的定时模式时，在列表页提示跳转授权页。

### bedtime 维持 DeskClock 驱动（但补齐附加设置）

bedtime 的定时仍由 DeskClock 自身闹钟驱动（与官方 Clock app 状态一致）。改动：`BedtimeStateReceiver` 收到 `BEDTIME_ACTIVE` 推送后，除同步开关外，调用 `ModeEngine` 应用/恢复 bedtime 模式的**全部附加设置**（DND policy、灰度、暗壁纸等，跳过 DeskClock 触发步骤避免循环）。修复当前"定时就寝只翻开关"的缺口。

## 手动操作与定时的交互语义

- 定时只在 schedule 边界（start/end 时刻）触发；窗口内用户手动关闭模式后，引擎不会抢回，直到下一边界。
- 手动开启一个有 schedule 的模式 → 立即激活，end 闹钟仍按原计划到点退出。
- 删除模式 → 取消其闹钟；若该模式处于激活状态，先执行 deactivate 再删除。

## 错误处理与降级

| 场景 | 行为 |
|---|---|
| `SCHEDULE_EXACT_ALARM` 未授权 | 降级 `setAndAllowWhileIdle`（可能延迟数分钟），UI 列表页提示授权入口 |
| 模块未激活 / hook 不可用 | 暂停应用、按应用放行两步记失败；其余原生机制照常生效 |
| 进程被杀 / 重启 / 改时间 | BootReceiver + TIME_SET/TIMEZONE_CHANGED 重排全部闹钟，自愈 |
| 单步应用失败（如灰度无权限也无 root） | StepResult 记失败，继续其余步骤，UI 诊断可见 |
| DeskClock 未安装/被杀 | bedtime 广播无接收者，记失败；其余 bedtime 附加设置照常应用 |

## 测试

1. **JVM 单元测试**：`ModeScheduler` 的下次触发计算——跨午夜、部分星期、边界分钟（恰好在 start/end 时刻）、disabled 返回 null。
2. **设备端手动验证**：
   - 设 2 分钟后触发的模式 → 划掉 app（必要时 `am kill`）→ 到点验证 DND/灰度生效、ModeStore 开关翻转、到 end 点精确恢复快照。
   - 重启手机 → 闹钟重排，到点触发。
   - 两个模式窗口重叠激活同一设置 → 退出其一不恢复，全退出才恢复（引用计数）。
   - 定时就寝（DeskClock 闹钟）→ 灰度/DND policy 随之应用。
   - 驾驶蓝牙连接 → 经 ModeEngine 激活驾驶模式。

## 明确不做（YAGNI）

- 不做 NotificationListenerService 实时内容过滤（原生 Policy 已覆盖需求）。
- 不把引擎搬进 system_server 或 DeskClock 进程。
- 不做前台 Service / 常驻通知。
- 不改动 DeskClock 侧 hook（DeskClockHook/BedtimeController 保持现状）。
