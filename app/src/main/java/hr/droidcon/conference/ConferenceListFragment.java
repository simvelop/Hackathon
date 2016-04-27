package hr.droidcon.conference;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import hr.droidcon.conference.hack.R;
import hr.droidcon.conference.objects.Conference;
import hr.droidcon.conference.utils.Utils;
import hr.droidcon.conference.views.ConferenceView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConferenceListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConferenceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConferenceListFragment extends Fragment {

    private static final String PARAM_ID = "FRAGMENT_ID";

    private OnFragmentInteractionListener mListener;

    @Bind(R.id.stageContainer)
    LinearLayout stageContainer;

    private String[] stageTab = {"Stage A", "Stage B", "Stage C"};

    private boolean isCreated = false;
    private int id;
    private List<Conference> conferences;
    private Map<String, Conference> conferanceMap = new HashMap<>();
    private List<String> timeList = new ArrayList<>();
//    private MainAdapter mAdapter;

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

        if (getArguments() != null) {
            id = getArguments().getInt(PARAM_ID);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isCreated = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conference_list, container, false);
        ButterKnife.bind(this, view);

        if (conferences != null) {
            createConferenceView();
        }

        isCreated = true;
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setConferences(List<Conference> conferences) {
        this.conferences = conferences;
        if (isCreated) {
            createConferenceView();
        }
    }

    private void createConferenceView() {
        for (final Conference conference : conferences) {
            if (!timeList.contains(conference.getStartDate())) {
                timeList.add(conference.getStartDate());
            }
            conferanceMap.put(conference.getStartDate() + conference.getLocation(), conference);
        }

        for (String time : timeList) {
            View[] viewTab = new View[3];
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setWeightSum(1);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 0; i < stageTab.length; i++) {
                final Conference conference = getConferenceInfo(time + stageTab[i]);
                if (conference != null) {
                    viewTab[i] = ConferenceView.createView(getContext(), conference);
                    viewTab[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClick(v, conference);
                        }
                    });
                    viewTab[i].setTag(conference.isSpeakerNullOrEmpty());
                } else {
                    viewTab[i] = ConferenceView.getBlankView(getContext());
                    viewTab[i].setTag(false);
                }
            }

            for (View view : viewTab) {
                boolean tag = (boolean) view.getTag();
                setLayoutWeight(view, tag ? 150 : 200, tag ? 1 : .333f);
                if (tag) {
                    linearLayout.removeAllViews();
                }
                linearLayout.addView(view);
            }
            stageContainer.addView(linearLayout);
        }
    }

    private void setLayoutWeight(View view, int size, float weight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, Utils.dpToPx(size, getContext()), weight);
        view.setLayoutParams(params);
    }

    private void onItemClick(View view, Conference conferance) {
        Pair<View, String> image = Pair.create(view.findViewById(R.id.image),
                getString(R.string.image));
        Pair<View, String> speaker = Pair.create(view.findViewById(R.id.speaker),
                getString(R.string.speaker));
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                image, speaker).toBundle();
        Intent intent = new Intent(getActivity(), ConferenceActivity.class);
        intent.putExtra("conference", conferance);
        ActivityCompat.startActivity(getActivity(), intent, bundle);
    }

    private Conference getConferenceInfo(String conferenceId) {
        return conferanceMap.get(conferenceId);
    }
}
