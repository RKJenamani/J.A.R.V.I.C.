package com.example.cb_jarvic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import java.io.*;
import android.widget.ListView;
import android.widget.Toast;
import java.net.*;
import java.lang.*;

public class ChatActivity extends AppCompatActivity {
    private EditText ed1;
    private ImageButton sendButton;
    private DataOutputStream dos;
    private DataInputStream dis;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private Socket s;
    private String previous_chat;
    MainActivity.socket_pass socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.showOverflowMenu();
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }
//        changeStatusBarColor();
        Intent i = getIntent();
        try {
            socket =  ((MainActivity.ObjectWrapperForBinder)i.getExtras().getBinder("socket_value")).getData();
            s = socket.return_socket();
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

//        get_previous_chat();

        ed1 = (EditText) findViewById(R.id.editText);
        sendButton = (ImageButton)findViewById(R.id.buttonSend);
        Toast.makeText(getApplicationContext(), "Welcome!!", Toast.LENGTH_SHORT).show();
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String message = ed1.getText().toString();
                        System.out.println(message);
                        if (message.length() > 0) {
                            try{
                                dos.writeUTF(message + "\n");
                                ed1.getText().clear();
                                final Message msg = new Message(message, "Me", true);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
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
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
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
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
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

            //moveTaskToBack(false);

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
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
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
    public void get_previous_chat()
    {
        try {
            try{
                previous_chat = dis.readUTF();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            if(!previous_chat.equals("p")) return;
            String msg = dis.readUTF();
//            System.out.println("\nJ.A.R.V.I.C. - " + msg);
            String[] parts = msg.split("$");
            for (int i = 0; i < parts.length; i += 2)
            {
                final Message message;
                if(parts[i].equals("u"))
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

