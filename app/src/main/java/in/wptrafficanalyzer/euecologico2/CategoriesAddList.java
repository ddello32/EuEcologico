package in.wptrafficanalyzer.euecologico2;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class CategoriesAddList extends DialogFragment {

    private boolean[] valid;
    Category[] validCategories;
    private Position position;
    private boolean[] status;
    private OnFragmentInteractionListener listener;

    public CategoriesAddList() {
        // Required empty public constructor
    }

    public static CategoriesAddList newInstance(Category[] categories, boolean [] valid, Position position) {
        CategoriesAddList fragment = new CategoriesAddList();
        Bundle args = new Bundle();
        args.putParcelableArray("Categories", categories);
        args.putBooleanArray("Valid", valid);
        args.putParcelable("Position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Parcelable[] parcelable = getArguments().getParcelableArray("Categories");
            Category[] categories = new Category[parcelable.length];
            for (int i = 0; i < categories.length; i++) {
                categories[i] = (Category) parcelable[i];
            }
            valid = getArguments().getBooleanArray("Valid");
            position = getArguments().getParcelable("Position");
            ArrayList<Category> categoriesList = new ArrayList<Category>();
            for (int i = 0; i < categories.length; i++) {
                if (valid[i]) {
                    categoriesList.add(categories[i]);
                }
            }
            validCategories = new Category[categoriesList.size()];
            for (int i = 0; i < validCategories.length; i++) {
                validCategories[i] = categoriesList.get(i);
            }
            status = new boolean[validCategories.length];
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        Dialog myDialog = getDialog();
        myDialog.setTitle("Select the categories");

        if (savedInstanceState != null) {
            status = savedInstanceState.getBooleanArray("Status");
        }
        else {
            for (int i = 0; i < validCategories.length; i++) {
                validCategories[i].setVisibility(false);
            }
        }

        final CategoriesListAdapter adapter = new CategoriesListAdapter(getActivity(), R.layout.row, validCategories);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = adapter.getItem(position);
                category.setVisibility(!category.getVisibility());
                status[position] = category.getVisibility();
                adapter.notifyDataSetChanged();
            }
        });

        Button Cancel = (Button) view.findViewById(R.id.cancel);
        Cancel.setOnClickListener(onCancel);
        Button OK = (Button) view.findViewById(R.id.ok);
        OK.setOnClickListener(onOk);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray("Status", status);
    }

    View.OnClickListener onOk = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            boolean[] result = new boolean[valid.length];
            int j = 0;
            for (int i = 0; i < valid.length; i++) {
                if (valid[i]) {
                    if (status[j]) {
                        result[i] = true;
                    }
                    j++;
                }
            }
            listener.onCategoriesAddListInteraction(position, result);
            dismiss();
        }
    };

    View.OnClickListener onCancel = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            listener.onCategoriesAddListInteraction(position, null);
            dismiss();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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
        public void onCategoriesAddListInteraction(Position positions, boolean[] result);
    }
}