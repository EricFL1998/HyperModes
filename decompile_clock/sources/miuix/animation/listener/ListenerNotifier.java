package miuix.animation.listener;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import miuix.animation.FolmeFactory;
import miuix.animation.IAnimTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.ObjectPool;

/* JADX INFO: loaded from: classes2.dex */
public class ListenerNotifier {
    final Map<Object, List<TransitionListener>> mListenerMap = new ConcurrentHashMap();
    final IAnimTarget mTarget;
    static final BeginNotifier sBegin = new BeginNotifier();
    static final UpdateNotifier sUpdate = new UpdateNotifier();
    static final CancelNotifier sCancelAll = new CancelNotifier();
    static final EndNotifier sEndAll = new EndNotifier();

    interface INotifier {
        void doNotify(Object obj, TransitionListener transitionListener, Collection<UpdateInfo> collection, UpdateInfo updateInfo);
    }

    static class BeginNotifier implements INotifier {
        BeginNotifier() {
        }

        @Override // miuix.animation.listener.ListenerNotifier.INotifier
        public void doNotify(Object obj, TransitionListener transitionListener, Collection<UpdateInfo> collection, UpdateInfo updateInfo) {
            transitionListener.onBegin(obj);
            if (collection != null) {
                transitionListener.onBegin(obj, collection);
            }
        }
    }

    static class PropertyBeginNotifier implements INotifier {
        PropertyBeginNotifier() {
        }

        @Override // miuix.animation.listener.ListenerNotifier.INotifier
        public void doNotify(Object obj, TransitionListener transitionListener, Collection<UpdateInfo> collection, UpdateInfo updateInfo) {
            transitionListener.onBegin(obj, collection);
        }
    }

    static class MassUpdateNotifier implements INotifier {
        static final List<UpdateInfo> sEmptyList = new ArrayList();

        MassUpdateNotifier() {
        }

        @Override // miuix.animation.listener.ListenerNotifier.INotifier
        public void doNotify(Object obj, TransitionListener transitionListener, Collection<UpdateInfo> collection, UpdateInfo updateInfo) {
            transitionListener.onUpdate(obj, sEmptyList);
        }
    }

    static class UpdateNotifier implements INotifier {
        UpdateNotifier() {
        }

        @Override // miuix.animation.listener.ListenerNotifier.INotifier
        public void doNotify(Object obj, TransitionListener transitionListener, Collection<UpdateInfo> collection, UpdateInfo updateInfo) {
            transitionListener.onUpdate(obj, collection);
        }
    }

    static class CancelNotifier implements INotifier {
        CancelNotifier() {
        }

        @Override // miuix.animation.listener.ListenerNotifier.INotifier
        public void doNotify(Object obj, TransitionListener transitionListener, Collection<UpdateInfo> collection, UpdateInfo updateInfo) {
            transitionListener.onCancel(obj);
        }
    }

    static class EndNotifier implements INotifier {
        EndNotifier() {
        }

        @Override // miuix.animation.listener.ListenerNotifier.INotifier
        public void doNotify(Object obj, TransitionListener transitionListener, Collection<UpdateInfo> collection, UpdateInfo updateInfo) {
            transitionListener.onComplete(obj);
        }
    }

    public ListenerNotifier(IAnimTarget iAnimTarget) {
        this.mTarget = iAnimTarget;
    }

    public boolean addListeners(Object obj, AnimConfig animConfig) {
        if (animConfig.listeners.isEmpty()) {
            return false;
        }
        CommonUtils.addTo(animConfig.listeners, getListenerSet(obj));
        return true;
    }

    public void removeListeners(Object obj) {
        ObjectPool.release(FolmeFactory.getEngine().getObjPool(), this.mListenerMap.remove(obj));
    }

    public void removeListeners() {
        ObjectPool.release(FolmeFactory.getEngine().getObjPool(), this.mListenerMap.values());
        this.mListenerMap.clear();
    }

    private List<TransitionListener> getListenerSet(Object obj) {
        List<TransitionListener> list = this.mListenerMap.get(obj);
        if (list != null) {
            return list;
        }
        List<TransitionListener> list2 = (List) ObjectPool.acquire(FolmeFactory.getEngine().getObjPool(), ArrayList.class, new Object[0]);
        this.mListenerMap.put(obj, list2);
        return list2;
    }

    public void notifyBegin(Object obj, Object obj2, Collection<UpdateInfo> collection, Set<TransitionListener> set) {
        notify(obj, obj2, sBegin, collection, null, set);
    }

    public void notifyUpdate(Object obj, Object obj2, Collection<UpdateInfo> collection, Set<TransitionListener> set) {
        notify(obj, obj2, sUpdate, collection, null, set);
    }

    public void notifyCancelAll(Object obj, Object obj2, Set<TransitionListener> set) {
        notify(obj, obj2, sCancelAll, null, null, set);
    }

    public void notifyEndAll(Object obj, Object obj2, Set<TransitionListener> set) {
        notify(obj, obj2, sEndAll, null, null, set);
    }

    private void notify(Object obj, Object obj2, INotifier iNotifier, Collection<UpdateInfo> collection, UpdateInfo updateInfo, Set<TransitionListener> set) {
        List<TransitionListener> list = this.mListenerMap.get(obj);
        if (list == null || list.isEmpty()) {
            return;
        }
        notifyListenerSet(obj2, list, iNotifier, collection, updateInfo, set);
    }

    private static void notifyListenerSet(Object obj, List<TransitionListener> list, INotifier iNotifier, Collection<UpdateInfo> collection, UpdateInfo updateInfo, Set<TransitionListener> set) {
        set.addAll(list);
        for (TransitionListener transitionListener : set) {
            if (transitionListener == null) {
                Log.e(CommonUtils.TAG, "listener null tag=" + obj);
            } else {
                iNotifier.doNotify(obj, transitionListener, collection, updateInfo);
            }
        }
        set.clear();
    }
}
