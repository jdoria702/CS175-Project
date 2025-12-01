package edu.sjsu.android.a175project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPlay = findViewById(R.id.btn_play);
        Button btnShop = findViewById(R.id.btn_shop);
        Button btnQuit = findViewById(R.id.btn_quit);

        btnPlay.setOnClickListener(v -> {
            // TODO: Start your GameManagerActivity
            Intent intent = new Intent(MainActivity.this, GameManagerActivity.class);
            startActivity(intent);
        });

        btnShop.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ShopActivity.class);
            startActivity(intent);
        });

        btnQuit.setOnClickListener(v -> finishAffinity()); // Closes the app
    }
}
