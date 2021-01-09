package com.example.bluetoothschach.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_game);
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

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(savedGamesList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
