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

    private Context mContext;

    private List<AlertBean.DataBean> mAlertList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView alertTitle, alertTime, alertLifeCycle, alertDesc;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            alertTitle = view.findViewById(R.id.alert_title);
            alertTime = view.findViewById(R.id.alert_time);
            alertLifeCycle = view.findViewById(R.id.alert_lifecycle);
            alertDesc = view.findViewById(R.id.alert_desc);
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_alert, parent, false);
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
        AlertBean.DataBean alert = mAlertList.get(position);
        holder.alertTitle.setText(alert.getAttributes().getService_effect());
        holder.alertTime.setText(alert.getAttributes().getUpdated_at().substring(0, 10));
        holder.alertLifeCycle.setText(alert.getAttributes().getLifecycle());
        holder.alertDesc.setText(alert.getAttributes().getHeader());
    }

    @Override
    public int getItemCount() {
        return mAlertList.size();
    }
}

