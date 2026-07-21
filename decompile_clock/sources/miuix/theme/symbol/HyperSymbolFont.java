package miuix.theme.symbol;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.widget.TextView;
import miuix.theme.FontLoader;

/* JADX INFO: loaded from: classes3.dex */
public class HyperSymbolFont {
    static final int BASE_WEIGHT = 330;
    static final int DEFAULT_VF_SCALE = 50;
    private static final String FONT_WEIGHT_SCALE_PREFIX = "key_var_font_scale=";
    static final String HOS_SYMBOL_FONT_PATH = "fonts/misymbol_vf.ttf";
    static final int MAX_WEIGHT = 500;
    static final int MIN_WEIGHT = 250;
    public static final String RESET = String.valueOf(Character.toChars(983040));
    public static final String REPLACE = String.valueOf(Character.toChars(983041));
    public static final String PLAY = String.valueOf(Character.toChars(983042));
    public static final String MORE = String.valueOf(Character.toChars(983043));
    public static final String SEARCH = String.valueOf(Character.toChars(983044));
    public static final String REDO = String.valueOf(Character.toChars(983045));
    public static final String UNDO = String.valueOf(Character.toChars(983046));
    public static final String PAUSE = String.valueOf(Character.toChars(983047));
    public static final String OK = String.valueOf(Character.toChars(983048));
    public static final String CLOSE = String.valueOf(Character.toChars(983049));
    public static final String BACK = String.valueOf(Character.toChars(983050));
    public static final String LINK = String.valueOf(Character.toChars(983051));
    public static final String HIDE = String.valueOf(Character.toChars(983052));
    public static final String SHOW = String.valueOf(Character.toChars(983053));
    public static final String STORE = String.valueOf(Character.toChars(983054));
    public static final String COPY = String.valueOf(Character.toChars(983055));
    public static final String PASTE = String.valueOf(Character.toChars(983056));
    public static final String LOCK = String.valueOf(Character.toChars(983057));
    public static final String UNLOCK = String.valueOf(Character.toChars(983058));
    public static final String COMMUNITY = String.valueOf(Character.toChars(983059));
    public static final String TRANSLATE = String.valueOf(Character.toChars(983060));
    public static final String DOWNLOAD = String.valueOf(Character.toChars(983061));
    public static final String SCAN = String.valueOf(Character.toChars(983062));
    public static final String DELETE = String.valueOf(Character.toChars(983063));
    public static final String FILE = String.valueOf(Character.toChars(983064));
    public static final String SIDEBAR = String.valueOf(Character.toChars(983065));
    public static final String EDIT = String.valueOf(Character.toChars(983066));
    public static final String SHARE = String.valueOf(Character.toChars(983067));
    public static final String MOVE_FILE = String.valueOf(Character.toChars(983068));
    public static final String SETTINGS = String.valueOf(Character.toChars(983069));
    public static final String FAVORITES = String.valueOf(Character.toChars(983070));
    public static final String FAVORITES_FILL = String.valueOf(Character.toChars(983099));
    public static final String REPORT = String.valueOf(Character.toChars(983071));
    public static final String SEARCH_DEVICE = String.valueOf(Character.toChars(983072));
    public static final String CLOSE_2 = String.valueOf(Character.toChars(983073));
    public static final String REFRESH = String.valueOf(Character.toChars(983074));
    public static final String ANSWER = String.valueOf(Character.toChars(983075));
    public static final String HELP = String.valueOf(Character.toChars(983076));
    public static final String MORE_CIRCLE = String.valueOf(Character.toChars(983077));
    public static final String INFO = String.valueOf(Character.toChars(983078));
    public static final String ADD_CIRCLE = String.valueOf(Character.toChars(983079));
    public static final String UPDATE = String.valueOf(Character.toChars(983080));
    public static final String CUT = String.valueOf(Character.toChars(983081));
    public static final String RENAME = String.valueOf(Character.toChars(983082));
    public static final String PIN = String.valueOf(Character.toChars(983083));
    public static final String UNPIN = String.valueOf(Character.toChars(983084));
    public static final String SELECT_ALL = String.valueOf(Character.toChars(983085));
    public static final String PLAYLIST = String.valueOf(Character.toChars(983086));
    public static final String SORT = String.valueOf(Character.toChars(983144));
    public static final String CREATE = String.valueOf(Character.toChars(983145));
    public static final String VOLUME_UP = String.valueOf(Character.toChars(983146));
    public static final String VOLUME_OFF = String.valueOf(Character.toChars(983147));
    public static final String TRIM = String.valueOf(Character.toChars(983148));
    public static final String FILTER = String.valueOf(Character.toChars(983149));
    public static final String IMPORT = String.valueOf(Character.toChars(983150));
    public static final String FOLDER = String.valueOf(Character.toChars(983151));
    public static final String ADD_FOLDER = String.valueOf(Character.toChars(983152));
    public static final String REMOVE = String.valueOf(Character.toChars(983153));
    public static final String ROTATE_LEFT = String.valueOf(Character.toChars(983154));
    public static final String MERGE = String.valueOf(Character.toChars(983155));
    public static final String RECORDING_TAPE = String.valueOf(Character.toChars(983156));
    public static final String NOTES = String.valueOf(Character.toChars(983157));
    public static final String MUSIC = String.valueOf(Character.toChars(983158));
    public static final String REMOVE_CONTACT = String.valueOf(Character.toChars(983159));
    public static final String BLOCKLIST = String.valueOf(Character.toChars(983160));
    public static final String CONTACTS = String.valueOf(Character.toChars(983161));
    public static final String CONTACTS_BOOK = String.valueOf(Character.toChars(983162));
    public static final String THEME = String.valueOf(Character.toChars(983163));
    public static final String CONVERT_FILE = String.valueOf(Character.toChars(983164));
    public static final String IMAGE = String.valueOf(Character.toChars(983165));
    public static final String TUNE = String.valueOf(Character.toChars(983166));
    public static final String ADD = String.valueOf(Character.toChars(983167));
    public static final String MIC = String.valueOf(Character.toChars(983168));
    public static final String MIC_SLASH = String.valueOf(Character.toChars(983169));
    public static final String REPLY = String.valueOf(Character.toChars(983170));
    public static final String REPLY_ALL = String.valueOf(Character.toChars(983171));
    public static final String BACKUP = String.valueOf(Character.toChars(983172));
    public static final String EXPAND_MORE = String.valueOf(Character.toChars(983173));
    public static final String EXPAND_LESS = String.valueOf(Character.toChars(983174));
    public static final String MIND_MAP = String.valueOf(Character.toChars(983175));
    public static final String LAYERS = String.valueOf(Character.toChars(983176));
    public static final String BACKGROUND = String.valueOf(Character.toChars(983177));
    public static final String MAP_ALBUM = String.valueOf(Character.toChars(983178));
    public static final String HORIZONTAL_SPLIT = String.valueOf(Character.toChars(983179));
    public static final String VERTICAL_SPLIT = String.valueOf(Character.toChars(983180));
    public static final String CHEVRON_BACKWARD = String.valueOf(Character.toChars(983181));
    public static final String CHEVRON_FORWARD = String.valueOf(Character.toChars(983182));
    public static final String ZOOM_OUT = String.valueOf(Character.toChars(983183));
    public static final String EMAIL = String.valueOf(Character.toChars(983184));
    public static final String SEND = String.valueOf(Character.toChars(983185));
    public static final String FORWARD = String.valueOf(Character.toChars(983186));
    public static final String CLEAR = String.valueOf(Character.toChars(983189));
    public static final String UPLOAD_CLOUD = String.valueOf(Character.toChars(983190));
    public static final String ALARM = String.valueOf(Character.toChars(983087));
    public static final String WORLD_CLOCK = String.valueOf(Character.toChars(983088));
    public static final String RECENT = String.valueOf(Character.toChars(983089));
    public static final String STOPWATCH = String.valueOf(Character.toChars(983090));
    public static final String TIMER = String.valueOf(Character.toChars(983091));
    public static final String FOLDER_FILL = String.valueOf(Character.toChars(983092));
    public static final String CLOUD_FILL = String.valueOf(Character.toChars(983093));
    public static final String PHOTOS = String.valueOf(Character.toChars(983094));
    public static final String ALBUM = String.valueOf(Character.toChars(983095));
    public static final String NOTES_FILL = String.valueOf(Character.toChars(983096));
    public static final String TASKS = String.valueOf(Character.toChars(983097));
    public static final String PHONE = String.valueOf(Character.toChars(983098));
    public static final String CONTACTS_CIRCLE = String.valueOf(Character.toChars(983100));
    public static final String CARRIER = String.valueOf(Character.toChars(983101));
    public static final String MESSAGES = String.valueOf(Character.toChars(983102));
    public static final String PROMOTIONS = String.valueOf(Character.toChars(983103));
    public static final String BANK_CARDS = String.valueOf(Character.toChars(983104));
    public static final String YEARS = String.valueOf(Character.toChars(983105));
    public static final String MONTHS = String.valueOf(Character.toChars(983106));
    public static final String WEEKS = String.valueOf(Character.toChars(983107));
    public static final String LOCATION = String.valueOf(Character.toChars(983108));
    public static final String ALL = String.valueOf(Character.toChars(983109));
    public static final String RECORDING = String.valueOf(Character.toChars(983110));
    public static final String CALL_RECORDING = String.valueOf(Character.toChars(983111));
    public static final String APP_RECORDING = String.valueOf(Character.toChars(983112));
    public static final String FILE_DOWNLOADS = String.valueOf(Character.toChars(983187));
    public static final String TOP_DOWNLOADS = String.valueOf(Character.toChars(983188));
    public static final String TH_1 = String.valueOf(Character.toChars(983113));
    public static final String TH_2 = String.valueOf(Character.toChars(983114));
    public static final String TH_3 = String.valueOf(Character.toChars(983115));
    public static final String TH_4 = String.valueOf(Character.toChars(983116));
    public static final String TH_5 = String.valueOf(Character.toChars(983117));
    public static final String TH_6 = String.valueOf(Character.toChars(983118));
    public static final String TH_7 = String.valueOf(Character.toChars(983119));
    public static final String TH_8 = String.valueOf(Character.toChars(983120));
    public static final String TH_9 = String.valueOf(Character.toChars(983121));
    public static final String TH_10 = String.valueOf(Character.toChars(983122));
    public static final String TH_11 = String.valueOf(Character.toChars(983123));
    public static final String TH_12 = String.valueOf(Character.toChars(983124));
    public static final String TH_13 = String.valueOf(Character.toChars(983125));
    public static final String TH_14 = String.valueOf(Character.toChars(983126));
    public static final String TH_15 = String.valueOf(Character.toChars(983127));
    public static final String TH_16 = String.valueOf(Character.toChars(983128));
    public static final String TH_17 = String.valueOf(Character.toChars(983129));
    public static final String TH_18 = String.valueOf(Character.toChars(983130));
    public static final String TH_19 = String.valueOf(Character.toChars(983131));
    public static final String TH_20 = String.valueOf(Character.toChars(983132));
    public static final String TH_21 = String.valueOf(Character.toChars(983133));
    public static final String TH_22 = String.valueOf(Character.toChars(983134));
    public static final String TH_23 = String.valueOf(Character.toChars(983135));
    public static final String TH_24 = String.valueOf(Character.toChars(983136));
    public static final String TH_25 = String.valueOf(Character.toChars(983137));
    public static final String TH_26 = String.valueOf(Character.toChars(983138));
    public static final String TH_27 = String.valueOf(Character.toChars(983139));
    public static final String TH_28 = String.valueOf(Character.toChars(983140));
    public static final String TH_29 = String.valueOf(Character.toChars(983141));
    public static final String TH_30 = String.valueOf(Character.toChars(983142));
    public static final String TH_31 = String.valueOf(Character.toChars(983143));
    public static final String LIST_VIEW = String.valueOf(Character.toChars(983191));
    public static final String GRID_VIEW = String.valueOf(Character.toChars(983192));
    public static final String SCREEN_CAPTURE = String.valueOf(Character.toChars(983193));
    public static final String SCREEN_MIRRORING = String.valueOf(Character.toChars(983194));
    public static final String READ = String.valueOf(Character.toChars(983196));
    public static final String MARK_ALL_AS_READ = String.valueOf(Character.toChars(983197));
    public static final String MARK_AS_READ = String.valueOf(Character.toChars(983198));
    public static final String SAVE = String.valueOf(Character.toChars(983199));
    public static final String STARRED = String.valueOf(Character.toChars(983200));
    public static final String INSUFFICIENT_CLOUD_STORAGE_SPACE = String.valueOf(Character.toChars(983201));
    public static final String UNSTAR = String.valueOf(Character.toChars(983202));
    public static final String MARK_RAS_UNREAD = String.valueOf(Character.toChars(983203));

    public static int getWeightByScale(int i) {
        float f;
        float f2;
        if (i < 0) {
            return 250;
        }
        if (i > 100) {
            return 500;
        }
        if (i == 50) {
            return 330;
        }
        if (i < 50) {
            f = ((i * 1.0f) * 80.0f) / 50.0f;
            f2 = 250.0f;
        } else {
            f = (((i - 50) * 1.0f) * 170.0f) / 50.0f;
            f2 = 330.0f;
        }
        return (int) (f + f2);
    }

    public static synchronized Typeface getDefaultSymbolFont(Context context) {
        if (context == null) {
            return null;
        }
        return FontLoader.getFont(context.getAssets(), HOS_SYMBOL_FONT_PATH);
    }

    public static synchronized void applyDefaultSymbolFont(TextView textView) {
        Typeface defaultSymbolFont;
        Context context = textView.getContext();
        if (context != null && (defaultSymbolFont = getDefaultSymbolFont(context)) != null) {
            textView.setTypeface(defaultSymbolFont);
        }
    }

    public static int getFontWeightScaleFromConfig(Configuration configuration) {
        if (Build.VERSION.SDK_INT >= 31 && configuration.fontWeightAdjustment > 0) {
            return configuration.fontWeightAdjustment;
        }
        String string = configuration.toString();
        int iIndexOf = string.indexOf(FONT_WEIGHT_SCALE_PREFIX);
        if (iIndexOf == -1) {
            return 50;
        }
        try {
            int i = iIndexOf + 19;
            int iIndexOf2 = string.indexOf("}", i);
            if (iIndexOf2 == -1) {
                return 50;
            }
            return Integer.parseInt(string.substring(i, iIndexOf2));
        } catch (Exception unused) {
            return 50;
        }
    }

    public static int getWeightByConfig(Configuration configuration) {
        return getWeightByScale(getFontWeightScaleFromConfig(configuration));
    }
}
