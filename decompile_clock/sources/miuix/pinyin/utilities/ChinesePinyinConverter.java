package miuix.pinyin.utilities;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.micloudsdk.utils.MiCloudRuntimeConstants;
import com.xiaomi.onetrack.b.e;
import com.xiaomi.onetrack.util.z;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import miuix.core.util.DirectIndexedFile;
import miuix.core.util.DirectIndexedFileExtractor;
import miuix.core.util.SoftReferenceSingleton;

/* JADX INFO: loaded from: classes3.dex */
public class ChinesePinyinConverter {
    private static final char FIRST_BASIC_UNIHAN = 19968;
    private static final char FIRST_HINDI_UNICODE = 2304;
    private static final char LAST_BASIC_UNIHAN = 40869;
    private static final char LAST_HINDI_UNICODE = 2431;
    private static final char SPECIAL_LING = 12295;
    private static final String TAG = "ChinesePinyinConverter";
    private ChinesePinyinDictionary mDictionary;
    private static HashMap<String, String[]> sHyphenatedNamePolyPhoneMap = new HashMap<>();
    private static HashMap<Character, String> sLastNamePolyPhoneMap = new HashMap<>();
    private static final SoftReferenceSingleton<ChinesePinyinConverter> INSTANCE = new SoftReferenceSingleton<ChinesePinyinConverter>() { // from class: miuix.pinyin.utilities.ChinesePinyinConverter.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // miuix.core.util.SoftReferenceSingleton
        public ChinesePinyinConverter createInstance(Object obj) {
            return new ChinesePinyinConverter((Context) obj);
        }
    };

    public boolean isChinesePinyinKnown(char c) {
        return c >= 19968 && c <= 40869;
    }

    static {
        sHyphenatedNamePolyPhoneMap.put("单于", new String[]{"CHAN", "YU"});
        sHyphenatedNamePolyPhoneMap.put("长孙", new String[]{"ZHANG", "SUN"});
        sHyphenatedNamePolyPhoneMap.put("子车", new String[]{"ZI", "JU"});
        sHyphenatedNamePolyPhoneMap.put("万俟", new String[]{"MO", "QI"});
        sHyphenatedNamePolyPhoneMap.put("澹台", new String[]{"TAN", "TAI"});
        sHyphenatedNamePolyPhoneMap.put("尉迟", new String[]{"YU", "CHI"});
        sLastNamePolyPhoneMap.put((char) 20040, "YAO");
        sLastNamePolyPhoneMap.put((char) 19969, "DING");
        sLastNamePolyPhoneMap.put((char) 20446, "YU");
        sLastNamePolyPhoneMap.put((char) 36158, "JIA");
        sLastNamePolyPhoneMap.put((char) 27784, "SHEN");
        sLastNamePolyPhoneMap.put((char) 21340, "BU");
        sLastNamePolyPhoneMap.put((char) 34180, "BO");
        sLastNamePolyPhoneMap.put((char) 23387, "BO");
        sLastNamePolyPhoneMap.put((char) 36146, "BEN");
        sLastNamePolyPhoneMap.put((char) 36153, "FEI");
        sLastNamePolyPhoneMap.put((char) 27850, "BAN");
        sLastNamePolyPhoneMap.put((char) 33536, "BI");
        sLastNamePolyPhoneMap.put((char) 35098, "CHU");
        sLastNamePolyPhoneMap.put((char) 20256, "CHUAN");
        sLastNamePolyPhoneMap.put((char) 21442, "CAN");
        sLastNamePolyPhoneMap.put((char) 21333, "SHAN");
        sLastNamePolyPhoneMap.put((char) 37079, "CHI");
        sLastNamePolyPhoneMap.put((char) 38241, "CHAN");
        sLastNamePolyPhoneMap.put((char) 26397, "CHAO");
        sLastNamePolyPhoneMap.put((char) 21852, "CHUAI");
        sLastNamePolyPhoneMap.put((char) 34928, "CUI");
        sLastNamePolyPhoneMap.put((char) 26216, "CHANG");
        sLastNamePolyPhoneMap.put((char) 19985, "CHOU");
        sLastNamePolyPhoneMap.put((char) 30259, "CHOU");
        sLastNamePolyPhoneMap.put((char) 38271, "CHANG");
        sLastNamePolyPhoneMap.put((char) 36710, "CHE");
        sLastNamePolyPhoneMap.put((char) 32735, "ZHAI");
        sLastNamePolyPhoneMap.put((char) 20291, "DIAN");
        sLastNamePolyPhoneMap.put((char) 20992, "DIAO");
        sLastNamePolyPhoneMap.put((char) 35843, "DIAO");
        sLastNamePolyPhoneMap.put((char) 36934, "DI");
        sLastNamePolyPhoneMap.put((char) 26123, "GUI");
        sLastNamePolyPhoneMap.put((char) 33445, "GAI");
        sLastNamePolyPhoneMap.put((char) 33554, "KUANG");
        sLastNamePolyPhoneMap.put((char) 37063, "HUAN");
        sLastNamePolyPhoneMap.put((char) 24055, "XIANG");
        sLastNamePolyPhoneMap.put((char) 25750, "HAN");
        sLastNamePolyPhoneMap.put((char) 35265, "JIAN");
        sLastNamePolyPhoneMap.put((char) 38477, "JIANG");
        sLastNamePolyPhoneMap.put((char) 35282, "JIAO");
        sLastNamePolyPhoneMap.put((char) 32564, "JIAO");
        sLastNamePolyPhoneMap.put((char) 35760, "JI");
        sLastNamePolyPhoneMap.put((char) 29722, "JU");
        sLastNamePolyPhoneMap.put((char) 21095, "JI");
        sLastNamePolyPhoneMap.put((char) 38589, "JUAN");
        sLastNamePolyPhoneMap.put((char) 38551, "KUI");
        sLastNamePolyPhoneMap.put((char) 38752, "KU");
        sLastNamePolyPhoneMap.put((char) 20048, "YUE");
        sLastNamePolyPhoneMap.put((char) 21895, "LA");
        sLastNamePolyPhoneMap.put((char) 38610, "LUO");
        sLastNamePolyPhoneMap.put((char) 20102, "LIAO");
        sLastNamePolyPhoneMap.put((char) 32554, "MIAO");
        sLastNamePolyPhoneMap.put((char) 20340, "MI");
        sLastNamePolyPhoneMap.put((char) 35884, "MIAO");
        sLastNamePolyPhoneMap.put((char) 20060, "NIE");
        sLastNamePolyPhoneMap.put((char) 36898, "PANG");
        sLastNamePolyPhoneMap.put((char) 34028, "PENG");
        sLastNamePolyPhoneMap.put((char) 26420, "PIAO");
        sLastNamePolyPhoneMap.put((char) 20167, "QIU");
        sLastNamePolyPhoneMap.put((char) 35203, "QIN");
        sLastNamePolyPhoneMap.put((char) 30655, "QU");
        sLastNamePolyPhoneMap.put((char) 20160, "SHI");
        sLastNamePolyPhoneMap.put((char) 25240, "SHE");
        sLastNamePolyPhoneMap.put((char) 30509, "SUI");
        sLastNamePolyPhoneMap.put((char) 35299, "XIE");
        sLastNamePolyPhoneMap.put((char) 31995, "XI");
        sLastNamePolyPhoneMap.put((char) 38500, "XU");
        sLastNamePolyPhoneMap.put((char) 21592, "YUAN");
        sLastNamePolyPhoneMap.put((char) 36128, "YUAN");
        sLastNamePolyPhoneMap.put((char) 26366, "ZENG");
        sLastNamePolyPhoneMap.put((char) 26597, "ZHA");
        sLastNamePolyPhoneMap.put((char) 20256, "CHUAN");
        sLastNamePolyPhoneMap.put((char) 21484, "SHAO");
        sLastNamePolyPhoneMap.put((char) 37325, "chong");
        sLastNamePolyPhoneMap.put((char) 21306, "OU");
        sLastNamePolyPhoneMap.put((char) 26044, "YU");
        sLastNamePolyPhoneMap.put((char) 31181, "CHONG");
    }

    private ChinesePinyinConverter(Context context) {
        this.mDictionary = new ChinesePinyinDictionary(context);
    }

    public static ChinesePinyinConverter getInstance(Context context) {
        return INSTANCE.get(context);
    }

    public static class Token {
        public static final int HINDI = 4;
        public static final int LATIN = 1;
        public static final int PINYIN = 2;
        private static final char SEPARATOR = ' ';
        public static final int UNKNOWN = 3;
        public String[] polyPhones;
        public String source;
        public String target;
        public int type;

        public Token() {
        }

        public Token(int i, String str, String str2) {
            this.type = i;
            this.source = str;
            this.target = str2;
        }
    }

    private ArrayList<Token> getPolyPhoneLastNameTokens(String str) {
        String strSubstring;
        String[] strArr;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        ArrayList<Token> arrayList = new ArrayList<>();
        if (str.length() >= 2 && (strArr = sHyphenatedNamePolyPhoneMap.get((strSubstring = str.substring(0, 2)))) != null) {
            for (int i = 0; i < strArr.length; i++) {
                Token token = new Token();
                token.type = 2;
                token.source = String.valueOf(strSubstring.charAt(i));
                token.target = strArr[i];
                arrayList.add(token);
            }
            return arrayList;
        }
        Character chValueOf = Character.valueOf(str.charAt(0));
        String str2 = sLastNamePolyPhoneMap.get(chValueOf);
        if (str2 == null) {
            return null;
        }
        Token token2 = new Token();
        token2.type = 2;
        token2.source = chValueOf.toString();
        token2.target = str2;
        arrayList.add(token2);
        return arrayList;
    }

    public ArrayList<Token> get(String str) {
        return get(str, true, true);
    }

    public ArrayList<Token> get(String str, boolean z, boolean z2) {
        int size;
        ArrayList<Token> polyPhoneLastNameTokens;
        ArrayList<Token> arrayList = new ArrayList<>();
        if (TextUtils.isEmpty(str)) {
            return arrayList;
        }
        if (z2 || (polyPhoneLastNameTokens = getPolyPhoneLastNameTokens(str)) == null || polyPhoneLastNameTokens.size() <= 0) {
            size = 0;
        } else {
            arrayList.addAll(polyPhoneLastNameTokens);
            size = polyPhoneLastNameTokens.size();
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder();
        int i = 1;
        while (size < length) {
            char cCharAt = str.charAt(size);
            if (cCharAt == ' ') {
                if (sb.length() > 0) {
                    addToken(sb, arrayList, i);
                }
                if (!z) {
                    String strValueOf = String.valueOf(' ');
                    arrayList.add(new Token(3, strValueOf, strValueOf));
                }
            } else {
                if (cCharAt < 256) {
                    if (size > 0) {
                        char cCharAt2 = str.charAt(size - 1);
                        if ((cCharAt2 >= '0' && cCharAt2 <= '9') != (cCharAt >= '0' && cCharAt <= '9') && sb.length() > 0) {
                            addToken(sb, arrayList, i);
                        }
                    }
                    if (i != 1 && sb.length() > 0) {
                        addToken(sb, arrayList, i);
                    }
                    sb.append(cCharAt);
                    i = 1;
                } else {
                    int i2 = 2;
                    if (cCharAt == 12295) {
                        Token token = new Token();
                        token.type = 2;
                        token.target = "ling";
                        if (sb.length() > 0) {
                            addToken(sb, arrayList, i);
                        }
                        arrayList.add(token);
                    } else if (cCharAt >= 19968 && cCharAt <= 40869) {
                        String[] pinyinString = this.mDictionary.getPinyinString(cCharAt);
                        Token token2 = new Token();
                        token2.source = Character.toString(cCharAt);
                        if (pinyinString == null) {
                            token2.type = 3;
                            token2.target = Character.toString(cCharAt);
                        } else {
                            token2.type = 2;
                            token2.target = pinyinString[0];
                            if (pinyinString.length > 1) {
                                token2.polyPhones = pinyinString;
                            }
                        }
                        if (token2.type == 2) {
                            if (sb.length() > 0) {
                                addToken(sb, arrayList, i);
                            }
                            arrayList.add(token2);
                        } else {
                            if (i != token2.type && sb.length() > 0) {
                                addToken(sb, arrayList, i);
                            }
                            i2 = token2.type;
                            sb.append(cCharAt);
                        }
                    } else if (cCharAt >= 2304 && cCharAt <= 2431) {
                        if (i != 4 && sb.length() > 0) {
                            addToken(sb, arrayList, i);
                        }
                        sb.append(cCharAt);
                        i = 4;
                    } else {
                        if (i != 3 && sb.length() > 0) {
                            addToken(sb, arrayList, i);
                        }
                        sb.append(cCharAt);
                    }
                    i = i2;
                }
                size++;
            }
            i = 3;
            size++;
        }
        if (sb.length() > 0) {
            addToken(sb, arrayList, i);
        }
        return arrayList;
    }

    private void addToken(StringBuilder sb, ArrayList<Token> arrayList, int i) {
        String string = sb.toString();
        Token token = new Token(i, string, string);
        if (4 == i) {
            String[] strArrHindiToPinyins = HindiPinyinConverter.getInstance().hindiToPinyins(token.source);
            if (strArrHindiToPinyins.length > 0) {
                if (strArrHindiToPinyins.length == 1) {
                    token.target = strArrHindiToPinyins[0];
                    token.polyPhones = new String[]{token.target};
                } else {
                    token.target = strArrHindiToPinyins[0];
                    token.polyPhones = strArrHindiToPinyins;
                }
            }
        }
        arrayList.add(token);
        sb.setLength(0);
    }

    public CharSequence convertPinyin2Bopomofo(String str, StringBuilder sb) {
        return BopomofoConvertor.convert(str, sb);
    }

    private static class ChinesePinyinDictionary {
        private static final String UNICODE_2_PINYIN_ASSET_NAME = "pinyinindex.idf";
        private DirectIndexedFile.Reader mReader;

        private ChinesePinyinDictionary(Context context) {
            String directIndexedFilePath = DirectIndexedFileExtractor.getDirectIndexedFilePath(context, UNICODE_2_PINYIN_ASSET_NAME);
            if (directIndexedFilePath != null && new File(directIndexedFilePath).exists()) {
                try {
                    this.mReader = DirectIndexedFile.open(directIndexedFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (this.mReader == null) {
                try {
                    this.mReader = DirectIndexedFile.open(context.getResources().getAssets().open(UNICODE_2_PINYIN_ASSET_NAME, 1));
                } catch (Exception unused) {
                    Log.e(ChinesePinyinConverter.TAG, "Init resource IOException");
                }
            }
        }

        public String[] getPinyinString(char c) {
            DirectIndexedFile.Reader reader = this.mReader;
            if (reader == null) {
                return null;
            }
            String str = (String) reader.get(0, c - 19968, 0);
            if (TextUtils.isEmpty(str)) {
                Log.e(ChinesePinyinConverter.TAG, "The ChinesePinyinConverter dictionary is not correct, need rebuild or reset the ROM.");
                return null;
            }
            return str.split(z.b);
        }

        protected void finalize() throws Throwable {
            DirectIndexedFile.Reader reader = this.mReader;
            if (reader != null) {
                reader.close();
            }
            super.finalize();
        }
    }

    private static class BopomofoConvertor {
        private static final String[] sPinyinToZhuyin = {"b", "ㄅ", MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.PERSONAL, "ㄆ", "m", "ㄇ", "f", "ㄈ", "d", "ㄉ", "t", "ㄊ", "n", "ㄋ", e.a, "ㄌ", MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.GLOBAL, "ㄍ", "k", "ㄎ", "h", "ㄏ", "j", "ㄐ", "q", "ㄑ", "x", "ㄒ", "zh", "ㄓ", "ch", "ㄔ", "sh", "ㄕ", "r", "ㄖ", "z", "ㄗ", "c", "ㄘ", MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.SUBSCRIPTION, "ㄙ", "y", "ㄧ", "w", "ㄨ", "a", "ㄚ", "o", "ㄛ", "e", "ㄜ", "i", "ㄧ", "u", "ㄨ", "v", "ㄩ", "ao", "ㄠ", "ai", "ㄞ", "an", "ㄢ", "ang", "ㄤ", "ou", "ㄡ", "ong", "ㄨㄥ", "ei", "ㄟ", "en", "ㄣ", "eng", "ㄥ", "er", "ㄦ", "ie", "ㄧㄝ", "iu", "ㄧㄡ", "in", "ㄧㄣ", "ing", "ㄧㄥ", "iong", "ㄩㄥ", "ui", "ㄨㄟ", "un", "ㄨㄣ", "ue", "ㄩㄝ", "ve", "ㄩㄝ", "van", "ㄩㄢ", "vn", "ㄩㄣ", "", "", "zhi", "ㄓ", "chi", "ㄔ", "shi", "ㄕ", "ri", "ㄖ", "zi", "ㄗ", "ci", "ㄘ", "si", "ㄙ", "yi", "ㄧ", "ye", "ㄧㄝ", "you", "ㄧㄡ", "yin", "ㄧㄣ", "ying", "ㄧㄥ", "wu", "ㄨ", "wei", "ㄨㄟ", "wen", "ㄨㄣ", "yu", "ㄩ", "yue", "ㄩㄝ", "yuan", "ㄩㄢ", "yun", "ㄩㄣ", "yong", "ㄩㄥ", "ju", "ㄐㄩ", "jue", "ㄐㄩㄝ", "juan", "ㄐㄩㄢ", "jun", "ㄐㄩㄣ", "qu", "ㄑㄩ", "que", "ㄑㄩㄝ", "quan", "ㄑㄩㄢ", "qun", "ㄑㄩㄣ", "xu", "ㄒㄩ", "xue", "ㄒㄩㄝ", "xuan", "ㄒㄩㄢ", "xun", "ㄒㄩㄣ"};
        private static final Node sRoot = build();

        private BopomofoConvertor() {
        }

        private static class Node {
            public char ch;
            public Node children;
            public Node next;
            public String output;
            public boolean specials;

            private Node() {
            }
        }

        private static Node build() {
            Node node = new Node();
            node.output = "";
            int i = 0;
            boolean z = false;
            while (true) {
                String[] strArr = sPinyinToZhuyin;
                if (i >= strArr.length) {
                    return node;
                }
                String str = strArr[i];
                if (str.length() == 0) {
                    z = true;
                } else {
                    int length = str.length();
                    Node node2 = node;
                    for (int i2 = 0; i2 < length; i2++) {
                        char lowerCase = Character.toLowerCase(str.charAt(i2));
                        Node node3 = node2.children;
                        while (node3 != null && node3.ch != lowerCase) {
                            node3 = node3.next;
                        }
                        if (node3 == null) {
                            node3 = new Node();
                            node3.ch = lowerCase;
                            node3.next = node2.children;
                            node2.children = node3;
                        }
                        node2 = node3;
                    }
                    node2.specials = z;
                    node2.output = sPinyinToZhuyin[i + 1];
                }
                i += 2;
            }
        }

        private static int convert(String str, int i, StringBuilder sb) {
            int length = str.length();
            Node node = sRoot;
            Node node2 = null;
            int i2 = i;
            int i3 = i2;
            while (i2 < length && node != null) {
                char lowerCase = Character.toLowerCase(str.charAt(i2));
                node = node.children;
                while (node != null && node.ch != lowerCase) {
                    node = node.next;
                }
                if (node != null && node.output != null && (!node.specials || i2 == length - 1)) {
                    i3 = i2 + 1;
                    node2 = node;
                }
                i2++;
            }
            if (node2 != null) {
                sb.append(node2.output);
            }
            return i3 - i;
        }

        public static CharSequence convert(String str, StringBuilder sb) {
            if (str == null || str.length() == 0) {
                return str;
            }
            int i = 0;
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.setLength(0);
            }
            int length = str.length();
            while (i != length) {
                int iConvert = convert(str, i, sb);
                if (iConvert <= 0) {
                    return "";
                }
                i += iConvert;
            }
            return sb;
        }
    }
}
