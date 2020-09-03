package com.nova.yonggyun_client.adater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.item.DailyMissionViewPageritem;

import java.util.List;

public class DailyMissionDeatilViewPagerAdapter extends RecyclerView.Adapter<DailyMissionDeatilViewPagerAdapter.ViewPagerHolder> {

    private List<DailyMissionViewPageritem> mDataList;
    private ViewPager2 viewPager2;
    private String TAG= DailyMissionDeatilViewPagerAdapter.class.getSimpleName();

    public DailyMissionDeatilViewPagerAdapter(List<DailyMissionViewPageritem> mDataList, ViewPager2 viewPager2) {
        this.mDataList = mDataList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public ViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewPagerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewpager_dailymission_detail,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerHolder holder, int position) {
        holder.setImg(mDataList.get(position));
        if(position == mDataList.size() -2 ){
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ViewPagerHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        ViewPagerHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgItem);
        }

        void setImg(DailyMissionViewPageritem dailyMissionViewPageritem){
            imageView.setImageResource(dailyMissionViewPageritem.getImg());
        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mDataList.addAll(mDataList);
            notifyDataSetChanged();
        }
    };
}
