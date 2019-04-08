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
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;


public class ForgetPasswordActivity extends Activity {
    private transient Button b1;
    private transient EditText ed1, ed2, ed3;
    private transient TextView tv1;
    private InetAddress ip;
    private int port;
    private transient DataInputStream dis;
    private transient DataOutputStream dos;
    private MainActivity.socket_pass socket;
    private transient Socket s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        Intent i = getIntent();
        if (i.getExtras() != null) {
            try {
                socket = ((MainActivity.ObjectWrapperForBinder) i.getExtras().getBinder("socket_value")).getData();
                ip = socket.hostname;
                port = socket.port;
                s = socket.return_socket();
                if(s != null) {
                    dos = new DataOutputStream(socket.s.getOutputStream());
                    dis = new DataInputStream(socket.s.getInputStream());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        b1 = (Button) findViewById(R.id.button2);
        ed1 = (EditText) findViewById(R.id.editText8);
        ed2 = (EditText) findViewById(R.id.editText10);
        ed3 = (EditText) findViewById(R.id.editText9);
        tv1 = (TextView) findViewById(R.id.textView7);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!ed1.getText().toString().equals("") &&
                            !ed2.getText().toString().equals("") && !ed3.getText().toString().equals("")) {
                        connectToServer(ed1.getText().toString(), ed2.getText().toString(), ed3.getText().toString());
                    } else {
                        if (ed1.getText().toString().equals(""))
                            ed1.setError("Enter Email address");
                        if (ed2.getText().toString().equals(""))
                            ed2.setError("Enter contact number");
                        if (ed3.getText().toString().equals(""))
                            ed3.setError("Enter Password");
                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                socket.get_socket(s, ip, port);
                final Bundle bundle = new Bundle();
                bundle.putBinder("socket_value", new MainActivity.ObjectWrapperForBinder(socket));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

    }

    public void connectToServer(final String email, final String contact, final String password) throws Exception
    {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        if(s == null) {
                            s = new Socket(ip, port);
                            dos = new DataOutputStream(s.getOutputStream());
                            dis = new DataInputStream(s.getInputStream());
                        }
                        socket = new MainActivity.socket_pass();
                        socket.get_socket(s, ip, port);
                        String send = email + "$" + contact + "$" + password;
                        dos.writeUTF(send);
                        System.out.println(send);

                        String success = dis.readUTF();
                        System.out.println("\nJ.A.R.V.I.C. - " + success);
                        if(success.equals("y")) {
                            final Bundle bundle = new Bundle();
                            bundle.putBinder("socket_value", new MainActivity.ObjectWrapperForBinder(socket));
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                                }
                            });
                            final Bundle bundle = new Bundle();
                            bundle.putBinder("socket_value", new MainActivity.ObjectWrapperForBinder(socket));
                            Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to go back to login?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        if (s != null) {
                            final Bundle bundle = new Bundle();
                            bundle.putBinder("socket_value", new MainActivity.ObjectWrapperForBinder(socket));
                            intent.putExtras(bundle);
                        }
                        startActivity(intent);
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
