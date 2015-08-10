package com.example.hanjiyeon.maptest;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class CustomDialog extends Dialog implements View.OnClickListener
{
    Button btn;
    double lat,lng;
    String addr;

    public CustomDialog(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        //super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        btn = (Button)findViewById(R.id.select_button);
        btn.setOnClickListener(this);
    }

    public CustomDialog(Context context,double lat,double lng,String addr)
    {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        //super(context);
        this.lat = lat;
        this.lng=lng;
        this.addr = addr;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        btn = (Button)findViewById(R.id.select_button);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btn)
        {
            Intent intentSubActivity =
                    new Intent(getContext(), Schedule.class);

            intentSubActivity.putExtra("lat",lat);
            intentSubActivity.putExtra("lng",lng);
            intentSubActivity.putExtra("address",addr);

            //startActivity(intentSubActivity);
            dismiss();
        }
    }
}
