package ca.thereckoner.reckoner.View;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.google.gson.Gson;

import ca.thereckoner.reckoner.API;
import ca.thereckoner.reckoner.Article;
import ca.thereckoner.reckoner.R;

import java.util.List;

/**
 * Created by Varun Venkataramanan.
 * Service Class to deliver notifications. Checks to see whether newest article has already had a notification delivered before sending a new one.
 */
public class NotificationService extends Service {
    private PowerManager.WakeLock mWakeLock;

    private static final String TAG = "NotificationService";

    public NotificationService() { //Empty Default Constructor
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; //No communication needed, all work is performed here
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(); //handle the intent
        return START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }

    private void handleIntent(){
        // obtain the wake lock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ServiceTag"); //CPU on, screen off
        mWakeLock.acquire();

        // check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!cm.getBackgroundDataSetting()) {
            stopSelf();
            return;
        }

        checkForNewPost(getApplicationContext()); //Check to see if there's a new pos
    }

    /**
     * Checks for a new post on the Reckoner API
     * @param context
     */
    private void checkForNewPost(Context context){
        Article newArticle;
        try {
            List articles = API.getArticles(1, "", 1); //1 page, no category, 1 article one
            newArticle = (Article) articles.get(0); //Get the first (and only) object from the arraylist
        }catch (Exception e){
            e.printStackTrace();
            newArticle = null;
        }

        Article oldArticle = getArticleFromPrefs(context); //Get the last notified article

        if((newArticle != null && oldArticle != null) && !newArticle.equals(oldArticle)){ //If the articles are different
            NewArticleNotification.notify(context, newArticle); //Display the notification
            saveArticleToPrefs(context, newArticle); //Save the newest article in cache
        }
    }

    /**
     * Save the article to shared prefs to access later. Used to check if the newest article should be pushed as a notification.
     * @param context Context of Prefs
     * @param article Article to Save
     */
    public void saveArticleToPrefs(Context context, Article article){
        //Get the prefs and setup the editor
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.sharedPrefsKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        //Save the article as a JSON String using GSON Library
        editor.putString(getString(R.string.articleParam), new Gson().toJson(article));

        //apply() commits the changes to the prefs in the background
        editor.apply();
    }

    /**
     * Accesses the article saved in the prefs
     * @param context Context of Prefs
     * @return Article if found; null otherwise
     */
    public Article getArticleFromPrefs(Context context){
        //Setup the prefs
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.sharedPrefsKey), Context.MODE_PRIVATE);
        //Get the json string with the fallback value as null
        String json = sharedPref.getString(getString(R.string.articleParam), null);

        if(json == null) { //If the json is null return so
            Log.v(TAG, "No article found in prefs");
            return null;
        }

        //Parse the json and return it
        Article article = new Gson().fromJson(json, Article.class);
        return article;
    }
}
