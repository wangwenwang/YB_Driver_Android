package com.example.administrator.ybdriver.adapter;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.bean.Order;
import com.example.administrator.ybdriver.ui.activity.OrderDetailActivity;
import com.example.administrator.ybdriver.ui.activity.OrderPayActivity;
import com.example.administrator.ybdriver.ui.fragment.UnpayedFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/20.
 */
public class UnpayedOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM =0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //底部FootView
    public static final int LOADING_MORE=10; //上拉加载更多
    public static final int NO_MORE=11; //无更多数据
    public int loadState=10;//上拉加载状态值
    //private final int unpayed_Request_Code=1000;
    ArrayList<Order> morderlist;
    UnpayedFragment unpayedFragment;
    private MyViewHolder llbtnsVisibleHolder;
    public UnpayedOrdersAdapter(ArrayList<Order> morderlist, UnpayedFragment unpayedFragment) {
        this.morderlist = morderlist;
        this.unpayedFragment=unpayedFragment;
    }

    public ArrayList<Order> getMorderlist() {
        return morderlist;
    }



    public void setMorderlist(ArrayList<Order> morderlist) {
        this.morderlist = morderlist;
        UnpayedOrdersAdapter.this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position+1==getItemCount()){
            return TYPE_FOOTER;
        }else {
            return TYPE_ITEM;
        }
    }
    @Override
    public int getItemCount() {
        return morderlist.size()+1;//预加上底部FootView
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_FOOTER){
            View view= LayoutInflater.from(unpayedFragment.getMContext()).inflate(R.layout.more_item_orderlist,parent,false);
            MyFootViewHolder myfootViewHolder=new MyFootViewHolder(view);
            return myfootViewHolder;
        }else if (viewType==TYPE_ITEM){
            View itemview=LayoutInflater.from(unpayedFragment.getMContext()).inflate(R.layout.order_item_cardview,parent,false);
            MyViewHolder myViewHolder=new MyViewHolder(itemview);
            return myViewHolder;
        }

        return null;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof MyViewHolder){
            ((MyViewHolder) holder).ll_buttons.setVisibility(View.GONE);

//            if (((MyViewHolder) holder).tv_order_no.getText().equals(morderlist.get(0).getORD_NO())){
//                UnpayedOrdersAdapter.this.loadState=UnpayedOrdersAdapter.LOADING_MORE;
//            }
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder){
            final Order order=morderlist.get(position);
            ((MyViewHolder) holder).tv_order_no.setText(order.getORD_NO());
            ((MyViewHolder) holder).tv_shipment_no.setText(order.getTMS_FLLET_NAME());//yb 承运商名称
            ((MyViewHolder) holder).tv_date_issue.setText(order.getTMS_DATE_ISSUE());
            ((MyViewHolder) holder).tv_order_fromName.setText(order.getTMS_DRIVER_TEL());//yb 司机电话
            ((MyViewHolder) holder).tv_driver_name.setText(order.getTMS_DRIVER_NAME());
            ((MyViewHolder) holder).tv_order_toName.setText(order.getORD_TO_NAME());
            ((MyViewHolder) holder).tv_order_toAddress.setText(order.getORD_TO_ADDRESS());
            ((MyViewHolder) holder).tv_ord_issue_qty.setText(order.getORD_QTY());
            ((MyViewHolder) holder).tv_order_workFlow.setText(order.getORD_WEIGHT());//yb 货物总重
            ((MyViewHolder) holder).tv_driver_pay.setText(order.getDRIVER_PAY());
            ((MyViewHolder) holder).btn_order_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(unpayedFragment.getMContext(), OrderDetailActivity.class);
                    intent.putExtra("order_id", order.getORD_IDX());
               //     intent.putExtra("ispayed",false);
                    unpayedFragment.startActivity(intent);

                }
            });
            ((MyViewHolder) holder).btn_driver_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(unpayedFragment.getMContext(), OrderPayActivity.class);
                    intent.putExtra("order_id",order.getORD_IDX());
                    intent.putExtra("order_driver_pay",order.getDRIVER_PAY());
                    unpayedFragment.startActivity(intent);
                }
            });

            ((MyViewHolder) holder).mcardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MyViewHolder) holder).ll_buttons.getVisibility()==View.GONE) {
                       if (llbtnsVisibleHolder!=null){
                           llbtnsVisibleHolder.ll_buttons.setVisibility(View.GONE);
                       }
                        ((MyViewHolder) holder).ll_buttons.setVisibility(View.VISIBLE);
                        llbtnsVisibleHolder= (MyViewHolder) holder;
                    }else {
                        ((MyViewHolder) holder).ll_buttons.setVisibility(View.GONE);
                    }

                }
            });

        }else if (holder instanceof MyFootViewHolder){
            switch (loadState){
                case LOADING_MORE:  ((MyFootViewHolder) holder).moreProgressbar.setVisibility(View.VISIBLE);
                        ((MyFootViewHolder) holder).moreTextview.setText("加载更多订单...");
                                    break;
                case NO_MORE: ((MyFootViewHolder) holder).moreProgressbar.setVisibility(View.GONE);
                            ((MyFootViewHolder) holder).moreTextview.setText("无更多数据");
            }

        }
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        CardView mcardView;
        public LinearLayout ll_buttons;
        //订单号，装运编号，出库时间，起运点名称，司机名，到达点名称，到达点地址，订单货物总数，订单流程，订单交付状态
        TextView tv_order_no,tv_shipment_no,tv_date_issue,tv_order_fromName,tv_driver_name,tv_order_toName,tv_order_toAddress,
                tv_ord_issue_qty,tv_order_workFlow,tv_driver_pay;
        Button btn_order_detail,btn_driver_pay;//查看订单详情，司机交付订单

        public MyViewHolder(View itemView) {
            super(itemView);
            mcardView= (CardView) itemView.findViewById(R.id.cardView_order);
            ll_buttons= (LinearLayout) itemView.findViewById(R.id.ll_btns);
            tv_order_no= (TextView) itemView.findViewById(R.id.tv_tms_order_no);
            tv_shipment_no= (TextView) itemView.findViewById(R.id.tv_tms_shipment_no);
            tv_date_issue= (TextView) itemView.findViewById(R.id.tv_tms_date_issue);
            tv_order_fromName= (TextView) itemView.findViewById(R.id.tv_tms_order_fromName);
            tv_driver_name= (TextView) itemView.findViewById(R.id.tv_tms_driver_name);
            tv_order_toName= (TextView) itemView.findViewById(R.id.tv_tms_order_toName);
            tv_order_toAddress= (TextView) itemView.findViewById(R.id.tv_order_toAddress);
            tv_ord_issue_qty= (TextView) itemView.findViewById(R.id.tv_ord_issue_qty);
            tv_order_workFlow= (TextView) itemView.findViewById(R.id.tv_order_workFlow);
            tv_driver_pay= (TextView) itemView.findViewById(R.id.tv_tms_driver_pay);
            btn_order_detail= (Button) itemView.findViewById(R.id.btn_order_detail);
            btn_driver_pay= (Button) itemView.findViewById(R.id.btn_driver_pay);
        }
    }
    class MyFootViewHolder extends RecyclerView.ViewHolder {
        ProgressBar moreProgressbar;
        TextView moreTextview;
        public MyFootViewHolder(View itemView) {
            super(itemView);
            moreProgressbar= (ProgressBar) itemView.findViewById(R.id.progressBar_more_item);
            moreTextview= (TextView) itemView.findViewById(R.id.tv_more_item);
        }
    }

}
