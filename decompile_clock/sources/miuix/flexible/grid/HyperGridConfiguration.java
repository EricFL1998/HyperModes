package miuix.flexible.grid;

/* JADX INFO: loaded from: classes2.dex */
public class HyperGridConfiguration {
    private static final int MAX_POOL_SIZE = 10;
    private static HyperGridConfiguration sPool;
    private static int sPoolSize;
    private static final Object sPoolSync = new Object();
    public float cellWidth;
    public int columnCount;
    public float columnSpacing;
    private HyperGridConfiguration next;

    public static HyperGridConfiguration obtain() {
        synchronized (sPoolSync) {
            HyperGridConfiguration hyperGridConfiguration = sPool;
            if (hyperGridConfiguration != null) {
                sPool = hyperGridConfiguration.next;
                hyperGridConfiguration.next = null;
                sPoolSize--;
                return hyperGridConfiguration;
            }
            return new HyperGridConfiguration();
        }
    }

    public void recycle() {
        synchronized (sPoolSync) {
            int i = sPoolSize;
            if (i < 10) {
                this.next = sPool;
                sPool = this;
                sPoolSize = i + 1;
            }
        }
    }
}
