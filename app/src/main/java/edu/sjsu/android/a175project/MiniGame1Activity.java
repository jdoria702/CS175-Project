package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import edu.sjsu.android.a175project.ShopManager;

public class MiniGame1Activity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private boolean gameOver = false;
    private View timeBar;
    private View flameOverlay;
    private TextView flameCounter;

    private long maxTime;
    private static final int FLAME_TAPS_REQUIRED = 5;
    private int tapsLeft = FLAME_TAPS_REQUIRED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame1);

        Button tapBtn = findViewById(R.id.tapButton);
        timeBar = findViewById(R.id.timeBar);
        TextView characterLabel = findViewById(R.id.characterLabel);
        ImageView characterBody = findViewById(R.id.characterBody);
        flameOverlay = findViewById(R.id.flameOverlay);
        flameCounter = findViewById(R.id.flameCounter);
        String selectedChar = ShopManager.getSelectedCharacter(this);
        characterLabel.setText("Character: " + selectedChar);
        characterBody.setImageResource(ShopManager.getCharacterDrawable(selectedChar));

        timeBar.setPivotX(0);
        updateFlameUI();

        maxTime = GameManagerActivity.getTimerDuration();

        startCountdown();

        tapBtn.setOnClickListener(v -> {
            if (!gameOver) {
                handleExtinguishTap();
            }
        });
    }

    private void handleExtinguishTap() {
        tapsLeft = Math.max(0, tapsLeft - 1);
        updateFlameUI();
        if (tapsLeft == 0) {
            gameOver = true;
            countDownTimer.cancel();
            minigamePassed();
        }
    }

    private void updateFlameUI() {
        if (flameCounter != null) {
            flameCounter.setText("Flames left: " + tapsLeft);
        }
        if (flameOverlay != null) {
            float alpha = Math.max(0f, (float) tapsLeft / FLAME_TAPS_REQUIRED);
            flameOverlay.setAlpha(alpha);
        }
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(maxTime, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                float fraction = (float) millisUntilFinished / maxTime;
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
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
