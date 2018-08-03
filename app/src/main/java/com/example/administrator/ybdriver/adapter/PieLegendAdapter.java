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
import com.example.administrator.ybdriver.ui.widget.IntegerValueFormatter;
import com.github.mikephil.charting.data.PieEntry;
import com.kaidongyuan.app.basemodule.utils.nomalutils.DensityUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 圆饼图例列表adapter
 */
public class PieLegendAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<PieEntry> xPieVals;
	private List<Integer> colors;

	public PieLegendAdapter(Context mContext,List<Integer> colors) {
		this.mContext = mContext;
		xPieVals=new ArrayList<>();
		this.colors =new ArrayList<>();
		this.colors.addAll(colors);
	}

	/**
	 *
	 * @param xPieVals 圆饼数据原
	 * @param isOrder 数据展示顺序
     */
	public void setData(ArrayList<PieEntry> xPieVals,boolean isOrder){
		this.xPieVals.clear();
		this.xPieVals.addAll(xPieVals);
		if (!isOrder){
			Collections.reverse(this.xPieVals);//反转指定List集合中元素的顺序
			Collections.reverse(this.colors);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return xPieVals.size();
	}

	@Override
	public Object getItem(int position) {
		return xPieVals.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final PieEntry pieEntry = xPieVals.get(position);
		if (convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pie_legend, null);
			holder = new ViewHolder();
			holder.iv_pie_legend = (ImageView) convertView.findViewById(R.id.iv_pie_legend);
			holder.tv_pie_legend = (TextView) convertView.findViewById(R.id.tv_pie_legend);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_pie_legend.setText(pieEntry.getLabel());

		holder.iv_pie_legend.setBackgroundColor(colors.get(position%colors.size()));
		return convertView;
	}
	class ViewHolder{
		TextView tv_pie_legend;
		ImageView iv_pie_legend;
	}
}
