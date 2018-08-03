package com.example.administrator.ybdriver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.bean.Notify;
import com.kaidongyuan.app.basemodule.utils.nomalutils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知列表adapter
 */
public class NotifyAdapter extends BaseAdapter {

	private Context mContext;
	List<Notify> mNotifyList;

	public NotifyAdapter(Context mContext) {
		this.mContext = mContext;
		this.mNotifyList = new ArrayList<>();
	}

	public void setData(List<Notify> notifyList){
		this.mNotifyList = notifyList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mNotifyList.size();
	}

	@Override
	public Object getItem(int position) {
		return mNotifyList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final Notify notify = mNotifyList.get(position);
		if (convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_notify, null);
			holder = new ViewHolder();
			holder.iv_notify = (ImageView) convertView.findViewById(R.id.iv_notify);
			holder.tv_notify_title = (TextView) convertView.findViewById(R.id.tv_notify_title);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_notify_title.setText("你有新的订单消息，订单号："+notify.getITitle());
		Glide.with(mContext).load("http://china56pd.com:8090/uploadfile/" + notify.getIImage())
				.error(R.drawable.ic_hot_normal)
				.override(DensityUtil.dip2px(40),DensityUtil.dip2px(40))
				.diskCacheStrategy(DiskCacheStrategy.NONE)
				.crossFade().centerCrop().into(holder.iv_notify);
		return convertView;
	}
	class ViewHolder{
		TextView tv_notify_title;
		ImageView iv_notify;
	}
}
