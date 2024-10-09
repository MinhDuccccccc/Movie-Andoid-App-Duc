//đây là activity của list film khi ta chọn phim bô, phim lẻ,....
package com.example.movieapp.activities;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Domain.TheLoaiPhim;
import com.example.movieapp.R;
import com.example.movieapp.adapters.TheLoaiPhimAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.internal.Ref;

public class FilmListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TheLoaiPhimAdapter adapter;
    private ProgressBar loading;
    private List<TheLoaiPhim> filmList;
    private RequestQueue requestQueue;
    private TextView textViewCurrentPage, listFilm;
    private String apiUrl;

    private int currentPage = 1;
    private Button nextButton, previousButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_list);

        apiUrl = getIntent().getStringExtra("apiUrl");
        recyclerView = findViewById(R.id.listPhimRecycleView);
        loading = findViewById(R.id.progressBarListFilm);
        textViewCurrentPage = findViewById(R.id.textViewCurrentPage);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);
        listFilm = findViewById(R.id.listFilm);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        filmList = new ArrayList<>();
        adapter = new TheLoaiPhimAdapter(this, filmList); // Sử dụng 'this' làm context
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchFilmData(currentPage);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage++;
                fetchFilmData(currentPage);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage > 1){
                    currentPage--;
                    fetchFilmData(currentPage);
                }else{
                    currentPage = 1;
                    Toast.makeText(FilmListActivity.this, "You are on the first page", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchFilmData(int page) {
        String apiWithPage = apiUrl + "?page=" + page;
        // Tạo một yêu cầu StringRequest từ API
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,        // Phương thức HTTP GET
                apiWithPage,              // URL API được nhận từ Intent
                response -> {
                    // Xử lý dữ liệu phản hồi từ API
                    Gson gson = new Gson();

                    // Tạo một đối tượng JsonObject để phân tích dữ liệu
                    JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

                    // Lấy trường "data" từ JsonObject
                    JsonObject dataObject = jsonObject.getAsJsonObject("data");

                    // Lấy trường "items" từ đối tượng data
                    JsonArray itemsArray = dataObject.getAsJsonArray("items");

                    // Chỉ định kiểu danh sách phim bạn muốn phân tích
                    Type filmListType = new TypeToken<List<TheLoaiPhim>>(){}.getType();

                    // Lấy thông tin trang hiện tại từ "pagination"
                    JsonObject paginationObject = dataObject.getAsJsonObject("params")
                            .getAsJsonObject("pagination");

                    int currentPage = paginationObject.get("currentPage").getAsInt();

                    // Lấy thông tin về listFilm (Film lẻ, film bộ,....)
                    String theLoai_film = dataObject.get("titlePage").getAsString();
                    listFilm.setText("Danh sách: " + theLoai_film);
                    Log.d("API_RESPONSE", dataObject.toString());

                    // Phân tích chuỗi JSON thành danh sách phim
                    List<TheLoaiPhim> filmList = gson.fromJson(itemsArray, filmListType);

                    // Cập nhật dữ liệu cho adapter và ẩn ProgressBar
                    this.runOnUiThread(() -> {
                        loading.setVisibility(View.GONE);
                        textViewCurrentPage.setText("Trang: " + currentPage);
                        adapter.updateData(filmList); // Cập nhật dữ liệu cho adapter
                    });
                },
                error -> {
                    this.runOnUiThread(() -> {
                        if (loading != null) {
                            loading.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(FilmListActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    });
                }
        );

        // Thiết lập thời gian chờ (timeout)
        int socketTimeout = 100000; // 10 giây
        RetryPolicy policy = new DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Thêm yêu cầu vào hàng đợi
        requestQueue.add(stringRequest);
    }

}
