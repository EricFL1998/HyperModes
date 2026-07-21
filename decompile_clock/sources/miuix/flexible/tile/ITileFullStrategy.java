package miuix.flexible.tile;

/* JADX INFO: loaded from: classes2.dex */
public interface ITileFullStrategy {
    boolean afterUpdateTileCache(int i, int i2);

    void beforeUpdateTileCache(int i, int i2);

    int[] getTileSize(int i);

    boolean isResized(int i);
}
