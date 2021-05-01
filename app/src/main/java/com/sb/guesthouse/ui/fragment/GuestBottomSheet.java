package com.sb.guesthouse.ui.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sb.guesthouse.R;
import com.sb.guesthouse.customgui.SPopup;
import com.sb.guesthouse.model.Guest;
import com.sb.guesthouse.resource.StaticResources;
import com.sb.guesthouse.ui.AddItemActivity;
import com.sb.guesthouse.utils.DateUtils;
import com.sb.guesthouse.utils.LogUtil;
import com.sb.guesthouse.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Bottom sheet to add and edit Guest info
 * @author Sharn25
 * @version 0.0
 * @since 06-03-2021
 */
public class GuestBottomSheet extends BottomSheetDialogFragment {
    private final static String TAG = "GuestBottomSheet";

    private EditText etGuestName, etDeparment, etCompany, etDate, etTime;
    private RadioGroup rgStayType;
    private RadioButton rbParmanent, rbTimeLimit;
    private LinearLayout llTimeLimit;
    private boolean isTimeLimit;
    private Button btnGuestSubmit;
    private static GuestBottomSheet fragment;
    private DatabaseReference dbReff;


    public static GuestBottomSheet newInstance() {
        if(fragment==null){
            fragment = new GuestBottomSheet();
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.layout_guest_add, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        //Init
        etGuestName = (EditText) contentView.findViewById(R.id.et_guest_name);
        etDeparment = (EditText) contentView.findViewById(R.id.et_department);
        etCompany = (EditText)contentView.findViewById(R.id.et_company);
        etTime = (EditText) contentView.findViewById(R.id.et_time);
        etTime.setOnClickListener(timeListener);
        etDate = (EditText) contentView.findViewById(R.id.et_date);
        etDate.setOnClickListener(dateListener);

        rgStayType = (RadioGroup) contentView.findViewById(R.id.rg_stay_type);
        rgStayType.setOnCheckedChangeListener(rgStayTypeListener);

        llTimeLimit = (LinearLayout) contentView.findViewById(R.id.ll_time_limit);

        btnGuestSubmit = (Button) contentView.findViewById(R.id.btn_guest_submit);
        btnGuestSubmit.setOnClickListener(submitGListener);

        initDB();
    }

    private void initDB(){
        //String currentMonth = DateUtils.getDate(StaticResources.defaultMonthFormat);
        dbReff = FirebaseDatabase.getInstance().getReference().child(StaticResources.GUEST_DB);//.child(currentMonth);
        dbReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SPopup.showToast(getActivity(), getResources().getString(R.string.error_sending_data));
            }
        });
    }

    /**
     * Submit Button Listener to send data to Server
     */
    View.OnClickListener submitGListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(TextUtils.isEmpty(etGuestName.getText().toString()) && TextUtils.isEmpty(etDeparment.getText().toString()) && TextUtils.isEmpty(etCompany.getText().toString())){
                SPopup.showToast(getActivity(),getResources().getString(R.string.empty_value_guest));
                return;
            }
            if(!NetworkUtils.isInternetAvailable(getActivity())) {
                SPopup.showToast(getActivity(),getResources().getString(R.string.no_internet));
                return;
            }

            String name = etGuestName.getText().toString();
            String deparment = etDeparment.getText().toString();
            String company = etCompany.getText().toString();
            String date = "", time = "";
            String status = "";
            if(isTimeLimit){
                if(TextUtils.isEmpty(etDate.getText().toString()) && TextUtils.isEmpty(etTime.getText().toString())){
                    SPopup.showToast(getActivity(),getResources().getString(R.string.empty_value_guest_date_time));
                    return;
                }
                date = etDate.getText().toString();
                time = etTime.getText().toString();
                status = StaticResources.STAYING;
            }else {
                date = "01-" + DateUtils.getDate(StaticResources.defaultMonthFormat);
                time = "09:00 AM";
                status  = getResources().getString(R.string.guest_detail_permanent);
            }

            LogUtil.l(TAG, "Name-" + name + ", deparment-" + deparment + ", company-" + company + ", date-" + date + ", time" + time, true);

            String absDates = "";
            String enddate = "";
            Guest guest = new Guest(name,deparment,company,date, time,"", absDates,enddate, status, isTimeLimit);
            dbReff.push().setValue(guest, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if(error==null){
                        final Dialog dialog = new Dialog(getActivity());
                        SPopup.showFinishDialog(dialog, getResources().getString(R.string.finish_guest_success), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GuestBottomSheet.newInstance().dismiss();
                                dialog.dismiss();
                            }
                        });
                    }else{
                        LogUtil.e(TAG,"Error! " + error.getMessage(),true);
                        SPopup.popinfo(getActivity(),"Error! " + error.getMessage());
                    }
                }
            });


        }
    };

    /**
     * Stay type change Listener
     */
    RadioGroup.OnCheckedChangeListener rgStayTypeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (group.getCheckedRadioButtonId()){
                case R.id.rb_parmanent:
                    isTimeLimit = false;
                    llTimeLimit.setVisibility(View.GONE);
                    break;
                case R.id.rb_time_limit:
                    isTimeLimit = true;
                    llTimeLimit.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    /**
     * Date dialog listener
     */
    private View.OnClickListener dateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            String date = DateUtils.getFormatedDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, StaticResources.defaultDateFormat);
                            etDate.setText(date);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    };

    /**
     * time Dialog listener
     */
    private View.OnClickListener timeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            etTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    };
}
