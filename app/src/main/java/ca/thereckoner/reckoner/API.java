package ca.thereckoner.reckoner;

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
 * Created by Varun Venkataramanan on 2016-12-13.
 *
 * Class to handle API calls to The Reckoner's JSON feed.
 */

public class API {

    private static final String URL = "http://www.thereckoner.ca/wp-json/posts?page=";
    private static final String PARAM_CATEGORY = "&filter[category_name]=";
    private static final String PARAM_POST_LIMIT = "&filter[posts_per_page]=";
    private static final String TAG = "API";
    private static OkHttpClient client = new OkHttpClient();

    /**
     * Retrieves articles from The Reckoner's API. AsyncTask is executed synchronously.
     * @param page Page on feed
     * @param category Category of articles wanted. Most be from Strings.xml
     * @return ArrayList containing article objects.
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static ArrayList<Article> getArticles(int page, String category) throws JSONException, IOException, InterruptedException, ExecutionException{
        String resp = new GetArticles().execute(URL + page + PARAM_CATEGORY + category).get(); //Make the call synchronously

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

                String content = "<!DOCTYPE html><html><head><style>" +
                        "body{margin: 0; padding: 0;}" +
                        "p{font-family: Constantia, \"Book Antiqua\", Cambria, serif;font-size: 14px;line-height: 1.5;}" +
                        "div[id^=\"attachment\"],img{max-width: 100%;height: auto;}" +
                        "a[href^=\"http://thereckoner.ca/wp-content/uploads/\"]{pointer-events: none;cursor: default;}" +
                        "</style><body>" + current.get("content") + "</body></html>";

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
                    //e.printStackTrace(); //No need to print the stacktrace
                }
                //String image = current.getString("guid");

                String url = current.getString("link");

                articles.add(new Article(title, author, desc, image, content, url));
            }
        }

        return articles;
    }

    /**
     * Retrieves articles from The Reckoner's API. AsyncTask is executed synchronously.
     * @param page Page on feed
     * @param category Category of articles wanted. Most be from Strings.xml
     * @param numPosts Number of posts to be returned from the API call
     * @return ArrayList containing article objects.
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static ArrayList<Article> getArticles(int page, String category, int numPosts) throws JSONException, IOException, InterruptedException, ExecutionException{
        String resp = new GetArticles().execute(URL + page + PARAM_CATEGORY + category + PARAM_POST_LIMIT + numPosts).get(); //Make the call synchronously

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

                String content = "<!DOCTYPE html><html><head><style>" +
                        "body{margin: 0; padding: 0;}" +
                        "p{font-family: Constantia, \"Book Antiqua\", Cambria, serif;font-size: 14px;line-height: 1.5;}" +
                        "div[id^=\"attachment\"],img{max-width: 100%;height: auto;}" +
                        "a[href^=\"http://thereckoner.ca/wp-content/uploads/\"]{pointer-events: none;cursor: default;}" +
                        "</style><body>" + current.get("content") + "</body></html>";

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
                    //e.printStackTrace(); //No need to print the stacktrace
                }
                //String image = current.getString("guid");

                String url = current.getString("link");

                articles.add(new Article(title, author, desc, image, content, url));
            }
        }

        return articles;
    }

    /**
     * Static AsyncTask to handle getting articles
     */
    private static class GetArticles extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... requests) {
            String result;
            try{
                Request request = new Request.Builder()
                        .url(requests[0])
                        .build();

                Response response = client.newCall(request).execute();
                result = response.body().string();
            }catch (IOException e){
                result = null;
                e.printStackTrace();
            }

            return result;
        }
    }
}
