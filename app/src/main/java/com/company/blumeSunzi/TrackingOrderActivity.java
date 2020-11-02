package com.company.blumeSunzi;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.company.blumeSunzi.Adapter.MyCustomMakerAdapter;
import com.company.blumeSunzi.Common.Common;
import com.company.blumeSunzi.Model.ShippingOrderModel;
import com.company.blumeSunzi.Remote.IGoogleAPI;
import com.company.blumeSunzi.Remote.RetrofitGoogleClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.company.blumeSunzi.Common.Common.currentShippingOrder;

public class TrackingOrderActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    private Marker shipperMarker;
    CompositeDisposable compositeDisposable=new CompositeDisposable();
    private Polyline redPoliline;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private List<LatLng> polylineList;
    IGoogleAPI iGoogleAPI;
    private DatabaseReference shiperRef;
    private Handler handler;
    private int index,next;
    private LatLng start,end;
    private float v;
    private double lat,lng;
    private boolean isInit;
    private Polyline greyPoliline,blackPolyline;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @OnClick(R.id.btn_call)
    void onCallView(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            return;
    Intent intent=new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(new StringBuilder("tel:")
        .append(currentShippingOrder.getOrderModel().getUserPhone()).toString()));
    startActivity(intent);

        Dexter.withActivity(this).withPermission(Manifest.permission.CALL_PHONE)
                .withListener(new PermissionListener() {
        @Override
        public void onPermissionGranted(PermissionGrantedResponse response) {

        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse response) {
            Toast.makeText(TrackingOrderActivity.this, "You must accept this permission to call user", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionRationaleShouldBeShown(PermissionRequest
        permission, PermissionToken token) {

        }
    }).check();

}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);
        iGoogleAPI= RetrofitGoogleClient.getInstance().create(IGoogleAPI.class);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        subscribeShipperMove();
    }

    private void subscribeShipperMove() {
        shiperRef= FirebaseDatabase.getInstance().getReference(Common.SHIPPING_ORDER_REF)
                .child(currentShippingOrder.getKey());
        shiperRef.addValueEventListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new MyCustomMakerAdapter(getLayoutInflater()));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        try{
            boolean success=googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
                    R.raw.uber_light_with_label));
            if(!success)
                Log.e("Blume","Style parsing failed");
        }catch (Resources.NotFoundException ex){
            Log.e("Blume","Resource not found");
        }

       drawRoutes();
    }

    private void drawRoutes() {
            LatLng locationOrder=new LatLng(currentShippingOrder.getOrderModel().getLat(),
                    currentShippingOrder.getOrderModel().getLng());
            LatLng locationShipper=new LatLng(currentShippingOrder.getCurrentLat(), currentShippingOrder.getCurrentLng());


        //Add box
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.box))
                .title(currentShippingOrder.getOrderModel().getUserName())
                .snippet(currentShippingOrder.getOrderModel().getShippingAddress())
                .position(new LatLng(currentShippingOrder.getOrderModel().getLat(),currentShippingOrder.getOrderModel().getLng())));

        //Add shipper
        if(shipperMarker==null){
            //Inflate drawable
            int height, width;
            height=width=80;
            BitmapDrawable bitmapDrawable=(BitmapDrawable) ContextCompat
                    .getDrawable(TrackingOrderActivity.this, R.drawable.shippernew);
            Bitmap resized =Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), width, height, false);

            shipperMarker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(resized))
                    .title(new StringBuilder("Shipper: ").append(currentShippingOrder.getShipperName()).toString())
                    .snippet(new StringBuilder("Shipper Info: ").append(currentShippingOrder.getShipperPhone())
                            .append("\n")
                            .append("Estimate Time Delivery: ")
                            .append(currentShippingOrder.getEstimateTime()).toString())
                    .position(locationShipper));

            shipperMarker.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper, 18));
        }
        else{
            shipperMarker.setPosition(locationShipper);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper, 18));
        }
        //Draw routes
        String to=new StringBuilder()
                .append(currentShippingOrder.getOrderModel().getLat())
                .append(",")
                .append(currentShippingOrder.getOrderModel().getLng())
                .toString();
        String from=new StringBuilder()
                .append(currentShippingOrder.getCurrentLat())
                .append(",")
                .append(currentShippingOrder.getCurrentLng())
                .toString();

        compositeDisposable.add(iGoogleAPI.getDirections("driving", "less_driving", from,to,getString(R.string.google_maps_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s-> {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyline = poly.getString("points");
                            polylineList = Common.decodePoly(polyline);
                        }
                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.RED);
                        polylineOptions.width(12);
                        polylineOptions.startCap(new SquareCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polylineList);
                        redPoliline = mMap.addPolyline(polylineOptions);
                    }
                    catch(Exception e){
                        Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },throwable->Toast.makeText(TrackingOrderActivity.this, ""+throwable, Toast.LENGTH_SHORT).show()));

    }

    @Override
    protected void onDestroy() {
        shiperRef.removeEventListener(this);
        isInit=false;
        super.onDestroy();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        //Save old position
        String from=new StringBuilder()
                .append(currentShippingOrder.getCurrentLat())
                .append(",")
                .append(currentShippingOrder.getCurrentLng())
                .toString();

        //Update position
        Common.currentShippingOrder = snapshot.getValue(ShippingOrderModel.class);
        Common.currentShippingOrder.setKey(snapshot.getKey());

        //Save new position
        String to=new StringBuilder()
                .append(currentShippingOrder.getCurrentLat())
                .append(",")
                .append(currentShippingOrder.getCurrentLng())
                .toString();
        if(snapshot.exists()){
            if(isInit){
                moveMarkerAnimation(shipperMarker,from,to);
            }
            else  isInit=true;
        }

    }

    private void moveMarkerAnimation(Marker shipperMarker, String from, String to) {
        compositeDisposable.add(iGoogleAPI.getDirections("driving","less_driving",from,to,getString(R.string.google_maps_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(returnResult ->{
                    try{
                        //Parse json
                        JSONObject jsonObject=new JSONObject(returnResult);
                        JSONArray jsonArray=jsonObject.getJSONArray("routes");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject route=jsonArray.getJSONObject(i);
                            JSONObject poly=route.getJSONObject("overview_polyline");
                            String polyline=poly.getString("points");
                            polylineList=Common.decodePoly(polyline);
                        }

                        polylineOptions=new PolylineOptions();
                        polylineOptions.color(Color.GRAY);
                        polylineOptions.width(5);
                        polylineOptions.startCap(new SquareCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polylineList);
                        greyPoliline=mMap.addPolyline(polylineOptions);

                        blackPolylineOptions=new PolylineOptions();
                        blackPolylineOptions.color(Color.BLACK);
                        blackPolylineOptions.width(5);
                        blackPolylineOptions.startCap(new SquareCap());
                        blackPolylineOptions.jointType(JointType.ROUND);
                        blackPolylineOptions.addAll(polylineList);
                        blackPolyline=mMap.addPolyline(blackPolylineOptions);

                        //Animator
                        ValueAnimator polylineAnimator=ValueAnimator.ofInt(0,100);
                        polylineAnimator.setDuration(2000);
                        polylineAnimator.setInterpolator(new LinearInterpolator());
                        polylineAnimator.addUpdateListener(valueAnimator -> {
                            List<LatLng> points=greyPoliline.getPoints();
                            int percentValue=(int)valueAnimator.getAnimatedValue();
                            int size=points.size();
                            int newPoints=(int)(size*(percentValue/100.0f));
                            List<LatLng>p=points.subList(0,newPoints);
                            blackPolyline.setPoints(p);
                        });
                        polylineAnimator.start();

                        //Bike moving
                        handler=new Handler();
                        index=-1;
                        next=1;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(index<polylineList.size()-1){
                                    index++;
                                    next=index+1;
                                    start=polylineList.get(index);
                                    end=polylineList.get(next);
                                }
                                ValueAnimator valueAnimator=ValueAnimator.ofInt(0,1);
                                valueAnimator.setDuration(1500);
                                valueAnimator.setInterpolator(new LinearInterpolator());
                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        v=valueAnimator.getAnimatedFraction();
                                        lng=v*end.longitude+(1-v)*start.longitude;
                                        lat=v*end.latitude+(1-v)*start.latitude;
                                        LatLng newPos=new LatLng(lat,lng);
                                        shipperMarker.setPosition(newPos);
                                        shipperMarker.setAnchor(0.5f,0.5f);
                                        shipperMarker.setRotation(Common.getBearing(start, newPos ));

                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(shipperMarker.getPosition()));

                                    }
                                });
                                valueAnimator.start();
                                if(index<polylineList.size()-2)//Reach destination
                                    handler.postDelayed(this,1500);
                            }
                        }, 1500);

                    }catch (Exception e){

                    }
                },throwable->{
                    if(throwable!=null)
                        Toast.makeText(this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}