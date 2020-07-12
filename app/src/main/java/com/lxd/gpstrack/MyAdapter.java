package com.lxd.gpstrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lxd.gpstrack.Entiy.GouPlan;

import java.util.LinkedList;

public class MyAdapter extends BaseAdapter {
    private Context mContext;
    private LinkedList<GouPlan> mGouPlan;

    public MyAdapter() {
    }

    public MyAdapter(Context mContext, LinkedList<GouPlan> mGouPlan) {
        this.mContext = mContext;
        this.mGouPlan = mGouPlan;
    }

    @Override
    public int getCount() {
        return mGouPlan.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list,parent,false);
            holder = new ViewHolder();
            holder.xuhao = convertView.findViewById(R.id.xuhao);
            holder.gudao = convertView.findViewById(R.id.gudao);
            holder.guacheshu = convertView.findViewById(R.id.guacheshu);
            holder.shuaicheshu = convertView.findViewById(R.id.shuaicheshu);
            holder.jishi = convertView.findViewById(R.id.jishi);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.xuhao.setText(mGouPlan.get(position).getXuhao());
        holder.gudao.setText(mGouPlan.get(position).getGudao());
        holder.guacheshu.setText(mGouPlan.get(position).getGuacheshu());
        holder.shuaicheshu.setText(mGouPlan.get(position).getShuaicheshu());
        holder.jishi.setText(mGouPlan.get(position).getJishi());
        return convertView;
    }

    /**
     * 添加一条记录
     * @param gouPlan
     */
    public void add(GouPlan gouPlan){
        if (mGouPlan == null){
            mGouPlan = new LinkedList<>();
        }
        mGouPlan.add(gouPlan);
        notifyDataSetChanged();
    }

    /**
     * 删除一条记录
     * @param gouPlan 要删除的记录
     */
    public void remove(GouPlan gouPlan){
        if (mGouPlan != null){
            mGouPlan.remove(gouPlan);
        }
        notifyDataSetChanged();
    }

    /**
     * 删除所有记录
     */
    public void clear(){
        if (mGouPlan != null){
            mGouPlan.clear();
        }
        notifyDataSetChanged();
    }

    private class ViewHolder{
        TextView xuhao;
        TextView gudao;
        TextView guacheshu;
        TextView shuaicheshu;
        TextView jishi;

    }
}
