package com.example.movieapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class fragment1 extends Fragment {

    private ImageView poster, btnBack, btnFav;
    private TextView movieName, tinhTrang, soTap, thoiLuong, namSX, chatLuong, ngonNgu, daoDien, dienVien, theLoai, quocGia;
    private Button btnPlay;
    private NestedScrollView nestedScView;
    private ProgressBar progressBar;

    private String slugFilm;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    // Phương thức newInstance để nhận dữ liệu từ Activity
    public static fragment1 newInstance(String slug) {
        fragment1 fragment = new fragment1();
        Bundle args = new Bundle();
        args.putString("slug", slug); // Đưa slug vào bundle
        fragment.setArguments(args);  // Gắn bundle vào fragment
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy slug từ Bundle
        if (getArguments() != null) {
            slugFilm = getArguments().getString("slug");
        }

        // Ánh xạ các thành phần giao diện
        btnBack = view.findViewById(R.id.btnBack);
        btnFav = view.findViewById(R.id.btnFav);
        btnPlay = view.findViewById(R.id.btnPlay);

        poster = view.findViewById(R.id.poster);
        movieName = view.findViewById(R.id.movieName);
        tinhTrang = view.findViewById(R.id.tinhTrang);

        soTap = view.findViewById(R.id.soTap);
        thoiLuong = view.findViewById(R.id.thoiLuong);
        namSX = view.findViewById(R.id.namSX);

        chatLuong = view.findViewById(R.id.chatLuong);
        ngonNgu = view.findViewById(R.id.ngonNgu);
        daoDien = view.findViewById(R.id.daoDien);

        dienVien = view.findViewById(R.id.dienVien);
        theLoai = view.findViewById(R.id.theLoai);
        quocGia = view.findViewById(R.id.quocGia);
        nestedScView = view.findViewById(R.id.nestedScView);
        progressBar = view.findViewById(R.id.progressBar_summary);

        btnBack.setOnClickListener(v -> getActivity().finish());

        btnPlay.setOnClickListener(v -> {
            if (getActivity() instanceof DetailActivity) {
                ((DetailActivity) getActivity()).switchToFragment(1); // Chuyển sang fragment2
            }
        });

        if (slugFilm != null) {
            sendRequest();
        }
    }

    @SuppressLint("SuspiciousIndentation")
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

            // Xử lý dữ liệu từ response
            FilmItem filmItem = gson.fromJson(response, FilmItem.class);
            Glide.with(this)
                    .load(filmItem.getMovie().getThumbUrl())
                    .into(poster);

            movieName.setText(filmItem.getMovie().getName());
            tinhTrang.setText(filmItem.getMovie().getStatus().equals("completed") ? "Hoàn tất" : "Đang diễn ra");
            soTap.setText(filmItem.getMovie().getEpisodeTotal());
            thoiLuong.setText(filmItem.getMovie().getTime());
            namSX.setText(String.valueOf(filmItem.getMovie().getYear()));
            chatLuong.setText(filmItem.getMovie().getQuality());
            ngonNgu.setText(filmItem.getMovie().getLang());

            // Xử lý đạo diễn
            List<String> director = filmItem.getMovie().getDirector();
            if (director != null && !director.isEmpty()) {
                daoDien.setText(String.join(", ", director));
            }

            // Xử lý diễn viên
            List<String> actor = filmItem.getMovie().getActor();
            if (actor != null && !actor.isEmpty()) {
                dienVien.setText(String.join(", ", actor));
            }

            // Xử lý thể loại
            List<Category> categories = filmItem.getMovie().getCategory();
            if (categories != null && !categories.isEmpty()) {
                List<String> categoryName = new ArrayList<>();
                for (Category category : categories) {
                    categoryName.add(category.getName());
                }
                theLoai.setText(String.join(", ", categoryName));
            }

            // Xử lý quốc gia
            List<Country> countries = filmItem.getMovie().getCountry();
            if (countries != null && !countries.isEmpty()) {
                List<String> countryName = new ArrayList<>();
                for (Country country : countries) {
                    countryName.add(country.getName());
                }
                quocGia.setText(String.join(", ", countryName));
            }

        }, volleyError -> {
            // Xử lý lỗi từ API
            Log.e("API Error", "Error: " + volleyError.getMessage());
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
