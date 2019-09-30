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
import com.eddy.mbta.json.TimeScheduleBean;

import java.util.List;

public class TimeScheduleAdapter extends RecyclerView.Adapter<TimeScheduleAdapter.ViewHolder>{

    private static final String TAG = "TimeScheduleAdapter";

    private Context mContext;

    private List<TimeScheduleBean.DataBeanXXXX> mScheduleList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView routeImage;
        TextView routeName, time;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            routeImage = (ImageView) view.findViewById(R.id.route_image);
            routeName = (TextView) view.findViewById(R.id.route_name);
            time = (TextView) view.findViewById(R.id.time);
        }
    }

    public TimeScheduleAdapter(List<TimeScheduleBean.DataBeanXXXX> scheduleList) {
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

        TimeScheduleBean.DataBeanXXXX schedule = mScheduleList.get(position);


        //holder.routeImage.setImageResource();

        String arrTimeData = schedule.getAttributes().getArrival_time();
        String depTimeData = schedule.getAttributes().getDeparture_time();

        String route_id = schedule.getRelationships().getRoute().getData().getId();
        int direction_id = schedule.getAttributes().getDirection_id();
        String stop_id = schedule.getRelationships().getStop().getData().getId();

        if ("Orange".equals(route_id)) {
            holder.routeImage.setImageResource(R.drawable.ic_orange);
        } else if ("Red".equals(route_id)) {
            holder.routeImage.setImageResource(R.drawable.ic_red);
        } else if ("Mattapan".equals(route_id)) {
            holder.routeImage.setImageResource(R.drawable.ic_mattapan);
        } else if ("Blue".equals(route_id)) {
            holder.routeImage.setImageResource(R.drawable.ic_blue);
        } else if (route_id.endsWith("B")) {
            holder.routeImage.setImageResource(R.drawable.ic_greenb);
        } else if (route_id.endsWith("C")) {
            holder.routeImage.setImageResource(R.drawable.ic_greenc);
        } else if (route_id.endsWith("D")) {
            holder.routeImage.setImageResource(R.drawable.ic_greend);
        } else if (route_id.endsWith("E")) {
            holder.routeImage.setImageResource(R.drawable.ic_greene);
        }

        holder.routeName.setText(route_id);
        holder.time.setText(arrTimeData.substring(0,arrTimeData.length()-6));

    }

    @Override
    public int getItemCount() {
        return mScheduleList.size();
    }

}


