# Focus Native Detail UI Simplification Design

## Goal

Refine the Focus native detail panel while retaining HyperOS `QSDetailContent`, its native transition, native row interactions, specific-height routing, and panel lifecycle.

The finished panel must:

- retain the outer glass rounded panel;
- omit the bottom “more settings” button;
- remove the dark inner list background;
- hide the vertical scrollbar while preserving scrolling;
- show each mode name without `On` or `Off` summary text;
- localize the three unchanged built-in mode names from module string resources while preserving user-renamed names;
- show the mode's existing vector Drawable icon on the left;
- preserve the current content height behavior instead of forcing the native content to fill the panel;
- preserve mode selection, click handling, scrolling, accessibility state, and panel-close refresh behavior.

## Scope

This change is limited to Focus-owned native detail content registered through `FocusNativeDetailRegistry`. It must not alter other SystemUI or MIUI detail panels.

The existing native-detail class loading, adapter mapping, item-count, specific-height, and hidden-completion hooks remain in place. This change does not replace `QSDetailContent` with a custom list and does not introduce global resource overrides.

## Adapter Contract

`FocusModeDetailSession.DetailAdapterHandler` will return `null` from `getSettingsIntent()`.

The MIUI detail panel treats the settings intent as the source for its bottom more-settings action. Returning `null` removes that action instead of leaving an empty button. The adapter continues to provide its existing metrics category, title, animation preference, no-header behavior, and native detail view.

## Item Presentation

Each `QSDetailContent.SelectableItem` will continue to receive:

- `title`: the configured mode name;
- `tag`: the mode identifier used by the native callback;
- `selected`: always `false`, so HyperOS never splits or highlights the active row;
- `selectable` and `activated`: the native interaction state used to preserve click handling.

The item will not receive `summary` or `secondarySummary`. The repository remains the source of truth for the active mode, but the native list intentionally exposes no selected/highlighted state and no visible `On` or `Off` text.

The item will receive `iconDrawable` from a shared Focus mode-icon resolver. Resolution order is:

1. the module drawable named by `ModeConfig.statusIcon`;
2. the module drawable mapped from `ModeConfig.icon` by `ModeIconMapper.getStatusBarIcon()`;
3. `R.drawable.ic_stat_zen`;
4. `android.R.drawable.ic_dialog_info`;
5. a transparent `ColorDrawable`.

The resolver uses the module context as the primary resource owner and the plugin context only as a compatibility fallback. This preserves existing custom mode vector-drawable styles and remains compatible with older modes that only store `icon`.

Built-in row titles are resolved at display time without changing persisted configuration. Stable IDs `dnd`, `bedtime`, and `driving` map to `R.string.mode_dnd`, `R.string.mode_bedtime`, and `R.string.mode_driving` only while the stored name remains one of that built-in mode’s default English or Simplified Chinese names. Any other stored name is treated as a user rename and displayed verbatim; custom mode IDs are always displayed verbatim.

This is a presentation-only change. Selecting a row still updates the repository, requests panel dismissal once, and refreshes the card after the native panel finishes hiding.

## Local List Decoration

After `QSDetailContent` has been converted or inflated, `FocusModeDetailSession` will apply best-effort decoration to that returned Focus-owned content only.

The decoration will locate the internal scrolling list without depending on a hard-coded application resource ID. The preferred strategy is to walk the returned content’s descendants and identify the RecyclerView/list view used by `QSDetailContent`. Once found, it will:

- clear the list view and immediate list-host backgrounds;
- clear Focus-owned row background, foreground, MIUI blur/blend state, and Folme touch animation after native `onBindViewHolder` completes;
- disable the vertical scrollbar;
- add an idempotent 16dp top padding to the Focus list while preserving left, right, and bottom padding;
- set `clipToPadding=false` and leave scrolling enabled;
- preserve existing content and list layout parameters without forcing `MATCH_PARENT`;
- leave the outer MIUI `DetailPanel` background untouched.

A single native `QSDetailContent.Adapter.onBindViewHolder` after-hook will perform row cleanup only when the adapter’s outer content is registered in `FocusNativeDetailRegistry`. The hook runs after HyperOS applies its row background, blur, blend colors, selection grouping, click listener, and Folme touch style. Other SystemUI detail content returns unchanged.

## Compatibility and Failure Behavior

List decoration is optional presentation work. If a future SystemUI version changes the internal hierarchy and the list cannot be found:

- native item binding must still succeed;
- the panel must remain scrollable and interactive;
- the session must not fall back to “Focus detail is unavailable”;
- the system’s default list background or scrollbar may remain visible;
- a diagnostic may record the decoration failure without treating it as native API failure.

Removing the settings intent and omitting item summaries use stable `DetailAdapter` and `SelectableItem` contracts already resolved by the native API layer.

## Testing

Unit tests will cover these independent behaviors:

1. `getSettingsIntent()` returns `null`, preventing the bottom settings action.
2. Built native selectable items leave both summary fields unset while preserving title, tag, selected state, and click behavior.
3. Local list decoration clears list/host and bound-row backgrounds, disables only the scrollbar, preserves scrolling, and does not alter layout parameters.
4. Failure to locate a compatible list is non-fatal and still returns the native content.
5. Mode icon resolution prefers `statusIcon`, falls back through `ModeIconMapper`, and provides the documented final fallbacks.
6. Every native selectable row receives the resolved non-null `iconDrawable` while retaining title, tag, and selected state.
7. Existing native conversion, item submission, callback installation, adapter mapping, specific-height, item-count, and hidden-completion tests continue to pass.

## Device Verification

After installing the debug build and restarting SystemUI:

1. Open the Focus card detail panel.
2. Confirm the outer glass rounded panel remains.
3. Confirm the bottom more-settings button is absent.
4. Confirm no dark inner rectangular list background is visible.
5. Confirm no vertical scrollbar is visible.
6. Confirm rows show the configured Drawable icon and mode name, with no status summary.
7. Confirm the current content height behavior is unchanged.
8. Scroll through all modes.
9. Select a mode and confirm the panel closes and the Focus card refreshes.
10. Reopen the panel and confirm the selected state and content remain correct.
11. Check logs for absence of native API, conversion, item, callback, icon-resolution, and decoration failures.
