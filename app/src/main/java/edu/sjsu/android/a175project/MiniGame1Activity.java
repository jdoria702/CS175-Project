package edu.sjsu.android.a175project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MiniGame1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame1);

        Button winButton = findViewById(R.id.btn_win);
        Button loseButton = findViewById(R.id.btn_lose);

        winButton.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        loseButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}