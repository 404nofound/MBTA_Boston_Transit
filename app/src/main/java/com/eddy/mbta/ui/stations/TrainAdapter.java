package com.eddy.mbta.ui.stations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;

import java.util.List;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.ViewHolder>{

    private static final String TAG = "TrainAdapter";

    private Context mContext;

    private List<Integer> mTrainList;

    public String[] route_id = {"Red", "Mattapan", "Orange", "Green B", "Green C", "Green D", "Green E", "Blue"};
    public String[] start = {"Alewife", "Ashmont", "Oak Grove", "Park St", "North Station", "Park St", "Lechmere", "Bowdoin"};
    public String[] end = {"Braintree", "Mattapan", "Forest Hills", "Boston College", "Cleveland Cir", "Riverside", "Health St", "Wonderland"};
    private int[] icon = {R.drawable.ic_red, R.drawable.ic_mattapan, R.drawable.ic_orange, R.drawable.ic_greenb, R.drawable.ic_greenc, R.drawable.ic_greend, R.drawable.ic_greene, R.drawable.ic_blue};

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView iconView;
        TextView nameView, startView, endView;

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
        int num = mTrainList.get(position);
        holder.nameView.setText(route_id[num]);
        holder.startView.setText(start[num]);
        holder.endView.setText(end[num]);
        holder.iconView.setImageResource(icon[num]);
    }

    @Override
    public int getItemCount() {
        return mTrainList.size();
    }

}

