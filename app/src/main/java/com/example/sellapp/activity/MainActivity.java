package com.example.sellapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.sellapp.R;
import com.example.sellapp.adapter.ProductCategoryAdapter;
import com.example.sellapp.model.ProductCategory;
import com.example.sellapp.retrofit.RetrofitClient;
import com.example.sellapp.retrofit.SellApi;
import com.example.sellapp.utils.Utils;
import com.google.android.material.navigation.NavigationView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sellApi = RetrofitClient.getInstance(Utils.BASE_URL).create(SellApi.class);

        map();
        actionBar();

        if (isConnected(this)) {
            Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
            actionViewFlipper();
            getLoaiSanPham();
        } else {
            Toast.makeText(getApplicationContext(), "khong co internet", Toast.LENGTH_LONG).show();
        }
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(sellApi.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loaiSpModel -> {
                    if (loaiSpModel.isSuccess()) {
                        Toast.makeText(getApplicationContext(), loaiSpModel.getResult().get(0).getProductName(), Toast.LENGTH_LONG).show();
                    }
                }));
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
        toolbar=findViewById(R.id.toolbarhome);
        viewFlipper=findViewById(R.id.viewflipperhome);
        recyclerViewHome=findViewById(R.id.recyclerview);
        navigationView=findViewById(R.id.navigationview);
        listViewHome=findViewById(R.id.listviewhome);
        drawerLayoutHome=findViewById(R.id.drawerlayouthome);
        //init list
        productCategories = new ArrayList<>();
        //init adapter
        productCategoryAdapter = new ProductCategoryAdapter(productCategories, getApplicationContext());
        listViewHome.setAdapter(productCategoryAdapter);
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected());
    }
}
