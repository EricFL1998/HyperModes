# HyperOS Lockscreen ↔ Full-AOD Internals Reference

Complete reference for injecting views into the HyperOS 3 (Android 16) lockscreen
and Full-AOD, based on decompiled MIUI SystemUI sources and verified on-device
(pandora, 25098PN5AC, screen 1220×2656) with LSPosed hooks.

Decompiled sources: `apk_decompiled/miuisystemui_decompiler/sources/`
(line numbers below refer to those files).

---

## 1. View Hierarchy (verified on device)

Walking up from `aod_root_view` during Full-AOD:

```
AodView (id=aod_root_view, 1220×2656, VISIBLE only in steady AOD)
  └─ NotificationShadeWindowView (id=legacy_window_root, 1220×2656, always VISIBLE)
       └─ ViewRootImpl
```

Key facts:

- **`NotificationPanelView` is NOT an ancestor of AOD content.** Anything parked
  in the notification panel has no guaranteed visibility during AOD.
- `aod_root_view` (`com.android.keyguard.widget.AodView`) is **GONE during the
  lockscreen→AOD transition** and becomes visible only afterwards. Views injected
  into it pop in late (looks like a "redraw").
- `NotificationShadeWindowView` is the window content root: attached and visible
  through the whole transition and all of AOD, never faded, never scaled by the
  native animation. **This is the only verified always-visible injection host.**
- The keyguard bottom area (`KeyguardBottomAreaView`, a plain FrameLayout holding
  `keyguard_indication_area`) is scaled AND faded during the transition — see §2.

## 2. The Native Transition (`KeyguardPanelViewController.linkageViewAnim`)

`.../com/android/keyguard/panel/KeyguardPanelViewController.java:2158-2264`

- `wallpaperScale` animates 1.05 ↔ 1.0.
- Every view in `animationViews` (or `fullAodAnimationViewsForVideoDepth` when
  `depthVideoEnable`) gets `scale = wallpaperScale − 0.05` → **final 0.95**, with
  pivot = **screen-size** `(0.5W, 0.4H)` (lines 2238–2245), NOT the view's center.
- So any lockscreen element's on-screen endpoint is:

  ```
  targetX = 0.5W + 0.95 × (x − 0.5W)
  targetY = 0.4H + 0.95 × (y − 0.4H)     // W,H = screen size
  ```

  Example (1220×2656, mode text at (340, 2314)): endpoint = (354, 2251) — it
  rides ~63 px up and ~14 px right.

- `fullAodAnimationViewsForVideoDepth` = `[miuiKeyguardStatusBarView,
  keyguardBottomAreaView]` (lines 3171, 3192): in depth-video mode the whole
  bottom area (and everything inside it) is scaled with the screen pivot.
- `doHideKeyguardViewAnim` (2682–2763) sets `transitionAlpha` on the bottom area
  when entering AOD. **`transitionAlpha` applies subtree-wide — a child can never
  escape a parent's fade.** Anything inside the bottom area fades out.
- Burn-in: `FullAodStateListener.onPositionChanged(int)` (1228–1235) translates
  members of `animationViews`. Only views inside those members inherit it.
- Scale resets: `initDeductedImageScale` (3067–3130) and the loop at 1895–1901
  reset scale/translation on wake/show/enable-change, not during steady AOD.

### Call sites of `linkageViewAnim` (all guarded)

- Line 1366 — wake-direction animation state machine (`z=true`).
- Line 1475 — `wakeObserver.onFinishedGoingToSleep`, if `linkageWithoutOffAnim
  && !interactive`.
- Line 1522 — `wakeObserver.onStartedGoingToSleep`, if
  `((isLinkageStateWithoutTiny() && mScreenOffNeedLinkageAnim)
    || mScreenOffNeedFullAodAnim) && !(mScreenOffNeedFullAodAnim && mNeedScreenFade)`.

So it does not fire on every doze entry; do not rely on it as a lifecycle signal.

## 3. Verified Working Hook Points (LSPosed / libxposed)

All installed from the `com.android.systemui` package classloader in
`onPackageReady`, all with `setExceptionMode(PROTECTIVE)` and `chain.proceed()`
first.

| Hook | Signature | Fires | Use |
|---|---|---|---|
| `com.android.keyguard.doze.MiuiDozeService.onDreamingStarted` | `()` | On doze entry **and pulses every ~1–5 s** while dozing | Full-AOD entry + re-assert |
| `MiuiDozeService.onDreamingStopped` | `()` | On doze exit | Marker only |
| `com.android.systemui.statusbar.phone.DozeServiceHost.stopDozing` | `()` | On wake / doze teardown | Cleanup / restore |
| `KeyguardPanelViewController.linkageViewAnim$default` | static, see §4 | Only on guarded transitions (§2) | Transition marker |

From `onDreamingStarted` reach everything via the service instance:

```kotlin
val service  = HookUtils.getThisObject(chain)
val injector = Reflect.getField(service, "mDozeServiceHostInjector")
val aodView  = Reflect.getField(injector, "mAodView") as FrameLayout
val shadeRoot = aodView.parent as ViewGroup   // NotificationShadeWindowView
```

### Full-AOD vs ordinary doze

```kotlin
val isFullAod = aodView.getTag(aodView.id) == true
```

Verified: ordinary doze pulses report `fullAod=false` with `aod_root_view` GONE
(vis=8); Full-AOD pulses report `true` with it VISIBLE.

## 4. The Kotlin `$default` Signature Trap

`linkageViewAnim$default` is a Kotlin default-arguments synthetic. JADX shows:

```java
public static void linkageViewAnim$default(KeyguardPanelViewController self,
                                           boolean z, String str, int i)   // i = mask
```

but the real bytecode appends mask/marker params JADX hides
(`(self, z, str, mask, Object)` or more). `getDeclaredMethod` with the displayed
signature throws `NoSuchMethodException` and the hook silently never installs.

**Always hook such methods by name enumeration:**

```kotlin
controllerClass.declaredMethods
    .filter { it.name == "linkageViewAnim\$default" }
    .forEach { module.hook(it).setExceptionMode(PROTECTIVE).intercept(...) }
```

## 5. Where to Inject What

| Goal | Host | Why |
|---|---|---|
| Survive lockscreen→AOD, one instance, no fade, no pop-in | `NotificationShadeWindowView` (parent of `aod_root_view`) | Always visible, unfaded, unscaled |
| Inherit native burn-in translation | Inside a member of `animationViews` (e.g. `aod_root_view`) | `onPositionChanged` translates them — but `aod_root_view` is GONE during the transition |
| Lockscreen-only content | `KeyguardBottomAreaInjector.mIndicationArea` | Fades/scales away with the bottom area on AOD entry |
| Never | miuiaod `AODView` | Full-AOD sets its parent alpha to 0 |

A view parked in `NotificationShadeWindowView` does NOT move during the native
shrink (the host itself is never transformed). To make it ride the transition,
mirror the trajectory: park at the raw lockscreen position, then animate
translation to the §2 endpoint (580 ms, `DecelerateInterpolator(1f)` matches the
native feel). Re-assert on every `onDreamingStarted` pulse.

### Positioning recipe (what HyperModes does)

1. While on the lockscreen, cache the injected view's screen bounds on every
   layout (`getLocationOnScreen`).
2. On Full-AOD start: reparent the SAME view into the shade root with
   `FrameLayout.LayoutParams` at margins = cached screen position (host origin
   is (0,0)), then `view.animate().translationX/Y(endpoint − raw)` over 580 ms.
3. On each doze pulse: re-set translations directly (no animation).
4. On `stopDozing`: cancel the animator, zero translations, restore the view to
   its recorded parent/index/layoutParams.

Gotchas:

- **Do not re-read bounds at park time.** By dream start the bottom area is
  already mid-transition, so `getLocationOnScreen` returns distorted values; use
  the last steady-lockscreen cache.
- **Do not clear the cached bounds when a read fails** (e.g. the view is
  momentarily unlaid-out right after a restore). Clearing there makes every
  second doze entry skip parking (alternating visible/invisible). Only clear
  when a genuinely different view instance attaches.
- `NotificationShadeWindowView` extends FrameLayout, so `FrameLayout.LayoutParams`
  margins are honored as-is.

## 6. Logging / Debugging on Device

- `module.log(...)` (libxposed) goes to LSPosed's internal log — **NOT visible in
  `adb logcat`** without root.
- `android.util.Log.w(tag, msg)` from inside the SystemUI process IS visible.
  Dual-log everything during bring-up:

  ```
  adb logcat -v time -s HyperModes.ModeDisplay HyperModes.FullAod HyperModes.LockHook
  ```

- `adb shell pkill` / `force-stop` on SystemUI is not permitted without root;
  restart SystemUI from the device (or LSPosed manager) to load new builds.
  **New APK code only loads after a SystemUI restart** — check the PID in your
  log lines to know which build you are reading.
- Fast doze-flutter (~1–2 s start/stop cycles) is normal on this device; design
  re-assert logic to be idempotent across pulses.
- Screen recordings: OBS Android-mirror crops; extract frames with
  `ffmpeg -vf "select='eq(n\,N)'"` (exact frame numbers, not `mod()`).

## 7. HyperOS Version-Specific Guards Found in Decompiled Code

- `keyguardCommonSettingObserver.isLinkageStateWithoutTiny()` / tiny panel mode
  changes the transition path (TinyKeyguardPanelViewController exists alongside).
- `getAodUsingSuperWallpaperStyle()` routes to `aodSuperWallpaperViewAlphaAnim`
  instead of the scale linkage.
- `depthVideoEnable` (KeyguardPanelViewController.java:317) swaps the animation
  view list to `fullAodAnimationViewsForVideoDepth`.

These flags vary per wallpaper/settings, so never assume a single transition
path — anchor on lifecycle hooks (`onDreamingStarted`/`stopDozing`) and measure
live view geometry instead.

## 8. OS4 Native Lockscreen Status Row ("勿扰 | n 条通知")

HyperOS 4 adds a native status row on the lockscreen — the num-state view
("勿扰 | 3 条通知") — which sits visually close to our injected mode display.
Decompiled chain (sources: `os4_android17_apks/miuisystemui_decompiler`):

- Layout: `res/layout/num_state_view.xml` — `zen_view` (LinearLayout:
  `zen_icon` ImageView + `zen_text` TextView) | `dividing_line` |
  `notification_count_view`.
- View: `com.miui.systemui.notification.view.NotificationNumStateView`
  (public fields `zenView`, `zenText`, `zenIcon`, `isZenModeEnabled`).
- Text source: string `keyguard_num_state_zen_mode_text` ("勿扰" in zh-rCN),
  applied by `updateZenViewText()` — but only when `isZenModeEnabled == true`.
- Visibility driver: `NotificationNumStateViewBinder` collects
  `NotificationNumStateViewModel.isZenModeEnabled` (a StateFlow mapped from
  `ZenModeControllerImpl` = system DND state), sets the field, calls
  `updateZenViewText()`, then posts a Folme animation runnable.
  `updateZenViewText()` is re-invoked on config/locale changes too.

HyperModes rewrites this text in place via `ZenTextHook` (hooked in
`onPackageReady` for `com.android.systemui`): after `chain.proceed()` it
reads the active mode (`Settings.Global pixel_routines_full_config`) and, when
a mode is active while the row would show "勿扰", replaces `zenText` (and
`zenView.contentDescription`) with the mode name. A ContentObserver on the
config key re-applies on mode switches that do not toggle DND (no flow edge),
and restores the native string when the last mode exits while DND stays on.

### 8.1 Showing the segment without DND

The zen segment must also show while DND is OFF (mode active but the mode's
DND action disabled). Native pieces involved:

- Binder emission (case 0): sets `view.isZenModeEnabled`, calls
  `updateZenViewText()`, `requestLayout()`, then posts
  `NotificationNumStateView$updateZenMode$1(0)` with the CAPTURED DND value.
- The runnable (fields `this$0`, `$isZenModeEnabled`, `$r8$classId`):
  classId 0 = zen, 1 = notification count. The zen variant fades
  `zenView` + `dividingLine` in/out via
  `NumStateViewAnimateExt.animateUpdateViewVisibility` and applies the
  half-width translate compensations — all keyed off the captured flag, NOT
  the view field.
- Layout math (`onMeasure`/`getRealWidth`) reads the view FIELD; text is
  applied by `updateZenViewText()` only when the field is true.

`ZenTextHook` therefore: forces `isZenModeEnabled = realZen || modeActive`
after every `updateZenViewText()` (real DND read from
`Settings.Global zen_mode`), writes mode name + mode icon (`zenIcon`,
re-applied after `updateColor()` resets it to the native moon), and drives
show/hide by instantiating the NATIVE runnable via reflection
(`newInstance(cls, 0)` + set `this$0`/`$isZenModeEnabled` + `view.post`),
so fades/divider/translations stay pixel-native. Hooking the runnable's
`run()` flips the captured flag back to true whenever a mode is active,
neutralizing DND-off emissions; classId 1 passes untouched. Mode exit:
effectiveZen false → native fade-out; DND still on → native "勿扰" restored.

### 8.2 Why the old injected display path was removed

The row lives at the TOP of the keyguard root ConstraintLayout
(`NotificationNumStateSection`: top/start/end constrained to parent), NOT in
the bottom indication area. In steady Full-AOD,
`KeyguardPanelViewController` pushes `scaleAndAlpha = (1.0, 1.0)` (when
`fullAodEnable() && !startedWakeupAnimation && !keyguardBouncerShowing`),
which the binder applies to the row — so the native row (and the mode text
riding in it) stays visible through Full-AOD without any injection.

Consequently the whole injected path was deleted: `LockscreenHook.kt`,
`FullAodHook.kt`, `ModeDisplayCoordinator.kt`, `ModeDisplayViewFactory.kt`,
`ModeDisplayPositioner.kt` (+ their tests). `ModeDisplayState.kt`
(state parsing) remains — `ZenTextHook` consumes it.
