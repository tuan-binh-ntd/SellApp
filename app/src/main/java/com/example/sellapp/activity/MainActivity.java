package com.example.sellapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.sellapp.R;
import com.example.sellapp.adapter.NewProductAdapter;
import com.example.sellapp.adapter.ProductCategoryAdapter;
import com.example.sellapp.model.NewProduct;
import com.example.sellapp.model.ProductCategory;
import com.example.sellapp.retrofit.RetrofitClient;
import com.example.sellapp.retrofit.SellApi;
import com.example.sellapp.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewHome;
    NavigationView navigationView;
    ListView listViewHome;
    DrawerLayout drawerLayoutHome;
    ProductCategoryAdapter productCategoryAdapter;
    List<ProductCategory> productCategories;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    SellApi sellApi;
    List<NewProduct> newProductList;
    NewProductAdapter newProductAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sellApi = RetrofitClient.getInstance(Utils.BASE_URL).create(SellApi.class);

        map();
        actionBar();

        if (isConnected(this)) {
            actionViewFlipper();
            getLoaiSanPham();
            getNewProduct();
            getEventClick();
        } else {
            Toast.makeText(getApplicationContext(), "không có internet, vui lòng kết nối", Toast.LENGTH_LONG).show();
        }
    }

    private void getEventClick() {
        listViewHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Intent home = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(home);
                        break;
                    case 1:
                        Intent phone = new Intent(getApplicationContext(), PhoneActivity.class);
                        phone.putExtra("loai",1);
                        startActivity(phone);
                        break;
                    case 2:
                        Intent laptop = new Intent(getApplicationContext(), PhoneActivity.class);
                        laptop.putExtra("loai",2);
                        startActivity(laptop);
                        break;
                    case 5:
                        Intent order = new Intent(getApplicationContext(), OrderActivity.class);
                        startActivity(order);
                        break;
                }
            }
        });
    }

    private void getNewProduct() {
        boolean add = compositeDisposable.add(sellApi.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newProductModel -> {
                            if (newProductModel.isSuccess()) {
                                newProductList = newProductModel.getResult();
                                //init adapter
                                newProductAdapter = new NewProductAdapter(getApplicationContext(), newProductList);
                                recyclerViewHome.setAdapter(newProductAdapter);
                            }
                        }, throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được với server" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void getLoaiSanPham() {
        boolean add = compositeDisposable.add(sellApi.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(productCategoryModel -> {
                            if (productCategoryModel.isSuccess()) {
                                productCategories = productCategoryModel.getResult();
                                //init adapter
                                productCategoryAdapter = new ProductCategoryAdapter(productCategories, getApplicationContext());
                                listViewHome.setAdapter(productCategoryAdapter);
                            }
                        }, throwable -> Log.e("asd","Throwable " + throwable.getMessage())
                ));
    }

    private void actionViewFlipper() {
        List<String> advertise = new ArrayList<>();
        advertise.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-Le-hoi-phu-kien-800-300.png");
        advertise.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-HC-Tra-Gop-800-300.png");
        advertise.add("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-big-ky-nguyen-800-300.jpg");
        for (int i = 0; i < advertise.size(); i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(advertise.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);

    }


    private void actionBar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(view -> drawerLayoutHome.openDrawer(GravityCompat.START));
    }

    private void map() {
        search = findViewById(R.id.search_img);
        toolbar=findViewById(R.id.toolbarhome);
        viewFlipper=findViewById(R.id.viewflipperhome);
        recyclerViewHome=findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager  = new GridLayoutManager(this, 2);
        recyclerViewHome.setLayoutManager(layoutManager);
        recyclerViewHome.setHasFixedSize(true);
        navigationView=findViewById(R.id.navigationview);
        listViewHome=findViewById(R.id.listviewhome);
        drawerLayoutHome=findViewById(R.id.drawerlayouthome);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.frameCart);
        //init list
        productCategories = new ArrayList<>();
        newProductList = new ArrayList<>();
        if(Utils.cartList == null) {
            Utils.cartList = new ArrayList<>();
        } else {
            int totalItem = 0;
            for(int i = 0; i < Utils.cartList.size(); i++) {
                totalItem = totalItem + Utils.cartList.get(i).getQuantity();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for(int i = 0; i < Utils.cartList.size(); i++) {
            totalItem = totalItem + Utils.cartList.get(i).getQuantity();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected());
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();

    }
}
