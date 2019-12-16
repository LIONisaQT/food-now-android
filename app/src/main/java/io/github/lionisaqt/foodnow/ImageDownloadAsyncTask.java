package io.github.lionisaqt.foodnow;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class ImageDownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private final String TAG = getClass().getSimpleName();

    AsyncResponse delegate = null;

    @Override
    protected Bitmap doInBackground(String... strings) {

        return null;
    }
}
