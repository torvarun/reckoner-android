package net.snapshotsofharmony.reckoner;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import net.snapshotsofharmony.reckoner.View.ImageGridAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Varun Venkataramanan.
 *
 * Fragment for Humans of Garneau browsing. Presents the thumbnails in a grid view along with a static banner and button.
 */
public class HOGFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManager;
    ImageGridAdapter mAdapter;

    //Used to check for scrolled state
    private boolean loading = true; //Changed if new articles need to be loaded when scrolled
    int pastVisiblesItems;
    int visibleItemCount;
    int totalItemCount;
    int nextPage = 2; //Next page to load. First page loaded by default

    private final String TAG = "HOGFragment";

    public HOGFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HOGFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HOGFragment newInstance() {
        HOGFragment fragment = new HOGFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hog, container, false);

        mRecyclerView =  (RecyclerView) view.findViewById(R.id.hogGrid);
        mRecyclerView.setHasFixedSize(true); //Size can't change

        mGridLayoutManager = new GridLayoutManager(getContext(), 2); //2 columns
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addOnScrollListener(mOnScroll);

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
     * Loads Humans of Garneau Articles from the Reckoner API
     */
    private void loadArticles(){
        //Make the API Calls
        List articles;
        try {
            articles = API.getArticles(1, getString(R.string.humansOfGarneau)); //Use the param from strings.xml
        }catch (Exception e){
            //e.printStackTrace();
            articles = new ArrayList();
        }

        if(articles != null) { //If there are articles to display
            //Setup the adapter
            mAdapter = new ImageGridAdapter(articles);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    RecyclerView.OnScrollListener mOnScroll = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if(dy > 0){ //check for scroll down
                //Get current scroll state
                int pos = mRecyclerView.getLayoutManager().getItemCount();
                visibleItemCount = mGridLayoutManager.getChildCount();
                totalItemCount = mGridLayoutManager.getItemCount();
                pastVisiblesItems = mGridLayoutManager.findFirstVisibleItemPosition();


                if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    loading = false;
                    Log.v(TAG, "Loading more articles");

                    //Load more articles
                    List articles;
                    try {
                        articles = API.getArticles(nextPage, getString(R.string.humansOfGarneau));
                        nextPage++;
                    }catch (Exception e){
                        e.printStackTrace();
                        articles = new ArrayList();
                    }

                    mAdapter.addArticles(articles); //Add the new articles to the adapter
                    mRecyclerView.invalidate();
                    mRecyclerView.getLayoutManager().scrollToPosition(pastVisiblesItems);
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //VERY IMPORTANT that this is blank to allow for proper scroll reload handling

        }


    };
}
