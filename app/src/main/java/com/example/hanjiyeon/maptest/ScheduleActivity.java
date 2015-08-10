package com.example.hanjiyeon.maptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Toolbar toolbar_schedule = (Toolbar) findViewById(R.id.toolbar_schedule);

        setSupportActionBar(toolbar_schedule);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ImageButton add_btn=(ImageButton)findViewById(R.id.add_btn);

        final ArrayList task_item=new ArrayList<String>();
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
                TextView address=(TextView)findViewById(R.id.address_tv);

                address.getText();

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
