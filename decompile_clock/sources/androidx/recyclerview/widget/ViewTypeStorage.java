package androidx.recyclerview.widget;

import android.util.SparseArray;
import android.util.SparseIntArray;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
interface ViewTypeStorage {

    public interface ViewTypeLookup {
        void dispose();

        int globalToLocal(int i);

        int localToGlobal(int i);
    }

    ViewTypeLookup createViewTypeWrapper(NestedAdapterWrapper nestedAdapterWrapper);

    NestedAdapterWrapper getWrapperForGlobalType(int i);

    public static class SharedIdRangeViewTypeStorage implements ViewTypeStorage {
        SparseArray<List<NestedAdapterWrapper>> mGlobalTypeToWrapper = new SparseArray<>();

        @Override // androidx.recyclerview.widget.ViewTypeStorage
        public NestedAdapterWrapper getWrapperForGlobalType(int i) {
            List<NestedAdapterWrapper> list = this.mGlobalTypeToWrapper.get(i);
            if (list == null || list.isEmpty()) {
                throw new IllegalArgumentException("Cannot find the wrapper for global view type " + i);
            }
            return list.get(0);
        }

        @Override // androidx.recyclerview.widget.ViewTypeStorage
        public ViewTypeLookup createViewTypeWrapper(NestedAdapterWrapper nestedAdapterWrapper) {
            return new WrapperViewTypeLookup(nestedAdapterWrapper);
        }

        void removeWrapper(NestedAdapterWrapper nestedAdapterWrapper) {
            for (int size = this.mGlobalTypeToWrapper.size() - 1; size >= 0; size--) {
                List<NestedAdapterWrapper> listValueAt = this.mGlobalTypeToWrapper.valueAt(size);
                if (listValueAt.remove(nestedAdapterWrapper) && listValueAt.isEmpty()) {
                    this.mGlobalTypeToWrapper.removeAt(size);
                }
            }
        }

        class WrapperViewTypeLookup implements ViewTypeLookup {
            final NestedAdapterWrapper mWrapper;

            @Override // androidx.recyclerview.widget.ViewTypeStorage.ViewTypeLookup
            public int globalToLocal(int i) {
                return i;
            }

            WrapperViewTypeLookup(NestedAdapterWrapper nestedAdapterWrapper) {
                this.mWrapper = nestedAdapterWrapper;
            }

            @Override // androidx.recyclerview.widget.ViewTypeStorage.ViewTypeLookup
            public int localToGlobal(int i) {
                List<NestedAdapterWrapper> arrayList = SharedIdRangeViewTypeStorage.this.mGlobalTypeToWrapper.get(i);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                    SharedIdRangeViewTypeStorage.this.mGlobalTypeToWrapper.put(i, arrayList);
                }
                if (!arrayList.contains(this.mWrapper)) {
                    arrayList.add(this.mWrapper);
                }
                return i;
            }

            @Override // androidx.recyclerview.widget.ViewTypeStorage.ViewTypeLookup
            public void dispose() {
                SharedIdRangeViewTypeStorage.this.removeWrapper(this.mWrapper);
            }
        }
    }

    public static class IsolatedViewTypeStorage implements ViewTypeStorage {
        SparseArray<NestedAdapterWrapper> mGlobalTypeToWrapper = new SparseArray<>();
        int mNextViewType = 0;

        int obtainViewType(NestedAdapterWrapper nestedAdapterWrapper) {
            int i = this.mNextViewType;
            this.mNextViewType = i + 1;
            this.mGlobalTypeToWrapper.put(i, nestedAdapterWrapper);
            return i;
        }

        @Override // androidx.recyclerview.widget.ViewTypeStorage
        public NestedAdapterWrapper getWrapperForGlobalType(int i) {
            NestedAdapterWrapper nestedAdapterWrapper = this.mGlobalTypeToWrapper.get(i);
            if (nestedAdapterWrapper != null) {
                return nestedAdapterWrapper;
            }
            throw new IllegalArgumentException("Cannot find the wrapper for global view type " + i);
        }

        @Override // androidx.recyclerview.widget.ViewTypeStorage
        public ViewTypeLookup createViewTypeWrapper(NestedAdapterWrapper nestedAdapterWrapper) {
            return new WrapperViewTypeLookup(nestedAdapterWrapper);
        }

        void removeWrapper(NestedAdapterWrapper nestedAdapterWrapper) {
            for (int size = this.mGlobalTypeToWrapper.size() - 1; size >= 0; size--) {
                if (this.mGlobalTypeToWrapper.valueAt(size) == nestedAdapterWrapper) {
                    this.mGlobalTypeToWrapper.removeAt(size);
                }
            }
        }

        class WrapperViewTypeLookup implements ViewTypeLookup {
            final NestedAdapterWrapper mWrapper;
            private SparseIntArray mLocalToGlobalMapping = new SparseIntArray(1);
            private SparseIntArray mGlobalToLocalMapping = new SparseIntArray(1);

            WrapperViewTypeLookup(NestedAdapterWrapper nestedAdapterWrapper) {
                this.mWrapper = nestedAdapterWrapper;
            }

            @Override // androidx.recyclerview.widget.ViewTypeStorage.ViewTypeLookup
            public int localToGlobal(int i) {
                int iIndexOfKey = this.mLocalToGlobalMapping.indexOfKey(i);
                if (iIndexOfKey > -1) {
                    return this.mLocalToGlobalMapping.valueAt(iIndexOfKey);
                }
                int iObtainViewType = IsolatedViewTypeStorage.this.obtainViewType(this.mWrapper);
                this.mLocalToGlobalMapping.put(i, iObtainViewType);
                this.mGlobalToLocalMapping.put(iObtainViewType, i);
                return iObtainViewType;
            }

            @Override // androidx.recyclerview.widget.ViewTypeStorage.ViewTypeLookup
            public int globalToLocal(int i) {
                int iIndexOfKey = this.mGlobalToLocalMapping.indexOfKey(i);
                if (iIndexOfKey < 0) {
                    throw new IllegalStateException("requested global type " + i + " does not belong to the adapter:" + this.mWrapper.adapter);
                }
                return this.mGlobalToLocalMapping.valueAt(iIndexOfKey);
            }

            @Override // androidx.recyclerview.widget.ViewTypeStorage.ViewTypeLookup
            public void dispose() {
                IsolatedViewTypeStorage.this.removeWrapper(this.mWrapper);
            }
        }
    }
}
