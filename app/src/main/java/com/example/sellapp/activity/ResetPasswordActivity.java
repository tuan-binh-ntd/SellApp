package com.example.sellapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sellapp.R;
import com.example.sellapp.retrofit.RetrofitClient;
import com.example.sellapp.retrofit.SellApi;
import com.example.sellapp.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText email, password, confirm_password;
    AppCompatButton resetBtn;
    SellApi sellApi;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
        initControl();
    }

    private void initControl() {
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = email.getText().toString().trim();
                String str_password = password.getText().toString().trim();
                String str_confirm_password = confirm_password.getText().toString().trim();
                if(TextUtils.isEmpty(str_email)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập email", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(str_password)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa mật khẩu", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(str_confirm_password)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa xác nhận mật khẩu", Toast.LENGTH_LONG).show();
                } else {
                    if(str_password.equals(str_confirm_password)) {
                        compositeDisposable.add(sellApi.reset(str_email, str_password)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        userModel -> {
                                            if(userModel.isSuccess()) {
                                                Utils.user.setEmail(str_email);
                                                Utils.user.setPassword(str_password);
                                                Toast.makeText(getApplicationContext(), "Thành công", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        },
                                        throwable -> {
                                            Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra kết nối mạng", Toast.LENGTH_LONG).show();
                                        }
                                ));
                    } else {
                        Toast.makeText(getApplicationContext(), "Mật khẩu chưa khớp", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void initView() {
        email = findViewById(R.id.email_input_reset);
        resetBtn = findViewById(R.id.reset_btn);
        password = findViewById(R.id.password_input_reset);
        confirm_password = findViewById(R.id.confirm_password_input_reset);
        sellApi = RetrofitClient.getInstance(Utils.BASE_URL).create(SellApi.class);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}