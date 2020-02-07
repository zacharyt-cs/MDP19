package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MessagingActivity extends AppCompatActivity {

    Button sendBtn, clearBtn;
    ImageButton backBtn;
    Button f1Btn, f2Btn, reconfigureBtn;

    TextView receivedTextView, sentTextView;
    EditText inputText;
    TextView mTitle;
    Toolbar toolbar;

    final String TAG = "MESSAGEACTIVITYTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        toolbar = findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Message");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        inputText = findViewById(R.id.inputText);

        receivedTextView = findViewById(R.id.receivedTextView);
        receivedTextView.setMovementMethod(new ScrollingMovementMethod());

        sentTextView = findViewById(R.id.sentTextView);
        sentTextView.setMovementMethod(new ScrollingMovementMethod());

        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToBeSent = inputText.getText().toString();
                sendText(textToBeSent);

            }
        });

        clearBtn = findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sentTextView.setText("");
                receivedTextView.setText("");
                inputText.setText("");
            }
        });

        f1Btn = findViewById(R.id.f1);
        f1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText(getF1Text());
            }
        });

        f2Btn = findViewById(R.id.f2Btn);
        f2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText(getF2Text());
            }
        });

        //reconfigurable part
        reconfigureBtn = findViewById(R.id.reconfigBtn);


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.reconfigure_dialog);

        final EditText f1Txt = dialog.findViewById(R.id.f1Txt);
        final EditText f2Txt = dialog.findViewById(R.id.f2Txt);

        reconfigureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                f1Txt.setText(getF1Text());
                f2Txt.setText(getF2Text());
                dialog.show();
            }
        });


        Button reconSave = dialog.findViewById(R.id.ReconSave);
        reconSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.reconfigurable), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.f1Mapping), f1Txt.getText().toString());
                editor.putString(getString(R.string.f2Mapping), f2Txt.getText().toString());
                editor.commit();
                dialog.cancel();
            }
        });

        Button reconCancel = dialog.findViewById(R.id.ReconCancel);
        reconCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("incomingMessage"));
    }


    public void sendText(String msg) {
        if (msg == null || msg.equals("")) {
            return;
        }
        String sentText = sentTextView.getText().toString();
//        String json = "{\"text\":+\"" + msg + "\"}";
        sentText += msg + "\n";

        try {
            byte[] bytes = msg.getBytes();
            BluetoothConnectionService.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        sentTextView.setText(sentText);
        scrollBottom(sentTextView);
        inputText.setText("");
    }

    public String getF1Text() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.reconfigurable), Context.MODE_PRIVATE);
        String f1 = sharedPref.getString(getString(R.string.f1Mapping), "");
        Log.d(TAG, "f1 = " + f1);
        return f1;
    }

    public String getF2Text() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.reconfigurable), Context.MODE_PRIVATE);
        String f2 = sharedPref.getString(getString(R.string.f2Mapping), "");
        Log.d(TAG, "f2 = " + f2);
        return f2;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("receivedMessage");
            String receivedText = receivedTextView.getText().toString();
            receivedText += text + "\n";
            receivedTextView.setText(receivedText);
            scrollBottom(receivedTextView);

        }
    };

    private void scrollBottom(TextView textView) {
        final Layout layout = textView.getLayout();
        if (layout != null) {
            int scrollDelta = layout.getLineBottom(textView.getLineCount() - 1)
                    - textView.getScrollY() - textView.getHeight();
            if (scrollDelta > 0)
                textView.scrollBy(0, scrollDelta);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.bluetooth) {
            Intent intent1 = new Intent(this, MainActivity.class);
            this.startActivity(intent1);
            return true;
        }
        if (id == R.id.menu) {
            Intent intent2 = new Intent(this, Dashboard.class);
            this.startActivity(intent2);
            return true;
        }
        if (id == R.id.arena) {
            Intent intent4 = new Intent(this, ArenaActivity.class);
            this.startActivity(intent4);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
