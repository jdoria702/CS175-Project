package edu.sjsu.android.a175project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import java.util.Random;

public class GameManagerActivity extends AppCompatActivity {

    private int score = 0;
    private int lives = 3;

    // This launcher listens for mini-game results
    private final ActivityResultLauncher<Intent> miniGameLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    score += 10; // gained points
                } else {
                    lives--; // lost a life
                }

                if (lives > 0) {
                    launchRandomMiniGame(); // play next mini-game
                } else {
                    endGame();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_manager);

        TextView tvStatus = findViewById(R.id.tv_status);
        tvStatus.setText("Starting game...");

        // Start first mini-game
        launchRandomMiniGame();
    }

    private void launchRandomMiniGame() {
        // for now, only one dummy game
        Intent intent = new Intent(this, MiniGame1Activity.class);
        miniGameLauncher.launch(intent);
    }

    private void endGame() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("FINAL_SCORE", score);
        startActivity(intent);
        finish();
    }
}
