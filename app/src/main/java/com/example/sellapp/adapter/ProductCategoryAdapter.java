package com.example.sellapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sellapp.R;
import com.example.sellapp.model.ProductCategory;

import java.util.List;

public class ProductCategoryAdapter extends BaseAdapter {
    List<ProductCategory> productCategories;
    Context context;

    public ProductCategoryAdapter(List<ProductCategory> productCategories, Context context) {
        this.productCategories = productCategories;
        this.context = context;
    }

    @Override
    public int getCount() {
        return productCategories.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class ViewHolder {
        TextView productNameTxt;
        ImageView productImage;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.product_item, null);
            viewHolder.productNameTxt = view.findViewById(R.id.item_productname);
            viewHolder.productImage = view.findViewById(R.id.item_image);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            viewHolder.productNameTxt.setText(productCategories.get(i).getProductName());
            Glide.with(context).load(productCategories.get(i).getProductImage()).into(viewHolder.productImage);
        }
        return view;
    }
}
