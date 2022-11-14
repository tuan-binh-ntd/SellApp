package com.example.sellapp.retrofit;

import com.example.sellapp.model.NewProductModel;
import com.example.sellapp.model.ProductCategoryModel;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SellApi {
    @GET("getloaisp.php")
    Observable<ProductCategoryModel> getLoaiSp();

    @GET("getspmoi.php")
    Observable<NewProductModel> getSpMoi();

    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<NewProductModel> getSanPham(
            @Field("page") int page,
            @Field("loai") int loai
    );
}
