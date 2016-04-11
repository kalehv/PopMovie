package me.kalehv.popmovie.models;

import android.net.Uri;

/**
 * Created by harshadkale on 4/9/16.
 */
public class ThumbnailItem {
    private Uri imageUri;
    private String title;

    public ThumbnailItem(Uri poster, String title) {
        this.imageUri = poster;
        this.title = title;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
