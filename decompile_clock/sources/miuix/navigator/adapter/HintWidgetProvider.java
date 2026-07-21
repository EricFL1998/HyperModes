package miuix.navigator.adapter;

import android.view.ViewGroup;
import android.widget.TextView;
import androidx.collection.ArrayMap;
import java.util.Map;
import miuix.navigator.R;

/* JADX INFO: loaded from: classes3.dex */
public class HintWidgetProvider<T> extends LayoutWidgetProvider<T> {
    private final Map<T, CharSequence> mMap;

    public HintWidgetProvider() {
        super(R.layout.miuix_navigator_item_widget_hint);
        this.mMap = new ArrayMap();
    }

    public void setHint(T t, CharSequence charSequence) {
        this.mMap.put(t, charSequence);
    }

    public CharSequence getHint(T t) {
        if (this.mMap.containsKey(t)) {
            return this.mMap.get(t);
        }
        return "";
    }

    @Override // miuix.navigator.adapter.LayoutWidgetProvider, miuix.navigator.adapter.WidgetProvider
    public void onSetupWidget(ViewGroup viewGroup, T t, boolean z) {
        if (getWidgetFrameRes() == 0) {
            return;
        }
        TextView textView = (TextView) viewGroup.findViewById(R.id.miuix_navigator_item_widget_hint);
        textView.setVisibility(z ? 8 : 0);
        textView.setText(getHint(t));
    }
}
