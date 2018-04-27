package com.itrax.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.itrax.R;
import com.itrax.adapters.SummeryListAdapter;
import com.itrax.utils.Constants;
import com.itrax.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SummaryActivity extends BaseActivity {

    /**
     * AllEvents List set up
     */
    @BindView(R.id.listView)
    SwipeMenuListView listView;

    @BindView(R.id.btn_submit)
    Button btn_submit;

    @BindView(R.id.img_back)
    ImageView img_back;
    public static SummeryListAdapter summeryListAdapter;
    private int mDeletePosition = -1;
    private int mEditPosition = -1;

    public static ArrayList<Integer> count;
    public static ArrayList<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);
        intiUI();
    }

    /**
     * This method is used to initiate
     */
    private void intiUI() {
        stringList = getIntent().getStringArrayListExtra(Constants.SELECTED_LIST);
        count = getIntent().getIntegerArrayListExtra(Constants.SELECTED_COUNT);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.showToastMessage(SummaryActivity.this, "Successfully Submitted");
                finishAffinity();
            }
        });

        setGridViewData();
    }

    /*This method is used to set the lsit view data*/
    private void setGridViewData() {
        summeryListAdapter = new SummeryListAdapter(this, 200, stringList);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "open" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xEF, 0x53,
                        0x53)));
                // set item width
                deleteItem.setWidth(dp2px(70));
                // set item title
                deleteItem.setTitle("Delete");
                // set item title fontsize
                deleteItem.setTitleSize(15);
                deleteItem.setIcon(R.drawable.delete_icon);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);

                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xB2, 0xE7,
                        0xFA)));
                openItem.setWidth(dp2px(70));
                // set item title
                openItem.setTitle("Edit");
                // set item title fontsize
                openItem.setTitleSize(15);
                openItem.setIcon(R.drawable.edit_icon);
                // set item title font color
                openItem.setTitleColor(Utility.getColor(SummaryActivity.this, R.color.back_ground));
                // add to menu
                menu.addMenuItem(openItem);


            }
        };
        // set creator
        listView.setMenuCreator(creator);
        listView.setAdapter(summeryListAdapter);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        mDeletePosition = position;
                        stringList.remove(mDeletePosition);
                        setGridViewData();
                        break;
                    case 1:
                        mEditPosition = position;
                        Utility.showAskCountDialog(mEditPosition, SummaryActivity.this, stringList.get(mEditPosition), "Summary");
                        break;
                }
                return false;
            }
        });
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}
