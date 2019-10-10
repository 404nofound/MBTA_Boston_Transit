package com.eddy.mbta.ui.stations;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;
import com.eddy.mbta.db.Station;
import com.eddy.mbta.ui.map.SchedulePopWindow;

import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder>{

    private static final String TAG = "StationAdapter";

    private Context mContext;
    private Window mWindow;

    private List<Station> mStationList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView wheelView;
        TextView nameView, addressView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            wheelView = view.findViewById(R.id.wheel_access);
            nameView = view.findViewById(R.id.station_name);
            addressView = view.findViewById(R.id.station_address);
        }
    }

    public StationAdapter(List<Station> stationList, Window window) {
        mStationList = stationList;
        mWindow = window;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_station, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Station station = mStationList.get(position);

                SchedulePopWindow PopWin = new SchedulePopWindow(mContext, station.getStationName(), station.getAlias());

                PopWin.showAtLocation(mWindow.getDecorView().findViewById(R.id.layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                final WindowManager.LayoutParams params = mWindow.getAttributes();

                params.alpha = 0.7f;
                mWindow.setAttributes(params);

                PopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        params.alpha = 1f;
                        mWindow.setAttributes(params);
                    }
                });

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Station station = mStationList.get(position);

        holder.nameView.setText(station.getStationName());
        holder.addressView.setText(station.getAddress());
        if (station.getWheelchair() == 1) {
            holder.wheelView.setImageResource(R.drawable.ic_wheelchair_access);
        } else {
            holder.wheelView.setImageResource(R.drawable.ic_wheelchair_disable);
        }
    }

    @Override
    public int getItemCount() {
        return mStationList.size();
    }
}

