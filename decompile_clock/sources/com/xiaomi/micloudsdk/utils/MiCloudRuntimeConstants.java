package com.xiaomi.micloudsdk.utils;

/* JADX INFO: loaded from: classes2.dex */
public class MiCloudRuntimeConstants {

    public static class MEMBER {
        public static final String DAILY_MEMBERSHIP_URL = "https://account.xiaomi.com/pass/serviceLogin?callback=https%3A%2F%2Fdaily.i.mi.com%2Fsts%3Fsign%3DGTIyREMRa%252Bf1eVmlJubCFg%252FK3eA%253D%26followup%3Dhttps%253A%252F%252Fdaily.i.mi.com%252Fvip&sid=i.mi.com&_locale=zh_CN";
        public static final String MEMBERSHIP_URL = "https://account.xiaomi.com/pass/serviceLogin?callback=https%3A%2F%2Fi.mi.com%2Fsts%3Fsign%3Dn9zfyPtPHlxmLf0eYJmwASvEjEo%253D%26followup%3Dhttps%253A%252F%252Fi.mi.com%252Fvip&sid=i.mi.com";
        public static final String STAGING_MEMBERSHIP_URL = "http://account.preview.n.xiaomi.net/pass/serviceLogin?callback=http%3A%2F%2Fmicloudweb.preview.n.xiaomi.com%2Fsts%3Fsign%3DLMx3DWB%252FO%252FtjMckaek2OtO0%252BkzQ%253D%26followup%3Dhttp%253A%252F%252Fmicloudweb.preview.n.xiaomi.com%252Fvip&sid=i.mi.com";
    }

    public static class PERMISSION {
        public static final String PERMISSION_PROVISION_ACCESS_SYNC_STATUS = "com.miui.cloudservice.permission.PROVISION_ACCESS_SYNC_STATUS";
    }

    public static class PUSH {
        public static final String ATTRIBUTES_NAME = "micloud-push";
        public static final String AUTH_TOKEN_TYPE = "micloud";
        public static final String MICLOUD_PUSH_RECEIVE = "com.xiaomi.micloudPush.RECEIVE";
        public static final String MICLOUD_PUSH_REGISTRATION = "com.xiaomi.micloudPush.REGISTRATION";
        public static final String MICLOUD_PUSH_SUBSCRIBE = "com.xiaomi.micloudpush.SUBSCRIBE";
        public static final String PUSH_DATA = "pushData";
        public static final String PUSH_NAME = "pushName";
        public static final String PUSH_TYPE = "pushType";
        public static final String RECEIVER_META_DATA = "com.xiaomi.MicloudPush";
        public static final String URI_WATERMARK_LIST = "content://%s/watermark_list";

        public interface MICLOUD_PUSH_ATTRI_NAME {
            public static final String CAPABILITY = "capability";
            public static final String CONTENT_AUTHORITY = "contentAuthority";
            public static final String PUSH_NAME = "pushName";
            public static final String PUSH_TYPE = "pushType";
        }

        public interface WATERMARK_TYPE {
            public static final String GLOBAL = "g";
            public static final String PERSONAL = "p";
            public static final String SUBSCRIPTION = "s";
        }
    }

    private MiCloudRuntimeConstants() {
    }

    public static class ACCOUNT {
        public static final String XIAOMI_ACCOUNT_TYPE = "com.xiaomi";

        private ACCOUNT() {
        }
    }

    public static class CLOUDINFO {
        public static final String ItemInfoList = "ItemInfoList";
        public static final String LEVEL = "level";
        public static final String LocalizedName = "LocalizedName";
        public static final String NAME = "Name";
        public static final String TOTAL = "Total";
        public static final String USED = "Used";
        public static final String VIPNAME = "vipName";
        public static final String WARN = "Warn";
        public static final String YearlyPackageCreateTime = "YearlyPackageCreateTime";
        public static final String YearlyPackageExpireTime = "YearlyPackageExpireTime";
        public static final String YearlyPackageSize = "YearlyPackageSize";
        public static final String YearlyPackageType = "YearlyPackageType";

        private CLOUDINFO() {
        }
    }

    public static class INTENT {
        public static final String ACTION_BIND_XIAOMI_ACCOUNT_SERVICE = "android.intent.action.BIND_XIAOMI_ACCOUNT_SERVICE";
        public static final String ACTION_BIND_XIAOMI_ACCOUNT_SERVICE_2 = "com.xiaomi.account.action.BIND_XIAOMI_ACCOUNT_SERVICE";
        public static final String ACTION_CLOUD_BACKUP_SETTINGS = "miui.action.CLOUD_BACKUP_SETTINGS";
        public static final String ACTION_CLOUD_RESTORE_SETTINGS = "miui.action.CLOUD_RESTORE_SETTINGS";
        public static final String ACTION_DATA_IN_TRANSFER = "com.xiaomi.action.DATA_IN_TRANSFER";
        public static final String ACTION_FIND_DEVICE_GUIDE = "com.xiaomi.action.MICLOUD_FIND_DEVICE_GUIDE";
        public static final String ACTION_MICLOUD_INFO_SETTINGS = "com.xiaomi.action.MICLOUD_INFO_SETTINGS";
        public static final String ACTION_MICLOUD_MEMBER = "com.xiaomi.action.MICLOUD_MEMBER";
        public static final String ACTION_MICLOUD_NETWORK_AVAILABILITY_CHANGED = "miui.intent.action.MICLOUD_NETWORK_AVAILABILITY_CHANGED";
        public static final String ACTION_MICLOUD_PRIVACY_DENIED = "com.xiaomi.action.PRIVACY_DENIED";
        public static final String ACTION_MICLOUD_STAT = "com.xiaomi.action.MICLOUD_STAT";
        public static final String ACTION_MIUI_FINDDEVICE_LAST_STATUS_CHANGED = "miui.com.xiaomi.finddevice.action.LAST_STATUS_CHANGED";
        public static final String ACTION_MIUI_PACKAGE_ADDED = "miui.android.intent.action.PACKAGE_ADDED";
        public static final String ACTION_PROVISION_SYNC_STATUS_CHANGED = "com.miui.cloudservice.PROVISION_SYNC_STATUS_CHANGED";
        public static final String ACTION_SYNC_ALERT = "action_sync_alert";
        public static final String ACTION_SYNC_RESUME = "com.xiaomi.action.SYNC_RESUME";
        public static final String ACTION_SYNC_SETTINGS_APPENDER = ".SYNC_SETTINGS";
        public static final String ACTION_UPLOAD_PHONE_LIST = "com.miui.cloudservice.mms.UPLOAD_PHONE_LIST";
        public static final String ACTION_VIEW_CLOUD = "com.xiaomi.action.MICLOUD_MAIN";
        public static final String ACTION_WARN_INVALID_DEVICE_ID = "com.xiaomi.action.WARN_INVALID_DEVICE_ID";
        public static final String ACTION_WIPE_DATA_ACTIVITY = "com.xiaomi.action.MICLOUD_WIPE_DATA_CONFIRM";
        public static final String EXTRA_ACTIVE = "active";
        public static final String EXTRA_ANALYTICS_EVENT_ID = "extra_analytics_event_id";
        public static final String EXTRA_ANALYTICS_EVENT_PARAMETERS = "extra_analytics_event_parameters";
        public static final String EXTRA_DEVICE_ID = "device_id";
        public static final String EXTRA_FIND_DEVICE_SETTING_SOURCE = "extra_micloud_find_device_guide_source";
        public static final String EXTRA_IS_SSO_URL = "extra_is_sso_url";
        public static final String EXTRA_PASSWORD_LOGIN = "password_login";
        public static final String EXTRA_PROVISION_SYNC_STATUS = "extra_provision_sync_status";
        public static final String EXTRA_RESULT_RECEIVER = "result_receiver";
        public static final String EXTRA_RETRY_TIME = "retryTime";
        public static final String EXTRA_SOURCE = "extra_membership_source";
        public static final String EXTRA_SYNC_OPERATION = "extra_operation";
        public static final String EXTRA_URL = "extra_url";

        private INTENT() {
        }
    }

    public static class PACKAGE_NAME {
        public static final String ACCOUNT = "com.xiaomi.account";
        public static final String CLOUDSERVICE = "com.miui.cloudservice";
        public static final String XMSF = "com.xiaomi.xmsf";

        private PACKAGE_NAME() {
        }
    }

    public static class USER_DATA {
        public static final String KEY_FIND_DEVICE_ENABLED = "extra_find_device_enabled";
        public static final String KEY_FIND_DEVICE_TOKEN = "extra_find_my_device_token";
        public static final String KEY_MICLOUD_STATUS_INFO_QUOTA = "extra_micloud_status_info_quota";
        public static final String KEY_MICLOUD_VIP_AVAILIABLE = "extra_micloud_vip_availiable";

        private USER_DATA() {
        }
    }

    public static class AUTHORITY {
        public static final String CLOUD_APP_PROVIDER = "com.miui.backup.cloud.CloudAppProvider";
        public static final String CLOUD_BACKUP = "micloud_cloud_backup";
        public static final String FIND_DEVICE = "micloud_find_device";

        @Deprecated
        public static final String MICLOUD_SETTINGS_PROVIDER = "com.xiaomi.micloudsdk.provider.MiCloudSettingsProvider";
        public static final String MMSLITE_PROVIDER = "com.xiaomi.mms.providers.SmsProvider";
        public static final String PERMANENT_MICLOUD_SETTINGS_PROVIDER = "com.xiaomi.xmsf.provider.MiCloudSettingsProvider";

        private AUTHORITY() {
        }
    }
}
