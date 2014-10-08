package ch.ethz.itet.pps.budgetSplit;


import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProjectOverview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectOverview extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROJECT_CONTENT_URI = "projectContentUri";

    private Uri projectUri;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contentUri Parameter 1.
     * @return A new instance of fragment ProjectOverview.
     */
    public static ProjectOverview newInstance(Uri contentUri) {
        ProjectOverview fragment = new ProjectOverview();
        Bundle args = new Bundle();
        args.putParcelable(PROJECT_CONTENT_URI, contentUri);
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectOverview() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectUri = getArguments().getParcelable(PROJECT_CONTENT_URI);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_overview, container, false);
    }


}
