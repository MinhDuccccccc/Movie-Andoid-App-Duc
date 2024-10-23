//màn hình chính của film
package com.example.movieapp.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Domain.FilmItem;
import com.example.movieapp.Domain.GenresItem;
import com.example.movieapp.Domain.ListFilm;
import com.example.movieapp.Domain.LoaiPhim;
import com.example.movieapp.R;
import com.example.movieapp.adapters.CategoryListAdapter;
import com.example.movieapp.adapters.FilmListAdapter;
import com.example.movieapp.adapters.FilmdtoAdapter;
import com.example.movieapp.adapters.LoaiPhimAdapter;
import com.example.movieapp.adapters.SliderAdapters;
import com.example.movieapp.Domain.SliderItems;
import com.example.movieapp.dto.FilmDTO;
import com.example.movieapp.network.NetworkUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class DashboardActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;

    private FilmListAdapter adapterBestMovies, adapterUpcoming;
    private CategoryListAdapter adapterCategory;
    private RecyclerView recyclerViewBestMovies, recyclerViewCategory, recyclerViewUpcoming, recyclerViewLoaiPhim;
    private LoaiPhimAdapter loaiPhimAdapter;
    private List<LoaiPhim> itemList;
    private TextView upComingDetail, bestMoviesDetail;


    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest, mStringRequest2, mStringRequest3, mStringRequest4;
    private ProgressBar loading, loading2, loading3, loading4, loading5;
    private ViewPager2 viewPager2;
    private Handler slideHandler = new Handler();
    private boolean isConnected;


    //search Activity
    private EditText editTextSearch;
    private RecyclerView recyclerViewSuggestions;
    private FilmdtoAdapter adapter;
    private List<FilmDTO> filmDTOs; // Danh sách FilmDTO chứa dữ liệu phim
    private LinearLayout mainLayout;
    private ScrollView scrollView;
    // Khởi tạo Handler cho debounce
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        isConnected = NetworkUtils.checkConnection(this);
        if (isConnected) {
            initView ();
            banners();
            sendRequestBestMovies();
            sendRequestUpComing();
            sendRequestCategory();
        } else {
            initView();
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
        }
//        refresh layout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            isConnected = NetworkUtils.checkConnection(DashboardActivity.this);
            initView ();
            banners();
            sendRequestBestMovies();
            sendRequestUpComing();
            sendRequestCategory();
            clearSearchSuggestion();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    //back btn device
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Exit")
                .setMessage("Do you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // If the user chooses "Yes", exit the app
                    finishAffinity();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // If the user chooses "No", just dismiss the dialog
                    dialog.dismiss();
                })
                .setCancelable(true) // Allow the user to dismiss by tapping outside
                .show();
    }



    private void sendRequestUpComing() {
        // Khởi tạo RequestQueue
        mRequestQueue = Volley.newRequestQueue(this);
        loading3.setVisibility(View.VISIBLE);

        String url = "https://phimapi.com/danh-sach/phim-moi-cap-nhat?page=1";
        mStringRequest3 = new StringRequest(Request.Method.GET, url, s -> {
            Gson gson = new Gson();
            loading3.setVisibility(View.GONE);
            ListFilm items = gson.fromJson(s, ListFilm.class);
            adapterUpcoming = new FilmListAdapter(items);
            recyclerViewUpcoming.setAdapter(adapterUpcoming);
        }, volleyError -> {
            loading.setVisibility(View.VISIBLE);
            Log.i("Notification", "Error" + volleyError.toString());
        });

        mStringRequest3.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));


        mRequestQueue.add(mStringRequest3);
    }


    private void sendRequestBestMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading.setVisibility(View.VISIBLE);

        // Lấy giá trị ngày hiện tại từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int maxPage = preferences.getInt("maxPage", 1500); // Mặc định là 1500
        int page = maxPage - 1;

        Log.d("MaxPage", String.valueOf(maxPage));


        mStringRequest = new StringRequest(Request.Method.GET, "https://phimapi.com/danh-sach/phim-moi-cap-nhat?page="+page, s -> {
            Gson gson = new Gson();
            loading.setVisibility(View.GONE);
            ListFilm items = gson.fromJson(s, ListFilm.class);
            adapterBestMovies = new FilmListAdapter(items);
            recyclerViewBestMovies.setAdapter(adapterBestMovies);
        }, volleyError -> {
            loading.setVisibility(View.VISIBLE);
            Log.i("Notification", "Error" + volleyError.toString());
        });


        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        mRequestQueue.add(mStringRequest);


        // Lấy giá trị ngày cuối cùng được lưu trong SharedPreferences
        int lastUpdatedDay = preferences.getInt("lastUpdatedDay", -1);
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);

        if (currentDay != lastUpdatedDay) {
            maxPage++; // Tăng maxPage lên 1
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("maxPage", maxPage);
            editor.putInt("lastUpdatedDay", currentDay);
            editor.apply();
        }


        Log.d("MaxPage", String.valueOf(maxPage));
    }


    private void sendRequestCategory() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading2.setVisibility(View.VISIBLE);
        mStringRequest2 = new StringRequest(Request.Method.GET, "https://phimapi.com/the-loai", s -> {
            Gson gson = new Gson();
            loading2.setVisibility(View.GONE);
            ArrayList<GenresItem> catList = gson.fromJson(s, new TypeToken<ArrayList<GenresItem>>(){
            }.getType());
            adapterCategory = new CategoryListAdapter(catList);
            recyclerViewCategory.setAdapter(adapterCategory);
        }, volleyError -> {
            loading2.setVisibility(View.VISIBLE);
            Log.i("Notification", "Error" + volleyError.toString());
        });


        mStringRequest2.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        mRequestQueue.add(mStringRequest2);
    }

    private void banners() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading5.setVisibility(View.VISIBLE);
        // Lấy giá trị ngày hiện tại từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int maxPage = preferences.getInt("maxPage", 1500);
        Random random = new Random();
        int page = random.nextInt(maxPage) + 1;

        String url = "https://phimapi.com/danh-sach/phim-moi-cap-nhat?page=";


        mStringRequest4 = new StringRequest(Request.Method.GET, url+page, response -> {

            Gson gson = new Gson();
            loading5.setVisibility(View.GONE);
            ListFilm items = gson.fromJson(response, ListFilm.class);


            // Thiết lập adapter cho ViewPager2, nếu không phải là viewPager hoặc là recycleView thì set ảnh được ở ngoài này luôn
            SliderAdapters adapter = new SliderAdapters(items, viewPager2);
            viewPager2.setAdapter(adapter);

        }, volleyError -> {
            // Xử lý lỗi từ API
            Log.i("Notification", "Error" + volleyError.toString());
        });

        mStringRequest4.setRetryPolicy(new DefaultRetryPolicy(
                100000, //
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));


        mRequestQueue.add(mStringRequest4);

        // Các cài đặt khác cho ViewPager2
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.setCurrentItem(1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandler.removeCallbacks(sliderRunnable);
            }
        });

        // Tăng giá trị maxPage mỗi ngày
        long lastUpdated = preferences.getLong("lastUpdated", 0);
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastUpdated > 24*3600*1000){
            maxPage++;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("maxPage", maxPage);
            editor.putLong("lastUpdated", currentTime);
            editor.apply();
        }
    }


    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }


    @Override
    protected void onResume() {
        super.onResume();
        clearSearchSuggestion();
        slideHandler.postDelayed(sliderRunnable, 2000);
    }



    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        viewPager2 = findViewById(R.id.viewPagerSlider);
        recyclerViewBestMovies = findViewById(R.id.view1);
        recyclerViewCategory = findViewById(R.id.view2);
        recyclerViewUpcoming = findViewById(R.id.view3);
        recyclerViewLoaiPhim = findViewById(R.id.view4);
        upComingDetail = findViewById(R.id.upComingDetail);
        bestMoviesDetail = findViewById(R.id.bestMoviesDetail);
//        upcoming detail
        upComingDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, FilmListExpandActivity.class);
                startActivity(intent);
            }
        });
//        best movie detail
        bestMoviesDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, BestMovieExpandActivity.class);
                startActivity(intent);
            }
        });

//        click bottom bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomBar);
        bottomNavigationView.setSelectedItemId(R.id.explore);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.explore) {
                return true;
            } else if (itemId == R.id.favorite) {
                startActivity(new Intent(getApplicationContext(), FavoriteActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.recent) {
                startActivity(new Intent(getApplicationContext(), RecentActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });



        // Ánh xạ các view cho search
        editTextSearch = findViewById(R.id.editTextSearch);
        recyclerViewSuggestions = findViewById(R.id.recyclerViewSuggestions);
        mainLayout = findViewById(R.id.mainActivity);
        scrollView = findViewById(R.id.scrollView);

        filmDTOs = new ArrayList<>();
        adapter = new FilmdtoAdapter(this, filmDTOs);
        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(this));
        // Thêm DividerItemDecoration
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewSuggestions.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerViewSuggestions.addItemDecoration(dividerItemDecoration);
        //set adapter
        recyclerViewSuggestions.setAdapter(adapter);

//        scroll recycleView suggest search and no swipeRefreshLayout
        recyclerViewSuggestions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy != 0) {
                    swipeRefreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    swipeRefreshLayout.setEnabled(true);
                }
            }
        });
        // touchInParentLayout
        mainLayout.setOnTouchListener((v, event) -> {
            clearSearchSuggestion();
            return true;
        });

        //srollView and hide suggestion search
        // >= API 23
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Kiểm tra nếu người dùng đang cuộn
                if (scrollY != oldScrollY) {
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

                        Intent intent = new Intent(DashboardActivity.this, SearchActivity.class);
                        intent.putExtra("keySearch", keySearch);
                        startActivity(intent);
                    }
                    return true;
                }
                return false;
            }
        });




        //fetch data and show in recycleView suggestion
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nếu có runnable đang chờ, xóa nó đi
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Tạo runnable mới
                searchRunnable = () -> {
                    // Kiểm tra nếu từ khóa không rỗng
                    if (charSequence.length() > 0) {
                        recyclerViewSuggestions.setVisibility(View.VISIBLE);

                        fetchFilms(charSequence.toString());
                    } else {
                        filmDTOs.clear();
                        adapter.notifyDataSetChanged();
                        recyclerViewSuggestions.setVisibility(View.GONE);
                    }
                };

                // Gọi runnable sau 100ms
                searchHandler.postDelayed(searchRunnable, 100);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });



        //init recycleView
        recyclerViewBestMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewLoaiPhim.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        loading = findViewById(R.id.progressBar1);
        loading2 = findViewById(R.id.progressBar2);
        loading3 = findViewById(R.id.progressBar3);
        loading4 = findViewById(R.id.progressBar4);
        loading5 = findViewById(R.id.progressBar5);

        //tao danh sach du lieu cho loai phim:
        itemList = new ArrayList<>();
        itemList.add(new LoaiPhim("Phim Lẻ",R.drawable.phimle, "phim-le"));
        itemList.add(new LoaiPhim("Phim Bộ",R.drawable.phimbo, "phim-bo"));
        itemList.add(new LoaiPhim("Hoạt Hình",R.drawable.hoathinh, "hoat-hinh"));
        itemList.add(new LoaiPhim("TV Show",R.drawable.tvshow, "tv-shows"));


        //check connection theloaiphim
        if(isConnected){
            loaiPhimAdapter = new LoaiPhimAdapter(this, itemList);
            recyclerViewLoaiPhim.setAdapter(loaiPhimAdapter);
            loading4.setVisibility(View.GONE);
        }
        else{
            loading4.setVisibility(View.VISIBLE);
        }
    }


    private void fetchFilms(String keyword) {
        mRequestQueue = Volley.newRequestQueue(this);
        String url = "https://phimapi.com/v1/api/tim-kiem?keyword=" + keyword;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
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

                        // Cập nhật danh sách filmDTOs
                        filmDTOs.clear();
                        filmDTOs.addAll(newFilmDTOs);

                        // Cập nhật RecyclerView
                        adapter.notifyDataSetChanged();

                        if (filmDTOs.isEmpty()) {
                            recyclerViewSuggestions.setVisibility(View.GONE);
                        } else {
                            recyclerViewSuggestions.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Xử lý lỗi ở đây
                    error.printStackTrace();
                });

        // Thêm yêu cầu vào hàng đợi
        mRequestQueue.add(jsonObjectRequest);
    }



    private void clearSearchSuggestion() {
        editTextSearch.clearFocus();
        recyclerViewSuggestions.setVisibility(View.GONE);
    }


}