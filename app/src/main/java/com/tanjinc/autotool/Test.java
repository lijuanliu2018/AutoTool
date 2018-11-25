package com.tanjinc.autotool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Test extends Activity{
    private void test() {
        startActivity(new Intent(this,WorkService.class));
        Intent intent = new Intent();
        intent.setClassName("com.jifen.qukan", "com.jifen.qkbase.main.MainActivity");
    }
}
