package com.leslieliuzjnu.findstudentdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    final String API_URL_STRING = "http://10.1.47.99:8000/dataservice/api/1/163";
    TextView alarmTextView;
    EditText editText;
    Button button;
    CalendarView calendarView;
    String dateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmTextView = findViewById(R.id.textView_alarm);
        editText = findViewById(R.id.editText);
        calendarView = findViewById(R.id.calendarView);
        dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date(calendarView.getDate()));
        button = findViewById(R.id.button);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Date date = new Date(year - 1900, month, dayOfMonth);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
            dateString = simpleDateFormat.format(date);
        });
        button.setOnClickListener(v -> {
            String schoolCardNumberString = editText.getText().toString();
            if (schoolCardNumberString.isEmpty()) {
                alarmTextView.setText("");
                alarmTextView.setTextColor(Color.parseColor("#ff0000"));
                alarmTextView.setText("Please input the number!");
            } else {
                button.setClickable(false);
                String requestUrlString = API_URL_STRING + "?school_card_number=" + schoolCardNumberString + "&class_date=" + dateString;
                new GetJsonTask().execute(requestUrlString);
            }
        });
    }

    private class GetJsonTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            InputStream stream = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000);
                connection.connect();
                stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                return buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                alarmTextView.setText("");
                alarmTextView.setTextColor(Color.parseColor("#ff0000"));
                alarmTextView.setText("Network Problem!");
                button.setClickable(true);
            } else {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONObject(result).getJSONObject("data").getJSONArray("rows");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (jsonArray == null) {
                    alarmTextView.setText("");
                    alarmTextView.setTextColor(Color.parseColor("#ff0000"));
                    alarmTextView.setText("API Problem!");
                    button.setClickable(true);
                } else if (jsonArray.length() == 0) {
                    alarmTextView.setText("");
                    alarmTextView.setTextColor(Color.parseColor("#0000ff"));
                    alarmTextView.setText("Not Found!");
                    button.setClickable(true);
                } else {
                    alarmTextView.setText("");
                    alarmTextView.setTextColor(Color.parseColor("#00ff00"));
                    alarmTextView.setText("Please Wait!");
                    Intent intent = new Intent(getApplicationContext(), CourseListActivity.class);
                    Bundle bundle = new Bundle();
                    String name = null;
                    String number = null;
                    String grade = null;
                    String organization = null;
                    String date = null;
                    String day = null;
                    try {
                        name = jsonArray.getJSONObject(0).getString("full_name");
                        number = jsonArray.getJSONObject(0).getString("school_card_number");
                        grade = jsonArray.getJSONObject(0).getString("student_grade_name");
                        organization = jsonArray.getJSONObject(0).getString("student_organization_id");
                        date = jsonArray.getJSONObject(0).getString("class_date");
                        day = jsonArray.getJSONObject(0).getString("day");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayList<String> stringArrayList = new ArrayList<String>();
                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject jsonObject = null;
                        String string = "";
                        try {
                            jsonObject = jsonArray.getJSONObject(index);
                            string += "课程名称： " + jsonObject.getString("subject") + "\n";
                            string += "课程地点： " + jsonObject.getString("building_name") + jsonObject.getString("room_name") + "\n";
                            string += "上课时间： " + jsonObject.getString("start_time") + jsonObject.getString("am_or_pm") + "\n";
                            string += "下课时间： " + jsonObject.getString("end_time") + jsonObject.getString("am_or_pm");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        stringArrayList.add(string);
                    }
                    bundle.putString("NAME", name);
                    bundle.putString("NUMBER", number);
                    bundle.putString("GRADE", grade);
                    System.out.println(organization + date + day);
                    bundle.putString("ORGANIZATION", organization);
                    bundle.putString("DATE", date);
                    bundle.putString("DAY", day);
                    bundle.putStringArrayList("STRING_ARRAY_LIST", stringArrayList);
                    intent.putExtras(bundle);
                    button.setClickable(true);
                    alarmTextView.setText("");
                    startActivity(intent);
                }
            }
        }
    }
}