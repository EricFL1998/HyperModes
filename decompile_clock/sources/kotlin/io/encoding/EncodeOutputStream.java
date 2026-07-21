package kotlin.io.encoding;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.io.IOException;
import java.io.OutputStream;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import miuix.navigation.SplitLayout;

/* JADX INFO: compiled from: Base64IOStream.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\r\b\u0003\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\nH\u0016J \u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\nH\u0016J\b\u0010\u0015\u001a\u00020\u0010H\u0016J\b\u0010\u0016\u001a\u00020\u0010H\u0016J \u0010\u0017\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\n2\u0006\u0010\u0019\u001a\u00020\nH\u0002J\b\u0010\u001a\u001a\u00020\u0010H\u0002J \u0010\u001b\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\n2\u0006\u0010\u0019\u001a\u00020\nH\u0002J\b\u0010\u001c\u001a\u00020\u0010H\u0002R\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001d"}, d2 = {"Lkotlin/io/encoding/EncodeOutputStream;", "Ljava/io/OutputStream;", "output", "base64", "Lkotlin/io/encoding/Base64;", "<init>", "(Ljava/io/OutputStream;Lkotlin/io/encoding/Base64;)V", "isClosed", "", "lineLength", "", "symbolBuffer", "", "byteBuffer", "byteBufferLength", "write", "", "b", "source", TypedValues.CycleType.S_WAVE_OFFSET, "length", "flush", SplitLayout.TAG_CLOSE, "copyIntoByteBuffer", "startIndex", "endIndex", "encodeByteBufferIntoOutput", "encodeIntoOutput", "checkOpen", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
final class EncodeOutputStream extends OutputStream {
    private final Base64 base64;
    private final byte[] byteBuffer;
    private int byteBufferLength;
    private boolean isClosed;
    private int lineLength;
    private final OutputStream output;
    private final byte[] symbolBuffer;

    public EncodeOutputStream(OutputStream output, Base64 base64) {
        Intrinsics.checkNotNullParameter(output, "output");
        Intrinsics.checkNotNullParameter(base64, "base64");
        this.output = output;
        this.base64 = base64;
        this.lineLength = base64.getIsMimeScheme() ? 76 : -1;
        this.symbolBuffer = new byte[1024];
        this.byteBuffer = new byte[3];
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        checkOpen();
        byte[] bArr = this.byteBuffer;
        int i = this.byteBufferLength;
        int i2 = i + 1;
        this.byteBufferLength = i2;
        bArr[i] = (byte) b;
        if (i2 == 3) {
            encodeByteBufferIntoOutput();
        }
    }

    @Override // java.io.OutputStream
    public void write(byte[] source, int offset, int length) throws IOException {
        int i;
        Intrinsics.checkNotNullParameter(source, "source");
        checkOpen();
        if (offset < 0 || length < 0 || (i = offset + length) > source.length) {
            throw new IndexOutOfBoundsException("offset: " + offset + ", length: " + length + ", source size: " + source.length);
        }
        if (length == 0) {
            return;
        }
        int i2 = this.byteBufferLength;
        if (i2 >= 3) {
            throw new IllegalStateException("Check failed.");
        }
        if (i2 != 0) {
            offset += copyIntoByteBuffer(source, offset, i);
            if (this.byteBufferLength != 0) {
                return;
            }
        }
        while (offset + 3 <= i) {
            int iMin = Math.min((this.base64.getIsMimeScheme() ? this.lineLength : this.symbolBuffer.length) / 4, (i - offset) / 3);
            int i3 = (iMin * 3) + offset;
            if (encodeIntoOutput(source, offset, i3) != iMin * 4) {
                throw new IllegalStateException("Check failed.");
            }
            offset = i3;
        }
        ArraysKt.copyInto(source, this.byteBuffer, 0, offset, i);
        this.byteBufferLength = i - offset;
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        checkOpen();
        this.output.flush();
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.isClosed) {
            return;
        }
        this.isClosed = true;
        if (this.byteBufferLength != 0) {
            encodeByteBufferIntoOutput();
        }
        this.output.close();
    }

    private final int copyIntoByteBuffer(byte[] source, int startIndex, int endIndex) {
        int iMin = Math.min(3 - this.byteBufferLength, endIndex - startIndex);
        ArraysKt.copyInto(source, this.byteBuffer, this.byteBufferLength, startIndex, startIndex + iMin);
        int i = this.byteBufferLength + iMin;
        this.byteBufferLength = i;
        if (i == 3) {
            encodeByteBufferIntoOutput();
        }
        return iMin;
    }

    private final void encodeByteBufferIntoOutput() {
        if (encodeIntoOutput(this.byteBuffer, 0, this.byteBufferLength) != 4) {
            throw new IllegalStateException("Check failed.");
        }
        this.byteBufferLength = 0;
    }

    private final int encodeIntoOutput(byte[] source, int startIndex, int endIndex) throws IOException {
        int iEncodeIntoByteArray = this.base64.encodeIntoByteArray(source, this.symbolBuffer, 0, startIndex, endIndex);
        if (this.lineLength == 0) {
            this.output.write(Base64.INSTANCE.getMimeLineSeparatorSymbols$kotlin_stdlib());
            this.lineLength = 76;
            if (iEncodeIntoByteArray > 76) {
                throw new IllegalStateException("Check failed.");
            }
        }
        this.output.write(this.symbolBuffer, 0, iEncodeIntoByteArray);
        this.lineLength -= iEncodeIntoByteArray;
        return iEncodeIntoByteArray;
    }

    private final void checkOpen() throws IOException {
        if (this.isClosed) {
            throw new IOException("The output stream is closed.");
        }
    }
}
