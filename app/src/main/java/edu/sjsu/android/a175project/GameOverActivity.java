package edu.sjsu.android.a175project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        TextView scoreText = findViewById(R.id.tvFinalScore);
        TextView highScoreText = findViewById(R.id.tvHighScore);
        TextView bannerText = findViewById(R.id.tvBanner);
        Button playAgain = findViewById(R.id.btnPlayAgain);
        Button backHome = findViewById(R.id.btnBackHome);

        int score = getIntent().getIntExtra("FINAL_SCORE", 0);
        boolean isHigh = getIntent().getBooleanExtra("IS_HIGH_SCORE", false);
        int highScore = getIntent().getIntExtra("HIGH_SCORE", 0);

        scoreText.setText("Your Score: " + score);
        highScoreText.setText("High Score: " + highScore);
        bannerText.setText(isHigh ? "New High Score!" : "Game Over");

        playAgain.setOnClickListener(v -> {
            Intent intent = new Intent(GameOverActivity.this, GameManagerActivity.class);
            startActivity(intent);
            finish();
        });

        backHome.setOnClickListener(v -> {
            Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
