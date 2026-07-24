# iOS Focus-Style Tile Implementation Guide (Dynamic Modes)

## Overview
This guide explains how to create an iOS Focus-style tile for HyperOS that pulls focus modes from the actual Android system's AutomaticZenRules, displaying a compact view initially and expanding to show all configured focus modes on long press.

## Key Difference from Static Implementation
Instead of hardcoding focus modes, this implementation dynamically loads them from:
- **Android's AutomaticZenRules** - User-configured DND modes
- **System Zen Rules** - Bedtime, Driving, Theater, etc.

## Architecture

### Key Components

1. **FocusTile** - Main tile implementation
2. **FocusDetailAdapter** - Expandable detail view with dynamic focus modes
3. **FocusModeController** - Manages and loads actual system zen rules
4. **ZenRuleWrapper** - Wraps AutomaticZenRule for display

## Implementation

### 1. FocusModeController (Dynamic Rule Loading)

```java
package com.android.systemui.qs.tiles;

import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.service.notification.Condition;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FocusModeController {
    private static final String TAG = "FocusModeController";
    
    private final Context mContext;
    private final NotificationManager mNotificationManager;
    
    public FocusModeController(Context context) {
        mContext = context;
        mNotificationManager = context.getSystemService(NotificationManager.class);
    }

    /**
     * Get all configured automatic zen rules from the system
     */
    public List<ZenRuleWrapper> getAllFocusModes() {
        List<ZenRuleWrapper> modes = new ArrayList<>();
        
        try {
            Map<String, AutomaticZenRule> rules = mNotificationManager.getAutomaticZenRules();
            
            if (rules != null) {
                for (Map.Entry<String, AutomaticZenRule> entry : rules.entrySet()) {
                    String ruleId = entry.getKey();
                    AutomaticZenRule rule = entry.getValue();
                    
                    // Wrap the rule for easier display
                    ZenRuleWrapper wrapper = new ZenRuleWrapper(ruleId, rule, mContext);
                    
                    // Get the current active state
                    int state = mNotificationManager.getAutomaticZenRuleState(ruleId);
                    wrapper.isActive = (state == Condition.STATE_TRUE);
                    
                    modes.add(wrapper);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get automatic zen rules", e);
        }
        
        return modes;
    }

    /**
     * Get the currently active focus mode
     */
    public ZenRuleWrapper getCurrentMode() {
        int zenMode = mNotificationManager.getZenMode();
        
        // No zen mode active
        if (zenMode == android.provider.Settings.Global.ZEN_MODE_OFF) {
            return null;
        }
        
        // Find which rule is currently active
        List<ZenRuleWrapper> modes = getAllFocusModes();
        for (ZenRuleWrapper mode : modes) {
            if (mode.isActive) {
                return mode;
            }
        }
        
        return null;
    }

    /**
     * Toggle a specific focus mode on/off
     */
    public void setModeActive(String ruleId, boolean active) {
        try {
            AutomaticZenRule rule = mNotificationManager.getAutomaticZenRule(ruleId);
            if (rule == null) {
                Log.e(TAG, "Rule not found: " + ruleId);
                return;
            }
            
            if (active) {
                // Enable the rule
                if (!rule.isEnabled()) {
                    rule.setEnabled(true);
                    mNotificationManager.updateAutomaticZenRule(ruleId, rule, true);
                }
                
                // Activate the rule by setting its condition to true
                Uri conditionId = rule.getConditionId();
                if (conditionId != null) {
                    Condition activeCondition = new Condition(
                        conditionId,
                        rule.getName(),
                        Condition.STATE_TRUE
                    );
                    mNotificationManager.setAutomaticZenRuleState(ruleId, activeCondition);
                }
            } else {
                // Deactivate by setting condition to false
                Uri conditionId = rule.getConditionId();
                if (conditionId != null) {
                    Condition inactiveCondition = new Condition(
                        conditionId,
                        rule.getName(),
                        Condition.STATE_FALSE
                    );
                    mNotificationManager.setAutomaticZenRuleState(ruleId, inactiveCondition);
                }
            }
            
            Log.d(TAG, "Set mode " + ruleId + " to " + active);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set mode state", e);
        }
    }

    /**
     * Check if a specific rule is currently active
     */
    public boolean isModeActive(String ruleId) {
        try {
            int state = mNotificationManager.getAutomaticZenRuleState(ruleId);
            return state == Condition.STATE_TRUE;
        } catch (Exception e) {
            Log.e(TAG, "Failed to get rule state", e);
            return false;
        }
    }
    
    /**
     * Check if any zen mode is currently active
     */
    public boolean isAnyModeActive() {
        return mNotificationManager.getZenMode() != android.provider.Settings.Global.ZEN_MODE_OFF;
    }
}
```

### 2. ZenRuleWrapper (Display Helper)

```java
package com.android.systemui.qs.tiles;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.graphics.drawable.Icon;
import com.android.systemui.R;

/**
 * Wrapper around AutomaticZenRule to make it easier to display in the tile
 */
public class ZenRuleWrapper {
    public final String id;
    public final String name;
    public final String description;
    public final int iconRes;
    public final int type;
    public boolean isActive;
    
    private final AutomaticZenRule mRule;
    private final Context mContext;

    public ZenRuleWrapper(String id, AutomaticZenRule rule, Context context) {
        this.id = id;
        this.mRule = rule;
        this.mContext = context;
        this.name = rule.getName();
        this.type = rule.getType();
        
        // Get description from trigger description or generate one
        String triggerDesc = rule.getTriggerDescription();
        this.description = (triggerDesc != null && !triggerDesc.isEmpty()) 
            ? triggerDesc 
            : getDefaultDescription(type);
        
        // Map icon based on type
        this.iconRes = getIconForType(type);
    }

    public AutomaticZenRule getRule() {
        return mRule;
    }

    /**
     * Get icon resource based on rule type
     */
    private int getIconForType(int type) {
        switch (type) {
            case AutomaticZenRule.TYPE_BEDTIME:
                return R.drawable.ic_qs_focus_sleep; // Bed icon
            case AutomaticZenRule.TYPE_DRIVING:
                return R.drawable.ic_qs_focus_driving; // Car icon
            case AutomaticZenRule.TYPE_THEATER:
                return R.drawable.ic_qs_focus_theater; // Theater icon
            case AutomaticZenRule.TYPE_IMMERSIVE:
                return R.drawable.ic_qs_focus_immersive; // Immersive icon
            case AutomaticZenRule.TYPE_SCHEDULE_TIME:
            case AutomaticZenRule.TYPE_SCHEDULE_CALENDAR:
                return R.drawable.ic_qs_focus_schedule; // Calendar/clock icon
            case AutomaticZenRule.TYPE_MANAGED:
                return R.drawable.ic_qs_focus_work; // Work icon
            case AutomaticZenRule.TYPE_OTHER:
            case AutomaticZenRule.TYPE_UNKNOWN:
            default:
                return R.drawable.ic_qs_focus_moon; // Default moon icon
        }
    }

    /**
     * Get background color based on rule type
     */
    public int getBackgroundColor() {
        switch (type) {
            case AutomaticZenRule.TYPE_BEDTIME:
                return R.color.focus_mode_sleep_bg; // Blue
            case AutomaticZenRule.TYPE_DRIVING:
                return R.color.focus_mode_driving_bg; // Orange
            case AutomaticZenRule.TYPE_THEATER:
                return R.color.focus_mode_theater_bg; // Purple
            case AutomaticZenRule.TYPE_IMMERSIVE:
                return R.color.focus_mode_immersive_bg; // Dark
            case AutomaticZenRule.TYPE_MANAGED:
                return R.color.focus_mode_work_bg; // Green
            case AutomaticZenRule.TYPE_SCHEDULE_TIME:
            case AutomaticZenRule.TYPE_SCHEDULE_CALENDAR:
                return R.color.focus_mode_schedule_bg; // Gray
            default:
                return R.color.focus_mode_dnd_bg; // Blue
        }
    }

    /**
     * Get default description based on type
     */
    private String getDefaultDescription(int type) {
        switch (type) {
            case AutomaticZenRule.TYPE_BEDTIME:
                return mContext.getString(R.string.focus_mode_bedtime_desc);
            case AutomaticZenRule.TYPE_DRIVING:
                return mContext.getString(R.string.focus_mode_driving_desc);
            case AutomaticZenRule.TYPE_THEATER:
                return mContext.getString(R.string.focus_mode_theater_desc);
            case AutomaticZenRule.TYPE_IMMERSIVE:
                return mContext.getString(R.string.focus_mode_immersive_desc);
            case AutomaticZenRule.TYPE_MANAGED:
                return mContext.getString(R.string.focus_mode_work_desc);
            case AutomaticZenRule.TYPE_SCHEDULE_TIME:
            case AutomaticZenRule.TYPE_SCHEDULE_CALENDAR:
                return mContext.getString(R.string.focus_mode_schedule_desc);
            default:
                return mContext.getString(R.string.focus_mode_default_desc);
        }
    }
}
```

### 3. FocusTile (Main Tile)

```java
package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R;
import com.android.systemui.animation.Expandable;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.QSDetailContent;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.QsEventLoggerImpl;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.qs.tileimpl.QSDetailAdapter;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import java.util.ArrayList;
import java.util.List;

public final class FocusTile extends QSTileImpl {
    private static final String TAG = "FocusTile";
    
    private final FocusDetailAdapter mDetailAdapter;
    private final FocusModeController mFocusModeController;

    public FocusTile(QSHost host, QsEventLoggerImpl qsEventLoggerImpl, 
                     Looper looper, Handler handler, FalsingManager falsingManager, 
                     MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, 
                     ActivityStarter activityStarter, QSLogger qSLogger) {
        super(host, qsEventLoggerImpl, looper, handler, falsingManager, metricsLogger, 
              statusBarStateController, activityStarter, qSLogger);
        
        this.mFocusModeController = new FocusModeController(this.mContext);
        this.mDetailAdapter = new FocusDetailAdapter(this.mContext);
    }

    @Override
    public QSTile.State newTileState() {
        return new QSTile.BooleanState();
    }

    @Override
    public CharSequence getTileLabel() {
        ZenRuleWrapper currentMode = mFocusModeController.getCurrentMode();
        return currentMode != null ? currentMode.name : mContext.getString(R.string.focus_tile_label);
    }

    @Override
    protected void handleClick(Expandable expandable) {
        // Toggle current focus mode on/off
        ZenRuleWrapper currentMode = mFocusModeController.getCurrentMode();
        
        if (currentMode != null) {
            // Turn off current mode
            mFocusModeController.setModeActive(currentMode.id, false);
        } else {
            // No mode active, show detail to choose one
            showDetail(true);
        }
        
        refreshState(null);
    }

    @Override
    protected void handleSecondaryClick(Expandable expandable) {
        // Show detail view with all focus modes
        showDetail(true);
    }

    @Override
    protected void handleLongClick(Expandable expandable) {
        // Also show detail view on long press (iOS behavior)
        showDetail(true);
    }

    @Override
    public Object getDetailAdapter() {
        return mDetailAdapter;
    }

    @Override
    protected void handleUpdateState(QSTile.State state, Object arg) {
        QSTile.BooleanState booleanState = (QSTile.BooleanState) state;
        
        ZenRuleWrapper currentMode = mFocusModeController.getCurrentMode();
        boolean isActive = mFocusModeController.isAnyModeActive();
        
        booleanState.value = isActive;
        booleanState.state = isActive ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;
        
        if (currentMode != null) {
            booleanState.label = currentMode.name;
            booleanState.icon = ResourceIcon.get(currentMode.iconRes);
            booleanState.secondaryLabel = currentMode.description;
        } else {
            booleanState.label = mContext.getString(R.string.focus_tile_label);
            booleanState.icon = ResourceIcon.get(R.drawable.ic_qs_focus_moon);
            booleanState.secondaryLabel = null;
        }
        
        booleanState.contentDescription = booleanState.label;
    }

    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.ZEN_MODE_SETTINGS");
    }

    @Override
    public int getMetricsCategory() {
        return 118; // Same as DND tile
    }

    // Inner class: Detail Adapter
    public final class FocusDetailAdapter extends QSDetailAdapter 
            implements QSDetailContent.Callback {
        
        private QSDetailContent mDetailView;

        public FocusDetailAdapter(Context context) {
            super(context);
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
            return mContext.getString(R.string.focus_modes_title);
        }

        @Override
        public Boolean getToggleState() {
            return mFocusModeController.isAnyModeActive();
        }

        @Override
        public void setToggleState(boolean state) {
            if (state) {
                // Turn on - user needs to pick a mode, do nothing
                // Or activate the last used mode
            } else {
                // Turn off all modes
                ZenRuleWrapper currentMode = mFocusModeController.getCurrentMode();
                if (currentMode != null) {
                    mFocusModeController.setModeActive(currentMode.id, false);
                    updateItems();
                    FocusTile.this.refreshState(null);
                }
            }
        }

        @Override
        public Intent getSettingsIntent() {
            return new Intent("android.settings.ZEN_MODE_SETTINGS");
        }

        @Override
        public int getMetricsCategory() {
            return 118;
        }

        @Override
        public void onDetailItemClick(QSDetailContent.Item item) {
            Object tag = item.getTag();
            if (!(tag instanceof ZenRuleWrapper)) {
                return;
            }
            
            ZenRuleWrapper selectedMode = (ZenRuleWrapper) tag;
            
            // If this mode is active, turn it off
            // If inactive, turn off others and turn this on
            boolean newState = !selectedMode.isActive;
            
            if (newState) {
                // Turn off all other modes first
                List<ZenRuleWrapper> allModes = mFocusModeController.getAllFocusModes();
                for (ZenRuleWrapper mode : allModes) {
                    if (mode.isActive && !mode.id.equals(selectedMode.id)) {
                        mFocusModeController.setModeActive(mode.id, false);
                    }
                }
            }
            
            // Toggle the selected mode
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
            
            // Get all focus modes from system
            List<ZenRuleWrapper> modes = mFocusModeController.getAllFocusModes();
            
            if (modes.isEmpty()) {
                // No modes configured, show empty state
                mDetailView.setEmptyState(
                    R.drawable.ic_qs_focus_empty,
                    R.string.focus_modes_empty_message
                );
                mDetailView.setItems(new QSDetailContent.Item[0]);
                return;
            }
            
            ArrayList<QSDetailContent.Item> items = new ArrayList<>();
            
            for (ZenRuleWrapper mode : modes) {
                QSDetailContent.SelectableItem item = new QSDetailContent.SelectableItem();
                
                item.title = mode.name;
                item.summary = mode.description;
                item.iconRes = mode.iconRes;
                item.selected = mode.isActive;
                item.selectable = true;
                item.tag = mode;
                
                // Use mode-specific background color
                item.backgroundColorRes = mode.getBackgroundColor();
                
                // Add checkmark when active (iOS style)
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

## Resources Required

### 1. Strings (res/values/strings.xml)

```xml
<!-- Focus Mode Strings -->
<string name="focus_tile_label">Focus</string>
<string name="focus_modes_title">Focus Modes</string>
<string name="focus_modes_empty_message">No focus modes configured.\nCreate modes in Settings > Sound &amp; vibration > Do Not Disturb</string>

<!-- Default descriptions for rule types -->
<string name="focus_mode_bedtime_desc">Relax and get some rest</string>
<string name="focus_mode_driving_desc">Stay focused on the road</string>
<string name="focus_mode_theater_desc">Enjoy the show</string>
<string name="focus_mode_immersive_desc">Fully immerse yourself</string>
<string name="focus_mode_work_desc">Get things done</string>
<string name="focus_mode_schedule_desc">Automatically activates on schedule</string>
<string name="focus_mode_default_desc">Custom focus mode</string>
```

### 2. Colors (res/values/colors.xml)

```xml
<!-- Focus Mode Background Colors (iOS-style, semi-transparent) -->
<color name="focus_mode_dnd_bg">#4A7D9D</color>          <!-- Blue -->
<color name="focus_mode_sleep_bg">#5D7D9D</color>        <!-- Blue -->
<color name="focus_mode_driving_bg">#9D7D4A</color>      <!-- Orange -->
<color name="focus_mode_theater_bg">#8B7D9D</color>      <!-- Purple -->
<color name="focus_mode_immersive_bg">#6D7D8D</color>    <!-- Dark -->
<color name="focus_mode_work_bg">#5D9D7D</color>         <!-- Green -->
<color name="focus_mode_schedule_bg">#7D8B9D</color>     <!-- Gray-blue -->
<color name="focus_mode_personal_bg">#7D8B9D</color>     <!-- Gray -->
```

### 3. Icons (res/drawable/)

Required icon resources:
- `ic_qs_focus_moon.xml` - Moon icon (default/DND)
- `ic_qs_focus_sleep.xml` - Bed icon (bedtime)
- `ic_qs_focus_driving.xml` - Car icon (driving)
- `ic_qs_focus_theater.xml` - Theater/masks icon
- `ic_qs_focus_immersive.xml` - VR/immersive icon
- `ic_qs_focus_work.xml` - Briefcase icon (managed)
- `ic_qs_focus_schedule.xml` - Calendar/clock icon
- `ic_qs_focus_mode_selected.xml` - Checkmark indicator
- `ic_qs_focus_empty.xml` - Empty state icon

## Key Features

### ✅ Dynamic Mode Loading
- Reads all configured AutomaticZenRules from the system
- No hardcoded modes - displays whatever the user has configured
- Automatically shows new modes when user creates them in Settings

### ✅ Mode Type Detection
- Detects rule type (Bedtime, Driving, Theater, Work, etc.)
- Displays appropriate icon and color for each type
- Falls back to default styling for custom modes

### ✅ iOS-Style Behavior
- Compact tile shows current active mode
- Long press expands to show all available modes
- Tap a mode to activate it (turns off others)
- Tap active mode to turn it off
- Header toggle to turn off all modes

### ✅ Live Updates
- Monitors zen mode changes
- Updates when modes are activated/deactivated from other sources
- Refreshes when new modes are added in Settings

## Usage Notes

1. **Empty State**: If no AutomaticZenRules are configured, shows a message directing users to Settings
2. **Manual Rule**: The MANUAL_RULE is typically excluded as it's the system's manual DND toggle
3. **Rule States**: Uses `getAutomaticZenRuleState()` to check if a rule is currently active
4. **Condition Setting**: Activates rules by setting their condition state to TRUE/FALSE

## Testing

1. Go to Settings > Sound & vibration > Do Not Disturb
2. Create some automatic rules (e.g., Bedtime schedule)
3. Long press the Focus tile
4. Verify all configured modes appear
5. Tap a mode to activate it
6. Verify notification filtering works
7. Test activating from Settings also updates the tile

## Advanced: Listening for Changes

To make the tile update when modes change externally, add a BroadcastReceiver:

```java
private final BroadcastReceiver mZenModeReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        refreshState(null);
        if (mShowingDetail) {
            mDetailAdapter.updateItems();
        }
    }
};

@Override
protected void handleSetListening(boolean listening) {
    if (listening) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED);
        filter.addAction(NotificationManager.ACTION_AUTOMATIC_ZEN_RULE_STATUS_CHANGED);
        mContext.registerReceiver(mZenModeReceiver, filter);
    } else {
        mContext.unregisterReceiver(mZenModeReceiver);
    }
}
```

## Next Steps

1. Implement the three classes (FocusTile, FocusModeController, ZenRuleWrapper)
2. Add required resources (strings, colors, icons)
3. Register the tile in the appropriate QS controller
4. Test with different rule types
5. Refine UI to match iOS aesthetic
