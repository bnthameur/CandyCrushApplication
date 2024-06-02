package com.example.candycrush;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Récupérer le score depuis l'intent
        int score = getIntent().getIntExtra("SCORE", 0);

        // Afficher le score
        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText("Score: " + score);
    }

    // Méthode appelée lors du clic sur le bouton "Rejouer"
    public void replay(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Méthode appelée lors du clic sur le bouton "Quitter"
    public void quit(View view) {
        finishAffinity();
    }
}
