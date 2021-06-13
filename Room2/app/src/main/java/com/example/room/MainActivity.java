package com.example.room;

import androidx.appcompat.app.AppCompatActivity;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final String lg = "msg";
    BluetoothSocket btSocket = null;
    BluetoothAdapter btAdapter = null;
    OutputStream out = null;
    BluetoothDevice tooth;
    Switch lightSwitch;
    Button offButton;
    Button connectButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightSwitch = (Switch) findViewById(R.id.lightSwitch);
        offButton = (Button) findViewById(R.id.offButton);
        connectButton = (Button) findViewById(R.id.ConnectButton);

        connectButton.setVisibility(View.VISIBLE);
        lightSwitch.setVisibility(View.INVISIBLE);
        offButton.setVisibility(View.INVISIBLE);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btAdapter = BluetoothAdapter.getDefaultAdapter();
                tooth = btAdapter.getRemoteDevice("00:18:E4:35:BD:45");
                int n=0;
                do {
                    try {
                        btSocket = tooth.createRfcommSocketToServiceRecord(mUUID);
                        Log.i(lg,"socket created");
                        btSocket.connect();
                        System.out.println(btSocket.isConnected());
                    } catch (IOException e) {
                        Log.i(lg,"exception in creating socket");
                        e.printStackTrace();
                    }
                    if(btSocket.isConnected()) {
                        Log.i(lg, "Connected to tooth");
                        Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_SHORT).show();
                        lightSwitch.setVisibility(View.VISIBLE);
                        offButton.setVisibility(View.VISIBLE);
                        connectButton.setVisibility(View.INVISIBLE);
                    }
                    else
                        Log.i(lg,"Connection failed");
                    n++;
                }while (!btSocket.isConnected() && n<5);

                if(!btSocket.isConnected()){
                    Toast.makeText(MainActivity.this, "Couldnt connect", Toast.LENGTH_SHORT).show();
                }
            }
        });


        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(lg,"checked changed");

                try {
                    out = btSocket.getOutputStream();
                    Log.i(lg,"output stream initialized");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(lg,"exception in output stream");
                }
                if(lightSwitch.isChecked()){
                    try {
                        out.write(1);
                        if(btSocket.isConnected())
                            Log.i(lg,"connected to tooth");
                        else
                            Log.i(lg,"disconnected");
                        Log.i(lg,"light on");


                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(lg,"exception in light on");
                    }
                }
                else if(!lightSwitch.isChecked()){
                    try {
                        out.write(0);
                        if(btSocket.isConnected())
                            Log.i(lg,"connected to tooth");
                        else
                            Log.i(lg,"disconnected");
                        Log.i(lg,"light off");

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(lg,"exception in light off");
                    }
                }
//                try {
//                    out.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    out.close();
                    btSocket.close();
                    lightSwitch.setVisibility(View.INVISIBLE);
                    offButton.setVisibility(View.INVISIBLE);
                    connectButton.setVisibility(View.VISIBLE);
                    Log.i(lg,"socket closed");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(lg,"exception in disconnect");
                }
            }
        });
    }
}



