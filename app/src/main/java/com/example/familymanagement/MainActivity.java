package com.example.familymanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("datalogin", MODE_PRIVATE);
    }

    public void toUserInfo(View view){
        Intent intent = new Intent(MainActivity.this, ChangeInfo.class);
        startActivity(intent);
    }

    public void logout(View view){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("checked");
        editor.commit();
        Intent intent = new Intent(MainActivity.this, SignIn.class);
        startActivity(intent);
    }
}
