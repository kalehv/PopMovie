package me.kalehv.popmovie.utils;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by hk022893 on 4/11/16.
 */
public class PicassoPaletteTransformation implements Transformation {
    private static final PicassoPaletteTransformation INSTANCE = new PicassoPaletteTransformation();
    private static final Map<Bitmap, Palette> CACHE = new WeakHashMap<>();

    private PicassoPaletteTransformation() {}

    public static PicassoPaletteTransformation instance() {
        return INSTANCE;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Palette palette = Palette.from(source).generate();
        CACHE.put(source, palette);
        return source;
    }

    @Override
    public String key() {
        return "";
    }

    public static Palette getPalette(Bitmap bitmap) {
        return CACHE.get(bitmap);
    }
}
