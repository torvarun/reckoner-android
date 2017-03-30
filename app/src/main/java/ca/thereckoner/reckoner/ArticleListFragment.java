package ca.thereckoner.reckoner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import ca.thereckoner.reckoner.View.InfiniteRecyclerViewScrollAdapter;
import ca.thereckoner.reckoner.View.OnItemClickListener;
import ca.thereckoner.reckoner.View.ArticleListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Varun Venkataramanan.
 *
 * Most used Fragment in the app. Displays posts based on the param category.
 * Uses the ArticleList Recycler View to display the articles.
 */
public class ArticleListFragment extends Fragment {
  private static final String ARG_PARAM1 = "ARG_CATEGORY_NAME";

  private String category;

  private OnFragmentInteractionListener interactionListener;

  private Handler handler;
  private final int INIT_LOAD_DELAY = 10000; //Delay to load the secondary items

  RecyclerView articleList; //The main recycler view
  LinearLayoutManager layoutManager; //Layout manager for the recycler view
  ArticleListAdapter articleListAdapter; //Adapter to display the data in the recycler view
  InfiniteRecyclerViewScrollAdapter scrollAdapter;

  ProgressBar loadingAnimation;

  int nextPage = 1; //Last page of articles loaded by the app

  private static final String TAG = "ArticleListFragment";

  private static final String URL = "http://www.thereckoner.ca/wp-json/posts?page=";
  private static final String PARAM_CATEGORY = "&filter[category_name]=";
  private static final String PARAM_POST_LIMIT = "&filter[posts_per_page]=";
  OkHttpClient client = new OkHttpClient();

  public ArticleListFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param category Parameter referring to the content being displayed in the fragment.
   * @return A new instance of fragment ArticleListFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static ArticleListFragment newInstance(String category) {
    ArticleListFragment fragment = new ArticleListFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, category);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      category = getArguments().getString(ARG_PARAM1);
    }

    handler = new Handler();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    View view =
        inflater.inflate(ca.thereckoner.reckoner.R.layout.fragment_article_list, container, false);

    articleList = (RecyclerView) view.findViewById(ca.thereckoner.reckoner.R.id.articleList);
    articleList.setHasFixedSize(true); //Size can't change

    layoutManager = new LinearLayoutManager(getContext());
    articleList.setLayoutManager(layoutManager);

    scrollAdapter = new InfiniteRecyclerViewScrollAdapter(layoutManager) {
      @Override public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
        loadFromAPI();
      }
    };
    articleList.addOnScrollListener(scrollAdapter);


    loadingAnimation = (ProgressBar) view.findViewById(R.id.loading_animation);

    return view;
  }

  // TODO: Rename method, update argument and hook method into UI event
  public void onButtonPressed(Uri uri) {
    if (interactionListener != null) {
      interactionListener.onFragmentInteraction(uri);
    }
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            interactionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
  }

  @Override public void onResume() {
    super.onResume();

    loadFromAPI();

    handler.postDelayed(new Runnable() {
      @Override public void run() {
        loadFromAPI();
      }
    }, INIT_LOAD_DELAY);

  }

  @Override public void onDetach() {
    super.onDetach();
    interactionListener = null;
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
  }

  /**
   * Loads more articles from the API.
   */
  private void loadFromAPI(){
    new GetArticles().execute(URL + nextPage + PARAM_CATEGORY + category);
    nextPage++;
  }

  /**
   * Static AsyncTask to handle getting articles
   */
  private class GetArticles extends AsyncTask<String, Void, String> {

    @Override protected String doInBackground(String... requests) {
      String result;
      try {
        Request request = new Request.Builder().url(requests[0]).build();

        Response response = client.newCall(request).execute();
        result = response.body().string();
      } catch (IOException e) {
        result = null;
        e.printStackTrace();
      }

      return result;
    }

    @Override protected void onPreExecute() {
      super.onPreExecute();

      if(articleListAdapter == null) {
        articleList.setVisibility(View.GONE);
        loadingAnimation.setVisibility(View.VISIBLE);
      }
    }

    @Override protected void onPostExecute(String s) {
      super.onPostExecute(s);

      try {
        if (s == null) {
          Log.v(TAG, "resp is null");
          throw new Exception();
        }

        JSONArray json;
        json = new JSONArray(s);

        if (json == null) {
          Log.v(TAG, "json parse error");
          throw new Exception();
        }

        Log.v(TAG, s.toString());
        ArrayList<Article> articles = new ArrayList<>();

        for (int i = 0; i < json.length(); i++) {
          JSONObject current = json.getJSONObject(i);

          if (current != null) {
            String title = current.getString("title").replace("&nbsp;", ""); //Remove gibberish
            String author = current.getJSONObject("author").getString("name");
            String desc = current.getString("excerpt");
            desc =
                desc.substring(3, desc.length() - 5).replace("&nbsp;", ""); //Removes the html tags

            String content = "<!DOCTYPE html><html><head><style>"
                + "body{margin: 0; padding: 0;}"
                + "p{font-family: Constantia, \"Book Antiqua\", Cambria, serif;font-size: 14px;line-height: 1.5;}"
                + "div[id^=\"attachment\"],img{max-width: 100%;height: auto;}"
                + "a[href^=\"http://thereckoner.ca/wp-content/uploads/\"]{pointer-events: none;cursor: default;}"
                + "</style><body>"
                + current.get("content")
                + "</body></html>";

            //CHeck the sdk due to Html.fromHtml(String) being deprecated
            if (Build.VERSION.SDK_INT >= 24) {
              title = Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY).toString();
              desc = Html.fromHtml(desc, Html.FROM_HTML_MODE_LEGACY).toString();
            } else {
              title = Html.fromHtml(title).toString();
              desc = Html.fromHtml(desc).toString();
            }

            String image;
            try {
              image = current.getJSONObject("featured_image").getString("guid");
            } catch (Exception e) {
              image = "";
              //e.printStackTrace(); //No need to print the stacktrace
            }
            //String image = current.getString("guid");

            String url = current.getString("link");

            articles.add(new Article(title, author, desc, image, content, url));
          }
        }

        if (articleListAdapter == null) {
          //Setup the adapter with the on click listener
          articleListAdapter = new ArticleListAdapter(articles, new OnItemClickListener() {
            @Override public void onItemClick(Article a) {
              Log.v(TAG, "Item clicked: " + a.getTitle()); //Log the article that was clicked

              //Display ReadingActivity with the selected article
              Intent intent = new Intent(getContext(), ReadingActivity.class);
              intent.putExtra(getString(ca.thereckoner.reckoner.R.string.articleParam), a);
              startActivity(intent);
            }
          });

          articleList.setAdapter(articleListAdapter); //Set the adapter to the recycler view
        } else {
          int length = articleListAdapter.getItemCount();
          articleListAdapter.addArticles(articles); //Add the new articles to the adapter
          articleListAdapter.notifyItemRangeInserted(length, articleListAdapter.getItemCount() - 1);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      loadingAnimation.setVisibility(View.GONE);
      articleList.setVisibility(View.VISIBLE);
    }
  }


}
