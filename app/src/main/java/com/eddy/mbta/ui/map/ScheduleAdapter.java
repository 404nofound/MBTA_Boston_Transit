package com.eddy.mbta.ui.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;
import com.eddy.mbta.json.AlertBean;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder>{

    private static final String TAG = "ScheduleAdapter";

    private Context mContext;

    private List<AlertBean.DataBean> mAlertList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView stationName, directionInfo, scheduleTime;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            stationName = (TextView) view.findViewById(R.id.station_name);
            directionInfo = (TextView) view.findViewById(R.id.direction_info);
            scheduleTime = (TextView) view.findViewById(R.id.schedule_time);
        }
    }

    public ScheduleAdapter(List<AlertBean.DataBean> alertList) {
        mAlertList = alertList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.schedule_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                /*AlertBean fruit = mAlertList.get(position);
                Intent intent = new Intent(mContext, FruitActivity.class);
                intent.putExtra(FruitActivity.FRUIT_NAME, fruit.getName());
                intent.putExtra(FruitActivity.FRUIT_IMAGE_ID, fruit.getImageId());
                mContext.startActivity(intent);*/
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AlertBean.DataBean alert = mAlertList.get(position);
        holder.stationName.setText(alert.getAttributes().getService_effect());
        holder.directionInfo.setText(alert.getAttributes().getUpdated_at().substring(0, 10));
        holder.scheduleTime.setText(alert.getAttributes().getLifecycle());
        //holder.alertDesc.setText(alert.getAttributes().getHeader());
        //Glide.with(mContext).load(alert.getImageId()).into(holder.fruitImage);
    }

    @Override
    public int getItemCount() {
        return mAlertList.size();
    }

}


