package com.bethel.eyo.kryptobash.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.bethel.eyo.kryptobash.R;


public class BtcCardSuccessDialog extends Dialog implements View.OnClickListener {
    Button okBtn;
    public BtcCardSuccessDialog(Context context) {
        super(context);
        setContentView(R.layout.btc_success_dialog);
        okBtn = findViewById(R.id.ok_btc);
        okBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        BtcCardSuccessDialog.this.dismiss();
    }
}
