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
import android.widget.CheckBox;
import android.widget.ListView;

public class Filter extends DialogFragment {

    private Category[] categories;
    private boolean[] status;
    private OnFragmentInteractionListener listener;

    public Filter() {
        // Required empty public constructor
    }

    public static Filter newInstance(Category[] categories) {
        Filter fragment = new Filter();
        Bundle args = new Bundle();
        args.putParcelableArray("Categories", categories);
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
            this.categories = categories;
            status = new boolean[categories.length];
        }
    }

    private boolean areAllTrue(boolean[] array) {
        for (boolean b : array) if(!b) return false;
        return true;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkbox_list, container, false);
        Dialog myDialog = getDialog();
        myDialog.setTitle("Select the categories");

        if (savedInstanceState != null) {
            status = savedInstanceState.getBooleanArray("Status");
        }
        else {
            for (int i = 0; i < categories.length; i++) {
                status[i] = categories[i].getVisibility();
            }
        }

        final CategoriesListAdapter adapter = new CategoriesListAdapter(getActivity(), R.layout.row_filter, categories);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        final CheckBox selectAll = (CheckBox) view.findViewById(R.id.selectAll);
        final float scale = this.getResources().getDisplayMetrics().density;
        selectAll.setPadding(selectAll.getPaddingRight() + (int)(10.0f * scale + 1f),
                selectAll.getPaddingTop(),
                selectAll.getPaddingLeft(),
                selectAll.getPaddingBottom());
        if (areAllTrue(status)) {
            selectAll.setChecked(true);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = adapter.getItem(position);
                category.setVisibility(!category.getVisibility());
                status[position] = category.getVisibility();
                adapter.notifyDataSetChanged();
                if (areAllTrue(status)) {
                    selectAll.setChecked(true);
                }
                else {
                    selectAll.setChecked(false);
                }
            }
        });
        selectAll.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectAll.isChecked()) {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        adapter.getItem(i).setVisibility(true);
                        status[i] = true;
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        adapter.getItem(i).setVisibility(false);
                        status[i] = false;
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        Button Cancel = (Button) view.findViewById(R.id.cancel);
        Cancel.setOnClickListener(onCancel);
        Button OK = (Button) view.findViewById(R.id.ok);
        OK.setOnClickListener(onOK);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray("Status", status);
    }

    View.OnClickListener onOK = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            listener.onFilterInteraction(status);
            dismiss();
        }
    };

    View.OnClickListener onCancel = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            listener.onFilterInteraction(null);
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
        public void onFilterInteraction(boolean[] result);
    }
}
