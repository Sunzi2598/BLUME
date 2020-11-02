package com.company.blumeSunzi.ui.view_orders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidwidgets.formatedittext.widgets.FormatEditText;
import com.company.blumeSunzi.Adapter.MyOrdersAdapter;
import com.company.blumeSunzi.Callback.ILoadOrderCallbackListener;
import com.company.blumeSunzi.Common.Common;
import com.company.blumeSunzi.Common.MySwipeHelper;
import com.company.blumeSunzi.Database.CartDataSource;
import com.company.blumeSunzi.Database.CartDatabase;
import com.company.blumeSunzi.Database.CartItem;
import com.company.blumeSunzi.Database.LocalCartDataSource;
import com.company.blumeSunzi.EventBus.CounterCartEvent;
import com.company.blumeSunzi.EventBus.MenuItemBack;
import com.company.blumeSunzi.Model.OrderModel;
import com.company.blumeSunzi.Model.RefundRequestModel;
import com.company.blumeSunzi.Model.ShippingOrderModel;
import com.company.blumeSunzi.R;
import com.company.blumeSunzi.TrackingOrderActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ViewOrdersFragment extends Fragment implements ILoadOrderCallbackListener {

    private ViewOrdersViewModel viewOrdersViewModel;
    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;

    private Unbinder unbinder;
    android.app.AlertDialog dialog;
    private ILoadOrderCallbackListener listener;
    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable=new CompositeDisposable();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewOrdersViewModel =
                ViewModelProviders.of(this).get(ViewOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_view_orders, container, false);
        unbinder= ButterKnife.bind(this,root);

        initViews(root);
        loadOrdersFromFirebase();

        viewOrdersViewModel.getMutableLiveDataOrderList().observe(this,orderList->{
            Collections.reverse(orderList);
            MyOrdersAdapter adapter=new MyOrdersAdapter(getContext(),orderList);
            recycler_orders.setAdapter(adapter);
        });

        return root;
    }

    private void loadOrdersFromFirebase() {
        List<OrderModel> orderList=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .orderByChild("userId")
                .equalTo(Common.currentUser.getUid())
                .limitToLast(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot orderSnapShot:dataSnapshot.getChildren()){
                            OrderModel order=orderSnapShot.getValue(OrderModel.class);
                            order.setOrderNumber(orderSnapShot.getKey());
                            orderList.add(order);
                        }
                        listener.onLoadOrderSuccess(orderList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onLoadOrderFailed(databaseError.getMessage());
                    }
                });
    }

    private void initViews(View root) {

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        listener=this;
        dialog=new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();

        recycler_orders.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recycler_orders.setLayoutManager(layoutManager);
        recycler_orders.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

        MySwipeHelper mySwipeHelper=new MySwipeHelper(getContext(),recycler_orders,250) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(),"Cancel Order",30,0, Color.parseColor("#FF3C30"),
                        pos->{
                            OrderModel orderModel=((MyOrdersAdapter)recycler_orders.getAdapter()).getItemAtPosition(pos);
                            if(orderModel.getOrderStatus()==0) {
                                if (orderModel.isCod()) {
                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                                    builder.setTitle("Cancel Order")
                                            .setMessage("Do you really want to cancel this order?")
                                            .setNegativeButton("NO", (dialogInterface, i) -> {
                                                dialogInterface.dismiss();
                                            })
                                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                                Map<String,Object>update_data=new HashMap<>();
                                                update_data.put("orderStatus", -1);
                                                FirebaseDatabase.getInstance()
                                                        .getReference(Common.ORDER_REF)
                                                        .child(orderModel.getOrderNumber())
                                                        .updateChildren(update_data)
                                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                                                        .addOnCompleteListener(aVoid -> {
                                                            orderModel.setOrderStatus(-1); //Local Update
                                                            ((MyOrdersAdapter) recycler_orders.getAdapter()).SetItemAtPosition(pos, orderModel);
                                                            recycler_orders.getAdapter().notifyItemChanged(pos);
                                                            Toast.makeText(getContext(), "Cancel order succesfully!", Toast.LENGTH_SHORT).show();
                                                        });
                                            });
                                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                                else //Not cod
                                {
                                        View layout_refund_request=LayoutInflater.from(getContext())
                                                .inflate(R.layout.layout_refund_request,null);

                                    EditText edt_name=(EditText)layout_refund_request.findViewById(R.id.edt_card_name);
                                    FormatEditText edt_card_number=(FormatEditText)layout_refund_request.findViewById(R.id.edt_card_number);
                                    FormatEditText edt_card_exp=(FormatEditText)layout_refund_request.findViewById(R.id.edt_exp);

                                    //Format credit cart
                                    edt_card_number.setFormat("---- ---- ---- ----");
                                    edt_card_exp.setFormat("--/--");

                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                                    builder.setTitle("Cancel Order")
                                            .setMessage("Do you really want to cancel this order?")
                                            .setView(layout_refund_request)
                                            .setNegativeButton("NO", (dialogInterface, i) -> {
                                                dialogInterface.dismiss();
                                            })
                                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                                RefundRequestModel refundRequestModel=new RefundRequestModel();
                                                refundRequestModel.setName(Common.currentUser.getName());
                                                refundRequestModel.setPhone(Common.currentUser.getPhone());
                                                refundRequestModel.setCardName(edt_name.getText().toString());
                                                refundRequestModel.setCardNumber(edt_card_number.getText().toString());
                                                refundRequestModel.setCardExp(edt_card_exp.getText().toString());
                                                refundRequestModel.setAmount(orderModel.getFinalPayment());

                                                FirebaseDatabase.getInstance()
                                                        .getReference(Common.REQUEST_REFUND_MODEL)
                                                        .child(orderModel.getOrderNumber())
                                                        .setValue(refundRequestModel)
                                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                                                        .addOnCompleteListener(aVoid -> {
                                                            Map<String,Object>update_data=new HashMap<>();
                                                            update_data.put("orderStatus", -1);
                                                            FirebaseDatabase.getInstance()
                                                                    .getReference(Common.ORDER_REF)
                                                                    .child(orderModel.getOrderNumber())
                                                                    .updateChildren(update_data)
                                                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                                                                    .addOnCompleteListener(a-> {
                                                                        orderModel.setOrderStatus(-1); //Local Update
                                                                        ((MyOrdersAdapter) recycler_orders.getAdapter()).SetItemAtPosition(pos, orderModel);
                                                                        recycler_orders.getAdapter().notifyItemChanged(pos);
                                                                        Toast.makeText(getContext(), "Cancel order succesfully!", Toast.LENGTH_SHORT).show();
                                                                    });
                                                        });
                                            });
                                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                            else{
                                Toast.makeText(getContext(), new StringBuilder("Your order was chanced to ")
                                        .append(Common.convertStatusToText(orderModel.getOrderStatus()))
                                        .append(", so you can't cancel it!"), Toast.LENGTH_SHORT).show();
                            }
                        }));
                buf.add(new MyButton(getContext(),"Tracking Order",30,0, Color.parseColor("#001970"),
                        pos->{
                            OrderModel orderModel=((MyOrdersAdapter)recycler_orders.getAdapter()).getItemAtPosition(pos);

                            //Fetch from Firebase
                            FirebaseDatabase.getInstance().getReference(Common.SHIPPING_ORDER_REF)
                                    .child(orderModel.getOrderNumber())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                Common.currentShippingOrder = snapshot.getValue(ShippingOrderModel.class);
                                                Common.currentShippingOrder.setKey(snapshot.getKey());
                                                if(Common.currentShippingOrder.getCurrentLat()!=-1 && Common.currentShippingOrder.getCurrentLng()!=-1){
                                                    startActivity(new Intent(getContext(), TrackingOrderActivity.class));
                                                }
                                                else{
                                                    Toast.makeText(getContext(), "Shipper not start ship your order, please wait", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getContext(), "Your order just been placed, you must wait it shipping", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }));
                buf.add(new MyButton(getContext(),"Repeat Order",30,0, Color.parseColor("#5d4037"),
                        pos->{
                            OrderModel orderModel=((MyOrdersAdapter)recycler_orders.getAdapter()).getItemAtPosition(pos);

                            dialog.show();
                            cartDataSource.cleanCart(Common.currentUser.getUid())//Clean all item in cart first
                            .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            //After clean cart just add now
                                            CartItem[] cartItems=orderModel.getCartItemList().toArray(new CartItem[orderModel.getCartItemList().size()]);

                                            //Insert new
                                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItems)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(()->{
                                                dialog.dismiss();
                                                Toast.makeText(getContext(), "Add all item in order to cart success", Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().postSticky(new CounterCartEvent(true));//Count fab
                                            },throwable->{
                                                dialog.dismiss();
                                                Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }));
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            dialog.dismiss();
                                            Toast.makeText(getContext(), "[Error]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }));
            }

        };


    }

    @Override
    public void onLoadOrderSuccess(List<OrderModel> orderList) {
        dialog.dismiss();
        viewOrdersViewModel.setMutableLiveDataOrderList(orderList);
    }

    @Override
    public void onLoadOrderFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }
}