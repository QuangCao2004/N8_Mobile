package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class SuaSanPham_Activity extends AppCompatActivity {
    Database database;



    ArrayList<SanPham> mangBS;
    SanPhamAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_san_pham);
        mangBS = new ArrayList<>();

        adapter = new SanPhamAdapter(this, mangBS, true);

        database = new Database(this, "banhang.db", null, 1);
        database.QueryData("CREATE TABLE IF NOT EXISTS sanpham(masp INTEGER PRIMARY KEY AUTOINCREMENT, tensp NVARCHAR(200),dongia FLOAT,mota TEXT,ghichu TEXT,soluongkho INTEGER,maso INTEGER , anh BLOB)");

    }
}