package org.app.gautam.smallcasetask1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by gautam on 03/02/18.
 */

public class ImageAdapter extends ArrayAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private String smallcases[];

    private String imgBaseURL = "https://www.smallcase.com/images/smallcases/187/";
    private String imgType = ".png";

    public ImageAdapter(Context mContext, String[] smallcases) {
        super(mContext, R.layout.imageview_item, smallcases);
        this.mContext = mContext;
        this.smallcases = smallcases;
        inflater = LayoutInflater.from(mContext);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.imageview_item, parent, false);
        }

        String url = imgBaseURL + smallcases[position] + imgType;
        Picasso.with(mContext).load(url).fit().into((ImageView) view);
        return view;
    }

}