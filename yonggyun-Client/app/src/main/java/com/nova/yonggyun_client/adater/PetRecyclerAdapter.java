package com.nova.yonggyun_client.adater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nova.yonggyun_client.R;
import com.nova.yonggyun_client.item.PetsRecyclerItem;

import java.util.ArrayList;

public class PetRecyclerAdapter extends RecyclerView.Adapter<PetRecyclerAdapter.PetRecyclerViewHolder> {
    private ArrayList<PetsRecyclerItem> mPetsRecyclerItems;
    private Context mContext;

    public void setData(ArrayList<PetsRecyclerItem> list){
        mPetsRecyclerItems = list;
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조
    private OnItemClickListener mListener = null;


    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }


    @NonNull
    @Override
    public PetRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_pets,parent,false);
        mContext = parent.getContext();
        return new PetRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetRecyclerViewHolder holder, int position) {
        final PetsRecyclerItem data = mPetsRecyclerItems.get(position);
        Glide.with(mContext).load(mPetsRecyclerItems.get(position).getImageurl()).into(holder.imageView);
        holder.description.setText(data.getDescription());
    }

    @Override
    public int getItemCount() {
        return mPetsRecyclerItems.size();
    }

    public class PetRecyclerViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView description;

        public PetRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.icon_recycler_pets);
            description = itemView.findViewById(R.id.description_recycler_pets);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        // 리스너 객체의 메서드 호출
                        if(mListener != null){
                            mListener.onItemClick(v,pos);
                        }
                    }
                }
            });

        }
    }
}
