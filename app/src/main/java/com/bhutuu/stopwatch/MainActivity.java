package com.bhutuu.stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TextView timeTextView, scrambleTextView;
    Button resetBtn, startStopBtn;

    private boolean isRunning = false;
    private boolean isInspected = false;
    private boolean isInspectionStarted = false;
    private long startTime = 0L;
    private long elapsedTime = 0L;
    private long inspectionTimeRemaining = 0L;
    private Handler handler = new Handler();
    private static final String[] MOVES = {"U", "D", "L", "R", "F", "B", "U'", "D'", "L'", "R'", "F'", "B'", "U2", "D2", "L2", "R2", "F2", "B2"};
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeTextView = findViewById(R.id.time_text_view);
        resetBtn = findViewById(R.id.reset_btn);
        startStopBtn = findViewById(R.id.start_stop_btn);
        scrambleTextView = findViewById(R.id.scramble_generated_text_view);
        generateRandomScramble();
        updateTimeTextView(15000);
        startStopBtn.setText("Inspect");

        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isInspected && !isRunning && !isInspectionStarted) {
                    inspectionTimeRemaining = SystemClock.uptimeMillis() + 15000;
                    isInspectionStarted = true;
                    handler.postDelayed(updateTimer, 0);
                } else if(isInspectionStarted) {
                    isInspected=true;
                    startStopBtn.setText("Stop");
                    if(isRunning) {
                        isRunning = false;
                        handler.removeCallbacks(updateTimer);
                        elapsedTime = SystemClock.uptimeMillis() - startTime;
                        startStopBtn.setText("Resume");
                    } else {
                        isRunning=true;
                        startTime = SystemClock.uptimeMillis() - elapsedTime;
                        handler.postDelayed(updateTimer, 0);
                        startStopBtn.setText("Stop");
                    }
                }
            }
        });
        startStopBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startTime = SystemClock.uptimeMillis();
                elapsedTime=0L;
                isInspectionStarted=true;
                isInspected=true;
                isRunning=false;
                handler.removeCallbacks(updateTimer);
                timeTextView.setText("00:00:00");
                startStopBtn.setText("Start");
                return true;
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = false;
                isInspected = false;
                isInspectionStarted = false;
                handler.removeCallbacks(updateTimer);
                elapsedTime = 0L;
                startTime = 0L;
                inspectionTimeRemaining = 0L;
                generateRandomScramble();
                timeTextView.setText("00:15:00");
                startStopBtn.setText("Inspect");
            }
        });
    }
    private void generateRandomScramble() {
        int length = 20;
        StringBuilder scramble = new StringBuilder();
        Random random = new Random();
        String prevMove = "";
        for (int i = 0; i < length; i++) {
            String move;
            do {
                int moveIndex = random.nextInt(MOVES.length);
                move = MOVES[moveIndex];
            } while (move.equals(prevMove)); // Avoid consecutive identical moves
            scramble.append(move).append(" ");
            prevMove = move;
        }
         scrambleTextView.setText(scramble.toString().trim());
    }
    private void updateTimeTextView(long timeInMillis) {
        int minutes = (int) (timeInMillis / (1000 * 60));
        int seconds = (int) ((timeInMillis / 1000) % 60);
        int milliseconds = (int) ((timeInMillis % 1000) / 10);
        String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", minutes, seconds, milliseconds);
        timeTextView.setText(time);
    }

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            if(!isInspected && !isRunning && isInspectionStarted) {
                long timeInMilliseconds = inspectionTimeRemaining - SystemClock.uptimeMillis();
                if(timeInMilliseconds >=0) {
                    updateTimeTextView(timeInMilliseconds);
                    handler.postDelayed(this, 10);
                    startStopBtn.setText("Start");
                } else {
                    isInspected =true;
                    timeTextView.setText("00:00:00");
                    startStopBtn.setText("Stop");
                    isRunning=true;
                    startTime = SystemClock.uptimeMillis() - elapsedTime;
                    handler.postDelayed(updateTimer, 0);
                    startStopBtn.setText("Stop");
                }
            } else if(isRunning) {
                long timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                updateTimeTextView(timeInMilliseconds);
                handler.postDelayed(this, 10); // Update every 10 milliseconds for smoother display
            }

        }
    };
}

