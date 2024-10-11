//banner ở trên đầu ấy
package com.example.movieapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.movieapp.Domain.ListFilm;
import com.example.movieapp.R;
import com.example.movieapp.activities.DetailActivity;


public class SliderAdapters extends RecyclerView.Adapter<SliderAdapters.SliderViewHolder> {

    private ListFilm items ;
    private ViewPager2 viewPager2;
    private Context context;



    public SliderAdapters(ListFilm items, ViewPager2 viewPager2) {
        this.items = items;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderAdapters.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.slide_item_container, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapters.SliderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(60));

        // Sử dụng Glide để tải ảnh từ URL và đặt kích thước cố định
        Log.d("SliderAdapters", "Start loading image at position: " + position);
        Glide.with(context)
                .load(items.getItems().get(position).getThumbUrl().equals(" ") ?
                        items.getItems().get(position).getThumbUrl() :
                        items.getItems().get(position).getPosterUrl())
                .apply(new RequestOptions()
                        .transform(new CenterCrop(), new RoundedCorners(60)))
                .into(holder.imageView);
        Log.d("SliderAdapters", "Finished loading image at position: " + position);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slug = items.getItems().get(position).getSlug();
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                intent.putExtra("slug", slug);
                context.startActivity(intent);
            }
        });

        if (position == items.getItems().size() - 2) {
            viewPager2.post(runnable);
        }
    }


    @Override
    public int getItemCount() {
        return items.getItems().size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            items.getItems().addAll(items.getItems());
            notifyDataSetChanged();
        }
    };
}
