package com.lxd.gpstrack.Draw;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.lxd.gpstrack.Entiy.GPSPointF;
import com.lxd.gpstrack.Entiy.Track;
import com.lxd.gpstrack.Entiy.TrackLink;
import com.lxd.gpstrack.Entiy.TrackStationMap;
import com.lxd.gpstrack.MyDatabaseHelper;
import com.lxd.gpstrack.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class DrawMap extends View {

    public DrawMap(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    MyDatabaseHelper myDB = new MyDatabaseHelper(getContext());

    private Paint paint = new Paint();
    private GPSPointF gpsPoint = new GPSPointF(5,80);
    TrackStationMap trackStationMap = getMap();
    Map<String,List<String>> trackPathMap = null;
    Stack<String> stack = new Stack<>();

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);

        canvas.drawColor(Color.BLACK);

        drawTrack(canvas, paint,trackStationMap);
        //drawTrackLink(canvas,paint,trackStationMap) //画连接线;
        drawTrackName(canvas,paint,trackStationMap.getTracks());

        drawGps(canvas,getGpsPoint(),paint);

    }

    /**
     * 画车
     * @param canvas 画布
     * @param gpsPoint gps坐标
     * @param paint 画笔
     */
    private void drawGps(Canvas canvas, GPSPointF gpsPoint, Paint paint) {
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        //canvas.drawRect(this.gpsPoint.getX()-5, this.gpsPoint.getY()-5, this.gpsPoint.getX()+5, this.gpsPoint.getY()+5,paint);
        canvas.drawRect(gpsPoint.getX()-5, gpsPoint.getY()-5, gpsPoint.getX()+5, gpsPoint.getY()+5,paint);
    }

    private void drawLamp(Canvas canvas, Paint paint) {
        canvas.drawCircle(270, 90, 5, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(270, 90, 5, paint);
        paint.setColor(Color.WHITE);
        canvas.drawLine(265, 80, 265, 98, paint);
        String text = "D7";
        paint.setTextSize(16);
        int textWidth = getTextWidth(paint, text);
        paint.getFontMetrics();
        canvas.drawText("D7", 265-textWidth-5, 95, paint);
    }

    /**
     * 画股道
     * @param canvas 画布
     * @param paint 画笔
     * @param map 地图
     */
    private void drawTrack(Canvas canvas, Paint paint,TrackStationMap map) {
        paint.setColor(Color.GREEN);
        List<Track> tracks = map.getTracks();

        for (int i=0;i<tracks.size();i++){
            canvas.drawLine(tracks.get(i).getStart().x,tracks.get(i).getStart().y,tracks.get(i).getEnd().x,tracks.get(i).getEnd().y,paint);
        }
    }

    /**
     * 画连接线
     * @param canvas 画布
     * @param paint 画笔
     * @param map 股道图
     */
    private void drawTrackLink(Canvas canvas, Paint paint,TrackStationMap map) {
        paint.setColor(Color.GREEN);
        List<TrackLink> trackLinks = map.getTrackLinks();
        for (int i=0;i<trackLinks.size();i++){
            canvas.drawLine(trackLinks.get(i).getStart().x,trackLinks.get(i).getStart().y,trackLinks.get(i).getEnd().x,trackLinks.get(i).getEnd().y,paint);
        }
    }

    /**
     * 画站名
     */
    private void drawTrackName(Canvas canvas,Paint paint,List<Track> tracks){
        paint.setColor(Color.RED);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.CENTER);
        for (int i=0;i<tracks.size();i++){
            if(tracks.get(i).getName() != null){
                canvas.drawText(tracks.get(i).getName(),
                        (tracks.get(i).getEnd().x - tracks.get(i).getStart().x)/2+tracks.get(i).getStart().x,
                        tracks.get(i).getStart().y-10,paint);
            }
        }
    }

    /**
     * 计算drawText中字符串的宽度
     * @param paint 画笔
     * @param str 字符串
     * @return int 宽度
     */
    public static int getTextWidth(Paint paint, String str) {
        Rect bounds = new Rect();
        paint.getTextBounds(str,0,str.length(),bounds);

        return bounds.width();
    }

    /**
     * 获得Map TrackStationMap
     * @return TrackStationMap 股道图
     */
    public TrackStationMap getMap(){
        TrackStationMap trackStationMap = new TrackStationMap();
        SQLiteDatabase db = myDB.getReadableDatabase();
        //查询轨道
        Cursor cursor = db.rawQuery("select * from track",null);
        List<Track> tracks = new ArrayList<>();
        while (cursor.moveToNext()){
            Track ts = new Track();
            ts.setId(cursor.getInt(cursor.getColumnIndex("id")));
            ts.setStart(new PointF(cursor.getFloat(cursor.getColumnIndex("startX")),cursor.getFloat(cursor.getColumnIndex("startY"))));
            ts.setEnd(new PointF(cursor.getFloat(cursor.getColumnIndex("endX")),cursor.getFloat(cursor.getColumnIndex("endY"))));
            ts.setName(cursor.getString(cursor.getColumnIndex("name")));
            tracks.add(ts);
        }

        /*List<TrackLink> trackLinks = new ArrayList<>();
        //查询连接线
        cursor = db.rawQuery("select * from trackLink",null);
        while (cursor.moveToNext()){
            TrackLink trackLink = new TrackLink();
            trackLink.setId(cursor.getInt(cursor.getColumnIndex("id")));
            trackLink.setStart(new PointF(cursor.getFloat(cursor.getColumnIndex("startX")),cursor.getFloat(cursor.getColumnIndex("startY"))));
            trackLink.setEnd(new PointF(cursor.getFloat(cursor.getColumnIndex("endX")),cursor.getFloat(cursor.getColumnIndex("endY"))));
            trackLinks.add(trackLink);
        }*/
        trackStationMap.setTracks(tracks);
        //trackStationMap.setTrackLinks(trackLinks);
        db.close();
        cursor.close();
        return trackStationMap;
    }

    /**
     * 获得列车在地图上的gps坐标
     * @return 坐标
     */
    public GPSPointF getGpsPoint() {
        gpsPoint.setX(gpsPoint.getX() + 10);
        if (gpsPoint.getX() <= 348) {
            gpsPoint.setY(gpsPoint.getY());
        }else if (gpsPoint.getX()>348 && gpsPoint.getX()<448){
            gpsPoint.setY((float)(gpsPoint.getX()*1.6-476.8));
        }else if (448<gpsPoint.getX()){
            gpsPoint.setY(240);
        }

        Log.i("TAGgpsPoint",String.valueOf(gpsPoint.getX()));

        return gpsPoint;
    }

    public GPSPointF getGpsPointTwo(){
        return gpsPoint;
    }
}
