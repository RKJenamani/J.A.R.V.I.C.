package com.example.cb_jarvic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.InetAddress;


public class SettingsActivity extends Activity {
    private transient Button b1;
    private transient EditText ed1,ed2;
    private static MainActivity.socket_pass socket;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        b1 = (Button)findViewById(R.id.settings_button);
        ed1 = (EditText)findViewById(R.id.hostname);
        ed2 = (EditText)findViewById(R.id.port);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try {
                    if (!ed1.getText().toString().equals("") &&
                            !ed2.getText().toString().equals("")) {
                        saveSocket(ed1.getText().toString(), ed2.getText().toString());
                    } else {
                        if(ed1.getText().toString().equals(""))
                            ed1.setError("Enter hostname");
                        if(ed2.getText().toString().equals(""))
                            ed2.setError("Enter server port");
                        Toast.makeText(getApplicationContext(), "Invalid Entry!", Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void saveSocket(final String hostname, final String port)
    {
        try {
            socket = new MainActivity.socket_pass();
            socket.hostname = InetAddress.getByName(hostname);
            socket.port = Integer.parseInt(port);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        socket.s = null;
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putBinder("socket_value", new MainActivity.ObjectWrapperForBinder(socket));
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                })
                .show();
    }

}
