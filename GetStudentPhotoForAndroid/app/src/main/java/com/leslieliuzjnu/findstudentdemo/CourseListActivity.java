package com.leslieliuzjnu.findstudentdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CourseListActivity extends AppCompatActivity {
    ListView courseListView;
    TextView textView;
    ArrayAdapter<String> courseArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        textView = findViewById(R.id.studentInfoTextView);
        String studentInfo = "";
        studentInfo += "姓名：" + getIntent().getExtras().getString("NAME") + "\n";
        studentInfo += "学号：" + getIntent().getExtras().getString("NUMBER") + "\n";
        studentInfo += "年级：" + getIntent().getExtras().getString("GRADE") + "\n";
        studentInfo += "班级编号：" + getIntent().getExtras().getString("ORGANIZATION") + "\n";
        studentInfo += "日期：" + getIntent().getExtras().getString("DATE") + "\n";
        studentInfo += "星期：" + getIntent().getExtras().getString("DAY");
        textView.setText(studentInfo);
        courseListView = findViewById(R.id.courseListView);
        courseArrayAdapter = new ArrayAdapter<String>(this, R.layout.item_course);
        courseListView.setAdapter(courseArrayAdapter);
        ArrayList<String> stringArrayList = getIntent().getExtras().getStringArrayList("STRING_ARRAY_LIST");
        for (int index = 0; index < stringArrayList.size(); index++)
            courseArrayAdapter.add(stringArrayList.get(index));
    }
}