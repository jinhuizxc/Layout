package com.example.jh.layout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取屏幕宽高
 *
 * 之后要整合到练习demo里
 */
public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RefreshFootAdapter adapter;

    private List<String> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshfoot);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 适配器
//        adapter = new RefreshFootAdapter(this, recyclerView, new LinearLayoutManager(this), 2);
        recyclerView.setAdapter(adapter);


    }




}
