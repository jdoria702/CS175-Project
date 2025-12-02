package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class MiniGame1Activity extends AppCompatActivity {


    private View timeBar;
    private TextView characterLabel;
    private TextView centerHint;

    private View wireRed;
    private View wireBlue;
    private View wireGreen;

    private boolean gameOver = false;
    private CountDownTimer timer;
    private long maxTime;   // use same idea as MiniGame1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame2);

        // find views
        timeBar = findViewById(R.id.timeBar);
        characterLabel = findViewById(R.id.characterLabel);
        centerHint = findViewById(R.id.centerHint);

        wireRed = findViewById(R.id.wireRed);
        wireBlue = findViewById(R.id.wireBlue);
        wireGreen = findViewById(R.id.wireGreen);

        // show selected character name (same idea as other screens)
        String selectedChar = ShopManager.getSelectedCharacter(this);
        characterLabel.setText("Character: " + selectedChar);

        // shrink timer from the left
        timeBar.setPivotX(0);

        // get timer duration from GameManager so difficulty can change
        maxTime = GameManagerActivity.getTimerDuration();

        setupWireClicks();
        startTimer();
    }

    private void setupWireClicks() {
        // wrong wires
        wireRed.setOnClickListener(v -> handleWrongTap());
        wireGreen.setOnClickListener(v -> handleWrongTap());

        // correct wire
        wireBlue.setOnClickListener(v -> handleCorrectTap());
    }

    private void handleCorrectTap() {
        if (gameOver) return;
        gameOver = true;
        centerHint.setText("Bomb defused!");
        minigamePassed();
    }

    private void handleWrongTap() {
        if (gameOver) return;
        gameOver = true;
        centerHint.setText("Wrong wire!");
        minigameFailed();
    }

    private void startTimer() {
        timer = new CountDownTimer(maxTime, 16) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (gameOver) {
                    cancel();
                    return;
                }
                float fraction = (float) millisUntilFinished / maxTime;
                timeBar.setScaleX(fraction);
            }

            @Override
            public void onFinish() {
                if (gameOver) return;
                gameOver = true;
                centerHint.setText("Too slow!");
                minigameFailed();
            }
        }.start();
    }

    private void minigamePassed() {
        // same pattern as MiniGame1
        if (timer != null) timer.cancel();
        setResult(RESULT_OK);
        GameManagerActivity.increaseDifficulty();
        finish();
    }

    private void minigameFailed() {
        // same pattern as MiniGame1
        if (timer != null) timer.cancel();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}