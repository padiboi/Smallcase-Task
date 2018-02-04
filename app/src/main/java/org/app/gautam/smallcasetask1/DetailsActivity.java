package org.app.gautam.smallcasetask1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by gautam on 03/02/18.
 */

public class DetailsActivity extends AppCompatActivity {

    private String smallcaseList[] = {
            "SCMO_0002", "SCMO_0003", "SCMO_0006", "SCNM_0003",
            "SCNM_0007", "SCNM_0008", "SCNM_0009", "SCMO_0001"
    };

    private String imgBaseURL = "https://www.smallcase.com/images/smallcases/187/";
    private String dataBaseURL = "https://api-dev.smallcase.com/smallcases/smallcase?scid=";
    private String histBaseURL = "https://api-dev.smallcase.com/smallcases/historical?scid=";
    private String imgType = ".png";
    private int smallcase_no = -1;

    TextView rationale_tv, index_tv, yearet_tv, moret_tv, title_tv;
    private static String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        Bundle b = getIntent().getExtras();
        if(b != null)
            smallcase_no = b.getInt("index");

        String url = imgBaseURL + smallcaseList[smallcase_no] + imgType;

        ImageView imgview = (ImageView) findViewById(R.id.detail_image);
        Picasso.with(DetailsActivity.this).load(url).into(imgview);
        rationale_tv = (TextView) findViewById(R.id.detail_rationale);
        index_tv = (TextView) findViewById(R.id.detail_index);
        yearet_tv = (TextView) findViewById(R.id.detail_1yr_ret);
        moret_tv = (TextView) findViewById(R.id.detail_1mo_ret);
        title_tv = (TextView) findViewById(R.id.detail_title);
        fetchSmallcaseDetails();

    }

    private void updateUI() {

        rationale_tv.setText(jsonString);

        try {
            JSONObject json = new JSONObject(jsonString);
            JSONObject data = json.getJSONObject("data");
            JSONObject stats = data.getJSONObject("stats");
            JSONObject returns = stats.getJSONObject("returns");

            title_tv.setText(data.getJSONObject("info").getString("name"));

            rationale_tv.setText(data.getString("rationale"));

            index_tv.setText(roundTwoDecimals(stats.getDouble("indexValue")) + "");
            moret_tv.setText(roundTwoDecimals(returns.getDouble("monthly")) + "%");
            yearet_tv.setText(roundTwoDecimals(returns.getDouble("yearly")) + "%");

        }catch (JSONException j){
        }

    }

    private void fetchSmallcaseDetails(){

        RequestQueue mRequestQueue;
        String url = dataBaseURL + smallcaseList[smallcase_no];
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        jsonString = response;
                        updateUI();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                jsonString = "null";
            }
        });
        mRequestQueue.add(stringRequest);

    }

    Double roundTwoDecimals(Double f) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(f));
    }

}
