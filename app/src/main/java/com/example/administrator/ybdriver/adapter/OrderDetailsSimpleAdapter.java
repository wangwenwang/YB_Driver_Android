package com.example.administrator.ybdriver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.bean.OrderDetails;
import com.example.administrator.ybdriver.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 物流信息中的Adapter，内容比较简单
 */
public class OrderDetailsSimpleAdapter extends BaseAdapter {
    private List<OrderDetails> orderDetails;
    private Context mContext;

    public OrderDetailsSimpleAdapter(Context context) {
        this.orderDetails = new ArrayList<>();
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return orderDetails.size();
    }

    public void resetData(List<OrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return orderDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderDetails orderDetail = this.orderDetails.get(position);
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_order_detail_simple, null);
            holder = new Holder();
            holder.tv_productName = (TextView) convertView.findViewById(R.id.tv_productName);
            holder.tv_wasehouse_name= (TextView) convertView.findViewById(R.id.tv_wasehouse_name);
            holder.tv_order_qty = (TextView) convertView.findViewById(R.id.tv_order_qty);
            holder.tv_order_weight = (TextView) convertView.findViewById(R.id.tv_order_weight);
            holder.tv_order_volume = (TextView) convertView.findViewById(R.id.tv_order_volume);
            holder.tv_ORD_REQUEST_ISSUE= (TextView) convertView.findViewById(R.id.tv_ORD_REQUEST_ISSUE);
            holder.tv_order_district= (TextView) convertView.findViewById(R.id.tv_order_district);
            holder.tv_order_office= (TextView) convertView.findViewById(R.id.tv_order_office);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_ORD_REQUEST_ISSUE.setText(orderDetail.getORD_REQUEST_ISSUE());//yb 计划发运日期
        holder.tv_productName.setText(orderDetail.getPRODUCT_NAME() + " (" + orderDetail.getPRODUCT_NO() + ")");
        holder.tv_wasehouse_name.setText(com.kaidongyuan.app.basemodule.utils.nomalutils.StringUtils.strNoDataSet(orderDetail.getSHIP_FROM_NAME()));//yb 发货仓库
        holder.tv_order_qty.setText(orderDetail.getORD_QTY());
        holder.tv_order_weight.setText(orderDetail.getISSUE_QTY());
        holder.tv_order_volume.setText(com.kaidongyuan.app.basemodule.utils.nomalutils.StringUtils.getTargetField(orderDetail.getORD_TO_REGION(),"\\.",0) );//yb 销售区域
        holder.tv_order_district.setText(com.kaidongyuan.app.basemodule.utils.nomalutils.StringUtils.getTargetField(orderDetail.getORD_TO_REGION(),"\\.",1));//yb 地区
        holder.tv_order_office.setText(com.kaidongyuan.app.basemodule.utils.nomalutils.StringUtils.getTargetField(orderDetail.getORD_TO_REGION(),"\\.",2));//yb 办事处
        return convertView;
    }

    class Holder {
        TextView tv_productName,tv_wasehouse_name, tv_order_qty, tv_order_weight, tv_order_volume,tv_ORD_REQUEST_ISSUE,tv_order_district,tv_order_office;
    }


}
