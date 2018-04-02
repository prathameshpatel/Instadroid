package io.github.prathameshpatel.instadroid.interfaces;


import io.github.prathameshpatel.instadroid.model.InstagramResponse;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    //get user recent media
    @GET("v1/users/self/media/recent")
    Call<InstagramResponse> getUserMedia(@Query("access_token") String access_token,
                                         @Query("count") int count);

    //curl -F 'access_token=ACCESS-TOKEN' \ https://api.instagram.com/v1/media/{media-id}/likes
    @FormUrlEncoded
    @POST("v1/media/{media-id}/likes")
    Call<InstagramResponse> postLike(@Path("media-id") String media_id,
                                     @Field("access_token") String access_token);

    //curl -X DELETE https://api.instagram.com/v1/media/{media-id}/likes?access_token=ACCESS-TOKEN
    @DELETE("v1/media/{media-id}/likes")
    Call<InstagramResponse> deleteLike(@Path("media-id") String media_id,
                                       @Query("access_token") String access_token);

}
