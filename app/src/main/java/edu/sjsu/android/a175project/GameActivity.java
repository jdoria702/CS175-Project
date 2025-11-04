package edu.sjsu.android.a175project;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameActivity extends AppCompatActivity {

    private int score = 0;
    private int timeLeft = 10; // seconds
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        TextView tvScore = findViewById(R.id.tv_score);
        TextView tvTimer = findViewById(R.id.tv_timer);
        Button btnClickMe = findViewById(R.id.btn_click_me);

        // Start a 10-second countdown
        timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = (int) (millisUntilFinished / 1000);
                tvTimer.setText("Time: " + timeLeft);
            }

            @Override
            public void onFinish() {
                tvTimer.setText("Time: 0");
                endGame();
            }
        }.start();

        // Increase score when button is clicked
        btnClickMe.setOnClickListener(v -> {
            score++;
            tvScore.setText("Score: " + score);
        });
    }

    private void endGame() {
        // Stop timer if needed
        if (timer != null) timer.cancel();

        // Return to Main Menu after 2 seconds
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}