package com.yathams.loginsystem.adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.yathams.loginsystem.R;
import com.yathams.loginsystem.databinding.ImagesItemLayoutBinding;
import com.yathams.loginsystem.pojo.ImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vyatham on 17/03/16.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private List<ImageItem> imageItemList = new ArrayList<>();
    private Activity mActivity;

    public ImagesAdapter(Activity mActivity, List<ImageItem> imageItemList) {
        this.mActivity = mActivity;
        this.imageItemList = imageItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImagesItemLayoutBinding binding = DataBindingUtil.inflate(mActivity.getLayoutInflater(), R.layout.images_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(imageItemList.get(position).bitmap != null)
        holder.binding.imageViewItem.setImageBitmap(imageItemList.get(position).bitmap);
//        Picasso.with(mActivity).load(imageItemList.get(position).uri).into();
        else
        Picasso.with(mActivity).load(imageItemList.get(position).uri).resize(120, 120).centerCrop().into(holder.binding.imageViewItem);
//                .resize(320, 100).centerCrop().into(holder.binding.imageViewItem);

//        Picasso.with(mActivity).load(new File(imageItemList.get(position).uri.toString())).resize(120, 120).centerCrop().into(holder.binding.imageViewItem);
//        holder.binding.imageViewItem.setImageURI(imageItemList.get(position).uri);
    }

    @Override
    public int getItemCount() {
        return imageItemList.size();
    }

    public void addImage(ImageItem imageItem) {
        imageItemList.add(imageItem);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImagesItemLayoutBinding binding;
        public ViewHolder(ImagesItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
