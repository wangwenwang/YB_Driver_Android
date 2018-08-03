package com.example.administrator.ybdriver.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.bean.Order;
import com.example.administrator.ybdriver.ui.activity.OrderTrackActivity;

import java.util.List;

/**
 * Created by Administrator on 2016/7/26.
 */
public class OrderChooseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mcontext;
    private List<Order>morderList;

    public OrderChooseAdapter(Context mcontext, List<Order> morderList) {
        this.mcontext = mcontext;
        this.morderList = morderList;
    }

    @Override
    public int getItemCount() {
        return morderList.size();
    }

    public List<Order> getMorderList() {
        return morderList;
    }

    public void setMorderList(List<Order> morderList) {
        this.morderList = morderList;
        OrderChooseAdapter.this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.order_item_choose,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Order order=morderList.get(position);
        MyViewHolder myViewHolder= (MyViewHolder) holder;
        myViewHolder.tv_order_no.setText(order.getORD_NO());
        myViewHolder.tv_orderToName.setText(order.getORD_TO_NAME());
        myViewHolder.tv_order_issueDate.setText(order.getTMS_DATE_ISSUE());
        myViewHolder.ll_orderChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mcontext, OrderTrackActivity.class);
                intent.putExtra("order_IDX",order.getORD_IDX());
                mcontext.startActivity(intent);
            }
        });

    }
   public class MyViewHolder extends RecyclerView.ViewHolder{
       public LinearLayout ll_orderChoose;
        TextView tv_order_no,tv_orderToName,tv_order_issueDate;
        public MyViewHolder(View itemView) {
            super(itemView);
            ll_orderChoose= (LinearLayout) itemView.findViewById(R.id.ll_order_choose);
            tv_order_no= (TextView) itemView.findViewById(R.id.tv_orderIDX);
            tv_orderToName= (TextView) itemView.findViewById(R.id.tv_order_to_name);
            tv_order_issueDate= (TextView) itemView.findViewById(R.id.tv_order_issueDate);
        }
    }

}
