package com.eddy.mbta.ui.stations;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;
import com.eddy.mbta.utils.Utility;

import java.util.List;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.ViewHolder>{

    private Context mContext;
    private List<Integer> mTrainList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView iconView;
        TextView nameView;
        Button startView, endView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            iconView = view.findViewById(R.id.route_image);
            nameView = view.findViewById(R.id.train_name);
            startView = view.findViewById(R.id.start_station);
            endView = view.findViewById(R.id.end_station);
        }
    }

    public TrainAdapter(List<Integer> trainList) {
        mTrainList = trainList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_train_line, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

                Intent intent = new Intent(mContext, DetailStationActivity.class);
                intent.putExtra("train", Utility.route_id[position]);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int num = mTrainList.get(position);
        holder.nameView.setText(Utility.route_id[num]);
        holder.startView.setText(Utility.start[num]);
        holder.endView.setText(Utility.end[num]);
        holder.iconView.setImageResource(Utility.icon[num]);
    }

    @Override
    public int getItemCount() {
        return mTrainList.size();
    }
}

