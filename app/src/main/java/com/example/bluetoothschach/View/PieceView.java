package com.example.bluetoothschach.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.bluetoothschach.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Model.Spiellogik.BoardImpl;
import Model.Spiellogik.Color;
import Model.Spiellogik.Figuren.iPiece;
import Model.Spiellogik.iPlayer;

public class PieceView extends View {
    private Canvas canvas;
    private Paint paint = new Paint();
    private Paint testPaint = new Paint(Paint.UNDERLINE_TEXT_FLAG);
    private Bitmap bitmap;
    private Color playerColor;
    private Map<iPlayer, List<iPiece>> boardMap;
    private final float width = getWidth();
    List<Bitmap> bitmaps = new LinkedList<>();
    private int[] lastTouchDownXY = new int[2];

    public PieceView(Context context) {
        super(context);
        init(null);
    }

    public PieceView(Context context, AttributeSet set){
        super(context, set);
        init(set);
    }


    private void init(@Nullable AttributeSet set){

    }

    private void drawFigureOnBitmap(int ressource){
        Bitmap mutableBitmap = BitmapFactory.decodeResource(getResources(), ressource);
        bitmap = Bitmap.createScaledBitmap(mutableBitmap, (getWidth()/ 8)-10, (getHeight()/ 8)-10, false);
    }

    public void setPlayerColor(Color color){
        this.playerColor = color;
    }


    @Override
    protected void onDraw(Canvas canvas){
        if (boardMap != null){
            for (Map.Entry<iPlayer, List<iPiece>> entry : boardMap.entrySet()){
                List<iPiece> pieces = entry.getValue();
                for (iPiece currentPiece : pieces){
                    switch (currentPiece.getColor()){
                        case Black:
                            switch (currentPiece.getType()){
                                case DAME:
                                    drawFigureOnBitmap(R.drawable.quuen);
                                    break;
                                case TURM:
                                    drawFigureOnBitmap(R.drawable.rook);
                                    break;
                                case BAUER:
                                    drawFigureOnBitmap(R.drawable.pawn);
                                    break;
                                case KOENIG:
                                    drawFigureOnBitmap(R.drawable.king);
                                    break;
                                case LAEUFER:
                                    drawFigureOnBitmap(R.drawable.bishop);
                                    break;
                                case SPRINGER:
                                    drawFigureOnBitmap(R.drawable.knight);
                                    break;
                            }
                            break;

                        case White:
                            switch (currentPiece.getType()){
                                case DAME:
                                    drawFigureOnBitmap(R.drawable.queen_white);
                                    break;
                                case TURM:
                                    drawFigureOnBitmap(R.drawable.rook_white);
                                    break;
                                case BAUER:
                                    drawFigureOnBitmap(R.drawable.pawn_white);
                                    break;
                                case KOENIG:
                                    drawFigureOnBitmap(R.drawable.king_white);
                                    break;
                                case LAEUFER:
                                    drawFigureOnBitmap(R.drawable.bishop_white);
                                    break;
                                case SPRINGER:
                                    drawFigureOnBitmap(R.drawable.knight_white);
                                    break;
                            }
                            break;
                    }
                    if (playerColor != null && playerColor == Color.Black){
                        canvas.drawBitmap(bitmap, ((getWidth()/8)*(9-(currentPiece.getPosition().getX()+1))+5),
                                (getHeight()/8)*((currentPiece.getPosition().getY()-1))+5, null);
                    } else {
                        canvas.drawBitmap(bitmap, ((getWidth()/8)*(currentPiece.getPosition().getX() - 1)+5),
                                (getHeight()/8)*(9-(currentPiece.getPosition().getY()+1))+5, null);
                    }

                }
            }
        }
    }

    private void drawBoardForWhite(iPiece currentPiece){

    }

    private void drawBoardForBlack(iPiece currentPiece){

    }

    public void invalidateCanvas(){
        invalidate();
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight){

    }

    public void setBoardMap(Map<iPlayer, List<iPiece>> map){
        this.boardMap = new HashMap<>(map);
    }

    public Bitmap getBitmap(){
        return bitmap;
    }
}

