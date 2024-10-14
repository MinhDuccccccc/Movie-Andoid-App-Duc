package com.example.movieapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.movieapp.R;
import com.example.movieapp.adapters.filmPagerAdapter;
import com.example.movieapp.network.NetworkUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DetailActivity extends AppCompatActivity {
    private String slugFilm;
    private ImageView poster, btnBack, btnFav;
    private TextView movieName, tinhTrang, soTap, thoiLuong, namSX, chatLuong, ngonNgu, daoDien, dienVien, theLoai, quocGia;
    private Button btnPlay;





    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;
    private filmPagerAdapter filmPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        slugFilm = getIntent().getStringExtra("slug");
        boolean isConnected = NetworkUtils.checkConnection(this);
        if (!isConnected) {
            // Hiển thị thông báo không có kết nối internet
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
        }
        initView();
    }








    private void initView() {

        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);

        filmPagerAdapter = new filmPagerAdapter(this, slugFilm);
        mViewPager.setAdapter(filmPagerAdapter);


        new TabLayoutMediator(mTabLayout, mViewPager, (tab, i) -> {
            switch (i){
                case 0:
                    tab.setText("Tổng quan");
                    break;
                case 1:
                    tab.setText("Xem phim");
                    break;
                case 2:
                    tab.setText("Nội dung phim");
                    break;
            }
        }).attach();


    }

    public void switchToFragment(int fragmentIndex) {
        mViewPager.setCurrentItem(fragmentIndex); // Chuyển đến fragment theo chỉ số
    }
}