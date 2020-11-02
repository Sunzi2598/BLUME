package com.company.blumeSunzi.ui.cart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.company.blumeSunzi.Adapter.MyCartAdapter;
import com.company.blumeSunzi.Callback.ILoadTimeFromFirebaseListener;
import com.company.blumeSunzi.Callback.ISearchCategoryCallbackListener;
import com.company.blumeSunzi.Common.Common;
import com.company.blumeSunzi.Common.MySwipeHelper;
import com.company.blumeSunzi.Database.CartDataSource;
import com.company.blumeSunzi.Database.CartDatabase;
import com.company.blumeSunzi.Database.CartItem;
import com.company.blumeSunzi.Database.LocalCartDataSource;
import com.company.blumeSunzi.EventBus.CounterCartEvent;
import com.company.blumeSunzi.EventBus.HideFABCart;
import com.company.blumeSunzi.EventBus.MenuItemBack;
import com.company.blumeSunzi.EventBus.UpdateItemInCart;
import com.company.blumeSunzi.Model.AddonModel;
import com.company.blumeSunzi.Model.CategoryModel;
import com.company.blumeSunzi.Model.FCMSendData;
import com.company.blumeSunzi.Model.FlowerModel;
import com.company.blumeSunzi.Model.OrderModel;
import com.company.blumeSunzi.R;
import com.company.blumeSunzi.Remote.ICloudFunctions;
import com.company.blumeSunzi.Remote.IFCMService;
import com.company.blumeSunzi.Remote.RetrofitFCMClient;
import com.company.blumeSunzi.Remote.RetrofitICloudClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CartFragment extends Fragment implements ILoadTimeFromFirebaseListener, ISearchCategoryCallbackListener, TextWatcher {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BottomSheetDialog bottomSheetDialog;
    private ChipGroup chip_group_add_on,chip_group_user_selected_addon;
    private EditText edt_search;
    private static final int REQUEST_BRAINTREE_CODE = 7777;
    ICloudFunctions cloudFunctions;
    ILoadTimeFromFirebaseListener listener;

    private CartDataSource cartDataSource;
    private Parcelable recyclerViewState;
    private CartViewModel cartViewModel;
    private Place placeSelected;
    private AutocompleteSupportFragment places_fragment;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields= Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG);
    String address,comment;

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    IFCMService ifcmService;
    private ISearchCategoryCallbackListener searchFlowerCallbackListener;


    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    @BindView(R.id.txt_total_price)
    TextView txt_total_price;
    @BindView(R.id.txt_empty_cart)
    TextView txt_empty_cart;
    @BindView(R.id.group_place_holder)
    CardView group_place_holder;

    @SuppressLint("MissingPermission")
    @OnClick(R.id.btn_place_order)
    void onPlaceOrderClick(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("One more step");

        View view=LayoutInflater.from(getContext()).inflate(R.layout.layout_place_order,null);


        EditText edt_comment=(EditText)view.findViewById(R.id.edt_comment);
        TextView txt_address=(TextView) view.findViewById(R.id.txt_address_detail);
        RadioButton rdi_home=(RadioButton)view.findViewById(R.id.rdi_home_address);
        RadioButton rdi_other_address=(RadioButton)view.findViewById(R.id.rdi_other_address);
        RadioButton rdi_ship_to_this_address=(RadioButton)view.findViewById(R.id.rdi_ship_this_address);
        RadioButton rdi_cod=(RadioButton)view.findViewById(R.id.rdi_cod);
        RadioButton rdi_braintree=(RadioButton)view.findViewById(R.id.rdi_braintree);
        places_fragment=(AutocompleteSupportFragment)getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.places_autocomplete_fragment);
        places_fragment.setPlaceFields(placeFields);
        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeSelected  = place;
                txt_address.setText(place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getContext(), ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Data
        txt_address.setText(Common.currentUser.getAddress()); //By default select home address, so user's address will dispaly

        //Event
        rdi_home.setOnCheckedChangeListener((compoundButton, b)->{
            if(b){
                txt_address.setText(Common.currentUser.getAddress());
                txt_address.setVisibility(View.VISIBLE);
                places_fragment.setHint(Common.currentUser.getAddress());
            }
        });
        rdi_other_address.setOnCheckedChangeListener((compoundButton, b)->{
            if(b){
                txt_address.setVisibility(View.VISIBLE);
            }
        });
        rdi_ship_to_this_address.setOnCheckedChangeListener((compoundButton, b)->{
            if(b){
                fusedLocationProviderClient.getLastLocation()
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            txt_address.setVisibility(View.GONE);
                        }).addOnCompleteListener(task -> {
                    String coordinates=new StringBuilder()
                            .append(task.getResult().getLatitude())
                            .append("/")
                            .append(task.getResult().getLongitude()).toString();

                    Single<String> singleAddress=Single.just(getAddressFromLatLng(task.getResult().getLatitude(),
                            task.getResult().getLongitude()));
                    Disposable disposable=singleAddress.subscribeWith(new DisposableSingleObserver<String>(){
                        @Override
                        public void onSuccess(String s) {
                            txt_address.setText(s);
                            txt_address.setVisibility(View.VISIBLE);
                            places_fragment.setHint(s);
                        }

                        @Override
                        public void onError(Throwable e) {
                            txt_address.setText(e.getMessage());
                            txt_address.setVisibility(View.VISIBLE);
                        }
                    });


                });
            }
        });

        builder.setView(view);
        builder.setNegativeButton("NO",(dialogInterface, i)->{
            dialogInterface.dismiss();
        }).setPositiveButton("YES",(dialogInterface, i)->{
            //Toast.makeText(getContext(), "Implement late", Toast.LENGTH_SHORT).show();
            if(rdi_cod.isChecked())
                paymentCOD(txt_address.getText().toString(),edt_comment.getText().toString());
            else if(rdi_braintree.isChecked()){
                address=txt_address.getText().toString();
                comment=edt_comment.getText().toString();
                if(!TextUtils.isEmpty(Common.currentToken)){
                    DropInRequest dropInRequest=new DropInRequest().clientToken(Common.currentToken);
                    startActivityForResult(dropInRequest.getIntent(getContext()),REQUEST_BRAINTREE_CODE);
                }

            }
        });
        AlertDialog dialog=builder.create();
        dialog.setOnDismissListener(dialogInterface -> {
            if(places_fragment!=null){
                getActivity().getSupportFragmentManager()
                        .beginTransaction().remove(places_fragment)
                        .commit();
            }
        });
        dialog.show();
    }

    private void paymentCOD(String address, String comment) {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    //When we have all cartItems we will get total price
                    cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Double>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(Double totalPrice) {
                                    double finalPrice = totalPrice; //We will modify this formula for discount late
                                    OrderModel order= new OrderModel();
                                    order.setUserId(Common.currentUser.getUid());
                                    order.setUserName(Common.currentUser.getName());
                                    order.setUserPhone(Common.currentUser.getPhone());
                                    order.setShippingAddress(address);
                                    order.setComment(comment);

                                    if(currentLocation!=null){
                                        order.setLat(currentLocation.getLatitude());
                                        order.setLng(currentLocation.getLongitude());
                                    }else{
                                        order.setLat(-0.1f);
                                        order.setLng(-0.1f);
                                    }
                                    order.setCartItemList(cartItems);
                                    order.setTotalPayment(totalPrice);
                                    order.setDiscount(0);
                                    order.setFinalPayment(finalPrice);
                                    order.setCod(true);
                                    order.setTransactionId("Cash On Delivery");

                                    //Submit this order object to Firebase
                                    syncLocalTimeWithGlobalTime(order);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if(!e.getMessage().contains("Query returned empty result set"))
                                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }, throwable -> {
                    Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }


    private void writeOrderToFirebase(OrderModel order) {
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .child(Common.createOrderNumber()) //Create order number with only one digit
                .setValue(order)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }).addOnCompleteListener(task -> {
            //Write success
            cartDataSource.cleanCart(Common.currentUser.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {

                            Map<String,String> notiData=new HashMap<>();
                            notiData.put(Common.NOTI_TITLE,"New Order");
                            notiData.put(Common.NOTI_CONTENT,"You have new order from"+Common.currentUser.getPhone());

                            FCMSendData sendData=new FCMSendData(Common.createTopicOrder(),notiData);

                            compositeDisposable.add(ifcmService.sendNotification(sendData)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        Toast.makeText(getContext(), "Order placed Successfully!", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    }, throwable -> {
                                        Toast.makeText(getContext(), "Order was sent but failure to send notification!", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    }));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }


    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder=new Geocoder(getContext(), Locale.getDefault());
        String result="";
        try{
            List<Address> addressList=geocoder.getFromLocation(latitude,longitude,1);
            if(addressList!=null && addressList.size()>0){
                Address address =addressList.get(0); //always get first item
                StringBuilder sb=new StringBuilder(address.getAddressLine(0));
                result=sb.toString();
            }
            else
                result="Address not found";
        }catch (IOException e){
            e.printStackTrace();
            result=e.getMessage();
        }
        return result;
    }

    private Unbinder unbinder;
    private MyCartAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        listener=this;
        cloudFunctions= RetrofitICloudClient.getInstance().create(ICloudFunctions.class);
        ifcmService= RetrofitFCMClient.getInstance().create(IFCMService.class);

        cartViewModel.initCartDataSource(getContext());
        cartViewModel.getMutableLiveDataCartItems().observe(this, new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> cartItems) {
                if (cartItems == null || cartItems.isEmpty()) {
                    recycler_cart.setVisibility(View.GONE);
                    group_place_holder.setVisibility(View.GONE);
                    txt_empty_cart.setVisibility(View.VISIBLE);
                } else {
                    recycler_cart.setVisibility(View.VISIBLE);
                    group_place_holder.setVisibility(View.VISIBLE);
                    txt_empty_cart.setVisibility(View.GONE);

                    adapter = new MyCartAdapter(getContext(), cartItems);
                    recycler_cart.setAdapter(adapter);
                }
            }
        });
        unbinder = ButterKnife.bind(this, root);
        initViews();
        initLocation();
        return root;
    }


    @SuppressLint("MissingPermission")
    private void initLocation() {
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


    private void buildLocationRequest() {
        locationRequest=new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    private void buildLocationCallback() {
        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);
                currentLocation=locationResult.getLastLocation();
            }
        };
    }


    private void initViews() {

        searchFlowerCallbackListener=this;

        initPlaceClient();

        setHasOptionsMenu(true);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        EventBus.getDefault().postSticky(new HideFABCart(true));

        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recycler_cart.setLayoutManager(layoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

        MySwipeHelper mySwipeHelper=new MySwipeHelper(getContext(),recycler_cart,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(),"Delete",30,0, Color.parseColor("#FF3C30"),
                        pos->{
                            CartItem cartItem=adapter.getItemAtPosition(pos);
                            cartDataSource.deleteCartItem(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            adapter.notifyItemRemoved(pos);
                                            sumAllItemInCart(); //Update total price
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true)); //Update FAB
                                            Toast.makeText(getContext(),"Delete item from Cart successful!",Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }));

                buf.add(new MyButton(getContext(),"Update",30,0, Color.parseColor("#5D4037"),
                        pos->{
                            CartItem cartItem=adapter.getItemAtPosition(pos);
                            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                                    .child(cartItem.getCategoryId())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                CategoryModel categoryModel=snapshot.getValue(CategoryModel.class);
                                                searchFlowerCallbackListener.onSearchCategoryFound(categoryModel,cartItem);
                                            }
                                            else {
                                                searchFlowerCallbackListener.onSearchCategoryNotFound("Category not found");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            searchFlowerCallbackListener.onSearchCategoryNotFound(error.getMessage());
                                        }
                                    });
                        }));
            }
        };
        sumAllItemInCart();

        //Addon
        bottomSheetDialog=new BottomSheetDialog(getContext(),R.style.DialogStyle);
        View layout_addon_display=getLayoutInflater().inflate(R.layout.layout_addon_display,null);
        chip_group_add_on=(ChipGroup)layout_addon_display.findViewById(R.id.chip_group_addon);
        edt_search=(EditText)layout_addon_display.findViewById(R.id.edt_search);
        bottomSheetDialog.setContentView(layout_addon_display);

        bottomSheetDialog.setOnDismissListener(dialogInterface -> {
            displayUserSelectedAddon(chip_group_user_selected_addon);
            calculateTotalPrice();
        });
    }

    private void displayUserSelectedAddon(ChipGroup chip_group_user_selected_addon) {
        if(Common.selectedFlower.getUserSelectedAddon()!=null && Common.selectedFlower.getUserSelectedAddon().size()>0){
            chip_group_user_selected_addon.removeAllViews();
            for(AddonModel addonModel:Common.selectedFlower.getUserSelectedAddon()){
                Chip chip=(Chip)getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+$")
                        .append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((compoundButton, b) -> {
                    if(b){
                        if(Common.selectedFlower.getUserSelectedAddon()==null)
                            Common.selectedFlower.setUserSelectedAddon(new ArrayList<>());
                        Common.selectedFlower.getUserSelectedAddon().add(addonModel);
                    }

                });
                chip_group_user_selected_addon.addView(chip);
            }
        }
        else {
                chip_group_user_selected_addon.removeAllViews();
        }
    }

    private void initPlaceClient() {
        Places.initialize(getContext(),getString(R.string.google_maps_key));
        placesClient=Places.createClient(getContext());
    }


    private void calculateTotalPrice() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double price) {
                        txt_total_price.setText(new StringBuilder("Total: $").append(Common.formatPrice(price)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(!e.getMessage().contains("Query returned empty result set"))
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void sumAllItemInCart() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double aDouble) {
                        txt_total_price.setText(new StringBuilder("Total: $").append(aDouble));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(!e.getMessage().contains("Query returned empty"))
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().postSticky(new HideFABCart(false));
        EventBus.getDefault().postSticky(new CounterCartEvent(false));
        cartViewModel.onStop();
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        if(fusedLocationProviderClient !=null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        compositeDisposable.clear();
        super.onStop();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        if(fusedLocationProviderClient !=null)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.getMainLooper());
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onUpdateItemInCartEvent(UpdateItemInCart event){
        if(event.getCartItem()!=null){
            recyclerViewState=recycler_cart.getLayoutManager().onSaveInstanceState();
            cartDataSource.updateCartItems(event.getCartItem())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            calculateTotalPrice();
                            recycler_cart.getLayoutManager().onRestoreInstanceState(recyclerViewState); //Fix error refresh recycler view after update

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), "[UPDATE_CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false); //Hide Home menu already inflate
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cart_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_clear_cart)
        {
            cartDataSource.cleanCart(Common.currentUser.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            Toast.makeText(getContext(), "Clear Cart Success", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_BRAINTREE_CODE){
            if (resultCode== Activity.RESULT_OK){
                DropInResult result=data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce=result.getPaymentMethodNonce();

                //Calculate sum cart
                cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Double>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Double totalPrice) {
                                //Get all item in cart to create order
                                compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(cartItems -> {
                                            //Submit payment
                                            compositeDisposable.add(cloudFunctions.submitPayment(
                                                    totalPrice,
                                                    nonce.getNonce())
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(braintreeTransaction -> {
                                                        if(braintreeTransaction.isSuccess()){
                                                            double finalPrice = totalPrice; //We will modify this formula for discount late
                                                            OrderModel order= new OrderModel();
                                                            order.setUserId(Common.currentUser.getUid());
                                                            order.setUserName(Common.currentUser.getName());
                                                            order.setUserPhone(Common.currentUser.getPhone());
                                                            order.setComment(comment);

                                                            order.setCartItemList(cartItems);
                                                            order.setTotalPayment(totalPrice);
                                                            order.setDiscount(0);
                                                            order.setFinalPayment(finalPrice);
                                                            order.setCod(false);
                                                            order.setTransactionId(braintreeTransaction.getTransaction().getId());

                                                            //Submit this order object to Firebase
                                                            //writeOrderToFirebase(order);
                                                            syncLocalTimeWithGlobalTime(order);
                                                        }
                                                    },throwable -> {
                                                        Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }));
                                        },throwable -> {
                                            Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }));
                            }

                            @Override
                            public void onError(Throwable e) {
                                if(!e.getMessage().contains("Query returned empty result set"))
                                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void syncLocalTimeWithGlobalTime(OrderModel order) {
        final DatabaseReference offsetRef=FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long offset=dataSnapshot.getValue(Long.class);
                long estimatedServerTimeMs=System.currentTimeMillis()+offset; //offset is missing time between your local time and server time
                SimpleDateFormat sdf=new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Date resultDate = new Date(estimatedServerTimeMs);
                Log.d("TEST_DATE",""+sdf.format(resultDate));

                listener.onLoadTimeSuccess(order,estimatedServerTimeMs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onLoadTimeFailed(databaseError.getMessage());
            }
        });
    }
    @Override
    public void onLoadTimeSuccess(OrderModel order, long estimateTimeInMs) {
        order.setCreateDate(estimateTimeInMs);
        order.setOrderStatus(0);
        writeOrderToFirebase(order);
    }

    @Override
    public void onLoadTimeFailed(String message) {
        Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }


    @Override
    public void onSearchCategoryFound(CategoryModel categoryModel,CartItem cartItem) {
        FlowerModel flowerModel=Common.findFlowerInListById(categoryModel,cartItem.getFlowerId());
        if(flowerModel!=null){
            showUpdateDialog(cartItem,flowerModel);
        }
    }

    private void showUpdateDialog(CartItem cartItem, FlowerModel flowerModel) {
        Common.selectedFlower=flowerModel;
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        View itemView=LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_update_cart, null);
        builder.setView(itemView);

        //View
        Button btn_cancel=(Button)itemView.findViewById(R.id.btn_cancel);
        Button btn_ok=(Button)itemView.findViewById(R.id.btn_ok);

        chip_group_user_selected_addon=(ChipGroup)itemView.findViewById(R.id.chip_group_user_selected_addon);
        ImageView img_add_on=(ImageView)itemView.findViewById(R.id.img_add_addon);
        img_add_on.setOnClickListener(view -> {
            if(flowerModel.getAddon()!=null)
            {
                displayAddonList();
                bottomSheetDialog.show();
            }
        });

        //Addon
        displayAlreadySelectedAddon(chip_group_user_selected_addon,cartItem);

        //Show Dialog
        AlertDialog dialog=builder.create();
        dialog.show();

        //Custom Dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        //Event
        btn_ok.setOnClickListener(view->{
            //First delete item in cart
            cartDataSource.deleteCartItem(cartItem)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            if(Common.selectedFlower.getUserSelectedAddon()!=null){
                                cartItem.setFlowerAddon(new Gson().toJson(Common.selectedFlower.getUserSelectedAddon()));
                            }
                            else cartItem.setFlowerAddon("Default");
                            cartItem.setFlowerExtraPrice(Common.calculateExtraPrice(Common.selectedFlower.getUserSelectedAddon()));

                            //Insert new
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() ->{
                                EventBus.getDefault().postSticky(new CounterCartEvent(true)); //Count cart again
                                calculateTotalPrice();
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Update Cart Success", Toast.LENGTH_SHORT).show();
                            },throwable->{
                                Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

    }

    private void displayAlreadySelectedAddon(ChipGroup chip_group_user_selected_addon, CartItem cartItem) {
        if(cartItem.getFlowerAddon()!=null && !cartItem.getFlowerAddon().equals("Default")){
            List<AddonModel> addonModels=new Gson().fromJson(cartItem.getFlowerAddon(), new TypeToken<List<AddonModel>>(){}.getType());
            Common.selectedFlower.setUserSelectedAddon(addonModels);
            chip_group_user_selected_addon.removeAllViews();
            for (AddonModel addonModel:addonModels){
                Chip chip=(Chip)getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+$")
                        .append(addonModel.getPrice()).append(")"));
                chip.setClickable(false);
                chip.setOnCloseIconClickListener(view -> {
                    chip_group_user_selected_addon.removeView(view);
                    Common.selectedFlower.getUserSelectedAddon().remove(addonModel);
                    calculateTotalPrice();
                });
                chip_group_user_selected_addon.addView(chip);
            }
        }
    }

    private void displayAddonList() {
        if(Common.selectedFlower.getAddon()!=null && Common.selectedFlower.getAddon().size()>0)
        {
            chip_group_add_on.clearCheck();
            chip_group_add_on.removeAllViews();
            edt_search.addTextChangedListener(this);

            //Add all view
            for (AddonModel addonModel:Common.selectedFlower.getAddon()){
                Chip chip=(Chip)getLayoutInflater().inflate(R.layout.layout_addon_item,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+$")
                .append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((compoundButton, b) -> {
                    if(b){
                        if(Common.selectedFlower.getUserSelectedAddon()==null)
                            Common.selectedFlower.setUserSelectedAddon(new ArrayList<>());
                        Common.selectedFlower.getUserSelectedAddon().add(addonModel);
                    }

                });
                chip_group_add_on.addView(chip);
            }
        }
    }

    @Override
    public void onSearchCategoryNotFound(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        chip_group_add_on.clearCheck();
        chip_group_add_on.removeAllViews();
        for (AddonModel addonModel:Common.selectedFlower.getAddon()) {
            if (addonModel.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.layout_addon_item, null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+$")
                        .append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b) {
                        if (Common.selectedFlower.getUserSelectedAddon() == null)
                            Common.selectedFlower.setUserSelectedAddon(new ArrayList<>());
                        Common.selectedFlower.getUserSelectedAddon().add(addonModel);
                    }

                });
                chip_group_add_on.addView(chip);
            }
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}