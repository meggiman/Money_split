package ch.ethz.itet.pps.budgetSplit;


import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProjectItems#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectItems extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROJECT_CONTENT_URI = "projectContentUri";

    private Uri contentUri;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param projectContentUri Parameter 1.
     * @return A new instance of fragment ProjectItems.
     */
    public static ProjectItems newInstance(Uri projectContentUri) {
        ProjectItems fragment = new ProjectItems();
        Bundle args = new Bundle();
        args.putParcelable(PROJECT_CONTENT_URI, projectContentUri);
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectItems() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contentUri = getArguments().getParcelable(PROJECT_CONTENT_URI);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_items, container, false);
    }


}
