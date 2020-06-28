package com.mahc.custombottomsheet.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahc.custombottomsheet.R;
import com.mahc.custombottomsheet.Class.RouteList1;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> {


    private ArrayList<RouteList1> route_list_detail;
    Context context;

    //아이템 뷰를 저장하는 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView route_number;
        TextView route_detail;
        TextView route_dd;

        OnItemClickListener listener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.route_number=itemView.findViewById(R.id.route_number);
            this.route_detail=itemView.findViewById(R.id.route_detail);
            this.route_dd=itemView.findViewById(R.id.route_dd);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("recylerview","click");
                    int position=getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });

        }


        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }


    OnItemClickListener listener;
    public static interface OnItemClickListener {
        public void onItemClick(ViewHolder holder, View view, int position);
    }



    public RouteAdapter(ArrayList<RouteList1> route_list_detail){
        this.route_list_detail=route_list_detail;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        //inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.route_item, parent, false) ;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //값들을 넣어줌.. 연결성을 지어줌..

        holder.route_number.setText(route_list_detail.get(position).getRoute_number());
        holder.route_detail.setText(route_list_detail.get(position).getRoute_detail());
        holder.route_dd.setText(route_list_detail.get(position).getRoute_dd());
        holder.setOnItemClickListener(listener);

        Log.i("holder"+position,holder.route_detail.getText()+"");


    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return (null != route_list_detail? route_list_detail.size():0);
    }


}
