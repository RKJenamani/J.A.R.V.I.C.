package com.example.cb_jarvic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.*;
import java.net.*;
import java.lang.*;

public class ChatActivity extends AppCompatActivity {
    private EditText ed1;
    private ImageButton sendButton;
    private InetAddress ip;
    private int port;
    private DataOutputStream dos;
    private DataInputStream dis;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private Socket s;
    MainActivity.socket_pass socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_chat);
        setSupportActionBar(myToolbar);
        myToolbar.showOverflowMenu();
        getSupportActionBar().setTitle("");

        Intent i = getIntent();
        try {
            socket =  ((MainActivity.ObjectWrapperForBinder)i.getExtras().getBinder("socket_value")).getData();
            s = socket.return_socket();
            ip = socket.hostname;
            port = socket.port;
            dos = new DataOutputStream(socket.s.getOutputStream());
            dis = new DataInputStream(socket.s.getInputStream());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        ed1 = (EditText) findViewById(R.id.editText);
        sendButton = (ImageButton)findViewById(R.id.buttonSend);
        sendButton.setBackgroundColor(Color.parseColor("#e1e1e1"));

        ed1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                sendButton.setBackgroundColor(Color.parseColor("#e1e1e1"));

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() != 0) {
                    sendButton.setBackgroundColor(Color.parseColor("#096E64"));
                } else {
                    sendButton.setBackgroundColor(Color.parseColor("#e1e1e1"));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        Toast.makeText(getApplicationContext(), "Welcome!!", Toast.LENGTH_SHORT).show();
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String message = ed1.getText().toString();
                        System.out.println(message);

                        if (message.length() > 0) {
                            try{
                                dos.writeUTF(message + "\n");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ed1.getText().clear();
                                        final Message msg = new Message(message, "Me", true);
                                        messageAdapter.add(msg);
                                        // scroll the ListView to the last added element
                                        messagesView.setSelection(messagesView.getCount() - 1);
                                    }
                                });
                            }
                            catch(Exception e) {e.printStackTrace();}
                        }
                    }
                }).start();
            }
        });
        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    onMessage();
                }
            }).start();
        }
        catch(Exception e) {e.printStackTrace();}
    }

    public void onMessage() {
        while(true) {
            try {
                String msg = dis.readUTF();
                System.out.println(msg);
                if(msg.equals("p")) continue;
                if(setStatusColor(msg)) continue;

                String[] parts = msg.split("\\$");
                System.out.println(parts.length);
                if(parts.length > 1) {
                    for (int i = 0; i < parts.length; i += 2) {
                        final Message message;
                        if (parts[i].equals("u"))
                            message = new Message(parts[i + 1], "J.A.R.V.I.C.", true);
                        else
                            message = new Message(parts[i + 1], "J.A.R.V.I.C.", false);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageAdapter.add(message);
                                // scroll the ListView to the last added element
                                messagesView.setSelection(messagesView.getCount() - 1);
                            }
                        });
                    }
                }
                else {
                    final Message message = new Message(msg, "J.A.R.V.I.C.", false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.add(message);
                            // scroll the ListView to the last added element
                            messagesView.setSelection(messagesView.getCount() - 1);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean setStatusColor(String msg)
    {
        if(msg.equals("calm"))
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary_sent));
                }
            });
            return true;
        }
        if(msg.equals("normal"))
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            });
            return true;
        }
        if(msg.equals("critical"))
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
                }
            });
            return true;
        }
        if(msg.equals("sad"))
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorTextInfo));
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out: {
                try {
                    s.close();
                    socket.get_socket(s, ip, port);
                    final Bundle bundle = new Bundle();
                    bundle.putBinder("socket_value", new MainActivity.ObjectWrapperForBinder(socket));
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                break;
            }
            case R.id.action_clear_chat: {
                messageAdapter = new MessageAdapter(this);
                messagesView.setAdapter(messageAdapter);
                break;
            }
            // case blocks for other MenuItems (if any)
        }
        return true;
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
                .setMessage("Do you want to Logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            s.close();
                            socket.get_socket(s, ip, port);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            final Bundle bundle = new Bundle();
                            bundle.putBinder("socket_value", new MainActivity.ObjectWrapperForBinder(socket));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
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

