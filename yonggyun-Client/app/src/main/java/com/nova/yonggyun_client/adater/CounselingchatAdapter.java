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
import com.nova.yonggyun_client.item.CounselingRecyclerItem;

import java.util.ArrayList;

public class CounselingchatAdapter extends RecyclerView.Adapter<CounselingchatAdapter.CounselingViewHolder> {
    private ArrayList<CounselingRecyclerItem> mCounselingItemList;
    private Context mContext;

    public void setData(ArrayList<CounselingRecyclerItem> list){ mCounselingItemList = list; }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CounselingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_counselingchat,parent,false);
        return new CounselingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CounselingViewHolder holder, int position) {
        CounselingRecyclerItem item= mCounselingItemList.get(position);
        // 이미지 추가
        Glide.with(mContext).load(item.getPetImg()).into(holder.imgPet);
        Glide.with(mContext).load(item.getCounselingImg()).into(holder.imgAttachment);
        holder.txtPetname.setText(item.getPetName());
        holder.txtCounselingcontent.setText(item.getCounselingContent());
    }

    @Override
    public int getItemCount() {
        return mCounselingItemList.size();
    }

    public class CounselingViewHolder extends RecyclerView.ViewHolder{
        ImageView imgPet;
        TextView txtPetname;
        //TextView txtDate;
        ImageView imgAttachment;
        TextView txtCounselingcontent;

        public CounselingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPet = itemView.findViewById(R.id.img_pet);
            txtPetname = itemView.findViewById(R.id.txt_petname);
            // txtDate = itemView.findViewById(R.id.txt_date);
            imgAttachment = itemView.findViewById(R.id.img_attachment);
            txtCounselingcontent = itemView.findViewById(R.id.txt_counselingcontent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        if(mListener != null){
                            mListener.onItemClick(v,pos);
                        }
                    }
                }
            });
        }
    }
}
