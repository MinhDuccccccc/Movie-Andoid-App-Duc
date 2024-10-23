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
import com.example.movieapp.R;
import com.example.movieapp.activities.DetailActivity;
import com.example.movieapp.dto.FilmDTO;

import java.util.List;

public class FilmDTOSearchAdapter extends RecyclerView.Adapter<FilmDTOSearchAdapter.FilmViewHolder> {
    private final Context context;
    private final List<FilmDTO> films;

    public FilmDTOSearchAdapter(Context context, List<FilmDTO> films) {
        this.context = context;
        this.films = films;
    }

    @NonNull
    @Override
    public FilmDTOSearchAdapter.FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_film_search, parent, false);
        return new FilmDTOSearchAdapter.FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmDTOSearchAdapter.FilmViewHolder holder, int position) {
        FilmDTO film = films.get(position);
        holder.titleName.setText(film.getName());
        holder.titleOriginName.setText(film.getOrigin_name());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(30));

        String urlImage = "https://phimimg.com/"+film.getThumb_url();

        Glide.with(context)
                .load(urlImage)
                .apply(requestOptions)
                .into(holder.pic);

        holder.itemView.setOnClickListener(view -> {
            String slug = film.getSlug();
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("slug", slug);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    public static class FilmViewHolder extends RecyclerView.ViewHolder{
        TextView titleName, titleOriginName;
        ImageView pic;
        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            titleName = itemView.findViewById(R.id.titleName);
            titleOriginName = itemView.findViewById(R.id.titleOriginName);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
