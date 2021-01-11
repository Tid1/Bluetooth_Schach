package com.example.bluetoothschach.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetoothschach.R;
import com.example.bluetoothschach.Utility.Utility;
import com.example.bluetoothschach.View.CustomView;
import com.google.gson.Gson;

import java.util.Random;

import Model.Exceptions.GameException;
import Model.Exceptions.StatusException;
import Model.Spiellogik.BoardImpl;
import Model.Spiellogik.Color;
import Model.Spiellogik.Figuren.iPiece;
import Model.Spiellogik.Status;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class LocalBoardActivity extends AppCompatActivity {
    private CustomView sCanvas;
    private ImageView imageView;
    private TextView currentTurn;
    private final String TURN_WHITE = "TURN: WHITE";
    private final String TURN_BLACK = "TURN: BLACK";
    private String gameIdentifier = "";
    private TextView errorText;
    private boolean firstTouch = true;
    private iPiece clickedPiece;
    private BoardImpl board;
    private final int COORDINATES = 2;
    private final int X_COORDINATE = 0;
    private final int Y_COORDINATE = 1;
    private float[] lastTouchDownXY = new float[COORDINATES];

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        errorText = (TextView)findViewById(R.id.errorTextBoard);
        currentTurn = (TextView)findViewById(R.id.changeTurns);
        Button saveGame = (Button)findViewById(R.id.returnFromBoard);
        saveGame.setOnClickListener(v -> returnToMain());
        currentTurn.setText(TURN_WHITE);
        handleCustomView();
        handleBoardCreation();
        handleImageView();
        handleSurrenderView();
    }

    @SuppressLint("ClickableViewAccessibility")
    View.OnTouchListener onTouchListener = (v, event) -> {
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
            if (errorText != null){
                errorText.setText("");
            }
            handleTouch(event.getX(), event.getY());
            //TODO entfernen
            System.out.println(event.getX());
        }
        return false;
    };

    @Override
    protected void onPause(){
        super.onPause();
        if (board != null && !board.getGameEnd()){
            //TODO GSON instance creator anschauen
            SharedPreferences preferences = getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            if (gameIdentifier.equals("")){
                this.gameIdentifier = createGameIdentifier();
            }
            editor.putString(gameIdentifier, Utility.objectToString(board));
            editor.commit();
        }
    }

    private void returnToMain(){
        this.finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @SuppressLint("ClickableViewAccessibility")
    View.OnTouchListener surrenderTouched = (v, event) -> {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN){
            LayoutInflater inflater = getLayoutInflater();
            View surrenderLayout = inflater.inflate(R.layout.surrender_screen, null);

            Dialog dialog = new Dialog(LocalBoardActivity.this);
            dialog.setContentView(surrenderLayout);
            dialog.setCancelable(true);

            handleSurrender(surrenderLayout, dialog);

        }
        return false;
    };

    private void handleBoardCreation(){
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey("BoardIdentifier")){
            SharedPreferences preferences = getDefaultSharedPreferences(getApplicationContext());
            String boardIdentifier = bundle.getString("BoardIdentifier");
            this.gameIdentifier = boardIdentifier;
            String boardJson = preferences.getString(boardIdentifier, "");
            this.board = Utility.stringToObjectS(boardJson);

            if (board.getStatus() == Status.TURN_WHITE){
                this.currentTurn.setText(TURN_WHITE);
            } else if (board.getStatus() == Status.TURN_BLACK){
                this.currentTurn.setText(TURN_BLACK);
            }
        } else {
            this.board = new BoardImpl();
            //TODO drawField
            try {
                board.pickColor("player1", Color.White);
                board.pickColor("player2", Color.Black);
                board.initializeField();
            } catch (StatusException e) {
                Log.e("PickColorFailed", e.getMessage());
            }
        }
        sCanvas.setBoardMap(board.getMap());
        sCanvas.invalidate();
    }


    private String createGameIdentifier(){
        int identifierLength = 5;
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("Board: ");
        for (int i = 0; i <= identifierLength; i++){
            sb.append(random.nextInt(10));
        }
        sb.append(" Local");
        return sb.toString();
    }

    private void handleImageView(){
        this.imageView = (ImageView)findViewById(R.id.chessboardImage);
        //imageView.setOnTouchListener(onTouchListener);
        //imageView.setOnClickListener(onClickListener);
    }

    public void handleTouch(float x, float y) {
        float fieldSize = sCanvas.getWidth() / 8f;

        int xCoordinate = (int) (x / fieldSize) + 1;
        int yCoordinate = 9 - ((int) (y / fieldSize) + 1);
        if (xCoordinate > 8) {
            xCoordinate = 8;
        }

        if (yCoordinate > 8) {
            yCoordinate = 8;
        }

        if (firstTouch){
            clickedPiece = board.onField(xCoordinate, yCoordinate);
            firstTouch = false;
            if (clickedPiece == null){
                firstTouch = true;
            }
        } else {
            firstTouch = true;
            if(clickedPiece != null){
                try {
                    board.move(clickedPiece, xCoordinate, yCoordinate);
                    handleTurns();
                } catch (StatusException | GameException e){
                    this.errorText.setText(e.getMessage());
                }
            }
        }
    }

    private void handleGameFinish(String typeOfFinish){
        LayoutInflater inflater = getLayoutInflater();
        View endGameView = inflater.inflate(R.layout.end_game_screen_local, null);

        Dialog dialog = new Dialog(LocalBoardActivity.this);
        dialog.setContentView(endGameView);
        dialog.setCancelable(false);


        TextView textView = (TextView)endGameView.findViewById(R.id.endGameText);
        Button replayGame = (Button)endGameView.findViewById(R.id.replayFromEnd);
        Button returnToMain = (Button)endGameView.findViewById(R.id.returnFromEnd);

        textView.setText(typeOfFinish);
        replayGame.setOnClickListener(v -> {
            this.finish();
            this.startActivity(new Intent(this, LocalBoardActivity.class));
            dialog.cancel();
        });

        returnToMain.setOnClickListener(v -> {
            this.finish();
            this.startActivity(new Intent(this, MainActivity.class));
            dialog.cancel();
        });

        dialog.show();
    }

    private void deleteGame(){
        SharedPreferences preferences = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        if (!this.gameIdentifier.equals("")){
            editor.remove(gameIdentifier);
        }
        editor.commit();
    }
    private void handleTurns(){
        if (board.getGameEnd()){
            deleteGame();
            if (board.getStatus() == Status.STALEMATE){
                handleGameFinish("GAME STALEMATED");
            } else {
                String winner = currentTurn.getText().toString().split(" ")[1];
                handleGameFinish(winner + " WON");
            }
        }
        if (currentTurn.getText().toString().equals(TURN_WHITE)){
            currentTurn.setText(TURN_BLACK);
        } else {
            currentTurn.setText(TURN_WHITE);
        }
        sCanvas.setBoardMap(board.getMap());
        sCanvas.invalidate();
    }


    private void handleCustomView(){
        sCanvas = (CustomView)findViewById(R.id.customView);
        sCanvas.setOnTouchListener(onTouchListener);
    }

    private void surrenderMessage(){
        if (currentTurn.getText().toString() == TURN_BLACK){
            handleGameFinish("WHITE WON");
        } else {
            handleGameFinish("BLACK WON");
        }
    }

    private void handleSurrender(View view, Dialog dialog){
        Button surrenderYesButton = view.findViewById(R.id.surrenderYes);
        surrenderYesButton.setOnClickListener((v1 -> {
            board.surrender();
            surrenderMessage();
            deleteGame();
            dialog.cancel();
        }));

        Button surrenderNoButton = view.findViewById(R.id.surrenderNo);
        surrenderNoButton.setOnClickListener(v -> {
            dialog.cancel();
        });
        dialog.show();
    }

    private void handleSurrenderView(){
        ImageView surrenderFlag = (ImageView)findViewById(R.id.surrenderFlag);
        surrenderFlag.setOnTouchListener(surrenderTouched);
    }
}
