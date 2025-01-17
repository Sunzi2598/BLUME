package com.company.blumeSunzi.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.blumeSunzi.Callback.IRecyclerClickListener;
import com.company.blumeSunzi.Common.Common;
import com.company.blumeSunzi.Database.CartItem;
import com.company.blumeSunzi.Model.OrderModel;
import com.company.blumeSunzi.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {

    private Context context;
    private List<OrderModel> orderList;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    public MyOrdersAdapter(Context context, List<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
        calendar = Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    public OrderModel getItemAtPosition(int pos){
        return orderList.get(pos);
    }
    public void SetItemAtPosition(int pos, OrderModel item){
        orderList.set(pos,item);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_order_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(orderList.get(position).getCartItemList().get(0).getFlowerImage())
                .into(holder.img_order); //Load default image in cart
        calendar.setTimeInMillis(orderList.get(position).getCreateDate());
        Date date=new Date(orderList.get(position).getCreateDate());
        holder.txt_order_date.setText(new StringBuilder(Common.getDateOfWeek(calendar.get(Calendar.DAY_OF_WEEK)))
                .append(" ")
                .append(simpleDateFormat.format(date)));
        holder.txt_order_number.setText(new StringBuilder("Order No: ").append(orderList.get(position).getOrderNumber()));
        holder.txt_order_comment.setText(new StringBuilder("Comment: ").append(orderList.get(position).getComment()));
        holder.txt_order_status.setText(new StringBuilder("Status: ").append(Common.convertStatusToText(orderList.get(position).getOrderStatus())));

        holder.setRecyclerClickListener((view, pos) -> {
            showDialog(orderList.get(pos).getCartItemList());
        });
    }

    private void showDialog(List<CartItem> cartItemList) {
        View layout_dialog=LayoutInflater.from(context).inflate(R.layout.layout_dialog_order_detail,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout_dialog);

        Button btn_ok=(Button)layout_dialog.findViewById(R.id.btn_ok);
        RecyclerView recycler_order_detail=(RecyclerView)layout_dialog.findViewById(R.id.recycler_order_detail);
        recycler_order_detail.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(context);
        recycler_order_detail.setLayoutManager(layoutManager);
        recycler_order_detail.addItemDecoration(new DividerItemDecoration(context, layoutManager.getOrientation()));
        MyOrderDetailAdapter myOrderDetailAdapter=new MyOrderDetailAdapter(context,cartItemList);
        recycler_order_detail.setAdapter(myOrderDetailAdapter);

        //Create Dialog
        AlertDialog dialog=builder.create();
        dialog.show();
        //Custom Dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        btn_ok.setOnClickListener(view-> dialog.dismiss());

    }
    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_order_status)
        TextView txt_order_status;
        @BindView(R.id.txt_order_comment)
        TextView txt_order_comment;
        @BindView(R.id.txt_order_number)
        TextView txt_order_number;
        @BindView(R.id.txt_order_date)
        TextView txt_order_date;
        @BindView(R.id.img_order)
        ImageView img_order;


        IRecyclerClickListener iRecyclerClickListener;

        public void setRecyclerClickListener(IRecyclerClickListener listener) {
            this.iRecyclerClickListener = listener;
        }

        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerClickListener.onItemClickListener(view, getAdapterPosition());
        }
    }

    }
