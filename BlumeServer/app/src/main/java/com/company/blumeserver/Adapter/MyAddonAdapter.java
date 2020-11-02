package com.company.blumeserver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.blumeserver.Callback.IRecyclerClickListener;
import com.company.blumeserver.EventBus.SelectAddonModel;
import com.company.blumeserver.EventBus.UpdateAddonModel;
import com.company.blumeserver.Model.AddonModel;
import com.company.blumeserver.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyAddonAdapter extends RecyclerView.Adapter<MyAddonAdapter.MyViewHolder> {

    Context context;
    List<AddonModel> addonModels;
    UpdateAddonModel updateAddonModel;
    int edtPos;

    public MyAddonAdapter(Context context, List<AddonModel> addOnModels) {
        this.context = context;
        this.addonModels = addOnModels;
        edtPos = -1;
        updateAddonModel = new UpdateAddonModel();

    }

    @NonNull
    @Override
    public MyAddonAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyAddonAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_addon_display, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAddonAdapter.MyViewHolder holder, int position) {
        holder.txt_name.setText(addonModels.get(position).getName());
        holder.txt_price.setText(String.valueOf(addonModels.get(position).getPrice()));

        //Event
        holder.img_delete.setOnClickListener(view -> {
            addonModels.remove(position);
            notifyItemRemoved(position);
            updateAddonModel.setAddonModels(addonModels); //Set for event
            EventBus.getDefault().postSticky(updateAddonModel); //Send event
        });

        holder.setListener((view, pos) -> {
            edtPos = position;
            EventBus.getDefault().postSticky(new SelectAddonModel(addonModels.get(pos)));
        });
    }

    @Override
    public int getItemCount() {
        return addonModels.size();
    }
    public void addNewAddon(AddonModel addOnModel) {
        addonModels.add(addOnModel);
        notifyItemInserted(addonModels.size()-1);
        updateAddonModel.setAddonModels(addonModels);
        EventBus.getDefault().postSticky(updateAddonModel);
    }
    public void editAddon(AddonModel addOnModel){
        if(edtPos!=-1){
            addonModels.set(edtPos,addOnModel);
            notifyItemChanged(edtPos);
            edtPos=-1; //reset variable after success
            //Send update
            updateAddonModel.setAddonModels(addonModels);
            EventBus.getDefault().postSticky(updateAddonModel);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.txt_name)
        TextView txt_name;
        @BindView(R.id.txt_price)
        TextView txt_price;
        @BindView(R.id.img_delete)
        ImageView img_delete;

        Unbinder unbinder;
        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(view->listener.onItemClickListener(view,getAdapterPosition()));
        }
    }
}