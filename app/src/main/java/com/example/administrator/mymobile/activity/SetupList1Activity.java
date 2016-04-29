package com.example.administrator.mymobile.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.administrator.mymobile.R;

public class SetupList1Activity extends BaseSetupActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_list1);
    }

    public void ShowNext() {
        Intent intent = new Intent(this, SetupList2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    @Override
    public void ShowPrevious() {

    }
}
