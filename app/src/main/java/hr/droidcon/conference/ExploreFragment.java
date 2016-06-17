package hr.droidcon.conference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import hr.droidcon.conference.objects.Conference;

import java.util.ArrayList;

/**
 * A fragment representing explore items.
 */
public class ExploreFragment extends Fragment implements View.OnClickListener {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExploreFragment() {}

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ExploreFragment newInstance() {
        ExploreFragment fragment = new ExploreFragment();
        return fragment;
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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private ArrayList<Conference> filterCategories(String filter) {
        ArrayList<Conference> resultConferences = new ArrayList<>();
        // TODO remove this ugly hack
        for (Conference conference : ((MainActivity) getActivity()).mConferences) {
            if (conference.getCategory().equalsIgnoreCase(filter)) {
                resultConferences.add(conference);
            }
        }
        return resultConferences;
    }

    private static final SparseArray<String> FILTERS = new SparseArray<>(4);
    static {
        FILTERS.put(R.id.explore_biz, FilteredActivity.BUSINESS_FILTER);
        FILTERS.put(R.id.explore_dev, FilteredActivity.DEVELOPMENT_FILTER);
        FILTERS.put(R.id.explore_ux, FilteredActivity.UX_UI_FILTER);
        FILTERS.put(R.id.explore_other, FilteredActivity.OTHER_FILTER);
    }

    @Override
    public void onClick(View v) {

        String filter = FILTERS.get(v.getId());
        if (filter != null) {
            Intent intent = new Intent(getActivity(), FilteredActivity.class);
            intent.putExtra(FilteredActivity.ARGS_KEY, filterCategories(filter));
            startActivity(intent);
        }
    }
}
