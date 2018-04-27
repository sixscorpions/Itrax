package com.itrax.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.itrax.R;
import com.itrax.activities.BaseActivity;
import com.itrax.activities.SummaryActivity;
import com.itrax.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shankar on 12/12/2017.
 */

public class SummeryListAdapter extends ArrayAdapter<String> {

    private ArrayList<String> spinnerModel;
    private LayoutInflater inflater;
    private BaseActivity parent;

    public SummeryListAdapter(BaseActivity context, int textViewResourceId,
                              ArrayList<String> categoryitems) {
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
            convertView = inflater.inflate(R.layout.row_spinner_summery, null);
            holder = new ViewHolder();

            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        String mData = spinnerModel.get(position);
        holder.tv_title.setText(mData);
        holder.tv_count.setText("" + SummaryActivity.count.get(position));
        return convertView;
    }

    private class ViewHolder {
        private TextView tv_title;
        private TextView tv_count;
    }

}