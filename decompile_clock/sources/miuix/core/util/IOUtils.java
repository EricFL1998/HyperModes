package miuix.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class IOUtils {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final String LINE_SEPARATOR;
    private static final Pools.Pool<StringWriter> STRING_WRITER_POOL;
    private static final ThreadLocal<SoftReference<byte[]>> THREAD_LOCAL_BYTE_BUFFER = new ThreadLocal<>();
    private static final ThreadLocal<SoftReference<char[]>> THREAD_LOCAL_CHAR_BUFFER = new ThreadLocal<>();
    private static final Pools.Pool<ByteArrayOutputStream> BYTE_ARRAY_OUTPUT_STREAM_POOL = Pools.createSoftReferencePool(new Pools.Manager<ByteArrayOutputStream>() { // from class: miuix.core.util.IOUtils.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // miuix.core.util.Pools.Manager
        public ByteArrayOutputStream createInstance() {
            return new ByteArrayOutputStream();
        }

        @Override // miuix.core.util.Pools.Manager
        public void onRelease(ByteArrayOutputStream byteArrayOutputStream) {
            byteArrayOutputStream.reset();
        }
    }, 2);
    private static final Pools.Pool<CharArrayWriter> CHAR_ARRAY_WRITER_POOL = Pools.createSoftReferencePool(new Pools.Manager<CharArrayWriter>() { // from class: miuix.core.util.IOUtils.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // miuix.core.util.Pools.Manager
        public CharArrayWriter createInstance() {
            return new CharArrayWriter();
        }

        @Override // miuix.core.util.Pools.Manager
        public void onRelease(CharArrayWriter charArrayWriter) {
            charArrayWriter.reset();
        }
    }, 2);

    /* JADX WARN: Multi-variable type inference failed */
    static {
        Pools.SoftReferencePool softReferencePoolCreateSoftReferencePool = Pools.createSoftReferencePool(new Pools.Manager<StringWriter>() { // from class: miuix.core.util.IOUtils.3
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // miuix.core.util.Pools.Manager
            public StringWriter createInstance() {
                return new StringWriter();
            }

            @Override // miuix.core.util.Pools.Manager
            public void onRelease(StringWriter stringWriter) {
                stringWriter.getBuffer().setLength(0);
            }
        }, 2);
        STRING_WRITER_POOL = softReferencePoolCreateSoftReferencePool;
        StringWriter stringWriter = (StringWriter) softReferencePoolCreateSoftReferencePool.acquire();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println();
        printWriter.flush();
        LINE_SEPARATOR = stringWriter.toString();
        printWriter.close();
        softReferencePoolCreateSoftReferencePool.release(stringWriter);
    }

    protected IOUtils() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    public static void closeQuietly(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException unused) {
            }
        }
    }

    public static void closeQuietly(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException unused) {
            }
        }
    }

    public static void closeQuietly(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException unused) {
            }
        }
    }

    public static void closeQuietly(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.flush();
            } catch (IOException unused) {
            }
            try {
                outputStream.close();
            } catch (IOException unused2) {
            }
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        Pools.Pool<ByteArrayOutputStream> pool = BYTE_ARRAY_OUTPUT_STREAM_POOL;
        ByteArrayOutputStream byteArrayOutputStreamAcquire = pool.acquire();
        copy(inputStream, byteArrayOutputStreamAcquire);
        byte[] byteArray = byteArrayOutputStreamAcquire.toByteArray();
        pool.release(byteArrayOutputStreamAcquire);
        return byteArray;
    }

    public static byte[] toByteArray(Reader reader) throws IOException {
        Pools.Pool<ByteArrayOutputStream> pool = BYTE_ARRAY_OUTPUT_STREAM_POOL;
        ByteArrayOutputStream byteArrayOutputStreamAcquire = pool.acquire();
        copy(reader, byteArrayOutputStreamAcquire);
        byte[] byteArray = byteArrayOutputStreamAcquire.toByteArray();
        pool.release(byteArrayOutputStreamAcquire);
        return byteArray;
    }

    public static byte[] toByteArray(Reader reader, String str) throws IOException {
        Pools.Pool<ByteArrayOutputStream> pool = BYTE_ARRAY_OUTPUT_STREAM_POOL;
        ByteArrayOutputStream byteArrayOutputStreamAcquire = pool.acquire();
        copy(reader, byteArrayOutputStreamAcquire, str);
        byte[] byteArray = byteArrayOutputStreamAcquire.toByteArray();
        pool.release(byteArrayOutputStreamAcquire);
        return byteArray;
    }

    public static char[] toCharArray(InputStream inputStream) throws IOException {
        Pools.Pool<CharArrayWriter> pool = CHAR_ARRAY_WRITER_POOL;
        CharArrayWriter charArrayWriterAcquire = pool.acquire();
        copy(inputStream, charArrayWriterAcquire);
        char[] charArray = charArrayWriterAcquire.toCharArray();
        pool.release(charArrayWriterAcquire);
        return charArray;
    }

    public static char[] toCharArray(InputStream inputStream, String str) throws IOException {
        Pools.Pool<CharArrayWriter> pool = CHAR_ARRAY_WRITER_POOL;
        CharArrayWriter charArrayWriterAcquire = pool.acquire();
        copy(inputStream, charArrayWriterAcquire, str);
        char[] charArray = charArrayWriterAcquire.toCharArray();
        pool.release(charArrayWriterAcquire);
        return charArray;
    }

    public static char[] toCharArray(Reader reader) throws IOException {
        Pools.Pool<CharArrayWriter> pool = CHAR_ARRAY_WRITER_POOL;
        CharArrayWriter charArrayWriterAcquire = pool.acquire();
        copy(reader, charArrayWriterAcquire);
        char[] charArray = charArrayWriterAcquire.toCharArray();
        pool.release(charArrayWriterAcquire);
        return charArray;
    }

    public static String toString(InputStream inputStream) throws IOException {
        Pools.Pool<StringWriter> pool = STRING_WRITER_POOL;
        StringWriter stringWriterAcquire = pool.acquire();
        copy(inputStream, stringWriterAcquire);
        String string = stringWriterAcquire.toString();
        pool.release(stringWriterAcquire);
        return string;
    }

    public static String toString(InputStream inputStream, String str) throws IOException {
        Pools.Pool<StringWriter> pool = STRING_WRITER_POOL;
        StringWriter stringWriterAcquire = pool.acquire();
        copy(inputStream, stringWriterAcquire, str);
        String string = stringWriterAcquire.toString();
        pool.release(stringWriterAcquire);
        return string;
    }

    public static String toString(Reader reader) throws IOException {
        Pools.Pool<StringWriter> pool = STRING_WRITER_POOL;
        StringWriter stringWriterAcquire = pool.acquire();
        copy(reader, stringWriterAcquire);
        String string = stringWriterAcquire.toString();
        pool.release(stringWriterAcquire);
        return string;
    }

    public static List<String> readLines(InputStream inputStream) throws IOException {
        return readLines(new InputStreamReader(inputStream));
    }

    public static List<String> readLines(InputStream inputStream, String str) throws IOException {
        InputStreamReader inputStreamReader;
        if (str == null || str.length() == 0) {
            inputStreamReader = new InputStreamReader(inputStream);
        } else {
            inputStreamReader = new InputStreamReader(inputStream, str);
        }
        return readLines(inputStreamReader);
    }

    public static List<String> readLines(Reader reader) throws IOException {
        BufferedReader bufferedReader = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
        ArrayList arrayList = new ArrayList();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                return arrayList;
            }
            arrayList.add(line);
        }
    }

    public static InputStream toInputStream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    public static InputStream toInputStream(String str, String str2) throws UnsupportedEncodingException {
        return new ByteArrayInputStream((str2 == null || str2.length() == 0) ? str.getBytes() : str.getBytes(str2));
    }

    public static void write(OutputStream outputStream, byte[] bArr) throws IOException {
        if (bArr != null) {
            outputStream.write(bArr);
        }
    }

    public static void write(Writer writer, byte[] bArr) throws IOException {
        if (bArr != null) {
            writer.write(new String(bArr));
        }
    }

    public static void write(Writer writer, byte[] bArr, String str) throws IOException {
        if (bArr != null) {
            writer.write((str == null || str.length() == 0) ? new String(bArr) : new String(bArr, str));
        }
    }

    public static void write(Writer writer, char[] cArr) throws IOException {
        if (cArr != null) {
            writer.write(cArr);
        }
    }

    public static void write(OutputStream outputStream, char[] cArr) throws IOException {
        if (cArr != null) {
            outputStream.write(new String(cArr).getBytes());
        }
    }

    public static void write(OutputStream outputStream, char[] cArr, String str) throws IOException {
        byte[] bytes;
        if (cArr != null) {
            if (str == null || str.length() == 0) {
                bytes = new String(cArr).getBytes();
            } else {
                bytes = new String(cArr).getBytes(str);
            }
            outputStream.write(bytes);
        }
    }

    public static void write(Writer writer, String str) throws IOException {
        if (str != null) {
            writer.write(str);
        }
    }

    public static void write(OutputStream outputStream, String str) throws IOException {
        if (str != null) {
            outputStream.write(str.getBytes());
        }
    }

    public static void write(OutputStream outputStream, String str, String str2) throws IOException {
        if (str != null) {
            outputStream.write((str2 == null || str2.length() == 0) ? str.getBytes() : str.getBytes(str2));
        }
    }

    public static void writeLines(OutputStream outputStream, Collection<Object> collection, String str) throws IOException {
        if (collection == null) {
            return;
        }
        if (str == null) {
            str = LINE_SEPARATOR;
        }
        for (Object obj : collection) {
            if (obj != null) {
                outputStream.write(obj.toString().getBytes());
            }
            outputStream.write(str.getBytes());
        }
    }

    public static void writeLines(OutputStream outputStream, Collection<Object> collection, String str, String str2) throws IOException {
        if (collection == null) {
            return;
        }
        if (str == null) {
            str = LINE_SEPARATOR;
        }
        for (Object obj : collection) {
            if (obj != null) {
                outputStream.write(obj.toString().getBytes(str2));
            }
            outputStream.write(str.getBytes(str2));
        }
    }

    public static void writeLines(Writer writer, Collection<Object> collection, String str) throws IOException {
        if (collection == null) {
            return;
        }
        if (str == null) {
            str = LINE_SEPARATOR;
        }
        for (Object obj : collection) {
            if (obj != null) {
                writer.write(obj.toString());
            }
            writer.write(str);
        }
    }

    public static long copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] byteArrayBuffer = getByteArrayBuffer();
        long j = 0;
        while (true) {
            int i = inputStream.read(byteArrayBuffer);
            if (i != -1) {
                outputStream.write(byteArrayBuffer, 0, i);
                j += (long) i;
            } else {
                outputStream.flush();
                return j;
            }
        }
    }

    public static void copy(InputStream inputStream, Writer writer) throws IOException {
        copy(new InputStreamReader(inputStream), writer);
    }

    public static void copy(InputStream inputStream, Writer writer, String str) throws IOException {
        InputStreamReader inputStreamReader;
        if (str == null || str.length() == 0) {
            inputStreamReader = new InputStreamReader(inputStream);
        } else {
            inputStreamReader = new InputStreamReader(inputStream, str);
        }
        copy(inputStreamReader, writer);
    }

    public static void copy(Reader reader, OutputStream outputStream) throws IOException {
        copy(reader, new OutputStreamWriter(outputStream));
    }

    public static void copy(Reader reader, OutputStream outputStream, String str) throws IOException {
        OutputStreamWriter outputStreamWriter;
        if (str == null || str.length() == 0) {
            outputStreamWriter = new OutputStreamWriter(outputStream);
        } else {
            outputStreamWriter = new OutputStreamWriter(outputStream, str);
        }
        copy(reader, outputStreamWriter);
    }

    public static long copy(Reader reader, Writer writer) throws IOException {
        char[] charArrayBuffer = getCharArrayBuffer();
        long j = 0;
        while (true) {
            int i = reader.read(charArrayBuffer);
            if (i != -1) {
                writer.write(charArrayBuffer, 0, i);
                j += (long) i;
            } else {
                writer.flush();
                return j;
            }
        }
    }

    private static byte[] getByteArrayBuffer() {
        ThreadLocal<SoftReference<byte[]>> threadLocal = THREAD_LOCAL_BYTE_BUFFER;
        SoftReference<byte[]> softReference = threadLocal.get();
        byte[] bArr = softReference != null ? softReference.get() : null;
        if (bArr != null) {
            return bArr;
        }
        byte[] bArr2 = new byte[4096];
        threadLocal.set(new SoftReference<>(bArr2));
        return bArr2;
    }

    private static char[] getCharArrayBuffer() {
        ThreadLocal<SoftReference<char[]>> threadLocal = THREAD_LOCAL_CHAR_BUFFER;
        SoftReference<char[]> softReference = threadLocal.get();
        char[] cArr = softReference != null ? softReference.get() : null;
        if (cArr != null) {
            return cArr;
        }
        char[] cArr2 = new char[4096];
        threadLocal.set(new SoftReference<>(cArr2));
        return cArr2;
    }
}
