package ca.thereckoner.thereckoner;

import android.app.Application;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import java.io.File;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by torvarun on 2017-07-17.
 */

public class ReckonerApp extends Application {

  @Override public void onCreate() {
    super.onCreate();

    //Create picasso singleton instance so it will cache images that is has already downloaded
    Cache cache = new Cache(new File(this.getCacheDir(), "picasso_cache"), 10 * 1000 * 1000); //10MB cache
    OkHttpClient client = new OkHttpClient.Builder()
        .cache(cache)
        .build();

    Picasso picasso = new Picasso.Builder(this)
        .downloader(new OkHttp3Downloader(client))

        .build();
    picasso.setIndicatorsEnabled(true);
    picasso.setLoggingEnabled(true);
    Picasso.setSingletonInstance(picasso);
  }
}
