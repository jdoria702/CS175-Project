package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MiniGame5Activity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private boolean gameOver = false;
    private View timeBar;
    private ImageView characterView;
    private ImageView trampolineView;
    private View gameArea;

    private long maxTime;
    private ValueAnimator fallAnimator;
    private float dragOffsetX;
    private final int[] gameAreaLocation = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame5);

        timeBar = findViewById(R.id.timeBar);
        characterView = findViewById(R.id.characterBody);
        trampolineView = findViewById(R.id.trampoline);
        gameArea = findViewById(R.id.gameArea);

        String selected = ShopManager.getSelectedCharacter(this);
        characterView.setImageResource(ShopManager.getCharacterDrawable(selected));
        timeBar.setPivotX(0);

        maxTime = GameManagerActivity.getTimerDuration();
        if (maxTime <= 0) maxTime = 3000; // fallback to 3 seconds if no timer is set

        trampolineView.setOnTouchListener(this::handleTrampolineDrag);

        gameArea.post(this::startGame);
    }

    private void startGame() {
        startCountdown();
        startFallAnimation();
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
                    stopFallAnimation();
                }
            }
        }.start();
    }

    private void startFallAnimation() {
        float maxX = Math.max(0, gameArea.getWidth() - characterView.getWidth());
        float startX = (float) (Math.random() * maxX);
        characterView.setX(startX);

        float startY = -characterView.getHeight();
        float endY = gameArea.getHeight() - characterView.getHeight();

        fallAnimator = ValueAnimator.ofFloat(startY, endY);
        fallAnimator.setDuration(maxTime);
        fallAnimator.addUpdateListener(anim -> {
            float y = (float) anim.getAnimatedValue();
            characterView.setTranslationY(y);
            checkCatch();
        });
        fallAnimator.start();
    }

    private void stopFallAnimation() {
        if (fallAnimator != null) {
            fallAnimator.cancel();
            fallAnimator = null;
        }
    }

    private boolean handleTrampolineDrag(View v, MotionEvent event) {
        if (gameOver) return true;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                gameArea.getLocationOnScreen(gameAreaLocation);
                float touchXInParent = event.getRawX() - gameAreaLocation[0];
                dragOffsetX = v.getX() - touchXInParent;
                return true;
            case MotionEvent.ACTION_MOVE:
                float moveXInParent = event.getRawX() - gameAreaLocation[0];
                float newX = moveXInParent + dragOffsetX;
                float maxX = gameArea.getWidth() - v.getWidth();
                newX = Math.max(0, Math.min(newX, maxX));
                v.setX(newX);
                checkCatch();
                return true;
            default:
                return false;
        }
    }

    private void checkCatch() {
        if (gameOver) return;

        float charLeft = characterView.getX();
        float charRight = charLeft + characterView.getWidth();
        float charBottom = characterView.getY() + characterView.getHeight();
        float charCenterX = (charLeft + charRight) / 2f;

        float trampLeft = trampolineView.getX();
        float trampRight = trampLeft + trampolineView.getWidth();
        float trampTop = trampolineView.getY();

        // Narrow catch window to make alignment more precise
        float catchHalfWidth = trampolineView.getWidth() * 0.2f; // 40% total width window
        float trampCenterX = (trampLeft + trampRight) / 2f;
        float catchLeft = trampCenterX - catchHalfWidth;
        float catchRight = trampCenterX + catchHalfWidth;

        float verticalCatchTop = trampTop;
        float verticalCatchBottom = trampTop + trampolineView.getHeight() * 0.25f; // top quarter only

        boolean horizontallyAligned = charCenterX >= catchLeft && charCenterX <= catchRight;
        boolean verticallyTouching = charBottom >= verticalCatchTop && charBottom <= verticalCatchBottom;

        if (horizontallyAligned && verticallyTouching) {
            gameOver = true;
            stopFallAnimation();
            if (countDownTimer != null) countDownTimer.cancel();
            minigamePassed();
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
        if (countDownTimer != null) countDownTimer.cancel();
        stopFallAnimation();
    }
}
