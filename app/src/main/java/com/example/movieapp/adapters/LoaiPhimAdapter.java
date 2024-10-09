//adapter của cái loại phim gồm 4 cái ấy
package com.example.movieapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Domain.ListFilm;
import com.example.movieapp.Domain.LoaiPhim;
import com.example.movieapp.R;
import com.example.movieapp.activities.FilmListActivity;

import java.util.List;

public class LoaiPhimAdapter extends RecyclerView.Adapter<LoaiPhimAdapter.ItemViewHolder> {
    List<LoaiPhim> itemList;
    Context context;  // Đảm bảo context được khởi tạo

    // Hàm khởi tạo với Context
    public LoaiPhimAdapter(Context context, List<LoaiPhim> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public LoaiPhimAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_film, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoaiPhimAdapter.ItemViewHolder holder, int position) {
        LoaiPhim loaiPhim = itemList.get(position);
        holder.titleTextView.setText(loaiPhim.getTitle());
        holder.imageView.setImageResource(loaiPhim.getThumb());

        String apiUrl = "";
        switch (loaiPhim.getType()){
            case "phim-le":
                apiUrl = "https://phimapi.com/v1/api/danh-sach/phim-le";
                break;
            case "phim-bo":
                apiUrl = "https://phimapi.com/v1/api/danh-sach/phim-bo";
                break;
            case "hoat-hinh":
                apiUrl = "https://phimapi.com/v1/api/danh-sach/hoat-hinh";
                break;
            case "tv-shows":
                apiUrl = "https://phimapi.com/v1/api/danh-sach/tv-shows";
                break;
        }

        String finalApiUrl = apiUrl;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FilmListActivity.class);
                intent.putExtra("apiUrl", finalApiUrl);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTxt);
            imageView = itemView.findViewById(R.id.pic);
        }
    }
}

