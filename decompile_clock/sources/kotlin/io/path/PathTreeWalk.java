package kotlin.io.path;

import com.android.deskclock.R2;
import java.nio.file.FileSystemLoopException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequenceScope;
import kotlin.sequences.SequencesKt;

/* JADX INFO: compiled from: PathTreeWalk.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010(\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0004\b\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u000e\u0010\u0004\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00060\u0005¢\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00020\u0016H\u0096\u0002JB\u0010\u0017\u001a\u00020\u0018*\b\u0012\u0004\u0012\u00020\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0018\u0010\u001e\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0 \u0012\u0004\u0012\u00020\u00180\u001fH\u0082H¢\u0006\u0002\u0010!J\u000e\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00020\u0016H\u0002J\u000e\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00020\u0016H\u0002R\u000e\u0010\u0003\u001a\u00020\u0002X\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\u0004\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\tR\u0014\u0010\n\u001a\u00020\u000b8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u001a\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00058BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0012\u001a\u00020\u000b8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\rR\u0014\u0010\u0014\u001a\u00020\u000b8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0014\u0010\r¨\u0006$"}, d2 = {"Lkotlin/io/path/PathTreeWalk;", "Lkotlin/sequences/Sequence;", "Ljava/nio/file/Path;", "start", "options", "", "Lkotlin/io/path/PathWalkOption;", "<init>", "(Ljava/nio/file/Path;[Lkotlin/io/path/PathWalkOption;)V", "[Lkotlin/io/path/PathWalkOption;", "followLinks", "", "getFollowLinks", "()Z", "linkOptions", "Ljava/nio/file/LinkOption;", "getLinkOptions", "()[Ljava/nio/file/LinkOption;", "includeDirectories", "getIncludeDirectories", "isBFS", "iterator", "", "yieldIfNeeded", "", "Lkotlin/sequences/SequenceScope;", "node", "Lkotlin/io/path/PathNode;", "entriesReader", "Lkotlin/io/path/DirectoryEntriesReader;", "entriesAction", "Lkotlin/Function1;", "", "(Lkotlin/sequences/SequenceScope;Lkotlin/io/path/PathNode;Lkotlin/io/path/DirectoryEntriesReader;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "dfsIterator", "bfsIterator", "kotlin-stdlib-jdk7"}, k = 1, mv = {2, 1, 0}, xi = 48)
public final class PathTreeWalk implements Sequence<Path> {
    private final PathWalkOption[] options;
    private final Path start;

    public PathTreeWalk(Path start, PathWalkOption[] options) {
        Intrinsics.checkNotNullParameter(start, "start");
        Intrinsics.checkNotNullParameter(options, "options");
        this.start = start;
        this.options = options;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean getFollowLinks() {
        return ArraysKt.contains(this.options, PathWalkOption.FOLLOW_LINKS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final LinkOption[] getLinkOptions() {
        return LinkFollowing.INSTANCE.toLinkOptions(getFollowLinks());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean getIncludeDirectories() {
        return ArraysKt.contains(this.options, PathWalkOption.INCLUDE_DIRECTORIES);
    }

    private final boolean isBFS() {
        return ArraysKt.contains(this.options, PathWalkOption.BREADTH_FIRST);
    }

    @Override // kotlin.sequences.Sequence
    public Iterator<Path> iterator() {
        return isBFS() ? bfsIterator() : dfsIterator();
    }

    private final Object yieldIfNeeded(SequenceScope<? super Path> sequenceScope, PathNode pathNode, DirectoryEntriesReader directoryEntriesReader, Function1<? super List<PathNode>, Unit> function1, Continuation<? super Unit> continuation) throws FileSystemLoopException {
        Path path = pathNode.getPath();
        if (pathNode.getParent() != null) {
            PathsKt.checkFileName(path);
        }
        LinkOption[] linkOptions = getLinkOptions();
        LinkOption[] linkOptionArr = (LinkOption[]) Arrays.copyOf(linkOptions, linkOptions.length);
        if (Files.isDirectory(path, (LinkOption[]) Arrays.copyOf(linkOptionArr, linkOptionArr.length))) {
            if (!PathTreeWalkKt.createsCycle(pathNode)) {
                if (getIncludeDirectories()) {
                    InlineMarker.mark(0);
                    sequenceScope.yield(path, continuation);
                    InlineMarker.mark(1);
                }
                LinkOption[] linkOptions2 = getLinkOptions();
                LinkOption[] linkOptionArr2 = (LinkOption[]) Arrays.copyOf(linkOptions2, linkOptions2.length);
                if (Files.isDirectory(path, (LinkOption[]) Arrays.copyOf(linkOptionArr2, linkOptionArr2.length))) {
                    function1.invoke(directoryEntriesReader.readEntries(pathNode));
                }
            } else {
                throw new FileSystemLoopException(path.toString());
            }
        } else if (Files.exists(path, (LinkOption[]) Arrays.copyOf(new LinkOption[]{LinkOption.NOFOLLOW_LINKS}, 1))) {
            InlineMarker.mark(0);
            sequenceScope.yield(path, continuation);
            InlineMarker.mark(1);
            return Unit.INSTANCE;
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: renamed from: kotlin.io.path.PathTreeWalk$dfsIterator$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: PathTreeWalk.kt */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H\n"}, d2 = {"<anonymous>", "", "Lkotlin/sequences/SequenceScope;", "Ljava/nio/file/Path;"}, k = 3, mv = {2, 1, 0}, xi = 48)
    @DebugMetadata(c = "kotlin.io.path.PathTreeWalk$dfsIterator$1", f = "PathTreeWalk.kt", i = {0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3}, l = {R2.attr.actionBarIndeterminateProgressStyle, R2.attr.actionBarNewIcon, R2.attr.actionBarSecondaryTabBarStyle, R2.attr.actionBarSplitBackground}, m = "invokeSuspend", n = {"$this$iterator", "stack", "entriesReader", "startNode", "this_$iv", "path$iv", "$this$iterator", "stack", "entriesReader", "$this$iterator", "stack", "entriesReader", "pathNode", "this_$iv", "path$iv", "$this$iterator", "stack", "entriesReader"}, s = {"L$0", "L$1", "L$2", "L$3", "L$4", "L$5", "L$0", "L$1", "L$2", "L$0", "L$1", "L$2", "L$3", "L$4", "L$5", "L$0", "L$1", "L$2"})
    static final class C00711 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object> {
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        Object L$5;
        int label;

        C00711(Continuation<? super C00711> continuation) {
            super(2, continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00711 c00711 = PathTreeWalk.this.new C00711(continuation);
            c00711.L$0 = obj;
            return c00711;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(SequenceScope<? super Path> sequenceScope, Continuation<? super Unit> continuation) {
            return ((C00711) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:41:0x0155  */
        /* JADX WARN: Code duplicated, block: B:45:0x017a  */
        /* JADX WARN: Code duplicated, block: B:50:0x019b  */
        /* JADX WARN: Code duplicated, block: B:52:0x01a1  */
        /* JADX WARN: Code duplicated, block: B:54:0x01b8 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:55:0x01b9  */
        /* JADX WARN: Code duplicated, block: B:57:0x01d1 A[PHI: r2 r6 r9 r10 r11 r12
  0x01d1: PHI (r2v13 kotlin.io.path.DirectoryEntriesReader) = (r2v10 kotlin.io.path.DirectoryEntriesReader), (r2v15 kotlin.io.path.DirectoryEntriesReader) binds: [B:51:0x019f, B:56:0x01c5] A[DONT_GENERATE, DONT_INLINE]
  0x01d1: PHI (r6v20 kotlin.collections.ArrayDeque) = (r6v18 kotlin.collections.ArrayDeque), (r6v22 kotlin.collections.ArrayDeque) binds: [B:51:0x019f, B:56:0x01c5] A[DONT_GENERATE, DONT_INLINE]
  0x01d1: PHI (r9v9 kotlin.sequences.SequenceScope) = (r9v7 kotlin.sequences.SequenceScope), (r9v11 kotlin.sequences.SequenceScope) binds: [B:51:0x019f, B:56:0x01c5] A[DONT_GENERATE, DONT_INLINE]
  0x01d1: PHI (r10v21 kotlin.io.path.PathNode) = (r10v12 kotlin.io.path.PathNode), (r10v23 kotlin.io.path.PathNode) binds: [B:51:0x019f, B:56:0x01c5] A[DONT_GENERATE, DONT_INLINE]
  0x01d1: PHI (r11v17 kotlin.io.path.PathTreeWalk) = (r11v14 kotlin.io.path.PathTreeWalk), (r11v27 kotlin.io.path.PathTreeWalk) binds: [B:51:0x019f, B:56:0x01c5] A[DONT_GENERATE, DONT_INLINE]
  0x01d1: PHI (r12v12 java.nio.file.Path) = (r12v10 java.nio.file.Path), (r12v14 java.nio.file.Path) binds: [B:51:0x019f, B:56:0x01c5] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARN: Code duplicated, block: B:59:0x01e9  */
        /* JADX WARN: Code duplicated, block: B:60:0x01f9  */
        /* JADX WARN: Code duplicated, block: B:73:0x0195 A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:75:0x0168 A[SYNTHETIC] */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:58:0x01e7 -> B:39:0x014b). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:59:0x01e9 -> B:39:0x014b). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r19) {
            /*
                Method dump skipped, instruction units count: 565
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlin.io.path.PathTreeWalk.C00711.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    private final Iterator<Path> dfsIterator() {
        return SequencesKt.iterator(new C00711(null));
    }

    /* JADX INFO: renamed from: kotlin.io.path.PathTreeWalk$bfsIterator$1, reason: invalid class name */
    /* JADX INFO: compiled from: PathTreeWalk.kt */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H\n"}, d2 = {"<anonymous>", "", "Lkotlin/sequences/SequenceScope;", "Ljava/nio/file/Path;"}, k = 3, mv = {2, 1, 0}, xi = 48)
    @DebugMetadata(c = "kotlin.io.path.PathTreeWalk$bfsIterator$1", f = "PathTreeWalk.kt", i = {0, 0, 0, 0, 0, 0, 1, 1, 1}, l = {R2.attr.actionBarIndeterminateProgressStyle, R2.attr.actionBarNewIcon}, m = "invokeSuspend", n = {"$this$iterator", "queue", "entriesReader", "pathNode", "this_$iv", "path$iv", "$this$iterator", "queue", "entriesReader"}, s = {"L$0", "L$1", "L$2", "L$3", "L$4", "L$5", "L$0", "L$1", "L$2"})
    static final class AnonymousClass1 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object> {
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        Object L$5;
        int label;

        AnonymousClass1(Continuation<? super AnonymousClass1> continuation) {
            super(2, continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            AnonymousClass1 anonymousClass1 = PathTreeWalk.this.new AnonymousClass1(continuation);
            anonymousClass1.L$0 = obj;
            return anonymousClass1;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(SequenceScope<? super Path> sequenceScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass1) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:13:0x0087  */
        /* JADX WARN: Code duplicated, block: B:15:0x0099  */
        /* JADX WARN: Code duplicated, block: B:20:0x00ba  */
        /* JADX WARN: Code duplicated, block: B:22:0x00c0  */
        /* JADX WARN: Code duplicated, block: B:24:0x00d7 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:25:0x00d8  */
        /* JADX WARN: Code duplicated, block: B:27:0x00e6 A[PHI: r1 r5 r6 r7 r8 r13
  0x00e6: PHI (r1v3 kotlin.io.path.DirectoryEntriesReader) = (r1v5 kotlin.io.path.DirectoryEntriesReader), (r1v6 kotlin.io.path.DirectoryEntriesReader) binds: [B:26:0x00df, B:21:0x00be] A[DONT_GENERATE, DONT_INLINE]
  0x00e6: PHI (r5v2 kotlin.collections.ArrayDeque) = (r5v4 kotlin.collections.ArrayDeque), (r5v5 kotlin.collections.ArrayDeque) binds: [B:26:0x00df, B:21:0x00be] A[DONT_GENERATE, DONT_INLINE]
  0x00e6: PHI (r6v4 kotlin.sequences.SequenceScope) = (r6v6 kotlin.sequences.SequenceScope), (r6v7 kotlin.sequences.SequenceScope) binds: [B:26:0x00df, B:21:0x00be] A[DONT_GENERATE, DONT_INLINE]
  0x00e6: PHI (r7v2 kotlin.io.path.PathTreeWalk) = (r7v10 kotlin.io.path.PathTreeWalk), (r7v11 kotlin.io.path.PathTreeWalk) binds: [B:26:0x00df, B:21:0x00be] A[DONT_GENERATE, DONT_INLINE]
  0x00e6: PHI (r8v3 java.nio.file.Path) = (r8v5 java.nio.file.Path), (r8v6 java.nio.file.Path) binds: [B:26:0x00df, B:21:0x00be] A[DONT_GENERATE, DONT_INLINE]
  0x00e6: PHI (r13v3 kotlin.io.path.PathNode) = (r13v6 kotlin.io.path.PathNode), (r13v13 kotlin.io.path.PathNode) binds: [B:26:0x00df, B:21:0x00be] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARN: Code duplicated, block: B:29:0x00fe  */
        /* JADX WARN: Code duplicated, block: B:30:0x0109  */
        /* JADX WARN: Code duplicated, block: B:32:0x0113  */
        /* JADX WARN: Code duplicated, block: B:39:0x0126 A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:40:0x00b4 A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:42:0x013d A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:44:0x007d A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:45:0x007d A[SYNTHETIC] */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:28:0x00fc -> B:11:0x007d). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:29:0x00fe -> B:11:0x007d). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached at block B:32:0x0113
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r13) {
            /*
                Method dump skipped, instruction units count: 321
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlin.io.path.PathTreeWalk.AnonymousClass1.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    private final Iterator<Path> bfsIterator() {
        return SequencesKt.iterator(new AnonymousClass1(null));
    }
}
