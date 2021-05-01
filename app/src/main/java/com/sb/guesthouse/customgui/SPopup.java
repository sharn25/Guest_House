package com.sb.guesthouse.customgui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.sb.guesthouse.R;

/**
 * Custom popupinfos
 * @author Sharn25
 * @since 27-02-2021
 * @version 0.0
 */
public class SPopup {
    private static Toast mToast;
    private static String c = "";

    public static void popinfo(Context context, String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setNegativeButton("OK", null)
                .create()
                .show();
    }

    public static void popSnackinfo(View view, String msg) {
        Snackbar mySnackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    public static void showToast(Context mActivity, CharSequence text) {
        if (mToast == null) {
            mToast = Toast.makeText(mActivity, text, Toast.LENGTH_SHORT);
        } else {
            mToast.cancel();
            mToast = Toast.makeText(mActivity, text, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void showFinishDialog(Dialog activity_dialog, String msg, View.OnClickListener btnListener){
        final Dialog dialog = activity_dialog;//new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.finish_dialog);

        TextView text = (TextView) dialog.findViewById(R.id.tv_msg_text);
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_okay);
        dialogButton.setOnClickListener(btnListener);

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

}
