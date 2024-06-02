package com.example.candycrush;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int[][] gameBoard;
    private final int rows = 6;
    private final int columns = 7;
    private ImageButton[][] buttons;
    private int x = -1, y = -1, score = 0, moves = 20;
    private TextView scoreTextView;
    private Random random = new Random();
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView = findViewById(R.id.scoreTextView);
        GridLayout gameGrid = findViewById(R.id.gameGrid);

        gameBoard = new int[rows][columns];
        buttons = new ImageButton[rows][columns];

        initializeGameBoard(gameGrid);
        updateScore();
    }

    private void initializeGameBoard(GridLayout gameGrid) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                gameBoard[i][j] = random.nextInt(6);

                ImageButton button = new ImageButton(this);
                button.setBackgroundResource(getDrawableForCandy(gameBoard[i][j]));
                button.setPadding(0, 0, 0, 0);
                button.setScaleType(ImageView.ScaleType.CENTER_CROP);
                button.setTag(i + "," + j);
                button.setOnClickListener(this::onCandyClick);

                buttons[i][j] = button;
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.rowSpec = GridLayout.spec(i, 1, 1f);
                params.columnSpec = GridLayout.spec(j, 1, 1f);
                params.setMargins(4, 4, 4, 4);
                button.setLayoutParams(params);

                gameGrid.addView(button);
            }
        }
    }

    private int getDrawableForCandy(int candyType) {
        switch (candyType) {
            case 0: return R.drawable.yellow3;
            case 1: return R.drawable.orange3;
            case 2: return R.drawable.blue3;
            case 3: return R.drawable.red3;
            case 4: return R.drawable.bomb70x70;
            case 5: return R.drawable.purple3;
            default: return R.drawable.green3;
        }
    }

    private void onCandyClick(View view) {
        String tag = (String) view.getTag();
        String[] pos = tag.split(",");
        int i = Integer.parseInt(pos[0]);
        int j = Integer.parseInt(pos[1]);

        Log.d(TAG, "Candy clicked at position: " + i + "," + j);

        if (x == -1 && y == -1) {
            x = i;
            y = j;
        } else {
            if ((i == x + 1 && j == y) || (i == x - 1 && j == y) ||
                    (i == x && j == y - 1) || (i == x && j == y + 1)) {

                animateSwap(buttons[x][y], buttons[i][j], x, y, i, j);
                x = -1;
                y = -1;
            } else {
                x = i;
                y = j;
            }
        }
    }

    private void animateSwap(final ImageButton button1, final ImageButton button2, final int x1, final int y1, final int x2, final int y2) {
        final float startX = button1.getX();
        final float startY = button1.getY();
        final float endX = button2.getX();
        final float endY = button2.getY();

        Log.d(TAG, "Animating swap between (" + x1 + "," + y1 + ") and (" + x2 + "," + y2 + ")");

        button1.animate().x(endX).y(endY).setDuration(300).start();
        button2.animate().x(startX).y(startY).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                // Swap the backgrounds after the animation is done
                swapCandies(x1, y1, x2, y2);
                button1.setX(startX);
                button1.setY(startY);
                button2.setX(endX);
                button2.setY(endY);

                button1.setBackgroundResource(getDrawableForCandy(gameBoard[x1][y1]));
                button2.setBackgroundResource(getDrawableForCandy(gameBoard[x2][y2]));

                moves--;
                updateScore();
                checkForMatches();

                if (moves == 0) {
                    showGameOverDialog();
                }
            }
        }).start();
    }

    private void swapCandies(int i, int j, int x, int y) {
        Log.d(TAG, "Swapping candies at (" + i + "," + j + ") and (" + x + "," + y + ")");
        int temp = gameBoard[i][j];
        gameBoard[i][j] = gameBoard[x][y];
        gameBoard[x][y] = temp;

        buttons[i][j].setBackgroundResource(getDrawableForCandy(gameBoard[i][j]));
        buttons[x][y].setBackgroundResource(getDrawableForCandy(gameBoard[x][y]));
    }

    private void checkForMatches() {
        boolean hasMatch = false;

        // Check rows for matches
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns - 2; j++) {
                int count = 1;
                while (j + count < columns && gameBoard[i][j] == gameBoard[i][j + count]) {
                    count++;
                }
                if (count >= 3) {
                    hasMatch = true;
                    score += 15 * (count - 2); // Score more for longer matches
                    moves += count - 2; // Extra moves for longer matches
                    for (int k = j; k < columns - count; k++) {
                        gameBoard[i][k] = gameBoard[i][k + count];
                    }
                    for (int k = columns - count; k < columns; k++) {
                        gameBoard[i][k] = random.nextInt(6);
                    }
                    j += count - 1;
                }
            }
        }

        // Check columns for matches
        for (int j = 0; j < columns; j++) {
            for (int i = 0; i < rows - 2; i++) {
                int count = 1;
                while (i + count < rows && gameBoard[i][j] == gameBoard[i + count][j]) {
                    count++;
                }
                if (count >= 3) {
                    hasMatch = true;
                    score += 15 * (count - 2); // Score more for longer matches
                    moves += count - 2; // Extra moves for longer matches
                    for (int k = i; k < rows - count; k++) {
                        gameBoard[k][j] = gameBoard[k + count][j];
                    }
                    for (int k = rows - count; k < rows; k++) {
                        gameBoard[k][j] = random.nextInt(6);
                    }
                    i += count - 1;
                }
            }
        }

        // Refresh the board
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                buttons[i][j].setBackgroundResource(getDrawableForCandy(gameBoard[i][j]));
            }
        }

        if (hasMatch) {
            checkForMatches();
        }
    }

    private void updateScore() {
        scoreTextView.setText("Score: " + score + "  Moves: " + moves);
        Log.d(TAG, "Score updated: " + score + ", Moves left: " + moves);
        if (moves <= 0) {
            Intent intent = new Intent(this, GameOverActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
            finish();
        }

    }

    private void showGameOverDialog() {
        Log.d(TAG, "Game Over. Showing dialog.");
        // Show game over dialog and offer to restart the game
    }
}
