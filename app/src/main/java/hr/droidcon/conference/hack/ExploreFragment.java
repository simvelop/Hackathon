package hr.droidcon.conference.hack;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import hr.droidcon.conference.hack.R;
import hr.droidcon.conference.hack.objects.Conference;

/**
 * A fragment representing explore items.
 */
public class ExploreFragment extends Fragment implements View.OnClickListener {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExploreFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ExploreFragment newInstance() {
        ExploreFragment fragment = new ExploreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        initExploreViews(view);
        return view;
    }

    private void initExploreViews(View parent) {
        setOnClickListener(R.id.explore_dev, parent);
        setOnClickListener(R.id.explore_ux, parent);
        setOnClickListener(R.id.explore_biz, parent);
        setOnClickListener(R.id.explore_other, parent);
    }

    private void setOnClickListener(int viewId, View parent) {
        View view = parent.findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(this);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private ArrayList<Conference> filterCategories(String filter) {
        ArrayList<Conference> resultConferences = new ArrayList<>();
        for (Conference conference: ((MainActivity)getActivity()).mConferences){
            if (conference.getCategory().equalsIgnoreCase(filter)) {
                resultConferences.add(conference);
            }
        }
        return resultConferences;
    }

    @Override
    public void onClick(View v) {
        ArrayList<Conference> resultConferences;
        Intent intent;
        switch(v.getId()) {
            case R.id.explore_biz:
                //BIZ INTENT
                 resultConferences = filterCategories(FilteredActivity.BUSINESS_FILTER);
                intent = new Intent(getActivity(), FilteredActivity.class);
                intent.putExtra(FilteredActivity.ARGS_KEY, resultConferences);
                startActivity(intent);
                break;
            case R.id.explore_dev:
                //DEV intent
                 resultConferences = filterCategories(FilteredActivity.DEVELOPMENT_FILTER);
                 intent = new Intent(getActivity(), FilteredActivity.class);
                intent.putExtra(FilteredActivity.ARGS_KEY, resultConferences);
                startActivity(intent);
                break;
            case R.id.explore_ux:
                //UX intent
                resultConferences = filterCategories(FilteredActivity.UX_UI_FILTER);
                intent = new Intent(getActivity(), FilteredActivity.class);
                intent.putExtra(FilteredActivity.ARGS_KEY, resultConferences);
                startActivity(intent);
                break;
            case R.id.explore_other:
                //OTHER intent
                resultConferences = filterCategories(FilteredActivity.OTHER_FILTER);
                intent = new Intent(getActivity(), FilteredActivity.class);
                intent.putExtra(FilteredActivity.ARGS_KEY, resultConferences);
                startActivity(intent);
                break;
            default:
                // Do nothing.
                break;
        }
    }
}
