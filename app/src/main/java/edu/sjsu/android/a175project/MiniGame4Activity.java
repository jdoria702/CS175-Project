package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiniGame4Activity extends AppCompatActivity {


    private CountDownTimer countDownTimer;
    private boolean gameOver = false;
    private View timeBar;
    private static final long MAX_TIME_MS = 4_000L;
    private static final long TICK_MS = 50L;
    
    private FrameLayout sceneRoot;
    private FrameLayout paintContainer;
    private TextView centerHint;

    private final List<View> spots = new ArrayList<>();
    private int spotsRemaining = 0;
    private final Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame4);

        timeBar = findViewById(R.id.timeBar);
        TextView characterLabel = findViewById(R.id.characterLabel);
        if (characterLabel != null) {
            characterLabel.setText(R.string.character_blue);
        }

        sceneRoot = findViewById(R.id.sceneRoot);
        View wallView = findViewById(R.id.wallView);
        paintContainer = findViewById(R.id.paintContainer);
        centerHint = findViewById(R.id.centerHint);

        timeBar.setPivotX(0);
        if (wallView != null) {
            wallView.setBackground(makeWallBackground());
        }
        startCountdown();

        sceneRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sceneRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                spawnSpots();
            }
        });

        if (centerHint != null) {
            centerHint.setText(R.string.erase_all_paint_hint);
            centerHint.setAlpha(1f);
            centerHint.animate()
                    .alpha(0f)
                    .setStartDelay(900)
                    .setDuration(300)
                    .withEndAction(() -> centerHint.setVisibility(View.GONE))
                    .start();
        }
    }

    private void spawnSpots() {
        if (paintContainer == null) return;
        paintContainer.removeAllViews();
        spots.clear();

        int width = paintContainer.getWidth();
        int height = paintContainer.getHeight();
        if (width <= 0 || height <= 0) {
            paintContainer.post(this::spawnSpots);
            return;
        }

        int count = 8;
        int padding = dp(24);

        addWallPattern(width, height, padding);

        for (int i = 0; i < count; i++) {
            int size = dp(40 + rnd.nextInt(26));
            int left = padding + rnd.nextInt(Math.max(1, width - size - 2 * padding));
            int top = padding + rnd.nextInt(Math.max(1, height - size - 2 * padding));

            View spot = new View(this);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size);
            lp.leftMargin = left;
            lp.topMargin = top;
            spot.setLayoutParams(lp);
            spot.setBackground(makeBlob(size, randomColor()));
            spot.setRotation(rnd.nextInt(61) - 30);
            spot.setAlpha(0f);
            spot.setScaleX(0.6f);
            spot.setScaleY(0.6f);
            paintContainer.addView(spot);
            spots.add(spot);

            spot.animate()
                    .alpha(1f).scaleX(1f).scaleY(1f)
                    .setDuration(180)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();

            spot.setOnClickListener(v -> eraseSpot(spot));
        }

        spotsRemaining = spots.size();
    }

    private void addWallPattern(int width, int height, int padding) {
        int lineColor = 0x22000000;
        int step = dp(48);
        int thickness = dp(2);
        for (int y = padding; y <= height - padding; y += step) {
            View line = new View(this);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width - 2 * padding, thickness);
            lp.leftMargin = padding;
            lp.topMargin = y;
            line.setLayoutParams(lp);
            line.setBackgroundColor(lineColor);
            paintContainer.addView(line);
        }
    }

    private void eraseSpot(View spot) {
        if (gameOver) return;
        spot.setClickable(false);
        spot.animate()
                .rotationBy(25f)
                .scaleX(0.1f)
                .scaleY(0.1f)
                .alpha(0f)
                .setDuration(140)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> {
                    paintContainer.removeView(spot);
                    spotsRemaining--;
                    if (spotsRemaining <= 0) onAllCleared();
                })
                .start();
    }

    private void onAllCleared() {
        if (gameOver) return;
        gameOver = true;
        if (countDownTimer != null) countDownTimer.cancel();
        minigamePassed();
    }

    private GradientDrawable makeBlob(int sizePx, int color) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setColor(color);
        d.setStroke(dp(2), 0x33000000);
        float r1 = sizePx * (0.35f + rnd.nextFloat() * 0.35f);
        float r2 = sizePx * (0.25f + rnd.nextFloat() * 0.45f);
        float r3 = sizePx * (0.30f + rnd.nextFloat() * 0.40f);
        float r4 = sizePx * (0.20f + rnd.nextFloat() * 0.50f);
        d.setCornerRadii(new float[]{r1, r1, r2, r2, r3, r3, r4, r4});
        return d;
    }

    private GradientDrawable makeWallBackground() {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFFE3E3E3, 0xFFC7C7C7});
        g.setShape(GradientDrawable.RECTANGLE);
        g.setCornerRadius(dp(8));
        g.setStroke(dp(1), 0x22000000);
        return g;
    }

    private int randomColor() {
        int[] colors = new int[] { 0xFFE57373, 0xFF64B5F6, 0xFF81C784, 0xFFFFB74D, 0xFFBA68C8, 0xFFFF8A65 };
        return colors[rnd.nextInt(colors.length)];
    }

    private void startCountdown() {
        if (timeBar != null) timeBar.setScaleX(1f);

        countDownTimer = new CountDownTimer(MAX_TIME_MS, TICK_MS) {
            @Override
            public void onTick(long millisUntilFinished) {
                float fraction = millisUntilFinished / (float) MAX_TIME_MS;
                if (timeBar != null) timeBar.setScaleX(fraction);
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

    private int dp(int v) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }
}
