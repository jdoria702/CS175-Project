package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import edu.sjsu.android.a175project.ShopManager;

public class MiniGame3Activity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private boolean gameOver = false;
    private View timeBar;

    private long maxTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame3);

        Button tapBtn = findViewById(R.id.btn_win3);
        timeBar = findViewById(R.id.timeBar);
        TextView characterLabel = findViewById(R.id.characterLabel);
        characterLabel.setText("Character: " + ShopManager.getSelectedCharacter(this));

        timeBar.setPivotX(0);

        maxTime = GameManagerActivity.getTimerDuration();

        startCountdown();

        tapBtn.setOnClickListener(v -> {
            if (!gameOver) {
                gameOver = true;
                countDownTimer.cancel();
                minigamePassed();
            }
        });
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
