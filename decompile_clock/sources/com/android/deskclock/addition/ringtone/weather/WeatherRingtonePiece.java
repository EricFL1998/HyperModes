package com.android.deskclock.addition.ringtone.weather;

import android.media.MediaFormat;
import com.android.deskclock.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class WeatherRingtonePiece {
    private static final int DEFAULT_PCM_ENCODING = 16;
    public int bitrate;
    public int channels;
    public byte[] data;
    public String mime;
    public String path;
    public int pcmEncoding;
    public int sampleRate;
    public long time;
    public long time2;
    public String type;

    public long getTime(int i) {
        if (i == 1) {
            return this.time;
        }
        return this.time2;
    }

    public void setMediaFormatInfo(MediaFormat mediaFormat) {
        if (mediaFormat != null) {
            this.pcmEncoding = 16;
            this.sampleRate = mediaFormat.getInteger("sample-rate");
            this.channels = mediaFormat.getInteger("channel-count");
            this.mime = mediaFormat.getString("mime");
            if (mediaFormat.containsKey("bitrate")) {
                this.bitrate = mediaFormat.getInteger("bitrate");
            } else {
                this.bitrate = 0;
            }
            Log.i("AudioUtil: setMediaFormatInfo; sampleRate=" + this.sampleRate + ", channels=" + this.channels + ", bitrate=" + this.bitrate + ", mm=" + this.mime + ", audioFormat=" + this.pcmEncoding);
            return;
        }
        Log.e("WeatherRingtonePiece setMediaFormatInfo error, null format");
    }
}
