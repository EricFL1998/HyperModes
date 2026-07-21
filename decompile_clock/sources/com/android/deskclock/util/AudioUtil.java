package com.android.deskclock.util;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.text.TextUtils;
import android.view.Surface;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtonePiece;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class AudioUtil {
    private static final String TAG = "AudioUtil: ";
    private static final int TIMEOUT_US = 1000;
    private static final int WAVE_AUDIO_SIZE_OFFSET = 44;
    private static final int WAVE_DATA_SIZE_OFFSET = 8;
    private static final int WAVE_HEADER_LENGTH = 44;
    private static boolean sSawInputEOS = false;
    private static boolean sSawOutputEOS = false;

    public static String getDynamicAlarmResource(ArrayList<WeatherRingtonePiece> arrayList) {
        if (arrayList == null && arrayList.size() == 0) {
            Log.e("getDynamicAlarmResource error, null data");
            return null;
        }
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            WeatherRingtonePiece weatherRingtonePiece = arrayList.get(i);
            try {
                if (TextUtils.isEmpty(weatherRingtonePiece.path)) {
                    Log.i("AudioUtil: getDynamicAlarmResource, null path, type=" + weatherRingtonePiece.type);
                    return null;
                }
                File file = new File(Util.getFilesDir() + File.separator + weatherRingtonePiece.type + ".wav");
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                MediaExtractor mediaExtractor = new MediaExtractor();
                try {
                    mediaExtractor.setDataSource(weatherRingtonePiece.path);
                    MediaFormat trackFormat = mediaExtractor.getTrackFormat(0);
                    weatherRingtonePiece.setMediaFormatInfo(trackFormat);
                    byte[] bArrDecodeToPCM = decodeToPCM(mediaExtractor, trackFormat, weatherRingtonePiece.mime, file);
                    mediaExtractor.release();
                    if (bArrDecodeToPCM != null && bArrDecodeToPCM.length > 0) {
                        arrayList2.add(bArrDecodeToPCM);
                        weatherRingtonePiece.data = bArrDecodeToPCM;
                    }
                } catch (Exception e) {
                    Log.e("AudioUtil:  decode error:" + weatherRingtonePiece.path, e);
                    return null;
                }
            } catch (Exception e2) {
                Log.e("AudioUtil: getDynamicResource error type=" + weatherRingtonePiece.type, e2);
                return null;
            }
        }
        return mergeWav(arrayList);
    }

    private static byte[] decodeToPCM(MediaExtractor mediaExtractor, MediaFormat mediaFormat, String str, File file) {
        FileOutputStream fileOutputStream;
        try {
            MediaCodec mediaCodecCreateDecoderByType = MediaCodec.createDecoderByType(str);
            mediaCodecCreateDecoderByType.configure(mediaFormat, (Surface) null, (MediaCrypto) null, 0);
            mediaCodecCreateDecoderByType.start();
            ByteBuffer[] inputBuffers = mediaCodecCreateDecoderByType.getInputBuffers();
            ByteBuffer[] outputBuffers = mediaCodecCreateDecoderByType.getOutputBuffers();
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mediaExtractor.selectTrack(0);
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                Log.e("AudioUtil: File not exception", e);
                fileOutputStream = null;
            }
            sSawOutputEOS = false;
            sSawInputEOS = false;
            ArrayList<byte[]> arrayList = new ArrayList();
            arrayList.clear();
            int length = 0;
            while (!sSawOutputEOS) {
                if (!sSawInputEOS) {
                    input(mediaExtractor, mediaCodecCreateDecoderByType, inputBuffers);
                }
                byte[] bArrOutput = output(mediaCodecCreateDecoderByType, fileOutputStream, outputBuffers, bufferInfo);
                if (bArrOutput != null && bArrOutput.length > 0) {
                    arrayList.add((byte[]) bArrOutput.clone());
                    length += bArrOutput.length;
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e2) {
                    Log.e("AudioUtil: close outputstream exception", e2);
                }
            }
            byte[] bArr = new byte[length];
            int length2 = 0;
            for (byte[] bArr2 : arrayList) {
                int length3 = bArr2.length + length2 > length ? length - length2 : bArr2.length;
                if (length3 == 0) {
                    break;
                }
                System.arraycopy(bArr2, 0, bArr, length2, length3);
                length2 += bArr2.length;
            }
            Log.i("AudioUtil: mergeWav decodeToPCM=" + length);
            return bArr;
        } catch (Exception e3) {
            Log.e("AudioUtil: decodeToPCM exception", e3);
            return null;
        }
    }

    private static void input(MediaExtractor mediaExtractor, MediaCodec mediaCodec, ByteBuffer[] byteBufferArr) {
        long sampleTime;
        int i;
        int iDequeueInputBuffer = mediaCodec.dequeueInputBuffer(1000L);
        if (iDequeueInputBuffer >= 0) {
            int sampleData = mediaExtractor.readSampleData(byteBufferArr[iDequeueInputBuffer], 0);
            if (sampleData < 0) {
                sSawInputEOS = true;
                sampleTime = 0;
                i = 0;
            } else {
                sampleTime = mediaExtractor.getSampleTime();
                i = sampleData;
            }
            mediaCodec.queueInputBuffer(iDequeueInputBuffer, 0, i, sampleTime, sSawInputEOS ? 4 : 0);
            if (sSawInputEOS) {
                return;
            }
            mediaExtractor.advance();
        }
    }

    private static byte[] output(MediaCodec mediaCodec, FileOutputStream fileOutputStream, ByteBuffer[] byteBufferArr, MediaCodec.BufferInfo bufferInfo) {
        int iDequeueOutputBuffer = mediaCodec.dequeueOutputBuffer(bufferInfo, 1000L);
        if (iDequeueOutputBuffer < 0) {
            if (iDequeueOutputBuffer == -3) {
                mediaCodec.getOutputBuffers();
                return null;
            }
            if (iDequeueOutputBuffer != -2) {
                return null;
            }
            mediaCodec.getOutputFormat();
            return null;
        }
        ByteBuffer byteBuffer = byteBufferArr[iDequeueOutputBuffer];
        int i = bufferInfo.size;
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr);
        byteBuffer.clear();
        if (i > 0) {
            try {
                fileOutputStream.write(bArr);
            } catch (IOException e) {
                Log.e("AudioUtil: write data exception", e);
            }
        }
        mediaCodec.releaseOutputBuffer(iDequeueOutputBuffer, false);
        if ((bufferInfo.flags & 4) != 0) {
            sSawOutputEOS = true;
        }
        return bArr;
    }

    /* JADX WARN: Code duplicated, block: B:19:0x0096  */
    /* JADX WARN: Code duplicated, block: B:22:0x00c6  */
    /* JADX WARN: Code duplicated, block: B:46:0x016b A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:47:0x016d  */
    /* JADX WARN: Code duplicated, block: B:48:0x017c  */
    /* JADX WARN: Code duplicated, block: B:58:0x019c A[EDGE_INSN: B:58:0x019c->B:51:0x019c BREAK  A[LOOP:0: B:21:0x00c4->B:50:0x0198], SYNTHETIC] */
    public static String mergeWav(ArrayList<WeatherRingtonePiece> arrayList) {
        int byteCount;
        int byteCount2;
        int byteCount3;
        int i;
        int i2;
        byte[] bArr;
        int i3;
        int i4;
        long time;
        int i5;
        long time2;
        if (arrayList == null || arrayList.size() == 0) {
            Log.e("AudioUtil: mergeWav error, null data");
            return null;
        }
        WeatherRingtonePiece weatherRingtonePiece = arrayList.get(0);
        int size = arrayList.size();
        Log.i("AudioUtil: mergeWav inputs.get(0).time2=" + arrayList.get(0).time2);
        char c = arrayList.get(0).time2 == 0 ? (char) 1 : (char) 2;
        if (c == 1) {
            int i6 = size - 1;
            byteCount = getByteCount(arrayList.get(0).time, arrayList.get(i6).time, weatherRingtonePiece);
            byte[] bArr2 = arrayList.get(i6).data;
            if (bArr2 != null) {
                byteCount2 = bArr2.length;
            }
            if (byteCount % 2 != 0) {
                byteCount++;
            }
            Log.d("AudioUtil: maxByteSize=" + byteCount);
            byte[] bArr3 = new byte[byteCount];
            byteCount3 = 44;
            System.arraycopy(getWaveHeader(byteCount - 8, weatherRingtonePiece.channels, weatherRingtonePiece.sampleRate, weatherRingtonePiece.pcmEncoding, byteCount - 44), 0, bArr3, 0, 44);
            i = 0;
            i2 = 1;
            int i7 = 44;
            while (i < size) {
                bArr = arrayList.get(i).data;
                if (bArr != null || bArr.length == 0) {
                    Log.e("mergeWav error, null data i=" + i);
                    i3 = i + 1;
                    if (i3 == size) {
                        if (i2 == 1) {
                            break;
                        }
                        int i8 = i2 + 1;
                        time = arrayList.get(0).getTime(i8);
                        i5 = i8;
                        i4 = 0;
                    } else {
                        long time3 = arrayList.get(i3).getTime(i2);
                        int i9 = i2;
                        i4 = i3;
                        time = time3;
                        i5 = i9;
                    }
                    byteCount3 = getByteCount(arrayList.get(0).time, time, weatherRingtonePiece);
                    i = i4;
                    i2 = i5;
                } else {
                    int length = bArr.length;
                    if (length + byteCount3 >= byteCount) {
                        int i10 = byteCount - byteCount3;
                        int i11 = i10 - 1;
                        length = i11 % 2 != 0 ? i10 - 2 : i11;
                    }
                    Log.d("AudioUtil:  mergeWav currentIndex=" + byteCount3 + ", totalLength=" + i7 + ", srcLength=" + length);
                    for (int i12 = 0; i12 < length; i12++) {
                        int i13 = byteCount3 + i12;
                        bArr3[i13] = (byte) (bArr3[i13] + bArr[i12]);
                    }
                    i7 = byteCount3 + length;
                    int i14 = i + 1;
                    if (i14 == size) {
                        if (c == 1 || i2 != 1) {
                            break;
                        }
                        int i15 = i2 + 1;
                        time2 = arrayList.get(0).getTime(i15);
                        i5 = i15;
                        i4 = 0;
                    } else {
                        long time4 = arrayList.get(i14).getTime(i2);
                        int i16 = i2;
                        i4 = i14;
                        time2 = time4;
                        i5 = i16;
                    }
                    byteCount3 = getByteCount(arrayList.get(0).time, time2, weatherRingtonePiece);
                    i = i4;
                    i2 = i5;
                }
            }
            String str = Util.getFilesDir() + File.separator + "temp.wav";
            Log.d("AudioUtil: mergeWav index =" + FileUtil.writeFile(str, bArr3) + ", path = " + str);
            return str;
        }
        int i17 = size - 1;
        byteCount = getByteCount(arrayList.get(0).time, arrayList.get(i17).time2, weatherRingtonePiece);
        byteCount2 = getByteCount(arrayList.get(i17).time, arrayList.get(0).time2, weatherRingtonePiece);
        byteCount += byteCount2;
        if (byteCount % 2 != 0) {
            byteCount++;
        }
        Log.d("AudioUtil: maxByteSize=" + byteCount);
        byte[] bArr4 = new byte[byteCount];
        byteCount3 = 44;
        System.arraycopy(getWaveHeader(byteCount - 8, weatherRingtonePiece.channels, weatherRingtonePiece.sampleRate, weatherRingtonePiece.pcmEncoding, byteCount - 44), 0, bArr4, 0, 44);
        i = 0;
        i2 = 1;
        int i18 = 44;
        while (i < size) {
            bArr = arrayList.get(i).data;
            if (bArr != null) {
            }
            Log.e("mergeWav error, null data i=" + i);
            i3 = i + 1;
            if (i3 == size) {
                if (i2 == 1) {
                    break;
                    break;
                }
                int i19 = i2 + 1;
                time = arrayList.get(0).getTime(i19);
                i5 = i19;
                i4 = 0;
            } else {
                long time5 = arrayList.get(i3).getTime(i2);
                int i20 = i2;
                i4 = i3;
                time = time5;
                i5 = i20;
            }
            byteCount3 = getByteCount(arrayList.get(0).time, time, weatherRingtonePiece);
            i = i4;
            i2 = i5;
        }
        String str2 = Util.getFilesDir() + File.separator + "temp.wav";
        Log.d("AudioUtil: mergeWav index =" + FileUtil.writeFile(str2, bArr4) + ", path = " + str2);
        return str2;
    }

    private static int getByteCount(long j, long j2, WeatherRingtonePiece weatherRingtonePiece) {
        int iCeil = (int) Math.ceil(((j2 - j) * ((long) getByteCountPerSecond(weatherRingtonePiece))) / 1000.0d);
        return iCeil % 2 != 0 ? iCeil + 1 : iCeil;
    }

    private static int getByteCountPerSecond(WeatherRingtonePiece weatherRingtonePiece) {
        return ((weatherRingtonePiece.sampleRate * weatherRingtonePiece.channels) * weatherRingtonePiece.pcmEncoding) / 8;
    }

    private static byte[] getWaveHeader(long j, int i, int i2, int i3, long j2) {
        long j3 = ((i2 * i) * i3) / 8;
        return new byte[]{82, 73, 70, 70, (byte) (j & 255), (byte) ((j >> 8) & 255), (byte) ((j >> 16) & 255), (byte) ((j >> 24) & 255), 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, (byte) i, 0, (byte) (i2 & 255), (byte) ((i2 >> 8) & 255), (byte) ((i2 >> 16) & 255), (byte) ((i2 >> 24) & 255), (byte) (j3 & 255), (byte) ((j3 >> 8) & 255), (byte) ((j3 >> 16) & 255), (byte) ((j3 >> 24) & 255), (byte) ((i * i3) / 8), 0, 16, 0, 100, 97, 116, 97, (byte) (j2 & 255), (byte) ((j2 >> 8) & 255), (byte) ((j2 >> 16) & 255), (byte) ((j2 >> 24) & 255)};
    }
}
