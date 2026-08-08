# HyperOS 3 换壁纸（锁屏 / 桌面）实现调研

目标：在 HyperModes 的模式动作里加入"切换锁屏/桌面壁纸"，基于解包代码确认
实现路径与约束。本文只做调研，不涉及代码改动。

解包来源：`apk_decompiled/framework_decompiler/`、`services_decompiler/`、
`miui-services_decompiler/`、`settings_decompiler/`、`miuihome_decompiler/`、
`miuiaod_decompiler/`。

---

## 1. 官方入口：设置 App 并不直接改壁纸

设置里的"壁纸"入口全部是**跳转到主题管家（com.android.thememanager）**，
真正的写入由主题管家通过框架 API 完成：

| 入口 | 行为 |
|---|---|
| `WallpaperTypeSettings` (settings/wallpaper) | 打开 `theme://zhuti.xiaomi.com/provisionwallpaper?wallpaperchoose=system&miback=true&miref=...` |
| `MiuiWallpaperTypeSettings` | 打开 `ThemeTabActivity`，带 `REQUEST_RESOURCE_CODE=wallpaper`（桌面）或 `lockscreen`（锁屏） |
| `WallpaperTypePreferenceController` | 枚举 `android.intent.action.SET_WALLPAPER` 的 Activity |
| `TopLevelWallpaperPreferenceController` | 打开 `Settings$WallpaperSettingsActivity`，extra `com.android.wallpaper.LAUNCH_SOURCE` |

所以做自动化不能依赖设置/主题管家 UI，必须走框架 `WallpaperManager` 或
小米壁纸服务。

## 2. 框架 API：WallpaperManager

`android/app/WallpaperManager.java`（framework_decompiler）：

```java
public int setBitmap(Bitmap fullImage, Rect visibleCropHint, boolean allowBackup,
                     int which, int userId)          // which: 1 桌面 / 2 锁屏 / 3 两者
public int setStream(InputStream bitmapData, Rect visibleCropHint, boolean allowBackup,
                     int which)                      // 原始字节流写入
public boolean setWallpaperComponentWithFlags(ComponentName name, int which, int userId) // 动态壁纸
```

标志位：

```java
FLAG_SYSTEM = 1          // 桌面
FLAG_LOCK = 2            // 锁屏
FLAG_DESKTOP_LOCK = 3    // 桌面+锁屏
FLAG_MULTI_DESKTOP_LOCK = 12  // 折叠屏多桌面+锁屏
```

`setStream` 内部走 `IWallpaperManager.setWallpaper(...)` 拿
`ParcelFileDescriptor`，把字节流写入服务端，然后等待
`WallpaperSetCompletion`（30s 超时）确认完成，最后调用
`WallpaperManagerStub.getInstance().notifyWallpaperChanged(...)`（MIUI 侧通知）。

关键：`setStream`/`setBitmap` 路径**不需要 UiContext**（只有
`getWallpaperColors`/`getDesiredMinimumWidth` 等读方法才
`assertUiContext`），因此 system_server 的系统 Context 可以直接调用。

## 3. 系统服务端：WallpaperManagerService

`services_decompiler/.../wallpaper/WallpaperManagerService.java`：

- 权限校验：`checkPermission("android.permission.SET_WALLPAPER")`，另外检查
  `isWallpaperSupported(callingPackage)` 与 `isSetWallpaperAllowed`（用户限制
  `no_set_wallpaper`）。system_server（uid 1000）天然满足。
- 写入位置：`/data/system/users/{userId}/wallpaper_orig`（桌面源图）、
  `wallpaper_lock_orig`（锁屏源图）、裁剪图 `wallpaper` / `wallpaper_lock`、
  元数据 `wallpaper_info.xml`（`WallpaperUtils.java`）。
- `WallpaperObserver`（FileObserver）监听源图变化 → 重新生成裁剪图 → 重绑壁纸组件。
- 桌面只改（which==1）时若当前桌面/锁屏是同一张，会先
  `migrateStaticSystemToLockWallpaperLocked` 把现有桌面迁移为锁屏，再写新桌面。
- 写完后发送 `android.intent.action.WALLPAPER_CHANGED` 广播，extra 带
  `WHICH_WALLPAPER_CHANGED`。Home 收到后自动重新适配图标/颜色
  （`miuihome .../DesktopWallpaperManager.java` 的
  `WallpaperBroadcastReceiver`）。

## 4. MIUI 侧：锁屏壁纸组件与 Stub

`miui-services_decompiler/.../wallpaper/WallpaperManagerServiceImpl.java`：

```java
private final ComponentName mKeyguardWallpaperComponent =
    ComponentName.unflattenFromString(
        "com.miui.miwallpaper/.wallpaperservice.MiuiKeyguardPictorialWallpaper");
MIUI_WALLPAPER_COMPONENTS = [ImageWallpaper, MiuiKeyguardPictorialWallpaper]
```

- `bindWallpaperComponentLocked`：which==2（锁屏）时绑定
  `MiuiKeyguardPictorialWallpaper`；桌面绑定 `ImageWallpaper`。
- `setMiuiWallpaperLayer`：锁屏壁纸窗口层 `POSITION_TOP`，桌面 `POSITION_BOTTOM`。
- 锁屏壁纸描述接口 `miui.miwallpaper.keyguard.Wallpaper`，桌面
  `miui.miwallpaper.desktop.Wallpaper`。

结论：**只要调用标准 WallpaperManager 接口，MIUI 会自动绑定正确的锁屏/桌面
壁纸组件，不需要我们手动操作 com.miui.miwallpaper。**

## 5. 小米壁纸服务（com.miui.miwallpaper，可选）

miuiaod/miuihome 里引用的 `IMiuiWallpaperManagerService` 暴露了
`setWallpaper/setWallpaper2/setWallpaper3/setMiuiWallpaper`（参数：which、文件路径、
名称、ComponentName、bool、list），用于视频壁纸 / 超级壁纸 / 动态效果等高级类型。
**静态图片换壁纸不需要它**，标准 WallpaperManager 足够。

## 5.1 主题管家解包后的关键发现（thememanager_decompiler）

官方 UI（com.android.thememanager）内部的壁纸应用由 `WallpaperApplyInfos`
驱动，这正是"锁屏+桌面合并成一个 set"的官方机制：

### WallpaperApplyInfos（model/WallpaperApplyInfos.java）

- `singleWhich`：应用范围。`-1`=按调用方判断、`1`=桌面、`2`=锁屏、
  `3`=锁屏+桌面合并、`4/8`=折叠屏内/外屏。
- `applyTogether`：是否"锁屏+桌面一起应用"（合并 set 的开关）。
- `ApplyCode` 枚举：`LOCK` / `HOME` / `BOTH_LOCK_HOME`（锁屏+桌面）/
  `LARGE_SCREEN` / `SMALL_SCREEN` / `BOTH_SCREEN`（折叠屏内外屏）。
- 其它字段：`effectId`（特效）、`enableBlur`（模糊）、`doodleStatus`（手绘状态）、
  `mSyncToFashionGallery`（同步到时尚画廊）。

### 应用范围如何决定（wallpaper/WallpaperController.java）

```java
public static int fn3e(boolean isLockScreen, WallpaperApplyInfos applyInfo) {
    if (applyInfo != null && applyInfo.getSingleWhich() > -1) {
        return applyInfo.getSingleWhich();   // 调用方显式指定（1 桌面 / 2 锁屏 / 3 合并）
    }
    if (DeviceUtils.lvui()) return t8r(isLockScreen);  // 折叠屏 10/5
    return isLockScreen ? 2 : 1;
}
```

`BaseWallpaperApplyTask` 中"合并应用"的固定写法（controller/BaseWallpaperApplyTask.java）：

```java
this.m.applyTogether = true;
if (DeviceUtils.jk()) {
    this.m.setSingleWhich(3);                 // 直板机：锁屏+桌面合并
} else {
    this.m.setSingleWhich(MiuiWallpaperManager.f);  // 折叠屏：15（全部）
}
```

`WallpaperUtils.ni7(...)` 里按 `applyInfos.applyTogether` 分流：
`t8r(...)`（合并，一次写入锁屏+桌面）或 `z(...)`（单屏），最终都走到
`WallpaperController` → `MiuiWallpaperManager.WallpaperApplierBuilder.wvg(which)`
→ `setStream`/`setBitmap`（which=3 即合并）。

### 外部调用官方 UI 的入口

`activity/WallpaperExternalPreviewActivity.java` 是专门给外部进程用的壁纸预览
+ 应用页，通过 Intent 传入：

- `IntentConstants.ga8`（entrance）：`EnumExternalWallpaperPreviewEntrance`，
  如 `ENTRANCE_LOCK(1)` / `ENTRANCE_HOME(2)` / `ENTRANCE_ALBUM(4)` /
  `ENTRANCE_EDIT(8)` / `ENTRANCE_HOME_EDIT_CARD(32)` / `ENTRANCE_KEYGUARD_EDIT_CARD(64)`。
- 图片路径（`IntentConstants.u02j` / `IntentConstants.v6we` / data URI）。
- 应用时 `WallpaperApplyInfos.setSingleWhich(i2)`，i2 由入口决定
  （如 HOME → 4，对应折叠屏外屏；直板机走 `WallpaperController.fn3e` 判断）。

其它官方入口（上一轮已记录）：`WallpaperSettingsActivity`、
`ThemeTabActivity` + `REQUEST_RESOURCE_CODE`、`theme://` 深链。

> 结论：**"锁屏+桌面合并成一个 set"在官方 UI 里就是
> `WallpaperApplyInfos.singleWhich=3` + `applyTogether=true`**。若 HyperModes
> 直接调 `WallpaperManager.setStream(..., which=3)` 效果等价；若希望用户通过
> 官方界面操作，则启动 `WallpaperExternalPreviewActivity` 或
> `WallpaperSettingsActivity` 并指定入口/图片参数，用户在官方预览页确认后
> 由主题管家完成合并应用。

> 关键：**官方 UI 里锁屏和桌面壁纸可以各自不同**（用户分别设锁屏图、桌面图），
> 也可以同一张（合并应用）。对 HyperModes 而言，"一个 set" = **锁屏快照 +
> 桌面快照两个独立子项**：进入模式时把两者都应用（锁屏走 `FLAG_LOCK`/锁屏样式
> JSON，桌面走 `FLAG_SYSTEM`/`<wp>` 节点），保存与恢复都作为一个整体 set 一起
> 处理，但两张图可以不同。

## 6. 锁屏配置的持久化与自动恢复（新需求核心）

用户需求：锁屏走**官方定制界面**（编辑锁屏样式/壁纸），桌面走官方壁纸页
（可分别设置不同的壁纸图），两者**作为一个 set 一起保存**，下次进入该模式
自动恢复整套（锁屏样式 + 锁屏壁纸 + 桌面壁纸）。

### 6.1 官方锁屏定制界面入口

| 入口 | 用途 |
|---|---|
| `com.android.thememanager/com.android.thememanager.settings.WallpaperSettingsActivity` | 锁屏样式设置页（设置"锁屏样式"推荐入口，`AodAndLockScreenSettings.tryBuildRecommendLayout` 里的 `#Intent;component=...WallpaperSettingsActivity;end`） |
| `com.android.thememanager/com.android.thememanager.activity.ThemeTabActivity` + extra `REQUEST_RESOURCE_CODE=lockscreen` | 锁屏壁纸/样式 tab（`MiuiWallpaperTypeSettings.getLockscreenPreference`） |
| `theme://zhuti.xiaomi.com/provisionwallpaper?wallpaperchoose=...` | 主题管家深链（`WallpaperTypeSettings` 用 `wallpaperchoose=system` 开桌面壁纸） |

### 6.2 锁屏配置存在哪里（编辑后要保存的东西）

锁屏配置 = **锁屏样式 JSON + 锁屏壁纸文件**两部分，都要快照/恢复。

#### 锁屏样式 JSON（时钟/颜色/手绘/智能相框/签名等）

存于 Settings.Secure，是完整的一套 JSON：

| 键 | 内容 | 谁读 |
|---|---|---|
| `constant_lockscreen_info` | `ConstantLockscreenInfo` JSON：`clockInfo`(ClockBean) / `wallpaperInfo` / `doodle` / `smartFrame` | SystemUI `KeyguardPanelViewController` |
| `constant_template_editor_info` | `{"lockscreenInfo": <上面的 JSON>}`，模板编辑器写入 | SystemUI `constantLockscreenInfoObserver` 监听此键，解析出 lockscreenInfo 后调 `onLockScreenInfoChange` |
| `miui_15_default_lockscreen_info` + `lockscreen_info_version` | OS3 默认锁屏信息（`buildLockScreenInfoOS3` 生成 classic_max） | 观察者里作为兜底/升级 |

JSON 内部结构（`com.android.keyguard.wallpaper.entity`）：

- `clockInfo`（`ClockBean`，核心样式）：`templateId`（时钟模板，如 classic/classic_plus/rhombus/magazine 等）、`style`、`infoAreaColor`/`primaryColor`/`secondaryColor`/`blendColor`/`secondaryBlendColor`、`isAutoPrimaryColor`/`isAutoSecondaryColor`/`isDiffHourMinuteColor`、`classicLine1..5`（日期/天气/运营商等行）、`signatureLine1..3`、`classicSignature`、`clockEffect`、`clockWeight`、`enableDiffusion`、`presetHealthJson`/`presetWeatherJson`、`dualClockLocalCity`、`extraFlag` 等
- `doodle`（`DoodleInfo`，手绘）：`solidColor`、`ribbonColor1/2`、`isAutoSolidColor`
- `smartFrame`（`SmartFrameInfo`，智能相框）：`solidColor`、`isAutoSolidColor`、`shape`
- `wallpaperInfo`（`WallpaperInfo`/`WallpaperTrackInfo`）：`cropSubject`、`magicType`、`supportSubject`、`largeScreenHierarchyEnable`
- 另有 `LockscreenInfo`（含 `templateId` + `signatureInfo`）与 `HomeInfo`（桌面侧 `followStatus` + `wallpaperInfo`）两种 OS3 形态，恢复时按设备实际结构原样写回

> 恢复时**整段 JSON 原样写回** `constant_lockscreen_info`（以及
> `constant_template_editor_info` 的 `{"lockscreenInfo": ...}` 包装），即可让
> SystemUI 观察者一次性重建时钟、颜色、手绘、智能相框与签名，无需逐字段还原。

#### 锁屏壁纸文件

| 路径 | 说明 |
|---|---|
| `/data/system/users/{uid}/wallpaper_lock_orig` | 锁屏源图（WallpaperManagerService 写入） |
| `/data/system/users/{uid}/wallpaper_lock` | 锁屏裁剪图 |
| `/data/system/users/{uid}/wallpaper_info.xml` | 元数据（`<kwp>` 节点 = 锁屏） |
| `/data/system/theme/lock_wallpaper` | Launcher 写入的锁屏壁纸（`WallpaperUtils.setLockWallpaperWithoutCrop`） |
| `/data/system/theme_magic/users/{uid}/wallpaper/data/` | 主题管家壁纸数据（视频/特效等） |

### 6.3 锁屏配置如何应用（恢复时的触发点）

- SystemUI `KeyguardPanelViewController` 注册
  `Settings.Secure` 的 `constant_template_editor_info` 内容观察者
  （`registerContentObserverAsUser(... UserHandle.ALL)`），键一变就
  `parse -> onLockScreenInfoChange(lockscreenInfo, false)`：
  - `clockInfo` → `MiuiClockController.updateClockBeanFromJson` 换时钟样式
  - `doodle` / `smartFrame` → 重建 DoodleView / SmartFrameView
  - 同步 `miuiFullAodManager.onLockScreenInfoChanged()`
- Launcher 设置锁屏壁纸后发 `com.miui.keyguard.setwallpaper` 广播
  （`WallpaperUtils.onLockWallpaperChanged`），并把锁屏路径通知主题管家：
  `content://com.android.thememanager.provider/lockscreen` + `key_lockscreen_path`
- 锁屏壁纸文件变化由 `WallpaperManagerService` 的 `WallpaperObserver`
  （FileObserver 监听 `wallpaper_lock_orig`）自动感知 → 重新生成裁剪 → 重绑
  `MiuiKeyguardPictorialWallpaper` 组件。

### 6.4 保存 / 恢复方案

#### 生命周期总览（先备份 → 应用 → 退出恢复）

核心约束：**进入模式前必须先快照"当前"锁屏样式/壁纸配置**，模式结束
（退出/切换）时用它恢复原状。锁屏与桌面**可单独编辑、单独生效**：

- 某个子项（锁屏或桌面）**有数据** → 该子项走"备份 → 应用 → 退出恢复"流程。
- 某个子项**无数据（未编辑）** → 该子项完全不进入备份/回滚，保持系统当前值
  不变；只有有数据的子项才参与 set 的保存与恢复。

任何模式的壁纸/样式切换都遵循（按子项独立执行）：

```
进入模式
  ├─ 对每个"有数据的子项"：
  │   ├─ 1. 若该子项无原始备份：快照当前值 → "该子项的原始快照"
  │   ├─ 2. 应用本模式的该子项（锁屏：写 JSON + 复制壁纸文件；桌面：写文件 + 键）
  │   └─ 3. 记录"该子项已应用"标记
  └─ 无数据子项：跳过（不动系统、不备份、不标记）

退出模式 / 切换到另一模式
  ├─ 对每个"已应用标记"的子项：
  │   ├─ 1. 若该子项存在"原始快照"：用快照回写（锁屏：JSON 三键 + 壁纸文件；
  │   │       桌面：壁纸文件 + 滚动/特效键）
  │   └─ 2. 清除该子项的"已应用"标记（原始快照保留到全部模式退出才删）
  └─ 未应用过的子项：跳过
```

要点：

- **数据判空即"未编辑"**：子项快照为空 / 配置 JSON 中该子项为 null，视为未编辑，
  跳过备份与回滚，对系统零影响。
- **原始快照只在"没有任何模式处于激活"时创建**；模式 A 激活后再进模式 B，
  不重新备份，退出 B 恢复 A，退出 A 才恢复原始。这样嵌套切换不会覆盖初始值。
- 快照存系统可读路径（如 `/data/system/theme_magic/../hypermodes_backup/` 或
  App 外部存储 + 路径写进配置 JSON），system_server 可读可写。
- 快照内容 = 锁屏样式 JSON 三键 + `wallpaper_lock_orig`/`wallpaper_lock` +
  `wallpaper_info.xml` 的 `<kwp>` 段 + 桌面 `wallpaper_orig`/`wallpaper` +
  `<wp>` 段 + 滚动/特效 Settings.Secure 键。**快照按子项独立存储**
  （`lockSnapshot` / `desktopSnapshot` 两段），互不影响。
- 崩溃兜底：system_server 重启后引擎可读取持久化的"原始快照 + 已应用标记"，
  按子项逐个检查：有标记则回写该子项，保证异常退出后样式能恢复。

**保存（用户编辑完成后）**：快照以下内容到模式配置（存 App 私有目录 + 路径写进
配置 JSON，或存 `/data/system/theme_magic` 旁的系统可读路径）：

1. `Settings.Secure["constant_lockscreen_info"]`（**锁屏样式 JSON 整段**：
   时钟/颜色/手绘/智能相框/签名）
2. `Settings.Secure["constant_template_editor_info"]`（编辑器信息，含 version
   与 `lockscreenInfo` 包装）
3. `Settings.Secure["miui_15_default_lockscreen_info"]` + `lockscreen_info_version`
   （OS3 默认/兜底，按需一并快照）
4. 锁屏壁纸源图 `/data/system/users/{uid}/wallpaper_lock_orig` + 裁剪图
   `wallpaper_lock` + `wallpaper_info.xml` 的 `<kwp>` 段
5. 若涉及主题管家壁纸特效，另备份 `/data/system/theme_magic/users/{uid}/wallpaper/`
   对应目录

**恢复（下次进入该模式时）**：

1. 把保存的 `constant_lockscreen_info`（锁屏样式 JSON 整段）写回
   `Settings.Secure`（`putStringForUser`，用 system_server 的 ContentResolver）；
   同时把 `constant_template_editor_info` 写成
   `{"lockscreenInfo": <保存的 JSON>}`（必要时连 `miui_15_default_lockscreen_info`
   与 `lockscreen_info_version` 一起写回）—— 这一步会触发 SystemUI 观察者
   立即应用锁屏样式（时钟/颜色/布局/手绘/智能相框/签名）。
2. 把保存的锁屏壁纸源图复制到 `wallpaper_lock_orig`、裁剪图复制到
   `wallpaper_lock`，`wallpaper_info.xml` 写回 `<kwp>` 节点（或直接调用
   `WallpaperManager.setStream(..., FLAG_LOCK)` 让服务端重写文件）。
   FileObserver 自动感知并重绑锁屏壁纸组件。
3. 发 `com.miui.keyguard.setwallpaper` 广播兜底通知。
4. 退出模式时反向恢复原锁屏配置（同一套快照/回写机制，先备份当前值）。

## 7. 桌面壁纸（走官方界面 + 保存/恢复）

用户需求：桌面壁纸也**走官方界面**（官方壁纸设置页带裁剪/滚动/特效等额外
选项），编辑后保存配置，下次进入该模式自动恢复。

### 7.1 官方桌面壁纸界面入口

| 入口 | 说明 |
|---|---|
| action `miui.intent.action.THEME_WALLPAPER_PICKER_PAGE` → `com.android.thememanager.settings.WallpaperSettingsActivity`（可带 `entrance=homeEdit`、`bottomEntryHeight`） | 长按桌面 → 壁纸 的官方入口（`EditingEntryThumbnailView.jumpThemeWallpaper`），带裁剪/滚动等选项 |
| `com.android.thememanager.activity.ThemeTabActivity` + `REQUEST_RESOURCE_CODE=wallpaper` | 设置"桌面壁纸"入口（`MiuiWallpaperTypeSettings.getDesktopPreference`） |
| `theme://zhuti.xiaomi.com/provisionwallpaper?wallpaperchoose=system&miback=true&miref=...` | 设置"壁纸"默认跳转（`WallpaperTypeSettings`） |
| `com.personalizedEditor.activity.WallpaperEffectDialogActivity`（action `miui.intent.action.THEME_WALLPAPER_EDITOR_PREVIEW`） | 桌面壁纸特效编辑 |

另在 `AppHandleGestureManager` 里发现主题管家还有专门给桌面编辑用的
Picker Activity：`WallpaperMiuiTabHomeEditPickerActivity`、
`ThemeAndWallpaperHomeEditPickerSettingActivity`、
`SuperWallpaperListHomeEditPickerActivity`、`AiWallpaperListHomeEditPickerActivity`。

### 7.2 官方界面保存的额外配置（要快照的东西）

桌面壁纸在官方界面编辑后，除了壁纸图本身，还持久化以下内容：

| 内容 | 位置 |
|---|---|
| 桌面壁纸源图 | `/data/system/users/{uid}/wallpaper_orig` |
| 桌面壁纸裁剪图 | `/data/system/users/{uid}/wallpaper` |
| 元数据（裁剪区域/组件/宽高） | `/data/system/users/{uid}/wallpaper_info.xml` 的 `<wp>` 节点 |
| 滚动壁纸开关 | `Settings.Secure["pref_key_wallpaper_screen_scrolled_span"]`（Home 的 `DesktopWallpaperManager` 监听此键，=1 可滚动） |
| 桌面壁纸特效类型 | `Settings.Secure["wallpaper_effect_type_1"]`（桌面）/ `wallpaper_effect_type_2`（锁屏，`KeyguardCommonSettingsRepository` 读取） |
| 壁纸来源标记 | `Settings.Secure["wallpaper_changed_2"]`（谁改的，SystemUI `MiuiKeyguardWallPaperManager` 据此决定是否刷新颜色） |
| 特效数据（视频/超级壁纸等） | `/data/system/theme_magic/users/{uid}/wallpaper/data/` |

### 7.3 恢复流程（下次进模式自动切换）

1. 把保存的 `wallpaper_orig` / `wallpaper` 复制回对应路径，`wallpaper_info.xml`
   写回 `<wp>` 节点（或直接 `WallpaperManager.setStream(FLAG_SYSTEM)` 让服务端
   重建，裁剪 hint 走 crop 参数）。`WallpaperObserver` 自动感知并重绑
   `ImageWallpaper` 组件。
2. 写回 `Settings.Secure["pref_key_wallpaper_screen_scrolled_span"]`（滚动）、
   `wallpaper_effect_type_1` / `wallpaper_effect_type_2`（特效）、
   `wallpaper_changed_2`（来源），Home/SystemUI 的观察者自动刷新。
3. 若涉及特效数据，恢复 `/data/system/theme_magic/...` 对应目录。
4. 退出模式时反向恢复原桌面壁纸（同一套快照/回写机制）。

桌面和锁屏共用同一套"快照 → 回写 → 触发系统观察者"机制，只是键名/文件
不同（桌面 `wp` + FLAG_SYSTEM，锁屏 `kwp` + FLAG_LOCK + 锁屏样式 JSON）。

**锁屏与桌面作为一个 set 一起保存/恢复**：用户可以在官方界面里给锁屏和桌面
分别设置不同的壁纸图（锁屏走 `ThemeTabActivity`+`lockscreen` 或
`WallpaperSettingsActivity`，桌面走 `wallpaper` 入口），HyperModes 把两者
**一起快照**（锁屏样式 JSON + 锁屏壁纸 + 桌面壁纸，同一套 set），下次进入该
模式**一起恢复**（锁屏、桌面各自按自己的键/文件回写）。两张图是否相同不影响
保存结构——set 始终包含锁屏子项和桌面子项，允许不同图。

## 8. 已知冲突与注意事项

1. **锁屏画报（Wallpaper Carousel）**：若用户开启"锁屏画报"（设置里
   `lockscreen_magazine`，AodAndLockScreenSettings），锁屏壁纸可能被画报应用
   覆盖，静态锁屏壁纸不生效或短暂生效后被换掉。需要在功能说明中提示用户关闭，
   或检测 `miui.intent.action.LOCKWALLPAPER_PROVIDER` 提供者存在时降级。
2. **壁纸轮播（桌面）**：同样可能覆盖桌面壁纸，需提示。
3. **折叠屏**：内/外屏壁纸语义复杂（which=4/8/12），本项目设备为直板机，
   先只支持 1/2/3。
4. **锁屏样式 JSON 的键名**：`constant_lockscreen_info` /
   `constant_template_editor_info` / `miui_15_default_lockscreen_info` 是解包
   确认的键；实际版本可能有差异，落地时先 dump 设备 Settings.Secure 核对。
5. **超级壁纸/动态壁纸**：静态图方案会替换动态壁纸；还原需额外记录
   动态壁纸组件（`setWallpaperComponentWithFlags`）与 theme_magic 特效数据。
6. **写入耗时**：setStream 会阻塞等待完成回调（内部 30s 上限），应在后台线程
   执行，避免卡住 system_server 主线程。
7. **桌面滚动/特效键**：`pref_key_wallpaper_screen_scrolled_span`、
   `wallpaper_effect_type_1/2`、`wallpaper_changed_2` 均来自解包确认；落地前
   先 dump 设备 Settings.Secure 核对实际取值。
8. **官方界面入口的包名/类名**：`WallpaperSettingsActivity` 与
   `WallpaperMiuiTabHomeEditPickerActivity` 等都属于主题管家
   （com.android.thememanager），设备上要确认这些 Activity 存在
   （`resolveActivity` 探测），不存在时回退到 `ThemeTabActivity` +
   `REQUEST_RESOURCE_CODE`。

## 9. 结论

**锁屏 + 桌面 = 一个 set**：官方界面里锁屏和桌面壁纸可以各自不同，但
HyperModes 把两者**作为一个整体 set 一起保存、一起恢复**。set 的组成：

```
Mode Set（模式壁纸/锁屏配置）
 ├─ 锁屏子项：锁屏样式 JSON（constant_lockscreen_info 等三键）
 │           + 锁屏壁纸文件（wallpaper_lock_orig/wallpaper_lock + <kwp>）
 └─ 桌面子项：桌面壁纸文件（wallpaper_orig/wallpaper + <wp>）
             + 滚动/特效 Settings.Secure 键
```

进入模式时按子项分别应用（锁屏回写 JSON + `FLAG_LOCK`，桌面回写文件 +
`FLAG_SYSTEM`），退出时整体还原。用户编辑任意一个子项都会写入同一份 set。

**惰性子项（关键优化）**：锁屏、桌面**可单独编辑**——配置 JSON 里
`lockSnapshot` / `desktopSnapshot` 各自独立，哪个有数据就只对哪个走
"备份 → 应用 → 退出恢复"；**无数据（未编辑）的子项完全不进入备份/回滚**，
对系统零影响。例如只配置了桌面壁纸：进入模式只备份+应用桌面，退出只还原桌面，
锁屏保持用户当前值不动。

**桌面**：走官方界面（`miui.intent.action.THEME_WALLPAPER_PICKER_PAGE` →
`WallpaperSettingsActivity` 或 ThemeTabActivity），编辑后快照壁纸文件 +
`wallpaper_info.xml` `<wp>` 节点 + 滚动/特效 Settings.Secure 键；下次进模式
回写文件与键 + 触发系统观察者即自动恢复。官方界面保证了裁剪、滚动、特效等
额外选项与系统行为一致。

**锁屏**：先打开官方定制界面（ThemeManager `WallpaperSettingsActivity` 或
`ThemeTabActivity` + `REQUEST_RESOURCE_CODE=lockscreen`），用户编辑完成后
**快照**锁屏样式 JSON（Settings.Secure 两键）+ 锁屏壁纸文件；下次进入模式时
**回写 Settings.Secure + 复制壁纸文件 + 触发 SystemUI 观察者**即可自动恢复。
这是"编辑一次、模式内自动切换"的完整闭环。

工作量集中在：配置模型扩展（锁屏快照 + 桌面快照，合并为一个 set）、
按子项惰性备份/回滚（有数据才进流程）、文件/设置备份与回写、
官方界面启动探测（resolveActivity 兜底）、`WallpaperController` 挂进
`ModeActionExecutor`、以及锁屏画报/桌面滚动特效冲突提示。
