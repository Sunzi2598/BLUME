package com.company.blumeSunzi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.blumeSunzi.Callback.IRecyclerClickListener;
import com.company.blumeSunzi.Common.Common;
import com.company.blumeSunzi.Database.CartDataSource;
import com.company.blumeSunzi.Database.CartDatabase;
import com.company.blumeSunzi.Database.CartItem;
import com.company.blumeSunzi.Database.LocalCartDataSource;
import com.company.blumeSunzi.EventBus.CounterCartEvent;
import com.company.blumeSunzi.EventBus.FlowerItemClick;
import com.company.blumeSunzi.Model.FlowerModel;
import com.company.blumeSunzi.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyFlowerListAdapter extends RecyclerView.Adapter<MyFlowerListAdapter.MyViewHolder> {

    private Context context;
    private List<FlowerModel> flowerModelList;
    private CompositeDisposable compositeDisposable=new CompositeDisposable();
    private CartDataSource cartDataSource;


    public MyFlowerListAdapter(Context context, List<FlowerModel> flowerModelList) {
        this.context = context;
        this.flowerModelList = flowerModelList;
        this.compositeDisposable=new CompositeDisposable();
        this.cartDataSource=new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_flower_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(flowerModelList.get(position).getImage()).into(holder.img_flower_image);
        holder.txt_flower_price.setText(new StringBuilder("$ ").append(flowerModelList.get(position).getPrice()));
        holder.txt_flower_name.setText(new StringBuilder("").append(flowerModelList.get(position).getName()));

        //Event
        holder.setListener((view,pos)->{
            Common.selectedFlower = flowerModelList.get(pos);
            Common.selectedFlower.setKey(String.valueOf(pos));
            EventBus.getDefault().postSticky(new FlowerItemClick(true,flowerModelList.get(pos)));
        });

        holder.img_cart.setOnClickListener(view -> {
            CartItem cartItem = new CartItem();
            cartItem.setUid(Common.currentUser.getUid());
            cartItem.setUserPhone(Common.currentUser.getPhone());
            cartItem.setCategoryId(Common.CategorySelected.getMenu_id());
            cartItem.setFlowerId(flowerModelList.get(position).getId());
            cartItem.setFlowerName(flowerModelList.get(position).getName());
            cartItem.setFlowerImage(flowerModelList.get(position).getImage());
            cartItem.setFlowerPrice(Double.valueOf(String.valueOf(flowerModelList.get(position).getPrice())));
            cartItem.setFlowerQuantity(1);
            cartItem.setFlowerExtraPrice(0.0); // Because default we not choose addon so extra price is 0
            cartItem.setFlowerAddon("Default");

            cartDataSource.getItemWithAllOptionsInCart(Common.currentUser.getUid(),
                    Common.CategorySelected.getMenu_id(),
                    cartItem.getFlowerId(),
                    cartItem.getFlowerAddon())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<CartItem>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(CartItem cartItemFromDB) {
                            if(cartItemFromDB.equals(cartItem)){
                                //Already in database, just update
                                cartItemFromDB.setFlowerExtraPrice(cartItem.getFlowerExtraPrice());
                                cartItemFromDB.setFlowerAddon(cartItem.getFlowerAddon());
                                cartItemFromDB.setFlowerQuantity(cartItemFromDB.getFlowerQuantity()+cartItem.getFlowerQuantity());


                                cartDataSource.updateCartItems(cartItemFromDB)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new SingleObserver<Integer>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onSuccess(Integer integer) {
                                                Toast.makeText(context, "Update Cart success", Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Toast.makeText(context, "[UPDATE CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            else{
                                // Item not available in cart before, insert new
                                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            Toast.makeText(context, "Add to Cart success", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        },throwable -> {
                                            Toast.makeText(context, "[CART ERROR]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(e.getMessage().contains("empty")){
                                //Default, if Cart is empty, this code will be fired
                                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            Toast.makeText(context, "Add to Cart success", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        },throwable -> {
                                            Toast.makeText(context, "[CART ERROR]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }));
                            }
                            else
                                Toast.makeText(context, "[GET CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }


    @Override
    public int getItemCount() {
        return flowerModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;
        @BindView(R.id.txt_flower_name)
        TextView txt_flower_name;
        @BindView(R.id.txt_flower_price)
        TextView txt_flower_price;
        @BindView(R.id.img_flower_image)
        ImageView img_flower_image;
        @BindView(R.id.img_fav)
        ImageView img_fav;
        @BindView(R.id.img_quick_cart)
        ImageView img_cart;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view,getAdapterPosition());
        }
    }
}