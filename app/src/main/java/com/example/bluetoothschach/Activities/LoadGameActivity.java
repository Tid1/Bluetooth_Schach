package com.example.bluetoothschach.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluetoothschach.R;
import com.example.bluetoothschach.View.RecyclerViewAdapter;
import com.example.bluetoothschach.View.SavedGameStateHolder;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class LoadGameActivity extends AppCompatActivity {
    List<SavedGameStateHolder> savedGamesList = new LinkedList<>();
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_game);
        Button clearButton = (Button)findViewById(R.id.clearPreferences);
        Button returnButton = (Button)findViewById(R.id.returnFromLoadGame);
        clearButton.setOnClickListener(v -> handleClearButton());
        returnButton.setOnClickListener(v -> retunToMainMenu());
        handleRecyclerView();

    }

    private void handleRecyclerView(){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        SharedPreferences preferences = getDefaultSharedPreferences(getApplicationContext());
        Map<String, ?> allPreferences = preferences.getAll();
        Set<String> set = allPreferences.keySet();
        for (String s : set){
            savedGamesList.add(new SavedGameStateHolder(s));
        }

        if (adapter == null){
            adapter = new RecyclerViewAdapter(savedGamesList, this);
        } else {
            adapter.notifyDataSetChanged();
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void handleClearButton(){
        LayoutInflater inflater = getLayoutInflater();
        View clearMenu = inflater.inflate(R.layout.clear_menu, null);
        Dialog dialog = new Dialog(this);

        dialog.setContentView(clearMenu);
        dialog.setCancelable(true);

        Button clearYesButton = (Button)clearMenu.findViewById(R.id.clearYes);
        Button clearNoButton = (Button)clearMenu.findViewById(R.id.clearNo);

        clearYesButton.setOnClickListener(v -> {
            clearSharedPreferences();
            dialog.cancel();
        });

        clearNoButton.setOnClickListener(v ->{
            dialog.cancel();
        });

        dialog.show();
    }


    private void clearSharedPreferences(){
        SharedPreferences preferences = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        clearRecyclerView();
    }

    private void retunToMainMenu(){
        this.finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void clearRecyclerView() {
        int size = savedGamesList.size();
        savedGamesList.clear();
        adapter.notifyItemRangeRemoved(0, size);
    }
}
