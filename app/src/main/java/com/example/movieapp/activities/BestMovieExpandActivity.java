package com.example.movieapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Domain.Item;
import com.example.movieapp.Domain.ListFilm;
import com.example.movieapp.R;
import com.example.movieapp.adapters.FilmListExpandAdapter;
import com.example.movieapp.network.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BestMovieExpandActivity extends AppCompatActivity {
    private RecyclerView listPhimRecycleViewExpand;
    private FilmListExpandAdapter adapter;
    private ProgressBar progressBarListFilm, progressExpand;
    private RequestQueue mRequestQueue;
    private StringRequest stringRequest1, stringRequest2, requestPage1, requestPage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_best_movie_expand);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy giá trị ngày hiện tại từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int maxPage = preferences.getInt("maxPage", 1500);
        int page = maxPage - 1;




        boolean isConnected = NetworkUtils.checkConnection(this);
        if (!isConnected) {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
        }

        listPhimRecycleViewExpand = findViewById(R.id.listPhimRecycleViewExpand);
        progressExpand = findViewById(R.id.progressExpand);
        progressBarListFilm = findViewById(R.id.progressBarListFilm);

        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        listPhimRecycleViewExpand.setLayoutManager(new GridLayoutManager(this, spanCount));

        //fetch
        fetchData(page);
        listPhimRecycleViewExpand.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (gridLayoutManager != null) {
                    int lastVisibleItemPosition = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = gridLayoutManager.getItemCount();
                    if (lastVisibleItemPosition == totalItemCount - 1) {
                        // Tạo giá trị page ngẫu nhiên
                        Random random = new Random();
                        int pageRandom = random.nextInt(maxPage) + 1;
                        fetchDataExpand(pageRandom);
                    }
                }
            }
        });
    }

    private void fetchData(int page) {
        mRequestQueue = Volley.newRequestQueue(this);
        progressBarListFilm.setVisibility(View.VISIBLE);
        progressExpand.setVisibility(View.GONE);

        ListFilm combinedListFilm = new ListFilm();
        List<Item> combinedFilms = new ArrayList<>();

        // Gọi API page 1
        String urlPage1 = "https://phimapi.com/danh-sach/phim-moi-cap-nhat?page=" + page;
        stringRequest1 = new StringRequest(Request.Method.GET, urlPage1, s -> {
            Gson gson = new Gson();
            ListFilm itemsPage1 = gson.fromJson(s, ListFilm.class);
            combinedFilms.addAll(itemsPage1.getItems());
            int nextPage = page + 1;

            // Gọi API page 2
            String urlPage2 = "https://phimapi.com/danh-sach/phim-moi-cap-nhat?page=" + nextPage;
            stringRequest2 = new StringRequest(Request.Method.GET, urlPage2, s2 -> {
                ListFilm itemsPage2 = gson.fromJson(s2, ListFilm.class);
                combinedFilms.addAll(itemsPage2.getItems());
                combinedListFilm.setItems(combinedFilms);
                adapter = new FilmListExpandAdapter(combinedListFilm);
                listPhimRecycleViewExpand.setAdapter(adapter);
                progressBarListFilm.setVisibility(View.GONE);
            }, volleyError -> {
                Log.i("Notification", "Error fetching page 2: " + volleyError.toString());
            });

            stringRequest2.setRetryPolicy(new DefaultRetryPolicy(100000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(stringRequest2);

        }, volleyError -> {
            Log.i("Notification", "Error fetching page 1: " + volleyError.toString());
        });

        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(100000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest1);
    }

    private void fetchDataExpand(int currentPage) {
        progressExpand.setVisibility(View.VISIBLE);
        String urlPage1 = "https://phimapi.com/danh-sach/phim-moi-cap-nhat?page=" + currentPage;
        requestPage1 = new StringRequest(Request.Method.GET, urlPage1, s -> {
            Gson gson = new Gson();
            ListFilm itemsPage1 = gson.fromJson(s, ListFilm.class);
            if (itemsPage1.getItems() != null && !itemsPage1.getItems().isEmpty()) {
                List<Item> newFilmsPage1 = itemsPage1.getItems();
                adapter.addItems(newFilmsPage1);
            }
        }, volleyError -> {
            Log.i("Notification", "Error fetching page " + currentPage + ": " + volleyError.toString());
        });

        String urlPage2 = "https://phimapi.com/danh-sach/phim-moi-cap-nhat?page=" + (currentPage + 1);
        requestPage2 = new StringRequest(Request.Method.GET, urlPage2, s -> {
            Gson gson = new Gson();
            ListFilm itemsPage2 = gson.fromJson(s, ListFilm.class);
            if (itemsPage2.getItems() != null && !itemsPage2.getItems().isEmpty()) {
                List<Item> newFilmsPage2 = itemsPage2.getItems();
                adapter.addItems(newFilmsPage2);
            }
            progressExpand.setVisibility(View.GONE);
        }, volleyError -> {
            Log.i("Notification", "Error fetching page " + (currentPage + 1) + ": " + volleyError.toString());
        });

        requestPage1.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestPage2.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Thêm các yêu cầu vào hàng đợi
        mRequestQueue.add(requestPage1);
        mRequestQueue.add(requestPage2);
    }
}