package com.in.den.android.notification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainTabActivity.class);
        startActivity(intent);
        finish();
    }
}
