package com.bethel.eyo.kryptobash.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.bethel.eyo.kryptobash.R;

public class EthCardSuccessDialog extends Dialog implements View.OnClickListener {
    Button ok;
    public EthCardSuccessDialog(Context context) {
        super(context);
        setContentView(R.layout.eth_success_dialog);
        ok = findViewById(R.id.ok_eth);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        EthCardSuccessDialog.this.dismiss();
    }
}
