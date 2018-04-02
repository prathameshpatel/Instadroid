package io.github.prathameshpatel.instadroid.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import io.github.prathameshpatel.instadroid.R;
import io.github.prathameshpatel.instadroid.adapter.GridAdapter;
import io.github.prathameshpatel.instadroid.model.Data;
import io.github.prathameshpatel.instadroid.model.InstagramResponse;
import io.github.prathameshpatel.instadroid.rest.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivity extends AppCompatActivity {

    private GridAdapter gridAdapter;
    private ArrayList<Data> data = new ArrayList<>();

    private String access_token = "";
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.feed_activity_title);
        setContentView(R.layout.activity_feed);

        //Get the access token from the intent extra
        Intent i = this.getIntent();
        access_token = i.getStringExtra("access_token");

        RecyclerView gridFeed = findViewById(R.id.grid_feed);
        gridFeed.setLayoutManager(new GridLayoutManager(this, 2));

        //Convert dp to pixels
        int dpToPx = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));

        //set item spacing and decoration on RecyclerView items
        gridFeed.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx, true));
        gridFeed.setItemAnimator(new DefaultItemAnimator());
        gridAdapter = new GridAdapter(this, access_token, data, Glide.with(this));
        gridFeed.setAdapter(gridAdapter);

        //fetch the instagram data for the user
        fetchData();
    }

    public void fetchData() {
        Call<InstagramResponse> call = RestClient.getRetrofitService().getUserMedia(access_token,15);
        call.enqueue(new Callback<InstagramResponse>() {
            @Override
            public void onResponse(Call<InstagramResponse> call, Response<InstagramResponse> response) {
                if(response.body() != null) {
                    for(Data d : response.body().getData()) {
                        data.add(d);
                    }
                    gridAdapter.notifyDataSetChanged();
                    updateOptionsMenu();
                }
            }

            @Override
            public void onFailure(Call<InstagramResponse> call, Throwable t) {
                //Handle failure
                Log.e("fetchData()-onFailure()","Cannot fetch data");
                Toast.makeText(getApplicationContext(),t.toString(), Toast.LENGTH_LONG).show();
                System.out.println(t.toString());
            }
        });
    }

    //Handle like changes coming back from DetailsActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent backIntent) {
        super.onActivityResult(requestCode, resultCode, backIntent);
        if(requestCode == 1 && resultCode == RESULT_OK)  {
            int position = backIntent.getIntExtra("position",-1);
            int likeChanged = backIntent.getIntExtra("like_changed",0);
            if(likeChanged == 1) {
                int likeCount = data.get(position).getLikes().getCount();
                data.get(position).getLikes().setCount(likeCount+1);
                data.get(position).setUserHasLiked(true);
            } else if(likeChanged == -1) {
                int likeCount = data.get(position).getLikes().getCount();
                data.get(position).getLikes().setCount(likeCount-1);
                data.get(position).setUserHasLiked(false);
            }
            gridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        optionsMenu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Attach user profile picture as a Menu icon
    public void updateOptionsMenu() {
        final MenuItem logoutButton = optionsMenu.findItem(R.id.logout_button);
        String profileUrl = data.get(0).getUser().getProfile_picture();
        Glide.with(this).asBitmap().load(profileUrl).apply(RequestOptions.circleCropTransform()).into(new SimpleTarget<Bitmap>(100,100) {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                logoutButton.setIcon(new BitmapDrawable(getResources(), resource));
            }
        });
    }

    //Initiate logout action for menu item onClick event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Logout the user
        System.out.println(data.get(0).getUser().getProfile_picture());
        Intent backIntent = new Intent(FeedActivity.this, MainActivity.class);
        backIntent.putExtra("logout",true);
        setResult(RESULT_OK,backIntent);
        finish();
        return super.onOptionsItemSelected(item);
    }

    //RecyclerView item decoration custom implementation
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /*//Convert dp to pixels
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }*/
}
