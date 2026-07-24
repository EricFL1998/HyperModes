# iOS Focus-Style Tile Implementation Guide

## Overview
This guide explains how to create an iOS Focus-style tile for HyperOS that displays a compact view initially and expands to show multiple focus modes on long press.

## Architecture

### Key Components

1. **FocusTile** - Main tile implementation (extends QSTileImpl for system tiles or implements MiuiQSTile for plugin tiles)
2. **FocusDetailAdapter** - Handles the expandable detail view with multiple focus modes
3. **FocusMode** - Data model representing each focus mode (Do Not Disturb, Reduce Interruptions, Personal, Work, Sleep)

## Implementation Strategy

### Option 1: System Tile (Recommended)
Modify MiuiSystemUI to add the tile directly to the system.

**Location:** `MiuiSystemUI.apk/sources/com/android/systemui/qs/tiles/`

### Option 2: Plugin Tile
Add to MiuiSystemUIPlugin for easier deployment.

**Location:** `MiuiSystemUIPlugin.apk/sources/miui/systemui/quicksettings/`

## Code Structure

### 1. FocusTile Main Class

```java
package com.android.systemui.qs.tiles; // or miui.systemui.quicksettings for plugin

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSDetailContent;
import com.android.systemui.qs.tileimpl.QSDetailAdapter;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import java.util.ArrayList;
import java.util.List;

public final class FocusTile extends QSTileImpl {
    private final FocusDetailAdapter mDetailAdapter;
    private final FocusModeController mFocusModeController;
    private FocusMode mCurrentMode;

    public FocusTile(QSHost host, ...) {
        super(host, ...);
        this.mDetailAdapter = new FocusDetailAdapter(this.mContext);
        this.mFocusModeController = new FocusModeController(this.mContext);
        this.mCurrentMode = mFocusModeController.getCurrentMode();
    }

    @Override
    public QSTile.State newTileState() {
        return new QSTile.BooleanState();
    }

    @Override
    public CharSequence getTileLabel() {
        // Show current focus mode name or "Focus"
        return mCurrentMode != null ? mCurrentMode.name : "Focus";
    }

    @Override
    protected void handleClick(Expandable expandable) {
        // Toggle current focus mode on/off
        if (mCurrentMode != null) {
            boolean newState = !mCurrentMode.isActive;
            mFocusModeController.setModeActive(mCurrentMode.id, newState);
            refreshState(null);
        }
    }

    @Override
    protected void handleSecondaryClick(Expandable expandable) {
        // Show detail view with all focus modes
        showDetail(true);
    }

    @Override
    protected void handleLongClick(Expandable expandable) {
        // Also show detail view on long press
        showDetail(true);
    }

    @Override
    public Object getDetailAdapter() {
        return mDetailAdapter;
    }

    @Override
    protected void handleUpdateState(QSTile.State state, Object arg) {
        QSTile.BooleanState booleanState = (QSTile.BooleanState) state;
        
        mCurrentMode = mFocusModeController.getCurrentMode();
        
        if (mCurrentMode != null) {
            booleanState.value = mCurrentMode.isActive;
            booleanState.state = mCurrentMode.isActive ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;
            booleanState.label = mCurrentMode.name;
            booleanState.icon = ResourceIcon.get(mCurrentMode.iconRes);
            booleanState.secondaryLabel = mCurrentMode.isActive ? mCurrentMode.subtitle : null;
        } else {
            booleanState.value = false;
            booleanState.state = Tile.STATE_INACTIVE;
            booleanState.label = "Focus";
            booleanState.icon = ResourceIcon.get(R.drawable.ic_qs_focus_moon);
        }
    }

    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.ZEN_MODE_SETTINGS");
    }

    // Inner class: Detail Adapter
    public final class FocusDetailAdapter extends QSDetailAdapter 
            implements QSDetailContent.Callback {
        
        private QSDetailContent mDetailView;
        private List<FocusMode> mFocusModes;

        public FocusDetailAdapter(Context context) {
            super(context);
            initializeFocusModes();
        }

        private void initializeFocusModes() {
            mFocusModes = new ArrayList<>();
            
            // Do Not Disturb
            mFocusModes.add(new FocusMode(
                "dnd",
                R.string.focus_mode_dnd_name, // "Do Not Disturb"
                R.string.focus_mode_dnd_subtitle, // "Silence all notifications"
                R.drawable.ic_qs_focus_moon,
                R.color.focus_mode_dnd_bg // Blue background
            ));
            
            // Reduce Interruptions
            mFocusModes.add(new FocusMode(
                "priority",
                R.string.focus_mode_priority_name, // "Reduce Interruptions"
                R.string.focus_mode_priority_subtitle, // "Limit interruptions to only important notifications"
                R.drawable.ic_qs_focus_priority,
                R.color.focus_mode_priority_bg // Purple-ish background
            ));
            
            // Personal
            mFocusModes.add(new FocusMode(
                "personal",
                R.string.focus_mode_personal_name, // "Personal"
                R.string.focus_mode_personal_subtitle, // "Turn off work, focus on you"
                R.drawable.ic_qs_focus_personal,
                R.color.focus_mode_personal_bg // Gray background
            ));
            
            // Work
            mFocusModes.add(new FocusMode(
                "work",
                R.string.focus_mode_work_name, // "Work"
                R.string.focus_mode_work_subtitle, // "Get things done"
                R.drawable.ic_qs_focus_work,
                R.color.focus_mode_work_bg // Green background
            ));
            
            // Sleep
            mFocusModes.add(new FocusMode(
                "sleep",
                R.string.focus_mode_sleep_name, // "Sleep"
                R.string.focus_mode_sleep_subtitle, // "Relax and get some rest"
                R.drawable.ic_qs_focus_sleep,
                R.color.focus_mode_sleep_bg // Blue background
            ));
        }

        @Override
        public View createDetailView(Context context, View convertView, ViewGroup parent) {
            QSDetailContent.Companion.getClass();
            QSDetailContent detailContent = QSDetailContent.Companion.convertOrInflate(
                context, convertView, parent
            );
            
            this.mDetailView = detailContent;
            detailContent.setSuffix("focus");
            detailContent.setCallback(this);
            
            updateItems();
            return detailContent;
        }

        @Override
        public CharSequence getTitle() {
            return mContext.getString(R.string.focus_modes_title); // "Focus"
        }

        @Override
        public Boolean getToggleState() {
            FocusMode currentMode = mFocusModeController.getCurrentMode();
            return currentMode != null && currentMode.isActive;
        }

        @Override
        public void setToggleState(boolean state) {
            FocusMode currentMode = mFocusModeController.getCurrentMode();
            if (currentMode != null) {
                mFocusModeController.setModeActive(currentMode.id, state);
                updateItems();
                FocusTile.this.refreshState(null);
            }
        }

        @Override
        public Intent getSettingsIntent() {
            return new Intent("android.settings.ZEN_MODE_SETTINGS");
        }

        @Override
        public int getMetricsCategory() {
            return 118; // Same as DND tile
        }

        @Override
        public void onDetailItemClick(QSDetailContent.Item item) {
            Object tag = item.getTag();
            if (!(tag instanceof FocusMode)) {
                return;
            }
            
            FocusMode selectedMode = (FocusMode) tag;
            
            // Toggle the selected mode
            boolean newState = !selectedMode.isActive;
            mFocusModeController.setModeActive(selectedMode.id, newState);
            
            // Update the detail view
            updateItems();
            
            // Update the main tile
            FocusTile.this.refreshState(null);
            
            if (mDetailView != null) {
                mDetailView.setItemClicked(true);
            }
        }

        private void updateItems() {
            if (mDetailView == null) {
                return;
            }
            
            // Refresh mode states from controller
            for (FocusMode mode : mFocusModes) {
                mode.isActive = mFocusModeController.isModeActive(mode.id);
            }
            
            ArrayList<QSDetailContent.Item> items = new ArrayList<>();
            
            for (FocusMode mode : mFocusModes) {
                QSDetailContent.SelectableItem item = new QSDetailContent.SelectableItem();
                
                item.title = mContext.getString(mode.nameRes);
                item.summary = mContext.getString(mode.subtitleRes);
                item.iconRes = mode.iconRes;
                item.selected = mode.isActive;
                item.selectable = true;
                item.tag = mode;
                
                // Use mode-specific background color
                item.backgroundColorRes = mode.backgroundColorRes;
                
                // Add a small indicator icon when active (like iOS)
                if (mode.isActive) {
                    item.icon2Res = R.drawable.ic_qs_focus_mode_selected;
                }
                
                items.add(item);
            }
            
            mDetailView.setItems(items.toArray(new QSDetailContent.Item[0]));
        }
    }
}
```

### 2. FocusMode Data Model

```java
package com.android.systemui.qs.tiles;

public class FocusMode {
    public final String id;
    public final int nameRes;
    public final int subtitleRes;
    public final int iconRes;
    public final int backgroundColorRes;
    public boolean isActive;

    public FocusMode(String id, int nameRes, int subtitleRes, 
                     int iconRes, int backgroundColorRes) {
        this.id = id;
        this.nameRes = nameRes;
        this.subtitleRes = subtitleRes;
        this.iconRes = iconRes;
        this.backgroundColorRes = backgroundColorRes;
        this.isActive = false;
    }
}
```

### 3. FocusModeController

This controller manages the focus mode states and integrates with Android's DND system.

```java
package com.android.systemui.qs.tiles;

import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;

public class FocusModeController {
    private final Context mContext;
    private final NotificationManager mNotificationManager;
    private String mCurrentModeId;

    public FocusModeController(Context context) {
        mContext = context;
        mNotificationManager = context.getSystemService(NotificationManager.class);
    }

    public FocusMode getCurrentMode() {
        // Get the currently active focus mode
        int zenMode = mNotificationManager.getZenMode();
        
        if (zenMode == Settings.Global.ZEN_MODE_OFF) {
            return null;
        }
        
        // Map zen mode to focus mode ID
        // You can store the current mode in SharedPreferences
        String modeId = Settings.Secure.getString(
            mContext.getContentResolver(), 
            "focus_mode_current"
        );
        
        if (modeId == null) {
            modeId = "dnd"; // Default
        }
        
        return getFocusModeById(modeId);
    }

    public boolean isModeActive(String modeId) {
        int zenMode = mNotificationManager.getZenMode();
        
        if (zenMode == Settings.Global.ZEN_MODE_OFF) {
            return false;
        }
        
        String currentModeId = Settings.Secure.getString(
            mContext.getContentResolver(), 
            "focus_mode_current"
        );
        
        return modeId.equals(currentModeId);
    }

    public void setModeActive(String modeId, boolean active) {
        if (active) {
            // Activate the focus mode
            activateMode(modeId);
        } else {
            // Deactivate all focus modes
            mNotificationManager.setZenMode(
                Settings.Global.ZEN_MODE_OFF, 
                null, 
                "FocusTile"
            );
            
            Settings.Secure.putString(
                mContext.getContentResolver(), 
                "focus_mode_current", 
                null
            );
        }
    }

    private void activateMode(String modeId) {
        // Store current mode
        Settings.Secure.putString(
            mContext.getContentResolver(), 
            "focus_mode_current", 
            modeId
        );
        
        // Apply appropriate zen mode based on focus mode
        int zenMode;
        switch (modeId) {
            case "dnd":
                zenMode = Settings.Global.ZEN_MODE_ALARMS;
                break;
            case "priority":
                zenMode = Settings.Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS;
                break;
            case "personal":
            case "work":
            case "sleep":
                zenMode = Settings.Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS;
                // You can customize zen rules for each mode
                break;
            default:
                zenMode = Settings.Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS;
        }
        
        mNotificationManager.setZenMode(zenMode, null, "FocusTile");
    }

    private FocusMode getFocusModeById(String modeId) {
        // Create and return the appropriate FocusMode
        // This should match the modes defined in FocusDetailAdapter
        switch (modeId) {
            case "dnd":
                return new FocusMode(
                    "dnd",
                    R.string.focus_mode_dnd_name,
                    R.string.focus_mode_dnd_subtitle,
                    R.drawable.ic_qs_focus_moon,
                    R.color.focus_mode_dnd_bg
                );
            // Add other cases...
            default:
                return null;
        }
    }
}
```

## Resources Required

### 1. Strings (res/values/strings.xml)

```xml
<!-- Focus Mode Strings -->
<string name="focus_modes_title">Focus</string>

<!-- Do Not Disturb -->
<string name="focus_mode_dnd_name">Do Not Disturb</string>
<string name="focus_mode_dnd_subtitle">Silence all notifications</string>

<!-- Priority -->
<string name="focus_mode_priority_name">Reduce Interruptions</string>
<string name="focus_mode_priority_subtitle">Limit interruptions to only important notifications</string>

<!-- Personal -->
<string name="focus_mode_personal_name">Personal</string>
<string name="focus_mode_personal_subtitle">Turn off work, focus on you</string>

<!-- Work -->
<string name="focus_mode_work_name">Work</string>
<string name="focus_mode_work_subtitle">Get things done</string>

<!-- Sleep -->
<string name="focus_mode_sleep_name">Sleep</string>
<string name="focus_mode_sleep_subtitle">Relax and get some rest</string>
```

### 2. Colors (res/values/colors.xml)

```xml
<!-- Focus Mode Background Colors (iOS-style) -->
<color name="focus_mode_dnd_bg">#4A7D9D</color>          <!-- Blue -->
<color name="focus_mode_priority_bg">#8B7D9D</color>     <!-- Purple-ish -->
<color name="focus_mode_personal_bg">#7D8B9D</color>     <!-- Gray-blue -->
<color name="focus_mode_work_bg">#5D9D7D</color>         <!-- Green -->
<color name="focus_mode_sleep_bg">#5D7D9D</color>        <!-- Blue -->
```

### 3. Icons (res/drawable/)

You'll need to create or source these icons:
- `ic_qs_focus_moon.xml` - Moon icon for DND
- `ic_qs_focus_priority.xml` - Notification icon with filter
- `ic_qs_focus_personal.xml` - Person icon
- `ic_qs_focus_work.xml` - Briefcase icon
- `ic_qs_focus_sleep.xml` - Bed icon
- `ic_qs_focus_mode_selected.xml` - Checkmark or indicator for selected mode

## Registration

### For System Tile (MiuiSystemUI)

Add to tile specs in the appropriate controller class.

### For Plugin Tile (MiuiSystemUIPlugin)

Add to `LocalMiuiQSTilePlugin.java`:

```java
private void initAllMiuiTilesPlugin() {
    // ... existing tiles ...
    
    FocusTile focusTile = new FocusTile(this.mSysUIContext, this.mPluginContext);
    this.mAllMiuiTilesMap.put(focusTile.getTileSpec(), focusTile);
}
```

## Key Behaviors

1. **Normal Click**: Toggles the current focus mode on/off
2. **Long Press / Secondary Click**: Opens detail view with all focus modes
3. **Detail View Click**: Activates/deactivates the selected focus mode
4. **Tile Display**: Shows icon and name of currently active mode, or "Focus" if none active

## Technical Notes

### Important Methods

- `showDetail(true)` - Opens the detail panel
- `refreshState(null)` - Updates the tile state
- `getDetailAdapter()` - Must return your detail adapter instance
- `handleSecondaryClick()` - Called on long press in most launchers

### Detail View Features

The `QSDetailContent` class provides:
- Selectable items with titles and subtitles
- Icon support (primary and secondary icons)
- Background color customization per item
- Toggle switch in header
- Settings button

### Integration with Android DND

The implementation uses Android's NotificationManager and ZenMode APIs:
- `ZEN_MODE_OFF` - All focus modes off
- `ZEN_MODE_IMPORTANT_INTERRUPTIONS` - Priority mode
- `ZEN_MODE_ALARMS` - Complete silence except alarms

## Customization Options

1. **Card Style**: Modify to use card-style layout instead of list
2. **Animations**: Add transitions between modes
3. **Mode Profiles**: Save different notification filter profiles per mode
4. **Shortcuts**: Add quick actions to detail items (e.g., "Settings" button)
5. **Time-based**: Auto-enable modes based on time/calendar

## Testing

1. Build and install the modified APK
2. Long press the tile to see detail view
3. Test each focus mode activation
4. Verify notification filtering works
5. Check state persistence across reboots

## Next Steps

1. Create the required icon drawables
2. Add string and color resources
3. Implement the FocusTile class
4. Test in emulator or device
5. Refine UI to match iOS aesthetic
