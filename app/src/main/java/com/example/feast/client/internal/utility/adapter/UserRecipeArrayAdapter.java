package com.example.feast.client.internal.utility.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.feast.R;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class UserRecipeArrayAdapter extends ArrayAdapter<UserRecipe> {

    private ListView listView;

    private static class ViewHolder {
        TextView tvName;
        TextView tvTime;
        FloatingActionButton deleteRecipe;
    }

    public UserRecipeArrayAdapter(Context context, int resource, List<UserRecipe> items, ListView listView) {
        super(context, resource, items);
        this.listView = listView;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        UserRecipe ur = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.list_item_user_recipe, parent, false);

            viewHolder.tvName = convertView.findViewById(R.id.tw_ur_name);
            viewHolder.tvTime = convertView.findViewById(R.id.tw_ur_time);
            viewHolder.deleteRecipe = convertView.findViewById(R.id.button_delete_re);
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(ur.getName());
        viewHolder.tvTime.setText(String.valueOf(ur.getEstimatedTime()));
        viewHolder.deleteRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(getItem(position));
            }
        });
        return convertView;
    }
}
