package miuix.navigator.draganddrop;

import android.view.DragEvent;
import androidx.recyclerview.widget.RecyclerView;
import miuix.navigator.adapter.CategoryAdapter;

/* JADX INFO: loaded from: classes3.dex */
public interface NavigatorDragListener {
    void onBindDragPlaceholder(RecyclerView.ViewHolder viewHolder, boolean z);

    void onDragStart(DragEvent dragEvent, DragStartFeedback dragStartFeedback);

    boolean onDropAccept(DragEvent dragEvent, RecyclerView.ViewHolder viewHolder);

    boolean onDropInsert(DragEvent dragEvent, CategoryAdapter<? extends CategoryAdapter.Item> categoryAdapter, int i);

    default void onDragHover(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.itemView.isActivated()) {
            return;
        }
        viewHolder.itemView.performClick();
    }
}
