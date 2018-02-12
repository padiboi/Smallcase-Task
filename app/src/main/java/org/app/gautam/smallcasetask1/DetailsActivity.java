package org.app.gautam.smallcasetask1;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gautam on 03/02/18.
 */

public class DetailsActivity extends AppCompatActivity {

    private String smallcaseList[] = {
            "SCMO_0002", "SCMO_0003", "SCMO_0006", "SCNM_0003",
            "SCNM_0007", "SCNM_0008", "SCNM_0009", "SCMO_0001"
    };

    private static final String imgBaseURL = "https://www.smallcase.com/images/smallcases/187/";
    private static final String dataBaseURL = "https://api-dev.smallcase.com/smallcases/smallcase?scid=";
    private static final String histBaseURL = "https://api-dev.smallcase.com/smallcases/historical?scid=";
    private static final String imgType = ".png";
    private int smallcase_no = -1;

    private TextView rationale_tv, index_tv, yearet_tv, moret_tv, title_tv, ra_title;
    private LineChart chart;
    private List<Entry> entries = new ArrayList<Entry>();
    private ScrollView cl;
    private SharedPreferences cache;
    private SharedPreferences.Editor cacheEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_details_activity);

        Bundle b = getIntent().getExtras();
        if (b != null)
            smallcase_no = b.getInt("index");

        String url = imgBaseURL + smallcaseList[smallcase_no] + imgType;

        ImageView imgview = (ImageView) findViewById(R.id.detail_image);
        Picasso.with(DetailsActivity.this).load(url).into(imgview);
        rationale_tv = (TextView) findViewById(R.id.detail_rationale);
        index_tv = (TextView) findViewById(R.id.detail_index);
        yearet_tv = (TextView) findViewById(R.id.detail_1yr_ret);
        moret_tv = (TextView) findViewById(R.id.detail_1mo_ret);
        title_tv = (TextView) findViewById(R.id.detail_title);
        ra_title = (TextView) findViewById(R.id.rationale_title);
        cache = getApplicationContext().getSharedPreferences("cache", MODE_PRIVATE);
        cl = (ScrollView) findViewById(R.id.layout);

        fetchSmallcaseDetails();
        fetchHistoricData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isNetworkAvailable()) {
            Snackbar.make(cl, "App is offline", Snackbar.LENGTH_LONG).show();
        }
    }

    private void updateUI(String jsonString) {

        if (!(jsonString.compareTo("null") == 0)) {
            try {
                JSONObject json = new JSONObject(jsonString);
                JSONObject data = json.getJSONObject("data");
                JSONObject stats = data.getJSONObject("stats");
                JSONObject returns = stats.getJSONObject("returns");
                ra_title.setText("Rationale");
                title_tv.setText(data.getJSONObject("info").getString("name"));

                rationale_tv.setText(data.getString("rationale"));

                index_tv.setText("Index : " + roundTwoDecimals(stats.getDouble("indexValue")) + "");
                moret_tv.setText("1 Month : " + roundTwoDecimals(returns.getDouble("monthly")) + "%");
                yearet_tv.setText("1 Year : " + roundTwoDecimals(returns.getDouble("yearly")) + "%");

            } catch (JSONException j) {
                rationale_tv.setText("Could not parse server response");
                Log.i("gpexception", j.getMessage());
            }
        }
    }

    private void fetchSmallcaseDetails() {

        final String sm_ID = smallcaseList[smallcase_no];

        if (!isNetworkAvailable()) {
            String cachedData = cache.getString(sm_ID + "data", "null");
            updateUI(cachedData);
        } else {

            String url = dataBaseURL + sm_ID;

            RequestQueue mRequestQueue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            writeToCache(sm_ID + "data", response);
                            updateUI(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            mRequestQueue.add(stringRequest);
        }

    }

    private void updateChart(String histString) {

        chart = (LineChart) findViewById(R.id.chart);

        if (!(histString.compareTo("null") == 0)) {
            try {
                JSONObject json = new JSONObject(histString);
                final JSONArray histArray = json.getJSONObject("data").getJSONArray("points");
                final ArrayList<String> dates = new ArrayList<>();

                for (int i = 0; i < histArray.length(); i++) {
                    JSONObject j = histArray.getJSONObject(i);
                    String date = (String) j.get("date");
                    date = date.substring(0, 10);
                    dates.add(date);
                    long value = j.getLong("index");
                    Entry e = new Entry(i, value);
                    entries.add(e);
                }

                LineDataSet dataSet = new LineDataSet(entries, "Historic");
                LineData lineData = new LineData(dataSet);

                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return dates.get((int) value);
                    }
                });
                chart.invalidate();
                chart.setData(lineData);

            } catch (JSONException j) {
                Log.i("gpexception", j.getMessage());
            }
        }

    }

    private void fetchHistoricData() {

        final String sm_ID = smallcaseList[smallcase_no];

        if (!isNetworkAvailable()) {
            String cachedData = cache.getString(sm_ID + "hist", "null");
            updateChart(cachedData);
        } else {

            String url = histBaseURL + sm_ID;

            RequestQueue mRequestQueue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            writeToCache(sm_ID + "hist", response);
                            updateChart(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("gperror", error.getMessage());
                }
            });
            mRequestQueue.add(stringRequest);
        }

    }

    private void writeToCache(String key, String response) {
        cacheEditor = cache.edit();
        cacheEditor.putString(key, response).apply();
    }

    Double roundTwoDecimals(Double f) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(f));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
