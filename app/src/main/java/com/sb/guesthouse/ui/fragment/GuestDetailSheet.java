package com.sb.guesthouse.ui.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sb.guesthouse.R;
import com.sb.guesthouse.customgui.SPopup;
import com.sb.guesthouse.model.Guest;
import com.sb.guesthouse.resource.StaticResources;
import com.sb.guesthouse.ui.GuestListActivity;
import com.sb.guesthouse.utils.DateUtils;
import com.sb.guesthouse.utils.LogUtil;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GuestDetailSheet  extends BottomSheetDialogFragment {
    private final static String TAG = "GuestDetailSheet";

    private static Guest guest;
    private TextView tvGuestName, tvDeparment, tvCompany, tvStatus, tvDays, tvStart, tvEnd, tvTime, tvAbsenceDates;
    private Button btnEnd, btnAbsence;
    private static String guestKey;
    private static GuestDetailSheet fragment;
    private static DatabaseReference dbReff;

    public static GuestDetailSheet newInstance(Guest g, String key) {
        fragment = new GuestDetailSheet();
        guest = g;
        guestKey = key;
        return fragment;
    }

    private void init(View view){
        tvGuestName = (TextView) view.findViewById(R.id.tv_guest_name_detail);
        tvDeparment = (TextView) view.findViewById(R.id.tv_deparment_detail);
        tvCompany = (TextView) view.findViewById(R.id.tv_company_detail);
        tvStatus = (TextView) view.findViewById(R.id.tv_status_details);
        tvDays = (TextView) view.findViewById(R.id.tv_days_details);
        tvStart = (TextView) view.findViewById(R.id.tv_start_details);
        tvAbsenceDates = (TextView) view.findViewById(R.id.tv_absences_details);
        tvEnd = (TextView) view.findViewById(R.id.tv_end_details);
        btnEnd = (Button) view.findViewById(R.id.btn_end_stay);
        btnEnd.setOnClickListener(endListener);
        tvTime = (TextView) view.findViewById(R.id.tv_time_details);
        btnAbsence = (Button) view.findViewById(R.id.btn_ab_stay);
        btnAbsence.setOnClickListener(absenceListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.layout_guest_details, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        init(contentView);
        setValue();
    }

    private void setValue(){
        tvGuestName.setText(guest.getGuestName());
        tvDeparment.setText(guest.getDepartment());
        tvCompany.setText(guest.getCompany());
        tvStatus.setText(guest.getStatus());
        tvDays.setText(guest.getDays());
        tvStart.setText(guest.getDate());
        tvEnd.setText(guest.getEnddate());
        tvTime.setText(guest.getTime());
        String[] ads = guest.getAbsenceDates().split(",");
        if(!guest.getAbsenceDates().equals("")){
            int i = ads.length -1;
            tvAbsenceDates.setText(i+" days");
        }else{
            tvAbsenceDates.setText("0 day");
        }

    }

    private View.OnClickListener absenceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            String date = DateUtils.getFormatedDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, StaticResources.defaultDateFormat);
                            if(guest.getAbsenceDates().contains(date)){
                                SPopup.showToast(getActivity(),"Date already entered.");
                                return;
                            }
                            String curMonth = DateUtils.getDate(StaticResources.defaultMonthFormat);
                            dbReff = FirebaseDatabase.getInstance().getReference().child(StaticResources.GUEST_DB).child(guestKey);
                            Map<String, Object> update = new HashMap<>();
                            guest.setAbsenceDates(guest.getAbsenceDates() + "," +date);
                            String[] ads = guest.getAbsenceDates().split(",");
                            if(!guest.getAbsenceDates().equals("")){
                                int i = ads.length -1;
                                tvAbsenceDates.setText(i+" days");
                            }else{
                                tvAbsenceDates.setText("0 day");
                            }
                            update.put("absenceDates",guest.getAbsenceDates());
                            dbReff.updateChildren(update, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if(error==null){
                                        final Dialog dialog = new Dialog(getActivity());
                                        SPopup.showFinishDialog(dialog, getResources().getString(R.string.finish_guest_success), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

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
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();
        }
    };

    private View.OnClickListener endListener = new View.OnClickListener() {
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
                            String curMonth = DateUtils.getDate(StaticResources.defaultMonthFormat);
                            dbReff = FirebaseDatabase.getInstance().getReference().child(StaticResources.GUEST_DB).child(guestKey);
                            Map<String, Object> update = new HashMap<>();
                            update.put("enddate",date);
                            update.put("status",StaticResources.STAY_ENDED);
                            dbReff.updateChildren(update, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if(error==null){
                                        final Dialog dialog = new Dialog(getActivity());
                                        SPopup.showFinishDialog(dialog, getResources().getString(R.string.finish_guest_success), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                fragment.dismiss();
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
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();
        }
    };
}
