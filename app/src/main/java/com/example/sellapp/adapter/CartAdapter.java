package com.example.sellapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sellapp.Interface.ImageClickListener;
import com.example.sellapp.R;
import com.example.sellapp.model.Cart;
import com.example.sellapp.model.eventbus.TotalMoneyCalc;
import com.example.sellapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    Context context;
    List<Cart> cartList;

    public CartAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        holder.item_cart_name.setText(cart.getProductName());
        holder.item_cart_quantity.setText(cart.getQuantity() + "");
        Glide.with(context).load(cart.getImg()).into(holder.item_cart_img);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.item_cart_cost.setText("Giá: " + decimalFormat.format(cart.getCost()));
        long total = cart.getCost() * cart.getQuantity();
        holder.item_cart_total.setText(decimalFormat.format(total) + "Đ");
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    Utils.boughtList.add(cart);
                    EventBus.getDefault().postSticky(new TotalMoneyCalc());
                } else {
                    for (int i = 0; i < Utils.boughtList.size(); i++) {
                        if(Utils.boughtList.get(i).getProductId() == cart.getProductId()) {
                            Utils.boughtList.remove(i);
                            EventBus.getDefault().postSticky(new TotalMoneyCalc());
                        }
                    }
                }
            }
        });
        holder.setListener(new ImageClickListener() {
            @Override
            public void onImageClick(View view, int position, int value) {
                if (value == 1) {
                    if (cartList.get(position).getQuantity() > 1) {
                        int newQuantity = cartList.get(position).getQuantity() - 1;
                        cartList.get(position).setQuantity(newQuantity);

                        holder.item_cart_quantity.setText(cartList.get(position).getQuantity() + " ");
                        long total = cartList.get(position).getCost() * cartList.get(position).getQuantity();
                        holder.item_cart_total.setText(decimalFormat.format(total) + "Đ");
                        EventBus.getDefault().postSticky(new TotalMoneyCalc());
                    } else if (cartList.get(position).getQuantity() == 1){
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                        builder.setTitle("Thông báo");
                        builder.setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng không?");
                        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.cartList.remove(position);
                                notifyDataSetChanged();
                                EventBus.getDefault().postSticky(new TotalMoneyCalc());
                            }
                        });

                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();

                    }
                } else if (value == 2) {
                    if(cartList.get(position).getQuantity() < 11) {
                        int newQuantity = cartList.get(position).getQuantity() + 1;
                        cartList.get(position).setQuantity(newQuantity);
                    }
                }
                holder.item_cart_quantity.setText(cartList.get(position).getQuantity() + " ");
                long total = cartList.get(position).getCost() * cartList.get(position).getQuantity();
                holder.item_cart_total.setText(decimalFormat.format(total) + "Đ");
                EventBus.getDefault().postSticky(new TotalMoneyCalc());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_cart_img, item_cart_plus, item_cart_minus;
        TextView item_cart_name, item_cart_cost, item_cart_quantity, item_cart_total;
        ImageClickListener listener;
        CheckBox checkBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_cart_img = itemView.findViewById(R.id.item_cart_img);
            item_cart_name = itemView.findViewById(R.id.item_cart_name);
            item_cart_cost = itemView.findViewById(R.id.item_cart_cost);
            item_cart_quantity = itemView.findViewById(R.id.item_cart_quantity);
            item_cart_total = itemView.findViewById(R.id.item_cart_total);
            item_cart_plus = itemView.findViewById(R.id.item_cart_plus);
            item_cart_minus = itemView.findViewById(R.id.item_cart_minus);
            checkBox = itemView.findViewById(R.id.item_cart_check);
            // click event
            item_cart_plus.setOnClickListener(this);
            item_cart_minus.setOnClickListener(this);
        }

        public void setListener(ImageClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            if(view == item_cart_minus) {
                listener.onImageClick(view, getAdapterPosition(), 1);
            } else if (view == item_cart_plus) {
                listener.onImageClick(view, getAdapterPosition(), 2);
            }
        }
    }
}
