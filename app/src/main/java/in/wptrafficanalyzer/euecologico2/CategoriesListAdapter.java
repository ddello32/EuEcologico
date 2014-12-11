package in.wptrafficanalyzer.euecologico2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoriesListAdapter extends ArrayAdapter<Category> {

    private int resource;
    private Category[] objects;

    public CategoriesListAdapter(Context context, int resource, Category[] objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.objects = objects;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView title;
        ImageView checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            row = layoutInflater.inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) row.findViewById(R.id.icon);
            viewHolder.title = (TextView) row.findViewById(R.id.title);
            viewHolder.checkbox = (ImageView) row.findViewById(R.id.checkbox);
            row.setTag(viewHolder);
        }
        else {
            row = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.icon.setImageResource(objects[position].getIcon());
        viewHolder.title.setText(objects[position].getTitle());
        if (objects[position].getVisibility()) {
            viewHolder.checkbox.setImageResource(R.drawable.checkedcheckbox);
        }
        else {
            viewHolder.checkbox.setImageResource(R.drawable.uncheckedcheckbox);
        }
        return row;
    }
}