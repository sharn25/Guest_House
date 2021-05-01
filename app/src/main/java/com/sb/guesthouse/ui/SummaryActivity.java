package com.sb.guesthouse.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.sb.guesthouse.BuildConfig;
import com.sb.guesthouse.R;
import com.sb.guesthouse.adapter.ItemListAdapter;
import com.sb.guesthouse.customgui.SPopup;
import com.sb.guesthouse.helper.SwipeHelper2;
import com.sb.guesthouse.model.Item;
import com.sb.guesthouse.resource.StaticResources;
import com.sb.guesthouse.utils.CSVUtils;
import com.sb.guesthouse.utils.ConvertUtils;
import com.sb.guesthouse.utils.DateUtils;
import com.sb.guesthouse.utils.LogUtil;
import com.sb.guesthouse.utils.NetworkUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SummaryActivity extends AppCompatActivity implements ItemListAdapter.OnRVClickListener {
    private final static String TAG = "SummaryActivity";
    private DatabaseReference dbReff;
    private TextView tvTotalAmount, tvNoData;
    private List<Item> aryItem;
    private List<String> keyList;
    private List<String> monthList;
    private boolean isMonthReceived;
    private RecyclerView rvDataList;
    private ProgressBar pbDataLoad;
    private ItemListAdapter itemListAdapter;

    private Spinner spMonth;
    private Button btnExcelExport;

    //double total amount
    private double totalAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_2);
        init();
        setRvVisiblity(false);
        LogUtil.l(TAG,"OnCreate",true);
        if(NetworkUtils.isInternetAvailable(this)){

            getServerMonthList();

            //setUpSwipeButtonRV();
        }else {
            tvNoData.setVisibility(View.VISIBLE);
            pbDataLoad.setVisibility(View.GONE);
            btnExcelExport.setVisibility(View.GONE);
            SPopup.showToast(SummaryActivity.this,getResources().getString(R.string.no_internet));
        }

    }

    private List<String> getKeyList(){
        if(keyList==null){
            keyList = new ArrayList<>();
        }
        return keyList;
    }

    private List<String> getMonthsList(){
        if(monthList==null){
            monthList = new ArrayList<>();
        }
        return monthList;
    }

    private void init(){
        totalAmount = 0;
        tvTotalAmount = (TextView) findViewById(R.id.tv_total_amount);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        rvDataList = (RecyclerView) findViewById(R.id.rv_data_list);
        pbDataLoad = (ProgressBar) findViewById(R.id.pb_data_load);
        spMonth = (Spinner) findViewById(R.id.sp_month);
        spMonth.setOnItemSelectedListener(spMonthListener);
        btnExcelExport = (Button) findViewById(R.id.btn_excel_export);
        btnExcelExport.setOnClickListener(btnExcelListener);
    }

    private void loadSpMonth(){
        LogUtil.l(TAG,"Setting Month List to Spinner.",true);
        ArrayAdapter dataAdapter = new ArrayAdapter<String>(this, R.layout.sp_layout_round, getMonthsList());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonth.setAdapter(dataAdapter);
    }

    private AdapterView.OnItemSelectedListener spMonthListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            LogUtil.l(TAG,"Spinner item Changed - " + getMonthsList().get(position),true);
            isMonthReceived = true;
            getItems(getMonthsList().get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void setRvVisiblity(Boolean b){
        if(b){
            rvDataList.setVisibility(View.VISIBLE);
            pbDataLoad.setVisibility(View.GONE);
            tvNoData.setVisibility(View.GONE);
            btnExcelExport.setVisibility(View.VISIBLE);
        }else{
            rvDataList.setVisibility(View.GONE);
            btnExcelExport.setVisibility(View.GONE);
            pbDataLoad.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }
    }

    private void getServerMonthList(){

        LogUtil.l(TAG,"LogUtils.Call",true);
        dbReff = FirebaseDatabase.getInstance().getReference().child(StaticResources.ITEM_DB);
        dbReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getMonthsList().clear();
                if(snapshot!=null){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        LogUtil.l(TAG,"Item Month Key adding - " + dsp.getKey(),true);
                        getMonthsList().add(dsp.getKey());
                    }
                    String curMonth = DateUtils.getDate(StaticResources.defaultMonthFormat);
                    LogUtil.l(TAG,"Current Month - " + curMonth,true);
                    loadSpMonth();
                    if(getMonthsList().contains(curMonth)){
                        int curMon = getMonthsList().indexOf(curMonth);
                        LogUtil.l(TAG,"Month list contains - " + curMonth + " at index " + curMon,true);
                        spMonth.setSelection(curMon);
                        //getItems(curMonth);
                    }else {
                        LogUtil.l(TAG,"Month list not contains - " + curMonth,true);
                        spMonth.setSelection(getMonthsList().size()-1);
                        //getItems(getMonthsList().get(getMonthsList().size() - 1));
                    }

                    isMonthReceived = true;
                }else {
                    LogUtil.l(TAG,"snapshot - null",true);
                    isMonthReceived = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SPopup.showToast(SummaryActivity.this, getResources().getString(R.string.error_getting_data));
            }
        });
    }
    private void getItems(String childName){
        getAryItem().clear();
        getKeyList().clear();
        totalAmount = 0;
        dbReff = FirebaseDatabase.getInstance().getReference().child(StaticResources.ITEM_DB).child(childName);
        dbReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isMonthReceived) {
                    String item_id = StaticResources.item_name;
                    long total = snapshot.getChildrenCount();
                    LogUtil.l(TAG, "Total Items = " + total, true);
                    for (DataSnapshot dsp : snapshot.getChildren()) {
                        LogUtil.l(TAG, "dsp.getKey() - " + dsp.getKey(), true);
                        String name = dsp.child("itemName").getValue().toString();
                        String type = dsp.child("itemType").getValue().toString();
                        String amount = dsp.child("itemAmount").getValue().toString();
                        String date = dsp.child("date").getValue().toString();
                        String user = dsp.child("user").getValue().toString();
                        double amountD = ConvertUtils.stringToDouble(amount);
                        totalAmount = totalAmount + amountD;
                        Item item = new Item(name, type, amountD, date, user);
                        //get key and add the same to array list
                        getKeyList().add(dsp.getKey());
                        getAryItem().add(item);
                    }
                    UpdateRecylerView();
                    tvTotalAmount.setText("Rs." + totalAmount);
                    isMonthReceived = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SPopup.showToast(SummaryActivity.this, getResources().getString(R.string.error_getting_data));
            }
        });
    }

    private void UpdateRecylerView() {
        try {
            if (getAryItem() != null) {
                if (itemListAdapter == null) {
                    LogUtil.l(TAG + "UpdateRecyclerView", "rv_adapter is null. creating new.", true);
                    itemListAdapter = new ItemListAdapter(this, getAryItem(), this);
                    rvDataList.setLayoutManager(new GridLayoutManager(this, 1));
                    rvDataList.setAdapter(itemListAdapter);
                    setUpSwipeButtonRV();
                } else {
                    LogUtil.l(TAG + "UpdateRecyclerView", "rv_adapter already exist. Updateing..", true);
                    itemListAdapter.notifyDataSetChanged();
                }
                setRvVisiblity(true);
            } else {
                throw new java.lang.NullPointerException();
            }
        } catch (Exception e) {
            setRvVisiblity(false);
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private void setUpSwipeButtonRV(){
        SwipeHelper2 swipe = new SwipeHelper2(this, rvDataList, true) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper2.UnderlayButton(getResources().getString(R.string.btn_del), null, Color.parseColor("#ff0000"), Color.parseColor("#ffffff"), new UnderlayButtonClickListener() {
                    @Override
                    public void onClick(int pos) {
                        LogUtil.l(TAG,"Deleting Pos - " + pos, true);
                        getDelDilog(pos);
                    }
                }));
            }
        };
    }

    private void getDelDilog(final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Do nothing but close the dialog
                dbReff.child(getKeyList().get(pos)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SPopup.showToast(SummaryActivity.this,"Entry deleted Successfully.");
                    }
                });

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    View.OnClickListener btnExcelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String file = StaticResources.appDir + File.separator + spMonth.getSelectedItem().toString() + ".csv";
            LogUtil.l(TAG,"Excel Export Clicked.", true);
            CSVUtils csvFile = new CSVUtils(file);
            List<Item> itemList = getAryItem();
            for(int i = 0; i<itemList.size(); i++){
                Item item = itemList.get(i);
                csvFile.write(item.getDate());
                csvFile.write(item.getItemName());
                csvFile.write(item.getItemType());
                csvFile.write(ConvertUtils.doubleToString(item.getItemAmount()));
                csvFile.writeln(item.getUser());
            }
            LogUtil.l(TAG,"CSV - ", true);
            csvFile.close();
            sendFile(file);
        }
    };

    private void sendFile(String file){
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(file);

        if(fileWithinMyDir.exists()) {
            intentShareFile.setType("application/pdf");

            Uri uri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + "." + getLocalClassName() + ".provider",
                    fileWithinMyDir);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

    private List<Item> getAryItem(){
        if(aryItem==null){
            aryItem = new ArrayList<>();
        }
        return aryItem;
    }

    @Override
    public void OnRVClick(int position) {
        LogUtil.l(TAG,"Clicked " + position,true);
    }
}