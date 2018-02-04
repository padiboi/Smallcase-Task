package org.app.gautam.smallcasetask1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String smallcaseList[] = {
            "SCMO_0002", "SCMO_0003", "SCMO_0006", "SCNM_0003",
            "SCNM_0007", "SCNM_0008", "SCNM_0009", "SCMO_0001"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(MainActivity.this, smallcaseList));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, position + smallcaseList[position],
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Bundle b = new Bundle();
                b.putInt("index", position); //smallcase position
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        if(!isNetworkAvailable())
            Toast.makeText(MainActivity.this, "App is offline",
                    Toast.LENGTH_LONG).show();
        // checks only for internet not available
        // does not check for smallcase server being online

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
