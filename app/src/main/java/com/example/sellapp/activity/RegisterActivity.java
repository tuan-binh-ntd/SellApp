package com.example.sellapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sellapp.R;
import com.example.sellapp.retrofit.RetrofitClient;
import com.example.sellapp.retrofit.SellApi;
import com.example.sellapp.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity {
    EditText email, password, confirmPassword, phone, username;
    AppCompatButton registerBtn;
    SellApi sellApi;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initControl();
    }

    private void initControl() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }

        });
    }

    private void register() {
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String confirmPasswordInput = confirmPassword.getText().toString().trim();
        String phoneInput = phone.getText().toString().trim();
        String usernameInput = username.getText().toString().trim();

        if(TextUtils.isEmpty(emailInput)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập email", Toast.LENGTH_LONG).show();
        } else if(TextUtils.isEmpty(passwordInput)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_LONG).show();

        } else if(TextUtils.isEmpty(confirmPasswordInput)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa xác nhận mật khẩu", Toast.LENGTH_LONG).show();

        } else if(TextUtils.isEmpty(usernameInput)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập tên", Toast.LENGTH_LONG).show();

        } else if(TextUtils.isEmpty(phoneInput)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại", Toast.LENGTH_LONG).show();
        } else {
            if(passwordInput.equals(confirmPasswordInput)) {
                // post data
                compositeDisposable.add(sellApi.register(emailInput, passwordInput, usernameInput, phoneInput)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                userModel -> {
                                    if(userModel.isSuccess()) {
                                        // đăng ký thành công thì lưu vào user
                                        Utils.user.setEmail(emailInput);
                                        Utils.user.setPassword(passwordInput);
                                        Toast.makeText(getApplicationContext(), "Thành công", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                },
                                throwable -> {
                                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                }
                        ));
            }
            else {
                Toast.makeText(getApplicationContext(), "Mật khẩu chưa khớp", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initView() {
        email = findViewById(R.id.email_input_register);
        password = findViewById(R.id.password_input_register);
        confirmPassword = findViewById(R.id.confirm_password_input_register);
        registerBtn = findViewById(R.id.register_btn);
        phone = findViewById(R.id.phone_input_register);
        username = findViewById(R.id.username_input_register);
        sellApi = RetrofitClient.getInstance(Utils.BASE_URL).create(SellApi.class);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}