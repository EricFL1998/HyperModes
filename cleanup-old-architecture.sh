#!/bin/bash
# Cleanup Old Architecture Script
# Run this after verifying UI bridge works correctly

echo "=== HyperModes Old Architecture Cleanup ==="
echo ""

# Step 1: Commit bridge and UI changes
echo "Step 1: Committing UI bridge changes..."
git add app/src/main/java/com/banana/hypermodes/bridge/ModeControlBridge.kt
git add app/src/main/java/com/banana/hypermodes/ui/ModeDetailScreen.kt
git add app/src/main/java/com/banana/hypermodes/ui/HyperModesApp.kt
git commit -m "refactor: create UI bridge to system_server

- Add ModeControlBridge for UI → Settings.Global → system_server control
- Update ModeDetailScreen to use bridge instead of ModeEngine
- Update HyperModesApp to use bridge for mode deactivation
- Prepare for old architecture cleanup

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"

echo ""
echo "Step 2: Deleting old engine files..."
git rm app/src/main/java/com/banana/hypermodes/engine/ModeEngine.kt
git rm app/src/main/java/com/banana/hypermodes/engine/EngineState.kt
git rm app/src/main/java/com/banana/hypermodes/engine/ModeScheduler.kt
git rm app/src/main/java/com/banana/hypermodes/engine/TimeChangedReceiver.kt

echo ""
echo "Step 3: Deleting old driving files..."
git rm app/src/main/java/com/banana/hypermodes/driving/DrivingDetector.kt
git rm app/src/main/java/com/banana/hypermodes/driving/BluetoothDrivingReceiver.kt
git rm app/src/main/java/com/banana/hypermodes/driving/ActivityTransitionReceiver.kt
git rm app/src/main/java/com/banana/hypermodes/driving/BootReceiver.kt

echo ""
echo "Step 4: Deleting old receiver files..."
git rm app/src/main/java/com/banana/hypermodes/receiver/BedtimeStateReceiver.kt

echo ""
echo "Step 5: Keep EngineReceiver (still used for alarm intents)"
echo "  → Keeping: app/src/main/java/com/banana/hypermodes/engine/EngineReceiver.kt"

echo ""
echo "Step 6: Cleaning up AndroidManifest.xml..."
echo "  → You need to manually remove these receiver declarations:"
echo "    - TimeChangedReceiver"
echo "    - BootReceiver"
echo "    - BluetoothDrivingReceiver"
echo "    - ActivityTransitionReceiver"
echo "    - BedtimeStateReceiver"

echo ""
echo "Step 7: Building to verify..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build successful!"
    echo ""
    echo "Step 8: Committing cleanup..."
    git commit -m "refactor: remove old App-process architecture

- Delete ModeEngine, EngineState, ModeScheduler (replaced by system_server)
- Delete DrivingDetector, driving receivers (replaced by DrivingTriggerManager)
- Delete BedtimeStateReceiver (replaced by BedtimeListener)
- Keep EngineReceiver (still needed for alarm intents)
- All mode logic now runs in system_server via zero-process architecture

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"

    echo ""
    echo "=== Cleanup Complete! ==="
    echo ""
    echo "Summary:"
    echo "  - Deleted: 9 old architecture files"
    echo "  - Kept: EngineReceiver.kt (alarm handling)"
    echo "  - Created: ModeControlBridge.kt (UI bridge)"
    echo ""
    echo "Next: Manually clean AndroidManifest.xml and push to GitHub"
else
    echo ""
    echo "❌ Build failed! Please check errors before proceeding."
    echo "Run 'git status' to see what was deleted."
    echo "Run 'git reset --hard' to undo if needed."
fi
