package miuix.graphics.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.net.Uri;
import android.os.SystemClock;
import java.util.ArrayList;
import miuix.graphics.gif.DecodeGifImageHelper;
import miuix.graphics.gif.GifDecoder;
import miuix.io.ResettableInputStream;

/* JADX INFO: loaded from: classes2.dex */
public class GifAnimationDrawable extends AnimationDrawable {
    private int mCurrentFrame;
    private DrawableContainer.DrawableContainerState mDrawableContainerState;
    private Resources mResources;
    private final DecodeGifImageHelper mHelper = new DecodeGifImageHelper();
    private ArrayList<Integer> mDurations = new ArrayList<>();
    private ArrayList<Integer> mOriginalDurations = new ArrayList<>();

    @Override // android.graphics.drawable.DrawableContainer
    public final boolean selectDrawable(int i) {
        preSelectDrawable(i);
        this.mCurrentFrame = i;
        return super.selectDrawable(i);
    }

    private void preSelectDrawable(int i) {
        if (this.mHelper.mFrames.isEmpty()) {
            return;
        }
        DecodeGifImageHelper.GifFrame gifFrame = this.mHelper.mFrames.get(0);
        if (this.mHelper.mFrames.size() > 1) {
            this.mHelper.mFrames.remove(0);
        }
        this.mHelper.decodeNextFrames();
        this.mDrawableContainerState.getChildren()[i] = new BitmapDrawable(this.mResources, gifFrame.mImage);
        this.mDurations.add(i, Integer.valueOf(gifFrame.mDuration));
    }

    private boolean handleFirstDecodeResult(DecodeGifImageHelper.GifDecodeResult gifDecodeResult) {
        if (gifDecodeResult.mGifDecoder == null || !gifDecodeResult.mIsDecodeOk) {
            return false;
        }
        GifDecoder gifDecoder = gifDecodeResult.mGifDecoder;
        this.mHelper.mDecodedAllFrames = gifDecoder.isDecodeToTheEnd();
        int frameCount = gifDecoder.getFrameCount();
        if (frameCount <= 0) {
            return false;
        }
        for (int i = 0; i < frameCount; i++) {
            if (this.mHelper.mDecodedAllFrames) {
                addFrame(new BitmapDrawable(this.mResources, gifDecoder.getFrame(i)), gifDecoder.getDelay(i));
            } else {
                this.mHelper.mFrames.add(new DecodeGifImageHelper.GifFrame(gifDecoder.getFrame(i), gifDecoder.getDelay(i), i));
            }
        }
        if (!this.mHelper.mDecodedAllFrames) {
            this.mHelper.firstDecodeNextFrames();
            DecodeGifImageHelper.GifFrame gifFrame = this.mHelper.mFrames.get(0);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(this.mResources, gifFrame.mImage);
            addFrame(bitmapDrawable, gifFrame.mDuration);
            addFrame(bitmapDrawable, gifFrame.mDuration);
        }
        setOneShot(false);
        super.selectDrawable(0);
        return true;
    }

    @Override // android.graphics.drawable.AnimationDrawable, android.graphics.drawable.DrawableContainer
    protected final void setConstantState(DrawableContainer.DrawableContainerState drawableContainerState) {
        super.setConstantState(drawableContainerState);
        this.mDrawableContainerState = drawableContainerState;
    }

    @Override // android.graphics.drawable.AnimationDrawable
    public final int getDuration(int i) {
        return this.mDurations.get(i).intValue();
    }

    @Override // android.graphics.drawable.AnimationDrawable
    public final void addFrame(Drawable drawable, int i) {
        super.addFrame(drawable, i);
        this.mDurations.add(Integer.valueOf(i));
        this.mOriginalDurations.add(Integer.valueOf(i));
    }

    @Override // android.graphics.drawable.Drawable
    public final void scheduleSelf(Runnable runnable, long j) {
        if (j == SystemClock.uptimeMillis() + ((long) this.mOriginalDurations.get(this.mCurrentFrame).intValue())) {
            j = SystemClock.uptimeMillis() + ((long) this.mDurations.get(this.mCurrentFrame).intValue());
        }
        super.scheduleSelf(runnable, j);
    }

    private boolean internalLoad(Resources resources, ResettableInputStream resettableInputStream) {
        this.mResources = resources;
        this.mHelper.mGifSource = resettableInputStream;
        return handleFirstDecodeResult(this.mHelper.decodeFrom(0));
    }

    public boolean load(Context context, Uri uri) {
        return internalLoad(context.getResources(), new ResettableInputStream(context, uri));
    }

    public boolean load(Context context, String str) {
        return internalLoad(context.getResources(), new ResettableInputStream(str));
    }

    public boolean load(Context context, AssetManager assetManager, String str) {
        return internalLoad(context.getResources(), new ResettableInputStream(assetManager, str));
    }

    public final void setMaxDecodeSize(long j) {
        this.mHelper.mMaxDecodeSize = j;
    }
}
