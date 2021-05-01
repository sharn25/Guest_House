package com.sb.guesthouse.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sb.guesthouse.R;
import com.sb.guesthouse.adapter.GuestListAdapter;
import com.sb.guesthouse.adapter.ItemListAdapter;
import com.sb.guesthouse.customgui.SPopup;
import com.sb.guesthouse.helper.SwipeHelper2;
import com.sb.guesthouse.model.Guest;
import com.sb.guesthouse.model.Item;
import com.sb.guesthouse.resource.StaticResources;
import com.sb.guesthouse.ui.fragment.GuestBottomSheet;
import com.sb.guesthouse.ui.fragment.GuestDetailSheet;
import com.sb.guesthouse.utils.ConvertUtils;
import com.sb.guesthouse.utils.DateUtils;
import com.sb.guesthouse.utils.LogUtil;
import com.sb.guesthouse.utils.NetworkUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GuestListActivity extends AppCompatActivity implements GuestListAdapter.OnRVClickListener {
    private final static String TAG = "GuestListActivity";
    private DatabaseReference dbReff;
    private Button btnAddGuest;
    private RecyclerView rvGuestlist;
    private RecyclerView rvGuestListOut;
    private TextView tvNoGuest;
    private ProgressBar pbGuestData;
    private boolean isComptetedTab;
    private List<Guest> guestList, guestListOut;
    private java.util.List<String> guestKeys, guestKeysOut;
    GuestListAdapter guestListAdapter;
    GuestListAdapter guestListAdapterOut;

    private TabLayout tlGuestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_list);
        init();
        LogUtil.l(TAG,"OnCreate",true);
        if(NetworkUtils.isInternetAvailable(this)){
            loadData();
        }else {
            tvNoGuest.setVisibility(View.VISIBLE);
            pbGuestData.setVisibility(View.GONE);
            SPopup.showToast(GuestListActivity.this,getResources().getString(R.string.no_internet));
        }
    }

    private void init(){
        btnAddGuest = (Button) findViewById(R.id.btn_add_guest);
        btnAddGuest.setOnClickListener(addGuestListener);
        rvGuestlist = (RecyclerView) findViewById(R.id.rv_guest_list);
        rvGuestListOut = (RecyclerView) findViewById(R.id.rv_guest_list_out);
        tvNoGuest = (TextView) findViewById(R.id.tv_no_guest);
        pbGuestData = (ProgressBar) findViewById(R.id.pb_guest_data);
        tlGuestView = (TabLayout) findViewById(R.id.tl_guest_view);
        createTabs();
    }

    private void createTabs(){
        TabLayout.Tab firstTab = tlGuestView.newTab(); // Create a new Tab names
        firstTab.setText(StaticResources.STAYING); // set the Text for the first Tab
        TabLayout.Tab secondTab = tlGuestView.newTab(); // Create a new Tab names
        secondTab.setText(StaticResources.STAY_ENDED); // set the Text for the first Tab
        tlGuestView.addTab(firstTab,0);
        tlGuestView.addTab(secondTab,1);
        tlGuestView.setTabTextColors(Color.LTGRAY, Color.BLACK);
        tlGuestView.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        tlGuestView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int t = tab.getPosition();
                switch (t){
                    case 0:
                        isComptetedTab = false;
                        UpdateRecylerView();
                        break;
                    case 1:
                        isComptetedTab = true;
                        UpdateRecylerViewCompleted();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setRVGuestVisibility(boolean b){
        if(b){
            rvGuestListOut.setVisibility(View.GONE);
            rvGuestlist.setVisibility(View.VISIBLE);
        }else{
            rvGuestListOut.setVisibility(View.VISIBLE);
            rvGuestlist.setVisibility(View.GONE);
        }
    }

    private void loadData(){
        String curMonth = DateUtils.getDate("MMM-yyyy");
        LogUtil.l(TAG,"Current Month - " + curMonth,true);
        getItems();
    }

    private void setRvVisiblity(Boolean b, RecyclerView rv){
        rvGuestlist.setVisibility(View.GONE);
        rvGuestListOut.setVisibility(View.GONE);
        if(b){
            rv.setVisibility(View.VISIBLE);
            pbGuestData.setVisibility(View.GONE);
            tvNoGuest.setVisibility(View.GONE);

        }else{
            rv.setVisibility(View.GONE);
            pbGuestData.setVisibility(View.VISIBLE);
            tvNoGuest.setVisibility(View.GONE);
        }
    }

    private void getItems(){
        SharedPreferences MySetting = getSharedPreferences(StaticResources.PREF,0);
        //editor = MySetting.edit();
        dbReff = FirebaseDatabase.getInstance().getReference().child(StaticResources.GUEST_DB);//.child(childName);
        dbReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!=null){
                    getGuestList().clear();
                    getGuestKeys().clear();
                    getGuestListOut().clear();
                    getGuestKeysOut().clear();
                    long total = snapshot.getChildrenCount();
                    LogUtil.l(TAG, "Total Guest = " + total, true);
                    for (DataSnapshot dsp : snapshot.getChildren()) {
                        LogUtil.l(TAG, "dsp.getKey() - " + dsp.getKey(), true);
                        String name = dsp.child("guestName").getValue().toString();
                        String deparment = dsp.child("department").getValue().toString();
                        String company = dsp.child("company").getValue().toString();
                        String date = dsp.child("date").getValue().toString();
                        String time = dsp.child("time").getValue().toString();
                        String status = dsp.child("status").getValue().toString();
                        String ebsDates = dsp.child("absenceDates").getValue().toString();
                        String enddate = dsp.child("enddate").getValue().toString();
                        String days = dsp.child("days").getValue().toString();
                        String timeLimit = dsp.child("timeLimit").getValue().toString();
                        Boolean isTimeLimit = false;
                        if(timeLimit.equals("true")){
                            isTimeLimit = true;
                        }
                        if(enddate.equals("")){
                            days = DateUtils.getDays(date, DateUtils.getDate("dd-MMM-yyyy"));
                        }else{
                            days = DateUtils.getDays(date, enddate);
                        }
                        Guest guest = new Guest(name,deparment,company,date,time,days,ebsDates,enddate,status,isTimeLimit);
                        //get key and add the same to array list
                        if(enddate.equals("")){
                            getGuestKeys().add(dsp.getKey());
                            getGuestList().add(guest);
                        }else{
                            getGuestKeysOut().add(dsp.getKey());
                            getGuestListOut().add(guest);
                        }
                        tlGuestView.getTabAt(1).select();
                        tlGuestView.getTabAt(0).select();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SPopup.showToast(GuestListActivity.this, getResources().getString(R.string.error_getting_data));
            }
        });
    }

    public void updateEndDate(String key, String endDate){

    }

    private void UpdateRecylerView() {
        try {
            if (getGuestList() != null) {
                if (guestListAdapter == null) {
                    LogUtil.l(TAG + "UpdateRecyclerView", "rv_adapter is null. creating new.", true);
                    guestListAdapter = new GuestListAdapter(this, getGuestList(), this);
                    rvGuestlist.setLayoutManager(new GridLayoutManager(this, 1));
                    rvGuestlist.setAdapter(guestListAdapter);
                    setUpSwipeButtonRV(rvGuestlist);
                } else {
                    LogUtil.l(TAG + "UpdateRecyclerView", "rv_adapter already exist. Updateing..", true);
                    guestListAdapter.notifyDataSetChanged();
                }
                setRvVisiblity(true, rvGuestlist);
            } else {
                throw new java.lang.NullPointerException();
            }
        } catch (Exception e) {
            setRvVisiblity(false, rvGuestlist);
            tvNoGuest.setVisibility(View.VISIBLE);
        }
    }

    private void UpdateRecylerViewCompleted() {
        try {
            if (getGuestListOut() != null) {
                if (guestListAdapterOut == null) {
                    LogUtil.l(TAG + "UpdateRecyclerView", "rv_adapter is null. creating new.", true);
                    guestListAdapterOut = new GuestListAdapter(this, getGuestListOut(), this);
                    rvGuestListOut.setLayoutManager(new GridLayoutManager(this, 1));
                    rvGuestListOut.setAdapter(guestListAdapterOut);
                    setUpSwipeButtonRV(rvGuestListOut);
                } else {
                    LogUtil.l(TAG + "UpdateRecyclerView", "rv_adapter already exist. Updateing..", true);
                    guestListAdapterOut.notifyDataSetChanged();
                }
                setRvVisiblity(true, rvGuestListOut);
            } else {
                throw new java.lang.NullPointerException();
            }
        } catch (Exception e) {
            setRvVisiblity(false, rvGuestListOut);
            tvNoGuest.setVisibility(View.VISIBLE);
        }
    }

    private void setUpSwipeButtonRV(RecyclerView rv){
        SwipeHelper2 swipe = new SwipeHelper2(this, rv, true) {
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
                dbReff.child(getGuestKeysTab().get(pos)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SPopup.showToast(GuestListActivity.this,"Entry deleted Successfully.");
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

    View.OnClickListener addGuestListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GuestBottomSheet bottomSheetDialog = GuestBottomSheet.newInstance();
            bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
        }
    };

    /**
     * Creates a GuestList array Object
     * @return guestList Array
     */
    private List<Guest> getGuestList(){
        if(guestList==null){
            guestList = new ArrayList<>();
        }
        return guestList;
    }

    /**
     * Creates a Guest Key for guest data identification
     * @return guestKeys Array
     */
    private List<String> getGuestKeys(){
        if(guestKeys==null){
            guestKeys = new ArrayList<>();
        }
        return guestKeys;
    }

    /**
     * Creates a GuestList out array Object
     * @return guestListOut Array
     */
    private List<Guest> getGuestListOut(){
        if(guestListOut==null){
            guestListOut = new ArrayList<>();
        }
        return guestListOut;
    }

    private List<Guest> getGuestListTab(){
        if(isComptetedTab){
         return getGuestListOut();
        }else{
            return getGuestList();
        }
    }

    private List<String> getGuestKeysTab(){
        if(isComptetedTab){
            return getGuestKeysOut();
        }else{
            return getGuestKeys();
        }
    }

    /**
     * Creates a Guest Key out for guest data identification
     * @return guestKeysOut Array
     */
    private List<String> getGuestKeysOut(){
        if(guestKeysOut==null){
            guestKeysOut = new ArrayList<>();
        }
        return guestKeysOut;
    }

    @Override
    public void OnRVClick(int position) {
        LogUtil.l(TAG,"Clicked " + position,true);
        GuestDetailSheet bottomSheetDialog = GuestDetailSheet.newInstance(getGuestListTab().get(position), getGuestKeysTab().get(position));
        bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
    }
}