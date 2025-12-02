package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    private long totalTime = 4000; // 4 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame2);

        timeBar = findViewById(R.id.timeBar);
        characterLabel = findViewById(R.id.characterLabel);
        centerHint = findViewById(R.id.centerHint);

        wireRed = findViewById(R.id.wireRed);
        wireBlue = findViewById(R.id.wireBlue);
        wireGreen = findViewById(R.id.wireGreen);

        setupWireClicks();
        startTimer();
    }

    private void setupWireClicks() {
        wireRed.setOnClickListener(v -> handleWrongTap());
        wireGreen.setOnClickListener(v -> handleWrongTap());

        wireBlue.setOnClickListener(v -> handleCorrectTap());
    }

    private void handleCorrectTap() {
        if (gameOver) return;

        gameOver = true;
        centerHint.setText("Bomb defused! You win!");
        finishGame(true);
    }

    private void handleWrongTap() {
        if (gameOver) return;

        gameOver = true;
        centerHint.setText("Wrong wire! BOOM!");
        finishGame(false);
    }

    private void startTimer() {
        timer = new CountDownTimer(totalTime, 16) {
            @Override
            public void onTick(long millisLeft) {
                if (gameOver) {
                    timer.cancel();
                    return;
                }

                float fraction = millisLeft / (float) totalTime;
                int newWidth = (int) (timeBar.getRootView().getWidth() * fraction);

                timeBar.getLayoutParams().width = newWidth;
                timeBar.requestLayout();
            }

            @Override
            public void onFinish() {
                if (gameOver) return;

                gameOver = true;
                centerHint.setText("Too slow! You exploded!");
                finishGame(false);
            }
        };

        timer.start();
    }

    private void finishGame(boolean success) {
        if (timer != null)
            timer.cancel();

        Intent result = new Intent();
        result.putExtra("MINIGAME_RESULT", success);
        setResult(RESULT_OK, result);

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}