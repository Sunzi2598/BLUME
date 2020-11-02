package com.company.blumeSunzi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.company.blumeSunzi.Common.Common;
import com.company.blumeSunzi.Database.CartItem;
import com.company.blumeSunzi.EventBus.UpdateItemInCart;
import com.company.blumeSunzi.Model.AddonModel;
import com.company.blumeSunzi.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    Context context;

    public MyCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.gson=new Gson();
    }

    List<CartItem> cartItemList;
    Gson gson;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(cartItemList.get(position).getFlowerImage()).into(holder.img_cart);
        holder.txt_flower_name.setText(new StringBuilder(cartItemList.get(position).getFlowerName()));
        holder.txt_flower_price.setText(new StringBuilder("").append(cartItemList.get(position).getFlowerPrice()+cartItemList.get(position).getFlowerExtraPrice()));

        holder.numberButton.setNumber(String.valueOf(cartItemList.get(position).getFlowerQuantity()));

        if(cartItemList.get(position).getFlowerAddon()!=null){
            if(cartItemList.get(position).getFlowerAddon().equals("Default")){
                holder.txt_flower_addon.setText(new StringBuilder("Addon: ").append("Default"));
            }
            else {
                List<AddonModel> addonModels=gson.fromJson(cartItemList.get(position).getFlowerAddon(),new TypeToken<List<AddonModel>>(){}.getType());
                holder.txt_flower_addon.setText(new StringBuilder("Addon: ").append(Common.getListAddon(addonModels)));
            }

        }


        //Event
        holder.numberButton.setOnValueChangeListener((view,oldValue,newValue)->{
            //When user click this button, we will update database
            cartItemList.get(position).setFlowerQuantity(newValue);
            EventBus.getDefault().postSticky(new UpdateItemInCart(cartItemList.get(position)));

        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public CartItem getItemAtPosition(int pos) {
        return cartItemList.get(pos);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private Unbinder unbinder;
        @BindView(R.id.img_cart)
        ImageView img_cart;
        @BindView(R.id.txt_flower_price)
        TextView txt_flower_price;
        @BindView(R.id.txt_flower_name)
        TextView txt_flower_name;
        @BindView(R.id.txt_flower_addon)
        TextView txt_flower_addon;
        @BindView(R.id.number_button)
        ElegantNumberButton numberButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}

