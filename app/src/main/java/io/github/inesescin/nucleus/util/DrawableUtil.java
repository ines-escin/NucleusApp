package io.github.inesescin.nucleus.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.inesescin.nucleus.R;

/**
 * Created by jal3 on 01/04/2016.
 */
public class DrawableUtil {

    public static Bitmap getMarkerView(Activity activity, String value, int imageResourceId){
        View marker = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.ecopoint_marker_layout, null);
        TextView numTxt = (TextView) marker.findViewById(R.id.marker_number);
        ImageView imageView = (ImageView) marker.findViewById(R.id.marker_img);
        imageView.setImageResource(imageResourceId);
        numTxt.setText(value);
        return createDrawableFromView(activity, marker);
    }

    private static Bitmap createDrawableFromView(Activity activity, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
