package com.example.broadcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int RC_RECEIVE_SMS = 1;
    private MySMSReceiver mySMSReceiver;

    static ArrayList<String> smsList = new ArrayList<String>();
    static ListView Listview;
    static ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //final ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        mySMSReceiver = new MySMSReceiver();
        Listview = (ListView)findViewById(R.id.listview);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsList);
        Listview.setAdapter(adapter);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, RC_RECEIVE_SMS);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mySMSReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mySMSReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == RC_RECEIVE_SMS) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 ok
            } else {
                // 사용자에게 권한 필요성을 설명하는 다이얼로그
                // 다시 권한 요청..
                new AlertDialog.Builder(this).setTitle("Permission!")
                        .setMessage("RECEIVE_SMS permission is required to receive SMS.\nPress OK to grant the permission.")
                        .setPositiveButton("OK", (new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, RC_RECEIVE_SMS);
                            }
                        }))
                        .setNegativeButton("Cancel", null)
                        .create().show();
            }
        }
    }
}
