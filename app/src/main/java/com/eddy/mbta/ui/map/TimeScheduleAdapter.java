package com.eddy.mbta.ui.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;
import com.eddy.mbta.json.Schedule;

import java.util.List;

public class TimeScheduleAdapter extends RecyclerView.Adapter<TimeScheduleAdapter.ViewHolder>{

    private static final String TAG = "TimeScheduleAdapter";

    private Context mContext;

    private List<Schedule> mScheduleList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView routeImage;
        TextView routeName, time;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            routeImage = view.findViewById(R.id.route_image);
            routeName = view.findViewById(R.id.route_name);
            time = view.findViewById(R.id.time);

        }
    }

    public TimeScheduleAdapter(List<Schedule> scheduleList) {
        mScheduleList = scheduleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.schedule_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Schedule schedule = mScheduleList.get(position);
        holder.routeImage.setImageResource(schedule.getIcon());
        holder.routeName.setText(schedule.getRoute_id());
        holder.time.setText(schedule.getArrTime());

    }

    @Override
    public int getItemCount() {
        return mScheduleList.size();
    }
}


