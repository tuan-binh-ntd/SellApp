package com.example.sellapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sellapp.R;
import com.example.sellapp.retrofit.RetrofitClient;
import com.example.sellapp.retrofit.SellApi;
import com.example.sellapp.utils.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    TextView registerTxt, resetPassword;
    EditText email, password;
    AppCompatButton loginBtn;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    SellApi sellApi;
    //boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initControl();
    }

    private void initControl() {
        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set sự kiện cho nút đăng ký
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // thiết lập sự kiện cho nút quên mật khẩu
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailInput = email.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                if(TextUtils.isEmpty(emailInput)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập email", Toast.LENGTH_LONG).show();
                } else if(TextUtils.isEmpty(passwordInput)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_LONG).show();
                } else {
                    //save
                    Paper.book().write("email", emailInput);
                    Paper.book().write("password", passwordInput);

                    login(emailInput, passwordInput);
                }

            }
        });
    }

    private void initView() {
        Paper.init(this);
        // khởi tạo các
        registerTxt = findViewById(R.id.register_txt);
        email = findViewById(R.id.email_input_login);
        password = findViewById(R.id.password_input_login);
        loginBtn = findViewById(R.id.login_btn);
        sellApi = RetrofitClient.getInstance(Utils.BASE_URL).create(SellApi.class);
        resetPassword = findViewById(R.id.reset_password);
        //read data
        if(Paper.book().read("email") != null && Paper.book().read("password") != null) {
            email.setText(Paper.book().read("email"));
            password.setText(Paper.book().read("password"));
            if(Paper.book().read("isLogin") != null) {
                boolean flag = Boolean.TRUE.equals(Paper.book().read("isLogin"));
                if (flag) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //login(Paper.book().read("email"), Paper.book().read("password"));
                        }
                    }, 1000);
                }
            }
        }
    }

    private void login(String emailInput, String passwordInput) {
        compositeDisposable.add(sellApi.login(emailInput, passwordInput)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()) {
                                //isLogin = true;
                                Paper.book().write("isLogin", passwordInput);
                                Utils.user = userModel.getResult().get(0);
                                // luu lai thong tin nguoi dung
                                Paper.book().write("user", userModel.getResult().get(0));
                                Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra kết nối mạng", Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.user.getEmail() != null && Utils.user.getPassword() != null) {
            email.setText(Utils.user.getEmail());
            password.setText((Utils.user.getPassword()));
        }
    }
}