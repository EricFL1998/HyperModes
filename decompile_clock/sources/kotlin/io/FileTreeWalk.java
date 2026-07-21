package kotlin.io;

import com.xiaomi.onetrack.api.as;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.AbstractIterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;

/* JADX INFO: compiled from: FileTreeWalk.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010(\n\u0002\b\u0006\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0003\u001b\u001c\u001dB\u008b\u0001\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\u0014\u0010\u0006\u001a\u0010\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007\u0012\u0014\u0010\t\u001a\u0010\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\n\u0018\u00010\u0007\u00128\u0010\u000b\u001a4\u0012\u0013\u0012\u00110\u0002¢\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u000f\u0012\u0013\u0012\u00110\u0010¢\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u0011\u0012\u0004\u0012\u00020\n\u0018\u00010\f\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0013¢\u0006\u0004\b\u0014\u0010\u0015B\u001b\b\u0010\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0014\u0010\u0016J\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00020\u0018H\u0096\u0002J\u001a\u0010\u0006\u001a\u00020\u00002\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\b0\u0007J\u001a\u0010\t\u001a\u00020\u00002\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\n0\u0007J \u0010\u000b\u001a\u00020\u00002\u0018\u0010\u0019\u001a\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\n0\fJ\u000e\u0010\u0012\u001a\u00020\u00002\u0006\u0010\u001a\u001a\u00020\u0013R\u000e\u0010\u0003\u001a\u00020\u0002X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u0006\u001a\u0010\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\t\u001a\u0010\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\n\u0018\u00010\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R@\u0010\u000b\u001a4\u0012\u0013\u0012\u00110\u0002¢\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u000f\u0012\u0013\u0012\u00110\u0010¢\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u0011\u0012\u0004\u0012\u00020\n\u0018\u00010\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"Lkotlin/io/FileTreeWalk;", "Lkotlin/sequences/Sequence;", "Ljava/io/File;", "start", "direction", "Lkotlin/io/FileWalkDirection;", "onEnter", "Lkotlin/Function1;", "", "onLeave", "", "onFail", "Lkotlin/Function2;", "Lkotlin/ParameterName;", as.a, "f", "Ljava/io/IOException;", "e", "maxDepth", "", "<init>", "(Ljava/io/File;Lkotlin/io/FileWalkDirection;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function2;I)V", "(Ljava/io/File;Lkotlin/io/FileWalkDirection;)V", "iterator", "", "function", "depth", "WalkState", "DirectoryState", "FileTreeWalkIterator", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public final class FileTreeWalk implements Sequence<File> {
    private final FileWalkDirection direction;
    private final int maxDepth;
    private final Function1<File, Boolean> onEnter;
    private final Function2<File, IOException, Unit> onFail;
    private final Function1<File, Unit> onLeave;
    private final File start;

    /* JADX WARN: Multi-variable type inference failed */
    private FileTreeWalk(File file, FileWalkDirection fileWalkDirection, Function1<? super File, Boolean> function1, Function1<? super File, Unit> function2, Function2<? super File, ? super IOException, Unit> function3, int i) {
        this.start = file;
        this.direction = fileWalkDirection;
        this.onEnter = function1;
        this.onLeave = function2;
        this.onFail = function3;
        this.maxDepth = i;
    }

    /* synthetic */ FileTreeWalk(File file, FileWalkDirection fileWalkDirection, Function1 function1, Function1 function2, Function2 function3, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(file, (i2 & 2) != 0 ? FileWalkDirection.TOP_DOWN : fileWalkDirection, function1, function2, function3, (i2 & 32) != 0 ? Integer.MAX_VALUE : i);
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public FileTreeWalk(File start, FileWalkDirection direction) {
        this(start, direction, null, null, null, 0, 32, null);
        Intrinsics.checkNotNullParameter(start, "start");
        Intrinsics.checkNotNullParameter(direction, "direction");
    }

    public /* synthetic */ FileTreeWalk(File file, FileWalkDirection fileWalkDirection, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(file, (i & 2) != 0 ? FileWalkDirection.TOP_DOWN : fileWalkDirection);
    }

    @Override // kotlin.sequences.Sequence
    public Iterator<File> iterator() {
        return new FileTreeWalkIterator();
    }

    /* JADX INFO: compiled from: FileTreeWalk.kt */
    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\"\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\n\u0010\b\u001a\u0004\u0018\u00010\u0003H&R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\t"}, d2 = {"Lkotlin/io/FileTreeWalk$WalkState;", "", "root", "Ljava/io/File;", "<init>", "(Ljava/io/File;)V", "getRoot", "()Ljava/io/File;", "step", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    private static abstract class WalkState {
        private final File root;

        public abstract File step();

        public WalkState(File root) {
            Intrinsics.checkNotNullParameter(root, "root");
            this.root = root;
        }

        public final File getRoot() {
            return this.root;
        }
    }

    /* JADX INFO: compiled from: FileTreeWalk.kt */
    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\"\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005¨\u0006\u0006"}, d2 = {"Lkotlin/io/FileTreeWalk$DirectoryState;", "Lkotlin/io/FileTreeWalk$WalkState;", "rootDir", "Ljava/io/File;", "<init>", "(Ljava/io/File;)V", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    private static abstract class DirectoryState extends WalkState {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public DirectoryState(File rootDir) {
            super(rootDir);
            Intrinsics.checkNotNullParameter(rootDir, "rootDir");
        }
    }

    /* JADX INFO: compiled from: FileTreeWalk.kt */
    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0082\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0003\u000e\u000f\u0010B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\b\u0010\b\u001a\u00020\tH\u0014J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0002H\u0002J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0002H\u0082\u0010R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"}, d2 = {"Lkotlin/io/FileTreeWalk$FileTreeWalkIterator;", "Lkotlin/collections/AbstractIterator;", "Ljava/io/File;", "<init>", "(Lkotlin/io/FileTreeWalk;)V", "state", "Ljava/util/ArrayDeque;", "Lkotlin/io/FileTreeWalk$WalkState;", "computeNext", "", "directoryState", "Lkotlin/io/FileTreeWalk$DirectoryState;", "root", "gotoNext", "BottomUpDirectoryState", "TopDownDirectoryState", "SingleFileState", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    private final class FileTreeWalkIterator extends AbstractIterator<File> {
        private final ArrayDeque<WalkState> state;

        /* JADX INFO: compiled from: FileTreeWalk.kt */
        @Metadata(k = 3, mv = {2, 1, 0}, xi = 48)
        public /* synthetic */ class WhenMappings {
            public static final /* synthetic */ int[] $EnumSwitchMapping$0;

            static {
                int[] iArr = new int[FileWalkDirection.values().length];
                try {
                    iArr[FileWalkDirection.TOP_DOWN.ordinal()] = 1;
                } catch (NoSuchFieldError unused) {
                }
                try {
                    iArr[FileWalkDirection.BOTTOM_UP.ordinal()] = 2;
                } catch (NoSuchFieldError unused2) {
                }
                $EnumSwitchMapping$0 = iArr;
            }
        }

        public FileTreeWalkIterator() {
            ArrayDeque<WalkState> arrayDeque = new ArrayDeque<>();
            this.state = arrayDeque;
            if (FileTreeWalk.this.start.isDirectory()) {
                arrayDeque.push(directoryState(FileTreeWalk.this.start));
            } else if (FileTreeWalk.this.start.isFile()) {
                arrayDeque.push(new SingleFileState(this, FileTreeWalk.this.start));
            } else {
                done();
            }
        }

        @Override // kotlin.collections.AbstractIterator
        protected void computeNext() {
            File fileGotoNext = gotoNext();
            if (fileGotoNext != null) {
                setNext(fileGotoNext);
            } else {
                done();
            }
        }

        private final DirectoryState directoryState(File root) {
            int i = WhenMappings.$EnumSwitchMapping$0[FileTreeWalk.this.direction.ordinal()];
            if (i == 1) {
                return new TopDownDirectoryState(this, root);
            }
            if (i != 2) {
                throw new NoWhenBranchMatchedException();
            }
            return new BottomUpDirectoryState(this, root);
        }

        private final File gotoNext() {
            while (true) {
                WalkState walkStatePeek = this.state.peek();
                if (walkStatePeek == null) {
                    return null;
                }
                File fileStep = walkStatePeek.step();
                if (fileStep == null) {
                    this.state.pop();
                } else {
                    if (Intrinsics.areEqual(fileStep, walkStatePeek.getRoot()) || !fileStep.isDirectory() || this.state.size() >= FileTreeWalk.this.maxDepth) {
                        return fileStep;
                    }
                    this.state.push(directoryState(fileStep));
                }
            }
        }

        /* JADX INFO: compiled from: FileTreeWalk.kt */
        @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0082\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\n\u0010\u000e\u001a\u0004\u0018\u00010\u0003H\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0018\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\tX\u0082\u000e¢\u0006\u0004\n\u0002\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Lkotlin/io/FileTreeWalk$FileTreeWalkIterator$BottomUpDirectoryState;", "Lkotlin/io/FileTreeWalk$DirectoryState;", "rootDir", "Ljava/io/File;", "<init>", "(Lkotlin/io/FileTreeWalk$FileTreeWalkIterator;Ljava/io/File;)V", "rootVisited", "", "fileList", "", "[Ljava/io/File;", "fileIndex", "", "failed", "step", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
        private final class BottomUpDirectoryState extends DirectoryState {
            private boolean failed;
            private int fileIndex;
            private File[] fileList;
            private boolean rootVisited;
            final /* synthetic */ FileTreeWalkIterator this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public BottomUpDirectoryState(FileTreeWalkIterator fileTreeWalkIterator, File rootDir) {
                super(rootDir);
                Intrinsics.checkNotNullParameter(rootDir, "rootDir");
                this.this$0 = fileTreeWalkIterator;
            }

            @Override // kotlin.io.FileTreeWalk.WalkState
            public File step() {
                if (!this.failed && this.fileList == null) {
                    Function1 function1 = FileTreeWalk.this.onEnter;
                    if (function1 != null && !((Boolean) function1.invoke(getRoot())).booleanValue()) {
                        return null;
                    }
                    File[] fileArrListFiles = getRoot().listFiles();
                    this.fileList = fileArrListFiles;
                    if (fileArrListFiles == null) {
                        Function2 function2 = FileTreeWalk.this.onFail;
                        if (function2 != null) {
                            function2.invoke(getRoot(), new AccessDeniedException(getRoot(), null, "Cannot list files in a directory", 2, null));
                        }
                        this.failed = true;
                    }
                }
                File[] fileArr = this.fileList;
                if (fileArr != null) {
                    int i = this.fileIndex;
                    Intrinsics.checkNotNull(fileArr);
                    if (i < fileArr.length) {
                        File[] fileArr2 = this.fileList;
                        Intrinsics.checkNotNull(fileArr2);
                        int i2 = this.fileIndex;
                        this.fileIndex = i2 + 1;
                        return fileArr2[i2];
                    }
                }
                if (this.rootVisited) {
                    Function1 function3 = FileTreeWalk.this.onLeave;
                    if (function3 != null) {
                        function3.invoke(getRoot());
                    }
                    return null;
                }
                this.rootVisited = true;
                return getRoot();
            }
        }

        /* JADX INFO: compiled from: FileTreeWalk.kt */
        @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\n\u0010\r\u001a\u0004\u0018\u00010\u0003H\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0018\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\tX\u0082\u000e¢\u0006\u0004\n\u0002\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lkotlin/io/FileTreeWalk$FileTreeWalkIterator$TopDownDirectoryState;", "Lkotlin/io/FileTreeWalk$DirectoryState;", "rootDir", "Ljava/io/File;", "<init>", "(Lkotlin/io/FileTreeWalk$FileTreeWalkIterator;Ljava/io/File;)V", "rootVisited", "", "fileList", "", "[Ljava/io/File;", "fileIndex", "", "step", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
        private final class TopDownDirectoryState extends DirectoryState {
            private int fileIndex;
            private File[] fileList;
            private boolean rootVisited;
            final /* synthetic */ FileTreeWalkIterator this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public TopDownDirectoryState(FileTreeWalkIterator fileTreeWalkIterator, File rootDir) {
                super(rootDir);
                Intrinsics.checkNotNullParameter(rootDir, "rootDir");
                this.this$0 = fileTreeWalkIterator;
            }

            /* JADX WARN: Code restructure failed: missing block: B:30:0x007f, code lost:
            
                if (r0.length == 0) goto L31;
             */
            @Override // kotlin.io.FileTreeWalk.WalkState
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public java.io.File step() {
                /*
                    r10 = this;
                    boolean r0 = r10.rootVisited
                    r1 = 0
                    if (r0 != 0) goto L28
                    kotlin.io.FileTreeWalk$FileTreeWalkIterator r0 = r10.this$0
                    kotlin.io.FileTreeWalk r0 = kotlin.io.FileTreeWalk.this
                    kotlin.jvm.functions.Function1 r0 = kotlin.io.FileTreeWalk.access$getOnEnter$p(r0)
                    if (r0 == 0) goto L20
                    java.io.File r2 = r10.getRoot()
                    java.lang.Object r0 = r0.invoke(r2)
                    java.lang.Boolean r0 = (java.lang.Boolean) r0
                    boolean r0 = r0.booleanValue()
                    if (r0 != 0) goto L20
                    return r1
                L20:
                    r0 = 1
                    r10.rootVisited = r0
                    java.io.File r0 = r10.getRoot()
                    return r0
                L28:
                    java.io.File[] r0 = r10.fileList
                    if (r0 == 0) goto L47
                    int r2 = r10.fileIndex
                    kotlin.jvm.internal.Intrinsics.checkNotNull(r0)
                    int r0 = r0.length
                    if (r2 >= r0) goto L35
                    goto L47
                L35:
                    kotlin.io.FileTreeWalk$FileTreeWalkIterator r0 = r10.this$0
                    kotlin.io.FileTreeWalk r0 = kotlin.io.FileTreeWalk.this
                    kotlin.jvm.functions.Function1 r0 = kotlin.io.FileTreeWalk.access$getOnLeave$p(r0)
                    if (r0 == 0) goto L46
                    java.io.File r2 = r10.getRoot()
                    r0.invoke(r2)
                L46:
                    return r1
                L47:
                    java.io.File[] r0 = r10.fileList
                    if (r0 != 0) goto L93
                    java.io.File r0 = r10.getRoot()
                    java.io.File[] r0 = r0.listFiles()
                    r10.fileList = r0
                    if (r0 != 0) goto L77
                    kotlin.io.FileTreeWalk$FileTreeWalkIterator r0 = r10.this$0
                    kotlin.io.FileTreeWalk r0 = kotlin.io.FileTreeWalk.this
                    kotlin.jvm.functions.Function2 r0 = kotlin.io.FileTreeWalk.access$getOnFail$p(r0)
                    if (r0 == 0) goto L77
                    java.io.File r2 = r10.getRoot()
                    kotlin.io.AccessDeniedException r9 = new kotlin.io.AccessDeniedException
                    java.io.File r4 = r10.getRoot()
                    r7 = 2
                    r8 = 0
                    r5 = 0
                    java.lang.String r6 = "Cannot list files in a directory"
                    r3 = r9
                    r3.<init>(r4, r5, r6, r7, r8)
                    r0.invoke(r2, r9)
                L77:
                    java.io.File[] r0 = r10.fileList
                    if (r0 == 0) goto L81
                    kotlin.jvm.internal.Intrinsics.checkNotNull(r0)
                    int r0 = r0.length
                    if (r0 != 0) goto L93
                L81:
                    kotlin.io.FileTreeWalk$FileTreeWalkIterator r0 = r10.this$0
                    kotlin.io.FileTreeWalk r0 = kotlin.io.FileTreeWalk.this
                    kotlin.jvm.functions.Function1 r0 = kotlin.io.FileTreeWalk.access$getOnLeave$p(r0)
                    if (r0 == 0) goto L92
                    java.io.File r2 = r10.getRoot()
                    r0.invoke(r2)
                L92:
                    return r1
                L93:
                    java.io.File[] r0 = r10.fileList
                    kotlin.jvm.internal.Intrinsics.checkNotNull(r0)
                    int r1 = r10.fileIndex
                    int r2 = r1 + 1
                    r10.fileIndex = r2
                    r0 = r0[r1]
                    return r0
                */
                throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FileTreeWalk.FileTreeWalkIterator.TopDownDirectoryState.step():java.io.File");
            }
        }

        /* JADX INFO: compiled from: FileTreeWalk.kt */
        @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\n\u0010\b\u001a\u0004\u0018\u00010\u0003H\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\t"}, d2 = {"Lkotlin/io/FileTreeWalk$FileTreeWalkIterator$SingleFileState;", "Lkotlin/io/FileTreeWalk$WalkState;", "rootFile", "Ljava/io/File;", "<init>", "(Lkotlin/io/FileTreeWalk$FileTreeWalkIterator;Ljava/io/File;)V", "visited", "", "step", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
        private final class SingleFileState extends WalkState {
            final /* synthetic */ FileTreeWalkIterator this$0;
            private boolean visited;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public SingleFileState(FileTreeWalkIterator fileTreeWalkIterator, File rootFile) {
                super(rootFile);
                Intrinsics.checkNotNullParameter(rootFile, "rootFile");
                this.this$0 = fileTreeWalkIterator;
            }

            @Override // kotlin.io.FileTreeWalk.WalkState
            public File step() {
                if (this.visited) {
                    return null;
                }
                this.visited = true;
                return getRoot();
            }
        }
    }

    public final FileTreeWalk onEnter(Function1<? super File, Boolean> function) {
        Intrinsics.checkNotNullParameter(function, "function");
        return new FileTreeWalk(this.start, this.direction, function, this.onLeave, this.onFail, this.maxDepth);
    }

    public final FileTreeWalk onLeave(Function1<? super File, Unit> function) {
        Intrinsics.checkNotNullParameter(function, "function");
        return new FileTreeWalk(this.start, this.direction, this.onEnter, function, this.onFail, this.maxDepth);
    }

    public final FileTreeWalk onFail(Function2<? super File, ? super IOException, Unit> function) {
        Intrinsics.checkNotNullParameter(function, "function");
        return new FileTreeWalk(this.start, this.direction, this.onEnter, this.onLeave, function, this.maxDepth);
    }

    public final FileTreeWalk maxDepth(int depth) {
        if (depth <= 0) {
            throw new IllegalArgumentException("depth must be positive, but was " + depth + '.');
        }
        return new FileTreeWalk(this.start, this.direction, this.onEnter, this.onLeave, this.onFail, depth);
    }
}
