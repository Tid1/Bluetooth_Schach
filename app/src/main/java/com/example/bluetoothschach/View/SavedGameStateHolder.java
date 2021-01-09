package com.example.bluetoothschach.View;

public class SavedGameStateHolder {
    private String gameIdentifier;

    public SavedGameStateHolder(String gameIdentifier){
        this.gameIdentifier = gameIdentifier;
    }

    public String getGameIdentifier(){
        return gameIdentifier;
    }
}
