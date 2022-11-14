package com.example.sellapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sellapp.R;
import com.example.sellapp.model.NewProduct;

import java.text.DecimalFormat;
import java.util.List;

public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.MyViewHolder> {
    Context context;

    public NewProductAdapter(Context context, List<NewProduct> newProductList) {
        this.context = context;
        this.newProductList = newProductList;
    }

    List<NewProduct> newProductList;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_product_item, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NewProduct newProduct = newProductList.get(position);
        holder.nameTxt.setText(newProduct.getTensp());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.priceTxt.setText("Giá: " + decimalFormat.format(Double.parseDouble(newProduct.getGiasp())) + "Đ");
        Glide.with(context).load(newProduct.getHinhanh()).into(holder.productImg);
    }

    @Override
    public int getItemCount() {
        return newProductList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView priceTxt, nameTxt;
        ImageView productImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.item_product_name);
            priceTxt = itemView.findViewById(R.id.item_product_price);
            productImg = itemView.findViewById(R.id.item_product_image);
        }
    }
}
