package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class AdapterStdViewAtt extends ArrayAdapter {
    private ArrayList dataSet;
    Context mContext;
    // View lookup cache
    private static class ViewHolder {
        TextView CourseName;
        TextView Percent;
        TextView ClsDone;
        TextView ClsAttend;
    }
    public AdapterStdViewAtt(ArrayList data, Context context) {
        super(context, R.layout.view_att_node, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }
    @Override
    public StdViewAttData getItem(int position) {
        return (StdViewAttData) dataSet.get(position);
    }
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        AdapterStdViewAtt.ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new AdapterStdViewAtt.ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_att_node, parent, false);
            viewHolder.CourseName = (TextView) convertView.findViewById(R.id.std_name);
            viewHolder.Percent = (TextView) convertView.findViewById(R.id.attendance_percentage);
            viewHolder.ClsDone = (TextView) convertView.findViewById(R.id.classes_done);
            viewHolder.ClsAttend = (TextView) convertView.findViewById(R.id.classes_attended);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AdapterStdViewAtt.ViewHolder) convertView.getTag();
            result=convertView;
        }
        StdViewAttData item = getItem(position);
        viewHolder.CourseName.setText(item.CourseName);
        viewHolder.Percent.setText(item.percent + "%");
        viewHolder.ClsDone.setText("Classes Done = " + item.clsdone);
        viewHolder.ClsAttend.setText("Classes Attended = " + item.clsattend);
        return result;
    }
}
