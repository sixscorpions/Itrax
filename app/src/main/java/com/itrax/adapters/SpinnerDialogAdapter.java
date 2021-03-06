package com.itrax.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itrax.R;
import com.itrax.activities.BaseActivity;

import java.util.List;

/**
 * Created by Shankar on 12/12/2017.
 */

public class SpinnerDialogAdapter extends ArrayAdapter<String> {

    private List<String> spinnerModel;
    private LayoutInflater inflater;
    private BaseActivity parent;

    public SpinnerDialogAdapter(BaseActivity context, int textViewResourceId,
                                List<String> categoryitems) {
        super(context, textViewResourceId);
        spinnerModel = categoryitems;
        parent = context;
        inflater = LayoutInflater.from(context.getApplicationContext());
    }

    @Override
    public int getCount() {
        return spinnerModel.size();
    }

    @Override
    public String getItem(int position) {
        return spinnerModel.get(position);
    }

    @Override
    public int getPosition(String item) {
        return spinnerModel.indexOf(item);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup view) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_spinner_dialog, null);
            holder = new ViewHolder();

            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        String mData = spinnerModel.get(position);
        holder.tv_title.setText(mData);

        return convertView;
    }


    private class ViewHolder {
        private TextView tv_title;
    }

}