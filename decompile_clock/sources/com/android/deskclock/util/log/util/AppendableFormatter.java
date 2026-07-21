package com.android.deskclock.util.log.util;

import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class AppendableFormatter {
    private AppendableWrapper mAppendableWrapper;
    private Formatter mFormatter;

    public AppendableFormatter() {
        this(Locale.US);
    }

    public AppendableFormatter(Locale locale) {
        this.mAppendableWrapper = new AppendableWrapper();
        this.mFormatter = new Formatter(this.mAppendableWrapper, locale);
    }

    public void setAppendable(Appendable appendable) {
        this.mAppendableWrapper.setAppendable(appendable);
    }

    public AppendableFormatter format(String str, Object... objArr) {
        this.mFormatter.format(str, objArr);
        return this;
    }

    public AppendableFormatter format(Locale locale, String str, Object... objArr) {
        this.mFormatter.format(locale, str, objArr);
        return this;
    }

    private static class AppendableWrapper implements Appendable {
        private Appendable iAppendable;

        private AppendableWrapper() {
        }

        public void setAppendable(Appendable appendable) {
            this.iAppendable = appendable;
        }

        @Override // java.lang.Appendable
        public Appendable append(char c) throws IOException {
            this.iAppendable.append(c);
            return this;
        }

        @Override // java.lang.Appendable
        public Appendable append(CharSequence charSequence) throws IOException {
            this.iAppendable.append(charSequence);
            return this;
        }

        @Override // java.lang.Appendable
        public Appendable append(CharSequence charSequence, int i, int i2) throws IOException {
            this.iAppendable.append(charSequence, i, i2);
            return this;
        }
    }
}
