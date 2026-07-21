package miuix.module;

/* JADX INFO: loaded from: classes2.dex */
public class Dependency {
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_INSTALLED = 2;
    public static final int TYPE_NONE = 0;
    public static final int TYPE_OPTIONAL = 1;
    private Level level;
    private String name;
    private int type;

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public static class Level {
        private int maxLevel;
        private int minLevel;
        private int targetLevel;

        public int getMinLevel() {
            return this.minLevel;
        }

        public void setMinLevel(int i) {
            this.minLevel = i;
        }

        public int getTargetLevel() {
            return this.targetLevel;
        }

        public void setTargetLevel(int i) {
            this.targetLevel = i;
        }

        public int getMaxLevel() {
            return this.maxLevel;
        }

        public void setMaxLevel(int i) {
            this.maxLevel = i;
        }
    }
}
