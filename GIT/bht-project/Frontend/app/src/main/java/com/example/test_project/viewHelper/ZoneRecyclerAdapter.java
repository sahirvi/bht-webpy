package com.example.test_project.viewHelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_project.R;
import com.example.test_project.model.ZoneModel;

import java.util.ArrayList;
import java.util.List;

public class ZoneRecyclerAdapter extends RecyclerView.Adapter<ZoneRecyclerAdapter.ZoneViewHolder> {

    private Context context;
    private List<ZoneModel> zones;
    private ItemClickListener mClickListener;
    Boolean auto;

    private static final String TAG = "ZoneRecyclerAdapter TAG";

    public ZoneRecyclerAdapter(Context context, ItemClickListener clickListener, Boolean auto){
        this.zones = new ArrayList<>();
        this.context = context;
        this.mClickListener = clickListener;
        this.auto = auto;

    }

    public void setData(List<ZoneModel> zones){
        this.zones = zones;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create new view which defines the UI of item
        context = parent.getContext();

        View view;

        if (auto){

            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dash_zone_list_item, parent, false);
            view.findViewById(R.id.dash_zone_item_container).setBackgroundResource(R.drawable.autowater);
        }
        else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dash_zone_list_item, parent, false);
            view.findViewById(R.id.dash_zone_item_container).setBackgroundResource(R.drawable.manualwater);
        }

//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.dash_zone_list_item, parent, false);

        return new ZoneViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneViewHolder holder, int position) {
        if (zones.get(position).getName().toString() != null){
            holder.zone_id.setText(zones.get(position).getName());
        }
        else{
            holder.zone_id.setText("Zone " + zones.get(position).getZoneId().toString());
        }

        if (zones.get(position).getZoneId().toString() != null){
            holder.zone_number.setText(zones.get(position).getZoneId() + "");
        }
        else{
            holder.zone_number.setText("");
        }

        if (auto){
            holder.dash_zone_item_container.setBackgroundResource(R.drawable.autowater);
        }
        else{
            holder.dash_zone_item_container.setBackgroundResource(R.drawable.manualwater);

        }


        switch(position){
            case 0:
                holder.card_pic.setImageResource(R.drawable.visual_zone_1);
                break;
            case 1:
                holder.card_pic.setImageResource(R.drawable.visual_zone_2);
                break;
            case 2:
                holder.card_pic.setImageResource(R.drawable.visual_zone_3);
                break;
            case 3:
                holder.card_pic.setImageResource(R.drawable.visual_zone_4);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return zones.size();
    }


    // Class ViewHolder
    public static class ZoneViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView zone_id;
        TextView zone_number;
        ImageView card_pic;
        LinearLayout dash_zone_item_container;
        ItemClickListener clickListener;

        public ZoneViewHolder(@NonNull View itemView, ItemClickListener clickListener) {
            super(itemView);

            zone_id = itemView.findViewById(R.id.card_zone_name);
            card_pic = itemView.findViewById(R.id.card_pic);
            dash_zone_item_container = itemView.findViewById(R.id.dash_zone_item_container);
            zone_number = itemView.findViewById(R.id.zone_number);

            this.clickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getBindingAdapterPosition());
        }
    }

    public interface ItemClickListener {
        public void onItemClick(int position);
    }
}