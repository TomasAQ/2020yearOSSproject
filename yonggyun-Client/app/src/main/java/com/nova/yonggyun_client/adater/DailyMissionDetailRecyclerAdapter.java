package com.nova.yonggyun_client.adater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.item.DailyMissionDetailRecyclerItem;

import java.util.ArrayList;

public class DailyMissionDetailRecyclerAdapter extends RecyclerView.Adapter<DailyMissionDetailRecyclerAdapter.DailyMissionDetailRecyclerViewHolder>{
    private ArrayList<DailyMissionDetailRecyclerItem> mDataList;
    private Context mContext;




    public DailyMissionDetailRecyclerAdapter(ArrayList<DailyMissionDetailRecyclerItem> list) {
        mDataList = list;
    }

    @NonNull
    @Override
    public DailyMissionDetailRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_mission_detail , parent , false);
        return new DailyMissionDetailRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyMissionDetailRecyclerViewHolder holder, int position) {
        holder.mMissionDate.setText(mDataList.get(position).getDate());
        holder.mMissionUser.setText(mDataList.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public class DailyMissionDetailRecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView mMissionDate , mMissionUser;

        public DailyMissionDetailRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            mMissionDate = itemView.findViewById(R.id.txt_mission_date);
            mMissionUser = itemView.findViewById(R.id.txt_mission_user);
        }
    }
}
