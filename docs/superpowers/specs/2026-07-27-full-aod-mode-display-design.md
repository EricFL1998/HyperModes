# HyperModes Full-AOD 模式显示设计

**日期：** 2026-07-27  
**目标系统：** 基于 Android 16 的 Xiaomi HyperOS 3  
**范围：** 在全屏/超级壁纸 Full-AOD 中显示当前模式的图标和名称

## 1. 背景与根因

当前实现已经可以在锁屏底部显示活动模式，但进入全屏 AOD 后内容不可见。

现有锁屏路径将模式视图加入 `KeyguardBottomAreaInjector.mIndicationArea`，并在 Doze 开始时主动淡出。现有 AOD 路径则 Hook `com.miui.aod.AODView.onAttachedToWindow()`，把第二份模式视图直接加入 miuiaod 的 `AODView` 根节点。

反编译代码表明，这条 AOD 路径不适用于 Full-AOD：

- Full-AOD 开始后，miuiaod 的 `DozeHost` 会把 `com.miui.aod.AODView` 的父级 alpha 设为 `0`；
- 普通 AOD 的 `clock_container` 会被移除；
- 锁屏壁纸、锁屏元素、通知和转场改由 SystemUI Full-AOD 图层管理；
- 强制子视图自身的 alpha 或 visibility 无法突破父视图 alpha 为 `0` 的状态。

因此问题不在模式配置、图标或名称，而在于模式视图挂载到了 Full-AOD 不再显示的普通 AOD 内容树。

## 2. 已确认的产品目标

1. 模式图标和名称在 Full-AOD 期间全程保持显示。
2. Full-AOD 中的位置与锁屏中的当前位置连续，切换时不明显跳变。
3. 模式内容跟随 HyperOS 原生 Full-AOD 转场、缩放和 OLED 防烧屏位移。
4. 本次只支持 Full-AOD；普通黑底 AOD 不属于支持范围。
5. 实现遵循“原生怎么管理 Full-AOD，我们就接入同一套机制”，不另建平行的动画或定时位移系统。

## 3. 方案比较

### 3.1 继续向 miuiaod `AODView` 注入

不采用。Full-AOD 会把该根节点设为透明并移除普通时钟容器，子视图无法保持可见。

### 3.2 强制锁屏 indication view 在 Doze 中保持可见

不采用。`keyguard_indication_area` 位于 `KeyguardBottomAreaView` 内；Full-AOD 会把整个底部区域的 `transitionAlpha` 设为 `0`。只修改子视图 visibility 无法对抗父容器隐藏，而且该区域不直接参与原生 Full-AOD 防烧屏位置回调。

### 3.3 注入 SystemUI `aod_root_view` 并继承原生动画

采用。SystemUI 的 `com.android.keyguard.widget.AodView` 是 `aod_root_view`，在 Doze 时由 SystemUI 显示并作为插件宿主。HyperOS 原生会把这个宿主加入 `KeyguardPanelViewController.animationViews`，并统一应用 Full-AOD 转场、透明度、缩放、复位和防烧屏位移。

模式副本作为 `aod_root_view` 的子视图，自然继承父容器的原生动画。它不单独加入 `animationViews`，避免父子同时被设置 translation 或 scale 而产生双重变换。

## 4. 总体架构

锁屏和 Full-AOD 使用两个视图实例，但共享状态解析、样式创建和生命周期协调。

```text
Settings.Global 配置
        │
        ▼
ModeDisplayStateReader
        │  ModeDisplayState / 无活动模式
        ▼
ModeDisplayCoordinator（SystemUI 进程）
        ├── 锁屏实例 → keyguard_indication_area
        └── Full-AOD 实例 → SystemUI aod_root_view
                               │
                               └── 继承原生 animationViews 行为
```

### 4.1 `ModeDisplayStateReader`

职责：

- 读取 `Settings.Global["pixel_routines_full_config"]`；
- 解析 `activeModeId` 及对应模式；
- 解析用于显示的图标资源名和模式名称；
- 输出 `ModeDisplayState(iconResName, name)`，或无活动模式状态。

该单元不持有 View，不管理广播或生命周期，可作为纯逻辑测试。

### 4.2 `ModeDisplayViewFactory`

职责：

- 创建统一的横向图标+名称视图；
- 统一锁屏和 Full-AOD 的图标尺寸、字体、字号、间距和颜色；
- 从模块包加载 drawable；
- 根据 `ModeDisplayState` 更新现有视图。

限制：

- 不强制设置宿主 alpha、scale、translation；
- 不覆盖原生 Full-AOD 动画属性；
- 图标加载失败时隐藏图标但保留名称。

### 4.3 `ModeDisplayCoordinator`

运行于 SystemUI 进程，负责：

- 保存锁屏模式视图和 Full-AOD 模式视图的弱引用；
- 只注册一次模式状态广播；
- 在模式变化时统一刷新两个实例；
- 在 Full-AOD 开始和结束时创建、定位及清理 AOD 副本；
- 缓存最后一次有效的锁屏屏幕坐标；
- 保证重复回调和 Keyguard 重建时不会产生重复视图。

## 5. Full-AOD 宿主与原生机制

### 5.1 宿主

Full-AOD 模式副本加入 SystemUI 的 `com.android.keyguard.widget.AodView`，即 `R.id.aod_root_view`。

该宿主：

- 是 SystemUI 顶层 AOD 容器；
- 在 Doze 开始时由 SystemUI 设置为可见；
- 在停止 Doze 时由 SystemUI 隐藏；
- 已由 `KeyguardPanelViewSection` 加入 `KeyguardPanelViewController.animationViews`。

### 5.2 原生动画继承

HyperOS 会对 `animationViews` 应用：

- Full-AOD 进入/退出 Folme 动画；
- alpha、scale 和 translation 变化；
- `FullAodStateListener.onPositionChanged()` 产生的防烧屏垂直位移；
- 唤醒时的动画状态复位。

模式副本不注册自己的 `FullAodStateListener`，也不创建定时器。它通过 `aod_root_view` 继承上述行为。

### 5.3 Full-AOD 判定

Hook `MiuiDozeService.onDreamingStarted()` 生命周期，并读取系统已经写入 `aodView` 的 Full-AOD tag：

```text
aodView.getTag(aodView.id) == true
```

只有原生 Full-AOD 标记为真时才创建或显示模式副本。普通 Doze 或普通黑底 AOD 不注入。

`onDreamingStopped()` 用于结束本轮生命周期并清理引用和视图。

## 6. 位置连续性

产品目标是 Full-AOD 中沿用锁屏模式信息的位置，而不是硬编码底边距。

进入 Full-AOD 前后，协调器执行：

1. 获取锁屏模式视图的屏幕坐标和尺寸；
2. 获取 `aod_root_view` 的屏幕坐标；
3. 计算 AOD 宿主中的相对坐标：

```text
relativeX = lockscreenScreenX - aodRootScreenX
relativeY = lockscreenScreenY - aodRootScreenY
```

4. 把 Full-AOD 副本放到该相对位置；
5. 后续位移、缩放和透明度均由 `aod_root_view` 的原生动画驱动。

坐标规则：

- 锁屏模式视图每次布局完成后更新最后有效坐标；
- 如果 Full-AOD 开始时视图尚未布局，在下一次 pre-draw 重试；
- 如果锁屏视图已被隐藏但有最后有效坐标，则使用缓存；
- 如果从未取得有效坐标，本轮不显示 Full-AOD 副本，不回退到猜测的固定位置；
- 坐标和尺寸必须位于 AOD 宿主有效边界内，否则视为无效。

## 7. 状态与生命周期

### 7.1 有活动模式

- 锁屏：显示模式图标和名称；
- Full-AOD：在相同屏幕位置显示同一内容；
- 息屏期间模式切换：广播刷新 Full-AOD 副本；
- 原生 Full-AOD 防烧屏移动：通过父宿主自动继承。

### 7.2 无活动模式

- 锁屏和 Full-AOD 实例均设为 `GONE`；
- 如果模式在 Full-AOD 期间停用，副本保留到本轮 Dream 生命周期结束，但内容立即隐藏；
- `onDreamingStopped()` 统一移除副本。

### 7.3 重复和重建

- Full-AOD 副本使用固定 tag；
- 每次开始时先查找并复用或移除旧实例；
- 重复 `onDreamingStarted()` 不得增加第二个实例；
- Keyguard 或 AOD 宿主重建后，旧弱引用失效时重新注入；
- 广播接收器只注册一次，生命周期与 SystemUI 进程一致。

## 8. 转场原则

不再由 HyperModes 自己执行固定 400ms 的 Doze 淡出/淡入。

转场遵循以下原则：

- Full-AOD 副本在原生 AOD 宿主开始转场前准备并定位；
- 锁屏底部区域的淡出由 HyperOS 原生 Full-AOD 流程执行；
- `aod_root_view` 的显示、alpha、scale 和 translation 由原生流程执行；
- HyperModes 不强制 View alpha、visibility、scale 或 translation 来对抗系统状态；
- 唤醒时由原生宿主退出动画完成后清理副本。

## 9. 旧实现清理

由于本次明确只支持 Full-AOD：

- 停用并删除 `AodPluginHook` 的普通 AOD 注入路径；
- `XposedInit` 不再为 `com.miui.aod` 安装该 Hook；
- 删除 `com.miui.aod` 的模块静态作用域；
- 删除仅服务于普通 AOD 插件 classloader 探测的逻辑；
- 锁屏 Hook 与 Full-AOD 协调逻辑都保留在 `com.android.systemui` 作用域中。

## 10. 容错与诊断

所有反射和 Hook 使用保护模式，任何失败不得阻断原始 SystemUI 调用。

分阶段日志至少包括：

1. SystemUI Hook 是否安装成功；
2. `onDreamingStarted()` / `onDreamingStopped()` 是否触发；
3. 原生 Full-AOD tag 值；
4. 是否取得 `aod_root_view`；
5. 锁屏坐标、AOD 根坐标及换算结果；
6. 当前模式是否解析成功；
7. Full-AOD 副本是否创建、复用、刷新、隐藏或移除。

失败行为：

- 目标类/方法/字段缺失：记录日志并跳过 Full-AOD 功能；
- 配置解析失败：隐藏模式内容；
- drawable 加载失败：隐藏图标但显示名称；
- 坐标不可用：本轮不显示 AOD 副本；
- 宿主已销毁：清理引用，等待下一轮生命周期重建。

## 11. 测试策略

### 11.1 单元测试

`ModeDisplayStateReader`：

- 活动模式存在；
- 无活动模式；
- `activeModeId` 不匹配任何模式；
- malformed JSON；
- 图标映射存在和缺失。

坐标换算：

- 锁屏与 AOD 宿主原点不同；
- 坐标落在有效边界内；
- 负坐标、零尺寸和越界拒绝；
- 使用最后一次有效坐标；
- 无历史坐标时不显示。

### 11.2 Robolectric/协调器测试

- Full-AOD tag 为真时注入；
- tag 为假时不注入；
- 重复开始只存在一个 tagged view；
- 停止后正确移除；
- 广播同时刷新锁屏和 AOD；
- 无活动模式时两处隐藏；
- 宿主重建后可以重新注入；
- 不直接改写宿主或模式副本的动画属性。

### 11.3 真机集成验证

执行至少两轮“亮屏锁屏 → Full-AOD → 唤醒”：

1. 锁屏图标和名称正常；
2. 转入 Full-AOD 时位置无明显跳变；
3. Full-AOD 全程可见；
4. 模式内容与原生 AOD 宿主一起过渡和缩放；
5. 系统防烧屏位置变化自然传递，不出现双倍位移；
6. 唤醒后锁屏内容恢复且无重复实例；
7. Full-AOD 期间切换和停用模式能立即刷新；
8. 多次息屏/唤醒后无残留、重复 View 或异常日志。

普通黑底 AOD 不作为验收项。

## 12. 验收标准

- Full-AOD 中显示当前模式的图标和名称；
- 内容与锁屏位置连续；
- 全程显示且跟随 HyperOS 原生 Full-AOD 动画和防烧屏位移；
- 不再依赖透明的 miuiaod `AODView`；
- 不自建动画定时器或并行的位移监听体系；
- 无活动模式时不显示；
- 重复生命周期不会产生重复视图；
- Hook 失败不会导致 SystemUI 崩溃；
- 锁屏现有显示功能保持正常。
