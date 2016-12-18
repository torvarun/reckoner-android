package net.snapshotsofharmony.reckoner;

import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Varun on 2016-12-13.
 *
 * Class to handle API calls to The Reckoner's JSON feed.
 */

public class API {

    private static final String URL = "http://www.thereckoner.ca/wp-json/posts?page=";
    private static final String CATEGORY = "&filter[category_name]=";
    private static final String TAG = "API";
    private static OkHttpClient client = new OkHttpClient();

    /**
     * Retrieves articles from The Reckoner's API. AsyncTask is executed synchronously.
     * @param page
     * @param category
     * @return ArrayList containing article objects.
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static ArrayList<Article> getArticles(int page, String category) throws JSONException, IOException, InterruptedException, ExecutionException{
        String resp = new GetArticles().execute(URL + page + CATEGORY + category).get(); //Make the call

        if(resp == null) {
            Log.v(TAG, "resp is null");
            return null;
        }

        JSONArray json;
        json = new JSONArray(resp);

        if(json == null){
            Log.v(TAG, "json parse error");
            return null;
        }

        ArrayList<Article> articles = new ArrayList<>();

        for(int i = 0; i < json.length(); i++){
            JSONObject current = json.getJSONObject(i);

            if(current != null) {
                String title = current.getString("title").replace("&nbsp;", ""); //Remove gibberish
                String author = current.getJSONObject("author").getString("name");
                String desc = current.getString("excerpt");
                desc = desc.substring(3, desc.length() - 5).replace("&nbsp;", ""); //Removes the html tags

                //CHeck the sdk due to Html.fromHtml(String) being deprecated
                if (Build.VERSION.SDK_INT >= 24) {
                    title = Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY).toString();
                    desc = Html.fromHtml(desc, Html.FROM_HTML_MODE_LEGACY).toString();
                } else{
                    title = Html.fromHtml(title).toString();
                    desc = Html.fromHtml(desc).toString();
                }

                String image;
                try {
                    image = current.getJSONObject("featured_image").getString("guid");
                }catch (Exception e){
                    image = "";
                    e.printStackTrace();
                }
                //String image = current.getString("guid");

                Log.v(TAG, "image " + i + ": " + image);
                articles.add(new Article(title, author, desc, image));
            }
        }

        return articles;
    }

    static class GetArticles extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... requests) {
            String result;
            try{
                Request request = new Request.Builder()
                        .url(requests[0])
                        .build();

                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.v(TAG, result);
            }catch (IOException e){
                result = null;
                e.printStackTrace();
            }

            return result;
        }
    }
}
