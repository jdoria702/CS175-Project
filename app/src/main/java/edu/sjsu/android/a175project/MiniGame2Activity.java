package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MiniGame2Activity extends AppCompatActivity {

    private enum WireTarget {
        RED, BLUE, GREEN
    }

    private View timeBar;
    private TextView gameTitle;
    private TextView centerHint;

    private LinearLayout wireRow;
    private View wireRed;
    private View wireBlue;
    private View wireGreen;

    private WireTarget correctWire;
    private boolean gameOver = false;
    private CountDownTimer timer;
    private long totalTime = 4000;   // will be replaced by GameManager timer if available

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame2);

        // Find views
        timeBar = findViewById(R.id.timeBar);
        gameTitle = findViewById(R.id.gameTitle);
        centerHint = findViewById(R.id.centerHint);

        wireRow = findViewById(R.id.wireRow);
        wireRed = findViewById(R.id.wireRed);
        wireBlue = findViewById(R.id.wireBlue);
        wireGreen = findViewById(R.id.wireGreen);

        String selectedChar = ShopManager.getSelectedCharacter(this);

        // Timer bar will shrink from left to right
        timeBar.setPivotX(0f);

        // Use shared timer duration from GameManager if available
        long duration = GameManagerActivity.getTimerDuration();
        if (duration > 0) {
            totalTime = duration;
        }

        // Randomize the correct wire and positions, then update instruction text
        shuffleWirePositions();
        chooseTargetWire();

        startTimer();

        wireRed.setOnClickListener(v -> handleWireTap(WireTarget.RED));
        wireBlue.setOnClickListener(v -> handleWireTap(WireTarget.BLUE));
        wireGreen.setOnClickListener(v -> handleWireTap(WireTarget.GREEN));
    }

    private void startTimer() {
        timer = new CountDownTimer(totalTime, 16) {
            @Override
            public void onTick(long millisUntilFinished) {
                float fraction = (float) millisUntilFinished / totalTime;
                timeBar.setScaleX(fraction);
            }

            @Override
            public void onFinish() {
                if (!gameOver) {
                    gameOver = true;
                    minigameFailed();
                }
            }
        }.start();
    }

    private void chooseTargetWire() {
        WireTarget[] options = WireTarget.values();
        correctWire = options[new Random().nextInt(options.length)];
        updateInstructionText();
    }

    private void updateInstructionText() {
        String colorName = "UNKNOWN";
        switch (correctWire) {
            case RED:
                colorName = "RED";
                break;
            case BLUE:
                colorName = "BLUE";
                break;
            case GREEN:
                colorName = "GREEN";
                break;
        }

        if (gameTitle != null) {
            gameTitle.setText("TAP THE " + colorName + " WIRE!");
        }
        centerHint.setText("Tap the " + colorName + " wire before time runs out!");
    }

    private void shuffleWirePositions() {
        if (wireRow == null) return;

        List<View> wires = Arrays.asList(wireRed, wireBlue, wireGreen);
        Collections.shuffle(wires, new Random());

        wireRow.removeAllViews();

        int marginPx = dpToPx(24);
        int sizePx = dpToPx(80);

        for (int i = 0; i < wires.size(); i++) {
            View wire = wires.get(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePx, sizePx);
            if (i < wires.size() - 1) {
                params.setMarginEnd(marginPx);
            }
            wireRow.addView(wire, params);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void handleWireTap(WireTarget tappedWire) {
        if (gameOver) return;
        gameOver = true;
        if (timer != null) timer.cancel();

        if (tappedWire == correctWire) {
            minigamePassed();
        } else {
            minigameFailed();
        }
    }

    private void minigamePassed() {
        setResult(RESULT_OK);
        GameManagerActivity.increaseDifficulty();
        finish();
    }

    private void minigameFailed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
