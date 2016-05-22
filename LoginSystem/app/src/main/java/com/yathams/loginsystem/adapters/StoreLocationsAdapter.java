package com.yathams.loginsystem.adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yathams.loginsystem.R;
import com.yathams.loginsystem.databinding.StoreItemLayoutBinding;
import com.yathams.loginsystem.model.StoreLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vyatham on 18/03/16.
 */
public class StoreLocationsAdapter extends RecyclerView.Adapter<StoreLocationsAdapter.ViewHolder>{

    private Activity mActivity;
    private List<StoreLocation> stores = new ArrayList<>();

    public StoreLocationsAdapter(Activity mActivity, List<StoreLocation> stores) {
        this.mActivity = mActivity;
        this.stores = stores;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StoreItemLayoutBinding binding = DataBindingUtil.inflate(mActivity.getLayoutInflater(), R.layout.store_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final StoreLocation store = stores.get(position);
        holder.binding.setStore(store);
        holder.binding.storeItemContainer.setBackgroundColor(stores.get(position).isSelected?Color.BLUE:Color.WHITE);
        holder.binding.storeItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                store.isSelected = !store.isSelected;
                v.setBackgroundColor(store.isSelected?Color.BLUE:Color.WHITE);
            }

        });

    }

    @Override
    public int getItemCount() {
        return stores.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        StoreItemLayoutBinding binding;
        public ViewHolder(StoreItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
