package com.sb.guesthouse.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.sb.guesthouse.R;
import com.sb.guesthouse.customgui.SPopup;
import com.sb.guesthouse.resource.StaticResources;
import com.sb.guesthouse.ui.fragment.GuestBottomSheet;
import com.sb.guesthouse.utils.LogUtil;

import org.w3c.dom.Text;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private TextView tvUser;
    private Button btnAddItem, btnSummary, btnLang, btnGuests;
    private Locale myLocale;
    //SharedPref
    private SharedPreferences.Editor editor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUser();
        setAppLocale(StaticResources.currentLang);
        setContentView(R.layout.activity_main);
        //getActionBar().hide();
        init();
        setLangIcon();
        setData();
        LogUtil.l(TAG,"Current Lang: " + StaticResources.currentLang,true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setData(){
        tvUser.setText(getResources().getString(R.string.hi) + StaticResources.currentUser);
    }

    private void setLangIcon(){
        if(StaticResources.currentLang.equals(StaticResources.ENG)){
            btnLang.setBackgroundResource(R.drawable.eng);
        }else{
            btnLang.setBackgroundResource(R.drawable.hin);
        }
    }
    private void init(){
        LogUtil.l(TAG+"_init","Current User: " + StaticResources.currentUser,true);
        tvUser = (TextView) findViewById(R.id.tv_user);
        btnAddItem = (Button) findViewById(R.id.btn_add_item);
        btnAddItem.setOnClickListener(addItemListener);
        btnSummary = (Button) findViewById(R.id.btn_summary);
        btnSummary.setOnClickListener(summaryListener);
        btnLang = (Button) findViewById((R.id.btn_lang));
        btnLang.setOnClickListener(langListener);
        btnGuests =(Button) findViewById(R.id.btn_guests);
        btnGuests.setOnClickListener(guestListener);
    }


    View.OnClickListener langListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(MainActivity.this, btnLang);
            popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    SPopup.showToast(MainActivity.this,"You Clicked : " + item.getTitle());
                    //Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    if(item.getTitle().equals("English")){
                       setLocale(StaticResources.ENG);
                    }else{
                        setLocale(StaticResources.HIN);
                    }
                    return true;
                }
            });

            popup.show();
        }
    };

    View.OnClickListener addItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtil.l(TAG,"Add Item Button Clicked.", true);
            Intent i = new Intent(MainActivity.this, AddItemActivity.class);
            startActivity(i);
        }
    };

    View.OnClickListener summaryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtil.l(TAG,"Summary Button Clicked.", true);
            Intent i = new Intent(MainActivity.this, SummaryActivity.class);
            startActivity(i);
        }
    };

    View.OnClickListener guestListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //SPopup.showToast(getApplicationContext(), "Function no implemented.");
            Intent i = new Intent(MainActivity.this, GuestListActivity.class);
            startActivity(i);
        }
    };

    private void setAppLocale(String localeCode){
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN_MR1){
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }

    public void setLocale(String localeName) {
        if (!localeName.equals(StaticResources.currentLang)) {
            StaticResources.currentLang = localeName;
            editor.putString(StaticResources.LANG_KEY,localeName);
            editor.commit();
            setLangIcon();
            myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainActivity.class);
            refresh.putExtra(StaticResources.currentLang, localeName);
            startActivity(refresh);
        } else {
            Toast.makeText(MainActivity.this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUser(){
        SharedPreferences MySetting = getSharedPreferences(StaticResources.PREF,0);
        editor = MySetting.edit();
        String user = MySetting.getString("user",null);
        String lang = MySetting.getString(StaticResources.LANG_KEY,null);
        if(user==null){
            LogUtil.l(TAG+"_getUser","Unable to get saved user. Starting AlertBox",true);
            StaticResources.currentLang = StaticResources.ENG;
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.alert_box_user, null);
            final EditText etUser = alertLayout.findViewById(R.id.etUser);
            AlertDialog.Builder userDialog = new AlertDialog.Builder(this);
            userDialog.setTitle("Enter User");
            userDialog.setView(alertLayout);
            userDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            userDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StaticResources.currentUser = etUser.getText().toString();
                    setData();
                    editor.putString("user", StaticResources.currentUser);
                    editor.putString(StaticResources.LANG_KEY,StaticResources.ENG);
                    editor.commit();

                }
            });
            AlertDialog dialog = userDialog.create();
            dialog.show();
        }else{
            StaticResources.currentUser = user;
            StaticResources.currentLang = lang;
        }
    }
}