package miuix.internal.log.format;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Calendar;
import miuix.internal.log.Level;
import miuix.internal.log.message.Message;

/* JADX INFO: loaded from: classes2.dex */
public class SimpleFormatter implements Formatter {
    private static final String DEFAULT_DELIMITER = " - ";
    private ThreadLocal<ThreadCache> mThreadCache = new ThreadLocal<ThreadCache>() { // from class: miuix.internal.log.format.SimpleFormatter.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public ThreadCache initialValue() {
            return new ThreadCache();
        }
    };

    @Override // miuix.internal.log.format.Formatter
    public String format(String str, String str2, long j, Level level, String str3, Throwable th) {
        return doFormat(str, str2, j, level, str3, null, th);
    }

    @Override // miuix.internal.log.format.Formatter
    public String format(String str, String str2, long j, Level level, Message message) {
        return doFormat(str, str2, j, level, null, message, message.getThrowable());
    }

    private String doFormat(String str, String str2, long j, Level level, String str3, Message message, Throwable th) {
        ThreadCache threadCache = this.mThreadCache.get();
        StringBuilder sb = threadCache.out;
        sb.setLength(0);
        threadCache.dateFormat.format(sb, j);
        sb.append(DEFAULT_DELIMITER);
        sb.append('[').append(level.name()).append("::").append(str).append(']');
        sb.append(DEFAULT_DELIMITER);
        sb.append(str2).append(": ");
        if (str3 == null) {
            message.format(sb);
        } else {
            sb.append(str3);
        }
        sb.append('\n');
        if (th != null) {
            th.printStackTrace(threadCache.printer);
        }
        if (sb.length() > 8192) {
            sb.setLength(8192);
            sb.trimToSize();
        }
        return sb.toString();
    }

    private static class StringBuilderWriter extends Writer {
        private StringBuilder iOut;

        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }

        @Override // java.io.Writer, java.io.Flushable
        public void flush() throws IOException {
        }

        public StringBuilderWriter(StringBuilder sb) {
            this.iOut = sb;
        }

        @Override // java.io.Writer
        public void write(char[] cArr, int i, int i2) throws IOException {
            checkOffsetAndCount(cArr.length, i, i2);
            if (i2 == 0) {
                return;
            }
            this.iOut.append(cArr, i, i2);
        }

        private static void checkOffsetAndCount(int i, int i2, int i3) {
            if ((i2 | i3) < 0 || i2 > i || i - i2 < i3) {
                throw new ArrayIndexOutOfBoundsException("length=" + i + "; regionStart=" + i2 + "; regionLength=" + i3);
            }
        }
    }

    private static class ThreadCache {
        CachedDateFormat dateFormat = new CachedDateFormat();
        StringBuilder out = new StringBuilder();
        PrintWriter printer = new PrintWriter(new StringBuilderWriter(this.out));

        ThreadCache() {
        }
    }

    private static class CachedDateFormat {
        private static final int MILLISECONDS_RESERVE_OFFSET = 3;
        private static final int MINUTE_IN_MILLISECONDS = 60000;
        private static final int SECONDS_RESERVE_OFFSET = 6;
        private static final int SECOND_IN_MILLISECONDS = 1000;
        private StringBuilder iCache;
        private long iCacheTime;
        private Calendar iCalendar;
        private long iMillisecondsSlot;
        private long iSecondsSlot;

        private CachedDateFormat() {
            this.iCache = new StringBuilder();
            this.iCalendar = Calendar.getInstance();
        }

        public void format(StringBuilder sb, long j) {
            if (j != this.iCacheTime) {
                long j2 = this.iSecondsSlot;
                if (j2 == 0 || j < j2 || j >= j2 + 60000) {
                    buildCache(j);
                    this.iSecondsSlot = (j / 60000) * 60000;
                    this.iMillisecondsSlot = (j / 1000) * 1000;
                } else {
                    long j3 = this.iMillisecondsSlot;
                    if (j < j3 || j >= 1000 + j3) {
                        int i = (int) (j - j2);
                        int i2 = i / 1000;
                        int i3 = i2 * 1000;
                        StringBuilder sb2 = this.iCache;
                        sb2.setLength(sb2.length() - 6);
                        appendInt(this.iCache, i2, 2).append(',');
                        appendInt(this.iCache, i - i3, 3);
                        this.iMillisecondsSlot = this.iSecondsSlot + ((long) i3);
                    } else {
                        StringBuilder sb3 = this.iCache;
                        sb3.setLength(sb3.length() - 3);
                        appendInt(this.iCache, (int) (j - j3), 3);
                    }
                }
                this.iCacheTime = j;
            }
            sb.append((CharSequence) this.iCache);
        }

        private void buildCache(long j) {
            this.iCache.setLength(0);
            this.iCalendar.setTimeInMillis(j);
            this.iCache.append(this.iCalendar.get(1)).append('-');
            appendInt(this.iCache, this.iCalendar.get(2) + 1, 2).append('-');
            appendInt(this.iCache, this.iCalendar.get(5), 2).append(' ');
            appendInt(this.iCache, this.iCalendar.get(11), 2).append(':');
            appendInt(this.iCache, this.iCalendar.get(12), 2).append(':');
            appendInt(this.iCache, this.iCalendar.get(13), 2).append(',');
            appendInt(this.iCache, this.iCalendar.get(14), 3);
        }

        private StringBuilder appendInt(StringBuilder sb, int i, int i2) {
            if (i2 == 2) {
                if (i < 10) {
                    sb.append('0');
                }
                sb.append(i);
            } else if (i2 == 3) {
                if (i < 10) {
                    sb.append("00");
                } else if (i < 100) {
                    sb.append('0');
                }
                sb.append(i);
            } else {
                sb.append(i);
            }
            return sb;
        }
    }
}
