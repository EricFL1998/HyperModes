package kotlin.reflect.jvm.internal.impl.renderer;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: DescriptorRenderer.kt */
/* JADX INFO: loaded from: classes2.dex */
public enum RenderingFormat {
    PLAIN { // from class: kotlin.reflect.jvm.internal.impl.renderer.RenderingFormat.PLAIN
        @Override // kotlin.reflect.jvm.internal.impl.renderer.RenderingFormat
        public String escape(String string) {
            Intrinsics.checkNotNullParameter(string, "string");
            return string;
        }
    },
    HTML { // from class: kotlin.reflect.jvm.internal.impl.renderer.RenderingFormat.HTML
        @Override // kotlin.reflect.jvm.internal.impl.renderer.RenderingFormat
        public String escape(String string) {
            Intrinsics.checkNotNullParameter(string, "string");
            return StringsKt.replace$default(StringsKt.replace$default(string, "<", "&lt;", false, 4, (Object) null), ">", "&gt;", false, 4, (Object) null);
        }
    };

    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());

    /* synthetic */ RenderingFormat(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    public abstract String escape(String str);
}
