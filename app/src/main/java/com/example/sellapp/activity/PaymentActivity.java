package com.example.sellapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sellapp.R;
import com.example.sellapp.retrofit.RetrofitClient;
import com.example.sellapp.retrofit.SellApi;
import com.example.sellapp.utils.Utils;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PaymentActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView total, phone, email;
    AppCompatButton paymentBtn;
    EditText locationInput;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    SellApi sellApi;
    long totalMoney = 0;
    int totalItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initView();
        countItem();
        initControl();
    }

    private void countItem() {
        totalItem = 0;
        for(int i = 0; i < Utils.boughtList.size(); i++) {
            totalItem = totalItem + Utils.boughtList.get(i).getQuantity();
        }
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        totalMoney = getIntent().getLongExtra("total", 0);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        total.setText(decimalFormat.format(totalMoney));
        phone.setText(Utils.user.getMobile());
        email.setText(Utils.user.getEmail());

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = locationInput.getText().toString().trim();
                if(TextUtils.isEmpty(location)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_LONG).show();
                } else {
                    //post data
                    Log.d("test", new Gson().toJson(Utils.boughtList));
                    compositeDisposable.add(sellApi.createOrder(
                            Utils.user.getEmail(),
                            Utils.user.getMobile(), String.valueOf(totalMoney), Utils.user.getId(), totalItem, location, new Gson().toJson(Utils.boughtList))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    userModel -> {
                                        Toast.makeText(getApplicationContext(), "Thành công", Toast.LENGTH_LONG).show();
                                        Utils.boughtList.clear();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        finish();
                                    },
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                            ));
                }
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_payment);
        total = findViewById(R.id.total_payment);
        phone = findViewById(R.id.phone_payment);
        email = findViewById(R.id.email_payment);
        paymentBtn = findViewById(R.id.payment_btn);
        locationInput = findViewById(R.id.location_input);
        sellApi = RetrofitClient.getInstance(Utils.BASE_URL).create(SellApi.class);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}