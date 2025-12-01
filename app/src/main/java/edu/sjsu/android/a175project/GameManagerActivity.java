package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import java.util.Random;
import edu.sjsu.android.a175project.ShopManager;
import edu.sjsu.android.a175project.HighScoreManager;

public class GameManagerActivity extends AppCompatActivity {

    private TextView countdownText, scoreText, characterText;
    private LinearLayout livesContainer;
    private TextView coinText;
    private View dimOverlay;

    private int score = 0;
    private int lives = 3;
    private int previousMinigame = -1;
    private int lastCoinGain = 0;

    private int minigameCount = 5;
    private static final int COIN_REWARD = 10;
    private static final int NEXT_GAME_DELAY = 1000;

    // Difficulty values used by ALL minigames
    public static int difficultyLevel = 1;
    public static long baseTimer = 5000;      // Base: 5 seconds
    public static long timerDecrease = 1000;   // -1.0 sec per level

    // Called by minigames to get their countdown timer
    public static long getTimerDuration() {
        long duration = baseTimer - ((difficultyLevel - 1) * timerDecrease);
        return Math.max(2000, duration); // never less than 2 sec
    }

    // Called by minigames when they succeed
    public static void increaseDifficulty() {
        difficultyLevel++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_manager);

        countdownText = findViewById(R.id.countdownText);
        scoreText = findViewById(R.id.scoreText);
        livesContainer = findViewById(R.id.livesContainer);
        characterText = findViewById(R.id.characterText);
        coinText = findViewById(R.id.coinText);
        dimOverlay = findViewById(R.id.dimOverlay);

        updateUI();
        startPregameCountdown();
    }

    // ---------------------------------------------------------
    // UI Update
    // ---------------------------------------------------------
    private void updateUI() {
        scoreText.setText("Score: " + score);
        updateLivesDisplay();
        if (characterText != null) {
            characterText.setText("Character: " + ShopManager.getSelectedCharacter(this));
        }
        if (coinText != null) {
            int coins = ShopManager.getCoins(this);
            String coinLabel = "Coins: " + coins;
            if (lastCoinGain > 0) {
                coinLabel += " (+" + lastCoinGain + ")";
            }
            coinText.setText(coinLabel);
        }
    }

    private void updateLivesDisplay() {
        if (livesContainer == null) return;
        livesContainer.removeAllViews();
        String characterName = ShopManager.getSelectedCharacter(this);

        for (int i = 0; i < lives; i++) {
            ImageView lifeIcon = new ImageView(this);
            lifeIcon.setImageResource(ShopManager.getCharacterDrawable(characterName));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    120,
                    120
            );
            if (i > 0) {
                params.setMarginStart(12);
            }
            lifeIcon.setLayoutParams(params);
            lifeIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            lifeIcon.setAdjustViewBounds(true);
            livesContainer.addView(lifeIcon);
        }
    }

    // ---------------------------------------------------------
    // 3...2...1 Countdown before first minigame
    // ---------------------------------------------------------
    private void startPregameCountdown() {
        countdownText.setVisibility(View.VISIBLE);
        dimOverlay.animate().alpha(0.6f).setDuration(300).start();

        new CountDownTimer(3000, 1000) {
            int num = 3;

            @Override
            public void onTick(long millisUntilFinished) {
                countdownText.setText(String.valueOf(num));
                animateCountdownNumber();
                num--;
            }

            @Override
            public void onFinish() {
                countdownText.setVisibility(View.GONE);
                dimOverlay.animate().alpha(0f).setDuration(300).start();
                startNextMinigame();
            }
        }.start();
    }

    private void animateCountdownNumber() {
        countdownText.setScaleX(1f);
        countdownText.setScaleY(1f);
        countdownText.animate()
                .scaleX(1.6f)
                .scaleY(1.6f)
                .setDuration(350)
                .start();
    }

    // ---------------------------------------------------------
    // Minigame Launcher
    // ---------------------------------------------------------
    private void startNextMinigame() {
        int chosen = pickRandomMinigame();

        Intent intent;
        switch (chosen) {
            case 0:
                intent = new Intent(this, MiniGame1Activity.class);
                break;
            case 1:
                intent = new Intent(this, MiniGame2Activity.class);
                break;
            case 2:
                intent = new Intent(this, MiniGame3Activity.class);
                break;
            case 3:
                intent = new Intent(this, MiniGame4Activity.class);
                break;
            case 4:
                intent = new Intent(this, MiniGame5Activity.class);
                break;
            default:
                intent = new Intent(this, MiniGame1Activity.class);
        }

        previousMinigame = chosen;
        startActivityForResult(intent, 101);
    }

    private int pickRandomMinigame() {
        Random random = new Random();
        int choice;

        if (minigameCount <= 1) return 0;

        do {
            choice = random.nextInt(minigameCount);
        } while (choice == previousMinigame);

        return choice;
    }

    // ---------------------------------------------------------
    // Result Back from Minigame
    // ---------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                // WIN
                score++;
                lastCoinGain = COIN_REWARD;
                ShopManager.addCoins(this, COIN_REWARD);
                updateUI();

                new Handler().postDelayed(this::startNextMinigame, NEXT_GAME_DELAY);

            } else {
                // LOSS
                lives--;
                lastCoinGain = 0;
                updateUI();

                if (lives <= 0) {
                    endGame();
                } else {
                    new Handler().postDelayed(this::startNextMinigame, NEXT_GAME_DELAY);
                }
            }
        }
    }

    // ---------------------------------------------------------
    // End Game
    // ---------------------------------------------------------
    private void endGame() {
        int previousHigh = HighScoreManager.getHighScore(this);
        boolean isHigh = score > previousHigh;
        if (isHigh) {
            HighScoreManager.setHighScore(this, score);
        }

        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("FINAL_SCORE", score);
        intent.putExtra("IS_HIGH_SCORE", isHigh);
        intent.putExtra("HIGH_SCORE", Math.max(previousHigh, score));
        startActivity(intent);

        difficultyLevel = 1;
        finish();
    }
}
