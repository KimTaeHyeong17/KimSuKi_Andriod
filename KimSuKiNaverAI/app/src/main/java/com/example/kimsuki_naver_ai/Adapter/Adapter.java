package com.example.kimsuki_naver_ai.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kimsuki_naver_ai.Model.AudioModel;
import com.example.kimsuki_naver_ai.R;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {

    private ArrayList<AudioModel> arrayList = new ArrayList<>();
    private Activity activity;
    private LayoutInflater myInflater;
    private int type;

    public Adapter(Activity act, ArrayList<AudioModel> arrayList) {
        this.arrayList = arrayList;
        this.activity = act;
        myInflater  = (LayoutInflater) act.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        this.type = type;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder v;
        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.listview_item, null);
            v = new ViewHolder();
            v.tv_name = convertView.findViewById(R.id.tv_name);
            v.tv_date = convertView.findViewById(R.id.tv_date);
            v.tv_tag = convertView.findViewById(R.id.tv_tag);

            convertView.setTag(v);
        }else{
            v = (ViewHolder) convertView.getTag();
        }

        String item = arrayList.get(position).getCreatedAt();
        v.tv_name.setText(item);



        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);

    }

    public void filterList(ArrayList<AudioModel> filteredList) {
        arrayList = filteredList;
        notifyDataSetChanged();
    }

    public ArrayList<AudioModel> getFilteredArray(){
        return arrayList;
    }
    public static class ViewHolder
    {
        public TextView tv_name;
        public TextView tv_date;
        public TextView tv_tag;


    }

}
