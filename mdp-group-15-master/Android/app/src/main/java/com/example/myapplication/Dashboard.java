package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Dashboard extends AppCompatActivity {

    TextView mapPage;
    TextView arenaPage;
    TextView btPage;
    TextView messagePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        arenaPage = (TextView) findViewById(R.id.arenaPage);
        btPage = (TextView) findViewById(R.id.btPage);
        messagePage = (TextView) findViewById(R.id.messagePage);

        btPage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent mapIntent = new Intent(Dashboard.this, MainActivity.class);
                startActivity(mapIntent);
            }
        });
        messagePage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent messageIntent = new Intent(Dashboard.this, MessagingActivity.class);
                startActivity(messageIntent);
            }
        });
        arenaPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent arenaIntent = new Intent(Dashboard.this, ArenaActivity.class);
                startActivity(arenaIntent);
            }
        });

    }

}
