package com.example.hanjiyeon.maptest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ArrayList getSelectMarkerData(double lat,double lng){
        SQLiteDatabase db=getReadableDatabase();
        ArrayList list=new ArrayList();
        Cursor cursor=db.rawQuery("select * from SCHEDULE where lat="+lat+" and lng="+lng+"",null);
        while(cursor.moveToNext()){
            ScheduleData sd=new ScheduleData();
            sd.setTitle(cursor.getString(0));
            sd.setPlaceName(cursor.getString(1));
            sd.setLat(cursor.getDouble(2));
            sd.setLng(cursor.getDouble(3));
            sd.setTime(cursor.getInt(4));
            list.add(sd);
        }
        cursor=db.rawQuery("select lat,lng,content,plansuc from plan where lat="+lat+" and lng="+lng+"",null);
        while(cursor.moveToNext()){
            PlanData pd=new PlanData();
            pd.setLat(cursor.getDouble(0));
            pd.setLng(cursor.getDouble(1));
            pd.setContent(cursor.getString(2));
            pd.setPlansuc(cursor.getInt(3));
            list.add(pd);
        }
        return list;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블을 생성한다.
        // create table 테이블명 (컬럼명 타입 옵션);
        db.execSQL("CREATE TABLE IF NOT EXISTS SCHEDULE(title TEXT, placeName, lat REAL, lng REAL,time INTEGER,CONSTRAINT SCH_PK PRIMARY KEY(lat,lng));");

        db.execSQL("CREATE TABLE IF NOT EXISTS PLAN(_id INTEGER PRIMARY KEY AUTOINCREMENT ,lat REAL, lng REAL, content TEXT, plansuc INTEGER DEFAULT 0,FOREIGN KEY(lat,lng) REFERENCES SCHEDULE(lat,lng) ON DELETE CASCADE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }
    public void finishMarker(double lat,double lng){
        SQLiteDatabase db=getReadableDatabase();
        db.execSQL("delete from SCHEDULE where lat="+lat+" and lng="+lng+";");
    }
    public void init() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS SCHEDULE(title TEXT, placeName, lat REAL, lng REAL,time INTEGER,CONSTRAINT SCH_PK PRIMARY KEY(lat,lng));");

        db.execSQL("CREATE TABLE IF NOT EXISTS PLAN(_id INTEGER PRIMARY KEY AUTOINCREMENT ,lat REAL, lng REAL, content TEXT, plansuc INTEGER DEFAULT 0);");
        db.close();
    }
    public void updateChecked(int pid,int checked){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("update PLAN set plansuc="+checked+" where _id="+pid+";");
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public String PrintData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from SCHEDULE", null);
        while(cursor.moveToNext()) {
            str += "title: "+cursor.getString(0)
                    + "  placeName : "
                    + cursor.getString(1)
                    + ", lat = "
                    + cursor.getDouble(2)
                    + ", lng = "
                    + cursor.getDouble(3)
                    + ", time ="
                    + cursor.getInt(4)

                    + "\n";
        }

        return str;
    }

    public String getPlan(){
        SQLiteDatabase db=getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from PLAN", null);
        while(cursor.moveToNext()){
            str+= "id: "+ cursor.getInt(0)
                    +"lat: "+cursor.getDouble(1)
                    +" lng: "+cursor.getDouble(2)
                    +" content: "+cursor.getString(3)
                    +" plansuc: "+cursor.getInt(4)
                    +"\n";
        }
        return str;
    }

    public ArrayList getFirstMarker(){
        SQLiteDatabase db=getReadableDatabase();

        ArrayList list=new ArrayList();
        Cursor cursor=db.rawQuery("select * from SCHEDULE order by time limit 1",null);
        double lat=0.0;
        double lng=0.0;
        while(cursor.moveToNext()){
            ScheduleData sd=new ScheduleData();
            sd.setTitle(cursor.getString(0));
            sd.setPlaceName(cursor.getString(1));
            sd.setLat(cursor.getDouble(2));
            lat=cursor.getDouble(2);
            sd.setLng(cursor.getDouble(3));
            lng=cursor.getDouble(3);
            sd.setTime(cursor.getInt(4));
            list.add(sd);
        }

        cursor=db.rawQuery("select lat,lng,content,plansuc from plan where lat="+lat+" and lng="+lng+"",null);
        while(cursor.moveToNext()){
            PlanData pd=new PlanData();
            pd.setLat(cursor.getDouble(0));
            pd.setLng(cursor.getDouble(1));
            pd.setContent(cursor.getString(2));
            pd.setPlansuc(cursor.getInt(3));
            list.add(pd);
        }
        return list;
    }

    public ArrayList getAllMarker(){

        SQLiteDatabase db=getReadableDatabase();

        Date date=new Date();

        int month=date.getMonth()+1;
        int day=date.getDate();
        int hour=date.getHours();
        int minute=date.getMinutes();

        int time=month*1000000+day*10000+hour*100+minute;

        db.execSQL("delete from SCHEDULE where time<"+time+";");

        ArrayList allList=new ArrayList();
        Cursor cursor=db.rawQuery("select * from SCHEDULE order by time",null);
        double lat=0.0;
        double lng=0.0;

        while(cursor.moveToNext()){
            ArrayList list=new ArrayList();
            ScheduleData sd=new ScheduleData();
            sd.setTitle(cursor.getString(0));
            sd.setPlaceName(cursor.getString(1));
            sd.setLat(cursor.getDouble(2));
            lat=cursor.getDouble(2);
            sd.setLng(cursor.getDouble(3));
            lng=cursor.getDouble(3);
            sd.setTime(cursor.getInt(4));

            list.add(sd);

            Cursor cursor2=db.rawQuery("select lat,lng,content,plansuc from plan where lat="+lat+" and lng="+lng+"",null);
            while(cursor2.moveToNext()){
                PlanData pd=new PlanData();
                pd.setLat(cursor.getDouble(0));
                pd.setLng(cursor.getDouble(1));
                pd.setContent(cursor.getString(2));
                pd.setPlansuc(cursor.getInt(3));

                list.add(pd);
            }
            allList.add(list);

        }



        return allList;
    }
}