package miuix.navigator.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.Menu;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.widget.TintTypedArray;
import java.io.IOException;
import miuix.navigator.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes3.dex */
class NavigationMenuInflater extends SupportMenuInflater {
    private static final String XML_GROUP = "group";
    private static final String XML_ITEM = "item";
    private static final String XML_MENU = "menu";
    private Context mContext;

    public NavigationMenuInflater(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // androidx.appcompat.view.SupportMenuInflater, android.view.MenuInflater
    public void inflate(int i, Menu menu) {
        super.inflate(i, menu);
        XmlResourceParser layout = null;
        try {
            try {
                layout = this.mContext.getResources().getLayout(i);
                parseMenuExtraData(layout, Xml.asAttributeSet(layout), menu);
                if (layout != null) {
                    layout.close();
                }
            } catch (IOException e) {
                throw new InflateException("Error inflating menu XML", e);
            } catch (XmlPullParserException e2) {
                throw new InflateException("Error inflating menu XML", e2);
            }
        } catch (Throwable th) {
            if (layout != null) {
                layout.close();
            }
            throw th;
        }
    }

    /* JADX WARN: Code duplicated, block: B:31:0x006b A[PHI: r9
  0x006b: PHI (r9v2 int) = (r9v1 int), (r9v1 int), (r9v1 int), (r9v1 int), (r9v1 int), (r9v4 int) binds: [B:35:0x007e, B:38:0x0089, B:18:0x0045, B:26:0x0060, B:33:0x0075, B:30:0x0069] A[DONT_GENERATE, DONT_INLINE]] */
    private void parseMenuExtraData(XmlPullParser xmlPullParser, AttributeSet attributeSet, Menu menu) throws XmlPullParserException, IOException {
        int eventType = xmlPullParser.getEventType();
        do {
            if (eventType == 2) {
                String name = xmlPullParser.getName();
                if (name.equals(XML_MENU)) {
                    eventType = xmlPullParser.next();
                    break;
                }
                throw new RuntimeException("Expecting menu, got " + name);
            }
            eventType = xmlPullParser.next();
        } while (eventType != 1);
        boolean z = false;
        boolean z2 = false;
        int i = 0;
        String str = null;
        while (!z) {
            if (eventType == 1) {
                throw new RuntimeException("Unexpected end of document");
            }
            if (eventType != 2) {
                if (eventType == 3) {
                    String name2 = xmlPullParser.getName();
                    if (z2 && name2.equals(str)) {
                        z2 = false;
                        str = null;
                    } else if (!name2.equals(XML_GROUP)) {
                        if (name2.equals(XML_ITEM)) {
                            i++;
                        } else if (name2.equals(XML_MENU)) {
                            z = true;
                        }
                    }
                }
            } else if (!z2) {
                String name3 = xmlPullParser.getName();
                if (!name3.equals(XML_GROUP)) {
                    if (name3.equals(XML_ITEM)) {
                        readExtraDataToItem(attributeSet, menu, i);
                    } else if (!name3.equals(XML_MENU)) {
                        str = name3;
                        z2 = true;
                    }
                }
            }
            eventType = xmlPullParser.next();
        }
    }

    private void readExtraDataToItem(AttributeSet attributeSet, Menu menu, int i) {
        TintTypedArray tintTypedArrayObtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mContext, attributeSet, R.styleable.NavigationMenuItem);
        int i2 = tintTypedArrayObtainStyledAttributes.getInt(R.styleable.NavigationMenuItem_miuixNavigationId, -1);
        tintTypedArrayObtainStyledAttributes.recycle();
        if (i2 != -1) {
            Intent intent = new Intent();
            intent.putExtra(NavigationBarMenu.EXTRA_BOTTOM_TAB_ID, i2);
            menu.getItem(i).setIntent(intent);
        }
    }
}
