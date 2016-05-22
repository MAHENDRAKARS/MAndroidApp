package com.yathams.loginsystem.adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yathams.loginsystem.R;
import com.yathams.loginsystem.databinding.ProductItemLayoutBinding;
import com.yathams.loginsystem.pojo.ProductItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vyatham on 18/03/16.
 */
public class ProductNamesAdapter extends RecyclerView.Adapter<ProductNamesAdapter.ViewHolder>{

    private Activity mActivity;
    private List<ProductItem> productNames = new ArrayList<>();

    public ProductNamesAdapter(Activity mActivity, List<ProductItem> productNames) {
        this.mActivity = mActivity;
        this.productNames = productNames;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProductItemLayoutBinding binding = DataBindingUtil.inflate(mActivity.getLayoutInflater(), R.layout.product_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.binding.setProduct(productNames.get(position));
        holder.binding.buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productNames.get(position).quantity++;
                notifyDataSetChanged();
            }
        });
        holder.binding.buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productNames.get(position).quantity--;
                notifyDataSetChanged();
            }
        });
        holder.binding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productNames.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productNames.size();
    }

    public void addProduct(ProductItem productItem) {
        productNames.add(productItem);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ProductItemLayoutBinding binding;
        public ViewHolder(ProductItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
