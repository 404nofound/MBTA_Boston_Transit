package com.eddy.mbta.ui.alerts;

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

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder>{

    private static final String TAG = "AlertAdapter";

    private Context mContext;

    private List<AlertBean.DataBean> mAlertList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView alertTitle, alertTime, alertLifeCycle, alertDesc;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            alertTitle = (TextView) view.findViewById(R.id.alert_title);
            alertTime = (TextView) view.findViewById(R.id.alert_time);
            alertLifeCycle = (TextView) view.findViewById(R.id.alert_lifecycle);
            alertDesc = (TextView) view.findViewById(R.id.alert_desc);
        }
    }

    public AlertAdapter(List<AlertBean.DataBean> alertList) {
        mAlertList = alertList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.alert_item, parent, false);
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
        holder.alertTitle.setText(alert.getAttributes().getService_effect());
        holder.alertTime.setText(alert.getAttributes().getUpdated_at().substring(0, 10));
        holder.alertLifeCycle.setText(alert.getAttributes().getLifecycle());
        holder.alertDesc.setText(alert.getAttributes().getHeader());
        //Glide.with(mContext).load(alert.getImageId()).into(holder.fruitImage);
    }

    @Override
    public int getItemCount() {
        return mAlertList.size();
    }

}

