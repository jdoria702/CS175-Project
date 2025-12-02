package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;

public class MiniGame1Activity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private boolean gameOver = false;
    private View timeBar;
    private ImageView flameOverlay;

    private long maxTime;
    private static final int FLAME_TAPS_REQUIRED = 7;
    private int tapsLeft = FLAME_TAPS_REQUIRED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame1);

        timeBar = findViewById(R.id.timeBar);
        TextView titleFire = findViewById(R.id.titleFire);
        ImageView characterBody = findViewById(R.id.characterBody);
        flameOverlay = findViewById(R.id.flameOverlay);
        String selectedChar = ShopManager.getSelectedCharacter(this);
        characterBody.setImageResource(ShopManager.getCharacterDrawable(selectedChar));
        applyFireTitleColor(titleFire);

        timeBar.setPivotX(0);
        updateFlameUI();

        maxTime = GameManagerActivity.getTimerDuration();

        startCountdown();

        flameOverlay.setOnClickListener(v -> {
            if (!gameOver) {
                handleExtinguishTap();
            }
        });
    }

    private void applyFireTitleColor(TextView titleFire) {
        if (titleFire == null) return;
        String text = "Blow out the FIRE!";
        SpannableString ss = new SpannableString(text);
        int start = text.indexOf("FIRE");
        if (start >= 0) {
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#FF2D2D")),
                    start,
                    start + 4,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        titleFire.setText(ss);
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