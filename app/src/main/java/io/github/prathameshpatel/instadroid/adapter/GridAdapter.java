package io.github.prathameshpatel.instadroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

import io.github.prathameshpatel.instadroid.R;
import io.github.prathameshpatel.instadroid.activities.DetailsActivity;
import io.github.prathameshpatel.instadroid.activities.FeedActivity;
import io.github.prathameshpatel.instadroid.model.Data;
import io.github.prathameshpatel.instadroid.model.InstagramResponse;
import io.github.prathameshpatel.instadroid.rest.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Data> data;
    private LayoutInflater inflater;
    private String access_token;
    private RequestManager glide;

    public GridAdapter(Context context, String access_token, ArrayList<Data> objects, RequestManager glide) {
        this.context = context;
        this.access_token = access_token;
        this.data = objects;
        this.glide = glide;
        this.inflater = LayoutInflater.from(context);
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View recyclerView = inflater.inflate(R.layout.feed_item, parent, false);

        return new ViewHolder(recyclerView);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_photo;
        TextView feed_likes_text;
        ImageButton feed_likes_button;

        ViewHolder(View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_photo);
            feed_likes_text = itemView.findViewById(R.id.feed_likes_text);
            feed_likes_button = itemView.findViewById(R.id.feed_likes_button);

            iv_photo.setOnClickListener(this);
            feed_likes_button.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == iv_photo.getId()) {
                Intent intent = new Intent(view.getContext(), DetailsActivity.class);
                intent.putExtra("access_token", access_token);
                intent.putExtra("media_id", data.get(getAdapterPosition()).getId());
                intent.putExtra("image_url", data.get(getAdapterPosition()).getImages().getStandard_resolution().getUrl());
                intent.putExtra("user_has_liked", data.get(getAdapterPosition()).getUserHasLiked());
                intent.putExtra("number_of_likes", data.get(getAdapterPosition()).getLikes().getCount());
                intent.putExtra("created_time", data.get(getAdapterPosition()).getCreated_time());
                intent.putExtra("position", getAdapterPosition());
                ((FeedActivity) context).startActivityForResult(intent, 1);
            } else if (view.getId() == feed_likes_button.getId()) {
                if (!data.get(getAdapterPosition()).getUserHasLiked()) {
                    feed_likes_button.setImageResource(R.drawable.feed_like_liked);
                    data.get(getAdapterPosition()).setUserHasLiked(true); //user liked
                    int count = data.get(getAdapterPosition()).getLikes().getCount();
                    data.get(getAdapterPosition()).getLikes().setCount(count + 1);
                    //POST to instagram server
                    doLikePost(data.get(getAdapterPosition()).getId());
                    notifyDataSetChanged();
                } else {
                    feed_likes_button.setImageResource(R.drawable.feed_like_default);
                    data.get(getAdapterPosition()).setUserHasLiked(false); //user disliked
                    int count = data.get(getAdapterPosition()).getLikes().getCount();
                    data.get(getAdapterPosition()).getLikes().setCount(count - 1);
                    //DEL to instagram server
                    doLikeDelete(data.get(getAdapterPosition()).getId());
                    notifyDataSetChanged();
                }
            }
        }
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.feed_likes_text.setText(String.format("%d Likes", data.get(position).getLikes().getCount()));
        glide.load(data.get(position).getImages().getLow_resolution().getUrl()).into(holder.iv_photo);
        if (!data.get(position).getUserHasLiked()) {
            holder.feed_likes_button.setImageResource(R.drawable.feed_like_default);
        } else {
            holder.feed_likes_button.setImageResource(R.drawable.feed_like_liked);
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return data.size();
    }

    //Post a like to Instagram server
    private void doLikePost(String media_id) {
        Call<InstagramResponse> call = RestClient.getRetrofitService().postLike(media_id, access_token);
        call.enqueue(new Callback<InstagramResponse>() {
            @Override
            public void onResponse(retrofit2.Call<InstagramResponse> call, Response<InstagramResponse> response) {
                if (response.body() != null) {
                    if (response.body().getMeta().getCode() == 200) {
                        Log.e("likePost()-onResponse()", "Success post like from details activity");
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<InstagramResponse> call, Throwable t) {
                //Handle failure
                Log.e("likePost()-onFailure()", "Cannot post like from details activity");
                System.out.println(t.toString());
            }
        });
    }

    //Delete a like from Instagram server
    private void doLikeDelete(String media_id) {
        Call<InstagramResponse> call = RestClient.getRetrofitService().deleteLike(media_id, access_token);
        call.enqueue(new Callback<InstagramResponse>() {
            @Override
            public void onResponse(retrofit2.Call<InstagramResponse> call, Response<InstagramResponse> response) {
                if (response.body() != null) {
                    if (response.body().getMeta().getCode() == 200) {
                        Log.e("likeDel()-onResponse()", "Success delete like from feed activity");
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<InstagramResponse> call, Throwable t) {
                //Handle failure
                Log.e("likeDel()-onFailure()", "Cannot delete like from feed activity");
                System.out.println(t.toString());
            }
        });
    }
}
