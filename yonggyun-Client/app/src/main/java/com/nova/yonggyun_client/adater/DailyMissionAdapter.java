package com.nova.yonggyun_client.adater;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.item.DailyMissionRecyclerItem;

import java.util.ArrayList;

public class DailyMissionAdapter extends RecyclerView.Adapter<DailyMissionAdapter.DailyMissionViewHolder> {
    private ArrayList<DailyMissionRecyclerItem> mDailyMissionRecyclerItems;
    private Context mContext;

    public interface OnItemClickListener{
        void onItemClick(View v , int position);
    }
    private OnItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setData(ArrayList<DailyMissionRecyclerItem> list){
        mDailyMissionRecyclerItems = list;
    }
    @NonNull
    @Override
    public DailyMissionAdapter.DailyMissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_dailymision,parent,false);

        return new DailyMissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyMissionAdapter.DailyMissionViewHolder holder, int position) {
        final DailyMissionRecyclerItem data = mDailyMissionRecyclerItems.get(position);
        Glide.with(mContext).load(data.getImg()).into(holder.imgMission);
        holder.txtMissionDescription.setText(data.getDescription());
        holder.txtMissionTitle.setText(data.getTitle());
//        holder.txtMissionCompletCount.setText("완료 : "+Integer.toString(data.getCompletCount()));
//        holder.txtMissionParticipantCount.setText("참여자 수 : "+Integer.toString(data.getParticipantCount()));
    }

    @Override
    public int getItemCount() {
        return mDailyMissionRecyclerItems.size();
    }


    public class DailyMissionViewHolder extends RecyclerView.ViewHolder{
        ImageView imgMission;
        TextView txtMissionDescription,txtMissionTitle,txtMissionCompletCount,txtMissionParticipantCount;


        public DailyMissionViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMission = itemView.findViewById(R.id.img_mission);
            txtMissionDescription = itemView.findViewById(R.id.txt_mission_description);
            txtMissionTitle = itemView.findViewById(R.id.txt_mission_title);
//            txtMissionCompletCount = itemView.findViewById(R.id.txt_mission_complet_count);
//            txtMissionParticipantCount = itemView.findViewById(R.id.txt_mission_participant_count);
            imgMission = itemView.findViewById(R.id.img_mission);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(mOnItemClickListener != null){
                            mOnItemClickListener.onItemClick(v,pos);
                        }
                    }
                }
            });

        }
    }
}
