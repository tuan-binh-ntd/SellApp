package com.example.sellapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sellapp.R;
import com.example.sellapp.adapter.CartAdapter;
import com.example.sellapp.model.Cart;
import com.example.sellapp.model.eventbus.TotalMoneyCalc;
import com.example.sellapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    TextView emptyCart, total;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Button buyBtn;
    CartAdapter cartAdapter;
    List<Cart> cartList;
    long totalMoney = 0;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void totalMoneyCalcEvent(TotalMoneyCalc totalMoneyCalc) {
        if(totalMoneyCalc != null) {
            totalMoney();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        initView();
        initControl();
        totalMoney();
    }

    private void totalMoney() {
        totalMoney = 0;
        for (int i = 0; i < Utils.boughtList.size(); i++) {
            totalMoney = totalMoney + (Utils.boughtList.get(i).getCost() * Utils.boughtList.get(i).getQuantity());
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        total.setText("Giá: " + decimalFormat.format(totalMoney) + "Đ");
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

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if(Utils.cartList.size() == 0) {
            emptyCart.setVisibility(View.VISIBLE);
        } else {
            cartAdapter = new CartAdapter(getApplicationContext(), Utils.cartList);
            recyclerView.setAdapter(cartAdapter);
        }

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra("total", totalMoney);
                Utils.cartList.clear();
                startActivity(intent);
            }
        });
    }

    private void initView() {
        emptyCart = findViewById(R.id.empty_cart);
        toolbar = findViewById(R.id.toolbar_cart);
        recyclerView = findViewById(R.id.recyclerview_cart);
        total = findViewById(R.id.total);
        buyBtn = findViewById(R.id.buy_btn);
    }
}