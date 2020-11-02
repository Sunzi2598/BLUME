package com.company.blumeserver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.blumeserver.Callback.IRecyclerClickListener;
import com.company.blumeserver.Common.Common;
import com.company.blumeserver.Model.FlowerModel;
import com.company.blumeserver.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyFlowerListAdapter extends RecyclerView.Adapter<MyFlowerListAdapter.MyViewHolder> {

    private Context context;
    private List<FlowerModel> flowerModelList;

    public MyFlowerListAdapter(Context context, List<FlowerModel> flowerModelList) {
        this.context = context;
        this.flowerModelList = flowerModelList;
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
    public FlowerModel getItemAtPosition(int pos){
        return flowerModelList.get(pos);
    }
}