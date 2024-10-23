package com.example.movieapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.activities.DetailActivity;
import com.example.movieapp.activities.SearchActivity;
import com.example.movieapp.dto.FilmDTO;

import java.util.List;

public class FilmdtoAdapter extends RecyclerView.Adapter<FilmdtoAdapter.FilmViewHolder> {
    private final Context context;
    private final List<FilmDTO> films;

    public FilmdtoAdapter(Context context, List<FilmDTO> films) {
        this.context = context;
        this.films = films;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_film_suggest, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        // Bind data to the view
        FilmDTO film = films.get(position);
        holder.textViewName.setText(film.getName());
        holder.textViewOriginName.setText(film.getOrigin_name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String slugFilm = film.getSlug();
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("slug", slugFilm);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return films.size();
    }


     public static class FilmViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewOriginName;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewOriginName = itemView.findViewById(R.id.textViewOriginName);
        }
    }
}
