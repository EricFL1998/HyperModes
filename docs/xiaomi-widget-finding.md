# 小米小部件（非安卓小组件）写入调研

目标：研究 HyperModes 如何把内容写进**小米小部件**（负一屏 / 小部件中心的
MIUI Widget，区别于普通安卓 AppWidget）。本文只做调研，不涉及代码改动。

结论先行：**对第三方开发者而言，"小米小部件"本质上就是"带 `miuiWidget`
标识的 Android AppWidgetProvider"**，没有独立于 AppWidgetProvider 的另一套
写入体系。唯一例外是 MAML 卡片（小米原生小组件，如时钟/天气/相册），它走
小米服务端下发，第三方无法自行注册。

解包来源：`apk_decompiled/miuihome_decompiler/`（小米桌面，负责桌面/负一屏
托管小部件）。官方文档来自小米开放平台《HyperOS小部件设计规范》与《小部件
技术规范与系统能力说明》。

---

## 1. 官方规范要点（来自小米开放平台）

### 1.1 `miuiWidget` 标识

`AppWidgetProvider` 的 receiver 里必须带：

```xml
<receiver android:name=".WidgetProvider">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/widget_info" />
    <meta-data
        android:name="miuiWidget"
        android:value="true" />
</receiver>
```

- 带 `miuiWidget=true` 才享受小米能力（小部件中心上架、曝光刷新、调起详情页
  添加等）；否则降级为普通安卓 widget。
- **禁止修改 receiver 类名**，审核通过后也不得随意移除 `miuiWidget` 标识。

### 1.2 独立进程 `:widgetProvider`

- receiver / service / provider 必须运行在 `:widgetProvider` 独立进程。
- 该进程内存占用要求 **< 35M**，且**不得拉起主进程**（不能绑定主进程服务、
  不能调主进程 ContentProvider）。
- **Activity 不能放在 `:widgetProvider` 进程**。

### 1.3 曝光刷新（替代系统定时刷新）

去掉系统自带定时刷新，改为：

```xml
<meta-data android:name="miuiWidgetRefresh" android:value="exposure" />
<meta-data android:name="miuiWidgetRefreshMinInterval" android:value="10000" />
```

- 曝光刷新 = 小部件进入可视区域时刷新，最短间隔 10s。
- 刷新的 action 是 `miui.appwidget.action.APPWIDGET_UPDATE`（不是系统标准
  action）。

### 1.4 尺寸规范

| 规格 | minWidth | minHeight |
|---|---|---|
| 2×2 | 110 dp | 110 dp |
| 4×2 | 300 dp | 110 dp |
| 4×4 | 300 dp | 250 dp |

### 1.5 布局规范

- 根布局必须带 `android:id="@android:id/background"`，且**必须有非全透明的
  背景色**（纯透明会被判定不合规）。
- 根布局宽高 `match_parent`，内容居中。
- **深色模式只能 XML 静态适配**（`drawable-night` / `values-night`），
  不支持 RemoteViews 代码动态切换。

### 1.6 小部件版本号

```xml
<meta-data
    android:name="miuiWidgetVersion"
    android:value="1" />
```

- 放在 `<application>` 下，**每次更新必须递增**。

### 1.7 调起详情页引导添加

```java
Intent extras = new Intent();
extras.putExtra("addType", "appWidgetDetail");
extras.putExtra("widgetName", "包名/xxxProvider");
appWidgetManager.requestPinAppWidget(provider, extras, null);
```

- 仅 Android 8.0+ 支持。
- 这是系统级引导（pin 动画），不经过小部件中心审核也能用，但只出现在
  **原生安卓 widget 添加流程**里，不会出现在小部件中心的精选列表。

### 1.8 审核 / 上架

- 小部件独立于应用商店审核，需发邮件 `miui-widget@xiaomi.com` 联系小米走
  小部件测试流程。
- 审核通过后才能进入小部件中心（精选区），享受曝光刷新等小米能力。

参考文档：

- 《HyperOS小部件设计规范》：https://dev.mi.com/xiaomihyperos/documentation/detail?pId=1664
- 《小部件技术规范与系统能力说明》：https://dev.mi.com/xiaomihyperos/documentation/detail?pId=1584
- 《小部件提交审核与上传操作指南》：https://dev.mi.com/xiaomihyperos/documentation/detail?pId=1588
- 《Q&A》：https://dev.mi.com/xiaomihyperos/documentation/detail?pId=1591

---

## 2. 反编译证据：小米桌面如何托管小部件

`apk_decompiled/miuihome_decompiler/sources/com/miui/miuiwidget/servicedelivery/`：

### 2.1 `appwidget/WidgetController.java`

按 `type` 分发：

```java
type == 1  // APP_WIDGET    → AppWidgetFactory（AppWidgetHost 托管标准安卓 widget）
type == 2  // MAML_WIDGET   → MamlWidgetFactory（resPath 指向本地 MAML 文件）
```

### 2.2 `model/WidgetItem.java` — Type 常量

```java
APP_WIDGET      = 1
MAML_WIDGET     = 2
PENDING_WIDGET  = 3
STACK_WIDGET    = 4
DELIVERY_WIDGET = 5
```

### 2.3 `model/MamlWidgetItem.java`

MAML 卡片（小米原生小组件）字段：`productId`、`downloadUrl`、`resPath`、
`layoutStyle("2x2")`、`spanX/spanY`、`versionCode` —— **全部来自小米服务端
下发**，第三方 App 没有注册入口。

### 2.4 `view/ServiceDeliveryLayoutController.java`

负一屏服务卡片用 `AppWidgetManager.getInstalledProviders()` 匹配 Provider ——
说明负一屏的小部件也走标准 AppWidget 绑定链路。

### 2.5 `launcher/BaseLauncher.java` / `WidgetUtils.java`

- `getAppWidgetHost()` / `getAppWidgetManager()`：桌面通过 AppWidgetHost
  渲染 widget 视图。
- `WidgetUtils` 用 `bindAppWidgetIdIfAllowed` 绑定 widget id。

### 2.6 结论

小米小部件中心 = **审核过的 AppWidgetProvider + 小米 meta-data**。MAML 卡片
只对小米自家 / 签约开发者开放，第三方无法直接写入桌面。

---

## 3. HyperModes 的可行路径

### 3.0 关键验证：小部件选择界面不按"是否过审"过滤

用户目标：**不上架、装上 App 后，桌面"添加小部件"界面直接能看到**。

反编译 `miuihome_decompiler/sources/com/miui/home/launcher/widget/
BaseWidgetsVerticalAdapter.java`（小部件选择列表适配器，`initAllItems`）：

```java
List<AppWidgetProviderInfo> installedProvidersForAllUser =
    Utilities.getInstalledProvidersForAllUser(mContext, mAppWidgetManager);
// 仅过滤：
// 1) minWidth<=0 && minHeight<=0（无尺寸/无预览图）→ 移除
// 2) sDisabledComponents（硬编码列表，只有
//    "com.android.alarmclock.AnalogAppWidgetProvider" 一个已知坏组件）→ 移除
buildAppWidgetsItems(installedProvidersForAllUser, mAllItems);
```

结论：

- 选择列表用标准 `AppWidgetManager.getInstalledProviders()`（全用户）枚举
  **所有已安装的 AppWidgetProvider**，**不按 miuiWidget 标识、不按小部件
  中心审核状态过滤**。
- `sDisabledComponents` 是桌面硬编码的黑名单（只含一个系统闹钟组件），
  与"是否过审"无关。
- `MIUIWidgetUtil.isMIUIWidget()` 只影响行为（拖到负一屏、过渡动画等），
  不影响列表展示。
- 因此：**只要在 manifest 声明一个尺寸合法（minWidth/minHeight > 0，
  最好带 previewImage）的普通 AppWidgetProvider，装完 App 立刻就会出现在
  小米桌面"添加小部件"列表里，不需要任何审核**。
- 差异仅在展示位置：过审的（带 miuiWidget + 小米侧通过）出现在"小米小部件"
  精选区；未过审的出现在 App 小部件/安卓小部件分类里，且没有曝光刷新、
  小部件中心运营位等小米能力，但**基本功能完整可用**。

### 路径 A：官方正道（推荐长期方案）

1. HyperModes 声明一个 `AppWidgetProvider`（`:widgetProvider` 进程），
   布局展示当前模式状态 / 快捷切换按钮。
2. 加 `miuiWidget=true` + 曝光刷新 + 尺寸/布局合规。
3. 发邮件 `miui-widget@xiaomi.com` 走小部件审核，通过后进小部件中心。

特点：正规、可见于小部件中心、有曝光刷新；但**审核周期不确定**，且
HyperModes 是 LSPosed 模块（可能涉及 Xposed API），审核兼容性未知。

### 路径 B：原生 widget 直接上（不审核，自用 / 测试）

- 不声明 `miuiWidget`，或声明了但不过审。
- 通过 `requestPinAppWidget`（带 `addType="appWidgetDetail"` +
  `widgetName`）或桌面"添加小部件"列表直接添加。
- 特点：**无需审核立刻可用**，只缺少小部件中心精选位与曝光刷新；刷新需
  自己维护（`miui.appwidget.action.APPWIDGET_UPDATE` 或系统定时刷新）。
- 这是落地原型最快的一步。

### 路径 C：LSPosed hook 注入（绕过 widget 体系）

- 类似现有 Focus 卡片的 hook 路线，直接在桌面/负一屏进程里注入自定义卡片。
- 特点：不受审核与 widget 规范约束、表现力最强；但依赖桌面版本（MIUI Home
  每个大版本都会变），维护成本高。

### 与 HyperModes 架构的冲突点

- HyperModes 是零进程架构：核心逻辑在 `system_server`，App 只是配置编辑器，
  README 卖点是"无任何第三方后台服务常驻"。
- 小米规范要求 widget 必须跑在 `:widgetProvider` 独立进程，**进程里不能
  绑定主进程服务、不能调主进程 ContentProvider** —— 意味着 widget 进程只能
  做展示 + 定时/曝光刷新，真正的模式切换逻辑仍由 system_server 完成
  （widget 通过广播 / ContentProvider 跨进程通知 system_server）。
- 因此引入 widget 后，README 的"无后台进程"表述需要改为"无常驻后台服务，
  仅按需唤起小部件进程"。

---

## 4. 建议的下一步

1. 确认走哪条路线（A 官方审核 / B 原生 widget / C hook 注入，或 B→A 渐进）。
2. 若落地：先做路径 B 原型 —— 一个展示当前模式状态的 2×2
   `AppWidgetProvider`（`:widgetProvider` 进程），在设置里加
   "添加到桌面"按钮调 `requestPinAppWidget`，验证引导添加与渲染。
3. 原型通过后再评估是否需要 `miuiWidget` 标识 + 曝光刷新 + 小部件中心审核。
