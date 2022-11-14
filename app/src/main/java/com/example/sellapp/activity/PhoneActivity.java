package com.example.sellapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.sellapp.R;
import com.example.sellapp.adapter.PhoneAdapter;
import com.example.sellapp.model.NewProduct;
import com.example.sellapp.retrofit.RetrofitClient;
import com.example.sellapp.retrofit.SellApi;
import com.example.sellapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PhoneActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    SellApi sellApi;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int page = 1;
    int loai;
    PhoneAdapter phoneAdapter;
    List<NewProduct> newProductList;
    LinearLayoutManager linearLayoutManager;
    Handler handler = new Handler();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        sellApi = RetrofitClient.getInstance(Utils.BASE_URL).create(SellApi.class);
        loai = getIntent().getIntExtra("loai", 1);

        map();
        actionToolBar();
        getData(page);
        addEventLoad();
    }

    private void addEventLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isLoading == false) {
                    if(linearLayoutManager.findLastVisibleItemPosition() == newProductList.size() - 1) {
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // add null
                newProductList.add(null);
                phoneAdapter.notifyItemInserted(newProductList.size() - 1);
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //remove null
                newProductList.remove(newProductList.size() - 1);
                phoneAdapter.notifyItemRemoved(newProductList.size());
                page = page + 1;
                getData(page);
                phoneAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

    private void getData(int page) {
        compositeDisposable.add(sellApi.getSanPham(page, loai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newProductModel -> {
                    if(newProductModel.isSuccess()) {
                        if(phoneAdapter == null) {
                            newProductList = newProductModel.getResult();
                            phoneAdapter = new PhoneAdapter(getApplicationContext(), newProductList);
                            recyclerView.setAdapter(phoneAdapter);
                        } else {
                            int position = newProductList.size() - 1;
                            int quantity = newProductModel.getResult().size();
                            for(int i = 0; i<quantity; i++) {
                                newProductList.add(newProductModel.getResult().get(i));
                            }
                            phoneAdapter.notifyItemRangeInserted(position, quantity);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Hết dữ liệu", Toast.LENGTH_LONG).show();
                        isLoading = true;
                    }
                },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "khong ket noi server", Toast.LENGTH_LONG).show();
                        }));
    }

    private void actionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void map() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerview_phone);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        newProductList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}