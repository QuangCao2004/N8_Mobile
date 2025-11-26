package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

public class SuaNhomsanpham_Activity extends AppCompatActivity {
    Database database;
    ArrayList<NhomSanPham> mangNSP;
    NhomSanPhamAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_nhomsanpham);
        mangNSP = new ArrayList<>();
        adapter = new NhomSanPhamAdapter(SuaNhomsanpham_Activity.this, mangNSP, true);
        database = new Database(this, "banhang.db", null, 1);
        database.QueryData("CREATE TABLE IF NOT EXISTS nhomsanpham(maso INTEGER PRIMARY KEY AUTOINCREMENT, tennsp NVARCHAR(200), anh BLOB)");

    }
}