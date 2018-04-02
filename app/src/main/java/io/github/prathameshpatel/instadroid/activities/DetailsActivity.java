package io.github.prathameshpatel.instadroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Date;

import io.github.prathameshpatel.instadroid.R;
import io.github.prathameshpatel.instadroid.model.InstagramResponse;
import io.github.prathameshpatel.instadroid.rest.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {

    ImageView details_photo;
    ImageButton details_likes_button;
    TextView details_likes_text;
    TextView details_time_text;

    private String access_token;
    private String media_id;
    private int number_of_likes;
    private boolean user_has_liked;
    private int likeChanged = 0;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.details_activity_title);
        setContentView(R.layout.activity_details);

        Intent intent = this.getIntent();
        access_token = intent.getStringExtra("access_token");
        media_id = intent.getStringExtra("media_id");
        String image_url = intent.getStringExtra("image_url");
        number_of_likes = intent.getIntExtra("number_of_likes",0);
        user_has_liked = intent.getBooleanExtra("user_has_liked",false);
        String created_time = intent.getStringExtra("created_time");
        position = intent.getIntExtra("position",-1);

        details_photo = findViewById(R.id.details_photo);
        details_likes_button = findViewById(R.id.details_likes_button);
        details_likes_text = findViewById(R.id.details_likes_text);
        details_time_text = findViewById(R.id.details_time_text);

        Glide.with(this).load(image_url).into(details_photo);
        details_likes_text.setText(String.format("%d Likes", number_of_likes));
        details_time_text.setText(convertTime(created_time));

        if(!user_has_liked) {
            details_likes_button.setImageResource(R.drawable.photo_like_default);
        } else {
            details_likes_button.setImageResource(R.drawable.photo_like_liked);
        }

        details_likes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user_has_liked) {
                    details_likes_button.setImageResource(R.drawable.photo_like_liked);
                    number_of_likes += 1;
                    details_likes_text.setText(String.format("%d Likes", number_of_likes));
                    user_has_liked = true;
                    //POST to instagram server
                    likeChanged = 1;

                } else {
                    details_likes_button.setImageResource(R.drawable.photo_like_default);
                    number_of_likes -= 1;
                    details_likes_text.setText(String.format("%d Likes", number_of_likes));
                    user_has_liked = false;
                    //DEL to instagram server
                    likeChanged = -1;
                }
            }
        });
    }

    //Post a like to Instagram server
    public void doLikePost() {
        Call<InstagramResponse> call = RestClient.getRetrofitService().postLike(media_id,access_token);
        call.enqueue(new Callback<InstagramResponse>() {
            @Override
            public void onResponse(retrofit2.Call<InstagramResponse> call, Response<InstagramResponse> response) {
                if(response.body() != null) {
                    if(response.body().getMeta().getCode() == 200) {
                        Log.e("likePost()-onResponse()","Success post like from details activity");
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<InstagramResponse> call, Throwable t) {
                //Handle failure
                Log.e("likePost()-onFailure()","Cannot post like from details activity");
                System.out.println(t.toString());
            }
        });
    }

    //Delete a like from Instagram server
    public void doLikeDelete() {
        Call<InstagramResponse> call = RestClient.getRetrofitService().deleteLike(media_id,access_token);
        call.enqueue(new Callback<InstagramResponse>() {
            @Override
            public void onResponse(retrofit2.Call<InstagramResponse> call, Response<InstagramResponse> response) {
                if(response.body() != null) {
                    if(response.body().getMeta().getCode() == 200) {
                        Log.e("likeDel()-onResponse()","Success delete like from details activity");
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<InstagramResponse> call, Throwable t) {
                //Handle failure
                Log.e("likeDel()-onFailure()","Cannot delete like from details activity");
                System.out.println(t.toString());
            }
        });
    }

    //Convert unix-time from Instagram response to string values
    private String convertTime(String timeResponse) {
        Date then = new Date(Long.parseLong(timeResponse)*1000);
        Date now = new Date();
        long diff = now.getTime() - then.getTime();

//        long diffiSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        long diffWeeks = diff / (7 * 24 * 60 * 60 * 1000);

        long diffSeconds = diff/1000;
        if(diffSeconds < 60) return diffSeconds+"s ago";
        else if(diffSeconds > 60 && diffSeconds < 60*60) return diffMinutes+"m ago";
        else if(diffSeconds > 60*60 && diffSeconds < 60*60*24) return diffHours+"h ago";
        else if(diffSeconds > 60*60*24 && diffSeconds < 60*60*24*7) return diffDays+"d ago";
        else if(diffSeconds > 60*60*24*7 && diffSeconds < 60*60*24*7*4) return diffWeeks+"w ago";
        else return diffWeeks+"w ago";
    }

    //Handle like changes after back key click
    @Override
    public void onBackPressed() {
        if(likeChanged == 1) {
            doLikePost();
        } else if(likeChanged == -1) {
            doLikeDelete();
        }
        Intent backIntent = new Intent(DetailsActivity.this, FeedActivity.class);
        backIntent.putExtra("position",position);
        backIntent.putExtra("like_changed",likeChanged);
        setResult(RESULT_OK,backIntent);
        finish();
    }
}
