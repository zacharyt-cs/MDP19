package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.w3c.dom.Text;

public class ArenaActivity extends AppCompatActivity implements SensorEventListener {
    private String TAG = "ARENALOGTAG";
    private final String FROMANDROID = "\"from\":\"Android\",";

    TextView robotStatus;
    Toolbar toolbar;
    TextView mTitle, xAxisTextView, yAxisTextView;
    ImageButton backBtn, timerReset;
    ImageButton upButton, downButton, leftButton, rightButton;
    ToggleButton waypointTB, sPTB, obstacleTB, autoTB, startButton, exploreToggleButton;
    Button clearButton, updateButton;
    TextView time;
    TextView currentMode;
    Switch tiltSwitch;
    private GridMap gridMap;
    public static long exploreTimer;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private String log = "";

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millisExplore = System.currentTimeMillis() - exploreTimer;
            int secondsExplore = (int) (millisExplore / 1000);
            int minutesExplore = secondsExplore / 60;
            secondsExplore = secondsExplore % 60;

            time.setText(String.format("%02d:%02d", minutesExplore, secondsExplore));
            timerHandler.postDelayed(this, 500);
        }
    };

    boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena);

        toolbar = findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Arena");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        exploreTimer = 0;
        xAxisTextView = findViewById(R.id.xAxisTextView);
        yAxisTextView = findViewById(R.id.yAxisTextView);

        exploreToggleButton = findViewById(R.id.exploreToggleButton);

        upButton = (ImageButton) findViewById(R.id.upButton);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode) {
                    ToastUtil.showToast(getApplicationContext(), "Please exit edit mode");
                    return;
                }

                //if the there is no wall or obstacles in the front then send command
                boolean okToSendCommand = gridMap.move(GridMap.MOVE_TYPE.FORWARD);
                if (okToSendCommand) {
                    sendCommand(COMMAND_TYPE.FORWARD);
                }
                updateXYAxis();
            }
        });
        downButton = (ImageButton) findViewById(R.id.downButton);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode) {
                    ToastUtil.showToast(getApplicationContext(), "Please exit edit mode");
                    return;
                }
                boolean okToSendCommand = gridMap.move(GridMap.MOVE_TYPE.BACK);
                if (okToSendCommand) {
                    sendCommand(COMMAND_TYPE.BACK);
                }
                updateXYAxis();
            }
        });
        leftButton = (ImageButton) findViewById(R.id.leftButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode) {
                    ToastUtil.showToast(getApplicationContext(), "Please exit edit mode");
                    return;
                }
                sendCommand(COMMAND_TYPE.TURNLEFT);
                gridMap.move(GridMap.MOVE_TYPE.TURNLEFT);
                updateXYAxis();
            }
        });
        rightButton = (ImageButton) findViewById(R.id.rightButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode) {
                    ToastUtil.showToast(getApplicationContext(), "Please exit edit mode");
                    return;
                }
                sendCommand(COMMAND_TYPE.TURNRIGHT);
                gridMap.move(GridMap.MOVE_TYPE.TURNRIGHT);
                updateXYAxis();
            }
        });


        time = (TextView) findViewById(R.id.time);
        timerReset = (ImageButton) findViewById(R.id.timerReset);

        gridMap = findViewById(R.id.mapInformationView);
        waypointTB = findViewById(R.id.waypointToggleButton);
        waypointTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editMode = isChecked;
                if (editMode) {
                    ToastUtil.showToast(getApplicationContext(), "Entering edit mode");
                } else {
                    ToastUtil.showToast(getApplicationContext(), "Exiting edit mode");
                }
                sPTB.setEnabled(!isChecked);
                obstacleTB.setEnabled(!isChecked);
                gridMap.setAllowSetWaypoint(isChecked);
            }
        });
        sPTB = findViewById(R.id.sPToggleButton);
        sPTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editMode = isChecked;
                if (editMode) {
                    ToastUtil.showToast(getApplicationContext(), "Entering edit mode");
                } else {
                    ToastUtil.showToast(getApplicationContext(), "Exiting edit mode");
                }
                waypointTB.setEnabled(!isChecked);
                obstacleTB.setEnabled(!isChecked);
                gridMap.setAllowSetStartingPoint(isChecked);
                updateXYAxis();
            }
        });
        obstacleTB = findViewById(R.id.obstacleToggleButton);
        obstacleTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editMode = isChecked;
                if (editMode) {
                    ToastUtil.showToast(getApplicationContext(), "Entering edit mode");
                } else {
                    ToastUtil.showToast(getApplicationContext(), "Exiting edit mode");
                }
                sPTB.setEnabled(!isChecked);
                waypointTB.setEnabled(!isChecked);
                gridMap.setAllowSetObstacle(isChecked);
            }
        });

        currentMode = findViewById(R.id.currentModeTextView);
        autoTB = findViewById(R.id.autoToggleButton);
        autoTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    ToastUtil.showToast(getApplicationContext(), "Auto update mode");
                    currentMode.setText("Auto");
                } else {
                    ToastUtil.showToast(getApplicationContext(), "Manual update mode");
                    currentMode.setText("Manual");
                }
                updateButton.setEnabled(isChecked);
                gridMap.setAutoUpdate(!isChecked);
            }
        });
        updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() { //done
            @Override
            public void onClick(View view) {
                gridMap.promptArenaUpdate();
                sendCommand(COMMAND_TYPE.GETMAPSTATE);
            }
        });


        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        robotStatus = findViewById(R.id.robotStatusTxt);
        robotStatus.setMovementMethod(new ScrollingMovementMethod());


        clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() { //done
            @Override
            public void onClick(View view) {
                gridMap.clearMap();
                sendCommand(COMMAND_TYPE.CLEAR);
                xAxisTextView.setText("0");
                yAxisTextView.setText("0");
            }
        });

        startButton = findViewById(R.id.startButton);
        startButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!startButton.isChecked()) { //not checked
                    timerHandler.removeCallbacks(timerRunnable);
                }
                if (startButton.isChecked()) { //checked
                    if (editMode) {
                        ToastUtil.showToast(getApplicationContext(), "Please exit edit mode");
                        startButton.toggle();
                        return;
                    }
                    exploreTimer = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    sendCommand(exploreToggleButton.isChecked() ? COMMAND_TYPE.BEGINFASTESTPATH : COMMAND_TYPE.BEGINEXPLORATION);
                }
            }
        });
        timerReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time.setText("00:00");
                if (startButton.isChecked()) {
                    startButton.toggle();
                }
                timerHandler.removeCallbacks(timerRunnable);
            }
        });
        xAxisTextView = (TextView) findViewById(R.id.xAxisTextView);
        yAxisTextView = (TextView) findViewById(R.id.yAxisTextView);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("incomingMessage"));

        tiltSwitch = (Switch) findViewById(R.id.tiltSwitch);
        tiltSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (tiltSwitch.isChecked()) {
                    ToastUtil.showToast(ArenaActivity.this, "Tilt Control is Turned On");
                    tiltSwitch.setPressed(true);
                    mSensorManager.registerListener(ArenaActivity.this, mSensor, mSensorManager.SENSOR_DELAY_NORMAL);
                    sensorHandler.post(sensorDelay);
                } else {
                    try {
                        mSensorManager.unregisterListener(ArenaActivity.this);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();

                    }
                    sensorHandler.removeCallbacks(sensorDelay);
                }

            }
        });

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("receivedMessage");
            if (text != null && !text.equals("")) {
                String[] textArr = text.split(";");
                Log.d(TAG, "Fulltext:" + text);

                for (String s : textArr) {
                    Log.d(TAG, "String:" + s);

                    if (s == null || s.equals("")) {
                        continue;
                    }
                    updateRobotStatus(s);
                    gridMap.updateMap(s);
                    updateLog(s);
                    //update the coordinate here
                    updateXYAxis();
                }
            }

        }
    };

    private void updateXYAxis() {
        xAxisTextView.setText(Integer.toString(gridMap.getRobotX()));
        yAxisTextView.setText(Integer.toString(gridMap.getRobotY()));
    }


    public enum COMMAND_TYPE {
        FORWARD("W"),
        BACK("S"),
        TURNLEFT("A"),
        TURNRIGHT("D"),
        BEGINEXPLORATION("ex"),
        BEGINFASTESTPATH("fp"),
        GETMAPSTATE("gs"),
        CLEAR("clr");


        private final String value;

        COMMAND_TYPE(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    private void updateLog(String msg) {
        return;
//        log += msg + "\n\n";
//        logTxt.setText(log);
    }

    public void sendCommand(ArenaActivity.COMMAND_TYPE type) {
        String msg = ";{" + FROMANDROID + "\"com\":\"" + type.getValue() + "\"}";
        updateLog(msg);
        try {
            byte[] bytes = msg.getBytes();
            BluetoothConnectionService.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scrollBottom(TextView textView) {
        final Layout layout = textView.getLayout();
        if (layout != null) {
            int scrollDelta = layout.getLineBottom(textView.getLineCount() - 1)
                    - textView.getScrollY() - textView.getHeight();
            if (scrollDelta > 0)
                textView.scrollBy(0, scrollDelta);
        }
    }

    private void updateRobotStatus(String update) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode input = objectMapper.readTree(update);
            if (input.get("status") != null) {
                String status = input.get("status").asText() + "\n";
                String curText = robotStatus.getText().toString();
                robotStatus.setText(curText + status);
                scrollBottom(robotStatus);
            }
        } catch (Exception e) {
            Log.d(TAG, "json conversion not successful");
            Log.d(TAG, "String:" + update);
            e.printStackTrace();
        }
    }

    Handler sensorHandler = new Handler();
    boolean sensorFlag = false;

    private final Runnable sensorDelay = new Runnable() {
        @Override
        public void run() {
            sensorFlag = true;
            sensorHandler.postDelayed(this, 1000);
        }
    };

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if (sensorFlag) {
            if (editMode) {
                ToastUtil.showToast(getApplicationContext(), "Please exit edit mode");
                return;
            }
            if (y < -2) {
                boolean okToSendCommand = gridMap.move(GridMap.MOVE_TYPE.FORWARD);
                if (okToSendCommand) {
                    sendCommand(COMMAND_TYPE.FORWARD);
                }
                updateXYAxis();
            } else if (y > 2) {
                boolean okToSendCommand = gridMap.move(GridMap.MOVE_TYPE.BACK);
                if (okToSendCommand) {
                    sendCommand(COMMAND_TYPE.BACK);
                }
                updateXYAxis();
            } else if (x > 2) {
                boolean okToSendCommand = gridMap.move(GridMap.MOVE_TYPE.TURNLEFT);
                if (okToSendCommand) {
                    sendCommand(COMMAND_TYPE.TURNLEFT);
                }
                updateXYAxis();
            } else if (x < -2) {
                boolean okToSendCommand = gridMap.move(GridMap.MOVE_TYPE.TURNRIGHT);
                if (okToSendCommand) {
                    sendCommand(COMMAND_TYPE.TURNRIGHT);
                }
                updateXYAxis();
            }
        }
        sensorFlag = false;
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
        if (id == R.id.messages) {
            Intent intent1 = new Intent(this, MessagingActivity.class);
            this.startActivity(intent1);
            return true;
        }
        if (id == R.id.bluetooth) {
            Intent intent2 = new Intent(this, MainActivity.class);
            this.startActivity(intent2);
            return true;
        }
        if (id == R.id.menu) {
            Intent intent3 = new Intent(this, Dashboard.class);
            this.startActivity(intent3);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

