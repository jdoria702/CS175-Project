package edu.sjsu.android.a175project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private TextView coinText;
    private LinearLayout charactersContainer;

    private final List<CharacterItem> characters = Arrays.asList(
            new CharacterItem("Blue", 0, "Reliable starter hero"),
            new CharacterItem("Johnny", 150, "Fast reactions and swagger"),
            new CharacterItem("Leafy", 300, "Chill and cool under pressure"),
            new CharacterItem("Shadow", 450, "Silent but stylish")
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        coinText = findViewById(R.id.coinText);
        charactersContainer = findViewById(R.id.charactersContainer);
        Button backBtn = findViewById(R.id.btnBack);

        backBtn.setOnClickListener(v -> finish());

        populateCharacters();
        refreshCoins();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCoins();
        refreshCharacterButtons();
    }

    private void refreshCoins() {
        coinText.setText("Coins: " + ShopManager.getCoins(this));
    }

    private void populateCharacters() {
        LayoutInflater inflater = LayoutInflater.from(this);
        charactersContainer.removeAllViews();

        for (CharacterItem item : characters) {
            View card = inflater.inflate(R.layout.item_character_card, charactersContainer, false);
            TextView name = card.findViewById(R.id.tvCharacterName);
            TextView price = card.findViewById(R.id.tvCharacterPrice);
            Button action = card.findViewById(R.id.btnCharacterAction);
            ImageView portrait = card.findViewById(R.id.ivCharacterImage);

            name.setText(item.name);
            price.setText(item.price == 0 ? "Free" : item.price + " coins");
            portrait.setImageResource(ShopManager.getCharacterDrawable(item.name));

            action.setOnClickListener(v -> handleAction(item, action));
            charactersContainer.addView(card);
        }
        refreshCharacterButtons();
    }

    private void refreshCharacterButtons() {
        String selected = ShopManager.getSelectedCharacter(this);
        int count = charactersContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View card = charactersContainer.getChildAt(i);
            Button action = card.findViewById(R.id.btnCharacterAction);
            TextView name = card.findViewById(R.id.tvCharacterName);
            String characterName = name.getText().toString();

            boolean owned = ShopManager.isOwned(this, characterName);
            if (!owned) {
                action.setText("Buy");
                action.setEnabled(true);
            } else if (characterName.equals(selected)) {
                action.setText("Selected");
                action.setEnabled(false);
            } else {
                action.setText("Select");
                action.setEnabled(true);
            }
        }
    }

    private void handleAction(CharacterItem item, Button actionButton) {
        if (!ShopManager.isOwned(this, item.name)) {
            boolean success = ShopManager.purchaseCharacter(this, item.name, item.price);
            if (!success) {
                Toast.makeText(this, "Not enough coins", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(this, item.name + " purchased!", Toast.LENGTH_SHORT).show();
            }
        }
        ShopManager.setSelectedCharacter(this, item.name);
        refreshCoins();
        refreshCharacterButtons();
    }

    private static class CharacterItem {
        final String name;
        final int price;
        final String description;

        CharacterItem(String name, int price, String description) {
            this.name = name;
            this.price = price;
            this.description = description;
        }
    }
}
