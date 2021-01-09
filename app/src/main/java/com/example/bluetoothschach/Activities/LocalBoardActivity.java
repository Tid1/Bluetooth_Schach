package com.example.bluetoothschach.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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
import com.example.bluetoothschach.View.CustomView;

import Model.Exceptions.GameException;
import Model.Exceptions.StatusException;
import Model.Spiellogik.BoardImpl;
import Model.Spiellogik.Color;
import Model.Spiellogik.Figuren.iPiece;
import Model.Spiellogik.Status;

public class LocalBoardActivity extends AppCompatActivity {
    private CustomView sCanvas;
    private ImageView imageView;
    private TextView currentTurn;
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
        errorText = findViewById(R.id.errorTextBoard);
        currentTurn = (TextView)findViewById(R.id.turn_view_test);
        currentTurn.setText("TURN: WHITE");
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

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            float x = lastTouchDownXY[X_COORDINATE];
            float y = lastTouchDownXY[Y_COORDINATE];
            //TODO entfernen
            System.out.println("X: " + x + " Y: " + y);
        }
    };

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
        this.board = new BoardImpl();
        //TODO drawField
        try {
            board.pickColor("player1", Color.White);
            board.pickColor("player2", Color.Black);
            board.initializeField();
        } catch (StatusException e){
            Log.e("PickColorFailed", e.getMessage());
        }
        sCanvas.setBoardMap(board.getMap());
        sCanvas.invalidate();
    }


    private void handleImageView(){
        this.imageView = (ImageView)findViewById(R.id.imageView4);
        //imageView.setOnTouchListener(onTouchListener);
        //imageView.setOnClickListener(onClickListener);
    }

    private void handleTouch(float x, float y) {
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
                    if (board.getGameEnd()){
                        if (board.getStatus() == Status.STALEMATE){
                            handleGameFinish("GAME STALEMATED");
                        } else {
                            String winner = currentTurn.getText().toString().split(" ")[1];
                            handleGameFinish(winner + " WON");
                        }
                    }
                    if (currentTurn.getText().toString().equals("TURN: WHITE")){
                        currentTurn.setText("TURN: BLACK");
                    } else {
                        currentTurn.setText("TURN: WHITE");
                    }
                    sCanvas.setBoardMap(board.getMap());
                    sCanvas.invalidate();
                } catch (StatusException | GameException e){
                    errorText.setText(e.getMessage());
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


    private void handleCustomView(){
        sCanvas = (CustomView)findViewById(R.id.customView);
        sCanvas.setOnTouchListener(onTouchListener);
    }

    private void handleSurrender(View view, Dialog dialog){
        Button surrenderYesButton = view.findViewById(R.id.surrenderYes);
        surrenderYesButton.setOnClickListener((v1 -> {
            startActivity(new Intent(LocalBoardActivity.this, MainActivity.class));
            LocalBoardActivity.this.finish();
            dialog.cancel();
        }));

        Button surrenderNoButton = view.findViewById(R.id.surrenderNo);
        surrenderNoButton.setOnClickListener(v -> {
            dialog.cancel();
        });
        dialog.show();
    }

    private void handleSurrenderView(){
        ImageView surrenderFlag = (ImageView)findViewById(R.id.imageView2_test);
        surrenderFlag.setOnTouchListener(surrenderTouched);
    }
}
