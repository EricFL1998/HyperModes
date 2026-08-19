# OS4 控制中心渲染路径研究(2026-08-19)

## 结论

**HyperOS 4 的新版控制中心 UI 仍然由 miui.systemui.plugin 插件 APK 渲染**,架构与 OS3 一致。
此前"OS4 删除了 QSController / QSCardsController、控制中心内置到 SystemUI"的判断是错误的——
那些类只是不在 SystemUI APK 里,而在插件 APK 里(本次研究没有拉取 OS4 插件 APK,导致误判)。

## 证据链

0. **OS4 SystemUI APK 里没有插件实现(dexdump 全量验证)**
   对 OS4 MiuiSystemUI.apk 的 classes1/2/3.dex 做完整 dexdump,以下标记全部零命中:
   `getCardStyleTileSpecs`、`MainPanelContentDistributor`、`QSCardsController`、
   `miui/systemui/controlcenter/qs/QSController`、`card_style_tiles`。
   同时 `PLUGIN_MIUI_CONTROL_CENTER` 只在 classes2.dex 出现一次(接口常量自身),
   SystemUI 的 AndroidManifest 也没有声明该 action 的 service。
   ⇒ `os4_android17_apks/README.md` 中"com.miui.systemui.plugin 已并入 SystemUI APK"的结论不成立,
   插件仍是设备上的独立包,只是 2026-08-14 备份时没拉取。
   补拉方法(设备连接后):
   ```powershell
   adb shell pm path miui.systemui.plugin        # 或 com.miui.systemui.plugin
   adb shell cmd package query-services -a com.android.systemui.action.PLUGIN_MIUI_CONTROL_CENTER
   ```

   已勘误:README 该行结论错误,插件必须单独拉取。

1. **MiuiQSPanel 在新版控制中心下完全不工作**
   `MiuiQSPanel.setTiles(Collection)` 源码(jadx 反编译):
   ```java
   public void setTiles(Collection<QSTile> collection) {
       if (useControlCenter.getValue()) {
           return;   // 新版控制中心开启时直接返回,不渲染任何 tile
       }
       setTiles(collection, false);
   }
   ```
   因此 MiuiQSPanel / MiuiTileLayout / MiuiPagedTileLayout(小图标分页网格)只服务旧版样式。
   MiuiTileLayout 是纯均分网格(所有 tile 都是 mCellWidth×mCellHeight),本身没有 1x2 概念。

2. **新版控制中心内容来自插件**
   `com.miui.systemui.controlcenter.container.ControlCenterContentController`:
   ```java
   ControlCenterContent content =
       (!this.useControlCenter || this.plugin == null) ? null
           : this.plugin.getControlCenterContent();
   ```
   插件通过 `ControlCenterImpl` 注册:
   ```java
   pluginManager.addPluginListener(contentController, ControlCenterPlugin.class, true);
   ```

3. **OS4 的插件加载机制仍在**
   `com.android.systemui.shared.plugins.PluginInstance`(OS4 dexdump 核实):
   - `loadPlugin()` 存在,可 hook;
   - 字段 `packageName`(String)、`pluginData`(PluginData{plugin, context});
   - OS3 时代的 `getPackage()`/`getPluginContext()` 已改名,取 ClassLoader 用
     `pluginData.plugin.javaClass.classLoader` 最稳。

4. **大/小 tile 的判定在插件里,机制与 OS3 相同**
   OS3 插件 `miui.systemui.controlcenter.qs.QSController`:
   - `getCardStyleTileSpecs()` → 懒加载资源数组 `R.array.card_style_tiles_mobile/wifi`,
     返回"卡片式(1x2 大 tile)"spec 列表;
   - 组装完整 tile 列表时 cardStyle specs 在前(渲染成大卡片),其余进小图标区;
   - `QSCardsController.preparePanelUpdate()` + `QSRecord.setShrinkCardStyle(false)` 控制卡片横向尺寸;
   - `MainPanelContentDistributor.distributePanels(boolean)` 负责把内容插入主面板。

5. **1x2 渲染的完整数据链(OS3 反编译核实,OS4 插件同架构)**
   OS3 插件 `QSCardsController.createCardTiles()`:
   ```java
   List<String> cardStyleTileSpecs = qsController.getCardStyleTileSpecs();  // ← hook 追加点
   for (String str : cardStyleTileSpecs) {
       QSTile tile = qsController.createTile(str);      // → host.getQsFactories()
   }
   recordFactory.create(tile, true);                    // 第二参 true = cardStyle(1x2)
   ```
   `createTile` 内部走 `host.getQsFactories()` → OS4 SystemUI 的
   `MiuiQSHostAdapter.getQsFactories()` 返回 `[MiuiQSFactory]` ——
   即我们已 hook 的 `MiuiQSFactory.createTile(String)`。
   因此:
   - 插件侧 **无需重复 hook createTile**(SystemUI 侧 hook 已覆盖,这就是 tile 目前能出现的原因);
   - 只要 `getCardStyleTileSpecs()` 包含 `hypermodes_focus`,createCardTiles 就会主动创建
     Focus tile 并以 cardStyle=true 记录 → 1x2 大卡片;
   - `getCreateStartExcludeTileSpecs()` 内部同样调 `getCardStyleTileSpecs()`,
     会把 focus 从普通小图标流排除,不会出现"一大一小"两份。

   SystemUI 侧的注入(`CurrentTilesInteractor.addTile`)与插件侧 hook 形成双保险:
   前者保证 tile 进入 pipeline 持久化列表,后者保证卡片式渲染。

## 之前方案失败的原因

  tile 能出现但一直是 1x1 小图标:
  tile 对象由 SystemUI 侧管线创建(`MiuiQSFactory.createTile` hook + `CurrentTilesInteractor.addTile`),
  插件 UI 消费同一管线,所以能显示;
  但 `hypermodes_focus` 不在插件的 cardStyleTileSpecs 里 → 渲染成小图标。
  对 SystemUI 内 Compose 管线(DefaultLargeTilesRepository / IconTilesInteractor / SizedTileImpl)的
  hook 全部无效——那条路径不渲染新版控制中心。

## 本次代码修改

1. `XposedInit.hookPluginLoading()`:hook OS4 `PluginInstance.loadPlugin()`,
   匹配 miui.systemui.plugin 包名后用 `plugin.javaClass.classLoader` 安装插件侧 hook
   (安装前先用 `Class.forName(QSController)` 做能力校验)。
2. `ControlCenterCardHook.installPluginHooks()`:恢复 OS3 插件侧 hook——
   - `QSController.getCardStyleTileSpecs()` 追加 `hypermodes_focus`(升为 1x2 卡片);
   - `QSCardsController.preparePanelUpdate()` 后对 Focus 卡片 `setShrinkCardStyle(false)`;
   - 列表过滤 + `distributePanels` 尾部插入(全部防御式校验,OS4 插件签名变了会降级并打日志)。
3. 保留 SystemUI 侧原有注入(tile 对象创建 + 持久化 + 详情面板 hook)。

## 待真机验证

## 静态验证补充(生命周期与时序)

1. **tile 重复创建安全**:`QSCardsController.createCardTiles()` 可被反复调用,每次都新建 Focus tile
   代理。模块侧 `FocusCardTileProvider.create()` 每次生成独立的 proxy + handler + store,
   旧实例被替换后整条引用链(proxy → store → observer)一并不可达,可被 GC,`destroy()` 分支完整
   (清回调 / 关 observer / 销毁 session)。无泄漏、无跨实例状态污染。
2. **hook 安装时序**:`PluginInstance.loadPlugin()` 由 `ControlCenterImpl.start()` →
   `addPluginListener` → `PluginActionManager.loadAll()`(后台 executor)触发,晚于 SystemUI 进程
   attach;而模块在 `onPackageReady("com.android.systemui")` 即装好 loadPlugin hook,
   必然先于首次插件加载,不存在错过首次加载的窗口。
3. **插件重载**:包更新/禁启用会走 unload → 新 PluginInstance(新 ClassLoader)→ loadPlugin;
   能力检查重跑,`markInstalling` 按 ClassLoader 身份去重,旧 hook 随旧 loader 失效,新 loader 装新 hook。
4. **AutoAdd 无冲突**(已验证 OS4 源码):SystemUI 侧 `AutoAddInteractor` 是独立信号流,
   不消费插件的 cardStyle 排除列表,两侧 hook 互不干扰。
5. **小图标区去重(hook getter 而非依赖字段)**
   OS3 插件 `QSListController:628` 通过 `getQsListExcludeTileSpecs()`(getter)过滤小图标区:
   `if (!getQsListExcludeTileSpecs().contains(spec)) { 渲染为小 tile }`。
   但排除字段在 `QSController` 构造器里初始化,而构造发生在 `PluginInstance.loadPlugin()`
   内部的 `onPluginLoaded` 回调 —— **早于模块 after-proceed hook 安装**,字段快照不含 focus,
   SystemUI 侧持久化的 focus 会再渲染一份 1x1(与 1x2 卡片重复)。
   修复:直接 hook `getQsListExcludeTileSpecs()` getter,无条件在返回列表追加 focus;
   QSListController:628 与 TileQueryHelper(编辑模式,259/459 两处)都走此 getter,
   一处 hook 覆盖渲染与编辑两条路径。getter 每次调用都经过 hook,不受构造器字段快照影响。
   (相应地 `installPluginHooks()` 里新增 `hookQsListExcludeSpecs()`。)
   补充:TileQueryHelper:459 实际读的是 `getQsListStartExcludeTileSpecs()`(另一个 getter,
   返回 qsListStartExcludeTileSpecs 字段),渲染路径 QSListController:628 与
   TileQueryHelper:259 读 `getQsListExcludeTileSpecs()`。两个 getter 都已 hook。

## OS4 插件 APK 签名核验(2026-08-19,真机拉取)

设备已连接(65eca5c8),OS4 插件位于 `/product/app/MIUISystemUIPlugin/MIUISystemUIPlugin.apk`
(26.3 MB,jadx 反编译到 `Temp/os4_plugin_decompiler`)。
service 声明:`miui.systemui.controlcenter.MiuiControlCenter`,action
`com.android.systemui.action.PLUGIN_MIUI_CONTROL_CENTER`,包名 `miui.systemui.plugin`。

签名核验结果(全部与 OS3 一致):
- `QSController.createTile(String)` ✓
- `QSController.getCardStyleTileSpecs()` ✓
- `QSController.getQsListExcludeTileSpecs()` ✓
- `QSController.getQsListStartExcludeTileSpecs()` ✓
- `QSController.getTile(String)` ✓
- `QSCardsController.preparePanelUpdate()` / `getListItems()` ✓
- `QSListController.getListItems()` ✓(排除判定在 :629)
- `MainPanelContentDistributor.distributePanels(boolean)`、`rightPanelContent`、
  `rightFooterSpace`、`childControllers` ✓
- `QSRecord.setShrinkCardStyle(boolean)` / `getShrinkCardStyle()` / `getTileView()` ✓
- `MainPanelContent` 接口(getListItems/available/createViewHolder/getRightOrLeft/getPriority/moveElement)✓
- `SecondaryParamsKt.from(DetailAdapter)` / `DetailPanelParams.getUseSpecificHeight()` /
  `DetailPanelDelegate.onHidden()` ✓
- `MiuiControlCenter implements ControlCenterPlugin`,`getControlCenterContent()` ✓

唯一签名变化:`QSCardItemView.updateBackground(boolean)` → `updateBackground(boolean, boolean)`。
`applyFocusCardSizing()` 已兼容(`resolveUpdateBackgroundMethod` 先试 1 参再试 2 参,
`invokeUpdateBackground` 按形参数调用)。

## 时序修正:createClassLoader 作为主 hook 点(2026-08-19)

原方案 hook `PluginInstance.loadPlugin()` after-proceed,但 OS4 的 `loadPlugin` 内部在
proceed 里就同步完成 `Plugin.onCreate` 与 `ControlCenterContentController` 的
`getControlCenterContent()` → `createView()` → `ControlCenterWindowViewImpl.onFinishInflate`
→ `windowViewController.init()`,此时 `QSCardsController.onCreate()` → `createCardTiles()`
已经跑过一遍未 hook 的 `getCardStyleTileSpecs()`。

修正:主 hook 点前移到 `PluginInstance$PluginFactory.createClassLoader()`——
它在 `loadPlugin → createPlugin` 内部调用,严格早于 `Plugin.onCreate` 与控制中心构建,
保证 `createCardTiles` 首次执行即读到追加后的 cardStyleTileSpecs。
`PluginInstance.loadPlugin()` 保留为兜底(仅当 createClassLoader hook 失败才启用)。

- 设备连接后安装,重启 SystemUI,看日志:
  `plugin loaded: package=...` / `OS4 plugin control center hooks installed`;
- 打开控制中心,Focus 卡片应显示为 1x2;
- 若日志显示 tail feature set / sizing 不可用,拉取 OS4 插件 APK 反编译核对签名:
  `adb shell pm path miui.systemui.plugin`(或 com.miui.systemui.plugin)。
