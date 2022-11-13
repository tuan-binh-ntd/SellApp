package com.example.sellapp.retrofit;

import com.example.sellapp.model.ProductCategoryModel;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface SellApi {
    @GET("getloaisp.php")
    Observable<ProductCategoryModel> getLoaiSp();
}
