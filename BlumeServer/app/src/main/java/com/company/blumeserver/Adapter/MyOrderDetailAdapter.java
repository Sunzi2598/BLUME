package com.company.blumeserver.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.blumeserver.Callback.IRecyclerClickListener;
import com.company.blumeserver.Common.Common;
import com.company.blumeserver.Model.AddonModel;
import com.company.blumeserver.Model.CartItem;
import com.company.blumeserver.Model.OrderModel;
import com.company.blumeserver.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrderDetailAdapter extends RecyclerView.Adapter<MyOrderDetailAdapter.MyViewHolder> {

    Context context;
    List<CartItem> cartItemList;
    Gson gson;

    public MyOrderDetailAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        gson=new Gson();
    }

    @NonNull
    @Override
    public MyOrderDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyOrderDetailAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_order_detail_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context)
                .load(cartItemList.get(position).getFlowerImage())
                .into(holder.img_flower_image);
        holder.txt_flower_quantity.setText(new StringBuilder("Quantity: ").append(cartItemList.get(position).getFlowerQuantity()));
        holder.txt_flower_name.setText(new StringBuilder().append(cartItemList.get(position).getFlowerName()));
        if(!cartItemList.get(position).getFlowerAddon().equals("Default")){
            List<AddonModel> addonModels=gson.fromJson(cartItemList.get(position).getFlowerAddon(),new TypeToken<List<AddonModel>>(){}.getType());
            StringBuilder addonString=new StringBuilder();
            if(addonModels!=null){
                for(AddonModel addonModel:addonModels){
                    addonString.append(addonModel.getName()).append(",");
                }
                addonString.delete(addonString.length()-1,addonString.length());//Remove last "," character
                holder.txt_flower_addon.setText(new StringBuilder("Addon: ").append(addonString));
            }
            else holder.txt_flower_addon.setText(new StringBuilder("Addon: Default"));
        }
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_flower_name)
        TextView txt_flower_name;
        @BindView(R.id.txt_flower_addon)
        TextView txt_flower_addon;
        @BindView(R.id.txt_flower_quantity)
        TextView txt_flower_quantity;
        @BindView(R.id.img_flower_image)
        ImageView img_flower_image;



        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
