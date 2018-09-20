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
import com.itrax.aynctask.IAsyncCaller;
import com.itrax.aynctaskold.ServerIntractorAsync;
import com.itrax.db.CreateSalesDataSource;
import com.itrax.db.DatabaseHandler;
import com.itrax.models.CreateSalesModel;
import com.itrax.models.LoginModel;
import com.itrax.models.Model;
import com.itrax.models.PostNoteModel;
import com.itrax.models.SendOtpModel;
import com.itrax.parser.LoginParser;
import com.itrax.parser.PostNoteParser;
import com.itrax.utils.APIConstants;
import com.itrax.utils.Constants;
import com.itrax.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Shankar on 18-04-18.
 */

public class HomeFragment extends Fragment implements IAsyncCaller {
    public static final String TAG = HomeFragment.class.getSimpleName();
    private WorkBenchActivity mParent;
    private View view;

    @BindView(R.id.ll_dynamic_data)
    LinearLayout ll_dynamic_data;

    @BindView(R.id.tv_off_line_count)
    TextView tv_off_line_count;

    public static EditText edt_delivery_date;
    public static EditText edtNote;
    public static EditText edt_doctor_name;
    public static EditText edt_mobile_number;
    private LoginModel mLoginModel;
    public static ArrayList<Integer> count = new ArrayList<>();
    public static ArrayList<String> stringList;
    public static SpinnerDialogAdapterForMedicines adapter;
    public static ArrayList<EditText> views = new ArrayList<>();

    private CreateSalesDataSource createSalesDatasource;

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
        DatabaseHandler.getInstance(mParent);
        createSalesDatasource = new CreateSalesDataSource(mParent);
        if (createSalesDatasource.getDataCount() > 0) {
            tv_off_line_count.setText("" + createSalesDatasource.getDataCount());
            tv_off_line_count.setVisibility(View.VISIBLE);
        } else
            tv_off_line_count.setVisibility(View.GONE);

        edt_doctor_name = (EditText) view.findViewById(R.id.edt_doctor_name);
        edt_delivery_date = (EditText) view.findViewById(R.id.edt_delivery_date);
        edtNote = (EditText) view.findViewById(R.id.et_invite_note);
        edt_mobile_number = (EditText) view.findViewById(R.id.edt_mobile_number);
        if (mLoginModel != null && mLoginModel.getDynamicFieldsModels() != null
                && mLoginModel.getDynamicFieldsModels().size() > 0) {
            for (int i = 0; i < mLoginModel.getDynamicFieldsModels().size(); i++) {
                if (mLoginModel.getDynamicFieldsModels().get(i).getType().equalsIgnoreCase("DropdownPopup")) {
                    String mListNames = mLoginModel.getDynamicFieldsModels().get(i).getList();
                    String[] array_list = mListNames.split(", ");
                    stringList = new ArrayList<>();
                    for (int k = 0; k < array_list.length; k++) {
                        stringList.add(array_list[k]);
                        count.add(-1);
                    }
                }
            }
        }
        setDynamicData();
    }

    @OnClick({R.id.edt_delivery_date})
    void onDeliveryDate() {
        DashBoardActivity.SelectDateFragment newFragment = new DashBoardActivity.SelectDateFragment(edt_delivery_date);
        newFragment.show(mParent.getSupportFragmentManager(), "DatePicker");
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

    /**
     * This method is used to set the dynamic data
     */
    private void setDynamicData() {
        ll_dynamic_data.removeAllViews();
        String json = Utility.getSharedPrefStringData(mParent, Constants.LOGIN_RESPONSE);
        if (!Utility.isValueNullOrEmpty(json)) {
            LoginParser loginParser = new LoginParser();
            mLoginModel = (LoginModel) loginParser.parse(json, mParent);
        }
        if (mLoginModel != null && mLoginModel.getDynamicFieldsModels() != null
                && mLoginModel.getDynamicFieldsModels().size() > 0) {
            for (int i = 0; i < mLoginModel.getDynamicFieldsModels().size(); i++) {
                if (mLoginModel.getDynamicFieldsModels().get(i).getType().equalsIgnoreCase("Text")) {
                    LinearLayout linearLayout = (LinearLayout) mParent.getLayoutInflater().inflate(R.layout.edit_workbench_text_dynamic, null);
                    EditText editText = (EditText) linearLayout.findViewById(R.id.edt_id);
                    editText.setHint(mLoginModel.getDynamicFieldsModels().get(i).getLabel());
                    views.add(editText);
                    ll_dynamic_data.addView(linearLayout);
                } else if (mLoginModel.getDynamicFieldsModels().get(i).getType().equalsIgnoreCase("DropdownPopup")) {
                    LinearLayout linearLayout3 = (LinearLayout) mParent.getLayoutInflater().inflate(R.layout.edit_workbench_medicines_dynamic, null);
                    final EditText editTextSpinner = (EditText) linearLayout3.findViewById(R.id.edt_spinner);
                    editTextSpinner.setHint(mLoginModel.getDynamicFieldsModels().get(i).getLabel());
                    editTextSpinner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSpinnerDialogForMedicines();
                        }
                    });
                    views.add(editTextSpinner);
                    ll_dynamic_data.addView(linearLayout3);
                } else if (mLoginModel.getDynamicFieldsModels().get(i).getType().equalsIgnoreCase("Dropdown")) {
                    LinearLayout linearLayout3 = (LinearLayout) mParent.getLayoutInflater().inflate(R.layout.edit_workbench_spinner_dynamic, null);
                    final EditText editTextSpinner = (EditText) linearLayout3.findViewById(R.id.edt_spinner);
                    editTextSpinner.setHint(mLoginModel.getDynamicFieldsModels().get(i).getLabel());
                    final String mName = mLoginModel.getDynamicFieldsModels().get(i).getLabel();

                    String mListNames = mLoginModel.getDynamicFieldsModels().get(i).getList();
                    String[] array_list = mListNames.split(", ");
                    final List<String> stringList = new ArrayList<>();
                    for (int k = 0; k < array_list.length; k++) {
                        stringList.add(array_list[k]);
                    }
                    editTextSpinner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utility.showSpinnerDialogWorkBench(mParent, mName, stringList, editTextSpinner);
                        }
                    });
                    views.add(editTextSpinner);
                    ll_dynamic_data.addView(linearLayout3);
                }
            }
        }
    }

    @OnClick(R.id.btn_submit)
    void submitData() {
        if (isValidFields()) {
            Intent intent = new Intent(mParent, SummaryActivity.class);
            intent.putStringArrayListExtra(Constants.SELECTED_LIST, getFinalSelectionList());
            intent.putIntegerArrayListExtra(Constants.SELECTED_COUNT, getFinalCountSelectionList());
            startActivity(intent);
        }
    }

    private boolean isValidFields() {
        boolean isValidated = false;
        if (Utility.isValueNullOrEmpty(edt_doctor_name.getText().toString().trim())) {
            Utility.setSnackBar(edt_doctor_name, "Please enter doctor name");
            edt_doctor_name.requestFocus();
        } /*else if (Utility.isValueNullOrEmpty(edt_mobile_number.getText().toString().trim())) {
            Utility.setSnackBar(edt_mobile_number, "Please enter mobile number");
            edt_mobile_number.requestFocus();
        } else if (edt_mobile_number.getText().toString().trim().length() < 10) {
            Utility.setSnackBar(edt_mobile_number, "Please enter valid mobile number");
            edt_mobile_number.requestFocus();
        } else if (edt_mobile_number.getText().toString().trim().length() < 10) {
            Utility.setSnackBar(edt_mobile_number, "Please enter valid mobile number");
            edt_mobile_number.requestFocus();
        } else if (getFinalCountSelectionList() != null && getFinalCountSelectionList().size() == 0) {
            Utility.setSnackBar(edt_mobile_number, "Please select medicines");
            edt_mobile_number.requestFocus();
        } */ else {
            isValidated = true;
        }
        return isValidated;
    }


    private ArrayList<String> getFinalSelectionList() {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < count.size(); i++) {
            if (count.get(i) != -1) {
                strings.add(stringList.get(i));
            }
        }
        return strings;
    }

    private ArrayList<Integer> getFinalCountSelectionList() {
        ArrayList<Integer> strings = new ArrayList<>();
        for (int i = 0; i < count.size(); i++) {
            if (count.get(i) != -1) {
                strings.add(count.get(i));
            }
        }
        return strings;
    }


    /**
     * This method is used for sync
     */
    @OnClick(R.id.tv_off_line_count)
    void sync() {
        postLocationData();
    }

    private void postLocationData() {
        final ArrayList<CreateSalesModel> createSalesModels = createSalesDatasource.selectAll();
        JSONArray jsonSalesRecordsArray = new JSONArray();
        if (createSalesModels != null && createSalesModels.size() > 0) {
            for (int i = 0; i < createSalesModels.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Coordinates", createSalesModels.get(i).getCoordinates());
                    jsonObject.put("Area", createSalesModels.get(i).getArea());
                    jsonObject.put("Country", "India");
                    jsonObject.put("Time", createSalesModels.get(i).getTime());
                    jsonObject.put("Note", createSalesModels.get(i).getNote());
                    jsonObject.put("CustomerName", createSalesModels.get(i).getCustomer_name());
                    jsonObject.put("CustomerMobile", createSalesModels.get(i).getCustomer_mobile());
                    jsonObject.put("DueDate", createSalesModels.get(i).getDue_date());
                    jsonObject.put("IsOtpVerified", createSalesModels.get(i).getIsotpverified());
                    jsonObject.put("Source", "android");
                    jsonObject.put("InitiatedDate", createSalesModels.get(i).getInitiatedDate());
                    jsonObject.put("InitiatedTime", createSalesModels.get(i).getInitiatedTime());
                    jsonObject.put("AdditionalInfo", createSalesModels.get(i).getAdditional_info());
                    jsonSalesRecordsArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        try {
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            linkedHashMap.put("SalesRecords", jsonSalesRecordsArray);
            PostNoteParser mPostNoteParser = new PostNoteParser();
            ServerIntractorAsync serverJSONAsyncTask = new ServerIntractorAsync(
                    mParent, Utility.getResourcesString(mParent, R.string.please_wait), true,
                    APIConstants.CREATE_SALES_RECORD, linkedHashMap,
                    APIConstants.REQUEST_TYPE.POST, this, mPostNoteParser);
            Utility.execute(serverJSONAsyncTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete(Model model) {
        if (model != null) {
            if (model instanceof PostNoteModel) {
                PostNoteModel mPostNoteModel = (PostNoteModel) model;
                if (mPostNoteModel.isStatus()) {
                    createSalesDatasource.deleteAll();
                    tv_off_line_count.setVisibility(View.GONE);
                    Utility.showToastMessage(mParent, "Offline data sent successfully");
                }
            }
        }
    }
}
