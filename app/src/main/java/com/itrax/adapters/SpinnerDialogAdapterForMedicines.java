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
import com.itrax.activities.DashBoardActivity;
import com.itrax.activities.WorkBenchActivity;
import com.itrax.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shankar on 12/12/2017.
 */

public class SpinnerDialogAdapterForMedicines extends ArrayAdapter<String> {

    private List<String> spinnerModel;
    private List<String> sortedList;
    private LayoutInflater inflater;
    private BaseActivity parent;

    public SpinnerDialogAdapterForMedicines(BaseActivity context, int textViewResourceId,
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
            convertView = inflater.inflate(R.layout.row_spinner_dialog_medicines, null);
            holder = new ViewHolder();

            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        String mData = spinnerModel.get(position);
        holder.tv_title.setText(mData);
        if (HomeFragment.count.get(position) != 0) {
            holder.tv_count.setText("" + HomeFragment.count.get(position));
        } else {
            holder.tv_count.setText("");
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<String> FilteredArrList = new ArrayList<>();


                if (sortedList == null) {
                    sortedList = new ArrayList<>(spinnerModel); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = sortedList.size();
                    results.values = sortedList;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < sortedList.size(); i++) {
                        String data = sortedList.get(i);
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(data);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                spinnerModel = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    private class ViewHolder {
        private TextView tv_title;
        private TextView tv_count;
    }

}