package com.example.movieapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.movieapp.fragment.fragment1;
import com.example.movieapp.fragment.fragment2;
import com.example.movieapp.fragment.fragment3;

public class filmPagerAdapter extends FragmentStateAdapter {

    private final String slugFilm; // Biến chứa slug từ DetailActivity

    // Truyền slugFilm từ DetailActivity khi khởi tạo adapter
    public filmPagerAdapter(@NonNull FragmentActivity fa, String slugFilm) {
        super(fa);
        this.slugFilm = slugFilm;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return fragment2.newInstance(slugFilm);
            case 2:
                // Truyền slugFilm đến fragment1 thông qua newInstance
                return fragment3.newInstance(slugFilm);
            default:
                // Truyền slugFilm đến fragment1 thông qua newInstance
                return fragment1.newInstance(slugFilm);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
