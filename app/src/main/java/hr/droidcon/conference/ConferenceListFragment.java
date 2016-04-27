package hr.droidcon.conference;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hr.droidcon.conference.adapters.MainAdapter;
import hr.droidcon.conference.events.FilterUpdateEvent;
import hr.droidcon.conference.objects.Conference;
import hr.droidcon.conference.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConferenceListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConferenceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConferenceListFragment extends Fragment implements AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener {

    private static final String PARAM_ID = "FRAGMENT_ID";

    private static final String TAG = "ConfListFrag";

    private OnFragmentInteractionListener mListener;

    @Bind(R.id.conference_fragment_list)
    ListView conferencesListView;

    private int id;
    private List<Conference> conferences;
    private MainAdapter mAdapter;

    private MenuItem menuItemFavorites;

    public ConferenceListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConferenceListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConferenceListFragment newInstance(int id, List<Conference> conferences) {
        Log.d(TAG, "newInstance: called");
        ConferenceListFragment fragment = new ConferenceListFragment();
        Bundle args = new Bundle();
        args.putInt(PARAM_ID, id);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        fragment.setConferences(conferences);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            id = getArguments().getInt(PARAM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conference_list, container, false);
        ButterKnife.bind(this, view);

        updateConferenceList();

        conferencesListView.setOnScrollListener(this);
        conferencesListView.setOnItemClickListener(this);

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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_conference_list, menu);

        menuItemFavorites = menu.findItem(R.id.action_favorites_filter);
        updateMenuItem();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_favorites_filter:
                Log.d(TAG, "onOptionsItemSelected: filter conferences with id: " + id);
                //toggle favorites
                ((BaseApplication) getActivity().getApplication()).toogleFilterFavorite();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onMessageEvent(FilterUpdateEvent event){
        updateConferenceList();
        updateMenuItem();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (((Conference) parent.getAdapter().getItem(position)).getSpeaker().length() == 0) {
            // if the speaker field is empty, it's probably a coffee break or lunch
            return;
        }

        // TODO: FIX
        // On Lollipop we animate the speaker's name & picture
        // to the second activity
//        Pair<View, String> toolbar = Pair.create((View) mToolbar,
//                getString(R.string.toolbar));
        Pair<View, String> image = Pair.create(view.findViewById(R.id.image),
                getString(R.string.image));
        Pair<View, String> speaker = Pair.create(view.findViewById(R.id.speaker),
                getString(R.string.speaker));
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                image, speaker).toBundle();
        Intent intent = new Intent(getActivity(), ConferenceActivity.class);
        intent.putExtra("conference", (Conference) parent.getAdapter().getItem(position));
        ActivityCompat.startActivity(getActivity(), intent, bundle);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    /**
     * ScrollListener used only on Lollipop to smoothly elevate the {@link Toolbar}
     * when the user scroll.
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        if (firstVisibleItem == 0 && view != null && view.getChildCount() > 0) {
            Rect rect = new Rect();
            view.getChildAt(0).getLocalVisibleRect(rect);
            final float ratio = (float) Math.min(Math.max(rect.top, 0),
                    Utils.dpToPx(48, getActivity().getBaseContext()))
                    / Utils.dpToPx(48, getActivity().getBaseContext());
            final int newElevation = (int) (ratio * Utils.dpToPx(8, getActivity().getBaseContext()));

            // TODO: FIX
//            setToolbarElevation(newElevation);
        }
    }

    public void setConferences(List<Conference> conferences) {
        Log.d(TAG, "setConferences: called" + conferences.size());
        this.conferences = conferences;
    }

    private void updateMenuItem(){
        if (((BaseApplication) getActivity().getApplication()).isFilterFavorites()) {
            menuItemFavorites.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
        } else {
            menuItemFavorites.setIcon(getResources().getDrawable(R.drawable.ic_favorite_outline_white_24dp));
        }
    }

    private void updateConferenceList() {
        if(getActivity() == null){
            return;
        }

        final boolean filter = ((BaseApplication) getActivity().getApplication()).isFilterFavorites();
        Log.d(TAG, "updateConferenceList: filter: " + filter);

        List<Conference> filteredConferencesList = new ArrayList<>();

        for (Conference conference : this.conferences) {
            if (filter) {
                if (conference.isFavorite(getActivity())) {
                    filteredConferencesList.add(conference);
                }
            } else {
                filteredConferencesList.add(conference);
            }
        }

        mAdapter = new MainAdapter(this.getActivity(), filteredConferencesList);
        conferencesListView.setAdapter(mAdapter);
    }
}
