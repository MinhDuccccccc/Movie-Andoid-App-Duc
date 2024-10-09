//list phim khi ta ấn vôcasii phimle, phim bộ,...
package com.example.movieapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.movieapp.Domain.TheLoaiPhim;
import com.example.movieapp.R;
import com.example.movieapp.activities.DetailActivity;

import java.util.List;

public class TheLoaiPhimAdapter extends RecyclerView.Adapter<TheLoaiPhimAdapter.ItemViewHolder> {

    private List<TheLoaiPhim> filmList;
    private Context context;

    public TheLoaiPhimAdapter(Context context,List<TheLoaiPhim> filmList) {
        this.context = context;
        this.filmList = filmList;
    }

    @NonNull
    @Override
    public TheLoaiPhimAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_horizontal_film, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheLoaiPhimAdapter.ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TheLoaiPhim film = filmList.get(position);
        holder.txtName.setText(film.getName());
        holder.txtOriginName.setText(film.getOriginName());
        RequestOptions requestOptions = new RequestOptions() ;
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(30));


        String imageUrl = "https://phimimg.com/" + film.getPosterUrl();


        Glide.with(context)
                .load(imageUrl)
                .apply(requestOptions)
                .into(holder.image);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slug = filmList.get(position).getSlug();

                // Log the slug
                Log.d("Slug", "Slug: " + slug);

                // Pass the ID to the next activity
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                intent.putExtra("slug", slug);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filmList.size();
    }

    public void updateData(List<TheLoaiPhim> newFilmList) {
        this.filmList.clear();
        this.filmList.addAll(newFilmList);
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtOriginName;
        ImageView image;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.name);
            txtOriginName = itemView.findViewById(R.id.originName);
            image = itemView.findViewById(R.id.imageView);
        }
    }
}
