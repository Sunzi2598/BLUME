package com.company.blumeSunzi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.company.blumeSunzi.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MyCustomMakerAdapter implements GoogleMap.InfoWindowAdapter {

    private View itemView;

    public MyCustomMakerAdapter(LayoutInflater layoutInflater) {
        itemView = layoutInflater.inflate(R.layout.layout_marker_display, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView txt_shipper_name=(TextView)itemView.findViewById(R.id.txt_shipper_name);
        TextView txt_shipper_info=(TextView)itemView.findViewById(R.id.txt_shipper_info);
        txt_shipper_name.setText(marker.getTitle());
        txt_shipper_info.setText(marker.getSnippet());
        return itemView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
