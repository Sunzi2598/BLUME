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
import com.company.blumeserver.EventBus.CategoryClick;
import com.company.blumeserver.Model.BestDealModel;
import com.company.blumeserver.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyBestDealsAdapter extends RecyclerView.Adapter<MyBestDealsAdapter.MyViewHolder>{


    Context context;
    List<BestDealModel> bestDealModels;

    public MyBestDealsAdapter(Context context, List<BestDealModel> bestDealModels) {
        this.context = context;
        this.bestDealModels = bestDealModels;
    }

    @NonNull
    @Override
    public MyBestDealsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyBestDealsAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_category_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyBestDealsAdapter.MyViewHolder holder, int position) {
        Glide.with(context).load(bestDealModels.get(position).getImage())
                .into(holder.category_image);
        holder.category_name.setText(new StringBuilder(bestDealModels.get(position).getName()));
        //Event
        holder.setListener((view, pos)->{

        });
    }

    @Override
    public int getItemCount() {
        return bestDealModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;
        @BindView(R.id.img_category)
        ImageView category_image;
        @BindView(R.id.txt_category)
        TextView category_name;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView){
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
