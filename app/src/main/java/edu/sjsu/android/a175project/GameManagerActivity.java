package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import java.util.Random;

public class GameManagerActivity extends AppCompatActivity {

    private TextView countdownText, scoreText, livesText;
    private View dimOverlay;

    private int score = 0;
    private int lives = 3;
    private int previousMinigame = -1;

    private int minigameCount = 5;
    private int difficultyDelay = 1500;

    // Difficulty values used by ALL minigames
    public static int difficultyLevel = 1;
    public static long baseTimer = 5000;      // Base: 5 seconds
    public static long timerDecrease = 500;   // -0.5 sec per level

    // Called by minigames to get their countdown timer
    public static long getTimerDuration() {
        long duration = baseTimer - ((difficultyLevel - 1) * timerDecrease);
        return Math.max(1500, duration); // never less than 1.5 sec
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
        livesText = findViewById(R.id.livesText);
        dimOverlay = findViewById(R.id.dimOverlay);

        updateUI();
        startPregameCountdown();
    }

    // ---------------------------------------------------------
    // UI Update
    // ---------------------------------------------------------
    private void updateUI() {
        scoreText.setText("Score: " + score);
        livesText.setText("Lives: " + lives);
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
                updateUI();

                // Speed up pacing between games
                difficultyDelay = Math.max(500, difficultyDelay - 100);

                new Handler().postDelayed(this::startNextMinigame, difficultyDelay);

            } else {
                // LOSS
                lives--;
                updateUI();

                if (lives <= 0) {
                    endGame();
                } else {
                    new Handler().postDelayed(this::startNextMinigame, difficultyDelay);
                }
            }
        }
    }

    // ---------------------------------------------------------
    // End Game
    // ---------------------------------------------------------
    private void endGame() {
        Intent intent = new Intent();
        intent.putExtra("FINAL_SCORE", score);
        setResult(RESULT_OK, intent);
        difficultyLevel = 1;
        difficultyDelay = 1500;
        finish();
    }
}
