package com.diewland.openthedoor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    public static final String MY_PREFS_NAME = "OPENTHEDOOR";

    private EditText text_protocal;
    private EditText text_ip;
    private EditText text_port;
    private Button btn_lock;

    private static final int PICK_CAMERA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        text_protocal = findViewById(R.id.protocal);
        text_ip = findViewById(R.id.ip);
        text_port = findViewById(R.id.port);
        btn_lock = findViewById(R.id.btn_lock);

        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent myIntent = new Intent(MainActivity.this, LivePreviewActivity.class);
            startActivityForResult(myIntent, PICK_CAMERA);
            }
        });
    }

    @Override
    protected void onPause() {
        editor = prefs.edit();
        editor.putString("protocal", text_protocal.getText().toString());
        editor.putString("ip", text_ip.getText().toString());
        editor.putString("port", text_port.getText().toString());
        editor.apply();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        text_protocal.setText(prefs.getString("protocal", ""));
        text_ip.setText(prefs.getString("ip", ""));
        text_port.setText(prefs.getString("port", ""));
    }
}
