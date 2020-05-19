package com.example.feast.client.internal.utility.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.feast.R;
import com.example.feast.client.internal.model.Model;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class UserRecipeArrayAdapter extends ArrayAdapter<UserRecipe> {

    private ListView listView;

    /**
     * Constructor
     *
     * @param context
     * @param resource
     * @param items
     * @param listView
     */
    public UserRecipeArrayAdapter(Context context, int resource, List<UserRecipe> items, ListView listView) {
        super(context, resource, items);
        this.listView = listView;
    }

    /**
     * gets the view setup for userRecipes
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
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
                final UserRecipe ur = getItem(position);

                Model.getInstance().deleteUserRecipe(ur.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        remove(ur);
                    }
                });
            }
        });
        return convertView;
    }

    /**
     * inner viewHolder class
     */
    private static class ViewHolder {
        TextView tvName;
        TextView tvTime;
        ImageButton deleteRecipe;
    }
}
