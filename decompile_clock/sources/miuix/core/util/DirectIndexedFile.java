package miuix.core.util;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import com.android.deskclock.addition.resource.ExternalResourceUtils;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class DirectIndexedFile {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "DensityIndexFile: ";

    private interface InputFile extends DataInput {
        void close() throws IOException;

        long getFilePointer() throws IOException;

        void seek(long j) throws IOException;
    }

    protected DirectIndexedFile() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    private static class IndexGroupDescriptor {
        int mMaxIndex;
        int mMinIndex;
        long mOffset;

        /* synthetic */ IndexGroupDescriptor(int i, int i2, long j, AnonymousClass1 anonymousClass1) {
            this(i, i2, j);
        }

        private IndexGroupDescriptor(int i, int i2, long j) {
            this.mMinIndex = i;
            this.mMaxIndex = i2;
            this.mOffset = j;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static IndexGroupDescriptor read(DataInput dataInput) throws IOException {
            return new IndexGroupDescriptor(dataInput.readInt(), dataInput.readInt(), dataInput.readLong());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int write(DataOutput dataOutput) throws IOException {
            if (dataOutput == null) {
                return 16;
            }
            dataOutput.writeInt(this.mMinIndex);
            dataOutput.writeInt(this.mMaxIndex);
            dataOutput.writeLong(this.mOffset);
            return 16;
        }
    }

    private static class DataItemDescriptor {
        private static byte[] sBuffer = new byte[1024];
        private byte mIndexSize;
        private byte mLengthSize;
        private long mOffset;
        private byte mOffsetSize;
        private Type mType;

        private enum Type {
            BYTE,
            SHORT,
            INTEGER,
            LONG,
            STRING,
            BYTE_ARRAY,
            SHORT_ARRAY,
            INTEGER_ARRAY,
            LONG_ARRAY
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static byte getSizeOf(int i) {
            byte b = 0;
            for (long j = i * 2; j > 0; j >>= 8) {
                b = (byte) (b + 1);
            }
            if (b == 3) {
                return (byte) 4;
            }
            if (b <= 4 || b >= 8) {
                return b;
            }
            return (byte) 8;
        }

        /* synthetic */ DataItemDescriptor(Type type, byte b, byte b2, byte b3, long j, AnonymousClass1 anonymousClass1) {
            this(type, b, b2, b3, j);
        }

        private static byte[] aquireBuffer(int i) {
            byte[] bArr;
            synchronized (DataItemDescriptor.class) {
                byte[] bArr2 = sBuffer;
                if (bArr2 == null || bArr2.length < i) {
                    sBuffer = new byte[i];
                }
                bArr = sBuffer;
                sBuffer = null;
            }
            return bArr;
        }

        private static void releaseBuffer(byte[] bArr) {
            synchronized (DataItemDescriptor.class) {
                if (bArr != null) {
                    byte[] bArr2 = sBuffer;
                    if (bArr2 == null || bArr2.length < bArr.length) {
                        sBuffer = bArr;
                    }
                }
            }
        }

        private DataItemDescriptor(Type type, byte b, byte b2, byte b3, long j) {
            this.mType = type;
            this.mIndexSize = b;
            this.mLengthSize = b2;
            this.mOffsetSize = b3;
            this.mOffset = j;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static DataItemDescriptor read(DataInput dataInput) throws IOException {
            return new DataItemDescriptor(Type.values()[dataInput.readByte()], dataInput.readByte(), dataInput.readByte(), dataInput.readByte(), dataInput.readLong());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int write(DataOutput dataOutput) throws IOException {
            if (dataOutput == null) {
                return 12;
            }
            dataOutput.writeByte(this.mType.ordinal());
            dataOutput.writeByte(this.mIndexSize);
            dataOutput.writeByte(this.mLengthSize);
            dataOutput.writeByte(this.mOffsetSize);
            dataOutput.writeLong(this.mOffset);
            return 12;
        }

        /* JADX WARN: Code duplicated, block: B:57:0x005b A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:59:0x001a A[SYNTHETIC] */
        private int writeOffsets(DataOutput dataOutput, List<Object> list) throws IOException {
            int length;
            int i;
            byte b;
            int length2;
            if (dataOutput == null || this.mOffsetSize == 0 || this.mLengthSize == 0) {
                int size = list.size() * 4;
                int i2 = 0;
                for (Object obj : list) {
                    int i3 = AnonymousClass1.$SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[this.mType.ordinal()];
                    if (i3 != 1) {
                        if (i3 != 2) {
                            if (i3 == 3) {
                                length = ((short[]) obj).length;
                                i = length * 2;
                            } else if (i3 == 4) {
                                length = ((int[]) obj).length;
                                i = length * 4;
                            } else if (i3 != 5) {
                                length = 0;
                            } else {
                                length = ((long[]) obj).length;
                                i = length * 8;
                            }
                            size += i;
                        } else {
                            length = ((byte[]) obj).length;
                        }
                        if (i2 < length) {
                            i2 = length;
                        }
                    } else {
                        length = ((String) obj).getBytes().length;
                    }
                    size += length;
                    if (i2 < length) {
                        i2 = length;
                    }
                }
                byte sizeOf = getSizeOf(i2);
                this.mLengthSize = sizeOf;
                this.mOffsetSize = getSizeOf(size + (sizeOf * list.size()));
            }
            int size2 = this.mOffsetSize * list.size();
            if (dataOutput != null) {
                int i4 = size2;
                for (Object obj2 : list) {
                    writeAccordingToSize(dataOutput, this.mOffsetSize, i4);
                    int i5 = AnonymousClass1.$SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[this.mType.ordinal()];
                    if (i5 == 1) {
                        b = this.mLengthSize;
                        length2 = ((String) obj2).getBytes().length;
                    } else if (i5 == 2) {
                        b = this.mLengthSize;
                        length2 = ((byte[]) obj2).length;
                    } else if (i5 == 3) {
                        b = this.mLengthSize;
                        length2 = ((short[]) obj2).length * 2;
                    } else if (i5 == 4) {
                        b = this.mLengthSize;
                        length2 = ((int[]) obj2).length * 4;
                    } else if (i5 == 5) {
                        b = this.mLengthSize;
                        length2 = ((long[]) obj2).length * 8;
                    }
                    i4 += b + length2;
                }
            }
            return size2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int writeDataItems(DataOutput dataOutput, List<Object> list) throws IOException {
            int iWriteOffsets = 4;
            switch (AnonymousClass1.$SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[this.mType.ordinal()]) {
                case 1:
                    if (dataOutput != null) {
                        dataOutput.writeInt(list.size());
                    }
                    int iWriteOffsets2 = 4 + writeOffsets(dataOutput, list);
                    Iterator<Object> it = list.iterator();
                    int length = iWriteOffsets2;
                    while (it.hasNext()) {
                        byte[] bytes = ((String) it.next()).getBytes();
                        if (dataOutput != null) {
                            writeAccordingToSize(dataOutput, this.mLengthSize, bytes.length);
                            for (byte b : bytes) {
                                dataOutput.writeByte(b);
                            }
                        }
                        length += this.mLengthSize + bytes.length;
                    }
                    return length;
                case 2:
                    if (dataOutput != null) {
                        dataOutput.writeInt(list.size());
                    }
                    int iWriteOffsets3 = 4 + writeOffsets(dataOutput, list);
                    Iterator<Object> it2 = list.iterator();
                    int length2 = iWriteOffsets3;
                    while (it2.hasNext()) {
                        byte[] bArr = (byte[]) it2.next();
                        if (dataOutput != null) {
                            writeAccordingToSize(dataOutput, this.mLengthSize, bArr.length);
                            dataOutput.write(bArr);
                        }
                        length2 += this.mLengthSize + bArr.length;
                    }
                    return length2;
                case 3:
                    if (dataOutput != null) {
                        dataOutput.writeInt(list.size());
                    }
                    int iWriteOffsets4 = 4 + writeOffsets(dataOutput, list);
                    Iterator<Object> it3 = list.iterator();
                    int length3 = iWriteOffsets4;
                    while (it3.hasNext()) {
                        short[] sArr = (short[]) it3.next();
                        if (dataOutput != null) {
                            writeAccordingToSize(dataOutput, this.mLengthSize, sArr.length);
                            for (short s : sArr) {
                                dataOutput.writeShort(s);
                            }
                        }
                        length3 += this.mLengthSize + (sArr.length * 2);
                    }
                    return length3;
                case 4:
                    if (dataOutput != null) {
                        dataOutput.writeInt(list.size());
                    }
                    int iWriteOffsets5 = writeOffsets(dataOutput, list) + 4;
                    Iterator<Object> it4 = list.iterator();
                    int length4 = iWriteOffsets5;
                    while (it4.hasNext()) {
                        int[] iArr = (int[]) it4.next();
                        if (dataOutput != null) {
                            writeAccordingToSize(dataOutput, this.mLengthSize, iArr.length);
                            for (int i : iArr) {
                                dataOutput.writeInt(i);
                            }
                        }
                        length4 += this.mLengthSize + (iArr.length * 4);
                    }
                    return length4;
                case 5:
                    if (dataOutput != null) {
                        dataOutput.writeInt(list.size());
                    }
                    iWriteOffsets = 4 + writeOffsets(dataOutput, list);
                    Iterator<Object> it5 = list.iterator();
                    while (it5.hasNext()) {
                        long[] jArr = (long[]) it5.next();
                        if (dataOutput != null) {
                            writeAccordingToSize(dataOutput, this.mLengthSize, jArr.length);
                            for (long j : jArr) {
                                dataOutput.writeLong(j);
                            }
                        }
                        iWriteOffsets += this.mLengthSize + (jArr.length * 8);
                    }
                    break;
                case 6:
                    if (dataOutput != null) {
                        dataOutput.writeByte(((Byte) list.get(0)).byteValue());
                    }
                    return 1;
                case 7:
                    if (dataOutput != null) {
                        dataOutput.writeShort(((Short) list.get(0)).shortValue());
                    }
                    return 2;
                case 8:
                    if (dataOutput != null) {
                        dataOutput.writeInt(((Integer) list.get(0)).intValue());
                    }
                    break;
                case 9:
                    if (dataOutput == null) {
                        return 8;
                    }
                    dataOutput.writeLong(((Long) list.get(0)).longValue());
                    return 8;
                default:
                    return 0;
            }
            return iWriteOffsets;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v13, types: [int[]] */
        /* JADX WARN: Type inference failed for: r0v15, types: [long[]] */
        /* JADX WARN: Type inference failed for: r0v16 */
        /* JADX WARN: Type inference failed for: r0v5 */
        /* JADX WARN: Type inference failed for: r0v9 */
        public Object readSingleDataItem(InputFile inputFile, int i) throws IOException {
            byte[] bArrAquireBuffer;
            short[] str;
            long filePointer = inputFile.getFilePointer();
            if (i != 0) {
                inputFile.seek(((long) (this.mOffsetSize * i)) + filePointer);
            }
            inputFile.seek(filePointer + readAccordingToSize(inputFile, this.mOffsetSize));
            int i2 = AnonymousClass1.$SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[this.mType.ordinal()];
            int i3 = 0;
            if (i2 != 1) {
                bArrAquireBuffer = null;
                if (i2 == 2) {
                    byte[] bArr = new byte[(int) readAccordingToSize(inputFile, this.mLengthSize)];
                    inputFile.readFully(bArr);
                    str = bArr;
                } else if (i2 == 3) {
                    int accordingToSize = (int) readAccordingToSize(inputFile, this.mLengthSize);
                    str = new short[accordingToSize];
                    while (i3 < accordingToSize) {
                        str[i3] = inputFile.readShort();
                        i3++;
                    }
                } else if (i2 == 4) {
                    int accordingToSize2 = (int) readAccordingToSize(inputFile, this.mLengthSize);
                    str = new int[accordingToSize2];
                    while (i3 < accordingToSize2) {
                        str[i3] = inputFile.readInt();
                        i3++;
                    }
                } else if (i2 != 5) {
                    str = 0;
                } else {
                    int accordingToSize3 = (int) readAccordingToSize(inputFile, this.mLengthSize);
                    str = new long[accordingToSize3];
                    while (i3 < accordingToSize3) {
                        str[i3] = inputFile.readLong();
                        i3++;
                    }
                }
            } else {
                int accordingToSize4 = (int) readAccordingToSize(inputFile, this.mLengthSize);
                bArrAquireBuffer = aquireBuffer(accordingToSize4);
                inputFile.readFully(bArrAquireBuffer, 0, accordingToSize4);
                str = new String(bArrAquireBuffer, 0, accordingToSize4);
            }
            releaseBuffer(bArrAquireBuffer);
            return str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Object[] readDataItems(InputFile inputFile) throws IOException {
            switch (AnonymousClass1.$SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[this.mType.ordinal()]) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    Object[] objArr = new Object[inputFile.readInt()];
                    objArr[0] = readSingleDataItem(inputFile, 0);
                    return objArr;
                case 6:
                    return new Object[]{Byte.valueOf(inputFile.readByte())};
                case 7:
                    return new Object[]{Short.valueOf(inputFile.readShort())};
                case 8:
                    return new Object[]{Integer.valueOf(inputFile.readInt())};
                case 9:
                    return new Object[]{Long.valueOf(inputFile.readLong())};
                default:
                    return null;
            }
        }

        private static void writeAccordingToSize(DataOutput dataOutput, int i, long j) throws IOException {
            if (i == 1) {
                dataOutput.writeByte((int) j);
                return;
            }
            if (i == 2) {
                dataOutput.writeShort((int) j);
            } else if (i == 4) {
                dataOutput.writeInt((int) j);
            } else {
                if (i == 8) {
                    dataOutput.writeLong(j);
                    return;
                }
                throw new IllegalArgumentException("Unsuppoert size " + i);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static long readAccordingToSize(DataInput dataInput, int i) throws IOException {
            int i2;
            if (i == 1) {
                i2 = dataInput.readByte();
            } else if (i == 2) {
                i2 = dataInput.readShort();
            } else {
                if (i != 4) {
                    if (i == 8) {
                        return dataInput.readLong();
                    }
                    throw new IllegalArgumentException("Unsuppoert size " + i);
                }
                i2 = dataInput.readInt();
            }
            return i2;
        }
    }

    /* JADX INFO: renamed from: miuix.core.util.DirectIndexedFile$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type;

        static {
            int[] iArr = new int[DataItemDescriptor.Type.values().length];
            $SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type = iArr;
            try {
                iArr[DataItemDescriptor.Type.STRING.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[DataItemDescriptor.Type.BYTE_ARRAY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[DataItemDescriptor.Type.SHORT_ARRAY.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[DataItemDescriptor.Type.INTEGER_ARRAY.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[DataItemDescriptor.Type.LONG_ARRAY.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[DataItemDescriptor.Type.BYTE.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[DataItemDescriptor.Type.SHORT.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[DataItemDescriptor.Type.INTEGER.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[DataItemDescriptor.Type.LONG.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
        }
    }

    private static class DescriptionPair {
        private long mDataItemDescriptionOffset;
        private long mIndexGroupDescriptionOffset;

        /* synthetic */ DescriptionPair(long j, long j2, AnonymousClass1 anonymousClass1) {
            this(j, j2);
        }

        private DescriptionPair(long j, long j2) {
            this.mIndexGroupDescriptionOffset = j;
            this.mDataItemDescriptionOffset = j2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static DescriptionPair read(DataInput dataInput) throws IOException {
            return new DescriptionPair(dataInput.readLong(), dataInput.readLong());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int write(DataOutput dataOutput) throws IOException {
            if (dataOutput == null) {
                return 16;
            }
            dataOutput.writeLong(this.mIndexGroupDescriptionOffset);
            dataOutput.writeLong(this.mDataItemDescriptionOffset);
            return 16;
        }
    }

    private static class FileHeader {
        private static final int CURRENT_VERSION = 2;
        private static final byte[] FILE_TAG = {73, 68, 70, 32};
        private int mDataVersion;
        private DescriptionPair[] mDescriptionOffsets;

        /* synthetic */ FileHeader(int i, int i2, AnonymousClass1 anonymousClass1) {
            this(i, i2);
        }

        private FileHeader(int i, int i2) {
            this.mDescriptionOffsets = new DescriptionPair[i];
            this.mDataVersion = i2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static FileHeader read(DataInput dataInput) throws IOException {
            int length = FILE_TAG.length;
            byte[] bArr = new byte[length];
            for (int i = 0; i < length; i++) {
                bArr[i] = dataInput.readByte();
            }
            if (!Arrays.equals(bArr, FILE_TAG)) {
                throw new IOException("File tag unmatched, file may be corrupt");
            }
            if (dataInput.readInt() != 2) {
                throw new IOException("File version unmatched, please upgrade your reader");
            }
            int i2 = dataInput.readInt();
            FileHeader fileHeader = new FileHeader(i2, dataInput.readInt());
            for (int i3 = 0; i3 < i2; i3++) {
                fileHeader.mDescriptionOffsets[i3] = DescriptionPair.read(dataInput);
            }
            return fileHeader;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int write(DataOutput dataOutput) throws IOException {
            byte[] bArr = FILE_TAG;
            int length = bArr.length + 12;
            if (dataOutput != null) {
                dataOutput.write(bArr);
                dataOutput.writeInt(2);
                dataOutput.writeInt(this.mDescriptionOffsets.length);
                dataOutput.writeInt(this.mDataVersion);
            }
            for (DescriptionPair descriptionPair : this.mDescriptionOffsets) {
                length += descriptionPair.write(dataOutput);
            }
            return length;
        }
    }

    private static class DataInputRandom implements InputFile {
        private RandomAccessFile mFile;

        DataInputRandom(RandomAccessFile randomAccessFile) {
            this.mFile = randomAccessFile;
        }

        @Override // java.io.DataInput
        public boolean readBoolean() throws IOException {
            return this.mFile.readBoolean();
        }

        @Override // java.io.DataInput
        public byte readByte() throws IOException {
            return this.mFile.readByte();
        }

        @Override // java.io.DataInput
        public char readChar() throws IOException {
            return this.mFile.readChar();
        }

        @Override // java.io.DataInput
        public double readDouble() throws IOException {
            return this.mFile.readDouble();
        }

        @Override // java.io.DataInput
        public float readFloat() throws IOException {
            return this.mFile.readFloat();
        }

        @Override // java.io.DataInput
        public void readFully(byte[] bArr) throws IOException {
            this.mFile.readFully(bArr);
        }

        @Override // java.io.DataInput
        public void readFully(byte[] bArr, int i, int i2) throws IOException {
            this.mFile.readFully(bArr, i, i2);
        }

        @Override // java.io.DataInput
        public int readInt() throws IOException {
            return this.mFile.readInt();
        }

        @Override // java.io.DataInput
        public String readLine() throws IOException {
            return this.mFile.readLine();
        }

        @Override // java.io.DataInput
        public long readLong() throws IOException {
            return this.mFile.readLong();
        }

        @Override // java.io.DataInput
        public short readShort() throws IOException {
            return this.mFile.readShort();
        }

        @Override // java.io.DataInput
        public int readUnsignedByte() throws IOException {
            return this.mFile.readUnsignedByte();
        }

        @Override // java.io.DataInput
        public int readUnsignedShort() throws IOException {
            return this.mFile.readUnsignedShort();
        }

        @Override // java.io.DataInput
        public String readUTF() throws IOException {
            return this.mFile.readUTF();
        }

        @Override // java.io.DataInput
        public int skipBytes(int i) throws IOException {
            return this.mFile.skipBytes(i);
        }

        @Override // miuix.core.util.DirectIndexedFile.InputFile
        public void seek(long j) throws IOException {
            this.mFile.seek(j);
        }

        @Override // miuix.core.util.DirectIndexedFile.InputFile
        public void close() throws IOException {
            this.mFile.close();
        }

        @Override // miuix.core.util.DirectIndexedFile.InputFile
        public long getFilePointer() throws IOException {
            return this.mFile.getFilePointer();
        }
    }

    private static class DataInputStream implements InputFile {
        private InputStream mInputFile;
        private long mInputPos;

        DataInputStream(InputStream inputStream) {
            this.mInputFile = inputStream;
            inputStream.mark(0);
            this.mInputPos = 0L;
        }

        @Override // java.io.DataInput
        public boolean readBoolean() throws IOException {
            this.mInputPos++;
            return this.mInputFile.read() != 0;
        }

        @Override // java.io.DataInput
        public byte readByte() throws IOException {
            this.mInputPos++;
            return (byte) this.mInputFile.read();
        }

        @Override // java.io.DataInput
        public char readChar() throws IOException {
            byte[] bArr = new byte[2];
            this.mInputPos += 2;
            if (this.mInputFile.read(bArr) == 2) {
                return (char) (((char) (bArr[1] & 255)) | ((bArr[0] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK));
            }
            return (char) 0;
        }

        @Override // java.io.DataInput
        public double readDouble() throws IOException {
            throw new IOException();
        }

        @Override // java.io.DataInput
        public float readFloat() throws IOException {
            throw new IOException();
        }

        @Override // java.io.DataInput
        public void readFully(byte[] bArr) throws IOException {
            this.mInputPos += (long) this.mInputFile.read(bArr);
        }

        @Override // java.io.DataInput
        public void readFully(byte[] bArr, int i, int i2) throws IOException {
            this.mInputPos += (long) this.mInputFile.read(bArr, i, i2);
        }

        @Override // java.io.DataInput
        public int readInt() throws IOException {
            byte[] bArr = new byte[4];
            this.mInputPos += 4;
            if (this.mInputFile.read(bArr) == 4) {
                return (bArr[3] & 255) | ((bArr[2] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | ((bArr[1] << 16) & 16711680) | ((bArr[0] << 24) & ViewCompat.MEASURED_STATE_MASK);
            }
            return 0;
        }

        @Override // java.io.DataInput
        public String readLine() throws IOException {
            throw new IOException();
        }

        @Override // java.io.DataInput
        public long readLong() throws IOException {
            byte[] bArr = new byte[8];
            this.mInputPos += 8;
            if (this.mInputFile.read(bArr) != 8) {
                return 0L;
            }
            return ((((long) bArr[0]) << 56) & (-72057594037927936L)) | ((long) (bArr[7] & 255)) | (((long) (bArr[6] << 8)) & 65280) | (((long) (bArr[5] << 16)) & 16711680) | (((long) (bArr[4] << 24)) & 4278190080L) | ((((long) bArr[3]) << 32) & 1095216660480L) | ((((long) bArr[2]) << 40) & 280375465082880L) | ((((long) bArr[1]) << 48) & 71776119061217280L);
        }

        @Override // java.io.DataInput
        public short readShort() throws IOException {
            byte[] bArr = new byte[2];
            this.mInputPos += 2;
            if (this.mInputFile.read(bArr) == 2) {
                return (short) (((short) (bArr[1] & 255)) | ((bArr[0] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK));
            }
            return (short) 0;
        }

        @Override // java.io.DataInput
        public int readUnsignedByte() throws IOException {
            this.mInputPos++;
            return (byte) this.mInputFile.read();
        }

        @Override // java.io.DataInput
        public int readUnsignedShort() throws IOException {
            byte[] bArr = new byte[2];
            this.mInputPos += 2;
            if (this.mInputFile.read(bArr) == 2) {
                return (short) (((short) (bArr[1] & 255)) | ((bArr[0] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK));
            }
            return 0;
        }

        @Override // java.io.DataInput
        public String readUTF() throws IOException {
            throw new IOException();
        }

        @Override // java.io.DataInput
        public int skipBytes(int i) throws IOException {
            int iSkip = (int) this.mInputFile.skip(i);
            this.mInputPos += (long) iSkip;
            return iSkip;
        }

        @Override // miuix.core.util.DirectIndexedFile.InputFile
        public void seek(long j) throws IOException {
            this.mInputFile.reset();
            if (this.mInputFile.skip(j) == j) {
                this.mInputPos = j;
                return;
            }
            throw new IOException("Skip failed");
        }

        @Override // miuix.core.util.DirectIndexedFile.InputFile
        public void close() throws IOException {
            this.mInputFile.close();
        }

        @Override // miuix.core.util.DirectIndexedFile.InputFile
        public long getFilePointer() throws IOException {
            return this.mInputPos;
        }
    }

    public static class Reader {
        private InputFile mFile;
        private FileHeader mHeader;
        private IndexData[] mIndexData;

        private static class IndexData {
            private DataItemDescriptor[] mDataItemDescriptions;
            private Object[][] mDataItems;
            private IndexGroupDescriptor[] mIndexGroupDescriptions;
            private int mSizeOfItems;

            private IndexData() {
            }

            /* synthetic */ IndexData(AnonymousClass1 anonymousClass1) {
                this();
            }

            static /* synthetic */ int access$912(IndexData indexData, int i) {
                int i2 = indexData.mSizeOfItems + i;
                indexData.mSizeOfItems = i2;
                return i2;
            }
        }

        /* synthetic */ Reader(InputStream inputStream, AnonymousClass1 anonymousClass1) throws IOException {
            this(inputStream);
        }

        /* synthetic */ Reader(String str, AnonymousClass1 anonymousClass1) throws IOException {
            this(str);
        }

        private Reader(InputStream inputStream) throws IOException {
            this.mFile = new DataInputStream(inputStream);
            constructFromFile(ExternalResourceUtils.EXTERNAL_PATH_ASSERT);
        }

        private Reader(String str) throws IOException {
            this.mFile = new DataInputRandom(new RandomAccessFile(str, "r"));
            constructFromFile(str);
        }

        private void constructFromFile(String str) throws IOException {
            System.currentTimeMillis();
            try {
                this.mFile.seek(0L);
                FileHeader fileHeader = FileHeader.read(this.mFile);
                this.mHeader = fileHeader;
                this.mIndexData = new IndexData[fileHeader.mDescriptionOffsets.length];
                for (int i = 0; i < this.mHeader.mDescriptionOffsets.length; i++) {
                    this.mIndexData[i] = new IndexData(null);
                    this.mFile.seek(this.mHeader.mDescriptionOffsets[i].mIndexGroupDescriptionOffset);
                    int i2 = this.mFile.readInt();
                    this.mIndexData[i].mIndexGroupDescriptions = new IndexGroupDescriptor[i2];
                    for (int i3 = 0; i3 < i2; i3++) {
                        this.mIndexData[i].mIndexGroupDescriptions[i3] = IndexGroupDescriptor.read(this.mFile);
                    }
                    this.mFile.seek(this.mHeader.mDescriptionOffsets[i].mDataItemDescriptionOffset);
                    int i4 = this.mFile.readInt();
                    this.mIndexData[i].mSizeOfItems = 0;
                    this.mIndexData[i].mDataItemDescriptions = new DataItemDescriptor[i4];
                    for (int i5 = 0; i5 < i4; i5++) {
                        this.mIndexData[i].mDataItemDescriptions[i5] = DataItemDescriptor.read(this.mFile);
                        IndexData indexData = this.mIndexData[i];
                        IndexData.access$912(indexData, indexData.mDataItemDescriptions[i5].mIndexSize);
                    }
                    this.mIndexData[i].mDataItems = new Object[i4][];
                    for (int i6 = 0; i6 < i4; i6++) {
                        this.mFile.seek(this.mIndexData[i].mDataItemDescriptions[i6].mOffset);
                        this.mIndexData[i].mDataItems[i6] = this.mIndexData[i].mDataItemDescriptions[i6].readDataItems(this.mFile);
                    }
                }
            } catch (IOException e) {
                close();
                throw e;
            }
        }

        public int getDataVersion() {
            FileHeader fileHeader = this.mHeader;
            if (fileHeader == null) {
                return -1;
            }
            return fileHeader.mDataVersion;
        }

        public synchronized Object get(int i, int i2, int i3) {
            Object obj;
            if (this.mFile == null) {
                throw new IllegalStateException("Get data from a corrupt file");
            }
            if (i >= 0) {
                IndexData[] indexDataArr = this.mIndexData;
                if (i < indexDataArr.length) {
                    if (i3 < 0 || i3 >= indexDataArr[i].mDataItemDescriptions.length) {
                        throw new IllegalArgumentException("DataIndex " + i3 + " out of range[0, " + this.mIndexData[i].mDataItemDescriptions.length + ")");
                    }
                    System.currentTimeMillis();
                    long jOffset = offset(i, i2);
                    if (jOffset < 0) {
                        obj = this.mIndexData[i].mDataItems[i3][0];
                    } else {
                        try {
                            this.mFile.seek(jOffset);
                            Object singleDataItem = null;
                            for (int i4 = 0; i4 <= i3; i4++) {
                                switch (AnonymousClass1.$SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[this.mIndexData[i].mDataItemDescriptions[i4].mType.ordinal()]) {
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                        try {
                                            int accordingToSize = (int) DataItemDescriptor.readAccordingToSize(this.mFile, this.mIndexData[i].mDataItemDescriptions[i4].mIndexSize);
                                            if (i4 == i3) {
                                                singleDataItem = readSingleDataItem(i, i3, accordingToSize);
                                            } else {
                                                continue;
                                            }
                                        } catch (IOException e) {
                                            throw new IllegalStateException("File may be corrupt due to invalid data index size", e);
                                        }
                                        break;
                                    case 6:
                                        singleDataItem = Byte.valueOf(this.mFile.readByte());
                                        break;
                                    case 7:
                                        singleDataItem = Short.valueOf(this.mFile.readShort());
                                        break;
                                    case 8:
                                        singleDataItem = Integer.valueOf(this.mFile.readInt());
                                        break;
                                    case 9:
                                        singleDataItem = Long.valueOf(this.mFile.readLong());
                                        break;
                                    default:
                                        throw new IllegalStateException("Unknown type " + this.mIndexData[i].mDataItemDescriptions[i4].mType);
                                }
                            }
                            obj = singleDataItem;
                        } catch (IOException e2) {
                            throw new IllegalStateException("Seek data from a corrupt file", e2);
                        }
                    }
                }
            }
            throw new IllegalArgumentException("Kind " + i + " out of range[0, " + this.mIndexData.length + ")");
            return obj;
        }

        public synchronized Object[] get(int i, int i2) {
            if (this.mFile == null) {
                throw new IllegalStateException("Get data from a corrupt file");
            }
            if (i < 0 || i >= this.mIndexData.length) {
                throw new IllegalArgumentException("Cannot get data kind " + i);
            }
            System.currentTimeMillis();
            long jOffset = offset(i, i2);
            int length = this.mIndexData[i].mDataItemDescriptions.length;
            Object[] objArr = new Object[length];
            if (jOffset < 0) {
                for (int i3 = 0; i3 < length; i3++) {
                    objArr[i3] = this.mIndexData[i].mDataItems[i3][0];
                }
                return objArr;
            }
            try {
                this.mFile.seek(jOffset);
                for (int i4 = 0; i4 < length; i4++) {
                    switch (AnonymousClass1.$SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[this.mIndexData[i].mDataItemDescriptions[i4].mType.ordinal()]) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            try {
                                int accordingToSize = (int) DataItemDescriptor.readAccordingToSize(this.mFile, this.mIndexData[i].mDataItemDescriptions[i4].mIndexSize);
                                long filePointer = this.mFile.getFilePointer();
                                objArr[i4] = readSingleDataItem(i, i4, accordingToSize);
                                this.mFile.seek(filePointer);
                            } catch (IOException e) {
                                throw new IllegalStateException("File may be corrupt due to invalid data index size", e);
                            }
                            break;
                        case 6:
                            objArr[i4] = Byte.valueOf(this.mFile.readByte());
                            break;
                        case 7:
                            objArr[i4] = Short.valueOf(this.mFile.readShort());
                            break;
                        case 8:
                            objArr[i4] = Integer.valueOf(this.mFile.readInt());
                            break;
                        case 9:
                            objArr[i4] = Long.valueOf(this.mFile.readLong());
                            break;
                        default:
                            throw new IllegalStateException("Unknown type " + this.mIndexData[i].mDataItemDescriptions[i4].mType);
                    }
                }
                return objArr;
            } catch (IOException e2) {
                throw new IllegalStateException("Seek data from a corrupt file", e2);
            }
        }

        public synchronized void close() {
            InputFile inputFile = this.mFile;
            if (inputFile != null) {
                try {
                    inputFile.close();
                } catch (IOException unused) {
                }
            }
            this.mFile = null;
            this.mHeader = null;
            this.mIndexData = null;
        }

        private Object readSingleDataItem(int i, int i2, int i3) throws IOException {
            if (this.mIndexData[i].mDataItems[i2][i3] == null) {
                this.mFile.seek(this.mIndexData[i].mDataItemDescriptions[i2].mOffset + 4);
                this.mIndexData[i].mDataItems[i2][i3] = this.mIndexData[i].mDataItemDescriptions[i2].readSingleDataItem(this.mFile, i3);
            }
            return this.mIndexData[i].mDataItems[i2][i3];
        }

        /* JADX WARN: Code duplicated, block: B:13:0x0040  */
        /* JADX WARN: Code duplicated, block: B:14:0x0051 A[ORIG_RETURN, RETURN] */
        private long offset(int i, int i2) {
            IndexGroupDescriptor indexGroupDescriptor;
            int length = this.mIndexData[i].mIndexGroupDescriptions.length;
            int i3 = 0;
            while (i3 < length) {
                int i4 = (length + i3) / 2;
                if (this.mIndexData[i].mIndexGroupDescriptions[i4].mMinIndex > i2) {
                    length = i4;
                } else {
                    if (this.mIndexData[i].mIndexGroupDescriptions[i4].mMaxIndex > i2) {
                        indexGroupDescriptor = this.mIndexData[i].mIndexGroupDescriptions[i4];
                        if (indexGroupDescriptor != null) {
                            return indexGroupDescriptor.mOffset + ((long) ((i2 - indexGroupDescriptor.mMinIndex) * this.mIndexData[i].mSizeOfItems));
                        }
                        return -1L;
                    }
                    i3 = i4 + 1;
                }
            }
            indexGroupDescriptor = null;
            if (indexGroupDescriptor != null) {
                return indexGroupDescriptor.mOffset + ((long) ((i2 - indexGroupDescriptor.mMinIndex) * this.mIndexData[i].mSizeOfItems));
            }
            return -1L;
        }
    }

    public static class Builder {
        private IndexData mCurrentIndexData;
        private int mFileDataVersion;
        private FileHeader mFileHeader;
        private ArrayList<IndexData> mIndexDataList;

        private class Item implements Comparable<Item> {
            private int mIndex;
            private Object[] mObjects;

            /* synthetic */ Item(Builder builder, int i, Object[] objArr, AnonymousClass1 anonymousClass1) {
                this(i, objArr);
            }

            private Item(int i, Object[] objArr) {
                this.mIndex = i;
                this.mObjects = objArr;
            }

            public int hashCode() {
                return this.mIndex;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                return (obj instanceof Item) && this.mIndex == ((Item) obj).mIndex;
            }

            @Override // java.lang.Comparable
            public int compareTo(Item item) {
                return this.mIndex - item.mIndex;
            }
        }

        /* synthetic */ Builder(int i, AnonymousClass1 anonymousClass1) {
            this(i);
        }

        private class DataItemHolder {
            private ArrayList<Object> mList;
            private HashMap<Object, Integer> mMap;

            private DataItemHolder() {
                this.mMap = new HashMap<>();
                this.mList = new ArrayList<>();
            }

            /* synthetic */ DataItemHolder(Builder builder, AnonymousClass1 anonymousClass1) {
                this();
            }

            /* JADX INFO: Access modifiers changed from: private */
            public Integer put(Object obj) {
                Integer num = this.mMap.get(obj);
                if (num != null) {
                    return num;
                }
                Integer numValueOf = Integer.valueOf(this.mList.size());
                this.mMap.put(obj, numValueOf);
                this.mList.add(obj);
                return numValueOf;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public int size() {
                return this.mList.size();
            }

            /* JADX INFO: Access modifiers changed from: private */
            public ArrayList<Object> getAll() {
                return this.mList;
            }
        }

        private static class IndexData {
            private DataItemDescriptor[] mDataItemDescriptions;
            private ArrayList<DataItemHolder> mDataItemHolders;
            private HashMap<Integer, Item> mDataMap;
            private Item mDefaultValue;
            private ArrayList<ArrayList<Item>> mIndexDataGroups;
            private IndexGroupDescriptor[] mIndexGroupDescriptions;

            /* synthetic */ IndexData(int i, AnonymousClass1 anonymousClass1) {
                this(i);
            }

            private IndexData(int i) {
                this.mDataMap = new HashMap<>();
                this.mDataItemHolders = new ArrayList<>();
                this.mIndexDataGroups = new ArrayList<>();
                this.mDataItemDescriptions = new DataItemDescriptor[i];
            }
        }

        private Builder(int i) {
            this.mIndexDataList = new ArrayList<>();
            this.mFileDataVersion = i;
        }

        public void addKind(Object... objArr) {
            DataItemDescriptor.Type type;
            AnonymousClass1 anonymousClass1 = null;
            IndexData indexData = new IndexData(objArr.length, anonymousClass1);
            this.mCurrentIndexData = indexData;
            this.mIndexDataList.add(indexData);
            for (int i = 0; i < objArr.length; i++) {
                this.mCurrentIndexData.mDataItemHolders.add(new DataItemHolder(this, anonymousClass1));
                Object obj = objArr[i];
                byte b = 1;
                if (obj instanceof Byte) {
                    type = DataItemDescriptor.Type.BYTE;
                    ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i)).put(objArr[i]);
                } else if (obj instanceof Short) {
                    type = DataItemDescriptor.Type.SHORT;
                    ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i)).put(objArr[i]);
                    b = 2;
                } else if (obj instanceof Integer) {
                    type = DataItemDescriptor.Type.INTEGER;
                    ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i)).put(objArr[i]);
                    b = 4;
                } else if (obj instanceof Long) {
                    type = DataItemDescriptor.Type.LONG;
                    ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i)).put(objArr[i]);
                    b = 8;
                } else if (obj instanceof String) {
                    type = DataItemDescriptor.Type.STRING;
                    objArr[i] = ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i)).put(objArr[i]);
                } else if (obj instanceof byte[]) {
                    type = DataItemDescriptor.Type.BYTE_ARRAY;
                    objArr[i] = ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i)).put(objArr[i]);
                } else if (obj instanceof short[]) {
                    type = DataItemDescriptor.Type.SHORT_ARRAY;
                    objArr[i] = ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i)).put(objArr[i]);
                } else if (obj instanceof int[]) {
                    type = DataItemDescriptor.Type.INTEGER_ARRAY;
                    objArr[i] = ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i)).put(objArr[i]);
                } else if (obj instanceof long[]) {
                    type = DataItemDescriptor.Type.LONG_ARRAY;
                    objArr[i] = ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i)).put(objArr[i]);
                } else {
                    throw new IllegalArgumentException("Unsupported type of the [" + i + "] argument");
                }
                this.mCurrentIndexData.mDataItemDescriptions[i] = new DataItemDescriptor(type, b, (byte) 0, (byte) 0, 0L, null);
            }
            this.mCurrentIndexData.mDefaultValue = new Item(this, -1, objArr, anonymousClass1);
        }

        public void newGroup() {
            if (this.mCurrentIndexData.mIndexDataGroups.size() == 0 || ((ArrayList) this.mCurrentIndexData.mIndexDataGroups.get(this.mCurrentIndexData.mIndexDataGroups.size() - 1)).size() != 0) {
                this.mCurrentIndexData.mIndexDataGroups.add(new ArrayList());
            }
        }

        public void addGroup(int[] iArr, Object[][] objArr) {
            checkCurrentIndexingDataKind();
            if (iArr.length == objArr.length) {
                newGroup();
                for (int i = 0; i < iArr.length; i++) {
                    add(iArr[i], objArr[i]);
                }
                return;
            }
            throw new IllegalArgumentException("Different number between indexes and objects");
        }

        public void add(int i, Object... objArr) {
            checkCurrentIndexingGroup();
            if (this.mCurrentIndexData.mDataItemDescriptions.length != objArr.length) {
                throw new IllegalArgumentException("Different number of objects inputted, " + this.mCurrentIndexData.mDataItemDescriptions.length + " data expected");
            }
            for (int i2 = 0; i2 < objArr.length; i2++) {
                switch (AnonymousClass1.$SwitchMap$miuix$core$util$DirectIndexedFile$DataItemDescriptor$Type[this.mCurrentIndexData.mDataItemDescriptions[i2].mType.ordinal()]) {
                    case 1:
                        if (objArr[i2] instanceof String) {
                            objArr[i2] = ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i2)).put(objArr[i2]);
                            this.mCurrentIndexData.mDataItemDescriptions[i2].mIndexSize = DataItemDescriptor.getSizeOf(((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i2)).size());
                        } else {
                            throw new IllegalArgumentException("Object[" + i2 + "] should be String");
                        }
                        break;
                    case 2:
                        if (objArr[i2] instanceof byte[]) {
                            objArr[i2] = ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i2)).put(objArr[i2]);
                            this.mCurrentIndexData.mDataItemDescriptions[i2].mIndexSize = DataItemDescriptor.getSizeOf(((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i2)).size());
                        } else {
                            throw new IllegalArgumentException("Object[" + i2 + "] should be byte[]");
                        }
                        break;
                    case 3:
                        if (objArr[i2] instanceof short[]) {
                            objArr[i2] = ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i2)).put(objArr[i2]);
                            this.mCurrentIndexData.mDataItemDescriptions[i2].mIndexSize = DataItemDescriptor.getSizeOf(((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i2)).size());
                        } else {
                            throw new IllegalArgumentException("Object[" + i2 + "] should be short[]");
                        }
                        break;
                    case 4:
                        if (objArr[i2] instanceof int[]) {
                            objArr[i2] = ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i2)).put(objArr[i2]);
                            this.mCurrentIndexData.mDataItemDescriptions[i2].mIndexSize = DataItemDescriptor.getSizeOf(((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i2)).size());
                        } else {
                            throw new IllegalArgumentException("Object[" + i2 + "] should be int[]");
                        }
                        break;
                    case 5:
                        if (objArr[i2] instanceof long[]) {
                            objArr[i2] = ((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i2)).put(objArr[i2]);
                            this.mCurrentIndexData.mDataItemDescriptions[i2].mIndexSize = DataItemDescriptor.getSizeOf(((DataItemHolder) this.mCurrentIndexData.mDataItemHolders.get(i2)).size());
                        } else {
                            throw new IllegalArgumentException("Object[" + i2 + "] should be long[]");
                        }
                        break;
                    case 6:
                        if (!(objArr[i2] instanceof Byte)) {
                            throw new IllegalArgumentException("Object[" + i2 + "] should be byte");
                        }
                        break;
                        break;
                    case 7:
                        if (!(objArr[i2] instanceof Short)) {
                            throw new IllegalArgumentException("Object[" + i2 + "] should be short");
                        }
                        break;
                        break;
                    case 8:
                        if (!(objArr[i2] instanceof Integer)) {
                            throw new IllegalArgumentException("Object[" + i2 + "] should be int");
                        }
                        break;
                        break;
                    case 9:
                        if (!(objArr[i2] instanceof Long)) {
                            throw new IllegalArgumentException("Object[" + i2 + "] should be long");
                        }
                        break;
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported type of objects " + i2 + ", " + this.mCurrentIndexData.mDataItemDescriptions[i2].mType + " expected");
                }
            }
            Item item = new Item(this, i, objArr, null);
            this.mCurrentIndexData.mDataMap.put(Integer.valueOf(i), item);
            ((ArrayList) this.mCurrentIndexData.mIndexDataGroups.get(this.mCurrentIndexData.mIndexDataGroups.size() - 1)).add(item);
        }

        public void write(String str) throws Throwable {
            DataOutputStream dataOutputStream;
            Throwable th;
            build();
            try {
                dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(str)));
                try {
                    writeAll(dataOutputStream);
                    dataOutputStream.close();
                } catch (Throwable th2) {
                    th = th2;
                    if (dataOutputStream != null) {
                        dataOutputStream.close();
                    }
                    if (new File(str).delete()) {
                        System.err.println("Cannot delete file " + str);
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                dataOutputStream = null;
                th = th3;
            }
        }

        private void checkCurrentIndexingDataKind() {
            if (this.mCurrentIndexData == null) {
                throw new IllegalArgumentException("Please add a data kind before adding group");
            }
        }

        private void checkCurrentIndexingGroup() {
            checkCurrentIndexingDataKind();
            if (this.mCurrentIndexData.mIndexDataGroups.size() == 0) {
                throw new IllegalArgumentException("Please add a data group before adding data");
            }
        }

        private void build() {
            int size = this.mIndexDataList.size();
            this.mFileHeader = new FileHeader(size, this.mFileDataVersion, null);
            for (int i = 0; i < size; i++) {
                IndexData indexData = this.mIndexDataList.get(i);
                this.mFileHeader.mDescriptionOffsets[i] = new DescriptionPair(0L, 0L, null);
                int i2 = 0;
                while (i2 < indexData.mIndexDataGroups.size() && ((ArrayList) indexData.mIndexDataGroups.get(i2)).size() != 0) {
                    i2++;
                }
                indexData.mIndexGroupDescriptions = new IndexGroupDescriptor[i2];
                for (int i3 = 0; i3 < indexData.mIndexGroupDescriptions.length; i3++) {
                    ArrayList arrayList = (ArrayList) indexData.mIndexDataGroups.get(i3);
                    Collections.sort(arrayList);
                    indexData.mIndexGroupDescriptions[i3] = new IndexGroupDescriptor(((Item) arrayList.get(0)).mIndex, ((Item) arrayList.get(arrayList.size() - 1)).mIndex + 1, 0L, null);
                }
            }
            try {
                writeAll(null);
            } catch (IOException unused) {
            }
        }

        private int writeAll(DataOutput dataOutput) throws IOException {
            int iWriteDataItems = 0;
            for (int i = 0; i < this.mFileHeader.mDescriptionOffsets.length; i++) {
                int iWrite = iWriteDataItems + this.mFileHeader.write(dataOutput);
                this.mFileHeader.mDescriptionOffsets[i].mIndexGroupDescriptionOffset = iWrite;
                IndexData indexData = this.mIndexDataList.get(i);
                if (dataOutput != null) {
                    dataOutput.writeInt(indexData.mIndexGroupDescriptions.length);
                }
                int iWrite2 = iWrite + 4;
                for (int i2 = 0; i2 < indexData.mIndexGroupDescriptions.length; i2++) {
                    iWrite2 += indexData.mIndexGroupDescriptions[i2].write(dataOutput);
                }
                this.mFileHeader.mDescriptionOffsets[i].mDataItemDescriptionOffset = iWrite2;
                if (dataOutput != null) {
                    dataOutput.writeInt(indexData.mDataItemDescriptions.length);
                }
                iWriteDataItems = iWrite2 + 4;
                for (int i3 = 0; i3 < indexData.mDataItemDescriptions.length; i3++) {
                    iWriteDataItems += indexData.mDataItemDescriptions[i3].write(dataOutput);
                }
                for (int i4 = 0; i4 < indexData.mDataItemDescriptions.length; i4++) {
                    indexData.mDataItemDescriptions[i4].mOffset = iWriteDataItems;
                    iWriteDataItems += indexData.mDataItemDescriptions[i4].writeDataItems(dataOutput, ((DataItemHolder) indexData.mDataItemHolders.get(i4)).getAll());
                }
                for (int i5 = 0; i5 < indexData.mIndexGroupDescriptions.length; i5++) {
                    indexData.mIndexGroupDescriptions[i5].mOffset = iWriteDataItems;
                    if (dataOutput == null) {
                        int i6 = 0;
                        for (int i7 = 0; i7 < indexData.mDataItemDescriptions.length; i7++) {
                            i6 += indexData.mDataItemDescriptions[i7].mIndexSize;
                        }
                        iWriteDataItems += (indexData.mIndexGroupDescriptions[i5].mMaxIndex - indexData.mIndexGroupDescriptions[i5].mMinIndex) * i6;
                    } else {
                        for (int i8 = indexData.mIndexGroupDescriptions[i5].mMinIndex; i8 < indexData.mIndexGroupDescriptions[i5].mMaxIndex; i8++) {
                            Item item = (Item) indexData.mDataMap.get(Integer.valueOf(i8));
                            if (item == null) {
                                item = indexData.mDefaultValue;
                            }
                            for (int i9 = 0; i9 < indexData.mDataItemDescriptions.length; i9++) {
                                if (indexData.mDataItemDescriptions[i9].mIndexSize != 1) {
                                    if (indexData.mDataItemDescriptions[i9].mIndexSize != 2) {
                                        if (indexData.mDataItemDescriptions[i9].mIndexSize != 4) {
                                            if (indexData.mDataItemDescriptions[i9].mIndexSize == 8) {
                                                dataOutput.writeLong(((Long) item.mObjects[i9]).longValue());
                                            }
                                        } else {
                                            dataOutput.writeInt(((Integer) item.mObjects[i9]).intValue());
                                        }
                                    } else {
                                        dataOutput.writeShort(((Integer) item.mObjects[i9]).intValue());
                                    }
                                } else {
                                    dataOutput.writeByte(((Integer) item.mObjects[i9]).intValue());
                                }
                                iWriteDataItems += indexData.mDataItemDescriptions[i9].mIndexSize;
                            }
                        }
                    }
                }
            }
            return iWriteDataItems;
        }
    }

    public static Builder build(int i) {
        return new Builder(i, null);
    }

    public static Reader open(String str) throws IOException {
        return new Reader(str, (AnonymousClass1) null);
    }

    public static Reader open(InputStream inputStream) throws IOException {
        return new Reader(inputStream, (AnonymousClass1) null);
    }
}
