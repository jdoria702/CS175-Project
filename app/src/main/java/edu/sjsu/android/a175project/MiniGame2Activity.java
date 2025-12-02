package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class MiniGame2Activity extends AppCompatActivity {

    private View timeBar;
    private TextView characterLabel;
    private TextView centerHint;

    private View wireRed;
    private View wireBlue;
    private View wireGreen;

    private boolean gameOver = false;
    private CountDownTimer timer;
    private long totalTime = 4000;   // will be replaced by GameManager timer if available

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame2);

        // Find views
        timeBar = findViewById(R.id.timeBar);
        TextView gameTitle = findViewById(R.id.gameTitle);
        characterLabel = findViewById(R.id.characterLabel);
        centerHint = findViewById(R.id.centerHint);

        wireRed = findViewById(R.id.wireRed);
        wireBlue = findViewById(R.id.wireBlue);
        wireGreen = findViewById(R.id.wireGreen);

        // Set clear, visible texts
        if (gameTitle != null) {
            gameTitle.setText("TAP THE BLUE WIRE!");
        }

        String selectedChar = ShopManager.getSelectedCharacter(this);
        characterLabel.setText("Character: " + selectedChar);

        centerHint.setText("Tap the BLUE wire before time runs out!");

        // Timer bar will shrink from left to right
        timeBar.setPivotX(0f);

        // Use shared timer duration from GameManager if available
        long duration = GameManagerActivity.getTimerDuration();
        if (duration > 0) {
            totalTime = duration;
        }

        startTimer();

        // Blue wire = correct → pass game
        wireBlue.setOnClickListener(v -> {
            if (gameOver) return;
            gameOver = true;
            if (timer != null) timer.cancel();
            minigamePassed();
        });

        // Red or Green wire = wrong → fail game
        View.OnClickListener wrongWireListener = v -> {
            if (gameOver) return;
            gameOver = true;
            if (timer != null) timer.cancel();
            minigameFailed();
        };

        wireRed.setOnClickListener(wrongWireListener);
        wireGreen.setOnClickListener(wrongWireListener);
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
