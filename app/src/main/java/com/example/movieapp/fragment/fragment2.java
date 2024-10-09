package com.example.movieapp.fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Domain.Episode;
import com.example.movieapp.Domain.FilmItem;
import com.example.movieapp.Domain.ServerDatum;
import com.example.movieapp.R;
import com.example.movieapp.activities.VideoPlayerActivity;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;

import java.util.List;

public class fragment2 extends Fragment {
    private ImageView btnBack, btnFav;
    private String slugFilm;

    private ProgressBar progressBar;
    private NestedScrollView nestedScView;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;


    public static fragment2 newInstance (String slug){
        fragment2 fragment2 = new fragment2();
        Bundle args = new Bundle();
        args.putString("slug", slug);
        fragment2.setArguments(args);
        return fragment2;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            slugFilm = getArguments().getString("slug");
            Log.d("Slug phim:", slugFilm);
        }


        // Ánh xạ các thành phần giao diện
        btnBack = view.findViewById(R.id.btnBack);
        btnFav = view.findViewById(R.id.btnFav);
        nestedScView = view.findViewById(R.id.nestedScView);
        progressBar = view.findViewById(R.id.progressBar_contentFilm);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        
        if (slugFilm != null){
            sendRequest(view);
        }
    }

    private void sendRequest(View view) {
        mRequestQueue = Volley.newRequestQueue(requireContext());
        nestedScView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        mStringRequest = new StringRequest(Request.Method.GET, "https://phimapi.com/phim/" + slugFilm, s -> {
            Gson gson = new Gson();
            nestedScView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            FilmItem filmItem = gson.fromJson(s, FilmItem.class);

            FlexboxLayout flexboxLayout = view.findViewById(R.id.flexboxLayout);
            List<Episode> episodes = filmItem.getEpisodes();

            if (episodes != null) {
                for (Episode episode : episodes) {
                    List<ServerDatum> serverDataList = episode.getServerData();
                    if (serverDataList != null) {
                        for (ServerDatum serverDatum : serverDataList) {
                            AppCompatButton button = new AppCompatButton(requireContext());
                            button.setId(View.generateViewId());
                            button.setText(serverDatum.getName());
                            button.setTextColor(Color.WHITE);
                            button.setBackgroundResource(R.drawable.orange_button_background);

                            // Tạo LayoutParams cho FlexboxLayout
                            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                                    FlexboxLayout.LayoutParams.WRAP_CONTENT);

                            // Set margin (left, top, right, bottom)
                            params.setMargins(32, 32, 32, 32);

                            // Set flexGrow để button chiếm không gian đều nhau
                            params.setFlexGrow(1.0f);

                            // Set layout_flexBasisPercent để căn button đều ra
                            params.setFlexBasisPercent(0.25f); // Chiếm 25% chiều rộng

                            button.setLayoutParams(params);

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Lấy link phim
                                    String videoUrl = serverDatum.getLinkEmbed();
                                    Log.d("video", videoUrl);

                                    // Chuyển sang Activity mới để phát video
                                    Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
                                    intent.putExtra("VIDEO_URL", videoUrl);
                                    startActivity(intent);
                                }
                            });
                            flexboxLayout.addView(button);
                        }
                    }

                }
            } else {
                Log.e("sendRequest", "Episodes list is null");
            }
        }, volleyError -> {
            volleyError.printStackTrace();
        });

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        mRequestQueue.add(mStringRequest);
    }


}
