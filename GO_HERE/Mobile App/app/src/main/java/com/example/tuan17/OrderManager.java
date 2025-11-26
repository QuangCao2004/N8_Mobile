package com.example.tuan17;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class OrderManager {
    private DatabaseHelper dbHelper;

    public OrderManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Phương thức để lấy tất cả chi tiết đơn hàng


    // Thêm đơn hàng mới
    public long addOrder(String tenKh, String tenNguoiNhan, String diaChi, String sdt, float tongThanhToan) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenkh", tenKh);
        values.put("tenNguoiNhan", tenNguoiNhan);
        values.put("diachi", diaChi);
        values.put("sdt", sdt);
        values.put("tongthanhtoan", tongThanhToan);

        long id = db.insert("Dathang", null, values);
        db.close();
        return id;
    }

    // Thêm chi tiết đơn hàng mới
    public long addOrderDetails(int idDonHang, String masp, int soluong, float dongia, byte[] anh) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_dathang", idDonHang); // ID đơn hàng liên kết
        values.put("masp", masp);
        values.put("soluong", soluong);
        values.put("dongia", dongia);
        values.put("anh", anh); // Anh ở đây là kiểu byte[]

        long id = db.insert("Chitietdonhang", null, values);
        db.close();
        return id;
    }
}
