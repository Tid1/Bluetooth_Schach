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

import com.example.bluetoothschach.BluetoothSchach;
import com.example.bluetoothschach.Network.GameMessageReceiver;
import com.example.bluetoothschach.R;
import com.example.bluetoothschach.Utility.Utility;
import com.example.bluetoothschach.View.CustomView;

import net.sharksystem.asap.ASAPException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

import Model.Exceptions.GameException;
import Model.Exceptions.StatusException;
import Model.Spiellogik.BoardImpl;
import Model.Spiellogik.Color;
import Model.Spiellogik.Figuren.iPiece;
import Model.Spiellogik.Status;
import Netzwerk.BoardProtocolEngine;
import Netzwerk.iReceiver;
import Netzwerk.iSender;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class BluetoothGameActivity extends InitActivity implements iReceiver, iSender{
    private CustomView sCanvas;
    private ImageView imageView;
    private Color playerColor;
    private String uri;
    private GameMessageReceiver receiver;
    private ByteArrayInputStream bais;
    private ByteArrayOutputStream baos;
    private TextView currentTurn;
    private TextView errorText;
    private boolean firstTouch = true;
    private final String TURN_WHITE = "TURN: WHITE";
    private final String TURN_BLACK = "TURN: BLACK";
    private iPiece clickedPiece;
    private BoardImpl board;
    private BoardProtocolEngine engine;
    private String gameIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        errorText = findViewById(R.id.errorTextBoard);
        currentTurn = (TextView)findViewById(R.id.changeTurns);
        currentTurn.setText(TURN_WHITE);
        //TODO Laden des Feldes miteinbinden
        System.out.println("Bluetooth Game Activity started");
        handleIntentExtras();
        handleCustomView();
        handleBoardCreation();
        handleColorPick();
        handleSurrenderView();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        BluetoothSchach.deleteMessageReceivedListener(receiver);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (board != null && !board.getGameEnd()){
            SharedPreferences preferences = getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            if (gameIdentifier.equals("")){
                this.gameIdentifier = this.uri;
            }
            editor.putString(gameIdentifier, Utility.objectToString(board));
            editor.commit();
        }
    }
    private void handleBoardCreation(){
        this.board = new BoardImpl();
        this.baos = new ByteArrayOutputStream();
        this.bais = new ByteArrayInputStream(baos.toByteArray());
        this.engine = new BoardProtocolEngine(board, baos, bais);
        //TODO
    }

    private void handleTouch(float x, float y) {
        float fieldSize = sCanvas.getWidth() / 8f;
        System.out.println("Handle touch aufgerufen");

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
            if (clickedPiece != null && clickedPiece.getColor() != playerColor){
                errorText.setText("Can't click enemy Piece");
                return;
            }

            firstTouch = false;
            if (clickedPiece == null){
                firstTouch = true;
            }
        } else {
            firstTouch = true;
            if(clickedPiece != null){
                try {
                    engine.move(clickedPiece, xCoordinate, yCoordinate);
                    System.out.println("engine move aufgerufen");
                    sendTurn(engine.getBaos());
                    handleTurns();
                } catch (StatusException | GameException e){
                    errorText.setText(e.getMessage());
                }
            }
        }
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

    private void handleGameFinish(String typeOfFinish){
        LayoutInflater inflater = getLayoutInflater();
        View endGameView = inflater.inflate(R.layout.end_game_screen_online, null);

        Dialog dialog = new Dialog(BluetoothGameActivity.this);
        dialog.setContentView(endGameView);
        dialog.setCancelable(false);


        TextView textView = (TextView)endGameView.findViewById(R.id.endGameTextOnline);
        Button returnToMain = (Button)endGameView.findViewById(R.id.onlineReturnToMain);

        textView.setText(typeOfFinish);

        returnToMain.setOnClickListener(v -> {
            deleteGame();
            this.finish();
            this.startActivity(new Intent(this, MainActivity.class));
            dialog.cancel();
        });

        dialog.show();
    }

    private void sendTurn(ByteArrayOutputStream baos){
        byte[] message = baos.toByteArray();
        System.out.println("Send message with length: " + message.length);
        try {
            sendASAPMessage(BluetoothSchach.ASAP_APPNAME, uri, message, true);
        } catch (ASAPException e) {
            Log.e("GameActivityASAPError", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void receiveTurn(byte[] receivedMessage){
        try {
            System.out.println("Received Message length: " + receivedMessage.length);
            ByteArrayInputStream bais = new ByteArrayInputStream(receivedMessage);
            DataInputStream dais = new DataInputStream(bais);
            if (dais.readInt() == Integer.MAX_VALUE){
                board.surrender();
                receiveSurrenderMessage();
            } else {
                engine.updateBais(receivedMessage);
                handleTurns();
            }
        } catch (GameException e) {
            Log.e("GameExceptionReceived", e.getMessage());
        } catch (StatusException e) {
            Log.e("StatusExceptionReceived", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleCustomView(){
        sCanvas = (CustomView)findViewById(R.id.customView);
        sCanvas.setOnTouchListener((v, event) -> {
            if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
                if (errorText != null){
                    errorText.setText("");
                }
                handleTouch(event.getX(), event.getY());
                //TODO entfernen
                System.out.println(event.getX());
            }
            return false;
        });
    }

    private void handleSurrender(View view, Dialog dialog){
        Button surrenderYesButton = view.findViewById(R.id.surrenderYes);
        surrenderYesButton.setOnClickListener((v1 -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream daos = new DataOutputStream(baos);

            try {
                daos.writeInt(Integer.MAX_VALUE);
                sendASAPMessage(BluetoothSchach.ASAP_APPNAME, this.uri, baos.toByteArray(), true);
                board.surrender();
                sendSurrenderMessage();
            } catch (IOException | ASAPException e) {
                Log.e("SurrenderFailed", Objects.requireNonNull(e.getMessage()));
            }
        }));

        Button surrenderNoButton = view.findViewById(R.id.surrenderNo);
        surrenderNoButton.setOnClickListener(v -> {
            dialog.cancel();
        });
        dialog.show();
    }

    private void sendSurrenderMessage(){
        if (playerColor == Color.Black){
            handleGameFinish("WHITE WON");
        } else {
            handleGameFinish("BLACK WON");
        }
    }

    private void receiveSurrenderMessage(){
        if (playerColor == Color.Black){
            handleGameFinish("BLACK WON");
        } else {
            handleGameFinish("WHITE WON");
        }
    }

    private void handleIntentExtras(){
        Bundle bundle = getIntent().getExtras();
        this.playerColor = (Color)bundle.getSerializable("Color");
        this.uri = bundle.getString("URI");

        if (receiver == null){
            receiver = new GameMessageReceiver(uri);
        }
    }

    private void handleColorPick(){
        try {
            board.pickColor("player2", Color.Black);
            board.pickColor("player1", Color.White);
            board.initializeField();
            sCanvas.setBoardMap(board.getMap());
            sCanvas.invalidate();
        } catch (StatusException e) {
            Log.e("PickColorFailure", e.getMessage());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleSurrenderView(){
        ImageView surrenderFlag = (ImageView)findViewById(R.id.surrenderFlag);
        surrenderFlag.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN){
                LayoutInflater inflater = getLayoutInflater();
                View surrenderLayout = inflater.inflate(R.layout.surrender_screen, null);

                Dialog dialog = new Dialog(BluetoothGameActivity.this);
                dialog.setContentView(surrenderLayout);
                dialog.setCancelable(true);

                handleSurrender(surrenderLayout, dialog);
            }
            return false;
        });
    }
}
