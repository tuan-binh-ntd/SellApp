package com.example.sellapp.retrofit;

import com.example.sellapp.model.Cart;
import com.example.sellapp.model.NewProductModel;
import com.example.sellapp.model.OrderModel;
import com.example.sellapp.model.ProductCategoryModel;
import com.example.sellapp.model.UserModel;

import java.util.List;

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

    @POST("register.php")
    @FormUrlEncoded
    Observable<UserModel> register(
            @Field("email") String email,
            @Field("password") String password,
            @Field("username") String username,
            @Field("mobile") String mobile
    );

    @POST("login.php")
    @FormUrlEncoded
    Observable<UserModel> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("donhang.php")
    @FormUrlEncoded
    Observable<UserModel> createOrder(
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("totalmoney") String totalmoney,
            @Field("userid") int userid,
            @Field("quantity") int quantity,
            @Field("location") String location,
            @Field("chitiet") String chitiet
    );

    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<OrderModel> viewOrder(
            @Field("userid") int userid
    );

    @POST("search.php")
    @FormUrlEncoded
    Observable<NewProductModel> search(
            @Field("search") String search
    );
}
