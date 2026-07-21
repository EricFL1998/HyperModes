package miuix.appcompat.app.floatingactivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import miuix.appcompat.app.floatingactivity.multiapp.IFloatingService;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;

/* JADX INFO: loaded from: classes2.dex */
public class MemoryFileUtil {
    public static final String KEY_FD = "parcelFile";
    public static final String KEY_HEIGHT = "key_height";
    public static final String KEY_LENGTH = "parcelFileLength";
    public static final String KEY_WIDTH = "key_width";
    private static final String TAG = "MemoryFileUtil";

    /* JADX WARN: Code duplicated, block: B:20:0x003f  */
    public static ParcelFileDescriptor writeToMemory(byte[] bArr, int i) throws Throwable {
        MemoryFile memoryFile;
        MemoryFile memoryFile2 = null;
        parcelFileDescriptorDup = null;
        ParcelFileDescriptor parcelFileDescriptorDup = null;
        try {
            memoryFile = new MemoryFile("", i);
            try {
                try {
                    memoryFile.writeBytes(bArr, 0, 0, i);
                    Method declaredMethod = MemoryFile.class.getDeclaredMethod("getFileDescriptor", new Class[0]);
                    declaredMethod.setAccessible(true);
                    parcelFileDescriptorDup = ParcelFileDescriptor.dup((FileDescriptor) declaredMethod.invoke(memoryFile, new Object[0]));
                } catch (Throwable th) {
                    th = th;
                    memoryFile2 = memoryFile;
                    if (memoryFile2 != null) {
                        memoryFile2.close();
                    }
                    throw th;
                }
            } catch (Exception e) {
                e = e;
                Log.w(TAG, "catch write to memory error", e);
                if (memoryFile != null) {
                }
                return parcelFileDescriptorDup;
            }
        } catch (Exception e2) {
            e = e2;
            memoryFile = null;
        } catch (Throwable th2) {
            th = th2;
            if (memoryFile2 != null) {
                memoryFile2.close();
            }
            throw th;
        }
        memoryFile.close();
        return parcelFileDescriptorDup;
    }

    public static void sendToFdServer(IFloatingService iFloatingService, byte[] bArr, int i, int i2, int i3, String str, int i4) throws Throwable {
        ParcelFileDescriptor parcelFileDescriptorWriteToMemory = writeToMemory(bArr, i);
        HashMap map = new HashMap(1);
        map.put(KEY_FD, parcelFileDescriptorWriteToMemory);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_FD, map);
        bundle.putInt(KEY_LENGTH, i);
        bundle.putInt(KEY_WIDTH, i2);
        bundle.putInt(KEY_HEIGHT, i3);
        bundle.putInt(MethodCodeHelper.KEY_TASK_ID, i4);
        bundle.putString(MethodCodeHelper.KEY_REQUEST_IDENTITY, str);
        if (iFloatingService != null) {
            try {
                iFloatingService.callServiceMethod(7, bundle);
            } catch (RemoteException e) {
                Log.w(TAG, "catch stash snapshot to service error", e);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:49:0x0052 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static byte[] readFromMemory(HashMap<String, ParcelFileDescriptor> map, int i) throws Throwable {
        FileInputStream fileInputStream;
        ParcelFileDescriptor parcelFileDescriptor = map.get(KEY_FD);
        FileInputStream fileInputStream2 = null;
        if (parcelFileDescriptor != null) {
            byte[] bArr = new byte[i];
            try {
                fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                try {
                    try {
                        fileInputStream.read(bArr);
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            Log.w(TAG, "catch close FileInputStream error", e);
                        }
                        try {
                            parcelFileDescriptor.close();
                        } catch (IOException e2) {
                            Log.w(TAG, "catch close fd error", e2);
                        }
                        return bArr;
                    } catch (Throwable th) {
                        th = th;
                        fileInputStream2 = fileInputStream;
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (IOException e3) {
                                Log.w(TAG, "catch close FileInputStream error", e3);
                            }
                            try {
                                parcelFileDescriptor.close();
                            } catch (IOException e4) {
                                Log.w(TAG, "catch close fd error", e4);
                            }
                        }
                        throw th;
                    }
                } catch (Exception e5) {
                    e = e5;
                    Log.w(TAG, "catch read from memory error", e);
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e6) {
                            Log.w(TAG, "catch close FileInputStream error", e6);
                        }
                        try {
                            parcelFileDescriptor.close();
                        } catch (IOException e7) {
                            Log.w(TAG, "catch close fd error", e7);
                        }
                    }
                    return null;
                }
            } catch (Exception e8) {
                e = e8;
                fileInputStream = null;
            } catch (Throwable th2) {
                th = th2;
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                    parcelFileDescriptor.close();
                }
                throw th;
            }
        }
        return null;
    }

    public static Bitmap readBitmap(Bundle bundle) throws Throwable {
        HashMap map = (HashMap) bundle.getSerializable(KEY_FD);
        int i = bundle.getInt(KEY_LENGTH);
        int i2 = bundle.getInt(KEY_WIDTH);
        int i3 = bundle.getInt(KEY_HEIGHT);
        byte[] fromMemory = readFromMemory(map, i);
        Bitmap bitmapCreateBitmap = null;
        if (fromMemory == null) {
            Log.d(TAG, "getSnapShot with data is null");
            return null;
        }
        try {
            bitmapCreateBitmap = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
            bitmapCreateBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(fromMemory));
            return bitmapCreateBitmap;
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "catch illegal argument exception", e);
            return bitmapCreateBitmap;
        } catch (OutOfMemoryError e2) {
            Log.w(TAG, "catch oom exception", e2);
            return bitmapCreateBitmap;
        }
    }
}
