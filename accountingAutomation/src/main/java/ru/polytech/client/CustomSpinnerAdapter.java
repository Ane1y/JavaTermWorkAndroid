package ru.polytech.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomSpinnerAdapter<T> extends ArrayAdapter<T> {

    private final Context mContext;
    private List<T> list;

    public CustomSpinnerAdapter(Context context, int textViewResourceId) {

        super(context, textViewResourceId);
        this.mContext = context;
        this.list = new ArrayList<>();
    }

    @Override
    public void addAll(@NonNull Collection<? extends T> collection) {
        list = (List<T>) collection;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int index) {
        return list.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") 
		View view =  View.inflate(mContext, R.layout.department_row, null);
        TextView name = view.findViewById(R.id.TVDepartmentSpinnerRowName);

        name.setText(list.get(position).toString());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder") 
		View view =  View.inflate(mContext, R.layout.department_row, null);
        TextView name = view.findViewById(R.id.TVDepartmentSpinnerRowName);

        name.setText(list.get(position).toString());
        return view;
    }

    public List<T> getList() {
        return list;
    }
}