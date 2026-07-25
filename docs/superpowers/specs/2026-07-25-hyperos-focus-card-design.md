# HyperOS 3 Focus Control Center Card Design

## Summary

HyperModes will add a native-style Focus card to the HyperOS 3 Control Center through LSPosed. The card is a horizontal 1×2 card matching the existing Wi-Fi and mobile-data cards. It appears on the left side of the row immediately below those cards.

This is not an Android `TileService`, a normal compact quick-setting tile, an app widget, or a manually inserted standalone `View`. It participates in HyperOS's private card-style QSTile pipeline so HyperOS continues to own the card's layout, dimensions, background, blur, animation, typography, haptics, lifecycle, and detail-panel routing.

The design targets the decompiled HyperOS 3 implementation in `apk_decompiled/`, corresponding to `MIUISystemUIPlugin 17.1.4.71.0` on the current test device.

## Goals

- Add one automatic, non-editable Focus card below the Wi-Fi and mobile-data cards.
- Use the native `QSCardItemView` presentation and two-column span.
- Show the currently active HyperModes mode.
- When no mode is active, show the most recently used mode.
- On the first use only, if there is no history, randomly choose one configured mode and persist it as the last mode.
- Toggle the displayed mode on a normal click.
- Open a native HyperOS detail panel on long press and allow immediate mode switching.
- Refresh immediately when the app, an automatic trigger, system_server, or the card changes the active mode.
- Fail safely without destabilizing SystemUI when a private API changes.

## Non-goals

- Do not expose a standard Android `TileService`.
- Do not place the card in the normal quick-setting edit list.
- Do not rely on a standalone `miui.systemui.plugin` process.
- Do not replace Wi-Fi, mobile data, or another system card.
- Do not copy the visual appearance into a custom overlay or independent card view.
- Do not support simultaneous active modes; HyperModes guarantees at most one active mode.
- Do not provide user-driven card positioning in this version because the relevant system-card area is not editable.

## Confirmed User Experience

### Placement and size

- The card occupies two spans of HyperOS's four-column Control Center grid.
- It has the same height and width class as the Wi-Fi and mobile-data cards.
- It is appended after the native card-style specs. With the normal `[wifi, cell]` order, it occupies the left side of the next row.
- It is automatically present and cannot be added, removed, or reordered through Control Center editing.

### Content

- The left side shows the selected mode's monochrome system-style icon.
- The right side shows the selected mode's name.
- HyperOS supplies the native status subtitle and active/inactive text styling.
- Active mode: native enabled/highlighted card style.
- Inactive remembered mode: native disabled/low-emphasis card style.

### Click

- If a mode is active, clicking the card deactivates it and remembers it as the last mode.
- If no mode is active, clicking activates the remembered/displayed mode.
- If the remembered mode was deleted, the repository selects a valid configured mode and persists the replacement before activation.
- If no modes exist, the card is unavailable and a normal click does nothing.

### Long press

- Long press opens HyperOS's native secondary Detail panel.
- The panel lists configured HyperModes modes with their icon and name.
- The active mode is highlighted.
- Selecting a mode immediately replaces the active mode, records the selected mode as the last mode, refreshes the card, and closes the detail panel.
- If no modes exist, the detail panel shows an empty state and an action that opens HyperModes.

## Verified HyperOS Private Pipeline

The decompiled code establishes this path:

1. `miui.systemui.controlcenter.qs.QSController.getCardStyleTileSpecs()` returns card specs from `R.array.card_style_tiles_mobile` or `R.array.card_style_tiles_wifi`.
2. `miui.systemui.controlcenter.panel.main.qs.QSCardsController.createCardTiles()` calls `QSController.createTile(spec)` for each spec.
3. Each non-null `QSTile` is wrapped with `QSRecord.Factory.create(tile, true)`.
4. A card `QSRecord` reports view type `2273` and span size `2`, unless HyperOS explicitly applies a shrink-card style.
5. `QSCardsController.createViewHolder()` inflates `QsCardItemViewBinding`, initializes `QSCardItemView`, and returns `QSCardViewHolder`.
6. `MainPanelAdapter` uses `MainPanelListItem.getSpanSize()` in a four-column `GridLayoutManager`.
7. `QSRecord` wires the card view to the `QSTile` click, long-click, callback, listening, and detail behavior.

Relevant decompiled references:

- `apk_decompiled/miuisystemuiplugin_decompiler/sources/miui/systemui/controlcenter/qs/QSController.java`
- `apk_decompiled/miuisystemuiplugin_decompiler/sources/miui/systemui/controlcenter/panel/main/qs/QSCardsController.java`
- `apk_decompiled/miuisystemuiplugin_decompiler/sources/miui/systemui/controlcenter/panel/main/qs/QSRecord.java`
- `apk_decompiled/miuisystemuiplugin_decompiler/sources/miui/systemui/controlcenter/panel/main/qs/QSCardViewHolder.java`
- `apk_decompiled/miuisystemuiplugin_decompiler/sources/miui/systemui/controlcenter/qs/tileview/QSCardItemView.java`
- `apk_decompiled/miuisystemuiplugin_decompiler/sources/miui/systemui/controlcenter/panel/main/recyclerview/MainPanelAdapter.java`
- `apk_decompiled/miuisystemui_decompiler/sources/com/android/systemui/plugins/qs/QSTile.java`

## Architecture

### 1. Plugin hook entry

`ControlCenterCardHook` is installed from the `com.android.systemui` process.

It hooks `com.android.systemui.shared.plugins.PluginInstance.loadPlugin()` and, after the original call:

1. Calls `getPackage()` and accepts only `miui.systemui.plugin`.
2. Calls `getPluginContext()`.
3. Obtains `pluginContext.classLoader`.
4. Installs the HyperOS card hooks once for that ClassLoader identity.

The hook must not depend on `onPackageReady("miui.systemui.plugin")`: the plugin is dynamically loaded into the SystemUI process and does not run as an independent application process on the target device.

If the plugin is unloaded and later loaded through a new ClassLoader, the new ClassLoader may be hooked independently.

### 2. Card spec injection

Define one private spec:

```text
hypermodes_focus
```

Hook `QSController.getCardStyleTileSpecs()` after the original implementation:

1. Read the returned list.
2. Copy it to a mutable list.
3. Append `hypermodes_focus` only when absent.
4. Return the copy.

The hook preserves all native specs and their order. It does not add the Focus spec to the ordinary added-tile configuration or edit list.

### 3. QSTile creation

Hook `QSController.createTile(String)` before the original implementation:

- For specs other than `hypermodes_focus`, do not alter behavior.
- For `hypermodes_focus`, return a dynamic proxy implementing the exact `com.android.systemui.plugins.qs.QSTile` interface loaded from the plugin/SystemUI class space.
- If creation fails, log the failure and return `null`; `QSCardsController` will skip the unsupported spec.

The provider must implement the current QSTile interface, not the old `MiuiQSTile` interface used by the abandoned implementation.

### 4. Native record and view creation

The module does not directly create `QSRecord`, `QSCardItemView`, `QSCardViewHolder`, or grid layout parameters. The unchanged HyperOS implementation performs those steps. This gives the card:

- view type `2273`;
- span size `2`;
- native card dimensions and margins;
- native enabled and disabled backgrounds;
- native blur and color blending;
- native icon sizing and tinting;
- native typography and marquee behavior;
- native press animation and haptic feedback;
- native panel expansion animation and configuration handling.

## Components

### `ControlCenterCardHook`

Responsibilities:

- Hook the SystemUI plugin load boundary.
- Validate the plugin package and ClassLoader.
- Resolve the target HyperOS classes and method signatures.
- Install card-spec and tile-creation hooks once per ClassLoader.
- Record concise compatibility and failure diagnostics.

It does not implement mode business logic or card state.

### `FocusCardTileProvider`

Responsibilities:

- Create and own the QSTile dynamic proxy.
- Keep the current `QSTile.BooleanState`.
- Maintain a thread-safe callback collection.
- Implement click, long-click, refresh, listening, user-switch, and destroy behavior.
- Register and unregister the configuration observer.
- Convert module drawables to the plugin's `DrawableIcon`.
- Create the Focus detail adapter.

### `FocusCardStateRepository`

Responsibilities:

- Parse `Settings.Global["pixel_routines_full_config"]`.
- Resolve active, last-used, first-use-random, and empty states.
- Persist `lastModeId` durably.
- Activate and deactivate modes through the same configuration path used by the existing mode engine.
- Tolerate deleted modes and malformed configuration without destructive writes.

### `FocusModeDetailAdapter`

Responsibilities:

- Dynamically implement the current `DetailAdapter` interface.
- Build a mode list using the plugin Context and native-compatible Android views.
- Highlight the current active mode.
- Switch immediately on item selection.
- Expose no redundant header toggle (`getToggleVisible() == false`).
- Show an empty state and HyperModes entry action when no modes exist.

## Persistent State

### Existing configuration

The authoritative mode configuration remains:

```text
Settings.Global["pixel_routines_full_config"]
```

It already contains `activeModeId` and `modes`.

### Last-used mode

Add an optional `lastModeId` field to the serialized root configuration rather than using a plugin-private preference. This keeps the app, system_server, and SystemUI card on one source of truth.

Backward compatibility requirements:

- Existing JSON without `lastModeId` must continue parsing.
- Existing saves must preserve `lastModeId` once introduced.
- A deleted or unknown `lastModeId` is treated as absent.
- Malformed JSON is never overwritten merely because the card attempted to read it.

### Display selection algorithm

Given a valid parsed mode list:

1. If `activeModeId` references an existing mode, display that mode and persist it as `lastModeId` when needed.
2. Otherwise, if `lastModeId` references an existing mode, display it.
3. Otherwise, if the mode list is non-empty, choose one mode randomly once, persist its ID as `lastModeId`, and display it.
4. Otherwise display an unavailable fallback labelled `专注模式` with the default zen icon.

Random selection occurs only during first-time initialization or recovery from a missing/deleted history value. Reopening Control Center, restarting SystemUI, and rebooting must not reroll a valid persisted selection.

## QSTile State Mapping

The proxy returns `QSTile.BooleanState`.

For an active displayed mode:

```text
spec = "hypermodes_focus"
state = 2
value = true
label = displayed mode name
icon = displayed mode icon
contentDescription = mode name plus active status
dualTarget = false
handlesLongClick = true
handlesSecondaryClick = false
```

For a remembered but inactive mode:

```text
state = 1
value = false
```

For no configured modes:

```text
state = 0
value = false
label = "专注模式"
icon = default zen icon
```

`QSCardItemView` supplies the normal HyperOS status subtitle for non-cell cards. The first version does not hook the view layer solely to force custom subtitle wording.

Every primitive-returning QSTile method must return a type-correct value. Unknown methods must not blindly return `null`, because reflective proxy unboxing could crash SystemUI.

## Icon Flow

1. Read the mode's configured emoji/icon identifier.
2. Map it with `ModeIconMapper` to a monochrome vector drawable resource.
3. Create a module package Context from the plugin/SystemUI Context.
4. Load the drawable, falling back to `ic_stat_zen`.
5. Instantiate `miui.systemui.controlcenter.qs.DrawableIcon` with the plugin ClassLoader.
6. Place that object in `QSTile.State.icon`.

The native `QSCardItemIconView` remains responsible for sizing, tinting, and state animation.

## Runtime Data Flow

### Listening lifecycle

When any QSTile client starts listening:

- Track listener tokens rather than a single boolean where practical.
- Register one `ContentObserver` for `Settings.Global.getUriFor("pixel_routines_full_config")`.
- Refresh state immediately.

When no clients remain or `destroy()` is called:

- Unregister the observer.
- Release callbacks and Context references.

### Configuration change

When the observer fires:

1. Parse the latest configuration.
2. Resolve the display mode.
3. Build a new BooleanState.
4. Notify every callback using `onStateChanged(state)` on the main/UI looper.

This covers changes originating from the HyperModes UI, system_server automation, scheduling, triggers, and card interactions.

### Normal click

- Active mode present: clear `activeModeId`; retain that mode as `lastModeId`.
- No active mode: activate the displayed remembered mode.
- No valid mode: no-op.

The write triggers the existing `RoutineCoreEngine` observer, which applies or reverts mode actions.

### Long click and Detail routing

`longClick()` obtains the tile callbacks and invokes `onShowDetail(true)`. HyperOS then routes through `QSController`, `SecondaryPanelRouter`, and `DetailPanelController` using the tile's `DetailAdapter`.

The Detail adapter's list selection writes the selected ID as both `activeModeId` and `lastModeId`. The repository's single-active-mode contract replaces the previously active mode rather than allowing simultaneous modes.

After selection, the detail panel is closed through the available native callback/routing path and the tile refreshes from the resulting configuration update.

## Compatibility and Failure Protection

### Signature validation

Before installing hooks, verify:

- `QSController` exists in the plugin ClassLoader.
- `getCardStyleTileSpecs()` has no parameters and returns a `List`-compatible object.
- `createTile(String)` exists.
- the current QSTile interface and BooleanState class exist.
- `DrawableIcon(Drawable)` exists.
- DetailAdapter exists before enabling long-press details.

A missing Detail API may disable long press while leaving the card and click behavior operational.

### Safe degradation

- Plugin load hook failure: do not alter SystemUI.
- Card-spec hook failure: no Focus card appears.
- Focus tile creation failure: return `null`; native code skips it.
- Config parse failure: show an unavailable fallback; do not rewrite the malformed config.
- Missing icon: use the default zen icon.
- Settings write failure: keep the previous state and reread the authoritative config.
- Detail construction failure: normal click remains available and long press fails without propagating an exception.
- Every hook uses protective exception handling; exceptions must not escape onto the SystemUI main thread.

### Duplicate prevention

- Track installed plugin ClassLoaders by identity with weak references where feasible.
- Check the returned card-spec list before appending.
- Never register more than one observer per tile proxy.
- Clean up tile proxies on `destroy()`.

## Migration from the Abandoned Implementation

The current implementation follows an incompatible route:

- `ControlCenterCardHook.install()` is empty.
- `ControlCenterHook` searches for an obsolete `LocalMiuiQSTilePlugin` name.
- It expects a nonexistent `getAllPluginTiles()` map in this HyperOS version.
- `FocusTileProvider` proxies `MiuiQSTile` instead of the current `QSTile` interface.
- It derives state from `NotificationManager.automaticZenRules` instead of HyperModes's authoritative configuration.

Implementation should remove or disable that route so only the new card hook is installed. Unrelated existing control-center functionality must not be refactored as part of this work.

## Diagnostics

Use concise module logs for these boundaries:

1. SystemUI module loaded.
2. `miui.systemui.plugin` load completed.
3. Plugin ClassLoader obtained.
4. Target class and method signatures validated.
5. `hypermodes_focus` appended to card specs.
6. Focus tile creation requested and completed.
7. Tile listening started or stopped.
8. Config parsing or Settings writes failed.
9. Detail adapter creation or routing failed.

Do not log every draw, callback, or successful state read.

## Testing Strategy

### Pure unit tests

Extract state selection and config updates into functions testable without SystemUI classes.

Required cases:

- Active mode is displayed and remembered.
- Inactive state displays valid `lastModeId`.
- First use with modes selects one random candidate and persists it.
- A valid persisted selection is never rerolled.
- Deleted `lastModeId` selects and persists a replacement.
- Empty modes produce unavailable state.
- Deactivation clears only `activeModeId` and preserves history.
- Activation writes both active and last mode IDs.
- Malformed JSON does not cause a destructive write.

Inject the random selector so tests are deterministic.

### Proxy contract tests

Using small fake interfaces or available stubs, verify:

- Primitive return types are never `null`.
- callbacks receive state changes.
- repeated listener registration creates only one observer.
- the observer is removed after the last listener and on destroy.
- click follows active/inactive/empty rules.
- long click requests Detail only when available.

### Build verification

- `./gradlew :app:assembleDebug`
- `git diff --check`

### Device verification

On the target HyperOS 3 device:

1. Install the debug APK and enable the SystemUI scope.
2. Restart SystemUI.
3. Confirm the expected hook lifecycle logs in order.
4. Expand Control Center and verify placement below Wi-Fi/mobile data on the left.
5. Confirm native dimensions, corner radius, blur, text, icon tint, press animation, and haptics.
6. Verify active and inactive visual states.
7. Verify first-use random selection persists across repeated panel opens, SystemUI restart, and reboot.
8. Verify click deactivation and reactivation.
9. Verify external mode activation refreshes the card immediately.
10. Verify long press opens the native Detail panel and selection switches immediately.
11. Verify no duplicate card after repeated plugin reloads or configuration changes.
12. Verify an empty mode list and a deleted remembered mode.

## Acceptance Criteria

The feature is complete when all of the following are true:

- Exactly one Focus card appears automatically.
- It is a native card-style QSTile with a two-column span.
- It appears below Wi-Fi/mobile data on the left in the target layout.
- It uses HyperOS's native card view and animations.
- It displays the active mode or the persisted last-used mode.
- First-use random selection happens once and persists.
- Click toggles the expected mode through the existing mode engine.
- Long press opens the native mode-selection Detail panel.
- Selecting a mode immediately switches to it.
- All external mode changes refresh without restarting SystemUI.
- SystemUI and plugin reloads do not duplicate hooks, observers, or cards.
- Missing private APIs or malformed state do not crash or disable Control Center.

## Device-Test Revision: Bottom Placement and Detail Crash

The first on-device builds exposed two requirements that supersede the earlier second-row placement:

1. A two-span Focus record placed beside the two-span media controller inherits the media row's two-row height. Focus must therefore be the final real item in the right panel, after device controls and before the structural footer spacer.
2. Production `TrackingScrollView`, `TrackingLinearLayout`, and `TrackingImageView` test helpers crash SystemUI because Android calls overridden `ViewGroup` methods during superclass construction before Kotlin fields initialize. Test scaffolding must not subclass production Android views.

### Bottom placement architecture

Focus remains a native `QSRecord` owned behaviorally by `QSCardsController`, but it is exposed through a stable tail `MainPanelContent` proxy:

- Filter only `hypermodes_focus` from the list returned by `QSCardsController.getListItems()`.
- Preserve Wi-Fi, cell/Bluetooth, and VoWiFi order in the original content group.
- The tail proxy returns exactly the native Focus `QSRecord` from `getListItems()`.
- Delegate ViewHolder creation, binding, unbinding, configuration, mode, style, super-save, spread, expand, and payload behavior to `QSCardsController` or faithfully apply the `MainPanelContent` default behavior.
- After `MainPanelContentDistributor.distributePanels(boolean)` completes, insert the stable proxy into `rightPanelContent` immediately before `rightFooterSpace`; append if no footer is present.
- Remove an existing proxy before reinsertion so repeated distribution and configuration changes are idempotent.
- Keep the Focus record at `shrinkCardStyle=false`, giving it span size `2`. Full-width device content before it ends on a row boundary, so Focus occupies the bottom-left half and the bottom-right half stays blank.
- If the target private classes or methods differ, leave the native panel list unchanged rather than partially reordering it.

This revised placement replaces the earlier requirement that Focus appear directly below Wi-Fi/mobile data.

### Detail crash correction

`FocusModeDetailAdapter` must use only standard Android `ScrollView`, `LinearLayout`, and `ImageView` objects in production fallback UI. The production source must not contain `TrackingScrollView`, `TrackingLinearLayout`, or `TrackingImageView`.

Testing moves to pure seams:

- mode-to-row descriptor mapping;
- `FocusModeSelectionController` activation/refresh/dismiss behavior;
- reflected native `QSDetailContent` mapping through fake non-Android classes;
- safe app-launch selection;
- callback dispatch and native Detail close routing.

Detail creation remains fail-closed:

1. Prefer reflected native `QSDetailContent` and log the exact compatibility/setup stage when unavailable.
2. Fall back to standard Android views.
3. If fallback construction also fails, return a safe empty standard `View` and prevent the exception from escaping `DetailAdapter.createDetailView()` into SystemUI.

### Revised acceptance criteria

- Focus is the final real Control Center item, after available device-center/device-control content and before footer spacing.
- Focus occupies the left two spans of its own row; the right two spans remain empty.
- Focus never shares the media card's two-row height.
- Repeated panel distribution, plugin reload, orientation changes, and device-control availability changes do not duplicate Focus or its tail owner.
- Long press never restarts `com.android.systemui`.
- Production APK contains no `Tracking*` Android View subclasses from JVM test scaffolding.
- Native `QSDetailContent` is preferred; every fallback reason is diagnosable and every construction failure is contained.
- Mode selection still refreshes the tile and closes the native SecondaryPanel through `onShowDetail(false)`.
