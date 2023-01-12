package com.example.sellapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.sellapp.R;
import com.example.sellapp.model.Cart;
import com.example.sellapp.model.NewProduct;
import com.example.sellapp.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

import java.lang.reflect.Array;
import java.text.DecimalFormat;

public class DetailActivity extends AppCompatActivity {
    TextView name, price, desc;
    Button addBtn;
    ImageView img;
    Spinner spinner;
    Toolbar toolbar;
    NewProduct newProduct;
    NotificationBadge badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        actionToolBar();
        initData();
        initControl();
    }

    private void initControl() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });
    }

    private void addToCart() {
        if(Utils.cartList.size() > 0) {
            boolean flag = false;
            int quantity = Integer.parseInt(spinner.getSelectedItem().toString());
            for(int i = 0; i < Utils.cartList.size(); i++) {
                if(Utils.cartList.get(i).getProductId() == newProduct.getId()) {
                    Utils.cartList.get(i).setQuantity(quantity + Utils.cartList.get(i).getQuantity());
                    long cost = Long.parseLong(newProduct.getGiasp()) * Utils.cartList.get(i).getQuantity();
                    Utils.cartList.get(i).setCost(cost);
                    flag = true;
                }
            }
            if(!flag) {
                long cost = Long.parseLong(newProduct.getGiasp()) * quantity;
                Cart cart = new Cart();
                cart.setCost(cost);
                cart.setQuantity(quantity);
                cart.setProductId(newProduct.getId());
                cart.setProductName(newProduct.getTensp());
                cart.setImg(newProduct.getHinhanh());
                Utils.cartList.add(cart);
            }

        } else {
            int quantity = Integer.parseInt(spinner.getSelectedItem().toString());
            long cost = Long.parseLong(newProduct.getGiasp()) * quantity;
            Cart cart = new Cart();
            cart.setCost(cost);
            cart.setQuantity(quantity);
            cart.setProductId(newProduct.getId());
            cart.setProductName(newProduct.getTensp());
            cart.setImg(newProduct.getHinhanh());
            Utils.cartList.add(cart);
        }
        int totalItem = 0;
        for(int i = 0; i < Utils.cartList.size(); i++) {
            totalItem = totalItem + Utils.cartList.get(i).getQuantity();
        }
        badge.setText(String.valueOf(totalItem));
        Toast.makeText(getApplicationContext(), "Thêm vào giỏ hàng thành công", Toast.LENGTH_LONG).show();
    }

    private void initData() {
        newProduct = (NewProduct) getIntent().getSerializableExtra("detail");
        name.setText(newProduct.getTensp());
        desc.setText(newProduct.getMota());
        Glide.with(getApplicationContext()).load(newProduct.getHinhanh()).into(img);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        price.setText("Giá: " + decimalFormat.format(Double.parseDouble(newProduct.getGiasp())) + "Đ");
        Integer[] valueSpinner = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, valueSpinner);
        spinner.setAdapter(arrayAdapter);
    }

    private void initView() {
        name = findViewById(R.id.item_detail_name);
        price = findViewById(R.id.item_detail_price);
        desc = findViewById(R.id.detail_desc);
        addBtn = findViewById(R.id.add_item_to_cart);
        img = findViewById(R.id.img_detail);
        spinner = findViewById(R.id.spinner);
        toolbar = findViewById(R.id.toolbar_detail);
        badge = findViewById(R.id.menu_sl);
        FrameLayout frameLayoutCart = findViewById(R.id.frameCart);
        frameLayoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });
        if(Utils.cartList != null) {
            int totalItem = 0;
            for(int i = 0; i < Utils.cartList.size(); i++) {
                totalItem = totalItem + Utils.cartList.get(i).getQuantity();
            }
            badge.setText(String.valueOf(totalItem));
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.cartList != null) {
            int totalItem = 0;
            for(int i = 0; i < Utils.cartList.size(); i++) {
                totalItem = totalItem + Utils.cartList.get(i).getQuantity();
            }
            badge.setText(String.valueOf(totalItem));
        }
    }
}