# OS4 vs OS3 Lockscreen & Desktop Wallpaper Internals Research

- OS4 source root: `E:/work/Android Project/_reverse-eng-archive/os4_android17_apks`
- OS3 source root: `E:/work/Android Project/_reverse-eng-archive/apk_decompiled`
- Generated: 2026-08-24
- Method: ripgrep over decompiled frameworks/services/apps

## 1. Settings.Secure Keys

### 1.1 OS4 Java references

#### `constant_lockscreen_info`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-301-                }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-302-                WallpaperManager wallmanager2 = (WallpaperManager) this.mContext.getSystemService(GreezeReason.WALLPAPER);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-303-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java:304:            Settings.Secure.putString(this.mContext.getContentResolver(), "constant_lockscreen_info", "null");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-305-            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-306-            if (wifiManager != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-307-                List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-504-        JSONObject jSONObjectOptJSONObject;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-505-        JSONObject jSONObjectOptJSONObject2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-506-        boolean zIsOS3KeyguardEditorAtLeast = SystemUtil.isOS3KeyguardEditorAtLeast(context);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java:507:        String string = Settings.Secure.getString(context.getContentResolver(), zIsOS3KeyguardEditorAtLeast ? "constant_template_editor_info" : "constant_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-508-        if (string != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-509-            try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-510-                if (zIsOS3KeyguardEditorAtLeast) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\safemode\SafemodeController.java-218-        ((AODSafemodeListener) InterfacesImplManager.getImpl(AODSafemodeListener.class)).onSafemodeStart(this.context);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\safemode\SafemodeController.java-219-        ((KeyguardSafemodeListener) InterfacesImplManager.getImpl(KeyguardSafemodeListener.class)).onSafemodeStart(this.context);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\safemode\SafemodeController.java-220-        ((KeyguardPanelSafemodeListener) InterfacesImplManager.getImpl(KeyguardPanelSafemodeListener.class)).onSafemodeStart(this.context);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\safemode\SafemodeController.java:221:        Settings.Secure.putStringForUser(this.context.getContentResolver(), "constant_lockscreen_info", "{\"clockInfo\":{\"classicLine1\":11,\"classicLine2\":300,\"classicLine3\":207,\"classicLine4\":200,\"classicLine5\":12,\"classicSignature\":\"\",\"enableDiffusion\":false,\"isAutoPrimaryColor\":false,\"isAutoSecondaryColor\":false,\"isDiffHourMinuteColor\":false,\"primaryColor\":-1,\"secondaryColor\":0,\"style\":21,\"templateId\":\"classic\"}}", -2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\safemode\SafemodeController.java-222-        if (this.safemodeReceiver == null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\safemode\SafemodeController.java-223-            this.safemodeReceiver = new SafemodeReceiver(this.context);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\safemode\SafemodeController.java-224-        }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-185-            ((Integer) lockscreenInfo2.first).getClass();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-186-            return (String) lockscreenInfo2.second;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-187-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:188:        String stringForUser = SettingsCompat$Secure.getStringForUser(this.mContext.getContentResolver(), "constant_lockscreen_info", this.mCurrentUserId);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-189-        MiuiFullAodManager$$ExternalSyntheticOutline0.m(this.mCurrentUserId, "LockScreenInfoLayout", new StringBuilder("getStringForUser, mCurrentUserId = "));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-190-        return stringForUser;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-191-    }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-441-                this.mEditorVersion = ((Integer) lockscreenInfo.first).intValue();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-442-                return (String) lockscreenInfo.second;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-443-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:444:            String stringForUser = SettingsCompat.Secure.getStringForUser(this.mContext.getContentResolver(), "constant_lockscreen_info", this.mCurrentUserId);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-445-            Log.i("LockScreenInfoLayout", "getStringForUser, mCurrentUserId = " + this.mCurrentUserId);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-446-            return stringForUser;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-447-        }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-1122-        String str2 = "";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-1123-        String str3 = this.TAG;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-1124-        if (!z) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java:1125:            return Settings.Secure.getString(this.mContext.getContentResolver(), "constant_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-1126-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-1127-        try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-1128-            String constantTemplateEditorInfo = getConstantTemplateEditorInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-1129-            if (constantTemplateEditorInfo == null || constantTemplateEditorInfo.isEmpty()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java:1130:                str = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, this.mContext.getContentResolver(), "constant_lockscreen_info", Integer.valueOf(this.mCurrentUserId));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-1131-            } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-1132-                try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-1133-                    JSONObject jSONObject = new JSONObject(constantTemplateEditorInfo);
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImpl.java-4946-        TemplateApiImpl templateApiImpl = this;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImpl.java-4947-        Log.i("Keyguard-Editor-TemplateApiImpl", "getCurrentTemplateConfigInternal: requestGalleryContent = " + z);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImpl.java-4948-        String templateInfo = templateApiImpl.readTemplateInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImpl.java:4949:        String string = Settings.Secure.getString(templateApiImpl.context.getContentResolver(), "constant_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImpl.java-4950-        if ((templateInfo == null || templateInfo.length() == 0) && (string == null || string.length() == 0)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImpl.java-4951-            Log.d("Keyguard-Editor-TemplateApi
... (truncated)
```

#### `constant_template_editor_info`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-252-        } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-253-            iEventTracker3.track(new KeyguardUnlockWayStatusWithoutFingerEvent(companion2.getSecureType(), companion2.getDisplayPatternValue(), companion2.getDarkFingerprintUnlockValue(keyguardStat.context), companion2.getSideFingerprintUnlockWay(keyguardStat.context), companion2.getOpenFingerprintUnlockValue(keyguardStat.context), companion2.getFingerprintPrivacyPasswordValue(keyguardStat.context), companion2.getOpenFingerprintUnlockAppValue(keyguardStat.context), companion2.getGxzwAnim(keyguardStat.context), companion2.getOpenVibrationSwitchValue(keyguardStat.context), companion2.getShowFingerprintAfterSleepValue(keyguardStat.context), companion2.getFodQuickOpenValue(keyguardStat.context), companion2.getOpenFaceUnlockValue(keyguardStat.context), companion2.getFaceNum(keyguardStat.context), companion2.getStayKeyguardAfterFaceUnlockValue(keyguardStat.context), companion2.getFaceUnlockNotificationValue(keyguardStat.context), companion2.getHideNotificationContentBeforeFaceUnlockValue(keyguardStat.context), companion2.getOpenBlueUnlockValue(keyguardStat.context), EventConstantsKt.EVENT_KEYGUARD_UNLOCK_WAY_STATUS_TIP, numValueOf3));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-254-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java:255:        String stringForUser = Settings.Secure.getStringForUser(keyguardStat.context.getContentResolver(), "constant_template_editor_info", -2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-256-        if (stringForUser != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-257-            TrackTemplateEditorInfo trackTemplateEditorInfo = (TrackTemplateEditorInfo) new Gson().fromJson(TrackTemplateEditorInfo.class, stringForUser);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-258-            LockscreenInfo lockscreenInfo2 = trackTemplateEditorInfo.getLockscreenInfo();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-157-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-158-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-159-    private String getConstantTemplateEditorInfo() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:160:        String stringForUser = SettingsCompat$Secure.getStringForUser(this.mContext.getContentResolver(), "constant_template_editor_info", this.mCurrentUserId);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-161-        WallpaperProvider$$ExternalSyntheticOutline0.m(this.mCurrentUserId, "LockScreenInfoLayout", ActivityResultRegistry$$ExternalSyntheticOutline0.m("templateEditorInfo=", stringForUser, ", mCurrentUserId = "));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-162-        return stringForUser;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-163-    }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-171-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-172-    private String getJson() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-173-        if (!isSystemUI()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:174:            String string = Settings.Secure.getString(this.mContext.getContentResolver(), "constant_template_editor_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-175-            if (string == null || string.isEmpty()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:176:                return Settings.Secure.getString(this.mContext.getContentResolver(), "constant_template_editor_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-177-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-178-            Pair lockscreenInfo = getLockscreenInfo(string);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-179-            ((Integer) lockscreenInfo.first).getClass();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-430-        boolean zIsSystemUI = isSystemUI();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-431-        KeyguardDepthInteractor$updateAvoidStatus$1$$ExternalSyntheticOutline0.m("registerClockBeanListener isSystemUI = ", "LockScreenInfoLayout", zIsSystemUI);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-432-        if (!zIsSystemUI) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:433:            this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("constant_template_editor_info"), false, this.mListener);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-434-            return;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-435-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-436-        this.mUserSwitchBroadcastReceiver = new BroadcastReceiver() { // from class: com.miui.lockscreeninfo.LockScreenInfoLayout.1
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-461-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-462-        UserHandle userHandle = (UserHandle) objNewInstance;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-463-        this.mUserAllHandle = userHandle;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:464:        ReflectUtils.invokeObject(ContentResolver.class, this.mContext.getContentResolver(), "registerContentObserverAsUser", Void.TYPE, new Class[]{Uri.class, Boolean.TYPE, ContentObserver.class, UserHandle.class}, Settings.Secure.getUriFor("constant_template_editor_info"), Boolean.FALSE, this.mListener, this.mUserAllHandle);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-465-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-466-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-467-    public void setBackgroundBlurContainer(View view) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-776-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-777-            this.mModel = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-778-            this.signatureView.setText("");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:779:            Log.e("LockScreenInfoLayout", "ContentObserver fail, constant_template_editor_info value is Empty");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-780-        } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-781-            this.mModel = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-782-            this.signatureView.setText("");
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\clock\KeyguardClockContainer$mMiuiKeyguardUpdateMonitorCallback$1.java-76-        } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\clock\KeyguardClockContainer$mMiuiKeyguardUpdateMonitorCallback$1.java-77-            clockBean.setClockEffect(0);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\clock\KeyguardClockContainer$mMiuiKeyguardUpdateMonitorCallback$1.java-78-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\clock\KeyguardClockContainer$mMiuiKeyguardUpdateMonitorCallback$1.java:79:        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "constant_template_editor_info", -2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\clock\KeyguardClockContainer$mMiuiKeyguardUpdateMonitorCallback$1.java-80-        if (stringForUser != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\clock\KeyguardClockContainer$mMiuiKeyguardUpdateMonitorCallback$1.java-81-            try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_de
... (truncated)
```

#### `lockscreen_info_version`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-81-                    z = true;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-82-                } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-83-                    if (Settings.Secure.getStringForUser(keyguardOTAInteractor.context.getContentResolver(), "wallpaper_changed_2", iUserTracker.getUserId()) == null && Settings.Secure.getIntForUser(keyguardOTAInteractor.context.getContentResolver(), "wallpaper_effect_type_2", 0, iUserTracker.getUserId()) == 0) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java:84:                        Object obj2 = (Settings.Secure.getIntForUser(keyguardOTAInteractor.context.getContentResolver(), "lockscreen_info_version", 1, iUserTracker.getUserId()) == 2 && MiuiConfigs.IS_REDMI_BRAND && !Build.IS_INTERNATIONAL_BUILD) ? "classic_plus" : "classic";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-85-                        if (obj2.equals("classic")) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-86-                            ClockBean clockInfo = constantLockscreenInfoConvertJsonToConstantLockInfo.getClockInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-87-                            int classicLine1 = clockInfo.getClassicLine1();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-103-                keyguardOTAInteractor.writeEditorInfoIntoSettings(KeyguardOTAInteractor.generateEditorInfo(z ? strBuildLockInfo : keyguardOTAInteractor.preProcessTemplateForOS4(Settings.Secure.getStringForUser(keyguardOTAInteractor.context.getContentResolver(), "constant_lockscreen_info", iUserTracker.getUserId()))));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-104-                keyguardOTAInteractor.updateDefaultLockInfo(strBuildLockInfo);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-105-                stringForUser = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java:106:            } else if (!z2 || Settings.Secure.getIntForUser(keyguardOTAInteractor.context.getContentResolver(), "lockscreen_info_version", 1, iUserTracker.getUserId()) >= 4) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-107-                stringForUser = Settings.Secure.getStringForUser(keyguardOTAInteractor.context.getContentResolver(), "constant_template_editor_info", iUserTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-108-            } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-109-                keyguardOTAInteractor.updateDefaultLockInfo(keyguardOTAInteractor.buildLockInfo());
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-178-        ContentResolver contentResolver = this.context.getContentResolver();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-179-        IUserTracker iUserTracker = this.userTracker;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-180-        Settings.Secure.putStringForUser(contentResolver, "miui_15_default_lockscreen_info", str, iUserTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java:181:        Settings.Secure.putIntForUser(this.context.getContentResolver(), "lockscreen_info_version", 4, iUserTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-182-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-183-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-184-    public final void writeEditorInfoIntoSettings(String str) {
```

#### `miui_15_default_lockscreen_info`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6673-                    this.currentLockScreenInfo = str;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6674-                    if (Intrinsics.areEqual(this.constantLockscreenInfo.getClockInfo().getTemplateId(), "smart_frame")) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6675-                        KeyguardOTAInteractor keyguardOTAInteractor = this.keyguardOTAInteractor;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:6676:                        keyguardOTAInteractor.writeEditorInfoIntoSettings(KeyguardOTAInteractor.generateEditorInfo(Settings.Secure.getStringForUser(keyguardOTAInteractor.context.getContentResolver(), "miui_15_default_lockscreen_info", keyguardOTAInteractor.userTracker.getUserId())));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6677-                        Log.w("KeyguardOTAInteractor", "resetDefaultLockscreenInfo: constantTemplateEditorInfo has been reset to default.");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6678-                    } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6679-                        Handler handlerAccess$getMainHandler = Companion.access$getMainHandler();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-177-    public final void updateDefaultLockInfo(String str) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-178-        ContentResolver contentResolver = this.context.getContentResolver();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-179-        IUserTracker iUserTracker = this.userTracker;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java:180:        Settings.Secure.putStringForUser(contentResolver, "miui_15_default_lockscreen_info", str, iUserTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-181-        Settings.Secure.putIntForUser(this.context.getContentResolver(), "lockscreen_info_version", 4, iUserTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-182-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-183-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-70-        ClockBean clockBean = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-71-        if (DateFormatUtils.isCrossUser(context)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-72-            try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java:73:                string = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, context.getContentResolver(), "miui_15_default_lockscreen_info", Integer.valueOf(iUpdateCurrentUserId));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-74-            } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-75-                Log.e("ClockBean", "getStringForUser fail", e);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-76-                string = "";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-77-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-78-        } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java:79:            string = Settings.Secure.getString(context.getContentResolver(), "miui_15_default_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-80-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-81-        if (!TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-82-            try {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-402-        ClockBean clockBean = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-403-        if (DateFormatUtils.isCrossUser(context)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-404-            try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java:405:                string = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, context.getContentResolver(), "miui_15_default_lockscreen_info", Integer.valueOf(iUpdateCurrentUserId));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-406-            } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-407-                Log.e("ClockBean", "getStringForUser fail", e);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-408-                string = "";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-409-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-410-        } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java:411:            string = Settings.Secure.getString(context.getContentResolver(), "miui_15_default_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-412-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-413-        if (!TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-414-            try {
```

### 1.2 OS3 Java references

#### `constant_lockscreen_info`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-455-        JSONObject jSONObjectOptJSONObject;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-456-        JSONObject jSONObjectOptJSONObject2;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-457-        boolean zY = SystemUtil.y(context);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java:458:        String string = Settings.Secure.getString(context.getContentResolver(), zY ? "constant_template_editor_info" : "constant_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-459-        if (string != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-460-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-461-                if (zY) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-285-            this.f52013a = ((Integer) pairNi8.first).intValue();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-286-            return (String) pairNi8.second;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-287-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:288:        String qVar = SettingsCompat.Secure.toq(this.f52021s.getContentResolver(), "constant_lockscreen_info", this.f52015h);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-289-        Log.i(v, "getStringForUser, mCurrentUserId = " + this.f52015h);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-290-        return qVar;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-291-    }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java-28-        public static final String f52390q = "background_blur_enable";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java-29-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java-30-        /* JADX INFO: renamed from: toq, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java:31:        public static final String f52391toq = "constant_lockscreen_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java-32-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java-33-        /* JADX INFO: renamed from: zy, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java-34-        public static final String f52392zy = "constant_template_editor_info";
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-298-                }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-299-                WallpaperManager wallmanager2 = (WallpaperManager) this.mContext.getSystemService("wallpaper");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-300-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java:301:            Settings.Secure.putString(this.mContext.getContentResolver(), "constant_lockscreen_info", "null");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-302-            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-303-            if (wifiManager != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-304-                List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-111-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-112-    /* JADX INFO: renamed from: zy, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-113-    @NotNull
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:114:    private static final String f54262zy = "constant_lockscreen_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-115-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-116-    /* JADX INFO: renamed from: k, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-117-    @NotNull
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-280-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-281-            String json = SettingHelper.f54247i.toJson(templateBean);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-282-            Log.i(SettingHelper.f54259toq, "hasModifyLockData :" + z + " , updateTemplateInfoToSettings : " + json);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:283:            Settings.Secure.putString(AppContextManager.q().getContentResolver(), "constant_lockscreen_info", json);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-284-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-285-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-286-        public final boolean cdj() {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-430-                Log.d(SettingHelper.f54259toq, "os3 return");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-431-                return null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-432-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:433:            String string = Settings.Secure.getString(AppContextManager.q().getContentResolver(), "constant_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-434-            if (TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-435-                Log.d(SettingHelper.f54259toq, "get current lock screen info is null");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-436-                return null;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-547-        public final void zurt() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-548-            Log.d(SettingHelper.f54259toq, "resetLockScreenToDefault");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-549-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:550:                Settings.Secure.putString(AppContextManager.q().getContentResolver(), "constant_lockscreen_info", null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-551-                Settings.Secure.putString(AppContextManager.q().getContentResolver(), "constant_template_editor_info", null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-552-                AppContextManager.q().getContentResolver().call(Uri.parse("content://miui.keyguard.editor.templatefileprovider"), "callRefreshCurrentTemplate", (String) null, (Bundle) null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-553-            } catch (Exception e) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImplKt.java-48-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImplKt.java-49-    /* JADX INFO: renamed from: toq, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImplKt.java-50-    @NotNull
E:/work/Android Project/_reverse-en
... (truncated)
```

#### `constant_template_editor_info`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-419-        JSONObject jSONObjectOptJSONObject;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-420-        JSONObject jSONObjectOptJSONObject2;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-421-        boolean zIsOS3KeyguardEditorAtLeast = SystemUtil.isOS3KeyguardEditorAtLeast(context);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java:422:        String string = Settings.Secure.getString(context.getContentResolver(), zIsOS3KeyguardEditorAtLeast ? "constant_template_editor_info" : "constant_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-423-        if (string != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-424-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-425-                if (zIsOS3KeyguardEditorAtLeast) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-427-                setModelFromJson(json);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-428-            } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-429-                clear();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:430:                Log.e("LockScreenInfoLayout", "ContentObserver fail, constant_template_editor_info value is Empty");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-431-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-432-        } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-433-            clear();
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-447-            Log.i("LockScreenInfoLayout", "getStringForUser, mCurrentUserId = " + this.mCurrentUserId);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-448-            return stringForUser;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-449-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:450:        String string = Settings.Secure.getString(this.mContext.getContentResolver(), "constant_template_editor_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-451-        if (string != null && !string.isEmpty()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-452-            Pair lockscreenInfo2 = getLockscreenInfo(string);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-453-            this.mEditorVersion = ((Integer) lockscreenInfo2.first).intValue();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-454-            return (String) lockscreenInfo2.second;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-455-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:456:        return Settings.Secure.getString(this.mContext.getContentResolver(), "constant_template_editor_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-457-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-458-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-459-    private void setModelFromJson(String str) throws JSONException {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-526-            UserHandle userHandleNewInstance = UserHandleCompat.newInstance(-1);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-527-            this.mUserAllHandle = userHandleNewInstance;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-528-            ContextCompat.registerReceiverAsUser(this.mContext, this.mUserSwitchBroadcastReceiver, userHandleNewInstance, new IntentFilter("android.intent.action.USER_SWITCHED"), null, null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:529:            ContentResolverCompat.registerContentObserver(this.mContext.getContentResolver(), Settings.Secure.getUriFor("constant_template_editor_info"), false, this.mListener, this.mUserAllHandle);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-530-            return;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-531-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:532:        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("constant_template_editor_info"), false, this.mListener);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-533-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-534-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-535-    private void updateCurrentUserId() {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-776-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-777-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-778-    private String getConstantTemplateEditorInfo() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:779:        String stringForUser = SettingsCompat.Secure.getStringForUser(this.mContext.getContentResolver(), "constant_template_editor_info", this.mCurrentUserId);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-780-        Log.d("LockScreenInfoLayout", "templateEditorInfo=" + stringForUser + ", mCurrentUserId = " + this.mCurrentUserId);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-781-        return stringForUser;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-782-    }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-83-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-84-    /* JADX INFO: renamed from: q, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-85-    @NotNull
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:86:    private static final String f54255q = "constant_template_editor_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-87-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-88-    /* JADX INFO: renamed from: qrj, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-89-    @NotNull
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-297-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-298-            String json = SettingHelper.f54247i.toJson(templateBean);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-299-            Log.i(SettingHelper.f54259toq, "hasModifyLockData :" + z + " , updateTemplateInfoToSettings : " + json);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:300:            Settings.Secure.putString(AppContextManager.q().getContentResolver(), "constant_template_editor_info", json);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-301-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-302-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-303-        public final void fu4(boolean z) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-311-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-312-        @Nullable
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-313-        public
... (truncated)
```

#### `lockscreen_info_version`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-70-        this.KEY_LOCKSCREEN_INFO = "constant_lockscreen_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-71-        this.KEY_TEMPLATE_EDITOR_INFO = "constant_template_editor_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-72-        this.KEY_DEFAULT_LOCKSCREEN_INFO = "miui_15_default_lockscreen_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java:73:        this.KEY_LOCKSCREEN_INFO_VERSION = "lockscreen_info_version";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-74-        this.KEY_DESKTOP_SCROLL = "pref_key_wallpaper_screen_scrolled_span";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-75-        this.KEY_WALLPAPER_EFFECT_1 = "wallpaper_effect_type_1";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-76-        this.KEY_WALLPAPER_EFFECT_2 = "wallpaper_effect_type_2";
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\SystemModeHook.java-501-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\SystemModeHook.java-502-            bundle.putString(Protocol.EXTRA_TEMPLATE_EDITOR_JSON, editorInfo);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\SystemModeHook.java-503-            bundle.putString(Protocol.EXTRA_DEFAULT_LOCKSCREEN_JSON, Settings.Secure.getString(resolver, "miui_15_default_lockscreen_info"));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\SystemModeHook.java:504:            bundle.putInt(Protocol.EXTRA_LOCKSCREEN_VERSION, Settings.Secure.getInt(resolver, "lockscreen_info_version", 3));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\SystemModeHook.java-505-            File sysModeDir = new File("/data/system/hypermodes_backup/modes", modeId);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\SystemModeHook.java-506-            sysModeDir.mkdirs();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\SystemModeHook.java-507-            File file = new File(systemDir, "wallpaper_lock_orig");
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4419-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4420-    public final void updateDefaultLockScreenInfoInternal(String str) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4421-        Settings.Secure.putStringForUser(this.context.getContentResolver(), "miui_15_default_lockscreen_info", str, this.userTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:4422:        Settings.Secure.putIntForUser(this.context.getContentResolver(), "lockscreen_info_version", 3, this.userTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4423-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4424-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4425-    public final void updateKeyguardElementsExpansion(boolean z) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java-48-                if (stringForUser != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java-49-                    if (z) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java-50-                        KeyguardPanelViewController keyguardPanelViewController2 = keyguardPanelViewController;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java:51:                        if (Settings.Secure.getIntForUser(keyguardPanelViewController2.context.getContentResolver(), "lockscreen_info_version", 1, keyguardPanelViewController2.userTracker.getUserId()) < 3) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java-52-                            keyguardPanelViewController2.updateDefaultLockScreenInfoInternal(KeyguardPanelViewController.buildLockScreenInfoOS3());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java-53-                        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java-54-                        KeyguardPanelViewController.Companion companion = KeyguardPanelViewController.Companion;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java-75-                }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java-76-                if (constantLockscreenInfo != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java-77-                    if (Settings.Secure.getStringForUser(keyguardPanelViewController4.context.getContentResolver(), "wallpaper_changed_2", keyguardPanelViewController4.userTracker.getUserId()) == null && Settings.Secure.getIntForUser(keyguardPanelViewController4.context.getContentResolver(), "wallpaper_effect_type_2", 0, keyguardPanelViewController4.userTracker.getUserId()) == 0) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java:78:                        Object obj = (Settings.Secure.getIntForUser(keyguardPanelViewController4.context.getContentResolver(), "lockscreen_info_version", 1, keyguardPanelViewController4.userTracker.getUserId()) == 2 && MiuiConfigs.IS_REDMI_BRAND && !Build.IS_INTERNATIONAL_BUILD) ? "classic_plus" : "classic";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java-79-                        if (obj.equals("classic")) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java-80-                            ClockBean clockInfo = constantLockscreenInfo.getClockInfo();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1.java-81-                            if ((clockInfo.getClassicLine1() != 11 && clockInfo.getClassicLine1() != 101 && clockInfo.getClassicLine1() != 0) || clockInfo.getClassicLine2() != 300 || ((clockInfo.getClassicLine3() != 202 && clockInfo.getClassicLine3() != 210 && clockInfo.getClassicLine3() != 206) || ((clockInfo.getClassicLine4() != 400 && clockInfo.getClassicLine4() != 209 && clockInfo.getClassicLine4() != 200) || !clockInfo.isAutoPrimaryColor() || !clockInfo.isAutoSecondaryColor()))) {
```

#### `miui_15_default_lockscreen_info`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-57-        ClockBean clockBean = null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-58-        if (DateFormatUtils.isCrossUser(context)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-59-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java:60:                string = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, context.getContentResolver(), "miui_15_default_lockscreen_info", Integer.valueOf(iUpdateCurrentUserId));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-61-            } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-62-                Log.e("ClockBean", "getStringForUser fail", e);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-63-                string = "";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-64-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-65-        } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java:66:            string = Settings.Secure.getString(context.getContentResolver(), "miui_15_default_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-67-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-68-        if (!TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-69-            try {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-57-        ClockBean clockBean = null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-58-        if (DateFormatUtils.y(context)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-59-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java:60:                string = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, context.getContentResolver(), "miui_15_default_lockscreen_info", Integer.valueOf(iP));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-61-            } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-62-                Log.e("ClockBean", "getStringForUser fail", e);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-63-                string = "";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-64-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-65-        } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java:66:            string = Settings.Secure.getString(context.getContentResolver(), "miui_15_default_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-67-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-68-        if (!TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-69-            try {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3276-                    this.constantLockscreenInfo = (ConstantLockscreenInfo) new Gson().fromJson(ConstantLockscreenInfo.class, str);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3277-                    this.currentLockScreenInfo = str;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3278-                    if (Intrinsics.areEqual(this.constantLockscreenInfo.getClockInfo().getTemplateId(), "smart_frame")) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:3279:                        Settings.Secure.putStringForUser(this.context.getContentResolver(), "constant_template_editor_info", Settings.Secure.getStringForUser(this.context.getContentResolver(), "miui_15_default_lockscreen_info", this.userTracker.getUserId()), this.userTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3280-                    } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3281-                        Companion.access$getMainHandler().post(new Runnable() { // from class: com.android.keyguard.panel.KeyguardPanelViewController.onLockScreenInfoChange.1
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3282-                            @Override // java.lang.Runnable
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4418-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4419-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4420-    public final void updateDefaultLockScreenInfoInternal(String str) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:4421:        Settings.Secure.putStringForUser(this.context.getContentResolver(), "miui_15_default_lockscreen_info", str, this.userTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4422-        Settings.Secure.putIntForUser(this.context.getContentResolver(), "lockscreen_info_version", 3, this.userTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4423-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4424-
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-301-        ClockBean clockBean = null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-302-        if (DateFormatUtils.isCrossUser(context)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-303-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java:304:                string = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, context.getContentResolver(), "miui_15_default_lockscreen_info", Integer.valueOf(iUpdateCurrentUserId));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-305-            } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-306-                Log.e("ClockBean", "getStringForUser fail", e);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-307-                string = "";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-308-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-309-        } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java:310:            string = Settings.Secure.getString(context.getContentResolver(), "miui_15_default_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-311-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-312-        if (!TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-313-            try {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-69-        this.MARKER_LOCK_FOLLOWS_HOME = "lock_follows_home";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-70-        this.KEY_LOCKSCREEN_INFO = "constant_lockscreen_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-71-        this.KEY_TEMPLATE_EDITOR_INFO = "constant_template_editor_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java:72:        this.KEY_DEFAULT_LOCKSCREEN_INFO = "miui_15_default_lockscreen_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-73-        this.KEY_LOCKSCREEN_INFO_VERSION = "lockscreen_info_version";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-74-        this.KEY_DESKTOP_SCROLL = "pref_key_wallpaper_screen_s
... (truncated)
```

### 1.3 Additional OS4 lockscreen-related Secure keys

#### `lockscreen_info`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\android\app\admin\flags\FeatureFlagsImpl.java-83-            isMtePolicyEnforced = true;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\android\app\admin\flags\FeatureFlagsImpl.java-84-            keychainSuppressCertificateSelections = aconfigPackageLoad.getBooleanFlagValue("keychain_suppress_certificate_selections", false);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\android\app\admin\flags\FeatureFlagsImpl.java-85-            lockNowCoexistence = true;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\android\app\admin\flags\FeatureFlagsImpl.java:86:            lockscreenInfoCoexistence = aconfigPackageLoad.getBooleanFlagValue("lockscreen_info_coexistence", false);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\android\app\admin\flags\FeatureFlagsImpl.java-87-            managedEsimOutgoingTransferPolicy = aconfigPackageLoad.getBooleanFlagValue("managed_esim_outgoing_transfer_policy", false);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\android\app\admin\flags\FeatureFlagsImpl.java-88-            multiUserManagementDeviceProvisioning = aconfigPackageLoad.getBooleanFlagValue("multi_user_management_device_provisioning", false);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\android\app\admin\flags\FeatureFlagsImpl.java-89-            multiUserManagementUserProvisioning = aconfigPackageLoad.getBooleanFlagValue("multi_user_management_user_provisioning", false);
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\com\android\internal\hidden_from_bootclasspath\android\app\admin\flags\Flags.java-33-    public static final String FLAG_IS_MTE_POLICY_ENFORCED = "android.app.admin.flags.is_mte_policy_enforced";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\com\android\internal\hidden_from_bootclasspath\android\app\admin\flags\Flags.java-34-    public static final String FLAG_IS_RECURSIVE_REQUIRED_APP_MERGING_ENABLED = "android.app.admin.flags.is_recursive_required_app_merging_enabled";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\com\android\internal\hidden_from_bootclasspath\android\app\admin\flags\Flags.java-35-    public static final String FLAG_KEYCHAIN_SUPPRESS_CERTIFICATE_SELECTIONS = "android.app.admin.flags.keychain_suppress_certificate_selections";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\com\android\internal\hidden_from_bootclasspath\android\app\admin\flags\Flags.java:36:    public static final String FLAG_LOCKSCREEN_INFO_COEXISTENCE = "android.app.admin.flags.lockscreen_info_coexistence";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\com\android\internal\hidden_from_bootclasspath\android\app\admin\flags\Flags.java-37-    public static final String FLAG_LOCK_NOW_COEXISTENCE = "android.app.admin.flags.lock_now_coexistence";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\com\android\internal\hidden_from_bootclasspath\android\app\admin\flags\Flags.java-38-    public static final String FLAG_MANAGED_DEVICE_DEFINITION_EXTENDED = "android.app.admin.flags.managed_device_definition_extended";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\com\android\internal\hidden_from_bootclasspath\android\app\admin\flags\Flags.java-39-    public static final String FLAG_MANAGED_ESIM_OUTGOING_TRANSFER_POLICY = "android.app.admin.flags.managed_esim_outgoing_transfer_policy";
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-301-                }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-302-                WallpaperManager wallmanager2 = (WallpaperManager) this.mContext.getSystemService(GreezeReason.WALLPAPER);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-303-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java:304:            Settings.Secure.putString(this.mContext.getContentResolver(), "constant_lockscreen_info", "null");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-305-            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-306-            if (wifiManager != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\LiteFactoryResetImpl.java-307-                List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-504-        JSONObject jSONObjectOptJSONObject;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-505-        JSONObject jSONObjectOptJSONObject2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-506-        boolean zIsOS3KeyguardEditorAtLeast = SystemUtil.isOS3KeyguardEditorAtLeast(context);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java:507:        String string = Settings.Secure.getString(context.getContentResolver(), zIsOS3KeyguardEditorAtLeast ? "constant_template_editor_info" : "constant_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-508-        if (string != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-509-            try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-510-                if (zIsOS3KeyguardEditorAtLeast) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-402-        ClockBean clockBean = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-403-        if (DateFormatUtils.isCrossUser(context)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-404-            try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java:405:                string = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, context.getContentResolver(), "miui_15_default_lockscreen_info", Integer.valueOf(iUpdateCurrentUserId));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-406-            } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-407-                Log.e("ClockBean", "getStringForUser fail", e);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-408-                string = "";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-409-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-410-        } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java:411:            string = Settings.Secure.getString(context.getContentResolver(), "miui_15_default_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-412-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-413-        if (!TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-414-            try {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java-1225-    public final String getClockInfoJson(boolean z) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java-1226-        String str;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java-1227-        if (!z) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java:1228:            return Settings.Secure.getString(this.mContext.getContentResolver(), "constant_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java-1229-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java-1230-        String str2 = "";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java-1231-        try {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java-1235-                this.mEditorVersion = ((Integer) lockscreenInfo.first).intValue();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java-1236-                str = (String) lockscreenInfo.second;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java-1237-            } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java:1238:                str = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, this.mContext.getContentResolver(), "constant_lockscreen_info", Integer.valueOf(this.mCurrentUserId));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java-1239-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java-1240-            str2 = str;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\MiuiClockController.java-1241-          
... (truncated)
```

#### `template_editor_info`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-252-        } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-253-            iEventTracker3.track(new KeyguardUnlockWayStatusWithoutFingerEvent(companion2.getSecureType(), companion2.getDisplayPatternValue(), companion2.getDarkFingerprintUnlockValue(keyguardStat.context), companion2.getSideFingerprintUnlockWay(keyguardStat.context), companion2.getOpenFingerprintUnlockValue(keyguardStat.context), companion2.getFingerprintPrivacyPasswordValue(keyguardStat.context), companion2.getOpenFingerprintUnlockAppValue(keyguardStat.context), companion2.getGxzwAnim(keyguardStat.context), companion2.getOpenVibrationSwitchValue(keyguardStat.context), companion2.getShowFingerprintAfterSleepValue(keyguardStat.context), companion2.getFodQuickOpenValue(keyguardStat.context), companion2.getOpenFaceUnlockValue(keyguardStat.context), companion2.getFaceNum(keyguardStat.context), companion2.getStayKeyguardAfterFaceUnlockValue(keyguardStat.context), companion2.getFaceUnlockNotificationValue(keyguardStat.context), companion2.getHideNotificationContentBeforeFaceUnlockValue(keyguardStat.context), companion2.getOpenBlueUnlockValue(keyguardStat.context), EventConstantsKt.EVENT_KEYGUARD_UNLOCK_WAY_STATUS_TIP, numValueOf3));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-254-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java:255:        String stringForUser = Settings.Secure.getStringForUser(keyguardStat.context.getContentResolver(), "constant_template_editor_info", -2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-256-        if (stringForUser != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-257-            TrackTemplateEditorInfo trackTemplateEditorInfo = (TrackTemplateEditorInfo) new Gson().fromJson(TrackTemplateEditorInfo.class, stringForUser);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-258-            LockscreenInfo lockscreenInfo2 = trackTemplateEditorInfo.getLockscreenInfo();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-157-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-158-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-159-    private String getConstantTemplateEditorInfo() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:160:        String stringForUser = SettingsCompat$Secure.getStringForUser(this.mContext.getContentResolver(), "constant_template_editor_info", this.mCurrentUserId);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-161-        WallpaperProvider$$ExternalSyntheticOutline0.m(this.mCurrentUserId, "LockScreenInfoLayout", ActivityResultRegistry$$ExternalSyntheticOutline0.m("templateEditorInfo=", stringForUser, ", mCurrentUserId = "));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-162-        return stringForUser;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-163-    }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-171-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-172-    private String getJson() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-173-        if (!isSystemUI()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:174:            String string = Settings.Secure.getString(this.mContext.getContentResolver(), "constant_template_editor_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-175-            if (string == null || string.isEmpty()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:176:                return Settings.Secure.getString(this.mContext.getContentResolver(), "constant_template_editor_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-177-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-178-            Pair lockscreenInfo = getLockscreenInfo(string);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-179-            ((Integer) lockscreenInfo.first).getClass();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-430-        boolean zIsSystemUI = isSystemUI();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-431-        KeyguardDepthInteractor$updateAvoidStatus$1$$ExternalSyntheticOutline0.m("registerClockBeanListener isSystemUI = ", "LockScreenInfoLayout", zIsSystemUI);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-432-        if (!zIsSystemUI) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:433:            this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("constant_template_editor_info"), false, this.mListener);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-434-            return;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-435-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-436-        this.mUserSwitchBroadcastReceiver = new BroadcastReceiver() { // from class: com.miui.lockscreeninfo.LockScreenInfoLayout.1
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-461-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-462-        UserHandle userHandle = (UserHandle) objNewInstance;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-463-        this.mUserAllHandle = userHandle;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:464:        ReflectUtils.invokeObject(ContentResolver.class, this.mContext.getContentResolver(), "registerContentObserverAsUser", Void.TYPE, new Class[]{Uri.class, Boolean.TYPE, ContentObserver.class, UserHandle.class}, Settings.Secure.getUriFor("constant_template_editor_info"), Boolean.FALSE, this.mListener, this.mUserAllHandle);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-465-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-466-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-467-    public void setBackgroundBlurContainer(View view) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-776-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-777-            this.mModel = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-778-            this.signatureView.setText("");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:779:            Log.e("LockScreenInfoLayout", "ContentObserver fail, constant_template_editor_info value is Empty");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-780-        } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-781-            this.mModel = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-782-            this.signatureView.setText("");
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-512-                                Log.e(str9, "register receiver as user fail", e13);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-513-                            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-514-                            try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java:515:                                ContentResolver.class.getMethod("registerContentObserverAsUser", Uri.class, Boolean.TYPE, ContentObserver.class, UserHandle.class).invoke(miuiClockController13.mContext.getContentResolver(), Settings.Secure.getUriFor("constant_template_editor_info"), Boolean.FALSE, miuiClockController13.mClockInfoListener, miuiClockController13.mUserAllHandle);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-516-                            } catch (Exception e14) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\MiuiClockController.java-517-                                Log.e(str9, "register conte
... (truncated)
```

#### `default_lockscreen_info`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-70-        ClockBean clockBean = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-71-        if (DateFormatUtils.isCrossUser(context)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-72-            try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java:73:                string = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, context.getContentResolver(), "miui_15_default_lockscreen_info", Integer.valueOf(iUpdateCurrentUserId));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-74-            } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-75-                Log.e("ClockBean", "getStringForUser fail", e);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-76-                string = "";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-77-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-78-        } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java:79:            string = Settings.Secure.getString(context.getContentResolver(), "miui_15_default_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-80-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-81-        if (!TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-82-            try {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6673-                    this.currentLockScreenInfo = str;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6674-                    if (Intrinsics.areEqual(this.constantLockscreenInfo.getClockInfo().getTemplateId(), "smart_frame")) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6675-                        KeyguardOTAInteractor keyguardOTAInteractor = this.keyguardOTAInteractor;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:6676:                        keyguardOTAInteractor.writeEditorInfoIntoSettings(KeyguardOTAInteractor.generateEditorInfo(Settings.Secure.getStringForUser(keyguardOTAInteractor.context.getContentResolver(), "miui_15_default_lockscreen_info", keyguardOTAInteractor.userTracker.getUserId())));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6677-                        Log.w("KeyguardOTAInteractor", "resetDefaultLockscreenInfo: constantTemplateEditorInfo has been reset to default.");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6678-                    } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6679-                        Handler handlerAccess$getMainHandler = Companion.access$getMainHandler();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-177-    public final void updateDefaultLockInfo(String str) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-178-        ContentResolver contentResolver = this.context.getContentResolver();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-179-        IUserTracker iUserTracker = this.userTracker;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java:180:        Settings.Secure.putStringForUser(contentResolver, "miui_15_default_lockscreen_info", str, iUserTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-181-        Settings.Secure.putIntForUser(this.context.getContentResolver(), "lockscreen_info_version", 4, iUserTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-182-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-183-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-402-        ClockBean clockBean = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-403-        if (DateFormatUtils.isCrossUser(context)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-404-            try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java:405:                string = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, context.getContentResolver(), "miui_15_default_lockscreen_info", Integer.valueOf(iUpdateCurrentUserId));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-406-            } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-407-                Log.e("ClockBean", "getStringForUser fail", e);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-408-                string = "";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-409-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-410-        } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java:411:            string = Settings.Secure.getString(context.getContentResolver(), "miui_15_default_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-412-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-413-        if (!TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-414-            try {
```

### 1.4 Additional OS3 lockscreen-related Secure keys

#### `lockscreen_info`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-455-        JSONObject jSONObjectOptJSONObject;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-456-        JSONObject jSONObjectOptJSONObject2;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-457-        boolean zY = SystemUtil.y(context);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java:458:        String string = Settings.Secure.getString(context.getContentResolver(), zY ? "constant_template_editor_info" : "constant_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-459-        if (string != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-460-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-461-                if (zY) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-111-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-112-    /* JADX INFO: renamed from: zy, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-113-    @NotNull
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:114:    private static final String f54262zy = "constant_lockscreen_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-115-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-116-    /* JADX INFO: renamed from: k, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-117-    @NotNull
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-280-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-281-            String json = SettingHelper.f54247i.toJson(templateBean);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-282-            Log.i(SettingHelper.f54259toq, "hasModifyLockData :" + z + " , updateTemplateInfoToSettings : " + json);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:283:            Settings.Secure.putString(AppContextManager.q().getContentResolver(), "constant_lockscreen_info", json);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-284-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-285-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-286-        public final boolean cdj() {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-430-                Log.d(SettingHelper.f54259toq, "os3 return");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-431-                return null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-432-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:433:            String string = Settings.Secure.getString(AppContextManager.q().getContentResolver(), "constant_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-434-            if (TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-435-                Log.d(SettingHelper.f54259toq, "get current lock screen info is null");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-436-                return null;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-547-        public final void zurt() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-548-            Log.d(SettingHelper.f54259toq, "resetLockScreenToDefault");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-549-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:550:                Settings.Secure.putString(AppContextManager.q().getContentResolver(), "constant_lockscreen_info", null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-551-                Settings.Secure.putString(AppContextManager.q().getContentResolver(), "constant_template_editor_info", null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-552-                AppContextManager.q().getContentResolver().call(Uri.parse("content://miui.keyguard.editor.templatefileprovider"), "callRefreshCurrentTemplate", (String) null, (Bundle) null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-553-            } catch (Exception e) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-285-            this.f52013a = ((Integer) pairNi8.first).intValue();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-286-            return (String) pairNi8.second;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-287-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:288:        String qVar = SettingsCompat.Secure.toq(this.f52021s.getContentResolver(), "constant_lockscreen_info", this.f52015h);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-289-        Log.i(v, "getStringForUser, mCurrentUserId = " + this.f52015h);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-290-        return qVar;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-291-    }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java-28-        public static final String f52390q = "background_blur_enable";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java-29-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java-30-        /* JADX INFO: renamed from: toq, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java:31:        public static final String f52391toq = "constant_lockscreen_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java-32-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java-33-        /* JADX INFO: renamed from: zy, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\compat\SettingsCompat.java-34-        public static final String f52392zy = "constant_template_editor_info";
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImplKt.java-48-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImplKt.java-49-    /* JADX INFO: renamed from: toq, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImplKt.java-50-    @NotNull
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImplKt.java:51:    public static final String f48866toq = "constant_lockscreen_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImplKt.java-52-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImplKt.java-53-    /* JADX INFO: renamed from: x2, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImplKt.java-54-    @NotNull
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImpl.java-5259-            WallpaperInfo wallpaperInfo8;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImpl.java-5260-            Log.i("Keyguard-Editor-TemplateApiImpl", "getCurrentTemplateConfigInternal: requestGalleryContent = " + z);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateApiImpl.java-5261-            String strHyr = hyr();
E:/work/Android Project/_reverse-eng-arch
... (truncated)
```

#### `template_editor_info`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-83-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-84-    /* JADX INFO: renamed from: q, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-85-    @NotNull
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:86:    private static final String f54255q = "constant_template_editor_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-87-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-88-    /* JADX INFO: renamed from: qrj, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-89-    @NotNull
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-297-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-298-            String json = SettingHelper.f54247i.toJson(templateBean);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-299-            Log.i(SettingHelper.f54259toq, "hasModifyLockData :" + z + " , updateTemplateInfoToSettings : " + json);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:300:            Settings.Secure.putString(AppContextManager.q().getContentResolver(), "constant_template_editor_info", json);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-301-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-302-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-303-        public final void fu4(boolean z) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-311-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-312-        @Nullable
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-313-        public final CommonConfig g(boolean z) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:314:            String string = Settings.Secure.getString(AppContextManager.q().getContentResolver(), "constant_template_editor_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-315-            if (TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-316-                Log.d(SettingHelper.f54259toq, "get current lock screen info is null");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-317-                return null;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-548-            Log.d(SettingHelper.f54259toq, "resetLockScreenToDefault");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-549-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-550-                Settings.Secure.putString(AppContextManager.q().getContentResolver(), "constant_lockscreen_info", null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java:551:                Settings.Secure.putString(AppContextManager.q().getContentResolver(), "constant_template_editor_info", null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-552-                AppContextManager.q().getContentResolver().call(Uri.parse("content://miui.keyguard.editor.templatefileprovider"), "callRefreshCurrentTemplate", (String) null, (Bundle) null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-553-            } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\SettingHelper.java-554-                Log.d(SettingHelper.f54259toq, "resetLockScreenToDefault exception:" + e.getMessage());
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-98-    public static final String f53005t8r = "finger_print_rect_small_screen";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-99-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-100-    /* JADX INFO: renamed from: toq, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java:101:    public static final String f53006toq = "constant_template_editor_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-102-    public static final String wvg = "eastern_b";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-103-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-104-    /* JADX INFO: renamed from: x2, reason: collision with root package name */
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-455-        JSONObject jSONObjectOptJSONObject;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-456-        JSONObject jSONObjectOptJSONObject2;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-457-        boolean zY = SystemUtil.y(context);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java:458:        String string = Settings.Secure.getString(context.getContentResolver(), zY ? "constant_template_editor_info" : "constant_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-459-        if (string != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-460-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-461-                if (zY) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-259-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-260-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-261-    private String getConstantTemplateEditorInfo() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:262:        String qVar = SettingsCompat.Secure.toq(this.f52021s.getContentResolver(), "constant_template_editor_info", this.f52015h);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-263-        Log.d(v, "templateEditorInfo=" + qVar + ", mCurrentUserId = " + this.f52015h);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-264-        return qVar;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-265-    }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-271-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-272-    private String getJson() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-273-        if (!wvg()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:274:            String string = Settings.Secure.getString(this.f52021s.getContentResolver(), "constant_template_editor_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-275-            if (string == null || string.isEmpty()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:276:                return Settings.Secure.getString(this.f52021s.getContentResolver(), "constant_template_editor_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-277-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-278-            Pair<Integer, String> pairNi7 = ni7(string);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-279-            this.f52013a = ((Integer) pairNi7.first).intValue();
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-431-                setModelFromJson(json);
E:/work/Android Project/_reverse-eng-a
... (truncated)
```

#### `default_lockscreen_info`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-57-        ClockBean clockBean = null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-58-        if (DateFormatUtils.isCrossUser(context)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-59-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java:60:                string = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, context.getContentResolver(), "miui_15_default_lockscreen_info", Integer.valueOf(iUpdateCurrentUserId));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-61-            } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-62-                Log.e("ClockBean", "getStringForUser fail", e);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-63-                string = "";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-64-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-65-        } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java:66:            string = Settings.Secure.getString(context.getContentResolver(), "miui_15_default_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-67-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-68-        if (!TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\miui\clock\module\ClockBean.java-69-            try {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-57-        ClockBean clockBean = null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-58-        if (DateFormatUtils.y(context)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-59-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java:60:                string = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, context.getContentResolver(), "miui_15_default_lockscreen_info", Integer.valueOf(iP));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-61-            } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-62-                Log.e("ClockBean", "getStringForUser fail", e);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-63-                string = "";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-64-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-65-        } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java:66:            string = Settings.Secure.getString(context.getContentResolver(), "miui_15_default_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-67-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-68-        if (!TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\clock\module\ClockBean.java-69-            try {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3276-                    this.constantLockscreenInfo = (ConstantLockscreenInfo) new Gson().fromJson(ConstantLockscreenInfo.class, str);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3277-                    this.currentLockScreenInfo = str;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3278-                    if (Intrinsics.areEqual(this.constantLockscreenInfo.getClockInfo().getTemplateId(), "smart_frame")) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:3279:                        Settings.Secure.putStringForUser(this.context.getContentResolver(), "constant_template_editor_info", Settings.Secure.getStringForUser(this.context.getContentResolver(), "miui_15_default_lockscreen_info", this.userTracker.getUserId()), this.userTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3280-                    } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3281-                        Companion.access$getMainHandler().post(new Runnable() { // from class: com.android.keyguard.panel.KeyguardPanelViewController.onLockScreenInfoChange.1
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3282-                            @Override // java.lang.Runnable
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4418-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4419-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4420-    public final void updateDefaultLockScreenInfoInternal(String str) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:4421:        Settings.Secure.putStringForUser(this.context.getContentResolver(), "miui_15_default_lockscreen_info", str, this.userTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4422-        Settings.Secure.putIntForUser(this.context.getContentResolver(), "lockscreen_info_version", 3, this.userTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4423-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-4424-
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-301-        ClockBean clockBean = null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-302-        if (DateFormatUtils.isCrossUser(context)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-303-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java:304:                string = (String) Settings.Secure.class.getMethod("getStringForUser", ContentResolver.class, String.class, Integer.TYPE).invoke(null, context.getContentResolver(), "miui_15_default_lockscreen_info", Integer.valueOf(iUpdateCurrentUserId));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-305-            } catch (Exception e) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-306-                Log.e("ClockBean", "getStringForUser fail", e);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-307-                string = "";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-308-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-309-        } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java:310:            string = Settings.Secure.getString(context.getContentResolver(), "miui_15_default_lockscreen_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-311-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-312-        if (!TextUtils.isEmpty(string)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\clock\module\ClockBean.java-313-            try {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-69-        this.MARKER_LOCK_FOLLOWS_HOME = "lock_follows_home";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-70-        this.KEY_LOCKSCREEN_INFO = "constant_lockscreen_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-71-        this.KEY_TEMPLATE_EDITOR_INFO = "constant_template_editor_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java:72:        this.KEY_DEFAULT_LOCKSCREEN_INFO = "miui_15_default_lockscreen_info";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-73-        this.KEY_LOCKSCREEN_INFO_VERSION = "lockscreen_info_version";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-74-        this.KEY_DESKTOP_SCROLL = "pref_key_wallpaper_screen_s
... (truncated)
```

## 2. Lockscreen / Editor Application Classes

### 2.1 OS4

#### `com.miui.keyguard.editor`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\org\greenrobot\greendao\DbUtils.java-4-import android.database.Cursor;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\org\greenrobot\greendao\DbUtils.java-5-import android.database.DatabaseUtils;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\org\greenrobot\greendao\DbUtils.java-6-import android.database.sqlite.SQLiteDatabase;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\org\greenrobot\greendao\DbUtils.java:7:import com.miui.keyguard.editor.view.AutoBottomSheetKt;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\org\greenrobot\greendao\DbUtils.java-8-import java.io.ByteArrayOutputStream;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\org\greenrobot\greendao\DbUtils.java-9-import java.io.IOException;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\org\greenrobot\greendao\DbUtils.java-10-import java.io.InputStream;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\okio\Segment.java-1-package okio;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\okio\Segment.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\okio\Segment.java:3:import com.miui.keyguard.editor.view.AutoBottomSheetKt;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\okio\Segment.java-4-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\okio\Segment.java-5-/* JADX INFO: loaded from: classes3.dex */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\okio\Segment.java-6-final class Segment {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\graphics\gif\GifDecoder.java-1-package miuix.graphics.gif;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\graphics\gif\GifDecoder.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\graphics\gif\GifDecoder.java-3-import android.graphics.Bitmap;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\graphics\gif\GifDecoder.java:4:import com.miui.keyguard.editor.view.AutoBottomSheetKt;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\graphics\gif\GifDecoder.java-5-import java.io.BufferedInputStream;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\graphics\gif\GifDecoder.java-6-import java.io.InputStream;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\graphics\gif\GifDecoder.java-7-import java.util.Vector;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\animation\ViewTarget.java-17-import androidx.lifecycle.LifecycleObserver;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\animation\ViewTarget.java-18-import androidx.lifecycle.LifecycleOwner;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\animation\ViewTarget.java-19-import androidx.lifecycle.OnLifecycleEvent;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\animation\ViewTarget.java:20:import com.miui.keyguard.editor.view.AutoBottomSheetKt;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\animation\ViewTarget.java-21-import java.lang.ref.WeakReference;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\animation\ViewTarget.java-22-import java.util.HashSet;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\animation\ViewTarget.java-23-import java.util.Set;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\preference\GalleryPreference.java-19-import androidx.preference.PreferenceViewHolder;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\preference\GalleryPreference.java-20-import androidx.recyclerview.widget.RecyclerView;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\preference\GalleryPreference.java-21-import androidx.viewpager2.widget.OriginalViewPager2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\preference\GalleryPreference.java:22:import com.miui.keyguard.editor.view.AutoBottomSheetKt;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\preference\GalleryPreference.java-23-import miuix.core.util.MiuixUIUtils;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\preference\GalleryPreference.java-24-import miuix.miuixbasewidget.widget.PageIndicator;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\preference\GalleryPreference.java-25-import miuix.viewpager2.widget.ViewPager2;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\flexible\template\AbstractMarkTemplate.java-6-import android.util.AttributeSet;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\flexible\template\AbstractMarkTemplate.java-7-import android.view.View;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\flexible\template\AbstractMarkTemplate.java-8-import android.view.ViewGroup;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\flexible\template\AbstractMarkTemplate.java:9:import com.miui.keyguard.editor.view.AutoBottomSheetKt;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\flexible\template\AbstractMarkTemplate.java-10-import java.util.ArrayList;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\flexible\template\AbstractMarkTemplate.java-11-import java.util.Collections;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\flexible\template\AbstractMarkTemplate.java-12-import java.util.Comparator;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\DirectionAnimation.java-7-import android.view.ViewGroup;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\DirectionAnimation.java-8-import android.view.ViewTreeObserver;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\DirectionAnimation.java-9-import com.miui.aod.components.view.BaseStyleSelectView;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\DirectionAnimation.java:10:import com.miui.keyguard.editor.view.AutoBottomSheetKt;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\DirectionAnimation.java-11-import java.util.ArrayList;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\DirectionAnimation.java-12-import java.util.Collection;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\DirectionAnimation.java-13-import java.util.List;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\ArcAnimation.java-5-import android.view.ViewGroup;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\ArcAnimation.java-6-import android.view.ViewTreeObserver;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\ArcAnimation.java-7-import com.miui.aod.components.view.BaseStyleSelectView;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\ArcAnimation.java:8:import com.miui.keyguard.editor.view.AutoBottomSheetKt;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\ArcAnimation.java-9-import java.util.ArrayList;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\ArcAnimation.java-10-import java.util.List;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\popupwidget\widget\ArcAnimation.java-11-import miuix.animation.Folme;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\pickerwidget\widget\NumberPicker.java-50-import android.widget.TextView;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\pickerwidget\widget\NumberPicker.java-51-import androidx.annotation.ColorInt;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\pickerwidget\widget\NumberPicker.java-52-import androidx.appcompat.widget.AppCompatEditText;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\pickerwidget\widget\NumberPicker.java:53:import com.miui.keyguard.editor.view.AutoBottomSheetKt;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\pickerwidget\widget\NumberPicker.java-54-import java.util.ArrayList;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\pickerwidget\widget\NumberPicker.java-55-import java.util.Arrays;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\pickerwidget\widget\NumberPicker.java-56-import java.util.Collections;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\DeviceUtil.java-3-import android.os.Debug;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\DeviceUtil.java-4-import android.os.Environment;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\DeviceUtil.java-5-import android.os.StatFs;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\DeviceUtil.java:6:import com.miui.keyguard.editor.view.AutoBottomSheetKt;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\DeviceUtil.java-7-import java.io.BufferedReader;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\DeviceUtil.java-8-import java.io.FileReader;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\DeviceUtil.java-9-import java.io.IOException;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\core\util\ScreenModeHelper.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\miuix\core\util\ScreenModeHelper.java-3-import android.content.Context;
E:/work/
... (truncated)
```

#### `com.android.server.wallpaper`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-481-                outProviders.put("com.android.server.vibrator.VibratorManagerServiceStub", new VibratorManagerServiceImpl.Provider());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-482-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-483-        });
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java:484:        list.add(new MiuiStubRegistry.ImplProviderManifest() { // from class: com.android.server.wallpaper.WallpaperManagerServiceImpl$$GeneratedMiuiImplManifest
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-485-            public final void collectImplProviders(Map<String, MiuiStubRegistry.ImplProvider<?>> outProviders) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java:486:                outProviders.put("com.android.server.wallpaper.WallpaperManagerServiceStub", new WallpaperManagerServiceImpl.Provider());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-487-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-488-        });
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-489-        list.add(new MiuiStubRegistry.ImplProviderManifest() { // from class: com.android.server.wm.AppResurrectionServiceImpl$$GeneratedMiuiImplManifest
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\com\android\server\wallpaper\WallpaperCropperStub.java:1:package com.android.server.wallpaper;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\com\android\server\wallpaper\WallpaperCropperStub.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\com\android\server\wallpaper\WallpaperCropperStub.java-3-import android.graphics.Bitmap;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\com\android\server\wallpaper\WallpaperCropperStub.java-4-import android.graphics.BitmapFactory;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java-91-import com.android.server.miuiappadaptation.MiuiAppAdaptationManagerServiceStub;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java-92-import com.android.server.pm.pkg.AndroidPackage;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java-93-import com.android.server.ui.IUiService;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java:94:import com.android.server.wallpaper.WallpaperManagerInternal;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java-95-import com.android.server.wm.cloud.CloudControlConfig;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java-96-import com.miui.app.smartpower.SmartPowerServiceInternal;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java-97-import com.miui.base.MiuiStubRegistry;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\PreStartingManager.java-53-import com.android.server.MiuiUiModeManagerServiceStub;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\PreStartingManager.java-54-import com.android.server.am.SchedBoostManagerInternalStub;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\PreStartingManager.java-55-import com.android.server.policy.WindowManagerPolicy;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\PreStartingManager.java:56:import com.android.server.wallpaper.WallpaperManagerInternal;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\PreStartingManager.java-57-import com.miui.server.AccessController;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\PreStartingManager.java-58-import com.miui.server.mirim.policy.DouyinContentPagePolicy;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\PreStartingManager.java-59-import com.miui.server.mirim.policy.frame_sched_policy.AppInfo;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiTaskBackgroundControllerImpl.java-9-import android.util.Slog;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiTaskBackgroundControllerImpl.java-10-import com.android.internal.util.ParseUtils;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiTaskBackgroundControllerImpl.java-11-import com.android.server.taskbackground.MiuiTaskBackgroundController;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiTaskBackgroundControllerImpl.java:12:import com.android.server.wallpaper.WallpaperManagerServiceStub;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiTaskBackgroundControllerImpl.java-13-import com.miui.base.MiuiStubRegistry;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiTaskBackgroundControllerImpl.java-14-import java.io.FileDescriptor;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiTaskBackgroundControllerImpl.java-15-import java.io.PrintWriter;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\android\server\wallpaper\WallpaperCropperImpl.java:1:package com.android.server.wallpaper;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\android\server\wallpaper\WallpaperCropperImpl.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\android\server\wallpaper\WallpaperCropperImpl.java-3-import android.graphics.Bitmap;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\android\server\wallpaper\WallpaperCropperImpl.java-4-import android.graphics.BitmapFactory;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiBlurWallpaperManager.java-18-import android.os.RemoteException;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiBlurWallpaperManager.java-19-import android.util.Slog;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiBlurWallpaperManager.java-20-import com.android.server.LocalServices;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiBlurWallpaperManager.java:21:import com.android.server.wallpaper.WallpaperManagerInternal;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiBlurWallpaperManager.java-22-import java.io.FileDescriptor;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiBlurWallpaperManager.java-23-import java.io.PrintWriter;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiBlurWallpaperManager.java-24-import java.lang.ref.WeakReference;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\miui\base\FrameworkManifestCollector.java-300-                outProviders.put("com.android.internal.policy.PhoneWindowStub", new PhoneWindowStubImpl.Provider());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\miui\base\FrameworkManifestCollector.java-301-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\miui\base\FrameworkManifestCollector.java-302-        });
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\miui\base\FrameworkManifestCollector.java:303:        list.add(new MiuiStubRegistry.ImplProviderManifest() { // from class: com.android.server.wallpaper.WallpaperCropperImpl$$GeneratedMiuiImplManifest
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\miui\base\FrameworkManifestCollector.java-304-            public final void collectImplProviders(Map<String, MiuiStubRegistry.ImplProvider<?>> outProviders) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\miui\base\FrameworkManifestCollector.java:305:                outProviders.put("com.android.server.wallpaper.WallpaperCropperStub", new WallpaperCropperImpl.Provider());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\miui\base\FrameworkManifestCollector.java-306-                outProviders.put("android.service.wallpaper.WallpaperServiceStub", new WallpaperServiceImpl.Provider());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\miui\base\FrameworkManifestCollector.java-307-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\com\miui\base\FrameworkManifestCollector.java-308-        });
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceProxy.java:1:package com.android.server.wallpaper;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceProxy.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceProxy.java-3-import android.content.ComponentName;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceProxy.java-4-import com.xiaomi.reflect.RefClass;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decom
... (truncated)
```

#### `MIUI WallpaperManagerServiceImpl`
```java
(no matches)
```

#### `WallpaperObserver`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-3693-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-3694-            controlTopButtonState(true);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-3695-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java:3696:        rebindWallpaperObserverToOverlay();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-3697-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-3698-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-3699-    /* JADX WARN: Code duplicated, block: B:22:0x003e  */
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4095-        return signatureAreaView != null && signatureAreaView.hasFocus() && (z || this.keyboardLiftApplied);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4096-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4097-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java:4098:    public final void rebindWallpaperObserverToOverlay() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4099-        CombinedWallpaperView combinedWallpaperView;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4100-        final MultiTouchFrameLayout multiTouchFrameLayoutEnsureEditorOverlayLayer = ensureEditorOverlayLayer();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java:4101:        ObserverGestureListener observerGestureListener = new ObserverGestureListener(multiTouchFrameLayoutEnsureEditorOverlayLayer, this) { // from class: com.miui.keyguard.editor.edit.allinone.AllInOneTemplateView$rebindWallpaperObserverToOverlay$composite$1
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4102-            public final ObserverClickListener click;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4103-            public final /* synthetic */ AllInOneTemplateView this$0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4104-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4124-        if (baseTemplateView != null && (combinedWallpaperView = (CombinedWallpaperView) baseTemplateView.getWallpaperLayer()) != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4125-            combinedWallpaperView.setObserveTouchClickListener(observerGestureListener);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4126-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java:4127:        Log.i("AllInOneTemplateView", "rebindWallpaperObserverToOverlay: overlay=" + Integer.toHexString(System.identityHashCode(multiTouchFrameLayoutEnsureEditorOverlayLayer)) + " bound to CombinedWallpaperView");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4128-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4129-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4130-    public final void decideHierarchyLayerPosition() {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4226-                boolValueOf2 = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4227-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4228-            Log.i("AllInOneTemplateView", "SignatureContainerSync: hourContainer=" + boolValueOf + ", minContainer=" + boolValueOf2 + ", canShow=" + z2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java:4229:            rebindWallpaperObserverToOverlay();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4230-            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.miui.keyguard.editor.edit.allinone.AllInOneTemplateView$$ExternalSyntheticLambda10
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4231-                @Override // android.view.View.OnFocusChangeListener
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\edit\allinone\AllInOneTemplateView.java-4232-                public final void onFocusChange(View view3, boolean z3) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java-74-    public void setWallpaperManagerService(WallpaperManagerService service, Context context) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java-75-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java-76-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java:77:    public void handleWallpaperObserverEvent(WallpaperData mWallpaperData) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java-78-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java-79-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java-80-    public ParcelFileDescriptor getBlurWallpaper(IWallpaperManagerCallback cb) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-221-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-222-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-223-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java:224:    class WallpaperObserver extends FileObserver {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-225-        final int mUserId;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-226-        final WallpaperData mWallpaper;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-227-        final File mWallpaperDir;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-228-        final File mWallpaperFile;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-229-        final File mWallpaperLockFile;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-230-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java:231:        public WallpaperObserver(WallpaperData wallpaper) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-232-            super(WallpaperUtils.getWallpaperDir(wallpaper.userId).getAbsolutePath(), 1672);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-233-            this.mUserId = wallpaper.userId;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-234-            this.mWallpaperDir = WallpaperUtils.getWallpaperDir(wallpaper.userId);
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-308-                                        if (WallpaperManagerService.DEBUG) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-309-                                            Slog.v(WallpaperManagerService.TAG, "Home screen wallpaper changed");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-310-                                        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java:311:                                        IRemoteCallback.Stub callback = new IRemoteCallback.Stub() { /
... (truncated)
```

#### `WallpaperApplyInfos`
```java
(no matches)
```

#### `MiuiWallpaperManager`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\okhttp3\internal\connection\RealConnection.java-1-package okhttp3.internal.connection;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\okhttp3\internal\connection\RealConnection.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\okhttp3\internal\connection\RealConnection.java:3:import com.miui.miwallpaper.MiuiWallpaperManager;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\okhttp3\internal\connection\RealConnection.java-4-import java.io.IOException;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\okhttp3\internal\connection\RealConnection.java-5-import java.lang.ref.Reference;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\okhttp3\internal\connection\RealConnection.java-6-import java.net.ConnectException;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\okhttp3\internal\connection\RealConnection.java-482-    public String toString() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\okhttp3\internal\connection\RealConnection.java-483-        StringBuilder sbAppend = new StringBuilder("Connection{").append(this.route.address().url().host()).append(MethodCodeHelper.IDENTITY_INFO_SEPARATOR).append(this.route.address().url().port()).append(", proxy=").append(this.route.proxy()).append(" hostAddress=").append(this.route.socketAddress()).append(" cipherSuite=");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\okhttp3\internal\connection\RealConnection.java-484-        Handshake handshake = this.handshake;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\okhttp3\internal\connection\RealConnection.java:485:        return sbAppend.append(handshake != null ? handshake.cipherSuite() : MiuiWallpaperManager.DEFAULT_PENDING_PACKAGE).append(" protocol=").append(this.protocol).append('}').toString();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\okhttp3\internal\connection\RealConnection.java-486-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\okhttp3\internal\connection\RealConnection.java-487-}
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java-9-import android.view.MotionEvent;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java-10-import android.view.View;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java-11-import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java:12:import com.miui.miwallpaper.MiuiWallpaperManager;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java-13-import miuix.animation.Folme;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java-14-import miuix.animation.FolmeEase;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java-15-import miuix.animation.base.AnimConfig;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java-73-    static {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java-74-        MaterialToken materialTokenBuild = new MaterialToken.Builder(30, "page-indicator-glass", "light").setMaskBlur(20).setColorBlend(ColorBlendToken.Colored_Regular_Light).setBloomStroke(BloomStrokeToken.Glass_Stroke_Middle_Light).setShadow(ShadowToken.Regular).build();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java-75-        Page_Indicator_Glass_Light = materialTokenBuild;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java:76:        MaterialToken materialTokenBuild2 = new MaterialToken.Builder(30, "page-indicator-glass", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setMaskBlur(20).setColorBlend(ColorBlendToken.Colored_Regular_Dark).setBloomStroke(BloomStrokeToken.Glass_Stroke_Middle_Dark).setShadow(ShadowToken.Regular).build();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java-77-        Page_Indicator_Glass_Dark = materialTokenBuild2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java-78-        Page_Indicator_Glass_DayNight = new MaterialDayNightToken(materialTokenBuild, materialTokenBuild2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\PageIndicator.java-79-    }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-1-package miuix.miuixbasewidget.widget;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java:3:import com.miui.miwallpaper.MiuiWallpaperManager;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-4-import miuix.theme.token.BlendModeToken;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-5-import miuix.theme.token.BloomStrokeToken;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-6-import miuix.theme.token.ColorBlendToken;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-41-        Yellow_FAB_Color_Blend_Dark = colorBlendTokenBuild2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-42-        MaterialToken materialTokenBuild = new MaterialToken.Builder(10, "fab-yellow", "light").setElementBlur(20).setColorBlend(colorBlendTokenBuild).setBloomStroke(BloomStrokeToken.Glass_Stroke_Middle_Light).setShadow(ShadowToken.Regular).build();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-43-        Yellow_FAB_Light = materialTokenBuild;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java:44:        MaterialToken materialTokenBuild2 = new MaterialToken.Builder(10, "fab-yellow", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setElementBlur(20).setColorBlend(colorBlendTokenBuild2).setBloomStroke(BloomStrokeToken.Glass_Stroke_Middle_Dark).setShadow(ShadowToken.Regular).build();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-45-        Yellow_FAB_Dark = materialTokenBuild2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-46-        Yellow_FAB_Material = new MaterialDayNightToken(materialTokenBuild, materialTokenBuild2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-47-        ColorBlendToken colorBlendTokenBuild3 = new ColorBlendToken.Builder().setConfig(new int[]{-855638017, -434254534}, new int[]{BlendModeToken.PLUS_DARKER.value, BlendModeToken.SRC_OVER.value}).build();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-50-        Green_FAB_Color_Blend_Dark = colorBlendTokenBuild4;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-51-        MaterialToken materialTokenBuild3 = new MaterialToken.Builder(10, "fab-green", "light").setElementBlur(20).setColorBlend(colorBlendTokenBuild3).setBloomStroke(BloomStrokeToken.Glass_Stroke_Middle_Light).setShadow(ShadowToken.Regular).build();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-52-        Green_FAB_Light = materialTokenBuild3;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java:53:        MaterialToken materialTokenBuild4 = new MaterialToken.Builder(10, "fab-green", MiuiWallpaperManager.MI_WALLPAPER_TYPE_DARK).setElementBlur(20).setColorBlend(colorBlendTokenBuild4).setBloomStroke(BloomStrokeToken.Glass_Stroke_Middle_Dark).setShadow(ShadowToken.Regular).build();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-54-        Green_FAB_Dark = materialTokenBuild4;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-55-        Green_FAB_Material = new MaterialDayNightToken(materialTokenBuild3, materialTokenBuild4);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-56-        ColorBlendToken colorBlendTokenBuild5 = new ColorBlendToken.Builder().setConfig(new int[]{-855638017, -432766209}, new int[]{BlendModeToken.PLUS_DARKER.value, BlendModeToken.SRC_OVER.value}).build();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-59-        Blue_FAB_Color_Blend_Dark = colorBlendTokenBuild6;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-60-        MaterialToken materialTokenBuild5 = new MaterialToken.Builder(10, "fab-blue", "light").setElementBlur(20).setColorBlend(colorBlendTokenBuild5).setBloomStroke(BloomStrokeToken.Glass_Stroke_Middle_Light).setShadow(ShadowToken.Regular).build();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java-61-        Blue_FAB_Light = materialTokenBuild5;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\miuix\miuixbasewidget\widget\FloatingActionButtonTokens.java:62:        MaterialToken m
... (truncated)
```

### 2.2 OS3

#### `com.miui.keyguard.editor`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\springback\trigger\DefaultTrigger.java-6-import android.view.ViewGroup;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\springback\trigger\DefaultTrigger.java-7-import android.widget.ProgressBar;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\springback\trigger\DefaultTrigger.java-8-import android.widget.TextView;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\springback\trigger\DefaultTrigger.java:9:import com.miui.keyguard.editor.edit.EditFragment;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\springback\trigger\DefaultTrigger.java-10-import miuix.animation.Folme;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\springback\trigger\DefaultTrigger.java-11-import miuix.animation.base.AnimConfig;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\springback\trigger\DefaultTrigger.java-12-import miuix.animation.controller.AnimState;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\taiyi\api\segment\SegmentAbility.java-3-import android.content.Context;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\taiyi\api\segment\SegmentAbility.java-4-import android.graphics.Bitmap;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\taiyi\api\segment\SegmentAbility.java-5-import android.os.ParcelFileDescriptor;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\taiyi\api\segment\SegmentAbility.java:6:import com.miui.keyguard.editor.utils.segment.ImageSegmentCacheKt;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\taiyi\api\segment\SegmentAbility.java-7-import com.xiaomi.taiyi.sdk.base.utils.SdkLog;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\taiyi\api\segment\SegmentAbility.java-8-import com.xiaomi.taiyi.sdk.core.AIAbility;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\taiyi\api\segment\SegmentAbility.java-9-import com.xiaomi.taiyi.sdk.transfer.data.AIPackage;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\service\q.java-6-import android.os.Build;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\service\q.java-7-import android.text.TextUtils;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\service\q.java-8-import androidx.exifinterface.media.ExifInterface;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\service\q.java:9:import com.miui.keyguard.editor.utils.Wallpaper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\service\q.java-10-import com.xiaomi.push.BuildConfig;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\service\q.java-11-import com.xiaomi.push.Cif;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\service\q.java-12-import com.xiaomi.push.gv;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\ho.java-5-import com.android.thememanager.basemodule.analysis.AnalyticsConstants;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\ho.java-6-import com.android.thememanager.share.ShareStatisticsUploadHelper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\ho.java-7-import com.google.android.exoplayer2.text.ttml.TtmlNode;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\ho.java:8:import com.miui.keyguard.editor.edit.EditFragment;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\ho.java-9-import com.xiaomi.micloudsdk.utils.MiCloudRuntimeConstants;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\ho.java-10-import java.io.ByteArrayInputStream;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\ho.java-11-import java.io.IOException;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\fs.java-1-package com.xiaomi.push;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\fs.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\fs.java:3:import com.miui.keyguard.editor.data.bean.MagicType;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\fs.java-4-import com.xiaomi.taiyi.sdk.base.data.ErrorCode;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\fs.java-5-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\push\fs.java-6-/* JADX INFO: compiled from: r8-map-id-28176a47c9109cff812a5f7a0a997c1bce7bf6885f244bf2996d02e43b70343a */
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java-6-import android.util.EventLog;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java-7-import android.view.ContextMenu;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java-8-import android.view.View;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java:9:import com.miui.keyguard.editor.data.bean.MagicType;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java-10-import miuix.appcompat.internal.view.menu.MenuBuilder;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java-11-import miuix.appcompat.internal.view.menu.MenuDialogHelper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java-12-
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java-7-import android.view.ContextMenu;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java-8-import android.view.View;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java-9-import androidx.annotation.NonNull;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java:10:import com.miui.keyguard.editor.data.bean.MagicType;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java-11-import miuix.appcompat.internal.view.menu.MenuBuilder;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java-12-import miuix.appcompat.internal.view.menu.MenuDialogHelper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\miuix\appcompat\internal\view\menu\context\ContextMenuBuilder.java-13-
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\AIUtils.java-5-import android.content.pm.Signature;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\AIUtils.java-6-import android.content.pm.SigningInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\AIUtils.java-7-import android.os.Process;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\AIUtils.java:8:import com.miui.keyguard.editor.data.bean.MagicType;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\AIUtils.java-9-import java.io.File;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\AIUtils.java-10-import java.security.MessageDigest;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\xiaomi\taiyi\sdk\base\utils\AIUtils.java-11-import java.util.Arrays;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\milab\videosdk\utils\HandlerThreadHolder.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\milab\videosdk\utils\HandlerThreadHolder.java-3-import android.os.HandlerThread;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\milab\videosdk\utils\HandlerThreadHolder.java-4-import android.util.Log;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\milab\videosdk\utils\HandlerThreadHolder.java:5:import com.miui.keyguard.editor.track.TrackValues;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\milab\videosdk\utils\HandlerThreadHolder.java-6-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\milab\videosdk\utils\HandlerThreadHolder.java-7-/* JADX INFO: compiled from: r8-map-id-28176a47c9109cff812a5f7a0a997c1bce7bf6885f244bf2996d02e43b70343a */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\milab\videosdk\utils\HandlerThreadHolder.java-8-/* JADX INFO: loaded from: classes4.dex */
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\milab\videosdk\ReturnStatus.java-1-package com.xiaomi.milab.videosdk;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\milab\videosdk\ReturnStatus.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\milab\videosdk\ReturnStatus.java:3:import com.miui.keyguard.editor.track.TrackValues;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\milab\videosdk\ReturnStatus.java-4-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\xiaomi\milab\videosdk\ReturnStatus.java-5-/* JADX INFO: compiled from: r8-map-id-28176a47c9109cff812a5f7a0a997c1bce7bf6885f244bf2996d02e43b70343a */
E:/work/Android Pro
... (truncated)
```

#### `com.android.server.wallpaper`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\com\android\server\wallpaper\WallpaperCropperStub.java:1:package com.android.server.wallpaper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\com\android\server\wallpaper\WallpaperCropperStub.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\com\android\server\wallpaper\WallpaperCropperStub.java-3-import android.graphics.Bitmap;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\com\android\server\wallpaper\WallpaperCropperStub.java-4-import android.graphics.BitmapFactory;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java-84-import com.android.server.logmanager.HyperLogManagerServiceInternal;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java-85-import com.android.server.pm.pkg.AndroidPackage;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java-86-import com.android.server.ui.IUiService;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java:87:import com.android.server.wallpaper.WallpaperManagerInternal;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java-88-import com.miui.app.smartpower.SmartPowerServiceInternal;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java-89-import com.miui.base.MiuiStubRegistry;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wm\WindowManagerServiceImpl.java-90-import com.miui.server.stability.DumpSysInfoUtil;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceProxy.java:1:package com.android.server.wallpaper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceProxy.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceProxy.java-3-import android.content.ComponentName;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceProxy.java-4-import com.xiaomi.reflect.RefClass;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java:1:package com.android.server.wallpaper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-3-import android.R;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-4-import android.app.ActivityManager;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-605-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-606-        this.blurWallpaperThread = new HandlerThread("BlurWallpaperThread");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-607-        this.blurWallpaperThread.start();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java:608:        this.blurWallpaperHandler = new Handler(this.blurWallpaperThread.getLooper()) { // from class: com.android.server.wallpaper.WallpaperManagerServiceImpl.1
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-609-            @Override // android.os.Handler
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-610-            public void handleMessage(Message msg) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-611-                if (MiuiAppSizeCompatModeStub.get().isFlip() || (!MiuiAppSizeCompatModeStub.get().isEnabled() && !MiuiEmbeddingWindowServiceLoader.isActivityEmbeddingEnable())) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperDataParserProxy.java:1:package com.android.server.wallpaper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperDataParserProxy.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperDataParserProxy.java-3-import com.android.internal.util.JournaledFile;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperDataParserProxy.java-4-import com.xiaomi.reflect.RefClass;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-468-                outProviders.put("com.android.server.vibrator.VibratorManagerServiceStub", new VibratorManagerServiceImpl.Provider());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-469-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-470-        });
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java:471:        list.add(new MiuiStubRegistry.ImplProviderManifest() { // from class: com.android.server.wallpaper.WallpaperManagerServiceImpl$$GeneratedMiuiImplManifest
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-472-            public final void collectImplProviders(Map<String, MiuiStubRegistry.ImplProvider<?>> outProviders) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java:473:                outProviders.put("com.android.server.wallpaper.WallpaperManagerServiceStub", new WallpaperManagerServiceImpl.Provider());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-474-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-475-        });
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\base\ServiceManifestCollector.java-476-        list.add(new MiuiStubRegistry.ImplProviderManifest() { // from class: com.android.server.wm.AppResurrectionServiceImpl$$GeneratedMiuiImplManifest
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\WallpaperController.java-24-import com.android.internal.protolog.ProtoLogImpl_1166330376;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\WallpaperController.java-25-import com.android.internal.protolog.WmProtoLogGroups;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\WallpaperController.java-26-import com.android.internal.util.ToBooleanFunction;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\WallpaperController.java:27:import com.android.server.wallpaper.WallpaperCropper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\WallpaperController.java:28:import com.android.server.wallpaper.WallpaperDefaultDisplayInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\WallpaperController.java-29-import java.io.PrintWriter;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\WallpaperController.java-30-import java.util.ArrayList;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\WallpaperController.java-31-import java.util.List;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\DisplayPolicy.java-66-import com.android.server.notification.NotificationManagerInternal;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\DisplayPolicy.java-67-import com.android.server.policy.WindowManagerPolicy;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\DisplayPolicy.java-68-import com.android.server.statusbar.StatusBarManagerInternal;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\DisplayPolicy.java:69:import com.android.server.wallpaper.WallpaperManagerInternal;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\DisplayPolicy.java-70-import java.io.PrintWriter;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\DisplayPolicy.java-71-import java.util.ArrayList;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wm\DisplayPolicy.java-72-import java.util.Arrays;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationPerUserService.java:1:package com.android.server.wallpapereffectsgeneration;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationPerUserService.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationPerUserService.java-3-import android.app.AppGlobals;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationPerUserService.java-4-import android.app.wallpapereffectsgeneration.CinematicEffectRequest;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationPerUserService.java-67-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationPerU
... (truncated)
```

#### `MIUI WallpaperManagerServiceImpl`
```java
(no matches)
```

#### `WallpaperObserver`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-954-            baseLauncher.mShowUserPresentAnimation = MiuiSettingsUtils.isSystemAnimationOpen(baseLauncher.getApplicationContext(), true);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-955-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-956-    };
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java:957:    private ContentObserver mLockWallpaperObserver = new ContentObserver(new Handler()) { // from class: com.miui.home.launcher.BaseLauncher.37
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-958-        @Override // android.database.ContentObserver
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-959-        public void onChange(boolean z) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-960-            if (BaseLauncher.this.isDestroyed()) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-5142-        contentResolver.registerContentObserver(Settings.Secure.getUriFor(MiuiSettingsUtils.SETTINGS_LOCK_SCREEN_INFO), false, this.mConstantLockScreenInfoObserver);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-5143-        contentResolver.registerContentObserver(ILauncherProvider.CONTENT_APPWIDGET_RESET_URI, true, this.mWidgetObserver);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-5144-        contentResolver.registerContentObserver(Settings.System.getUriFor("light_speed_app"), false, this.mSpeedOfLightObserver);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java:5145:        contentResolver.registerContentObserver(Settings.System.getUriFor(MiuiSettingsUtils.LOCK_WALLPAPER_PROVIDER_AUTHORITY), false, this.mLockWallpaperObserver);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-5146-        contentResolver.registerContentObserver(Settings.Global.getUriFor(MiuiSettingsUtils.FORCE_FSG_NAV_BAR), false, this.mImmersiveNavigationBarObserver);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-5147-        DeviceConfigs.updateGestureEnable(this);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-5148-        this.mHandler.post(new Runnable() { // from class: com.miui.home.launcher.BaseLauncher$$ExternalSyntheticLambda93
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-5205-        contentResolver.unregisterContentObserver(this.mConstantLockScreenInfoObserver);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-5206-        contentResolver.unregisterContentObserver(this.mWidgetObserver);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-5207-        contentResolver.unregisterContentObserver(this.mSpeedOfLightObserver);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java:5208:        contentResolver.unregisterContentObserver(this.mLockWallpaperObserver);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-5209-        contentResolver.unregisterContentObserver(this.mImmersiveNavigationBarObserver);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-5210-        contentResolver.unregisterContentObserver(this.mVoiceServiceObserver);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\launcher\BaseLauncher.java-5211-        contentResolver.unregisterContentObserver(this.mGlobalEdgeObserver);
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-283-        this.mImageWallpaper = ComponentName.unflattenFromString(context.getResources().getString(R.string.keyguard_label_text));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-284-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-285-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java:286:    public void handleWallpaperObserverEvent(WallpaperData wallpaper) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-287-        if (this.mService == null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-288-            return;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-289-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java:290:        Slog.v(TAG, "handleWallpaperObserverEvent");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-291-        this.blurWallpaperHandler.removeMessages(1);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-292-        Message message = Message.obtain(this.blurWallpaperHandler, 1, wallpaper);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceImpl.java-293-        message.sendToTarget();
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java-70-    public void setWallpaperManagerService(WallpaperManagerService service, Context context) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java-71-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java-72-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java:73:    public void handleWallpaperObserverEvent(WallpaperData mWallpaperData) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java-74-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java-75-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerServiceStub.java-76-    public ParcelFileDescriptor getBlurWallpaper(IWallpaperManagerCallback cb) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-218-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-219-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-220-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java:221:    class WallpaperObserver extends FileObserver {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-222-        final int mUserId;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-223-        final WallpaperData mWallpaper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-224-        final File mWallpaperDir;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-225-        final File mWallpaperFile;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-226-        final File mWallpaperLockFile;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-227-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java:228:        public WallpaperObserver(WallpaperData wallpaper) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-229-            super(WallpaperUtils.getWallpaperDir(wallpaper.userId).getAbsolutePath(), 1672);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-230-            this.mUserId = wallpaper.userId;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-231-            this.mWallpaperDir = WallpaperUtils.getWallpaperDir(wallpaper.userId);
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-315-                                        if (WallpaperManagerService.DEBUG) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-316-                                            Slog.v(WallpaperManagerService.TAG, "Home screen wallpaper changed");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-317-                                        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java:318:                                        IRemoteCallback.Stub callback = new IRemoteCallback.Stub() { // from class: com.android.server.wallpaper.WallpaperManagerService.WallpaperObserver.1
E:/work/Android Pr
... (truncated)
```

#### `WallpaperApplyInfos`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-25-import com.android.thememanager.basemodule.utils.Utils;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-26-import com.android.thememanager.basemodule.video.VideoFormatUtil;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-27-import com.android.thememanager.controller.local.ProvisionApplyTheme;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java:28:import com.android.thememanager.model.WallpaperApplyInfos;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-29-import com.android.thememanager.router.detail.entity.VideoInfoUtils;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-30-import com.android.thememanager.settings.superwallpaper.UnitySuperWallpaperUtils;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-31-import com.android.thememanager.settings.superwallpaper.activity.data.SuperWallpaperSummaryData;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-107-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-108-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-109-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java:110:    public static int fn3e(boolean isLockScreen, WallpaperApplyInfos applyInfo) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-111-        if (applyInfo != null && applyInfo.getSingleWhich() > -1) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-112-            return applyInfo.getSingleWhich();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-113-        }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-260-        return null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-261-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-262-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java:263:    public boolean ch(InputStream is, String originPath, String darkModePath, boolean isLockScreen, boolean isUseDark, boolean needDark, WallpaperApplyInfos applyInfo) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-264-        InputStreamLoader inputStreamLoader;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-265-        InputStream inputStream;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-266-        CommonConfig commonConfigZp;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-477-        return null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-478-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-479-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java:480:    public boolean g(Object deskMamlPreviewIs, Object lockMamlPreviewIs, int which, WallpaperApplyInfos wallpaperApplyInfos) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-481-        if (!this.f31709toq || this.f31706k == null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-482-            Log.d(f31700f7l8, "applyThemeImageWallpaper null, return");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-483-            return false;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-484-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-485-        Log.d(f31700f7l8, "applyThemeImageWallpaper is, which = " + which);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-486-        if (wallpaperApplyInfos == null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java:487:            wallpaperApplyInfos = new WallpaperApplyInfos();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-488-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-489-        MiuiWallpaperManager.WallpaperApplierBuilder wallpaperApplierBuilderQrj = d3().wvg(which).qrj(deskMamlPreviewIs, lockMamlPreviewIs);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-490-        CommonConfig commonConfigZp = zp(wallpaperApplierBuilderQrj, wallpaperApplyInfos.getDoodleStatus(), which);
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-641-        return false;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-642-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-643-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java:644:    public boolean lv5(Bitmap destBitmap, int which, boolean needDark, WallpaperApplyInfos applyInfo) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-645-        if (!this.f31709toq || this.f31706k == null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-646-            Log.d(f31700f7l8, "setMiuiImageWallpaper null, return");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-647-            return false;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-648-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-649-        if (applyInfo == null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java:650:            applyInfo = new WallpaperApplyInfos();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-651-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-652-        Log.d(f31700f7l8, "setMiuiImageWallpaper bitmap, which = " + which + ", effectId:" + applyInfo.getEffectId() + ", enableBlur:" + applyInfo.isEnableBlur() + ", p3Bitmap:" + WideColorGamutManager.f31713k.f7l8(destBitmap) + " ,needDark:" + needDark + ", bitmapWidth=" + destBitmap.getWidth() + ", bitmapHeight=" + destBitmap.getHeight());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-653-        MiuiWallpaperManager.WallpaperApplierBuilder wallpaperApplierBuilderI = d3().wvg(which).f7l8(applyInfo.getEffectId()).y(applyInfo.isEnableBlur()).p(destBitmap).i(needDark);
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-689-        return bitmap;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-690-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-691-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java:692:    public boolean n(InputStream is, int which, WallpaperApplyInfos applyInfos) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-693-        if (!this.f31709toq || this.f31706k == null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-694-            Log.d(f31700f7l8, "applyThemeImageWallpaper null, return");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-695-            return false;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-701-        sb.append(is == null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\wallpaper\WallpaperController.java-702-        Log.d(f31700f7l8, sb.toString());
E:/work/Android Project/_reverse-eng-archive/a
... (truncated)
```

#### `MiuiWallpaperManager`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\IMiuiWallpaperManagerInner.java-4-import android.util.SparseArray;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\IMiuiWallpaperManagerInner.java-5-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\IMiuiWallpaperManagerInner.java-6-/* JADX INFO: loaded from: classes3.dex */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\IMiuiWallpaperManagerInner.java:7:public interface IMiuiWallpaperManagerInner {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\IMiuiWallpaperManagerInner.java-8-    int getCurrentUserId();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\IMiuiWallpaperManagerInner.java-9-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpaper\IMiuiWallpaperManagerInner.java-10-    Object getLock();
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java-18-import com.android.thememanager.util.WallpaperEditor;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java-19-import com.miui.keyguard.editor.data.bean.WallpaperPositionInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java-20-import com.miui.keyguard.editor.utils.BitmapUtil;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java:21:import com.miui.miwallpaper.MiuiWallpaperManager;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java-22-import com.personalizedEditor.interceptor.WallpaperInterceptorChainHandler;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java-23-import java.util.LinkedHashMap;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java-24-import java.util.List;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java-142-        this.f54324p = "WallpaperApplyVM";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java-143-        this.z = new MutableLiveData<>();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java-144-        this.t = "";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java:145:        int i3 = DeviceUtils.lvui() ? MiuiWallpaperManager.f : 3;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java-146-        this.o = i3;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java-147-        this.m = i3;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\viewmodel\WallpaperApplyVM.java-148-    }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\WallpaperExtraHelper.java-50-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\WallpaperExtraHelper.java-51-    public final int zy() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\WallpaperExtraHelper.java-52-        if (toq() == null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\WallpaperExtraHelper.java:53:            Log.d(f54272toq, "getMiWallpaperCapability , but mMiuiWallpaperManager is null");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\WallpaperExtraHelper.java-54-            return 0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\WallpaperExtraHelper.java-55-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\helper\WallpaperExtraHelper.java-56-        try {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-22-import com.miui.keyguard.editor.data.bean.CommonConfig;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-23-import com.miui.keyguard.editor.data.bean.WallpaperInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-24-import com.miui.keyguard.editor.viewmodel.IntWrapper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java:25:import com.miui.miwallpaper.IMiuiWallpaperManagerCallback;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-26-import com.personalizedEditor.dialog.EditorActivityViewModel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-27-import com.personalizedEditor.dialog.EffectChooseDialog;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-28-import com.personalizedEditor.helper.SettingHelper;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-360-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-361-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-362-    /* JADX INFO: compiled from: r8-map-id-28176a47c9109cff812a5f7a0a997c1bce7bf6885f244bf2996d02e43b70343a */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java:363:    public static final class WallpaperChangerListener extends IMiuiWallpaperManagerCallback.Stub {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-364-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-365-        @NotNull
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-366-        private final WeakReference<WallpaperEffectDialogActivity> weakActivity;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-370-            this.weakActivity = new WeakReference<>(activity);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-371-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-372-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java:373:        @Override // com.miui.miwallpaper.IMiuiWallpaperManagerCallback
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-374-        public void onWallpaperChanged(@Nullable WallpaperColors wallpaperColors, @Nullable String str, int i2) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-375-            WallpaperEffectDialogActivity wallpaperEffectDialogActivity = this.weakActivity.get();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-376-            Log.d(WallpaperEffectDialogActivity.l, "onWallpaperChanged , activity is valid " + (wallpaperEffectDialogActivity != null));
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-383-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-384-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-385-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java:386:        @Override // com.miui.miwallpaper.IMiuiWallpaperManagerCallback
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-387-        public void onDrawFrameEnd() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-388-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-389-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java:390:        @Override // com.miui.miwallpaper.IMiuiWallpaperManagerCallback
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-391-        public void onWallpaperFirstFrameRendered(int i2) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-392-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\activity\WallpaperEffectDialogActivity.java-393-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personaliz
... (truncated)
```

## 3. Desktop Wallpaper Scrolling & Effects

### 3.1 OS4

#### `wallpaper_scrolling`
```java
(no matches)
```

#### `wallpaper_scroll`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\aod\R.java-53739-        public static final int kg_wallpaper_scale_hint_text = 0x7f12037d;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\aod\R.java-53740-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\aod\R.java-53741-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\aod\R.java:53742:        public static final int kg_wallpaper_scroll_type_disable_tips = 0x7f12037e;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\aod\R.java-53743-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\aod\R.java-53744-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\aod\R.java:53745:        public static final int kg_wallpaper_scroll_type_tips = 0x7f12037f;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\aod\R.java-53746-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\aod\R.java-53747-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\aod\R.java-53748-        public static final int kg_word_spacing = 0x7f120380;
```

#### `wallpaper_effect`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\systemui\shared\Flags.java-14-    public static final String FLAG_ENABLE_RECENTS_IN_TASKBAR = "com.android.systemui.shared.enable_recents_in_taskbar";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\systemui\shared\Flags.java-15-    public static final String FLAG_ENABLE_SAGE = "com.android.systemui.shared.enable_sage";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\systemui\shared\Flags.java-16-    public static final String FLAG_EXAMPLE_SHARED_FLAG = "com.android.systemui.shared.example_shared_flag";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\systemui\shared\Flags.java:17:    public static final String FLAG_EXTENDED_WALLPAPER_EFFECTS = "com.android.systemui.shared.extended_wallpaper_effects";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\systemui\shared\Flags.java-18-    public static final String FLAG_EXTENDIBLE_THEME_MANAGER = "com.android.systemui.shared.extendible_theme_manager";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\systemui\shared\Flags.java-19-    public static final String FLAG_LAUNCHER_ANIMATION_SHELL_MIGRATION = "com.android.systemui.shared.launcher_animation_shell_migration";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\systemui\shared\Flags.java:20:    public static final String FLAG_PAN_AND_ZOOM_IN_EXTENDED_WALLPAPER_EFFECTS = "com.android.systemui.shared.pan_and_zoom_in_extended_wallpaper_effects";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\systemui\shared\Flags.java-21-    public static final String FLAG_PHOTO_SHUFFLE_FLAG = "com.android.systemui.shared.photo_shuffle_flag";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\systemui\shared\Flags.java-22-    public static final String FLAG_SHADE_ALLOW_BACK_GESTURE = "com.android.systemui.shared.shade_allow_back_gesture";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\systemui\shared\Flags.java-23-    public static final String FLAG_SIDEFPS_CONTROLLER_REFACTOR = "com.android.systemui.shared.sidefps_controller_refactor";
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java-39-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java-40-    @Override // com.android.server.SystemService
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java-41-    public void onStart() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java:42:        publishBinderService("wallpaper_effects_generation", new WallpaperEffectsGenerationManagerStub());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java-43-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java-44-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java-45-    @Override // com.android.server.infra.AbstractMasterSystemService
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\keyguard\data\repository\KeyguardCommonSettingsRepository.java-101-        this.animationRate = FlowKt.stateIn(miuiSystemSettingsRepository.intSetting(0, "miui_home_animation_rate"), coroutineScope, startedEagerly, 0);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\keyguard\data\repository\KeyguardCommonSettingsRepository.java-102-        this.isSupportLockScreenTextClick = FlowKt.stateIn(miuiSecureSettingsRepository.boolSetting("support_lock_screen_text_click", false), coroutineScope, startedEagerly, bool);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\keyguard\data\repository\KeyguardCommonSettingsRepository.java-103-        this.pickupSensorSettingsOpened = FlowKt.stateIn(miuiSystemSettingsRepository.boolSetting("pick_up_gesture_wakeup_mode", false), coroutineScope, startedEagerly, bool);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\keyguard\data\repository\KeyguardCommonSettingsRepository.java:104:        this.miuiWallpaperHomeEffect = FlowKt.stateIn(miuiSecureSettingsRepository.intSetting(0, "wallpaper_effect_type_1"), coroutineScope, startedEagerly, 0);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\keyguard\data\repository\KeyguardCommonSettingsRepository.java:105:        Flow flowIntSetting7 = miuiSecureSettingsRepository.intSetting(0, "wallpaper_effect_type_2");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\keyguard\data\repository\KeyguardCommonSettingsRepository.java-106-        this.miuiWallpaperEffect = FlowKt.stateIn(flowIntSetting7, coroutineScope, startedEagerly, 0);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\keyguard\data\repository\KeyguardCommonSettingsRepository.java-107-        KeyguardCommonSettingsRepository$special$$inlined$map$1 keyguardCommonSettingsRepository$special$$inlined$map$7 = new KeyguardCommonSettingsRepository$special$$inlined$map$1(7);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\keyguard\data\repository\KeyguardCommonSettingsRepository.java-108-        keyguardCommonSettingsRepository$special$$inlined$map$7.$this_unsafeTransform$inlined = flowIntSetting7;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6879-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6880-            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6881-            {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:6882:                super(iUserTracker, executor, secureSettings, handler, "wallpaper_effect_type_2", i, 0, true);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6883-                this.$r8$classId = i;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6884-                switch (i) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6885-                    case 1:
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6939-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6940-            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6941-            {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:6942:                super(iUserTracker, executor, secureSettings, handler, "wallpaper_effect_type_2", i, 0, true);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6943-                this.$r8$classId = i;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6944-                switch (i) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-6945-                    case 1:
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-80-                if (constantLockscreenInfoConvertJsonToConstantLockInfo == null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-81-                    z = true;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-82-                } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java:83:                    if (Settings.Secure.getStringForUser(keyguardOTAInteractor.context.getContentResolver(), "wallpaper_changed_2", iUserTracker.getUserId()) == null && Settings.Secure.getIntForUser(keyguardOTAInteractor.context.getContentResolver(), "wallpaper_effect_type_2", 0, iUserTracker.getUserId()) == 0) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-84-                        Object obj2 = (Settings.Secure.getIntForUser(keyguardOTAInteractor.context.getContentResolver(), "lockscreen_info_version", 1, iUserTracker.getUserId()) == 2 && MiuiConfigs.IS_REDMI_BRAND && !Build.IS_INTERNATIONAL_BUILD) ? "classic_plus" : "classic";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-85-                        if (obj2.equals("classic")) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-86-                            ClockBean clockInfo = constantLockscreenInfoConvertJsonToConstantLockInfo.getClockInfo();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\c
... (truncated)
```

#### `WALLPAPER_CHANGED`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1622-    public class ImmobulusBroadcastReceiver extends BroadcastReceiver {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1623-        public ImmobulusBroadcastReceiver() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1624-            IntentFilter intent = new IntentFilter();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java:1625:            intent.addAction("android.intent.action.WALLPAPER_CHANGED");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1626-            intent.addAction("android.net.conn.CONNECTIVITY_CHANGE");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1627-            intent.addAction(AurogonImmobulusMode.BROADCAST_SATELLITE);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1628-            intent.addAction(VibratorManagerServiceImpl.ACTION_AUDIO_LOWTEMP_SWITCH);
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1632-        @Override // android.content.BroadcastReceiver
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1633-        public void onReceive(Context context, Intent intent) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1634-            String action = intent.getAction();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java:1635:            if ("android.intent.action.WALLPAPER_CHANGED".equals(action)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1636-                AurogonImmobulusMode.this.getWallpaperPackageName();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1637-                return;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1638-            }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-89-        @Override // android.content.BroadcastReceiver
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-90-        public void onReceive(Context context, Intent intent) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-91-            CrossListPreloader crossListPreloader;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java:92:            if (!Intrinsics.areEqual("android.intent.action.WALLPAPER_CHANGED", intent != null ? intent.getAction() : null) || (crossListPreloader = EditorApplicationProxy.Companion.getCrossListPreloader()) == null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-93-                return;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-94-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-95-            crossListPreloader.invalidData("wallpaper changed from broadcast");
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-236-        Intrinsics.checkNotNullParameter(application, "application");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-237-        app = application;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-238-        submitSuicide();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java:239:        ContextCompat.registerReceiver(application, wallpaperChangeBroadcastReceiver, new IntentFilter("android.intent.action.WALLPAPER_CHANGED"), 2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-240-        WallpaperController.Companion.getInstance(application, false).registerWallpaperChangedListener(wallpaperChangedCallBack, WallpaperWhich.AllWhich.INSTANCE.getWhich());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-241-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-242-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\KeyguardEditorMiSightUtil.java-438-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\KeyguardEditorMiSightUtil.java-439-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\KeyguardEditorMiSightUtil.java-440-    public final void reportWaitWallpaperChangedTimeout(final long j) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\KeyguardEditorMiSightUtil.java:441:        dfxLog("WAIT_WALLPAPER_CHANGED_TIMEOUT", "duration=" + j + "ms");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\KeyguardEditorMiSightUtil.java-442-        getMiSightHandler().post(new Runnable() { // from class: com.miui.keyguard.editor.utils.KeyguardEditorMiSightUtil$$ExternalSyntheticLambda16
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\KeyguardEditorMiSightUtil.java-443-            @Override // java.lang.Runnable
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\KeyguardEditorMiSightUtil.java-444-            public final void run() {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\app\WallpaperManager.java-95-    public static final String EXTRA_FROM_FOREGROUND_APP = "android.service.wallpaper.extra.FROM_FOREGROUND_APP";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\app\WallpaperManager.java-96-    public static final String EXTRA_LIVE_WALLPAPER_COMPONENT = "android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\app\WallpaperManager.java-97-    public static final String EXTRA_NEW_WALLPAPER_ID = "android.service.wallpaper.extra.ID";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\app\WallpaperManager.java:98:    public static final String EXTRA_WHICH_WALLPAPER_CHANGED = "android.service.wallpaper.extra.WHICH_WALLPAPER_CHANGED";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\app\WallpaperManager.java-99-    public static final int FLAG_DESKTOP_LOCK = 3;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\app\WallpaperManager.java-100-    public static final int FLAG_LOCK = 2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\app\WallpaperManager.java-101-    public static final int FLAG_MULTI_DESKTOP_LOCK = 12;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\content\Intent.java-424-    public static final String ACTION_VOICE_COMMAND = "android.intent.action.VOICE_COMMAND";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\content\Intent.java-425-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\content\Intent.java-426-    @Deprecated
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\content\Intent.java:427:    public static final String ACTION_WALLPAPER_CHANGED = "android.intent.action.WALLPAPER_CHANGED";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\content\Intent.java-428-    public static final String ACTION_WEB_SEARCH = "android.intent.action.WEB_SEARCH";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\content\Intent.java-429-    public static final String ACTION_WELLBEING_CONFIRM_WITH_SPEEDBUMP = "android.intent.action.WELLBEING_CONFIRM_WITH_SPEEDBUMP";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\content\Intent.java-430-    private static final String ATTR_ACTION = "action";
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-4030-    /* JADX INFO: Access modifiers changed from: private */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-4031-    public void notifyWallpaperChanged(WallpaperData wallpaper) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-4032-        this.mHasSetWallpaper.set(true);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java:4033:        Intent intent = new Intent("android.intent.action.WALLPAPER_CHANGED");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-4034-        intent.putExtra("android.service.wallpaper.extra.FROM_FOREGROUND_APP", wallpaper.fromForegroundApp);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java:4035:        intent.putExtra("android.service.wallpaper.extra.WHICH_WALLPAPER_CHANGED", wallpaper.mWhich);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\services_decompiler\sources\com\android\server\wallpaper\WallpaperManagerService.java-4036-        this.mContext.sendBroadcastAsUser(intent, new UserHandle(this.mCurrentUserId));
E:/work/Android Project/_reverse-eng-archive/os4_andr
... (truncated)
```

#### `wallpaperChanged`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-95-            crossListPreloader.invalidData("wallpaper changed from broadcast");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-96-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-97-    };
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java:98:    public static final ProcessManager$wallpaperChangedCallBack$1 wallpaperChangedCallBack = new IMiuiWallpaperManagerCallback.Stub() { // from class: com.miui.keyguard.editor.utils.ProcessManager$wallpaperChangedCallBack$1
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-99-        @Override // com.miui.miwallpaper.IMiuiWallpaperManagerCallback
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-100-        public void onDrawFrameEnd() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-101-        }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-237-        app = application;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-238-        submitSuicide();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-239-        ContextCompat.registerReceiver(application, wallpaperChangeBroadcastReceiver, new IntentFilter("android.intent.action.WALLPAPER_CHANGED"), 2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java:240:        WallpaperController.Companion.getInstance(application, false).registerWallpaperChangedListener(wallpaperChangedCallBack, WallpaperWhich.AllWhich.INSTANCE.getWhich());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-241-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-242-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-243-    public final void killProcess() {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-253-        Application application = app;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-254-        if (application != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-255-            application.unregisterReceiver(wallpaperChangeBroadcastReceiver);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java:256:            WallpaperController.Companion.getInstance(application, false).unregisterWallpaperChangedListener(wallpaperChangedCallBack);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-257-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-258-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-259-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-48-    public final SecureSettings secureSettings;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-49-    public final WallpaperRepositoryImpl$special$$inlined$filter$1 selectedUser;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-50-    public final ReadonlyStateFlow shouldSendFocalArea;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java:51:    public final FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1 wallpaperChanged;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-52-    public final ReadonlyStateFlow wallpaperInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-53-    public final WallpaperManager wallpaperManager;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-54-    public final Flow wallpaperSupportsAmbientMode;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-128-        this.wallpaperManager = wallpaperManager;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-129-        this.context = context;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-130-        this.secureSettings = secureSettings;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java:131:        this.wallpaperChanged = FlowKt.onStart(broadcastDispatcher.broadcastFlow(new IntentFilter("android.intent.action.WALLPAPER_CHANGED"), (14 & 2) != 0 ? null : UserHandle.ALL, (14 & 4) != 0 ? 2 : 4, (14 & 8) == 0 ? "com.android.systemui.permission.SELF" : null, new BroadcastDispatcher$$ExternalSyntheticLambda0()), new WallpaperRepositoryImpl$wallpaperChanged$1(2, null));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-132-        ReadonlyStateFlow readonlyStateFlow = ((UserRepositoryImpl) userRepository).selectedUser;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-133-        WallpaperRepositoryImpl$special$$inlined$filter$1 wallpaperRepositoryImpl$special$$inlined$filter$1 = new WallpaperRepositoryImpl$special$$inlined$filter$1();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-134-        wallpaperRepositoryImpl$special$$inlined$filter$1.$this_unsafeTransform$inlined = readonlyStateFlow;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-154-        if (!this.wallpaperManager.isWallpaperSupported()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-155-            return new ReadonlyStateFlow(StateFlowKt.MutableStateFlow(null));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-156-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java:157:        return FlowKt.stateIn(LatestConflatedKt.mapLatestConflated(FlowKt.flowCombine(this.wallpaperChanged, this.selectedUser, AnonymousClass3.INSTANCE), new AnonymousClass4(i, null)), this.scope, SharingStarted.Companion.Eagerly, null);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-158-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl.java-159-}
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl$wallpaperChanged$1.java-11-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl$wallpaperChanged$1.java-12-/* JADX INFO: compiled from: go/retraceme 19940c44e4700823c48f6ef2e4cde0339962c7a4beafae4dae2762261bf9dd38 */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl$wallpaperChanged$1.java-13-/* JADX INFO: loaded from: classes3.dex */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl$wallpaperChanged$1.java:14:final class WallpaperRepositoryImpl$wallpaperChanged$1 extends SuspendLambda implements Function2 {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl$wallpaperChanged$1.java-15-    public /* synthetic */ Object L$0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl$wallpaperChanged$1.java-16-    int label;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl$wallpaperChanged$1.java-17-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl$wallpaperChanged$1.java-18-    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl$wallpaperChanged$1.java-19-    public final Continuation create(Object obj, Continuation continuation) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\systemui\wallpapers\data\repository\WallpaperRepositoryImpl$wallpaperChanged$1.java:20:        WallpaperRepositoryImpl$wallpaperChanged$1 wallpaperRepositoryImpl$wallpaperChanged$1 = new WallpaperRep
... (truncated)
```

#### `wallpaperAnim`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\view\ViewRootImpl.java-27398-    /* JADX INFO: Access modifiers changed from: private */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\view\ViewRootImpl.java-27399-    public /* synthetic */ void lambda$addCutFrameSelfBlurCallback$26(int blur, long frame) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\view\ViewRootImpl.java-27400-        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\view\ViewRootImpl.java:27401:        List<WallpaperAnimationTarget> wallpaperAnimTargets = WindowManagerGlobal.getInstance().getAllWallpaperAnimationTarget(0);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\view\ViewRootImpl.java:27402:        Slog.d(TAG, "setSelfBlurRadius true and radius = " + blur + " wallpaperAnimTargets = " + wallpaperAnimTargets.size());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\view\ViewRootImpl.java:27403:        for (int i = 0; i < wallpaperAnimTargets.size(); i++) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\view\ViewRootImpl.java:27404:            SurfaceControl surfaceControl = wallpaperAnimTargets.get(i) != null ? wallpaperAnimTargets.get(i).getLeash() : null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\view\ViewRootImpl.java-27405-            if (surfaceControl != null && surfaceControl.isValid()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\view\ViewRootImpl.java-27406-                if (blur == 0) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\framework_decompiler\sources\android\view\ViewRootImpl.java-27407-                    Slog.d(TAG, "setSelfBlurRadius false and surfaceControl = " + surfaceControl + " radius = " + blur);
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java-382-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java-383-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java-384-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java:385:    private Point calculatePosition(WallpaperAnimationTarget wallpaperAnimTarget) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java:386:        if (wallpaperAnimTarget == null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java:387:            Slog.w(TAG, "calculatePosition: wallpaperAnimTarget is null");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java-388-            return null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java-389-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java-390-        float wallpaperXOffset = 0.0f;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java-391-        float wallpaperYOffset = 0.0f;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java-392-        try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java:393:            float[] wallpaperOffsets = wallpaperAnimTarget.getPositionArray();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java-394-            if (wallpaperOffsets.length >= 2) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java-395-                wallpaperXOffset = wallpaperOffsets[0];
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-services_decompiler\sources\com\android\server\wm\MiuiWallpaperSurfaceAnimation.java-396-                wallpaperYOffset = wallpaperOffsets[1];
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1622-                    public final /* synthetic */ boolean $show;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1623-                    public final /* synthetic */ SyncRtSurfaceTransactionApplier $surfaceTransactionApplier;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1624-                    public final /* synthetic */ KeyguardState $toState;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:1625:                    public final /* synthetic */ WallpaperAnimationTarget $wallpaperAnimTarget;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1626-                    public final KeyguardAnimTracer.Manual scaleAnimTracer;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1627-                    public final /* synthetic */ KeyguardPanelViewController this$0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1628-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1631-                        this.this$0 = keyguardPanelViewController;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1632-                        this.$fromState = keyguardState;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1633-                        this.$toState = keyguardState2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:1634:                        this.$wallpaperAnimTarget = wallpaperByShowWhenLocked;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1635-                        this.$surfaceTransactionApplier = syncRtSurfaceTransactionApplier;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1636-                        this.$homeWallpaperTarget = wallpaperByShowWhenLocked2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1637-                        String str3 = z ? "KeyguardAnim-FullAod2Lock" : "KeyguardAnim-Lock2FullAod";
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1680-                        if (updateInfoFindByName == null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1681-                            return;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1682-                        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:1683:                        Log.i("KeyguardPanelViewController", "doWallpaperScaleAnim update: " + this.$wallpaperAnimTarget + " - " + updateInfoFindByName.getFloatValue());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:1684:                        if (this.$wallpaperAnimTarget != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:1685:                            this.this$0.doDeductedImageScaleAnim(updateInfoFindByName.getFloatValue(), this.$surfaceTransactionApplier, this.$wallpaperAnimTarget.leash, this.$homeWallpaperTarget);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1686-                        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1687-                    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-1688-                });
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2246-        KeyguardPanelViewController$$ExternalSyntheticOutline0.m(this.keyguardBlurRatio, "doBlurAndDim: ratio = ", "KeyguardPanelViewController");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2247-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2248-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:2249:    public final void doDeductedImageScaleAnim(float f, SyncRtSurfaceTransactionApplier syncRtSurfaceTransactionApplier, SurfaceControl surfaceControl, WallpaperAnimationTarget wallpaperAnimationTarget) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2250-        ViewRootImpl viewRootImpl;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2251-        SurfaceControl surfaceControl2;
E:/work/Android Project/_reverse-eng-archive/os4_android17
... (truncated)
```

#### `scrollWallpaper`
```java
(no matches)
```

#### `wallpaper_scrolling_enabled`
```java
(no matches)
```

#### `wallpaper_tilt`
```java
(no matches)
```

#### `wallpaper_zoom`
```java
(no matches)
```

### 3.2 OS3

#### `wallpaper_scrolling`
```java
(no matches)
```

#### `wallpaper_scroll`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\util\BaseModeManager.java-83-    private static final String bv = "home_wallpaper";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\util\BaseModeManager.java-84-    public static final String c = "home_wallpaper_type";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\util\BaseModeManager.java-85-    public static final int d = 8;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\util\BaseModeManager.java:86:    private static final String e = "home_wallpaper_scrolled";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\util\BaseModeManager.java-87-    public static final String f = "lockscreen_authority";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\util\BaseModeManager.java-88-    private static final String id = "lockstyle";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\util\BaseModeManager.java-89-    private static final String in = "black_wallpaper";
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-22296-        public static final int wallpaper_preview_flip_dark = 0x7f060bb6;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-22297-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-22298-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java:22299:        public static final int wallpaper_scroll_type = 0x7f060bb7;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-22300-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-22301-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-22302-        public static final int wallpaper_setting_action_bar_title_color = 0x7f060bb8;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-49360-        public static final int wallpaper_recycleview_horizontal_margin_start = 0x7f0719e9;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-49361-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-49362-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java:49363:        public static final int wallpaper_scroll_text_margin_end = 0x7f0719ea;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-49364-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-49365-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java:49366:        public static final int wallpaper_scroll_text_size = 0x7f0719eb;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-49367-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-49368-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java:49369:        public static final int wallpaper_scroll_type_padding_end = 0x7f0719ec;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-49370-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-49371-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java:49372:        public static final int wallpaper_scroll_type_padding_top = 0x7f0719ed;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-49373-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-49374-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-49375-        public static final int wallpaper_setting_add_margin_top = 0x7f0719ee;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-67162-        public static final int wallpaper_phone_icon = 0x7f08109b;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-67163-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-67164-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java:67165:        public static final int wallpaper_scroll_select = 0x7f08109c;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-67166-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-67167-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java:67168:        public static final int wallpaper_scroll_selected = 0x7f08109d;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-67169-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-67170-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-67171-        public static final int wallpaper_setting_add_bg = 0x7f08109e;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-92684-        public static final int kg_wallpaper_scale_hint_text = 0x7f120557;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-92685-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-92686-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java:92687:        public static final int kg_wallpaper_scroll_type_disable_tips = 0x7f120558;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-92688-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-92689-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java:92690:        public static final int kg_wallpaper_scroll_type_tips = 0x7f120559;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-92691-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-92692-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-92693-        public static final int kg_word_spacing = 0x7f12055a;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-97085-        public static final int wallpaper_online_tip = 0x7f120b1a;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-97086-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-97087-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java:97088:        public static final int wallpaper_scroll_type_tips = 0x7f120b1b;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-97089-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-97090-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\R.java-97091-        public static final int wallpaper_set_as_both = 0x7f120b1c;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\R.java-51906-        public static final int kg_wallpaper_scale_hint_text = 0x7f12034c;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\R.java-51907-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\R.java-51908-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\R.java:51909:        public static final int kg_wallpaper_scroll_type_disable_tips = 0x7f12034d;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\R.java-51910-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\R.java-51911-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\R.java:51912:        public static final int kg_wallpaper_scroll_type_tips = 0x7f12034e;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\R.java-51913-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\R.java-51914-        /* JADX INFO: Added by JADX */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\R.java-51915-        public static final int kg_word_spacing = 0x7f12034f;
```

#### `wallpaper_effect`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\systemui\shared\Flags.java-11-    public static final String FLAG_ENABLE_HOME_DELAY = "com.android.systemui.shared.enable_home_delay";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\systemui\shared\Flags.java-12-    public static final String FLAG_ENABLE_LPP_SQUEEZE_EFFECT = "com.android.systemui.shared.enable_lpp_squeeze_effect";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\systemui\shared\Flags.java-13-    public static final String FLAG_EXAMPLE_SHARED_FLAG = "com.android.systemui.shared.example_shared_flag";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\systemui\shared\Flags.java:14:    public static final String FLAG_EXTENDED_WALLPAPER_EFFECTS = "com.android.systemui.shared.extended_wallpaper_effects";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\systemui\shared\Flags.java-15-    public static final String FLAG_LOCKSCREEN_CUSTOM_CLOCKS = "com.android.systemui.shared.lockscreen_custom_clocks";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\systemui\shared\Flags.java-16-    public static final String FLAG_NEW_CUSTOMIZATION_PICKER_UI = "com.android.systemui.shared.new_customization_picker_ui";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\systemui\shared\Flags.java-17-    public static final String FLAG_NEW_TOUCHPAD_GESTURES_TUTORIAL = "com.android.systemui.shared.new_touchpad_gestures_tutorial";
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\dialog\WallpaperFilterView.java-228-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\dialog\WallpaperFilterView.java-229-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\dialog\WallpaperFilterView.java-230-    private final void y() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\dialog\WallpaperFilterView.java:231:        View viewInflate = LayoutInflater.from(getContext()).inflate(R.layout.wallpaper_effect_dialog_view, (ViewGroup) this, true);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\dialog\WallpaperFilterView.java-232-        ImageView imageView = (ImageView) viewInflate.findViewById(R.id.head_icon);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\dialog\WallpaperFilterView.java-233-        this.f54199k = imageView;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\personalizedEditor\dialog\WallpaperFilterView.java-234-        if (imageView != null) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java-39-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java-40-    @Override // com.android.server.SystemService
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java-41-    public void onStart() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java:42:        publishBinderService("wallpaper_effects_generation", new WallpaperEffectsGenerationManagerStub());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java-43-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java-44-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\services_decompiler\sources\com\android\server\wallpapereffectsgeneration\WallpaperEffectsGenerationManagerService.java-45-    @Override // com.android.server.infra.AbstractMasterSystemService
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-198-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-199-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-200-    public static int h(Context context, int i2) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java:201:        return Settings.Secure.getInt(context.getContentResolver(), "wallpaper_effect_type_" + i2, 0);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-202-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-203-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\material\utils\PartRectColorUtils.java-204-    private static boolean i() {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImplKt.java-24-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImplKt.java-25-    /* JADX INFO: renamed from: zy, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImplKt.java-26-    @NotNull
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImplKt.java:27:    private static final String f48880zy = "wallpaper_effect_type_2";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImplKt.java-28-}
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-174-        map.put("deviceType", Integer.valueOf(CommonUtils.f51274k.q()));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-175-        map.put(DevInfoKeys.f56268q, Integer.valueOf(DeviceUtil.f51326k.bo()));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-176-        map.put("wallpaper_matting_support_2", Boolean.valueOf(TemplateDataUtil.f48888k.ki(this.f48874toq)));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java:177:        map.put("wallpaper_effect_type_2", Integer.valueOf(Settings.Secure.getInt(this.f48874toq.getContentResolver(), "wallpaper_effect_type_2", 0)));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-178-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-179-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-180-    private final String jk() {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-205-                TemplateDataUtil.f48888k.zurt(this.f48874toq, ((Boolean) orDefault).booleanValue());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-206-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-207-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java:208:        Object orDefault2 = map.getOrDefault("wallpaper_effect_type_2", 0);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-209-        if (orDefault2 != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java:210:            Log.d("Keyguard-Editor-TemplateBackupApiImpl", "restoreSettingsInternal: wallpaper_effect_type_2=" + orDefault2);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-211-            if (orDefault2 instanceof Number) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java:212:                Settings.Secure.putInt(this.f48874toq.getContentResolver(), "wallpaper_effect_type_2", ((Number) orDefault2).intValue());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-213-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-214-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\template\TemplateBackupApiImpl.java-215-    }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\WallpaperApplyHelper.java-300-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\WallpaperApplyHelper.java-301-    public static int kja0(boolean isLock, Context context) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\WallpaperApplyHelper.java-302-        int i2 = isLock ? 2 : 1;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\WallpaperApplyHelper.java:303:        int i3 = Settings.Secure.getInt(context.getContentResolver(), "wallpaper_effect_type_" + i2, 0);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\android\thememanager\WallpaperApplyHelper.java-304-        Log.d(f15995k, "get magicType from wallpaper magicType is " + i3);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler
... (truncated)
```

#### `WALLPAPER_CHANGED`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1478-        public ImmobulusBroadcastReceiver() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1479-            IntentFilter intent = new IntentFilter();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1480-            intent.addAction(AurogonImmobulusMode.IMMOBULUS_GAME_CONTROLLER);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java:1481:            intent.addAction("android.intent.action.WALLPAPER_CHANGED");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1482-            intent.addAction("android.net.conn.CONNECTIVITY_CHANGE");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1483-            intent.addAction(AurogonImmobulusMode.BROADCAST_SATELLITE);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1484-            intent.addAction(VibratorManagerServiceImpl.ACTION_AUDIO_LOWTEMP_SWITCH);
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1516-                }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1517-                return;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1518-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java:1519:            if ("android.intent.action.WALLPAPER_CHANGED".equals(action)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1520-                AurogonImmobulusMode.this.getWallpaperPackageName();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1521-                return;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miui-services_decompiler\sources\com\miui\server\greeze\AurogonImmobulusMode.java-1522-            }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java-271-        @Override // android.content.BroadcastReceiver
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java-272-        public void onReceive(Context context, Intent intent) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java-273-            String action = intent.getAction();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java:274:            if ("android.intent.action.WALLPAPER_CHANGED".equals(action) || "miui.gallery.action.WALLPAPER_CHANGED".equals(action) || "android.intent.action.UPDATE_DESKTOP_VIDEO_WALLPAPER".equals(action)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java-275-                Log.d("AppPredictService", "AppPredictService::onReceive::Wallpaper changed.");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java-276-                AppPredictService.this.f22469d.sendMessage(AppPredictService.this.f22469d.obtainMessage(9));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java-277-            }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java-407-        intentFilter.addDataScheme("package");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java-408-        E.q(this.f22466a, this.f22470e, S0.H(-1), intentFilter, 4);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java-409-        IntentFilter intentFilter2 = new IntentFilter();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java:410:        intentFilter2.addAction("android.intent.action.WALLPAPER_CHANGED");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java:411:        intentFilter2.addAction("miui.gallery.action.WALLPAPER_CHANGED");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java-412-        intentFilter2.addAction("android.intent.action.UPDATE_DESKTOP_VIDEO_WALLPAPER");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java-413-        h hVar = new h();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\securitycenter_decompiler\sources\com\miui\apppredict\service\AppPredictService.java-414-        this.f22471f = hVar;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-35-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-36-/* JADX INFO: compiled from: WallpaperController.kt */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java-37-/* JADX INFO: loaded from: classes5.dex */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\systemserver\executor\WallpaperController.java:38:@Metadata(d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b(\n\u0002\u0010\u0003\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u0000 ]2\u00020\u0001:\u0001]B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010*\u001a\u00020+2\b\u0010,\u001a\u0004\u0018\u00010-J\u0006\u0010.\u001a\u00020+J\u0006\u0010/\u001a\u000200J\u000e\u00101\u001a\u00020+2\u0006\u00102\u001a\u000203J\u0010\u00104\u001a\u00020+2\u0006\u00102\u001a\u000203H\u0002J\b\u00105\u001a\u00020+H\u0002J\b\u00106\u001a\u00020+H\u0002J\b\u00107\u001a\u00020+H\u0002J\b\u00108\u001a\u00020+H\u0002J\b\u00109\u001a\u00020+H\u0002J\u0010\u0010:\u001a\u00020+2\u0006\u00102\u001a\u000203H\u0002J\b\u0010;\u001a\u00020+H\u0002J\u0018\u0010<\u001a\u00020+2\u0006\u0010=\u001a\u00020\t2\u0006\u0010>\u001a\u00020\u0007H\u0002J\u0018\u0010?\u001a\u0002002\u0006\u0010=\u001a\u00020\t2\u0006\u0010>\u001a\u00020\u0007H\u0002J\u0018\u0010@\u001a\u00020\u00182\u0006\u0010A\u001a\u00020\u00182\u0006\u0010B\u001a\u00020\tH\u0002J\u0018\u0010C\u001a\u00020+2\u0006\u0010D\u001a\u00020\t2\u0006\u0010E\u001a\u00020\tH\u0002J\u0018\u0010F\u001a\u00020+2\u0006\u0010=\u001a\u00020\t2\u0006\u0010G\u001a\u00020\tH\u0002J\u001e\u0010H\u001a\u0004\u0018\u00010\t2\b\u0010I\u001a\u0004\u0018\u00010\u00182\b\u0010J\u001a\u0004\u0018\u00010\u0018H\u0002J\u0018\u0010K\u001a\u00020+2\u0006\u0010E\u001a\u00020\t2\u0006\u0010L\u001a\u00020\u0018H\u0002J\u0018\u0010M\u001a\u00020+2\u0006\u0010E\u001a\u00020\t2\u0006\u0010L\u001a\u00020\u0018H\u0002J\u0018\u0010N\u001a\u00020+2\u0006\u0010E\u001a\u00020\t2\u0006\u0010L\u001a\u00020\u0018H\u0002J\u0018\u0010O\u001a\u00020+2\u0006\u0010E\u001a\u00020\t2\u0006\u0010L\u001a\u00020\u0018H\u0002J\u0010\u0010P\u001a\u00020+2\u0006\u0010L\u001a\u00020\u0018H\u0002J\u0010\u0010Q\u001a\u00020+2\u0006\u0010L\u001a\u00020\u0018H\u0002J\u0012\u0010R\u001a\u0004\u0018\u00010\u00182\u0006\u0010L\u001a\u00020\u0018H\u0002J\u0017\u0010S\u001a\u0004\u0018\u00010\u00072\u0006\u0010L\u001a\u00020\u0018H\u0002¢\u0006\u0002\u0010TJ\u0018\u0010U\u001a\u00020+2\u0006\u0010L\u001a\u00020\u00182\u0006\u0010V\u001a\u00020\u0018H\u0002J\u0018\u0010W\u001a\u00020+2\u0006\u0010L\u001a\u00020\u00182\u0006\u0010V\u001a\u00020\u0007H\u0002J\u0010\u0010X\u001a\u00020+2\u0006\u0010Y\u001a\u00020\u0018H\u0002J\u001c\u0010Z\u001a\u00020+2\u0006\u0010Y\u001a\u00020\u00182\n\b\u0002\u0010[\u001a\u0004\u0018\u00010\\H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\u00020\t8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0014\u0010\u000e\u001a\u00020\t8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\rR\u0014\u0010\u0010\u001a\u00020\t8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0011\u0010\rR\u0014\u0010\u0012\u001a\u00020\t8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\rR\u000e\u0010\u0014\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0015\u001a\u00020\t8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0016\u0010\rR\u000e\u0010\u0017\u001a\u00020\u0018X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0018X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0018X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0018X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0018X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u0018X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0018X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0018X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0018X\u0082D¢\u0006\u0002\n\u0000R\u0014\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00180\"X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u0018X\u0082D¢\u0006\u0002\n\u0000R\u001b\u0010$\u001a\u00020%8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b(\u0010)\u001a\u0004\b&\u0010'Ê\u0001\f\b_\u0012\b\b`\u0012\u0004\b\u0003\u0010\u0000¨\u0006^"}, d2 = {"Lcom/banana/hypermodes/systemserver/executor/WallpaperController;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "userId", "", "systemDir", "Ljava/io/File;", "backupRoot", "lockBackupDir", "getLockBackupDir", "()Ljava/io/File;", "desktopBackupDir", "getDesktopBackupDir", "lockOrigFile", "getLockOrigFile", "desktopOrigFile", "getDesktopOrigFile", "aodTemplateDir", "subjectMaskFile", "getSubjectMaskFile", "MARKER_LOCK_FOLLOWS_HOME", "", "KEY_LOCKSCREEN_INFO", "KEY_TEMPLATE_EDITOR_INFO", "KEY_DEFAULT_LOCKSCREEN_INFO", "KEY_LOCKSCREEN_INFO_VERSION", "KEY_DESKTOP_SCROLL", "KEY_WALLPAPER_EFFECT_1", "KEY_WALLPAPER_EFFECT_2", "KEY_WALLP
... (truncated)
```

#### `wallpaperChanged`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-121-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-122-    /* JADX INFO: renamed from: qrj, reason: collision with root package name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-123-    @NotNull
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java:124:    private static final ProcessManager$wallpaperChangedCallBack$1 f51509qrj = new IMiuiWallpaperManagerCallback.Stub() { // from class: com.miui.keyguard.editor.utils.ProcessManager$wallpaperChangedCallBack$1
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-125-        @Override // com.miui.miwallpaper.IMiuiWallpaperManagerCallback
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-126-        public void onWallpaperChanged(@Nullable WallpaperColors wallpaperColors, @Nullable String str, int i2) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\utils\ProcessManager.java-127-            if (ProcessManager.f51504k.qrj(ProcessManager.f51506n)) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplateView.java-347-                                @Nullable
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplateView.java-348-                                /* JADX INFO: renamed from: k, reason: merged with bridge method [inline-methods] */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplateView.java-349-                                public final Object emit(@NotNull String str, @NotNull Continuation<? super Unit> continuation) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplateView.java:350:                                    Log.i(SettingsTemplateView.ad, "onMergeWallPaperAndSettingsConfigChange: dataSourceChanged=" + str + ", wallpaperChangedCount=" + settingsTemplateView2.ip + ", configChangedCount=" + settingsTemplateView2.bb + ", applyingMaml=" + CurrentTemplateDataSource.f51064n.k() + ", isFromMatchInternal=" + settingsTemplateView2.bp + ", isApplyCurrentTemplate=" + settingsTemplateView2.x + ", isApplyingTemplateInternal=" + settingsTemplateView2.u);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplateView.java-351-                                    if (!Intrinsics.areEqual(str, SettingsConfigChangeDataSource.f51082n)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplateView.java-352-                                        settingsTemplateView2.f51043a = true;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplateView.java-353-                                        Log.d(SettingsTemplateView.ad, "capture for wallpaper changed");
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\WallpaperImageView.java-174-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\WallpaperImageView.java-175-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\WallpaperImageView.java-176-    @Override // com.miui.keyguard.editor.edit.wallpaper.IWallpaperLayer
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\WallpaperImageView.java:177:    public void ki(@Nullable WallpaperChangedListener wallpaperChangedListener) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\WallpaperImageView.java:178:        IWallpaperLayer.DefaultImpls.k(this, wallpaperChangedListener);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\WallpaperImageView.java-179-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\WallpaperImageView.java-180-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\WallpaperImageView.java-181-    @Override // com.miui.keyguard.editor.edit.wallpaper.IWallpaperLayer
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-264-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-265-    boolean i();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-266-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java:267:    void ki(@Nullable WallpaperChangedListener wallpaperChangedListener);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-268-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-269-    @Nullable
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-270-    View kja0();
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-314-            return null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-315-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-316-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java:317:        public static void k(@NotNull IWallpaperLayer iWallpaperLayer, @Nullable WallpaperChangedListener wallpaperChangedListener) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java:318:            if (wallpaperChangedListener == null || iWallpaperLayer.getWallpaperChangedListenerSet() == null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-319-                return;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-320-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java:321:            HashSet<WallpaperChangedListener> wallpaperChangedListenerSet = iWallpaperLayer.getWallpaperChangedListenerSet();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java:322:            Intrinsics.checkNotNull(wallpaperChangedListenerSet);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java:323:            wallpaperChangedListenerSet.add(wallpaperChangedListener);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-324-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-325-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\IWallpaperLayer.java-326-        public static /* synthetic */ WallpaperPositionInfo n(IWallpaperLayer iWallpaperLayer, Bitmap bitmap, int i2, Object obj) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java-377-        Bitmap bitmap;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java-378-        Bitmap bitmap2;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java-379-        WallpaperCallback wallpaperCallback;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java:380:        HashSet<WallpaperChangedListener> wallpaperChangedListenerSet;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java-381-        Ref.ObjectRef objectRef;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java-382-        final Ref.ObjectRef objectRef2;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java-383-        String str;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java-400-                bitmap2 = null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java-401-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java-402-            wallpaperCallback = getWallpaperCallback();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java:403:            wallpaperChangedListenerSet = getWallpaperChangedListenerSet();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java-404-            objectRef = new Ref.ObjectRef();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\edit\wallpaper\CombinedWallpaperView.java-405-            objectRef.element = this.f49972k;
E:/work/Androi
... (truncated)
```

#### `wallpaperAnim`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\android\view\ViewRootImpl.java-28652-    /* JADX INFO: Access modifiers changed from: private */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\android\view\ViewRootImpl.java-28653-    public /* synthetic */ void lambda$addCutFrameSelfBlurCallback$25(int blur, long frame) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\android\view\ViewRootImpl.java-28654-        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\android\view\ViewRootImpl.java:28655:        List<WallpaperAnimationTarget> wallpaperAnimTargets = WindowManagerGlobal.getInstance().getAllWallpaperAnimationTarget(0);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\android\view\ViewRootImpl.java:28656:        Slog.d(TAG, "setSelfBlurRadius true and radius = " + blur + " wallpaperAnimTargets = " + wallpaperAnimTargets.size());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\android\view\ViewRootImpl.java:28657:        for (int i = 0; i < wallpaperAnimTargets.size(); i++) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\android\view\ViewRootImpl.java:28658:            SurfaceControl surfaceControl = wallpaperAnimTargets.get(i) != null ? wallpaperAnimTargets.get(i).getLeash() : null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\android\view\ViewRootImpl.java-28659-            if (surfaceControl != null && surfaceControl.isValid()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\android\view\ViewRootImpl.java-28660-                if (blur == 0) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\framework_decompiler\sources\android\view\ViewRootImpl.java-28661-                    Slog.d(TAG, "setSelfBlurRadius false and surfaceControl = " + surfaceControl + " radius = " + blur);
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2229-                        float floatValue = updateInfoFindByName.getFloatValue();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2230-                        SyncRtSurfaceTransactionApplier syncRtSurfaceTransactionApplier2 = syncRtSurfaceTransactionApplier;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2231-                        SurfaceControl surfaceControl2 = wallpaperByShowWhenLocked.leash;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:2232:                        WallpaperAnimationTarget wallpaperAnimationTarget = wallpaperByShowWhenLocked2;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2233-                        KeyguardPanelViewController keyguardPanelViewController3 = keyguardPanelViewController;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2234-                        Point lockScreenSize = MiuiConfigs.getLockScreenSize(keyguardPanelViewController3.context);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2235-                        float f = lockScreenSize.x * 0.5f;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2248-                                return;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2249-                            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2250-                            float[] fArrCalculateWallpaperMatrixArray = keyguardPanelViewController3.calculateWallpaperMatrixArray(floatValue, f, f2);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:2251:                            if (wallpaperAnimationTarget != null && (surfaceControl = wallpaperAnimationTarget.leash) != null && surfaceControl.isValid()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:2252:                                syncRtSurfaceTransactionApplier2.scheduleApply(new SyncRtSurfaceTransactionApplier.SurfaceParams[]{new SyncRtSurfaceTransactionApplier.SurfaceParams.Builder(wallpaperAnimationTarget.leash).withMatrix((Matrix) keyguardPanelViewController3.matrix$delegate.getValue()).withAlpha(fArrCalculateWallpaperMatrixArray[4]).build()});
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2253-                            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2254-                            ConstraintLayout constraintLayout = keyguardPanelViewController3.keyguardRootView;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-2255-                            if (constraintLayout == null || (viewRootImpl = constraintLayout.getViewRootImpl()) == null) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3107-                setWallpaperZoom(z ? 0.75f : 1.0f);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3108-                WallpaperAnimationTarget wallpaperByShowWhenLocked = WindowManagerGlobal.getInstance().getWallpaperByShowWhenLocked(0, true);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3109-                if (wallpaperByShowWhenLocked == null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:3110:                    Log.d("KeyguardPanelViewController", "initDeductedImageScale: wallpaperAnimTarget is null");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3111-                    return;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3112-                }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3113-                SurfaceControl surfaceControl = wallpaperByShowWhenLocked.leash;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3699-                            return;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3700-                        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3701-                        float floatValue = updateInfoFindByName.getFloatValue();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:3702:                        WallpaperAnimationTarget wallpaperAnimationTarget = wallpaperByShowWhenLocked;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:3703:                        WallpaperAnimationTarget wallpaperAnimationTarget2 = wallpaperByShowWhenLocked2;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3704-                        KeyguardPanelViewController keyguardPanelViewController = KeyguardPanelViewController.this;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3705-                        keyguardPanelViewController.getClass();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3706-                        try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3707-                            Point lockScreenSize = MiuiConfigs.getLockScreenSize(keyguardPanelViewController.context);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3708-                            keyguardPanelViewController.calculateWallpaperMatrixArray(floatValue, lockScreenSize.x * 0.5f, lockScreenSize.y * 0.4f);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3709-                            SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:3710:                            if (wallpaperAnimationTarget2 != null && (surfaceControl2 = wallpaperAnimationTarget2.leash) != null && surfaceControl2.isValid()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:3711:                                SurfaceControl surfaceControl3 = wallpaperAnimationTarget2.leash;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3712-                                surfaceControl3.getClass();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3713-                                transaction.setMatrix(surfaceControl3, (Matrix) keyguardPanelViewController.matrix$delegate.getValue(), (float[]) keyguardPanelViewController.tmpMatrixArray$delegate.getValue());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-3714-                            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:3715:                         
... (truncated)
```

#### `scrollWallpaper`
```java
(no matches)
```

#### `wallpaper_scrolling_enabled`
```java
(no matches)
```

#### `wallpaper_tilt`
```java
(no matches)
```

#### `wallpaper_zoom`
```java
(no matches)
```

## 4. EditorActivity & isMiuiCall

### 4.1 OS4

#### `EditorActivity`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\waterbox\R.java-42956-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601da;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\waterbox\R.java-42957-        public static final int EditDialog = 0x7f1601db;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\waterbox\R.java-42958-        public static final int EditUserDialogTitle = 0x7f1601dc;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\waterbox\R.java:42959:        public static final int EditorActivityTheme = 0x7f1601dd;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\waterbox\R.java-42960-        public static final int EntityHeader = 0x7f1601de;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\waterbox\R.java-42961-        public static final int ExpandSubtitleStyle = 0x7f1601df;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\waterbox\R.java-42962-        public static final int ExpandTitleStyle = 0x7f1601e0;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\miui\security\SecurityManager.java-46-    public static final String SIMULATED_TOUCH_TIME = "lastSimulatedTouchTime";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\miui\security\SecurityManager.java-47-    public static final String SIMULATED_TOUCH_UID = "lastSimulatedTouchUid";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\miui\security\SecurityManager.java-48-    public static final String SKIP_INTERCEPT = "skip_interception";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\miui\security\SecurityManager.java:49:    public static final String SKIP_INTERCEPT_ACTIVITY_GALLERY_EDIT = "com.miui.gallery.editor.photo.screen.home.ScreenEditorActivity";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\miui\security\SecurityManager.java-50-    public static final String SKIP_INTERCEPT_ACTIVITY_GALLERY_EXTRA = "com.miui.gallery.activity.ExternalPhotoPageActivity";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\miui\security\SecurityManager.java-51-    public static final String SKIP_INTERCEPT_ACTIVITY_GALLERY_EXTRA_TRANSLUCENT = "com.miui.gallery.activity.TranslucentExternalPhotoPageActivity";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miui-framework_decompiler\sources\miui\security\SecurityManager.java-52-    public static final String SKIP_INTERCEPT_PACKAGE = "com.miui.gallery";
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\blurdrawable\R.java-42956-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601da;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\blurdrawable\R.java-42957-        public static final int EditDialog = 0x7f1601db;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\blurdrawable\R.java-42958-        public static final int EditUserDialogTitle = 0x7f1601dc;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\blurdrawable\R.java:42959:        public static final int EditorActivityTheme = 0x7f1601dd;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\blurdrawable\R.java-42960-        public static final int EntityHeader = 0x7f1601de;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\blurdrawable\R.java-42961-        public static final int ExpandSubtitleStyle = 0x7f1601df;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\blurdrawable\R.java-42962-        public static final int ExpandTitleStyle = 0x7f1601e0;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\flexible\R.java-42956-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601da;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\flexible\R.java-42957-        public static final int EditDialog = 0x7f1601db;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\flexible\R.java-42958-        public static final int EditUserDialogTitle = 0x7f1601dc;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\flexible\R.java:42959:        public static final int EditorActivityTheme = 0x7f1601dd;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\flexible\R.java-42960-        public static final int EntityHeader = 0x7f1601de;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\flexible\R.java-42961-        public static final int ExpandSubtitleStyle = 0x7f1601df;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\flexible\R.java-42962-        public static final int ExpandTitleStyle = 0x7f1601e0;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\animation\R.java-42956-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601da;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\animation\R.java-42957-        public static final int EditDialog = 0x7f1601db;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\animation\R.java-42958-        public static final int EditUserDialogTitle = 0x7f1601dc;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\animation\R.java:42959:        public static final int EditorActivityTheme = 0x7f1601dd;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\animation\R.java-42960-        public static final int EntityHeader = 0x7f1601de;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\animation\R.java-42961-        public static final int ExpandSubtitleStyle = 0x7f1601df;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miuix\animation\R.java-42962-        public static final int ExpandTitleStyle = 0x7f1601e0;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\splitlib\R.java-42956-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601da;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\splitlib\R.java-42957-        public static final int EditDialog = 0x7f1601db;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\splitlib\R.java-42958-        public static final int EditUserDialogTitle = 0x7f1601dc;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\splitlib\R.java:42959:        public static final int EditorActivityTheme = 0x7f1601dd;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\splitlib\R.java-42960-        public static final int EntityHeader = 0x7f1601de;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\splitlib\R.java-42961-        public static final int ExpandSubtitleStyle = 0x7f1601df;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\splitlib\R.java-42962-        public static final int ExpandTitleStyle = 0x7f1601e0;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\commonlib\R.java-42956-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601da;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\commonlib\R.java-42957-        public static final int EditDialog = 0x7f1601db;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\commonlib\R.java-42958-        public static final int EditUserDialogTitle = 0x7f1601dc;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\commonlib\R.java:42959:        public static final int EditorActivityTheme = 0x7f1601dd;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\commonlib\R.java-42960-        public static final int EntityHeader = 0x7f1601de;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\commonlib\R.java-42961-        public static final int ExpandSubtitleStyle = 0x7f1601df;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\miui\settings\commonlib\R.java-42962-        public static final int ExpandTitleStyle = 0x7f1601e0;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\com\miui\maml\R.java-42956-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601da;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\com\miui\maml\R.java-42957-        public static final int EditDialog = 0x7f1601db;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\com\miui\maml\R.java-42958-        public static final int EditUserDialogTitle = 0x7f1601dc;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\com\miui\maml\R.java:42959:        public static final int EditorActivityTheme = 0x7f1601dd;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\com\miui\maml\R.java-42960-        public static final int EntityHeader = 0x7f1601de;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\com\miui\maml\R.java-42961-        public static final int ExpandSubtitleStyle = 0x7f1601df;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\com\miui\maml\R.java-42962-        public static final int ExpandTitleStyle = 0x7f1601e0;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\com\xiaomi\mirror\opensdk\R.java-42956-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601da;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\com\xiaomi\mirror\opensdk\R.java-42957-        public static final int EditDialog = 0x7f1601db;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\com\xiaomi\mirror\opensdk\R.java-42958-        public static final int EditUserDialogTitle = 0x7f1601dc;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\com\xiaomi\mirror\opensdk\R.java:42959:        public static final int EditorActivityTheme = 0x7f1601dd;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\sources\com\xiaomi\mirror\opensdk\R.java-42960-        public static final int EntityHeader = 0x7f1601de;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\settings_decompiler\s
... (truncated)
```

#### `isMiuiCall`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1280-            Log.i("Keyguard-Editor-EditorActivity", "callingFromSystemUI");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1281-            return;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1282-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java:1283:        if (isMiuiCall() || isAppFunctionCall()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1284-            SettingsEditorServiceManager.INSTANCE.setActivity(this);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1285-            if (intent != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1286-                intent.putExtra("openSource", 1);
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1402-    public void onPause() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1403-        this.wallpaperPickerShowing = true;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1404-        Log.i("Keyguard-Editor-EditorActivity", "editor onPause");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java:1405:        if (this.applySuccess && (callingFromLockCross() || isMiuiCall())) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1406-            overridePendingTransition(0, 0);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1407-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1408-        super.onPause();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2011-        Folme.use((View) viewGroup2).state().to(new AnimState().add(ViewProperty.TRANSLATION_Y, f, new long[0]), new AnimConfig().setEase(-2, 0.95f, 0.3f));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2012-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2013-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java:2014:    public final boolean isMiuiCall() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2015-        String launchedFromPackage = getLaunchedFromPackage();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2016-        Log.d("Keyguard-Editor-EditorActivity", "launchPackage=" + launchedFromPackage);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2017-        if (Intrinsics.areEqual(launchedFromPackage, "com.android.shell") && Intrinsics.areEqual(this.isADB, "adb")) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java-161-            Log.i("Keyguard-Editor-CommonEditorActivity", "common callingFromSystemUI");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java-162-            return;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java-163-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java:164:        if (isMiuiCall() || isAppFunctionCall()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java-165-            SettingsEditorServiceManager.INSTANCE.setActivity(this);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java-166-            if (intent != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java-167-                intent.putExtra("openSource", 1);
```

### 4.2 OS3

#### `EditorActivity`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\waterbox\R.java-39470-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601cb;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\waterbox\R.java-39471-        public static final int EditDialog = 0x7f1601cc;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\waterbox\R.java-39472-        public static final int EditUserDialogTitle = 0x7f1601cd;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\waterbox\R.java:39473:        public static final int EditorActivityTheme = 0x7f1601ce;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\waterbox\R.java-39474-        public static final int EntityHeader = 0x7f1601cf;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\waterbox\R.java-39475-        public static final int ExpandSubtitleStyle = 0x7f1601d0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\waterbox\R.java-39476-        public static final int ExpandTitleStyle = 0x7f1601d1;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\blurdrawable\R.java-39470-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601cb;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\blurdrawable\R.java-39471-        public static final int EditDialog = 0x7f1601cc;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\blurdrawable\R.java-39472-        public static final int EditUserDialogTitle = 0x7f1601cd;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\blurdrawable\R.java:39473:        public static final int EditorActivityTheme = 0x7f1601ce;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\blurdrawable\R.java-39474-        public static final int EntityHeader = 0x7f1601cf;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\blurdrawable\R.java-39475-        public static final int ExpandSubtitleStyle = 0x7f1601d0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\blurdrawable\R.java-39476-        public static final int ExpandTitleStyle = 0x7f1601d1;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\flexible\R.java-39470-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601cb;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\flexible\R.java-39471-        public static final int EditDialog = 0x7f1601cc;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\flexible\R.java-39472-        public static final int EditUserDialogTitle = 0x7f1601cd;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\flexible\R.java:39473:        public static final int EditorActivityTheme = 0x7f1601ce;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\flexible\R.java-39474-        public static final int EntityHeader = 0x7f1601cf;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\flexible\R.java-39475-        public static final int ExpandSubtitleStyle = 0x7f1601d0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\flexible\R.java-39476-        public static final int ExpandTitleStyle = 0x7f1601d1;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\animation\R.java-39470-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601cb;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\animation\R.java-39471-        public static final int EditDialog = 0x7f1601cc;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\animation\R.java-39472-        public static final int EditUserDialogTitle = 0x7f1601cd;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\animation\R.java:39473:        public static final int EditorActivityTheme = 0x7f1601ce;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\animation\R.java-39474-        public static final int EntityHeader = 0x7f1601cf;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\animation\R.java-39475-        public static final int ExpandSubtitleStyle = 0x7f1601d0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miuix\animation\R.java-39476-        public static final int ExpandTitleStyle = 0x7f1601d1;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\splitlib\R.java-39470-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601cb;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\splitlib\R.java-39471-        public static final int EditDialog = 0x7f1601cc;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\splitlib\R.java-39472-        public static final int EditUserDialogTitle = 0x7f1601cd;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\splitlib\R.java:39473:        public static final int EditorActivityTheme = 0x7f1601ce;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\splitlib\R.java-39474-        public static final int EntityHeader = 0x7f1601cf;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\splitlib\R.java-39475-        public static final int ExpandSubtitleStyle = 0x7f1601d0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\splitlib\R.java-39476-        public static final int ExpandTitleStyle = 0x7f1601d1;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\commonlib\R.java-39470-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601cb;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\commonlib\R.java-39471-        public static final int EditDialog = 0x7f1601cc;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\commonlib\R.java-39472-        public static final int EditUserDialogTitle = 0x7f1601cd;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\commonlib\R.java:39473:        public static final int EditorActivityTheme = 0x7f1601ce;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\commonlib\R.java-39474-        public static final int EntityHeader = 0x7f1601cf;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\commonlib\R.java-39475-        public static final int ExpandSubtitleStyle = 0x7f1601d0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\miui\settings\commonlib\R.java-39476-        public static final int ExpandTitleStyle = 0x7f1601d1;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\xiaomi\mirror\opensdk\R.java-39470-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601cb;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\xiaomi\mirror\opensdk\R.java-39471-        public static final int EditDialog = 0x7f1601cc;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\xiaomi\mirror\opensdk\R.java-39472-        public static final int EditUserDialogTitle = 0x7f1601cd;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\xiaomi\mirror\opensdk\R.java:39473:        public static final int EditorActivityTheme = 0x7f1601ce;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\xiaomi\mirror\opensdk\R.java-39474-        public static final int EntityHeader = 0x7f1601cf;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\xiaomi\mirror\opensdk\R.java-39475-        public static final int ExpandSubtitleStyle = 0x7f1601d0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\xiaomi\mirror\opensdk\R.java-39476-        public static final int ExpandTitleStyle = 0x7f1601d1;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\miui\maml\R.java-39470-        public static final int EdgeToEdgeFloatingDialogWindowTheme = 0x7f1601cb;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\miui\maml\R.java-39471-        public static final int EditDialog = 0x7f1601cc;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\miui\maml\R.java-39472-        public static final int EditUserDialogTitle = 0x7f1601cd;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\miui\maml\R.java:39473:        public static final int EditorActivityTheme = 0x7f1601ce;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\miui\maml\R.java-39474-        public static final int EntityHeader = 0x7f1601cf;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\miui\maml\R.java-39475-        public static final int ExpandSubtitleStyle = 0x7f1601d0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\settings_decompiler\sources\com\miui\maml\R.java-39476-        public static final int ExpandTitleStyle = 0x7f1601d1;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\recents\NavStubView.java-6418-        if (this.mCalculator.isFastPullUp() && this.mWindowMode == 4) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\recents\NavStubView.java-6419-            ActivityManager.RunningTaskInfo runningTaskInfo = this.mRunningTaskInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\recents\NavStubView.java-6420-            ComponentName componentName = runningTaskInfo.baseActivity;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\recents\NavStubView.java:6421:            if (componentName != null && runningTaskInfo.topActivity != null && "com.miui.keyguard.editor.EditorActivity".equals(componentName.getClassName())) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\recents\NavStubView.java-6422-                if (TextUtils.equals(this.mRunningTaskInfo.baseActivity.getPackageName(), this.mRunningTaskInfo.topActivity.getPackageName())) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\recents\NavStubView.java-6423-                    sendBroadcastToAppForHandleGesture(getActionUpSpeedAndDirection());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuihome_decompiler\sources\com\miui\home\recents\NavStubView.java-6424-                } else {
--
E:/work/
... (truncated)
```

#### `isMiuiCall`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-38-                    if (i < length) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-39-                        obj = objArr[i];
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-40-                        Method method2 = (Method) obj;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java:41:                        if (Intrinsics.areEqual(method2.getName(), "isMiuiCall") && method2.getParameterCount() == 0) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-42-                            break;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-43-                        } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-44-                            i++;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-52-            } catch (Throwable th) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-53-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-54-            if (method == null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java:55:                log("isMiuiCall not found");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-56-            } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-57-                this.module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(new XposedInterface.Hooker() { // from class: com.banana.hypermodes.hook.AodEditorHook.install.1
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-58-                    public Object intercept(XposedInterface.Chain chain) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-59-                        Intrinsics.checkNotNullParameter(chain, "chain");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-60-                        String caller = AodEditorHook.this.callingPackage(chain);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-61-                        if (Intrinsics.areEqual(caller, "com.banana.hypermodes")) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java:62:                            AodEditorHook.this.log("isMiuiCall: allowing " + caller);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-63-                            return true;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-64-                        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-65-                        return chain.proceed();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-66-                    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-67-                });
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java:68:                log("isMiuiCall hooked");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-69-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-70-        } catch (Throwable t) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\hypermodes_decompiler\sources\com\banana\hypermodes\hook\AodEditorHook.java-71-            log("EditorActivity not found: " + t.getMessage());
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1240-            Log.i("Keyguard-Editor-EditorActivity", "callingFromSystemUI");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1241-            return;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1242-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java:1243:        if (isMiuiCall()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1244-            SettingsEditorServiceManager.INSTANCE.setActivity(this);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1245-            if (intent != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1246-                intent.putExtra("openSource", 1);
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1964-        Folme.use((View) viewGroup2).state().to(new AnimState().add(ViewProperty.TRANSLATION_Y, f, new long[0]), new AnimConfig().setEase(-2, 0.95f, 0.3f));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1965-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1966-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java:1967:    public final boolean isMiuiCall() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1968-        String launchedFromPackage = getLaunchedFromPackage();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1969-        Log.d("Keyguard-Editor-EditorActivity", "launchPackage=" + launchedFromPackage);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-1970-        return ConstantsKt.getCALL_PACKAGE_ALLOW().contains(launchedFromPackage);
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java-154-            Log.i("Keyguard-Editor-CommonEditorActivity", "common callingFromSystemUI");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java-155-            return;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java-156-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java:157:        if (isMiuiCall()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java-158-            SettingsEditorServiceManager.INSTANCE.setActivity(this);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java-159-            if (intent != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\CommonEditorActivity.java-160-                intent.putExtra("openSource", 1);
```

## 5. NotificationNumStateView & Zen Row

### 5.1 OS4

#### `NotificationNumStateView`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-34-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-35-/* JADX INFO: compiled from: go/retraceme 19940c44e4700823c48f6ef2e4cde0339962c7a4beafae4dae2762261bf9dd38 */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-36-/* JADX INFO: loaded from: classes3.dex */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java:37:public final class NotificationNumStateViewModel {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-38-    public final StateFlowImpl _bound;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-39-    public final StateFlowImpl _touchableRegion;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-40-    public final ReadonlyStateFlow affordanceWidth;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-64-    public final ReadonlyStateFlow touchableRegion;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-65-    public final ZenModeControllerImpl zenModeControllerImpl;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-66-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java:67:    public NotificationNumStateViewModel(ZenModeControllerImpl zenModeControllerImpl, NotificationNumRepositoryImpl notificationNumRepositoryImpl, KeyguardUpdateMonitorInjector keyguardUpdateMonitorInjector, StatusBarStateControllerExt statusBarStateControllerExt, ConfigurationController configurationController, KeyguardPanelViewController keyguardPanelViewController, dagger.Lazy lazy, KeyguardBottomAreaInjector keyguardBottomAreaInjector, CoroutineScope coroutineScope, NotificationScreenOnOffAnimator notificationScreenOnOffAnimator, dagger.Lazy lazy2, PowerInteractor powerInteractor, Context context) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-68-        this.zenModeControllerImpl = zenModeControllerImpl;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-69-        this.notificationNumRepository = notificationNumRepositoryImpl;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-70-        this.keyguardUpdateMonitorInjector = keyguardUpdateMonitorInjector;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-76-        this.affordanceWidth = ((MiuiConfigurationRepositoryImpl) lazy2.get()).getDimension(R.dimen.keyguard_affordance_fixed_width);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-77-        StateFlow stateFlow = (StateFlow) keyguardPanelViewController.nsslLockYPosition$delegate.getValue();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-78-        int i = 0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java:79:        NotificationNumStateViewModel$special$$inlined$map$1 notificationNumStateViewModel$special$$inlined$map$1 = new NotificationNumStateViewModel$special$$inlined$map$1(i);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-80-        notificationNumStateViewModel$special$$inlined$map$1.$this_unsafeTransform$inlined = stateFlow;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-81-        notificationNumStateViewModel$special$$inlined$map$1.this$0 = this;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-82-        VarHandle.storeStoreFence();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-89-        this.onLocaleListChanged = FlowKt.shareIn(ConfigurationControllerExtKt.getOnLocaleListChanged(configurationController), coroutineScope, startedEagerly, 1);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-90-        this.onThemeChanged = FlowKt.shareIn(ConfigurationControllerExtKt.getOnThemeChanged(configurationController), coroutineScope, startedEagerly, 1);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-91-        Flow zenModeFlow = ZenModeControllerExtKt.getZenModeFlow(zenModeControllerImpl);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java:92:        NotificationNumStateViewModel$special$$inlined$map$1 notificationNumStateViewModel$special$$inlined$map$2 = new NotificationNumStateViewModel$special$$inlined$map$1(i2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-93-        notificationNumStateViewModel$special$$inlined$map$2.$this_unsafeTransform$inlined = zenModeFlow;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-94-        notificationNumStateViewModel$special$$inlined$map$2.this$0 = this;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-95-        VarHandle.storeStoreFence();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-96-        this.isZenModeEnabled = FlowKt.stateIn(notificationNumStateViewModel$special$$inlined$map$2, coroutineScope, startedEagerly, Boolean.valueOf(ZenModeControllerExtKt.isZenModeOn(zenModeControllerImpl)));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-97-        ReadonlySynchronousStateFlow readonlySynchronousStateFlow = notificationNumRepositoryImpl.normalNotificationCount;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java:98:        NotificationNumStateViewModel$special$$inlined$map$3 notificationNumStateViewModel$special$$inlined$map$3 = new NotificationNumStateViewModel$special$$inlined$map$3(0);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-99-        notificationNumStateViewModel$special$$inlined$map$3.$this_unsafeTransform$inlined = readonlySynchronousStateFlow;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-100-        VarHandle.storeStoreFence();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-101-        this.normalNotificationCount = FlowKt.stateIn(notificationNumStateViewModel$special$$inlined$map$3, coroutineScope, startedEagerly, 1);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java:102:        NotificationNumStateViewModel$$ExternalSyntheticLambda0 notificationNumStateViewModel$$ExternalSyntheticLambda0 = new NotificationNumStateViewModel$$ExternalSyntheticLambda0(i);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-103-        notificationNumStateViewModel$$ExternalSyntheticLambda0.f$0 = this;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-104-        VarHandle.storeStoreFence();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-105-        Lazy lazy3 = LazyKt__LazyJVMKt.lazy(notificationNumStateViewModel$$ExternalSyntheticLambda0);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-106-        this.isNotificationCountViewVisible$delegate = lazy3;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-107-        ReadonlyStateFlow readonlyStateFlow = statusBarStateControllerExt.statusBarState;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java:108:        NotificationNumStateViewModel$special$$inlined$map$3 notificationNumStateViewModel$special$$inlined$map$4 = new NotificationNumStateViewModel$special$$inlined$map$3(1);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\notification\view\viewmodel\NotificationNumStateViewModel.java-109-        notificationNumStateViewModel$special$$inlined$map$4.$this_unsafeTransform$inlined = readonlyStateFlow;
E:/work/Android Project/_r
... (truncated)
```

#### `zen row`
```java
(no matches)
```

#### `zenRow`
```java
(no matches)
```

#### `ZenRow`
```java
(no matches)
```

### 5.2 OS3

#### `NotificationNumStateView`
```java
(no matches)
```

#### `zen row`
```java
(no matches)
```

#### `zenRow`
```java
(no matches)
```

#### `ZenRow`
```java
(no matches)
```

## 6. New OS4 Lockscreen Style / Wallpaper Services

### 6.1 OS4

#### `KeyguardOTAInteractor`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java-17-import android.widget.LinearLayout;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java-18-import androidx.collection.IntIntPair$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java-19-import com.android.keyguard.WallpaperProvider$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java:20:import com.android.keyguard.ota.KeyguardOTAInteractor$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java-21-import com.android.systemui.R;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java-22-import java.lang.invoke.VarHandle;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java-23-import miuix.appcompat.R$styleable;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java-318-                StringBuilder sbM2 = IntIntPair$$ExternalSyntheticOutline0.m(iMakeMeasureSpec3, View.MeasureSpec.getSize(i2), "getHeightMeasureSpecForDialog: measuredValue = ", ", size = ", ", fixedValue = ");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java-319-                sbM2.append(typedBaseValue2[0]);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java-320-                sbM2.append(", maxValue = ");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java:321:                KeyguardOTAInteractor$$ExternalSyntheticOutline0.m(sbM2, typedBaseValue2[1], ", useMaxLimit = ", z, ", mPanelMaxLimitHeight = ");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java-322-                sbM2.append(floatingABOLayoutSpec2.mPanelMaxLimitHeight);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java-323-                sbM2.append(", mIsFlipTinyScreen = ");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\internal\widget\DialogParentPanel2.java-324-                sbM2.append(floatingABOLayoutSpec2.mIsFlipTinyScreen);
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java-53-import com.android.keyguard.clock.animation.TinyClockBaseAnimation$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java-54-import com.android.keyguard.ext.KeyguardUnlockAnimationControllerExtKt$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java-55-import com.android.keyguard.fullaod.MiuiFullAodManager$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java:56:import com.android.keyguard.ota.KeyguardOTAInteractor$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java-57-import com.android.keyguard.panel.KeyguardPanelViewController$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java-58-import com.android.systemui.controlcenter.policy.MiuiFlashlightControllerImpl;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java-59-import java.lang.invoke.VarHandle;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java-2327-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java-2328-            if (this.mIsDebugEnabled) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java-2329-                StringBuilder sb = new StringBuilder("updateDialogPanelTranslationYByIme mPanelAndImeMargin ");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java:2330:                KeyguardOTAInteractor$$ExternalSyntheticOutline0.m(sb, this.mPanelAndImeMargin, " isMultiWindowMode ", zIsInMultiWindowMode2, " imeBottom ");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java-2331-                WallpaperProvider$$ExternalSyntheticOutline0.m(i2, "AlertController", sb);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java-2332-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\miuix\appcompat\app\AlertController.java-2333-            int i3 = (!zIsInMultiWindowMode2 || zIsTablet) ? (-i2) + this.mPanelAndImeMargin : -i2;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java-6-import androidx.compose.material3.internal.TextFieldImplKt$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java-7-import androidx.core.view.DisplayShapeCompat$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java-8-import com.android.keyguard.WallpaperProvider$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java:9:import com.android.keyguard.ota.KeyguardOTAInteractor$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java-10-import com.android.keyguard.tinyPanel.ClockInfo$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java-11-import java.util.List;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java-12-import kotlin.jvm.internal.Intrinsics;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java-74-        StringBuilder sbM = TextFieldImplKt$$ExternalSyntheticOutline0.m("OperatorConfig(defaultOperator=", ", CTSim=", ", hideVolte=", z, z2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java-75-        WallpaperProvider$$ExternalSyntheticOutline0.m(sbM, z3, ", hideVowifi=", z4, ", volteResId=");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java-76-        DisplayShapeCompat$$ExternalSyntheticOutline0.m(sbM, i, ", vowifiResId=", i2, ", vonrResId=");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java:77:        KeyguardOTAInteractor$$ExternalSyntheticOutline0.m(sbM, i3, ", separateDataAndVoice=", z5, ", showDataTypeDataDisconnected=");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java-78-        WallpaperProvider$$ExternalSyntheticOutline0.m(sbM, z6, ", showMobileDataTypeInMMS=", z7, ", showMobileDataTypeSingle=");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java-79-        WallpaperProvider$$ExternalSyntheticOutline0.m(sbM, z8, ", showSpnWhenAirplaneOn=", z9, ", showSpecial5GIcon=");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\interfaces\IOperatorCustomizedPolicy$OperatorConfig.java-80-        WallpaperProvider$$ExternalSyntheticOutline0.m(sbM, z10, ", support5GADisplay=", z11, ", hideNationalRoaming=");
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\charge\MiuiChargeController.java-45-import com.android.keyguard.injector.KeyguardIndicationInjector;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\charge\MiuiChargeController.java-46-import com.android.keyguard.injector.KeyguardUpdateMonitorInjector;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\charge\MiuiChargeController.java-47-import com.android.keyguard.magazine.KeyguardMagazineHelper;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\charge\MiuiChargeController.java:48:import com.android.keyguard.ota.KeyguardOTAInteractor$$ExternalSyntheticOutline0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\charge\MiuiChargeController.java-49-import com.android.keyguard.shortcut.MiuiShortcutController;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\charge\MiuiChargeController.java-50-import com.android.keyguard.stub.MiuiKeyguardUpdateMonitorInjectorStub;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\charge\MiuiChargeController.java-51-import com.android.keyguard.stub.MiuiKeyguardUpdateMonitorStub;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\charge\MiuiChargeController.java-509-        sbM.append(" chargeSpeed ");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\charge\MiuiChargeController.java-510-        sbM.append(batteryStatus.chargeSpeed);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\charge\MiuiChargeController.java-511-        sbM.append(" maxChargingWattage ");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\source
... (truncated)
```

#### `LockScreenInfoLayout`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-40-import org.json.JSONObject;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-41-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-42-/* JADX INFO: loaded from: classes2.dex */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:43:public class LockScreenInfoLayout extends RelativeLayout {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-44-    public boolean isSuperSaveOpen;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-45-    public ContentObserver mAccessibilityHighTextContrastObserver;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-46-    public boolean mAccessibilityHighTextEnabled;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-79-        return ((BaseTextView) getSignatureView()).getLineSpacingExtra();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-80-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-81-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:82:    public LockScreenInfoLayout(Context context) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-83-        this(context, null);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-84-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-85-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:86:    public LockScreenInfoLayout(Context context, AttributeSet attributeSet) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-87-        this(context, attributeSet, 0);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-88-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-89-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:90:    public LockScreenInfoLayout(Context context, AttributeSet attributeSet, int i) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-91-        super(context, attributeSet, i);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-92-        Handler handler = new Handler();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-93-        this.mHandler = handler;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-97-        this.mTextDark = false;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-98-        this.mNeedBuildFromSetting = false;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-99-        this.mEditorVersion = -1;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:100:        this.mListener = new ContentObserver(handler) { // from class: com.miui.lockscreeninfo.LockScreenInfoLayout.2
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-101-            @Override // android.database.ContentObserver
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-102-            public void onChange(boolean z) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:103:                Log.i("LockScreenInfoLayout", "parentView is " + LockScreenInfoLayout.this.getParent());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:104:                LockScreenInfoLayout.this.updateFromJson();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-105-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-106-        };
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-107-        initView(context);
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-152-    public void setModel(SignatureInfo signatureInfo) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-153-        if (signatureInfo == null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-154-            clear();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:155:            Log.e("LockScreenInfoLayout", "SignatureInfo is null");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-156-        } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-157-            this.mModel = signatureInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-158-            updateLayoutParams();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-242-    public void onConfigurationChanged(Configuration configuration) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-243-        super.onConfigurationChanged(configuration);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-244-        if (this.mModel != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:245:            Log.i("LockScreenInfoLayout", "onConfigurationChanged");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-246-            updateLayoutParams();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-247-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-248-    }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-269-        this.mPalette = map;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-270-        SignatureInfo signatureInfo = this.mModel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-271-        if (signatureInfo == null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:272:            Log.e("LockScreenInfoLayout", "mModel is " + this.mModel);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-273-            return;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-274-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-275-        signatureInfo.setTextDark(z);
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-294-            this.mModel.setFullAodPrimaryColor(GlobalColorUtils.transformFullAodColor(numValueOf.intValue()));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-295-            fullAodPrimaryColorToFullAod();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-296-            updateColor();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:297:            Log.e("LockScreenInfoLayout", "isAutoPrimaryColor =" + this.mModel.isAutoPrimaryColor() + " getHighTextContrastEnabled = " + getHighTextContrastEnabled() + " PrimaryColor = " + String.format("#%08X", Integer.valueOf(this.mModel.getPrimaryColor())));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-298-            return;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-299-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-300-        if (!zSupportBlur) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-350-        if (zSupportBlur) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-351-            updateClockBlendColor(SignatureInfo.isFullAODType(this.mModel.getDisplayType()) ? this.mModel.getFullAodBlendColor() : this.mModel.getBlendColor());
E:/work/Android Project/_reverse-eng-archive
... (truncated)
```

#### `ConstantLockscreenInfo`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-8-/* JADX INFO: compiled from: go/retraceme 19940c44e4700823c48f6ef2e4cde0339962c7a4beafae4dae2762261bf9dd38 */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-9-/* JADX INFO: loaded from: classes.dex */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-10-@Keep
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:11:public final class ConstantLockscreenInfo {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-12-    private final ClockBean clockInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-13-    private final DoodleInfo doodle;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-14-    private final SmartFrameInfo smartFrame;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-15-    private final WallpaperInfo wallpaperInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-16-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:17:    public /* synthetic */ ConstantLockscreenInfo(ClockBean clockBean, WallpaperInfo wallpaperInfo, DoodleInfo doodleInfo, SmartFrameInfo smartFrameInfo, int i, DefaultConstructorMarker defaultConstructorMarker) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-18-        this((i & 1) != 0 ? new ClockBean("all_in_one") : clockBean, (i & 2) != 0 ? null : wallpaperInfo, (i & 4) != 0 ? null : doodleInfo, (i & 8) != 0 ? null : smartFrameInfo);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-19-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-20-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:21:    public static /* synthetic */ ConstantLockscreenInfo copy$default(ConstantLockscreenInfo constantLockscreenInfo, ClockBean clockBean, WallpaperInfo wallpaperInfo, DoodleInfo doodleInfo, SmartFrameInfo smartFrameInfo, int i, Object obj) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-22-        if ((i & 1) != 0) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-23-            clockBean = constantLockscreenInfo.clockInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-24-        }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-50-        return this.smartFrame;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-51-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-52-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:53:    public final ConstantLockscreenInfo copy(ClockBean clockBean, WallpaperInfo wallpaperInfo, DoodleInfo doodleInfo, SmartFrameInfo smartFrameInfo) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:54:        return new ConstantLockscreenInfo(clockBean, wallpaperInfo, doodleInfo, smartFrameInfo);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-55-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-56-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-57-    public boolean equals(Object obj) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-58-        if (this == obj) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-59-            return true;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-60-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:61:        if (!(obj instanceof ConstantLockscreenInfo)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-62-            return false;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-63-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:64:        ConstantLockscreenInfo constantLockscreenInfo = (ConstantLockscreenInfo) obj;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-65-        return Intrinsics.areEqual(this.clockInfo, constantLockscreenInfo.clockInfo) && Intrinsics.areEqual(this.wallpaperInfo, constantLockscreenInfo.wallpaperInfo) && Intrinsics.areEqual(this.doodle, constantLockscreenInfo.doodle) && Intrinsics.areEqual(this.smartFrame, constantLockscreenInfo.smartFrame);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-66-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-67-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-92-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-93-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-94-    public String toString() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:95:        return "ConstantLockscreenInfo(clockInfo=" + this.clockInfo + ", wallpaperInfo=" + this.wallpaperInfo + ", doodle=" + this.doodle + ", smartFrame=" + this.smartFrame + ")";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-96-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-97-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:98:    public ConstantLockscreenInfo(ClockBean clockBean, WallpaperInfo wallpaperInfo, DoodleInfo doodleInfo, SmartFrameInfo smartFrameInfo) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-99-        this.clockInfo = clockBean;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-100-        this.wallpaperInfo = wallpaperInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-101-        this.doodle = doodleInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-102-        this.smartFrame = smartFrameInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-103-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-104-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:105:    public ConstantLockscreenInfo() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-106-        this(null, null, null, null, 15, null);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-107-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-108-}
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-117-import com.android.keyguard.utils.KeyguardAnimTracer;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-118-import com.android.keyguard.utils.KeyguardDepthLayerHelper$LayerConfig;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-119-import com.android.keyguard.wallpaper.MiuiKeyguardWallPaperManager;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\
... (truncated)
```

#### `constantLockscreenInfo`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-180-                miuiKeyguardWallPaperManager2.mOrientation = i4;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-181-                miuiKeyguardWallPaperManager2.mSmallestScreenWidthDp = i2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-182-                String str = MiuiConfigs.CUSTOMIZED_REGION;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java:183:                if (!MiuiMultiDisplayTypeInfo.isFlipDevice() && (MiuiConfigs.IS_PAD || MiuiConfigs.IS_FOLD || !"all_in_one".equals(((KeyguardPanelViewInjector) ((IKeyguardPanelViewInjector) InterfacesImplManager.getImpl(IKeyguardPanelViewInjector.class))).getKeyguardPanelViewController().constantLockscreenInfo.getClockInfo().getTemplateId()))) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-184-                    miuiKeyguardWallPaperManager2.updateColorAndDeep(false);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-185-                }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-186-            }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1018-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1019-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1020-    public final boolean shouldAbortAllInOneExtraction() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java:1021:        if ("all_in_one".equals(((KeyguardPanelViewInjector) ((IKeyguardPanelViewInjector) InterfacesImplManager.getImpl(IKeyguardPanelViewInjector.class))).getKeyguardPanelViewController().constantLockscreenInfo.getClockInfo().getTemplateId()) && !((DualClockObserver) InterfacesImplManager.getImpl(DualClockObserver.class)).mShowDualClock) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1022-            return false;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1023-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1024-        this.mPendingAllInOneOverride = false;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1113-                Settings.Secure.putInt(this.mContext.getContentResolver(), "color_scheme", this.mKeyguardColorScheme);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1114-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1115-            KeyguardPanelViewInjector keyguardPanelViewInjector = (KeyguardPanelViewInjector) ((IKeyguardPanelViewInjector) InterfacesImplManager.getImpl(IKeyguardPanelViewInjector.class));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java:1116:            String templateId = keyguardPanelViewInjector.getKeyguardPanelViewController().constantLockscreenInfo.getClockInfo().getTemplateId();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1117-            if (templateId.equals("doodle")) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1118-                if (((Integer) this.mPartDeepMap.get(str2)).intValue() == -1) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1119-                    int i = this.mKeyguardColorScheme;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1120-                    Map map = this.mFilterColorMap;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java:1121:                    DoodleInfo doodle = keyguardPanelViewInjector.getKeyguardPanelViewController().constantLockscreenInfo.getDoodle();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1122-                    if (doodle != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1123-                        boolean zIsAutoSolidColor = doodle.isAutoSolidColor();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1124-                        solidColor2 = doodle.getSolidColor();
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1135-                int i2 = this.mKeyguardColorScheme;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1136-                Map map2 = this.mFilterColorMap;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1137-                KeyguardPanelViewController keyguardPanelViewController = keyguardPanelViewInjector.getKeyguardPanelViewController();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java:1138:                SmartFrameInfo smartFrame = keyguardPanelViewController.constantLockscreenInfo.getSmartFrame();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1139-                if (smartFrame == null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1140-                    solidColor = 0;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-1141-                } else {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-18-        this((i & 1) != 0 ? new ClockBean("all_in_one") : clockBean, (i & 2) != 0 ? null : wallpaperInfo, (i & 4) != 0 ? null : doodleInfo, (i & 8) != 0 ? null : smartFrameInfo);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-19-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-20-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:21:    public static /* synthetic */ ConstantLockscreenInfo copy$default(ConstantLockscreenInfo constantLockscreenInfo, ClockBean clockBean, WallpaperInfo wallpaperInfo, DoodleInfo doodleInfo, SmartFrameInfo smartFrameInfo, int i, Object obj) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-22-        if ((i & 1) != 0) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:23:            clockBean = constantLockscreenInfo.clockInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-24-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-25-        if ((i & 2) != 0) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:26:            wallpaperInfo = constantLockscreenInfo.wallpaperInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-27-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-28-        if ((i & 4) != 0) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:29:            doodleInfo = constantLockscreenInfo.doodle;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-30-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-31-        if ((i & 8) != 0) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:32:            smartFrameInfo = constantLockscreenInfo.smartFrame;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-33-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:34:        return constantLockscreenInfo.copy(clockBean, wallpaperInfo, doodleInfo, smartFrameInfo);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-35-    
... (truncated)
```

#### `preProcessTemplateForOS4`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-128-        return (String) failure;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-129-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-130-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java:131:    public final String preProcessTemplateForOS4(String str) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-132-        ClockBean clockBeanDealWithOTAClockBean;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-133-        Object failure;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-134-        if (str == null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java:135:            Log.e("KeyguardOTAInteractor", "preProcessTemplateForOS4: LockscreenInfo is NULL! return default lockscreen info.");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-136-            return buildLockInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-137-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-138-        ConstantLockscreenInfo constantLockscreenInfoConvertJsonToConstantLockInfo = convertJsonToConstantLockInfo(str);
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-147-        MiSightHelper miSightHelper = this.miSightHelper;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-148-        if (clockBeanDealWithOTAClockBean == null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-149-            MiSightHelper.createAndSendEvent$default(miSightHelper, 923044601, null, 6);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java:150:            Log.e("KeyguardOTAInteractor", "preProcessTemplateForOS4: FATAL ERROR! return default lockscreen info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-151-            return buildLockInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-152-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\ota\KeyguardOTAInteractor.java-153-        if (needMergeToAllInOne(clockBeanDealWithOTAClockBean.getTemplateId())) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-100-                    z = false;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-101-                }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-102-                String strBuildLockInfo = keyguardOTAInteractor.buildLockInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java:103:                keyguardOTAInteractor.writeEditorInfoIntoSettings(KeyguardOTAInteractor.generateEditorInfo(z ? strBuildLockInfo : keyguardOTAInteractor.preProcessTemplateForOS4(Settings.Secure.getStringForUser(keyguardOTAInteractor.context.getContentResolver(), "constant_lockscreen_info", iUserTracker.getUserId()))));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-104-                keyguardOTAInteractor.updateDefaultLockInfo(strBuildLockInfo);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-105-                stringForUser = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-106-            } else if (!z2 || Settings.Secure.getIntForUser(keyguardOTAInteractor.context.getContentResolver(), "lockscreen_info_version", 1, iUserTracker.getUserId()) >= 4) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-107-                stringForUser = Settings.Secure.getStringForUser(keyguardOTAInteractor.context.getContentResolver(), "constant_template_editor_info", iUserTracker.getUserId());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-108-            } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-109-                keyguardOTAInteractor.updateDefaultLockInfo(keyguardOTAInteractor.buildLockInfo());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java:110:                String strPreProcessTemplateForOS4 = keyguardOTAInteractor.preProcessTemplateForOS4(keyguardOTAInteractor.parseEditorInfoToLockInfo(stringForUser2));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-111-                try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-112-                    JsonObject asJsonObject = JsonParser.parseString(stringForUser2).getAsJsonObject();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController$constantLockscreenInfoObserver$1$onChange$1.java-113-                    LinkedTreeMap linkedTreeMap = asJsonObject.members;
```

#### `LockscreenInfo`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-18-import com.android.keyguard.wallpaper.MiuiKeyguardWallPaperManager;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-19-import com.android.keyguard.wallpaper.entity.HomeInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-20-import com.android.keyguard.wallpaper.entity.KeyguardFavoriteTemplates;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java:21:import com.android.keyguard.wallpaper.entity.LockscreenInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-22-import com.android.keyguard.wallpaper.entity.TrackTemplateEditorInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-23-import com.android.systemui.statusbar.notification.analytics.INotificationStat;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-24-import com.android.systemui.statusbar.notification.analytics.NotificationStatImpl;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-204-        String templateId;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-205-        Integer numValueOf;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-206-        Integer numValueOf2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java:207:        LockscreenInfo lockscreenInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-208-        SignatureInfo signatureInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-209-        Integer num;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-210-        StatusBarStat statusBarStat = this.mStatusBarStat;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-255-        String stringForUser = Settings.Secure.getStringForUser(keyguardStat.context.getContentResolver(), "constant_template_editor_info", -2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-256-        if (stringForUser != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-257-            TrackTemplateEditorInfo trackTemplateEditorInfo = (TrackTemplateEditorInfo) new Gson().fromJson(TrackTemplateEditorInfo.class, stringForUser);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java:258:            LockscreenInfo lockscreenInfo2 = trackTemplateEditorInfo.getLockscreenInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-259-            ClockBean clockInfo = lockscreenInfo2 != null ? lockscreenInfo2.getClockInfo() : null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-260-            HomeInfo homeInfo = trackTemplateEditorInfo.getHomeInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-261-            if (homeInfo != null) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-336-                        numValueOf2 = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-337-                    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-338-                    String templateClockContentArea2 = companion3.getTemplateClockContentArea(z7, z8, str14, numValueOf2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java:339:                    lockscreenInfo = trackTemplateEditorInfo.getLockscreenInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-340-                    if (lockscreenInfo != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-341-                        signatureInfo = lockscreenInfo.getSignatureInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-342-                    } else {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-496-                numValueOf2 = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-497-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-498-            String templateClockContentArea4 = companion5.getTemplateClockContentArea(z9, z10, str16, numValueOf2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java:499:            lockscreenInfo = trackTemplateEditorInfo.getLockscreenInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-500-            if (lockscreenInfo != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-501-                signatureInfo = lockscreenInfo.getSignatureInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-502-            } else {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-505-            iEventTracker3.track(new KeyguardTemplateStatusEvent(templateName2, followKeyguardStatus2, desktopBlurStatus2, deskIsVideoWallpaper2, templateTextureEffect2, templateDepthEffect2, str8, templateFinePrintContent2, templateClockTimeEffect2, templateClockTimeStyle3, templateClockLayout2, templateClockTimeStyle4, templateClockContentArea3, templateClockContentArea4, companion5.getTemplateHasSignature(z9, z10, clockInfo, signatureInfo), companion5.getTemplateGlobalFont(z9, z10, clockInfo), companion5.getTemplateDigitalColorDifferent(z9, z10, clockInfo), Integer.valueOf(favoriteTemplates2.getFavoriteTemplatesCount()), favoriteTemplates2.getFavoriteTemplatesContent(), str8, str9, EventConstantsKt.EVENT_KEYGUARD_TEMPLATE_STATUS_TIP, 20250520));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-506-        } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-507-            z = true;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java:508:            Log.e("KeyguardStat", "handleKeyguardTemplateStatusEvent error : currentLockscreenInfo is null");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-509-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-510-        String str17 = MiuiConfigs.CUSTOMIZED_REGION;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\systemui\analytics\SystemUIStat.java-511-        if (MiuiMultiDisplayTypeInfo.isFlipDevice() && (currentTinyKeyguardInfoJsonStr = (companion = KeyguardSettingsEvent.Companion).getCurrentTinyKeyguardInfoJsonStr(keyguardStat.context, -2)) != null) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-175-            if (string == null || string.isEmpty()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-176-                return Settings.Secure.getString(this.mContext.getContentResolver(), "constant_template_editor_info");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-177-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:178:            Pair lockscreenInfo = getLockscreenInfo(string);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-179-            ((Integer) lockscreenInfo.first).getClass();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-180-            return (String) lockscreenInfo.second;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-181-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-182-        String constantTemplateEditorInfo = getConstantTemplateEditorInfo();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-183-        if (constantTemplateEditorInfo != null && !constantTemplateEditorInfo.isEmpty()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:184:            Pair lockscreenInfo2 = getLockscreenInfo(constantTemplateEditorInfo);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-185-            ((Integer) lockscreenInfo2.first).getClass();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuisystemui_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-186-            return (String) lockscreenInfo2.sec
... (truncated)
```

#### `TemplateApiImpl`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-52-import com.miui.keyguard.editor.data.bean.CommonConfig;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-53-import com.miui.keyguard.editor.data.template.BitmapTempStore;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-54-import com.miui.keyguard.editor.data.template.TemplateApi;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java:55:import com.miui.keyguard.editor.data.template.TemplateApiImpl;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-56-import com.miui.keyguard.editor.edit.EditFragment;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-57-import com.miui.keyguard.editor.edit.base.CombineEditListener;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-58-import com.miui.keyguard.editor.edit.base.EditModeChangedListener;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-419-        if (callingFromSystemUI()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-420-            ProcessManager.INSTANCE.setLaunchingEditorFromLockScreen(true);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-421-            EditorServiceManager.Companion.getInstance().registerWatchDog(this);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java:422:            TemplateApiImpl.Companion.setWaitFlushConfig(true);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-423-        } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java:424:            TemplateApiImpl.Companion companion2 = TemplateApiImpl.Companion;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-425-            companion2.clearOldConfig();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-426-            companion2.setWaitFlushConfig(false);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-427-        }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-32-import com.miui.keyguard.editor.data.bean.TemplateHistoryConfig;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-33-import com.miui.keyguard.editor.data.bean.WallpaperInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-34-import com.miui.keyguard.editor.data.template.TemplateApi;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java:35:import com.miui.keyguard.editor.data.template.TemplateApiImpl;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-36-import com.miui.keyguard.editor.data.template.TemplateFilePathGenerator;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-37-import com.miui.keyguard.editor.data.template.WallpaperSource;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-38-import com.miui.keyguard.editor.data.util.DataUtil;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-2617-                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-2618-                }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-2619-                ResultKt.throwOnFailure(obj);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java:2620:                TemplateApiImpl.Companion companion = TemplateApiImpl.Companion;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-2621-                Context context = this.this$0.getContext();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-2622-                Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-2623-                companion.flushConfigJson(context);
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java-13-import com.miui.keyguard.editor.data.bean.WallpaperInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java-14-import com.miui.keyguard.editor.data.template.CurrentTemplateApi;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java-15-import com.miui.keyguard.editor.data.template.TemplateApi;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java:16:import com.miui.keyguard.editor.data.template.TemplateApiImpl;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java-17-import com.miui.keyguard.editor.data.template.TemplateDataUtil;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java-18-import com.miui.keyguard.editor.data.template.WallpaperInfoType;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java-19-import com.miui.keyguard.editor.edit.wallpaper.WallpaperController;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java-142-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java-143-    public final void applyHistoryTemplate(Context context, Bundle bundle, Bundle result) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java-144-        Intrinsics.checkNotNullParameter(result, "result");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java:145:        Log.d("SettingsTemplatesProviderManager", "CALL_APPLY_HISTORY_TEMPLATE start, waitFlush=" + TemplateApiImpl.Companion.isWaitingFlush());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java-146-        if (context != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java-147-            isApplying = true;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\settings\SettingsTemplatesProviderManager.java-148-        }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-17-import com.miui.keyguard.editor.data.preset.FilterColor;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-18-import com.miui.keyguard.editor.data.template.StatusRecord;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-19-import com.miui.keyguard.editor.data.template.TemplateApi;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java:20:import com.miui.keyguard.editor.data.template.TemplateApiImpl;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-21-import com.miui.keyguard.editor.data.template.TemplateChangeRecorder;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-22-import com.miui.keyguard.editor.data.template.TemplateDataUtil;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-23-import com.miui.keyguard.editor.utils.DeviceUtil;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-52-            Log.w("Keyguard-Editor-DataUtil", "refreshScreenshotDataForPreset: config = null.");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-53-            return null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-54-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java:55:        PresetTemplateConfig presetTemplateConfigPreloadPresetTemplate = TemplateApiImpl.Companion.preloadPresetTemplate(context, bindItemConfig, null, i, i2, presetTemplate.getHideFlag());
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-56-        bindItemConfig.setReqWidth(i);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-57-        b
... (truncated)
```

#### `MiuiKeyguardWallpaper`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-93-    public volatile boolean mServiceConnected = false;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-94-    public volatile boolean mRebindServiceAllow = true;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-95-    public volatile boolean mDestroyed = false;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java:96:    public IMiuiKeyguardWallpaperCallback mKeyguardCallback = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-97-    public Surface mNormalSurface = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-98-    public Surface mAlphaSurface = null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-99-    public boolean mNotifyInitCompleteStarted = false;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1097-        return false;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1098-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1099-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java:1100:    public void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java:1101:        if (iMiuiKeyguardWallpaperCallback != null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java:1102:            this.mKeyguardCallback = iMiuiKeyguardWallpaperCallback;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1103-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1104-        if (isServiceReady()) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1105-            try {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java:1106:                this.mService.bindSystemUIProxy(iMiuiKeyguardWallpaperCallback);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1107-            } catch (Throwable th) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1108-                Log.e("MiuiWallpaperManager", "bindSystemUIProxy fail", th);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1109-            }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-4-import android.os.IBinder;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-5-import android.os.IInterface;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-6-import android.os.Parcel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:7:import com.miui.miwallpaper.IMiuiKeyguardWallpaperCallback;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-8-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-9-/* JADX INFO: loaded from: classes2.dex */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:10:public interface IMiuiKeyguardWallpaperService extends IInterface {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-11-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:12:    public static class Default implements IMiuiKeyguardWallpaperService {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-13-        @Override // android.os.IInterface
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-14-        public IBinder asBinder() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-15-            return null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-16-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-17-    }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-18-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:19:    void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-20-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-21-    void showWallpaperScreenOnAnim(boolean z);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-22-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-26-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-27-    void updateKeyguardWallpaperState(boolean z);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-28-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:29:    public static abstract class Stub extends Binder implements IMiuiKeyguardWallpaperService {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-30-        @Override // android.os.IInterface
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-31-        public IBinder asBinder() {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-32-            return this;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-35-        @Override // android.os.Binder
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-36-        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-37-            if (i >= 1 && i <= 16777215) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:38:                parcel.enforceInterface("com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-39-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-40-            if (i == 1598968902) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:41:                parcel2.writeString("com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService");
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-42-                return true;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-43-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-44-            if (i != 1) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-54-                    return super.onTransact(i, parcel, parcel2, i2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-55-                }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-56-            } else {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:57:                bindSystemUIProxy(IMiuiKeyguardWallpaperCallback.Stub.asInterface(parcel.readStrongBinder()));
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-58-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-59-            return 
... (truncated)
```

#### `KeyguardWallpaperService`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-8-import com.miui.miwallpaper.IMiuiKeyguardWallpaperCallback;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-9-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-10-/* JADX INFO: loaded from: classes2.dex */
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:11:public interface IMiuiKeyguardWallpaperService extends IInterface {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-12-    void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback) throws RemoteException;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-13-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-14-    void showWallpaperScreenOnAnim(boolean z) throws RemoteException;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-19-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-20-    void updateKeyguardWallpaperState(boolean z) throws RemoteException;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-21-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:22:    public static abstract class Stub extends Binder implements IMiuiKeyguardWallpaperService {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:23:        private static final String DESCRIPTOR = "com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService";
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-24-        static final int TRANSACTION_bindSystemUIProxy = 1;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-25-        static final int TRANSACTION_showWallpaperScreenOnAnim = 2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-26-        static final int TRANSACTION_showWallpaperUnlockAnim = 5;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-36-            attachInterface(this, DESCRIPTOR);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-37-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-38-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:39:        public static IMiuiKeyguardWallpaperService asInterface(IBinder iBinder) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-40-            if (iBinder == null) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-41-                return null;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-42-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-43-            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:44:            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IMiuiKeyguardWallpaperService)) {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:45:                return (IMiuiKeyguardWallpaperService) iInterfaceQueryLocalInterface;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-46-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-47-            return new Proxy(iBinder);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-48-        }
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-81-            return super.onTransact(i, parcel, parcel2, i2);
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-82-        }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-83-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:84:        private static class Proxy implements IMiuiKeyguardWallpaperService {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-85-            private IBinder mRemote;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-86-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-87-            Proxy(IBinder iBinder) {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-97-                return Stub.DESCRIPTOR;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-98-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-99-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:100:            @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-101-            public void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback) throws RemoteException {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-102-                Parcel parcelObtain = Parcel.obtain();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-103-                try {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-109-                }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-110-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-111-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:112:            @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-113-            public void showWallpaperScreenOnAnim(boolean z) throws RemoteException {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-114-                Parcel parcelObtain = Parcel.obtain();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-115-                try {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-121-                }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-122-            }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-123-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:124:            @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-125-            public void updateKeyguardWallpaperState(boolean z) throws RemoteException {
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-126-                Parcel parcelObtain = Parcel.obtain();
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-127-                try {
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-133-                }
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\deskclock_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-134-            }
E:/work/Android Project
... (truncated)
```

### 6.2 OS3

#### `KeyguardOTAInteractor`
```java
(no matches)
```

#### `LockScreenInfoLayout`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-41-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-42-/* JADX INFO: compiled from: r8-map-id-28176a47c9109cff812a5f7a0a997c1bce7bf6885f244bf2996d02e43b70343a */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-43-/* JADX INFO: loaded from: classes3.dex */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:44:public class LockScreenInfoLayout extends RelativeLayout {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-45-    public static final String ab = "android.intent.action.USER_SWITCHED";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-46-    private static final String bo = "signatureInfo";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-47-    private static final String d = "com.android.systemui";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-48-    public static final String ip = "android.intent.extra.user_handle";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:49:    private static final String v = "LockScreenInfoLayout";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-50-    private static final String w = "com.miui.aod";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-51-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-52-    /* JADX INFO: renamed from: a, reason: collision with root package name */
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-92-    private SignatureInfo f52022y;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-93-    private BroadcastReceiver z;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-94-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:95:    /* JADX INFO: renamed from: com.miui.lockscreeninfo.LockScreenInfoLayout$4, reason: invalid class name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-96-    /* JADX INFO: compiled from: r8-map-id-28176a47c9109cff812a5f7a0a997c1bce7bf6885f244bf2996d02e43b70343a */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-97-    class AnonymousClass4 extends ContentObserver {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-98-        AnonymousClass4(Handler handler) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-101-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-102-        public static /* synthetic */ void k(AnonymousClass4 anonymousClass4) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-103-            anonymousClass4.getClass();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:104:            Log.d(LockScreenInfoLayout.v, "Background Blur Enable Listener change");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:105:            boolean zD2ok = LockScreenInfoLayout.this.d2ok();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:106:            if (LockScreenInfoLayout.this.c != zD2ok) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:107:                LockScreenInfoLayout.this.c = zD2ok;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:108:                if (!LockScreenInfoLayout.this.c) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:109:                    LockScreenInfoLayout.this.t8r();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-110-                }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:111:                LockScreenInfoLayout lockScreenInfoLayout = LockScreenInfoLayout.this;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:112:                lockScreenInfoLayout.setClockPalette(lockScreenInfoLayout.j, LockScreenInfoLayout.this.m);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-113-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-114-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-115-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-116-        @Override // android.database.ContentObserver
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-117-        public void onChange(boolean z) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:118:            LockScreenInfoLayout.this.f52019p.post(new Runnable() { // from class: com.miui.lockscreeninfo.toq
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-119-                @Override // java.lang.Runnable
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-120-                public final void run() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:121:                    LockScreenInfoLayout.AnonymousClass4.k(this.f52431k);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-122-                }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-123-            });
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-124-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-125-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-126-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:127:    /* JADX INFO: renamed from: com.miui.lockscreeninfo.LockScreenInfoLayout$5, reason: invalid class name */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-128-    /* JADX INFO: compiled from: r8-map-id-28176a47c9109cff812a5f7a0a997c1bce7bf6885f244bf2996d02e43b70343a */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-129-    class AnonymousClass5 extends ContentObserver {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-130-        AnonymousClass5(Handler handler) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-132-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-133-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-134-        public static /* synthetic */ void k(AnonymousClass5 anonymousClass5) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:135:            boolean highTextContrastEnabled = LockScreenInfoLayout.this.getHighTextContrastEnabled();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:136:            if (LockScreenInfoLayout.this.f != highTextContrastEnabled) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:137:                LockScreenInfoLayout.this.f = highTextContrastEnabled;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:138:                LockScreenInfoLayout lockScreenInfoLayout = LockScreenInfoLayout.this;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-139-                lockScreenInfoLayout.t(lockScreenInfoLayout.f);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-140-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-141-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-142-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-143-        @Override // android.database.ContentObserver
E:/work/Android Project/_revers
... (truncated)
```

#### `ConstantLockscreenInfo`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-8-/* JADX INFO: compiled from: go/retraceme 2cd17b69f3a8634301eb7b7f9d0fcde63366b858dc62d23e4687cc6080d49999 */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-9-/* JADX INFO: loaded from: classes.dex */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-10-@Keep
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:11:public final class ConstantLockscreenInfo {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-12-    private final ClockBean clockInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-13-    private final DoodleInfo doodle;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-14-    private final SmartFrameInfo smartFrame;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-15-    private final WallpaperInfo wallpaperInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-16-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:17:    public ConstantLockscreenInfo() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-18-        this(null, null, null, null, 15, null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-19-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-20-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:21:    public static /* synthetic */ ConstantLockscreenInfo copy$default(ConstantLockscreenInfo constantLockscreenInfo, ClockBean clockBean, WallpaperInfo wallpaperInfo, DoodleInfo doodleInfo, SmartFrameInfo smartFrameInfo, int i, Object obj) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-22-        if ((i & 1) != 0) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-23-            clockBean = constantLockscreenInfo.clockInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-24-        }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-50-        return this.smartFrame;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-51-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-52-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:53:    public final ConstantLockscreenInfo copy(ClockBean clockBean, WallpaperInfo wallpaperInfo, DoodleInfo doodleInfo, SmartFrameInfo smartFrameInfo) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:54:        return new ConstantLockscreenInfo(clockBean, wallpaperInfo, doodleInfo, smartFrameInfo);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-55-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-56-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-57-    public boolean equals(Object obj) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-58-        if (this == obj) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-59-            return true;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-60-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:61:        if (!(obj instanceof ConstantLockscreenInfo)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-62-            return false;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-63-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:64:        ConstantLockscreenInfo constantLockscreenInfo = (ConstantLockscreenInfo) obj;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-65-        return Intrinsics.areEqual(this.clockInfo, constantLockscreenInfo.clockInfo) && Intrinsics.areEqual(this.wallpaperInfo, constantLockscreenInfo.wallpaperInfo) && Intrinsics.areEqual(this.doodle, constantLockscreenInfo.doodle) && Intrinsics.areEqual(this.smartFrame, constantLockscreenInfo.smartFrame);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-66-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-67-
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-92-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-93-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-94-    public String toString() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:95:        return "ConstantLockscreenInfo(clockInfo=" + this.clockInfo + ", wallpaperInfo=" + this.wallpaperInfo + ", doodle=" + this.doodle + ", smartFrame=" + this.smartFrame + ")";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-96-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-97-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:98:    public ConstantLockscreenInfo(ClockBean clockBean, WallpaperInfo wallpaperInfo, DoodleInfo doodleInfo, SmartFrameInfo smartFrameInfo) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-99-        this.clockInfo = clockBean;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-100-        this.wallpaperInfo = wallpaperInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-101-        this.doodle = doodleInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-102-        this.smartFrame = smartFrameInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-103-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-104-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:105:    public /* synthetic */ ConstantLockscreenInfo(ClockBean clockBean, WallpaperInfo wallpaperInfo, DoodleInfo doodleInfo, SmartFrameInfo smartFrameInfo, int i, DefaultConstructorMarker defaultConstructorMarker) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-106-        this((i & 1) != 0 ? new ClockBean("classic_max") : clockBean, (i & 2) != 0 ? null : wallpaperInfo, (i & 4) != 0 ? null : doodleInfo, (i & 8) != 0 ? null : smartFrameInfo);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-107-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-108-}
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-94-import com.android.keyguard.utils.AnimUtils;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-95-import com.android.keyguard.utils.WideColorBitmapDecoder;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-96-import com.android.keyguard.wallpaper.MiuiKeyguardWallPaperManager;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:97:import com.android.keyguard.wallpaper.entity.ConstantLockscreenInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPa
... (truncated)
```

#### `constantLockscreenInfo`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-719-                Settings.Secure.putInt(this.mContext.getContentResolver(), "color_scheme", this.mKeyguardColorScheme);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-720-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-721-            KeyguardPanelViewInjector keyguardPanelViewInjector = (KeyguardPanelViewInjector) ((IKeyguardPanelViewInjector) InterfacesImplManager.getImpl(IKeyguardPanelViewInjector.class));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java:722:            String templateId = keyguardPanelViewInjector.getKeyguardPanelViewController().constantLockscreenInfo.getClockInfo().getTemplateId();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-723-            if (templateId.equals("doodle")) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-724-                if (((Integer) this.mPartDeepMap.get(str2)).intValue() == -1) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-725-                    int i2 = this.mKeyguardColorScheme;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-726-                    Map map = this.mFilterColorMap;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java:727:                    DoodleInfo doodle = keyguardPanelViewInjector.getKeyguardPanelViewController().constantLockscreenInfo.getDoodle();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-728-                    if (doodle != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-729-                        boolean zIsAutoSolidColor = doodle.isAutoSolidColor();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-730-                        solidColor2 = doodle.getSolidColor();
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-741-                int i3 = this.mKeyguardColorScheme;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-742-                Map map2 = this.mFilterColorMap;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-743-                KeyguardPanelViewController keyguardPanelViewController = keyguardPanelViewInjector.getKeyguardPanelViewController();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java:744:                SmartFrameInfo smartFrame = keyguardPanelViewController.constantLockscreenInfo.getSmartFrame();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-745-                if (smartFrame != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-746-                    i = 0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\MiuiKeyguardWallPaperManager.java-747-                    solidColor = Settings.System.getInt(keyguardPanelViewController.context.getContentResolver(), "power_supersave_mode_open", 0) != 0 ? -16777216 : smartFrame.getSolidColor();
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-18-        this(null, null, null, null, 15, null);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-19-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-20-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:21:    public static /* synthetic */ ConstantLockscreenInfo copy$default(ConstantLockscreenInfo constantLockscreenInfo, ClockBean clockBean, WallpaperInfo wallpaperInfo, DoodleInfo doodleInfo, SmartFrameInfo smartFrameInfo, int i, Object obj) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-22-        if ((i & 1) != 0) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:23:            clockBean = constantLockscreenInfo.clockInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-24-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-25-        if ((i & 2) != 0) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:26:            wallpaperInfo = constantLockscreenInfo.wallpaperInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-27-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-28-        if ((i & 4) != 0) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:29:            doodleInfo = constantLockscreenInfo.doodle;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-30-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-31-        if ((i & 8) != 0) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:32:            smartFrameInfo = constantLockscreenInfo.smartFrame;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-33-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:34:        return constantLockscreenInfo.copy(clockBean, wallpaperInfo, doodleInfo, smartFrameInfo);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-35-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-36-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-37-    public final ClockBean component1() {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-61-        if (!(obj instanceof ConstantLockscreenInfo)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-62-            return false;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-63-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:64:        ConstantLockscreenInfo constantLockscreenInfo = (ConstantLockscreenInfo) obj;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java:65:        return Intrinsics.areEqual(this.clockInfo, constantLockscreenInfo.clockInfo) && Intrinsics.areEqual(this.wallpaperInfo, constantLockscreenInfo.wallpaperInfo) && Intrinsics.areEqual(this.doodle, constantLockscreenInfo.doodle) && Intrinsics.areEqual(this.smartFrame, constantLockscreenInfo.smartFrame);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-66-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-67-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\wallpaper\entity\ConstantLockscreenInfo.java-68-    public final ClockBean getClockInfo() {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-304-    public int colorSpaceMode;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-305-    public C05761 colorSpaceModeObserver;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-306-    public final ConfigurationController configurationController;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:307:    public volatile ConstantLockscreenInfo constantLockscreenInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java:308:    public final KeyguardPanelViewController$constantLockscreenInfoObserver$1 constantLockscreenInfoObserver;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\KeyguardPanelViewController.java-309-    public final Context context;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuisystemui_decompiler\sources\com\android\keyguard\panel\Keyguar
... (truncated)
```

#### `preProcessTemplateForOS4`
```java
(no matches)
```

#### `LockscreenInfo`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\template\CurrentTemplateApi.java-185-        if (deviceUtil.isVersionOs3(this.context)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\template\CurrentTemplateApi.java-186-            CommonConfig commonConfig = (CommonConfig) gson.fromJson(string, CommonConfig.class);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\template\CurrentTemplateApi.java-187-            if (commonConfig != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\template\CurrentTemplateApi.java:188:                return commonConfig.getLockscreenInfo();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\template\CurrentTemplateApi.java-189-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\template\CurrentTemplateApi.java-190-            return null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\aod\template\CurrentTemplateApi.java-191-        }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-439-        if (isSystemUI()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-440-            String constantTemplateEditorInfo = getConstantTemplateEditorInfo();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-441-            if (constantTemplateEditorInfo != null && !constantTemplateEditorInfo.isEmpty()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:442:                Pair lockscreenInfo = getLockscreenInfo(constantTemplateEditorInfo);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-443-                this.mEditorVersion = ((Integer) lockscreenInfo.first).intValue();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-444-                return (String) lockscreenInfo.second;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-445-            }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-449-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-450-        String string = Settings.Secure.getString(this.mContext.getContentResolver(), "constant_template_editor_info");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-451-        if (string != null && !string.isEmpty()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:452:            Pair lockscreenInfo2 = getLockscreenInfo(string);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-453-            this.mEditorVersion = ((Integer) lockscreenInfo2.first).intValue();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-454-            return (String) lockscreenInfo2.second;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-455-        }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-761-        return intForUser == 1;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-762-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-763-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java:764:    private Pair getLockscreenInfo(String str) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-765-        int i;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-766-        String string = "";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\lockscreeninfo\LockScreenInfoLayout.java-767-        try {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-405-        FontFilterData fontFilterData = new FontFilterData(iIntValue, iIntValue3, iIntValue2, z);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-406-        boolean z3 = false;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-407-        updateFontFilter(0, fontFilterData);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java:408:        ClockInfo clockInfo2 = (commonConfig == null || (lockscreenInfo2 = commonConfig.getLockscreenInfo()) == null) ? null : lockscreenInfo2.getClockInfo();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-409-        if (Intrinsics.areEqual(clockInfo2 != null ? clockInfo2.getTemplateId() : null, "magazine_c") && (clockInfo2.getStyle() == 6 || clockInfo2.getStyle() == 9)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-410-            z3 = true;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-411-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-412-        updateFontFilter(1, new FontFilterData(iIntValue, iIntValue5, iIntValue4, z3 ? true : z));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java:413:        updateDifferentColorStatus((commonConfig == null || (lockscreenInfo = commonConfig.getLockscreenInfo()) == null || (clockInfo = lockscreenInfo.getClockInfo()) == null || !clockInfo.isDiffHourMinuteColor()) ? 2 : 1, z);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-414-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-415-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-416-    /* JADX WARN: Code duplicated, block: B:127:0x01d2 A[PHI: r6 r8 r11
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-467-        int primaryFontColorPanelId2 = 0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-468-        if (ArraysKt.contains(new Integer[]{2, 3}, commonConfig != null ? Integer.valueOf(commonConfig.getVersion()) : null)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-469-            Intrinsics.checkNotNull(commonConfig);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java:470:            clockEffect = commonConfig.getLockscreenInfo().getClockInfo().getClockEffect();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-471-            if (!DeviceConfig.supportBackgroundBlur(EditorApplicationProxy.Companion.getApplication()) || effectDisable(clockEffect)) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-472-                clockEffect = 0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-473-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java:474:        } else if (commonConfig == null || (lockscreenInfo = commonConfig.getLockscreenInfo()) == null || (clockInfo = lockscreenInfo.getClockInfo()) == null || !clockInfo.isAutoPrimaryColor()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-475-            clockEffect = 0;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-476-        } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-477-            clockEffect = 1;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-478-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-479-        int blendColor = -1;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java:480:        if (Intrinsics.areEqual("magazine_a", (commonConfig == null || (lockscreenInfo10 = commonConfig.getLockscreenInfo()) == null || (clockInfo10 = lockscreenInfo10.getClockInfo()) == null) ? null : clockInfo10.getTemplateId())) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java:481:            primaryColor = commonConfig.getLockscreenInfo().getClockInfo().getPrimaryColor();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-482-            if (ArraysKt.contains(new Integer[]{2, 3}, Integer.valueOf(commonConfig.getVersion()))) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java:483:                primaryFontColorPanelId = commonConfig.getLockscreenInfo().getClockInfo().getPrimaryFontColorPanelId();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_d
... (truncated)
```

#### `TemplateApiImpl`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-33-import com.miui.keyguard.editor.data.bean.TemplateHistoryConfig;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-34-import com.miui.keyguard.editor.data.bean.WallpaperInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-35-import com.miui.keyguard.editor.data.template.TemplateApi;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java:36:import com.miui.keyguard.editor.data.template.TemplateApiImpl;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-37-import com.miui.keyguard.editor.data.template.TemplateFilePathGenerator;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-38-import com.miui.keyguard.editor.data.template.WallpaperSource;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-39-import com.miui.keyguard.editor.data.util.DataUtil;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-301-                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-302-                }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-303-                ResultKt.throwOnFailure(obj);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java:304:                TemplateApiImpl.Companion companion = TemplateApiImpl.f48839n7h;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-305-                Context context = this.this$0.getContext();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-306-                Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\LockScreenTransformerLayer.java-307-                companion.zy(context);
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java-3-import android.app.Application;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java-4-import android.app.KeyguardManager;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java-5-import android.util.Log;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java:6:import com.miui.keyguard.editor.data.template.TemplateApiImpl;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java-7-import com.miui.keyguard.editor.edit.wallpaper.FashionGalleryHelper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java-8-import com.miui.keyguard.editor.homepage.bean.FloorItemBean;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java-9-import com.miui.keyguard.editor.homepage.bean.TemplateItemBean;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java-69-        Log.i("Keyguard-Editor-CrossListPreloader", "preload start");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java-70-        Object systemService = this$0.zy().getSystemService("keyguard");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java-71-        KeyguardManager keyguardManager = systemService instanceof KeyguardManager ? (KeyguardManager) systemService : null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java:72:        TemplateApiImpl.f48839n7h.s(keyguardManager != null ? keyguardManager.isKeyguardLocked() : false);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java-73-        Task.q(this$0.mcp());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java-74-        Task.q(this$0.t());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\homepage\model\CrossListPreloader.java-75-        Task.q(this$0.o1t());
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-58-import com.miui.keyguard.editor.data.bean.CommonConfig;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-59-import com.miui.keyguard.editor.data.template.BitmapTempStore;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-60-import com.miui.keyguard.editor.data.template.TemplateApi;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java:61:import com.miui.keyguard.editor.data.template.TemplateApiImpl;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-62-import com.miui.keyguard.editor.edit.EditFragment;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-63-import com.miui.keyguard.editor.edit.aiwapper.ThemeApiWrapper;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-64-import com.miui.keyguard.editor.edit.base.CombineEditListener;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2261-        if (bwp()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2262-            ProcessManager.f51504k.fn3e(true);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2263-            EditorServiceManager.f48201x2.k().wvg(this);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java:2264:            TemplateApiImpl.f48839n7h.s(true);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2265-        } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java:2266:            TemplateApiImpl.Companion companion2 = TemplateApiImpl.f48839n7h;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2267-            companion2.toq();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2268-            companion2.s(false);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\EditorActivity.java-2269-        }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-18-import com.miui.keyguard.editor.data.preset.FilterTypeSelectInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-19-import com.miui.keyguard.editor.data.template.StatusRecord;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-20-import com.miui.keyguard.editor.data.template.TemplateApi;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java:21:import com.miui.keyguard.editor.data.template.TemplateApiImpl;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-22-import com.miui.keyguard.editor.data.template.TemplateChangeRecorder;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-23-import com.miui.keyguard.editor.data.template.TemplateDataUtil;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-24-import com.miui.keyguard.editor.utils.DeviceUtil;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-135-            Log.w(f48997toq, "refreshScreenshotDataForPreset: config = null.");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-136-            return null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-137-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java:138:        PresetTemplateConfig presetTemplateConfigF7l8 = TemplateApiImpl.f48839n7h.f7l8(context, bindItemConfig, null, i2, i3, presetTemplate.getHideFlag());
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-139-        bindItemConfig.setReqWidth(i2);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-140-        bindItemConfig.setReqHeight(i3);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\data\util\DataUtil.java-141-        presetTemplate.setLoadCompleted(true);
--
E:/w
... (truncated)
```

#### `MiuiKeyguardWallpaper`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-92-    private volatile boolean mServiceConnected = false;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-93-    private volatile boolean mRebindServiceAllow = true;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-94-    private volatile boolean mDestroyed = false;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java:95:    private IMiuiKeyguardWallpaperCallback mKeyguardCallback = null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-96-    private Surface mNormalSurface = null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-97-    private Surface mAlphaSurface = null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-98-    private boolean mNotifyInitCompleteStarted = false;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1114-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1115-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1116-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java:1117:    public void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java:1118:        if (iMiuiKeyguardWallpaperCallback != null) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java:1119:            this.mKeyguardCallback = iMiuiKeyguardWallpaperCallback;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1120-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1121-        if (isServiceReady()) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1122-            try {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java:1123:                this.mService.bindSystemUIProxy(iMiuiKeyguardWallpaperCallback);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1124-            } catch (Throwable th) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1125-                Log.e("MiuiWallpaperManager", "bindSystemUIProxy fail", th);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\MiuiWallpaperManager.java-1126-            }
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-4-import android.os.IBinder;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-5-import android.os.IInterface;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-6-import android.os.Parcel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:7:import com.miui.miwallpaper.IMiuiKeyguardWallpaperCallback;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-8-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-9-/* JADX INFO: loaded from: classes2.dex */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:10:public interface IMiuiKeyguardWallpaperService extends IInterface {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-11-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:12:    public static class Default implements IMiuiKeyguardWallpaperService {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-13-        @Override // android.os.IInterface
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-14-        public IBinder asBinder() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-15-            return null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-16-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-17-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-18-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:19:    void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-20-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-21-    void showWallpaperScreenOnAnim(boolean z);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-22-
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-26-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-27-    void updateKeyguardWallpaperState(boolean z);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-28-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:29:    public static abstract class Stub extends Binder implements IMiuiKeyguardWallpaperService {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-30-        @Override // android.os.IInterface
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-31-        public IBinder asBinder() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-32-            return this;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-35-        @Override // android.os.Binder
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-36-        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-37-            if (i >= 1 && i <= 16777215) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:38:                parcel.enforceInterface("com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-39-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-40-            if (i == 1598968902) {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:41:                parcel2.writeString("com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService");
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-42-                return true;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-43-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-44-            if (i != 1) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-54-                    return super.onTransact(i, parcel, parcel2, i2);
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-55-                }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-56-            } else {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:57:                bindSystemUIProxy(IMiuiKeyguardWallpaperCallback.Stub.asInterface(parcel.readStrongBinder()));
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-58-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-59-            return true;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-60-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\miuiaod_decompiler\sou
... (truncated)
```

#### `KeyguardWallpaperService`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-9-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-10-/* JADX INFO: compiled from: r8-map-id-28176a47c9109cff812a5f7a0a997c1bce7bf6885f244bf2996d02e43b70343a */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-11-/* JADX INFO: loaded from: classes3.dex */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:12:public interface IMiuiKeyguardWallpaperService extends IInterface {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:13:    public static final String DESCRIPTOR = "com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService";
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-14-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-15-    void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback) throws RemoteException;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-16-
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-23-    void updateKeyguardWallpaperState(boolean z) throws RemoteException;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-24-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-25-    /* JADX INFO: compiled from: r8-map-id-28176a47c9109cff812a5f7a0a997c1bce7bf6885f244bf2996d02e43b70343a */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:26:    public static class Default implements IMiuiKeyguardWallpaperService {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-27-        @Override // android.os.IInterface
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-28-        public IBinder asBinder() {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-29-            return null;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-30-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-31-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:32:        @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-33-        public void showWallpaperUnlockAnim() throws RemoteException {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-34-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-35-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:36:        @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-37-        public void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback) throws RemoteException {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-38-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-39-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:40:        @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-41-        public void showWallpaperScreenOnAnim(boolean z) throws RemoteException {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-42-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-43-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:44:        @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-45-        public void updateKeyguardWallpaperState(boolean z) throws RemoteException {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-46-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-47-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:48:        @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-49-        public void updateKeyguardWallpaperRatio(float f, long j) throws RemoteException {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-50-        }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-51-    }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-52-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-53-    /* JADX INFO: compiled from: r8-map-id-28176a47c9109cff812a5f7a0a997c1bce7bf6885f244bf2996d02e43b70343a */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:54:    public static abstract class Stub extends Binder implements IMiuiKeyguardWallpaperService {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-55-        static final int TRANSACTION_bindSystemUIProxy = 1;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-56-        static final int TRANSACTION_showWallpaperScreenOnAnim = 2;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-57-        static final int TRANSACTION_showWallpaperUnlockAnim = 5;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-59-        static final int TRANSACTION_updateKeyguardWallpaperState = 3;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-60-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-61-        /* JADX INFO: compiled from: r8-map-id-28176a47c9109cff812a5f7a0a997c1bce7bf6885f244bf2996d02e43b70343a */
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:62:        private static class Proxy implements IMiuiKeyguardWallpaperService {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-63-            private IBinder mRemote;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-64-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-65-            Proxy(IBinder iBinder) {
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-71-                return this.mRemote;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-72-            }
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-73-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java:74:            @Override // com.miui.miwallpaper.keyguard.IMiuiKeyguardWallpaperService
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-75-            public void bindSystemUIProxy(IMiuiKeyguardWallpaperCallback iMiuiKeyguardWallpaperCallback) throws RemoteException {
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-76-                Parcel parcelObtain = Parcel.obtain();
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\miwallpaper\keyguard\IMiuiKeyguardWallpaperService.java-77-                try {
E:/work/Android Project/_reverse-eng-archive/apk_
... (truncated)
```

## 7. New/changed OS4 package/class inventory

### 7.1 OS4 `com.miui.keyguard.editor` package files

#### `package com.miui.keyguard.editor`
```java
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModelKt.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModelKt.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModelKt.java-3-import kotlin.Metadata;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModelKt.java-4-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModel.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModel.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModel.java-3-import androidx.lifecycle.ViewModel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModel.java-4-import kotlin.Metadata;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\IntWrapper.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\IntWrapper.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\IntWrapper.java-3-import kotlin.Metadata;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\IntWrapper.java-4-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModelKt.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModelKt.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModelKt.java-3-import kotlin.Metadata;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModelKt.java-4-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModel.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModel.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModel.java-3-import android.content.Context;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModel.java-4-import android.util.Log;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModelKt.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModelKt.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModelKt.java-3-import kotlin.Metadata;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModelKt.java-4-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModel.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModel.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModel.java-3-import android.content.Context;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModel.java-4-import android.util.Log;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModelKt.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModelKt.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModelKt.java-3-import kotlin.Metadata;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModelKt.java-4-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-3-import android.content.Context;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-4-import android.util.Log;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\ApplyStatusManager.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\ApplyStatusManager.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\ApplyStatusManager.java-3-import android.util.Log;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\viewmodel\ApplyStatusManager.java-4-import kotlin.Metadata;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterViewKt.java:1:package com.miui.keyguard.editor.view;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterViewKt.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterViewKt.java-3-import kotlin.Metadata;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterViewKt.java-4-
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView.java:1:package com.miui.keyguard.editor.view;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView.java-3-import android.accessibilityservice.AccessibilityServiceInfo;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView.java-4-import android.annotation.SuppressLint;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView$onAttachedToWindow$1$talkbackEnable$1.java:1:package com.miui.keyguard.editor.view;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView$onAttachedToWindow$1$talkbackEnable$1.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView$onAttachedToWindow$1$talkbackEnable$1.java-3-import android.content.Context;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView$onAttachedToWindow$1$talkbackEnable$1.java-4-import kotlin.Metadata;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterSelectItemCallback.java:1:package com.miui.keyguard.editor.view;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterSelectItemCallback.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterSelectItemCallback.java-3-import android.view.View;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterSelectItemCallback.java-4-import kotlin.Metadata;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\ViewPager2.java:1:package com.miui.keyguard.editor.view.viewpager2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\ViewPager2.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\ViewPager2.java-3-import android.annotation.SuppressLint;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\ViewPager2.java-4-import android.content.Context;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\SnapHelper.java:1:package com.miui.keyguard.editor.view.viewpager2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\SnapHelper.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\SnapHelper.java-3-import android.util.DisplayMetrics;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\SnapHelper.java-4-import android.view.View;
--
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\ScrollEventAdapter.java:1:package com.miui.keyguard.editor.view.viewpager2;
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\ScrollEventAdapter.java-2-
E:/work/Android Project/_reverse-eng-archive/os4_android17_apks\miuiaod_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\
... (truncated)
```

### 7.2 OS3 `com.miui.keyguard.editor` package files

#### `package com.miui.keyguard.editor`
```java
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModelKt.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModelKt.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModelKt.java-3-import org.jetbrains.annotations.NotNull;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModelKt.java-4-
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModel.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModel.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModel.java-3-import android.content.Context;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\MainFragmentViewModel.java-4-import android.util.Log;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\IntWrapper.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\IntWrapper.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\IntWrapper.java-3-import kotlin.jvm.internal.DefaultConstructorMarker;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\IntWrapper.java-4-import org.jetbrains.annotations.NotNull;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModelKt.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModelKt.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModelKt.java-3-import org.jetbrains.annotations.NotNull;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModelKt.java-4-
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModel.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModel.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModel.java-3-import android.content.Context;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditorActivityViewModel.java-4-import android.graphics.Bitmap;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModelKt.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModelKt.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModelKt.java-3-import org.jetbrains.annotations.NotNull;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModelKt.java-4-
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModel.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModel.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModel.java-3-import android.content.Context;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModel.java-4-import android.util.Log;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModel$loadHistoryData$1.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModel$loadHistoryData$1.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModel$loadHistoryData$1.java-3-import android.content.Context;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditHistoryViewModel$loadHistoryData$1.java-4-import com.miui.keyguard.editor.homepage.bean.FloorItemBean;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModelKt.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModelKt.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModelKt.java-3-import org.jetbrains.annotations.NotNull;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModelKt.java-4-
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-3-import android.content.Context;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\EditFragmentViewModel.java-4-import android.util.Log;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\ApplyStatusManager.java:1:package com.miui.keyguard.editor.viewmodel;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\ApplyStatusManager.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\ApplyStatusManager.java-3-import android.util.Log;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\viewmodel\ApplyStatusManager.java-4-import kotlin.collections.ArraysKt;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterViewKt.java:1:package com.miui.keyguard.editor.view;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterViewKt.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterViewKt.java-3-import org.jetbrains.annotations.NotNull;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterViewKt.java-4-
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView.java:1:package com.miui.keyguard.editor.view;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView.java-3-import android.accessibilityservice.AccessibilityServiceInfo;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView.java-4-import android.annotation.SuppressLint;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView$onAttachedToWindow$1$talkbackEnable$1.java:1:package com.miui.keyguard.editor.view;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView$onAttachedToWindow$1$talkbackEnable$1.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView$onAttachedToWindow$1$talkbackEnable$1.java-3-import android.content.Context;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterView$onAttachedToWindow$1$talkbackEnable$1.java-4-import kotlin.ResultKt;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterSelectItemCallback.java:1:package com.miui.keyguard.editor.view;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterSelectItemCallback.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterSelectItemCallback.java-3-import android.view.View;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\WallpaperFilterSelectItemCallback.java-4-import org.jetbrains.annotations.NotNull;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\ViewPager2.java:1:package com.miui.keyguard.editor.view.viewpager2;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\ViewPager2.java-2-
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\ViewPager2.java-3-import android.R;
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\ViewPager2.java-4-import android.annotation.SuppressLint;
--
E:/work/Android Project/_reverse-eng-archive/apk_decompiled\thememanager_decompiler\sources\com\miui\keyguard\editor\view\viewpager2\SnapHelper
... (truncated)
```