# HyperOS Native Focus Detail Panel Design

## Summary

HyperModes will make the Focus card detail panel participate in the same private HyperOS pipeline as the built-in Wi-Fi and Bluetooth detail panels:

```text
QSTile
  -> DetailAdapter
  -> QSDetailContent
  -> native miuix RecyclerView
  -> DetailPanelDelegate
  -> SecondaryPanelRouter
```

The normal path will use the actual SystemUI `QSDetailContent`, `SelectableItem`, RecyclerView adapter, item holders, grouped backgrounds, blur, scrollbars, accessibility behavior, and secondary-panel animations. HyperModes will not recreate their appearance with a custom list.

Three narrowly scoped compatibility extensions are required because HyperOS does not know the private `hypermodes_focus` tile:

1. refresh the native item array when HyperModes configuration changes;
2. allow HyperModes `QSDetailContent` to render more than the built-in 20-item cap;
3. map the private Focus detail adapter back to the Focus card and the standard long-list panel height.

Every extension must be identity-scoped to HyperModes-owned objects and fail closed to the original HyperOS behavior.

This design supersedes the detail-panel portions of `2026-07-25-hyperos-focus-card-design.md`. The existing card placement and card sizing design remains unchanged.

## User-Confirmed Requirements

- Active-mode changes (from app, trigger, or another card click) must update the detail list's selected state while it remains open.
- Reopening the detail panel must always show the latest configuration without restarting SystemUI.
- The list must use the official HyperOS detail component rather than a visually similar custom implementation.
- The list must show every configured mode, including more than the 20 items normally exposed by `QSDetailContent.Adapter`.
- Panel height and the transition from fitting content to scrolling content must follow HyperOS's native detail-panel behavior rather than a custom eight-row threshold.
- Selecting a mode and closing through gesture/back must not leave Control Center partially collapsed or touch-blocked.
- Wi-Fi, Bluetooth, cellular, cooling-fan, and all other system detail panels must retain their original behavior, including their original item caps.

## Non-Goals

- Do not replace the native RecyclerView adapter.
- Do not subclass or copy `QSDetailContent`.
- Do not implement a new visual design or custom row component for the normal path.
- Do not hard-code an eight-row height.
- Do not modify mode creation limits in the HyperModes app.
- Do not change Focus card placement, span sizing, or tail distribution as part of this work.
- Do not directly call `SecondaryPanelRouter` from the Focus tile.
- Do not alter built-in detail panels to remove their 20-item cap.

## Verified HyperOS Behavior

### Native content

`QSDetailContent` inflates a `miuix.recyclerview.widget.RecyclerView` with a vertical scrollbar. Its native adapter creates HyperOS `SelectableItemHolder` rows and owns grouping, item backgrounds, accessibility delegates, touch behavior, and update dispatch.

Relevant sources:

- `apk_decompiled/miuisystemui_decompiler/resources/res/layout/qs_detail_content.xml`
- `apk_decompiled/miuisystemui_decompiler/resources/res/layout/qs_detail_item_selectable.xml`
- `apk_decompiled/miuisystemui_decompiler/sources/com/android/systemui/qs/QSDetailContent.java`

`QSDetailContent.setItems(Item...)` accepts the full array. The truncation is private adapter policy: `QSDetailContent.Adapter.getItemCount()` returns `min(items.length, 20)`.

`QSDetailContent.setDetailShowing(false)` clears its callback and removes pending item/callback messages. A cached content view must therefore be rebound on every `createDetailView()` call.

### Official live-update pattern

Built-in Wi-Fi and Bluetooth detail adapters retain their `QSDetailContent` instance and expose an `updateItems()` operation. Their controller callbacks invoke `updateItems()` while detail is showing, and that operation pushes a new native item array through `setItems()`.

HyperModes will follow this same ownership pattern. It will not depend on `DetailPanelDelegate.notifyDataSetChanged()`, because that method only rebinds the current array and cannot change its length or contents.

### Panel routing and sizing

`QSController.handleShowDetail()` calls `tile.setDetailListening(show)` and delegates opening/closing to `SecondaryPanelRouter`. The built-in long-list specs set `DetailPanelParams.useSpecificHeight`, causing `DetailPanelController` to use `CommonUtils.getControlCenterDetailMaxHeight()` and allowing the inner native RecyclerView to scroll within a bounded viewport.

HyperModes currently has a private spec and metrics category that are absent from HyperOS's hard-coded mappings. As a result, it follows the generic `wrap_content` path and cannot be resolved back to the Focus card for the normal card-to-detail animation.

### Hidden lifecycle

`DetailPanelDelegate.onHidden()` performs the authoritative end-of-detail sequence:

1. native detail content receives `setDetailShowing(false)`;
2. the `DetailCallback` tells the tile to stop detail listening;
3. the delegate clears its current adapter reference.

Configuration or tile-view updates must not be injected into the panel routing animation before this sequence has completed.

## Architecture

### 1. `FocusNativeDetailRegistry`

Add a process-local registry shared by the Focus detail adapter and the Control Center compatibility hooks.

It will maintain weak identity associations for:

- HyperModes `DetailAdapter` proxy -> detail session;
- HyperModes native `QSDetailContent` instance -> detail session.

The registry must use identity semantics, not user-defined `equals()` or `hashCode()`. Entries must not keep SystemUI views, adapters, contexts, or plugin ClassLoaders alive. Destroying the tile/session explicitly unregisters its adapter and content; weak cleanup remains a secondary safeguard.

The registry provides narrow predicates and callbacks:

```text
isFocusAdapter(adapter)
isFocusContent(content)
contentSession(content)
adapterSession(adapter)
notifyPanelHidden(adapter)
```

The constants remain private and stable:

```text
tile spec:       hypermodes_focus
metrics category: 118
content suffix:  HyperModesFocus
```

Identity registration is the primary ownership proof. The suffix and metrics category are secondary validation only; no hook may target an object solely because it returns integer `118` or a matching string.

### 2. `FocusModeDetailSession`

Refactor one-shot native building into a session owned by the cached Focus detail adapter.

The session owns:

- the adapter proxy;
- the reflected `FocusNativeDetailContentApi`;
- a weak reference to the currently bound native content view;
- the native item callback proxy;
- the latest listening/lifecycle state;
- item rebuilding from `FocusCardStateRepository`;
- the standard-view fallback when native APIs are unavailable.

The session exposes a small internal contract:

```text
adapter: Any
bindDetailView(context, convertView, parent): View?
setDetailListening(listening)
refreshItems()
onPanelHidden()
destroy()
```

`FocusCardDetailFactory` will return this handle rather than an unstructured `Any`. `FocusCardTileProvider.getDetailAdapter()` still returns only `handle.adapter` to HyperOS.

### 3. Native bind/rebind

On each native `createDetailView()` call, the session will:

1. call native `QSDetailContent.convertOrInflate(context, convertView, parent)`;
2. verify the returned object is both the resolved content class and an Android `View`;
3. register the content instance in `FocusNativeDetailRegistry` before submitting items;
4. call `setSuffix("HyperModesFocus")` once for that bind/rebind;
5. install a new native `QSDetailContent.Callback`, because HyperOS clears it when hidden;
6. read a fresh repository snapshot;
7. build one native `SelectableItem` per configured mode;
8. call native `setItems()` with the complete array, including arrays longer than 20;
9. store only a weak current-content reference.

Calling `setSuffix()` during bind is intentional and follows the official adapters; it also scrolls a newly opened/reopened panel to the top. Live refresh must not call `setSuffix()`, so adding or removing a mode while the panel is open will not unexpectedly reset the user's scroll position.

### 4. Native rows and empty state

Configured modes use native `QSDetailContent.SelectableItem` objects. HyperModes populates the supported fields already used by the current implementation:

- stable mode identity tag;
- localized title;
- current status/selection semantics;
- content description;
- native selected and force-single flags;
- module-provided drawable;
- no disconnect action or secondary action.

The tag will be represented by a private typed/prefixed HyperModes value rather than an unqualified arbitrary object. The click callback validates this tag before activating a mode.

An empty mode list remains inside native `QSDetailContent`; it must no longer force the normal path into the custom `ScrollView`. The session will:

- configure the native empty container through its standard Android view IDs;
- set the localized empty text and module drawable directly on the native empty views;
- submit an empty native item array;
- expose the normal HyperOS “More settings” footer through `DetailAdapter.getSettingsIntent()` so the user can open HyperModes and create a mode.

The footer intent targets exported `MainActivity` with `FLAG_ACTIVITY_NEW_TASK`. HyperOS remains responsible for rendering and launching the footer.

### 5. Live configuration updates

`GlobalFocusCardConfigStore` remains the single observer source. The detail adapter must not register a second independent ContentObserver.

`FocusCardTileProvider` owns observer lifetime while either of these is true:

- one or more native card listener tokens are active;
- the detail session is `OPEN`.

When the session enters `CLOSING`, it stops consuming configuration changes and releases its observer claim. The native card normally retains its own listener claim while Control Center is visible. If it does not, the provider records one in-memory pending-refresh flag from the selection that initiated closing; it does not keep a ContentObserver alive solely to watch a hidden/closing panel.

The provider unregisters the observer when no claim remains and always unregisters during `destroy()`. Hidden completion never depends on receiving another configuration callback.

On each store change, work runs on the SystemUI main handler and follows the current lifecycle state:

#### Detail open

1. read a fresh repository snapshot;
2. refresh the card state;
3. call `FocusModeDetailSession.refreshItems()`;
4. rebuild and submit the complete native item array through `QSDetailContent.setItems()`.

This matches built-in Wi-Fi/Bluetooth behavior: update the tile and call the detail adapter's item-update operation while detail is showing. The primary use case is reflecting active-mode changes initiated from the app, a scheduled trigger, or another card click while the user is viewing the detail list.

#### Detail closing

- do not submit new detail items;
- do not issue immediate card callbacks that can mutate the source card during routing;
- mark one pending state refresh.

#### Detail closed

- refresh the card immediately when it is listening;
- otherwise retain no UI work; the next `setListening(true)` performs the normal initial refresh.

Repeated observer callbacks may be coalesced into one pending card refresh and one latest-snapshot detail update. The repository remains the authoritative source; the session does not maintain a second mode model.

### 6. Detail lifecycle state

Use an explicit state machine:

```text
CLOSED -> OPEN -> CLOSING -> CLOSED
```

Transitions:

- `setDetailListening(true)` moves to `OPEN`, retains observer ownership, and allows native item refreshes.
- a HyperModes row selection records one pending card refresh, moves to `CLOSING`, releases the detail observer claim, and then sends `onShowDetail(false)`.
- `setDetailListening(false)` moves any still-`OPEN` session to `CLOSING`, releases the detail observer claim, stops item refresh immediately, and performs no synchronous `refreshState()`.
- the scoped `DetailPanelDelegate.onHidden()` completion hook moves the matching session to `CLOSED`, clears the current content reference, and posts the recorded pending card refresh after the original callback returns.
- `destroy()` moves directly to `CLOSED`, clears pending work, unregisters registry entries, and closes the observer.

For a close initiated by back, drag, or whole-panel collapse, `setDetailListening(false)` moves `OPEN` to `CLOSING`; the same native `onHidden()` completion ends it.

If the optional hidden-completion hook is unavailable on an incompatible HyperOS build, the session fails closed: it stops detail updates at `setDetailListening(false)`, relies on the next card-listening refresh, and never calls private routing methods.

### 7. Selection and close order

Mode selection remains terminal and idempotent. The order becomes:

1. validate the native item tag;
2. attempt `repository.activate(modeId)` once;
3. mark the detail session as closing;
4. request close once through the same QSTile callback identity used to open it;
5. let `QSController` and `SecondaryPanelRouter` perform the full native route;
6. refresh the card after the configuration observer or hidden-completion callback reaches a stable lifecycle point.

Remove the current synchronous tile refresh from `FocusModeSelectionController.select()` and from `FocusCardTileProvider.setDetailListening(false)`. They are the two update points most likely to race the native route.

The tile must never invoke `SecondaryPanelRouter.routeToMain()` directly.

## Scoped HyperOS Compatibility Hooks

All hooks use libxposed protective exception mode. They must return the original result whenever validation or reflection fails.

### 1. Full native item count

Hook:

```text
com.android.systemui.qs.QSDetailContent$Adapter.getItemCount()
```

Policy:

1. resolve the synthetic outer `QSDetailContent` reference by exact `this$0` name, with a fallback to the single declared field assignable to the resolved content class;
2. require the outer content identity to be registered to a live Focus detail session;
3. require `getSuffix() == "HyperModesFocus"`;
4. read the native `items` array;
5. return the full array length;
6. otherwise call/retain the original implementation.

The content is registered before its first `setItems()` call, so native `updateGroupInfo()` and RecyclerView notifications see the uncapped count from the start.

This hook changes only the item count. It does not replace the adapter and therefore preserves native item view types, grouping, diffing, accessibility, animations, and recycling.

### 2. Focus adapter to tile-spec mapping

Hook:

```text
miui.systemui.controlcenter.panel.secondary.SecondaryParamsKt.from(DetailAdapter)
```

Policy:

- if and only if the adapter identity is registered as the live HyperModes Focus adapter, return `hypermodes_focus`;
- otherwise retain the original result.

This allows `DetailPanelTilesDelegate` and `DetailPanelAnimator` to resolve the Focus card/fake card just as they resolve built-in Wi-Fi and Bluetooth tiles. It enables the native source-card expansion/collapse animation instead of the generic unmapped path.

Do not impersonate Wi-Fi, Bluetooth, cellular, or cooling-fan metrics categories. Focus keeps category `118`, and unrelated category/type behavior remains untouched. `DetailPanelController` may continue to report `PanelType.INVALIDATE` for Focus: the generic max-height calculation is the same as Wi-Fi/Bluetooth on the target build, while the separate `from(adapter)` mapping supplies the card spec required by the tiles delegate and animator.

### 3. Native long-list height

Hook:

```text
miui.systemui.controlcenter.panel.secondary.DetailPanelParams.getUseSpecificHeight()
```

Policy:

- reflect the params' adapter through its public getter;
- if and only if that adapter identity is registered as the Focus adapter, return `true`;
- otherwise retain the original result.

This uses `DetailPanelController.getSpecificHeight()`, which already delegates to HyperOS `CommonUtils.getControlCenterDetailMaxHeight()`. Content that exceeds the available panel height scrolls in the native RecyclerView. No row count or dp height is hard-coded by HyperModes.

### 4. Native hidden completion

Hook:

```text
miui.systemui.controlcenter.panel.secondary.detail.DetailPanelDelegate.onHidden()
```

Policy:

1. capture the current adapter before proceeding;
2. run the original `onHidden()` in full;
3. if the captured adapter identity belongs to HyperModes, notify its detail session after the original returns;
4. otherwise do nothing.

This hook observes lifecycle completion only. It must not suppress, reorder, or replace any original hidden behavior.

### Installation and idempotence

Install the hooks from `ControlCenterCardHook.install()` after the plugin ClassLoader is accepted and required classes are validated.

Some native SystemUI classes may resolve from the plugin ClassLoader's parent and therefore be shared across plugin reloads. Hook installation must be deduplicated by resolved method/class identity, not only by plugin ClassLoader identity. A new plugin ClassLoader may install its plugin-local hooks while a shared parent-class method remains hooked once.

## Failure and Compatibility Behavior

### Native content failure

The existing staged diagnostics remain:

```text
NATIVE_API_UNAVAILABLE
NATIVE_CONVERT
NATIVE_ITEMS
NATIVE_CALLBACK
MANUAL_BUILD
SAFE_BUILD
```

A native failure falls back to standard Android views to protect SystemUI. The fallback is compatibility-only, not the target appearance. It may be rebound by the same session for live data, but it does not participate in native cap removal.

### Cap hook failure

If method/field resolution fails, return the original adapter count. The visible list may fall back to 20 items, but Wi-Fi/Bluetooth and SystemUI remain stable. Log the compatibility stage once per resolved class.

### Mapping or height hook failure

Retain HyperOS's original result. The detail panel may use the generic transition or height path, but it must still open and close through the native router.

### Hidden hook failure

Stop detail updates as soon as `setDetailListening(false)` arrives. Do not perform a close-time refresh. The next card-listening event refreshes state.

### Threading

- ContentObserver callbacks use the main handler already owned by `GlobalFocusCardConfigStore`.
- All native `setItems()`, callback, suffix, and registry/lifecycle transitions execute on the SystemUI main thread.
- Off-main callers post work rather than touching views directly.
- Destroyed sessions ignore queued work.

## Component Changes

### `FocusModeDetailAdapter.kt`

- introduce the native detail session/handle;
- retain and rebind native `QSDetailContent`;
- expose `refreshItems()`, lifecycle, and destroy operations;
- make native empty state the normal empty path;
- return the HyperModes settings intent for the native footer;
- restore the official header behavior;
- keep the standard-view fallback and staged diagnostics;
- remove synchronous state refresh responsibility from row selection.

### `FocusCardTileProvider.kt`

- cache a typed detail handle instead of a raw adapter object;
- share ContentObserver ownership between card listeners and detail lifecycle;
- forward detail listening to the handle;
- update native items on store changes while open;
- defer card updates while closing;
- remove synchronous refresh from `setDetailListening(false)`;
- destroy/unregister the handle and observer deterministically.

### `ControlCenterCardHook.kt`

- install the four identity-scoped compatibility hooks;
- create and wire the typed detail handle;
- keep existing card placement and sizing logic unchanged.

### New native registry/policy file

Create a focused internal file under `controlcenter/` for:

- weak identity registry;
- item-count policy;
- adapter/content ownership checks;
- lifecycle completion dispatch;
- constants shared by the adapter and hooks.

The hook bodies stay thin; reflection-independent policy is unit-testable.

## Testing Strategy

### JVM tests: native detail session

Extend `FocusModeDetailAdapterTest` to cover:

- `createDetailView()` prefers and reuses native `QSDetailContent`;
- every configured mode is passed to native `setItems()`, including 25 or more modes;
- a store mutation followed by `refreshItems()` updates the same native content instance from 2 to 25 to 3 items;
- live refresh does not call `setSuffix()` again or reset scroll position;
- rebind after hidden reinstalls the callback and submits the latest snapshot;
- an empty list remains a native content view, shows native empty UI, and has a settings footer intent;
- malformed tags do not activate a mode;
- selection activates and requests close once;
- double taps remain terminal;
- native failures still reach standard-view fallback without escaping an exception.

### JVM tests: item-count policy and registry

Test pure policy with fake content/adapter classes:

- registered Focus content with 25 items returns 25;
- registered Focus content with 0 items returns 0;
- unregistered content retains the original count;
- registered content with the wrong suffix retains the original count;
- missing outer reference or items field retains the original count;
- destroyed/unregistered content retains the original count;
- weak registration does not define ownership solely by metrics category or suffix.

### JVM tests: tile/detail lifecycle

Extend `FocusCardTileProviderTest`:

- card listeners share one observer;
- detail listening alone retains one observer while `OPEN`;
- entering `CLOSING` releases the detail observer claim, and the observer closes when no card listener remains;
- store change while open refreshes both card and native items;
- store change while closing updates neither host card view nor detail content synchronously and records one pending refresh;
- `setDetailListening(false)` does not call `refreshState()`;
- hidden completion posts exactly one pending card refresh;
- gesture/back close and selection close converge on the same lifecycle state;
- destroy closes observer, unregisters native objects, and ignores queued callbacks.

### JVM tests: scoped hook policy

Extract hook decisions into pure functions and verify:

- only registered Focus adapters map to `hypermodes_focus`;
- only registered Focus params use specific height;
- built-in/fake unrelated adapters preserve original values;
- hidden completion is dispatched only for the captured Focus adapter;
- repeated installation decisions are idempotent for shared parent-loaded methods.

### Existing regression suite

Run at minimum:

```text
:app:testDebugUnitTest
:app:assembleDebug
```

Existing repository, parser, Focus card, DetailAdapter fallback, and Control Center tail tests must continue to pass.

## On-Device Verification

Target: the same HyperOS 3 / MIUISystemUIPlugin build represented by `apk_decompiled/`.

1. Install the debug APK, enable the SystemUI LSPosed scope, and restart SystemUI.
2. Open Focus detail with 1-5 configured modes.
3. Verify official header, footer, native rows, selected state, blur, grouped corners, ripple/Folme response, scrollbar behavior, and accessibility focus.
4. While detail stays open, add, remove, rename, and reorder modes in HyperModes; verify immediate in-place native updates.
5. Configure 25 or more modes; verify every mode is reachable by native scrolling and no truncation occurs at 20.
6. Verify adding/removing items while scrolled does not force an unexpected jump to the top.
7. Select a mode; verify one activation, one native close route, updated card state, and no half-closed panel.
8. Close by back, drag, tapping outside where supported, and collapsing the whole Control Center; verify the router reaches idle and touch is unblocked.
9. Reopen after each close and verify the latest list and functioning row callback.
10. Repeat after orientation, font-scale, theme, fold/large-screen, and plugin reload changes.
11. Open Wi-Fi, Bluetooth, cellular, and cooling-fan detail panels; verify their item count, height, animations, and interactions are unchanged.
12. Inspect logcat for duplicate hook installation, stale observer callbacks, fallback stages, or SystemUI exceptions.

## Acceptance Criteria

- Normal rendering uses the real HyperOS `QSDetailContent` and native RecyclerView stack.
- The detail list updates while open without restarting SystemUI or reopening the panel.
- Reopened cached detail content always rebinds the latest items and callback.
- All configured modes are visible and scrollable, including more than 20.
- No other native detail panel has its item cap or mapping changed.
- Panel height follows HyperOS's current detail maximum and adapts to configuration changes.
- Focus detail expands/collapses from the Focus card through native mapping when compatible APIs are present.
- Selection, back/gesture close, and whole-Control-Center collapse do not leave a partial panel or blocked touch state.
- No configuration or tile callback mutates the detail host during the closing transition.
- Observer, registry, adapter, content, and ClassLoader references are released when no longer needed.
- Any private-API mismatch falls back to original HyperOS behavior or the existing safe view without crashing SystemUI.
