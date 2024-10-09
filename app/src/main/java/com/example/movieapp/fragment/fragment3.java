package com.example.movieapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.movieapp.Domain.Category;
import com.example.movieapp.Domain.Country;
import com.example.movieapp.Domain.FilmItem;
import com.example.movieapp.R;
import com.example.movieapp.activities.DetailActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class fragment3 extends Fragment {
    private ImageView btnBack, btnFav;
    private TextView tvParagraphContent;
    private String slugFilm;

    private ProgressBar progressBar;
    private NestedScrollView nestedScView;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;


    // Phương thức newInstance để nhận dữ liệu từ Activity
    public static fragment3 newInstance(String slug) {
        fragment3 fragment = new fragment3();
        Bundle args = new Bundle();
        args.putString("slug", slug); // Đưa slug vào bundle
        fragment.setArguments(args);  // Gắn bundle vào fragment
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_3, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Lấy slug từ Bundle
        if (getArguments() != null) {
            slugFilm = getArguments().getString("slug");
        }


        // Ánh xạ các thành phần giao diện
        btnBack = view.findViewById(R.id.btnBack);
        btnFav = view.findViewById(R.id.btnFav);
        tvParagraphContent = view.findViewById(R.id.tvParagraphContent);
        nestedScView = view.findViewById(R.id.nestedScView);
        progressBar = view.findViewById(R.id.progressBar_contentFilm);


        btnBack.setOnClickListener(v -> {
            getActivity().finish();
        });



        if (slugFilm != null) {
            sendRequest();
        }
    }

    private void sendRequest() {
        // Khởi tạo RequestQueue với context của Fragment
        mRequestQueue = Volley.newRequestQueue(requireContext());
        nestedScView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        mStringRequest = new StringRequest(Request.Method.GET, "https://phimapi.com/phim/" + slugFilm, response -> {
            // Xử lý phản hồi API
            Gson gson = new Gson();
            nestedScView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            FilmItem filmItem = gson.fromJson(response, FilmItem.class);
            tvParagraphContent.setText(filmItem.getMovie().getContent());
        }, volleyError -> {
            // Xử lý lỗi từ API
            volleyError.printStackTrace();
        });

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000, // Thời gian chờ là 15 giây
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Thêm yêu cầu vào hàng đợi
        mRequestQueue.add(mStringRequest);
    }
}
