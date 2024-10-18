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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.movieapp.Domain.Item;
import com.example.movieapp.Domain.ListFilm;
import com.example.movieapp.R;
import com.example.movieapp.activities.DetailActivity;

import java.util.List;

public class FilmListExpandAdapter extends RecyclerView.Adapter<FilmListExpandAdapter.ViewHolder> {
    private ListFilm items;
    private Context context;

    public FilmListExpandAdapter(ListFilm items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FilmListExpandAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_film, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmListExpandAdapter.ViewHolder holder, int position) {
        holder.titleTxt.setText(items.getItems().get(position).getName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(30));

        Glide.with(context)
                .load(items.getItems().get(position).getThumbUrl())
                .apply(requestOptions)
                .into(holder.pic);

        holder.itemView.setOnClickListener(view -> {
            String slug = items.getItems().get(position).getSlug();
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("slug", slug);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.getItems().size();
    }

    public void addItems(List<Item> newFilms) {
        int startPosition = items.getItems().size();
        items.getItems().addAll(newFilms);
        notifyItemRangeInserted(startPosition, newFilms.size());
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
