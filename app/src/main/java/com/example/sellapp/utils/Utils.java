package com.example.sellapp.utils;

import com.example.sellapp.model.Cart;
import com.example.sellapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String BASE_URL = "http://192.168.0.107:8080/banhang/";
    public static List<Cart> cartList;
    public static List<Cart> boughtList = new ArrayList<>();
    public static User user = new User();
}
