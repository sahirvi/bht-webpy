package com.example.test_project.viewHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_project.R;
import com.example.test_project.model.ZoneModel;

import java.util.ArrayList;
import java.util.List;

public class DryZoneRecyclerAdapter extends RecyclerView.Adapter<DryZoneRecyclerAdapter.DryZoneViewHolder> {

    private Context context;
    private List<ZoneModel> zones;
    private ItemClickListener mClickListener;

    private static final String TAG = "ZoneRecyclerAdapter TAG";


    public DryZoneRecyclerAdapter(Context context, ItemClickListener clickListener){
        this.zones = new ArrayList<>();
        this.context = context;
        this.mClickListener = clickListener;

    }

    public void setData(List<ZoneModel> zones){
        this.zones = zones;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DryZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create new view which defines the UI of item
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dash_dry_zones_item, parent, false);

        return new DryZoneViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DryZoneViewHolder holder, int position) {

        holder.zone_id.setText("Gieß Zone " + zones.get(position).getZoneId().toString());

    }

    @Override
    public int getItemCount() {
        return zones.size();
    }


    // Class ViewHolder
    public static class DryZoneViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView zone_id;
        ItemClickListener clickListener;

        public DryZoneViewHolder(@NonNull View itemView, ItemClickListener clickListener) {
            super(itemView);

            zone_id = itemView.findViewById(R.id.dash_warn_zone);
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