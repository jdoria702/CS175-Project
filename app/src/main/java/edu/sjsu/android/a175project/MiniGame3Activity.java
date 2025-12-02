package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MiniGame3Activity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private boolean gameOver = false;

    private View timeBar;
    private ImageView dirtOverlay;
    private long maxTime;

    private float dirtAlpha = 1.0f;   // full dirt
    private final float CLEAN_AMOUNT = 0.08f;  // SWIPES NEEDED

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame3);

        timeBar = findViewById(R.id.timeBar);
        dirtOverlay = findViewById(R.id.dirtOverlay);
        timeBar.setPivotX(0);

        // Set character
        ImageView characterBody = findViewById(R.id.characterBody);
        String selected = ShopManager.getSelectedCharacter(this);
        characterBody.setImageResource(ShopManager.getCharacterDrawable(selected));

        maxTime = GameManagerActivity.getTimerDuration();

        startCountdown();

        dirtOverlay.setOnTouchListener((v, event) -> {
            if (!gameOver) handleCleaningGesture(event);
            return true;
        });
    }

    private void handleCleaningGesture(MotionEvent event) {
        // Clean a little bit with each motion
        if (event.getAction() == MotionEvent.ACTION_MOVE ||
                event.getAction() == MotionEvent.ACTION_DOWN) {

            dirtAlpha -= CLEAN_AMOUNT;
            dirtAlpha = Math.max(0f, dirtAlpha);
            dirtOverlay.setAlpha(dirtAlpha);

            if (dirtAlpha <= 0f) {
                gameOver = true;
                countDownTimer.cancel();
                minigamePassed();
            }
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
