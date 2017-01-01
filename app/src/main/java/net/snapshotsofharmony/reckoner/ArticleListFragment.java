package net.snapshotsofharmony.reckoner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.snapshotsofharmony.reckoner.View.OnItemClickListener;
import net.snapshotsofharmony.reckoner.View.ArticleListAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Varun Venkataramanan.
 *
 * Most used Fragment in the app. Displays posts based on the param category.
 * Uses the ArticleList Recycler View to display the articles.
 */
public class ArticleListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "ARG_CATEGORY_NAME";

    private String mCategory;

    private OnFragmentInteractionListener mListener;

    RecyclerView mRecyclerView; //The main recycler view
    LinearLayoutManager mLayoutManager; //Layout manager for the recycler view
    ArticleListAdapter mAdapter; //Adapter to display the data in the recycler view

    //Used to check for scrolled state
    private boolean loading = true; //Changed if new articles need to be loaded when scrolled
    int pastVisiblesItems;
    int visibleItemCount;
    int totalItemCount;
    int nextPage = 2; //Next page to load. First page loaded by default

    private final String TAG = "ArticleListFragment";

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_article_list, container, false);

        mRecyclerView =  (RecyclerView) view.findViewById(R.id.articleList);
        mRecyclerView.setHasFixedSize(true); //Size can't change

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(mOnScroll);


        //mRecyclerView.setOnClickListener(mOnClick);

        loadArticles(); //Load the articles

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
     * Loads the articles into the views
     */
    private void loadArticles(){
        //Make call to API
        List articles;
        try {
            articles = API.getArticles(1, mCategory); //Pass in the global fragment arg
        }catch (Exception e){
            //e.printStackTrace();
            articles = new ArrayList();
        }

        if(articles != null) { //If articles are returned
            //Setup the adapter with the on click listneer
            mAdapter = new ArticleListAdapter(articles, new OnItemClickListener() {
                @Override
                public void onItemClick(Article a) {
                    Log.v(TAG, "Item clicked: " + a.getTitle()); //Log the article that was clicked

                    //Display ReadingActivity with the selected article
                    Intent intent = new Intent(getContext(), ReadingActivity.class);
                    intent.putExtra(getString(R.string.articleParam), a);
                    startActivity(intent);
                }
            });

            mRecyclerView.setAdapter(mAdapter); //Set the adapter to the recycler view
        }
    }

    /**
     * Method to handle the loading of more articles on scroll to the bottom on the recycler view.
     */
    RecyclerView.OnScrollListener mOnScroll = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if(dy > 0){ //check for scroll down
                //Get the current state data
                int pos = mRecyclerView.getLayoutManager().getItemCount();
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    loading = false;
                    Log.v(TAG, "Loading more articles");

                    //Load more articles
                    List articles;
                    try {
                        articles = API.getArticles(nextPage, mCategory);
                        nextPage++;
                    }catch (Exception e){
                        e.printStackTrace();
                        articles = new ArrayList();
                    }

                    mAdapter.addArticles(articles); //Add the new articles to the adapter
                    mRecyclerView.invalidate();
                    mRecyclerView.getLayoutManager().scrollToPosition(pastVisiblesItems); //Reset the pos
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //VERY IMPORTANT that this is blank to allow for proper scroll reload handling
        }


    };

}
