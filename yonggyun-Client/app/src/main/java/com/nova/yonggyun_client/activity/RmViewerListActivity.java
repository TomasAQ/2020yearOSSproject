package com.nova.yonggyun_client.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nova.yonggyun_client.R;
import com.remotemonster.sdk.RemonCast;
import com.remotemonster.sdk.data.Room;

import java.util.ArrayList;
import java.util.List;

public class RmViewerListActivity extends AppCompatActivity {
    private static final String TAG = RmViewerListActivity.class.getSimpleName();
    RemonCast mRemonCast;
    ArrayList<Room> mRoomList;
    ListView listView;
    RoomAdapter apapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rm_viewer_list);
        listView = findViewById(R.id.lv_channel);

        mRoomList = new ArrayList<>();
        apapter = new RoomAdapter();
        listView.setAdapter(apapter);

        redata();
        // mRemonCast.fetchCasts();

    }

    public void recall(View view) {
        redata();
    }

    void redata(){
        mRemonCast = RemonCast.builder()
                .context(RmViewerListActivity.this)
                .serviceId("id")
                .key("key")
                .build();
        mRemonCast.onInit(() -> mRemonCast.fetchCasts());
        mRemonCast.onFetch(rooms ->{
            mRoomList.clear();
            for (Room room : rooms){
                Log.d(TAG, "onCreate: room : "+room);
                mRoomList.add(room);
            }
            apapter.notifyDataSetChanged();
        });

    }

    class RoomAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            Log.d(TAG, "getCount: size : "+mRoomList.size());
            return mRoomList.size();
        }
        @Override
        public Object getItem(int position) { return mRoomList.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_channel , parent , false);
            }

            TextView tvRoomInfo =  (TextView) convertView.findViewById(R.id.tvRoomInfo);
            TextView tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
            tvRoomInfo.setText(mRoomList.get(pos).getId());
            tvStatus.setText(mRoomList.get(pos).getStatus());
            tvRoomInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext() , RemotemonActivity.class);
                    intent.putExtra("isCreate",false);
                    intent.putExtra("chid",mRoomList.get(pos).getId());
                    startActivity(intent);
                }
            });

            tvStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext() , RemotemonActivity.class);
                    intent.putExtra("isCreate",false);
                    intent.putExtra("setConfig",true);
                    intent.putExtra("chid",mRoomList.get(pos).getId());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

}
