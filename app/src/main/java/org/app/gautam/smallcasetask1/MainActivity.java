package org.app.gautam.smallcasetask1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {

    private String smallcaseList[] = {
            "SCMO_0002", "SCMO_0003", "SCMO_0006", "SCNM_0003",
            "SCNM_0007", "SCNM_0008", "SCNM_0009", "SCMO_0001"
    };
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(MainActivity.this, smallcaseList));

        // checks only for internet not available
        // does not check for smallcase server being online

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Bundle b = new Bundle();
                b.putInt("index", position); //smallcase position
                intent.putExtras(b);
                startActivity(intent);
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isNetworkAvailable()) {
            Snackbar.make(gridView, "App is offline", Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
