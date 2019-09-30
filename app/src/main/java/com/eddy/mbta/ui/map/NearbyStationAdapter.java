package com.eddy.mbta.ui.map;

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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;
import com.eddy.mbta.json.NearbyStationBean;

import java.util.List;

public class NearbyStationAdapter extends RecyclerView.Adapter<NearbyStationAdapter.ViewHolder>{

    private static final String TAG = "NearbyStationAdapter";

    private Context mContext;

    private List<NearbyStationBean.IncludedBean> mNearbyStationList;

    private Window mWindow;
    private View mRoot;

    private FragmentActivity activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView wheelchair;
        TextView stationName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            stationName = (TextView) view.findViewById(R.id.station_name);
            wheelchair = (ImageView) view.findViewById(R.id.wheelchair);
        }
    }

    public NearbyStationAdapter(List<NearbyStationBean.IncludedBean> nearbyStationList, Window window, View root, FragmentActivity act) {
        mNearbyStationList = nearbyStationList;
        mWindow = window;
        mRoot = root;
        activity = act;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.nearby_station_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                NearbyStationBean.IncludedBean station = mNearbyStationList.get(position);

                SchedulePopWindow PopWin = new SchedulePopWindow(mContext, station.getId(), activity);

                PopWin.showAtLocation(mRoot.findViewById(R.id.layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                //final Window window = getActivity().getWindow();
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

                /*Intent intent = new Intent(mContext, FruitActivity.class);
                intent.putExtra(FruitActivity.FRUIT_NAME, fruit.getName());
                intent.putExtra(FruitActivity.FRUIT_IMAGE_ID, fruit.getImageId());
                mContext.startActivity(intent);*/
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
        //holder.directionInfo.setText(schedule.getAttributes().getDirection_id()+"");
        //holder.arrTime.setText(schedule.getAttributes().getArrival_time());
        //holder.depTime.setText(schedule.getAttributes().getDeparture_time());
        //holder.alertDesc.setText(alert.getAttributes().getHeader());
        //Glide.with(mContext).load(alert.getImageId()).into(holder.fruitImage);
    }

    @Override
    public int getItemCount() {
        return mNearbyStationList.size();
    }

}


