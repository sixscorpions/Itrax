package com.itrax.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itrax.R;
import com.itrax.activities.BaseActivity;
import com.itrax.activities.DashBoardActivity;
import com.itrax.activities.SummaryActivity;
import com.itrax.activities.WorkBenchActivity;
import com.itrax.adapters.SpinnerDialogAdapterForMedicines;
import com.itrax.models.LoginModel;
import com.itrax.parser.LoginParser;
import com.itrax.utils.Constants;
import com.itrax.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Shankar on 18-04-18.
 */

public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    private WorkBenchActivity mParent;
    private View view;

    @BindView(R.id.edt_doctor_name)
    EditText edt_doctor_name;

    @BindView(R.id.edt_mobile_number)
    EditText edt_mobile_number;
    @BindView(R.id.edt_medicines)
    EditText edt_medicines;
    static EditText edt_delivery_date;

    private LoginModel mLoginModel;
    public static ArrayList<Integer> count = new ArrayList<>();
    private static ArrayList<String> stringList;
    public static SpinnerDialogAdapterForMedicines adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParent = (WorkBenchActivity) getActivity();

        String json = Utility.getSharedPrefStringData(mParent, Constants.LOGIN_RESPONSE);
        if (!Utility.isValueNullOrEmpty(json)) {
            LoginParser loginParser = new LoginParser();
            mLoginModel = (LoginModel) loginParser.parse(json, mParent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null)
            return view;

        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        initUi();
        return view;
    }

    private void initUi() {
        edt_delivery_date = (EditText) view.findViewById(R.id.edt_delivery_date);
        if (mLoginModel != null && mLoginModel.getDynamicFieldsModels() != null
                && mLoginModel.getDynamicFieldsModels().size() > 0) {
            for (int i = 0; i < mLoginModel.getDynamicFieldsModels().size(); i++) {
                if (mLoginModel.getDynamicFieldsModels().get(i).getType().equalsIgnoreCase("DropdownPopup")
                        && mLoginModel.getDynamicFieldsModels().get(i).getLabel().equalsIgnoreCase("Medicines")) {
                    String mListNames = mLoginModel.getDynamicFieldsModels().get(i).getList();
                    String[] array_list = mListNames.split(", ");
                    stringList = new ArrayList<>();
                    for (int k = 0; k < array_list.length; k++) {
                        stringList.add(array_list[k]);
                        count.add(0);
                    }
                }
            }
        }
    }

    @OnClick({R.id.edt_delivery_date})
    void onDeliveryDate() {
        DashBoardActivity.SelectDateFragment newFragment = new DashBoardActivity.SelectDateFragment(edt_delivery_date);
        newFragment.show(mParent.getSupportFragmentManager(), "DatePicker");
    }

    @OnClick({R.id.edt_medicines})
    void onShowSpinnerDialogForMedicines() {
        showSpinnerDialogForMedicines();
    }

    private void showSpinnerDialogForMedicines() {

        final Dialog dialog = new Dialog(mParent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.madicines_dialog);
        /*dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);*/
        dialog.setCancelable(true); // can dismiss alert screen when user click back buttonon
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ListView lv_medicines = (ListView) dialog.findViewById(R.id.lv_medicines);
        EditText et_search = (EditText) dialog.findViewById(R.id.et_search);
        adapter = new SpinnerDialogAdapterForMedicines(mParent,
                100, stringList);
        lv_medicines.setAdapter(adapter);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lv_medicines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Utility.showAskCountDialog(i, mParent, stringList.get(i), "home");
            }
        });

        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView tv_et_search_image = (TextView) dialog.findViewById(R.id.tv_et_search_image);
        tv_et_search_image.setTypeface(Utility.getMaterialIconsRegular(mParent));

        dialog.show();
    }

    @OnClick(R.id.btn_submit)
    void submitData() {
        Intent intent = new Intent(mParent, SummaryActivity.class);
        intent.putStringArrayListExtra(Constants.SELECTED_LIST, getFinalSelectionList());
        intent.putIntegerArrayListExtra(Constants.SELECTED_COUNT, getFinalCountSelectionList());
        startActivity(intent);
    }

    private ArrayList<String> getFinalSelectionList() {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < count.size(); i++) {
            if (count.get(i) != 0) {
                strings.add(stringList.get(i));
            }
        }
        return strings;
    }

    private ArrayList<Integer> getFinalCountSelectionList() {
        ArrayList<Integer> strings = new ArrayList<>();
        for (int i = 0; i < count.size(); i++) {
            if (count.get(i) != 0) {
                strings.add(count.get(i));
            }
        }
        return strings;
    }


}
