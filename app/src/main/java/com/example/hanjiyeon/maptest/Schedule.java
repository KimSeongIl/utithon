package com.example.hanjiyeon.maptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;


public class Schedule extends AppCompatActivity {

    private ArrayList task_item=null;
    private DBManager dbManager=null;

    double lat;
    double lng;
    String address;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        try {
            Intent intent = getIntent();
            lat = intent.getExtras().getDouble("lat");
            lng = intent.getExtras().getDouble("lng");
            address = intent.getExtras().getString("address");
        }catch(Exception e){}

        Toolbar toolbar_schedule = (Toolbar) findViewById(R.id.toolbar_schedule);
        dbManager= new DBManager(getApplicationContext(), "WitchNote.db", null, 1);
        dbManager.init();

        setSupportActionBar(toolbar_schedule);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ImageButton add_btn=(ImageButton)findViewById(R.id.add_btn);
        task_item=new ArrayList<String>();

        final ArrayAdapter<String> adapter_task= new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, task_item);

        ListView task_list = (ListView) findViewById(R.id.taskList);
        task_list = (ListView) findViewById(R.id.taskList);
        task_list.setAdapter(adapter_task);
        task_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edit_text = (EditText) findViewById(R.id.edit_task);
                String text=edit_text.getText().toString();
                if (text.length() != 0) {
                    task_item.add(text);
                    edit_text.setText("");
                    adapter_task.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: {
                //스케쥴 저장 => 디비 저장
                try {
                    EditText scheduleName = (EditText) findViewById(R.id.scheduleName);
                    EditText month_edit = (EditText) findViewById(R.id.month_edit);
                    EditText date_edit = (EditText) findViewById(R.id.date_edit);
                    EditText hour_edit = (EditText) findViewById(R.id.hour_edit);
                    EditText minute_edit = (EditText) findViewById(R.id.minute_edit);

                    String title = String.valueOf(scheduleName.getText().toString());
                    if (month_edit.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "월 을 입력하세요", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    int month = Integer.parseInt(month_edit.getText().toString());
                    if (date_edit.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "일 을 입력하세요", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    int date = Integer.parseInt(date_edit.getText().toString());
                    if (hour_edit.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "시간 을 입력하세요", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    int hour = Integer.parseInt(hour_edit.getText().toString());
                    if (minute_edit.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "분 을 입력하세요", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    int minute = Integer.parseInt(minute_edit.getText().toString());

                    if (title == null || title.trim().equals("")) {
                        Toast.makeText(getApplicationContext(), "제목을 입력하세요", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    int time = month * 1000000 + date * 10000 + hour * 100 + minute;

                    dbManager.insert("insert into SCHEDULE values('" + title + "', '" + address + "', " + lat + ", " + lng + " , " + time + ");");

                    if(task_item !=null ){
                        for(int i=0; i<task_item.size();i++){
                            String str=task_item.get(i).toString();
                            dbManager.insert("insert into PLAN(lat,lng,content) values("+ lat + ", "+lng+", '"+str+"');");
                        }
                    }
                    Toast.makeText(getApplicationContext(),dbManager.PrintData()+dbManager.getPlan(),Toast.LENGTH_SHORT).show();
                    Log.d("test",dbManager.PrintData()+dbManager.getPlan());
                }catch(Exception e){
                    Log.d("test123",e.toString());
                }

                Intent intentSubActivity =
                        new Intent(Schedule.this, MapsActivity.class);

                intentSubActivity.putExtra("MB", true);
                intentSubActivity.putExtra("SB", false);

                startActivity(intentSubActivity);

                finish();
                return true;
            }
            case android.R.id.home: { //뒤로가기
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);

    }
}