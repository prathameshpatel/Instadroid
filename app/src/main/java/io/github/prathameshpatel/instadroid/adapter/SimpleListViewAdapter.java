package io.github.prathameshpatel.instadroid.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.github.prathameshpatel.instadroid.R;
import io.github.prathameshpatel.instadroid.model.Data;


public class SimpleListViewAdapter extends ArrayAdapter<Data> {

    private Context context;
    private ArrayList<Data> data;


    public SimpleListViewAdapter(Context context, int textViewResourceId, ArrayList<Data> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.data = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View curView = convertView;
        if(curView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            curView = inflater.inflate(R.layout.feed_item,null);
        }

        ImageView iv_photo = curView.findViewById(R.id.iv_photo);
        TextView feed_likes_text = curView.findViewById(R.id.feed_likes_text);
        ImageButton feed_likes_button = curView.findViewById(R.id.feed_likes_button);

//        feed_likes_text.setText(data.get(position).getUser().getProfile_picture());
        String createdTime = data.get(position).getCreated_time();
        feed_likes_text.setText(data.get(position).getLikes().getCount()+" Likes");
        Glide.with(context).load(data.get(position).getImages().getLow_resolution().getUrl())
                .thumbnail(0.5f)
                .into(iv_photo);


        return curView;
    }
}
