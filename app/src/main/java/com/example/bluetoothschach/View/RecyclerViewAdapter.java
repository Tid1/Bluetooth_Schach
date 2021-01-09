package com.example.bluetoothschach.View;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluetoothschach.Activities.HostGameActivity;
import com.example.bluetoothschach.Activities.LocalBoardActivity;
import com.example.bluetoothschach.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private List<SavedGameStateHolder> savedGames;
    private Activity activity;

    public RecyclerViewAdapter(List<SavedGameStateHolder> dataSet, Activity activity){
        this.savedGames = dataSet;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedGameStateHolder game = savedGames.get(position);
        TextView boardGameIdentifier = holder.recyclerTextView;
        boardGameIdentifier.setText(game.getGameIdentifier());
    }

    @Override
    public int getItemCount() {
        return savedGames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView recyclerTextView;
        public final Button recyclerViewButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerTextView = (TextView)itemView.findViewById(R.id.recyclerTextView);
            recyclerViewButton = (Button)itemView.findViewById(R.id.recyclerLoadButton);

            recyclerViewButton.setOnClickListener(v -> {
                if (!recyclerTextView.getText().toString().equals("") && recyclerTextView.getText() != null){
                   handleLoadSelection();
                }
            });
        }

        private void handleLoadSelection(){
            LayoutInflater inflater = activity.getLayoutInflater();
            View selectionView = inflater.inflate(R.layout.load_game_selection, null);

            Dialog dialog = new Dialog(activity);
            dialog.setContentView(selectionView);
            dialog.setCancelable(true);

            Button localGame = (Button)selectionView.findViewById(R.id.localFromLoad);
            Button hostGame = (Button)selectionView.findViewById(R.id.hostFromLoad);

            localGame.setOnClickListener(v -> {
                Intent startLocalGame = new Intent(activity, LocalBoardActivity.class);
                startLocalGame.putExtra("BoardIdentifier", recyclerTextView.getText());
                activity.startActivity(startLocalGame);
                activity.finish();
                dialog.cancel();
            });

            hostGame.setOnClickListener(v -> {
                Intent startBluetoothGame = new Intent(activity, HostGameActivity.class);
                startBluetoothGame.putExtra("BoardIdentifier", recyclerTextView.getText());
                activity.startActivity(startBluetoothGame);
                activity.finish();
                dialog.cancel();
            });

            dialog.show();
        }
    }
}
