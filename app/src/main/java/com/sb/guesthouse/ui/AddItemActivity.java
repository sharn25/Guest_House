package com.sb.guesthouse.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAssignedNumbers;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sb.guesthouse.R;
import com.sb.guesthouse.customgui.SPopup;
import com.sb.guesthouse.model.Item;
import com.sb.guesthouse.resource.StaticResources;
import com.sb.guesthouse.utils.ConvertUtils;
import com.sb.guesthouse.utils.DateUtils;
import com.sb.guesthouse.utils.LogUtil;
import com.sb.guesthouse.utils.NetworkUtils;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Add new item activity class
 * @author Sharn25
 * @since 27-02-2021
 * @version 0.0
 */
public class AddItemActivity extends AppCompatActivity {
    private final static String TAG = "AddItemActivity";
    private EditText etItemName, etItemAmount;
    private Spinner spType;
    private Button btnSubmit;
    private long itemCount;
    private DatabaseReference dbReff;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_2);
        init();
        initDB();
    }

    private void init(){
        itemCount = 0;
        etItemName = (EditText) findViewById(R.id.et_item_name);
        etItemAmount = (EditText) findViewById(R.id.et_item_amount);
        spType = (Spinner) findViewById(R.id.sp_type);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.item_type, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down);
        spType.setAdapter(adapter);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(submitListener);
    }

    private void initDB(){
        String currentMonth = DateUtils.getDate(StaticResources.defaultMonthFormat);
        dbReff = FirebaseDatabase.getInstance().getReference().child(StaticResources.ITEM_DB).child(currentMonth);
        dbReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!=null){
                    if(snapshot.hasChild(StaticResources.item_name + itemCount)){
                        LogUtil.l(TAG,"Child exists - " + itemCount, true);
                    }else{
                        LogUtil.l(TAG,"Child not exists - " + itemCount, true);
                    }
                    itemCount = (snapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SPopup.showToast(AddItemActivity.this, getResources().getString(R.string.error_sending_data));
            }
        });

    }

    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(TextUtils.isEmpty(etItemAmount.getText().toString()) || TextUtils.isEmpty(etItemName.getText().toString())){
                SPopup.showToast(AddItemActivity.this,getResources().getString(R.string.empty_value));
                return;
            }
            if(!NetworkUtils.isInternetAvailable(AddItemActivity.this)) {
                SPopup.showToast(AddItemActivity.this,getResources().getString(R.string.no_internet));
                return;
            }
            LogUtil.l(TAG,"Submit Button Clicked.", true);
            /*Date c = Calendar.getInstance().getTime();
            LogUtil.l(TAG,"Current Time => " + c, true);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);*/
            String formattedDate = DateUtils.getDate(StaticResources.defaultDateFormat);
            String type = spType.getSelectedItem().toString();
            String name = etItemName.getText().toString();
            double amount = ConvertUtils.stringToDouble(etItemAmount.getText().toString());
            Item item = new Item(name, type, amount, formattedDate, StaticResources.currentUser);
            //dbReff.child(StaticResources.item_name+itemCount)
            dbReff.push().setValue(item, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if(error==null){
                        //SPopup.popinfo(MainActivity,"Data Saved Successfully.");
                        final Dialog dialog = new Dialog(AddItemActivity.this);
                        SPopup.showFinishDialog(dialog, getResources().getString(R.string.finish_item_success), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }else{
                        LogUtil.e(TAG,"Error! " + error.getMessage(),true);
                        SPopup.popinfo(AddItemActivity.this,"Error! " + error.getMessage());

                    }
                }
            });
        }
    };

}