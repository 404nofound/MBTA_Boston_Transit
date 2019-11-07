package com.eddy.mbta.ui.map;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.MyApplication;
import com.eddy.mbta.R;
import com.eddy.mbta.json.NearbyStationBean;
import com.eddy.mbta.service.TimeScheduleService;

import java.util.List;

public class NearbyStationAdapter extends RecyclerView.Adapter<NearbyStationAdapter.ViewHolder>{

    private List<NearbyStationBean.IncludedBean> mNearbyStationList;

    private Context mContext;
    private Window mWindow;
    private View mRoot;

    private Listener listener;

    interface Listener {
        abstract void onClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView directionButton, wheelchair;
        TextView stationName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            stationName = view.findViewById(R.id.station_name);
            directionButton = view.findViewById(R.id.direction);
            wheelchair = view.findViewById(R.id.wheelchair);
        }
    }

    public NearbyStationAdapter(List<NearbyStationBean.IncludedBean> nearbyStationList, Window window, View root) {
        mNearbyStationList = nearbyStationList;
        mWindow = window;
        mRoot = root;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_nearby_station, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

                if (position == -1) return;

                listener.onClick(position);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

                if (position == -1) return;

                NearbyStationBean.IncludedBean station = mNearbyStationList.get(position);

                if (MyApplication.NET_STATUS == -1) {
                    Toast.makeText(MyApplication.getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }

                SchedulePopWindow PopWin = new SchedulePopWindow(mContext, station.getAttributes().getName(), station.getId());

                PopWin.showAtLocation(mRoot.findViewById(R.id.layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                final WindowManager.LayoutParams params = mWindow.getAttributes();

                params.alpha = 0.7f;
                mWindow.setAttributes(params);

                PopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                        if(SchedulePopWindow.handler != null){
                            SchedulePopWindow.handler.removeCallbacksAndMessages(null);
                        }

                        Intent stopIntent = new Intent(MyApplication.getContext(), TimeScheduleService.class);
                        MyApplication.getContext().stopService(stopIntent);

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

        NearbyStationBean.IncludedBean station = mNearbyStationList.get(position);
        holder.stationName.setText(station.getAttributes().getName());

        int wheel = station.getAttributes().getWheelchair_boarding();

        if (wheel == 1) {
            holder.wheelchair.setImageResource(R.drawable.ic_wheelchair_access);
        } else {
            holder.wheelchair.setImageResource(R.drawable.ic_wheelchair_disable);
        }
    }

    @Override
    public int getItemCount() {
        return mNearbyStationList.size();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}


