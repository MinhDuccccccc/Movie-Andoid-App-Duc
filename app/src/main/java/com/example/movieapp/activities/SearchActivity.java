package com.example.movieapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.R;
import com.example.movieapp.adapters.FilmDTOSearchAdapter;
import com.example.movieapp.adapters.FilmdtoAdapter;
import com.example.movieapp.dto.FilmDTO;
import com.example.movieapp.network.NetworkUtils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText editTextSearch;
    private TextView textViewKey;
    private RecyclerView listPhimSearch, recyclerViewSuggestions;
    private List<FilmDTO> filmDTO_Suggestion, filmDTO_Result;
    private FilmDTOSearchAdapter adapter;
    private FilmdtoAdapter adapterSearchSuggestion;
    private RequestQueue mRequestQueue;
    private ProgressBar progressBarListFilmSearch;
    // Khởi tạo Handler cho debounce
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boolean isConnected = NetworkUtils.checkConnection(this);
        if (!isConnected) {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
        }

        Intent intent = getIntent();
        String keySearch = intent.getStringExtra("keySearch");

        // Ẩn bàn phím khi Activity được tạo
        hideKeyboard();

        // init
        editTextSearch = findViewById(R.id.editTextSearch);
        textViewKey = findViewById(R.id.textViewKey);
        progressBarListFilmSearch = findViewById(R.id.progressBarListFilmSearch);

        //search suggestion
        recyclerViewSuggestions = findViewById(R.id.recyclerViewSuggestions);
        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(this));
        filmDTO_Suggestion = new ArrayList<>();
        // Thêm DividerItemDecoration
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewSuggestions.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerViewSuggestions.addItemDecoration(dividerItemDecoration);
        adapterSearchSuggestion = new FilmdtoAdapter(this, filmDTO_Suggestion);
        recyclerViewSuggestions.setAdapter(adapterSearchSuggestion);


        //search result
        listPhimSearch = findViewById(R.id.listPhimSearch);
        filmDTO_Result = new ArrayList<>();
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        listPhimSearch.setLayoutManager(new GridLayoutManager(this, spanCount));
        adapter = new FilmDTOSearchAdapter(this, filmDTO_Result);
        listPhimSearch.setAdapter(adapter);

        if (keySearch != null && !keySearch.isEmpty()) {
            fetchFilm(keySearch);
        }

        //scroll recycleView and hide suggestion search
        listPhimSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Kiểm tra nếu người dùng đang cuộn theo chiều dọc
                if (dy != 0) {
                    clearSearchSuggestion();
                }
            }
        });



        //enter or search in editext
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {


                    String keySearch = editTextSearch.getText().toString().trim();

                    if (!TextUtils.isEmpty(keySearch)) {

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0); // Ẩn bàn phím

                        Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                        intent.putExtra("keySearch", keySearch);
                        startActivity(intent);
                        finish();
                    }
                    return true;
                }
                return false;
            }
        });





        //fetch data and show recycleView suggestion
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nếu có runnable đang chờ, xóa nó đi
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    if (charSequence.length() > 0) {
                        recyclerViewSuggestions.setVisibility(View.VISIBLE);

                        fetchFilmSuggestion(charSequence.toString());
                    } else {
                        filmDTO_Suggestion.clear();
                        adapterSearchSuggestion.notifyDataSetChanged();
                        recyclerViewSuggestions.setVisibility(View.GONE);
                    }
                };

                // Gọi runnable sau 100ms
                searchHandler.postDelayed(searchRunnable, 100);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearSearchSuggestion();
    }

    private void clearSearchSuggestion() {
        editTextSearch.clearFocus();
        recyclerViewSuggestions.setVisibility(View.GONE);
    }

    private void fetchFilmSuggestion(String keyword) {
        mRequestQueue = Volley.newRequestQueue(this);
        String url = "https://phimapi.com/v1/api/tim-kiem?keyword=" + keyword;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Xử lý dữ liệu trả về ở đây
                        JSONObject data = response.getJSONObject("data");
                        JSONArray items = data.getJSONArray("items");

                        // Tạo danh sách tạm thời để lưu FilmDTOs
                        List<FilmDTO> newFilmDTOs = new ArrayList<>();

                        // Duyệt qua từng item trong items
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject film = items.getJSONObject(i);
                            String name = film.getString("name");
                            String originName = film.getString("origin_name");
                            String slug = film.getString("slug");

                            // Tạo FilmDTO và thêm vào danh sách tạm
                            FilmDTO filmDTO = new FilmDTO(name, slug, originName, null, null);
                            newFilmDTOs.add(filmDTO);
                        }

                        filmDTO_Suggestion.clear();
                        filmDTO_Suggestion.addAll(newFilmDTOs);

                        adapterSearchSuggestion.notifyDataSetChanged();

                        if (filmDTO_Suggestion.isEmpty()) {
                            recyclerViewSuggestions.setVisibility(View.GONE);
                        } else {
                            recyclerViewSuggestions.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                });

        mRequestQueue.add(jsonObjectRequest);
    }


    private void fetchFilm(String keyword) {
        String url = "https://phimapi.com/v1/api/tim-kiem?keyword=" + keyword+"&limit=1000";
        mRequestQueue = Volley.newRequestQueue(this);
        progressBarListFilmSearch.setVisibility(View.VISIBLE);

        textViewKey.setText("Search results: " + keyword);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject data = response.getJSONObject("data");
                JSONArray items = data.getJSONArray("items");

                List<FilmDTO> newFilmDTOs = new ArrayList<>();

                for(int i = 0; i< items.length(); i++){
                    JSONObject film = items.getJSONObject(i);
                    String name = film.getString("name");
                    String originName = film.getString("origin_name");
                    String slug = film.getString("slug");
                    String poster_url = film.getString("poster_url");
                    String thumb_url = film.getString("thumb_url");

                    FilmDTO filmDTO = new FilmDTO(name, slug, originName, poster_url, thumb_url);
                    newFilmDTOs.add(filmDTO);
                }

                // Cập nhật danh sách filmDTOs
                filmDTO_Result.clear();
                filmDTO_Result.addAll(newFilmDTOs);
                progressBarListFilmSearch.setVisibility(View.GONE);

                adapter.notifyDataSetChanged();

                if (filmDTO_Result.isEmpty()) {
                    listPhimSearch.setVisibility(View.GONE);
                } else {
                    listPhimSearch.setVisibility(View.VISIBLE);
                    clearSearchSuggestion();
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        });
        mRequestQueue.add(jsonObjectRequest);
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus(); // Lấy View hiện tại
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}