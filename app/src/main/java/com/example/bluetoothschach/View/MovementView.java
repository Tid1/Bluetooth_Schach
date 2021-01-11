package com.example.bluetoothschach.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

import Model.Spiellogik.Figuren.Position;

public class MovementView extends View {
    private List<Position> currentPieceMoveset;
    private Paint paint = new Paint();

    public MovementView(Context context) {
        super(context);
        init(null);
    }

    public MovementView(Context context, AttributeSet set){
        super(context, set);
        init(set);
    }


    private void init(@Nullable AttributeSet set){

    }


    private void drawDot(int x, int y, Canvas canvas){
        int sizeWidth =  ((getWidth() / 8)*x)-(getWidth() / 16);
        int sizeHeight = ((getHeight() / 8)*(9-y))-(getHeight() / 16);
        paint.setColor(Color.RED);
        canvas.drawCircle(sizeWidth, sizeHeight, 20, paint);
    }

    @Override
    protected void onDraw(Canvas canvas){
        if (currentPieceMoveset != null && currentPieceMoveset.size() != 0){
            for (Position position : currentPieceMoveset){
                drawDot(position.getX(), position.getY(), canvas);
            }
            currentPieceMoveset.clear();
        }
    }

    public void setCurrentPieceMoveset(List<Position> positions){
        this.currentPieceMoveset = positions;
    }
}
