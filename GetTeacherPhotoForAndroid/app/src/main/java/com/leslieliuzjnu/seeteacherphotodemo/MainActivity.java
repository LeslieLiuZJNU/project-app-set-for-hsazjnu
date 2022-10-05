package com.leslieliuzjnu.seeteacherphotodemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends AppCompatActivity {
    TextView textView;
    JSONArray jsonArray = null;
    int index = -1;
    ImageView bmImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        bmImage = findViewById(R.id.imageView);
        new JsonTask().execute("http://10.1.47.99:8000/dataservice/api/1/159?pagenum=1&pagesize=500");

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                String name = null;
                String zp = null;
                try {
                    name = jsonArray.getJSONObject(index).getString("tea_name");
                    zp = jsonArray.getJSONObject(index).getString("zp");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(zp);
                textView.setText(name);
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            InputStream stream = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
//                connection.setDoOutput(true);
                connection.setRequestMethod("GET");
//                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//                connection.setRequestProperty("Accept", "application/json");
                connection.connect();
//                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("username", "");
//                jsonObject.put("message", "");
//                jsonObject.put("latitude", "0");
//                jsonObject.put("longitude", "0");
//                jsonObject.put("id", "1");
//                os.writeBytes(jsonObject.toString());
//                os.flush();
//                os.close();
                stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

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

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            try {
//                textView.setText(jsonObject.getJSONObject("data").getJSONArray("rows").getJSONObject(0).toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            try {
                jsonArray = jsonObject.getJSONObject("data").getJSONArray("rows");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            textView.setText("finish");

        }
    }
}