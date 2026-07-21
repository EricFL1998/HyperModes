package miuix.appcompat.view.menu;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.widget.TintTypedArray;
import java.io.IOException;
import miuix.appcompat.R;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
public class HyperMenuInflater extends SupportMenuInflater {
    public static final String DEBUG_TAG = "HyperMenuInflater";
    private static final String XML_GROUP = "group";
    private static final String XML_ITEM = "item";
    private static final String XML_MENU = "menu";
    Context mContext;

    public HyperMenuInflater(Context context) {
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
                parseHyperGroupMenu(layout, Xml.asAttributeSet(layout), menu);
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

    private void parseHyperGroupMenu(XmlResourceParser xmlResourceParser, AttributeSet attributeSet, Menu menu) throws XmlPullParserException, IOException {
        int eventType = xmlResourceParser.getEventType();
        do {
            if (eventType == 2) {
                String name = xmlResourceParser.getName();
                if (name.equals(XML_MENU)) {
                    eventType = xmlResourceParser.next();
                    break;
                }
                throw new RuntimeException("Expecting menu, got " + name);
            }
            eventType = xmlResourceParser.next();
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
                    String name2 = xmlResourceParser.getName();
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
                String name3 = xmlResourceParser.getName();
                if (name3.equals(XML_GROUP)) {
                    appendExtraDataToGroup(attributeSet, menu, i);
                } else if (name3.equals(XML_ITEM)) {
                    appendExtraDataToItem(attributeSet, menu, i);
                } else if (name3.equals(XML_MENU)) {
                    i = 0;
                } else {
                    str = name3;
                    z2 = true;
                }
            }
            eventType = xmlResourceParser.next();
        }
    }

    private void appendExtraDataToGroup(AttributeSet attributeSet, Menu menu, int i) {
        TintTypedArray tintTypedArrayObtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mContext, attributeSet, R.styleable.HyperMenuItem);
        int i2 = tintTypedArrayObtainStyledAttributes.getInt(R.styleable.HyperMenuItem_hyperMenuGroupForeignKey, -1);
        tintTypedArrayObtainStyledAttributes.recycle();
        if (i2 != -1) {
            MenuItem item = menu.getItem(i);
            Intent intent = item.getIntent();
            if (intent == null) {
                intent = new Intent();
            }
            intent.putExtra(HyperMenuContract.HYPER_MENU_GROUP_FOREIGN_KEY, i2);
            item.setIntent(intent);
        }
    }

    private void appendExtraDataToItem(AttributeSet attributeSet, Menu menu, int i) {
        TintTypedArray tintTypedArrayObtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mContext, attributeSet, R.styleable.HyperMenuItem);
        int i2 = tintTypedArrayObtainStyledAttributes.getInt(R.styleable.HyperMenuItem_hyperMenuItemGroupId, -1);
        int i3 = tintTypedArrayObtainStyledAttributes.getInt(R.styleable.HyperMenuItem_hyperMenuItemForeignKey, -1);
        tintTypedArrayObtainStyledAttributes.recycle();
        MenuItem item = menu.getItem(i);
        if (i2 != -1) {
            Intent intent = item.getIntent();
            if (intent == null) {
                intent = new Intent();
            }
            intent.putExtra(HyperMenuContract.HYPER_MENU_GROUP_ID, i2);
            item.setIntent(intent);
        }
        if (i3 != -1) {
            Intent intent2 = item.getIntent();
            if (intent2 == null) {
                intent2 = new Intent();
            }
            intent2.putExtra(HyperMenuContract.HYPER_MENU_ITEM_FOREIGN_KEY, i3);
            item.setIntent(intent2);
        }
    }
}
