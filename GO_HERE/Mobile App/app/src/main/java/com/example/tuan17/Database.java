package com.example.tuan17;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    //truy vấn không trả kết quả
    public void QueryData(String sql){
        SQLiteDatabase database=getWritableDatabase();
        database.execSQL(sql);

    }
    public void QueryDulieu(String sql, byte[]... params) {
        SQLiteDatabase database = this.getWritableDatabase();
        SQLiteStatement statement = database.compileStatement(sql);

        for (int i = 0; i < params.length; i++) {
            statement.bindBlob(i + 1, params[i]); // Gán blob vào câu lệnh
        }

        statement.executeInsert(); // Hoặc executeUpdate/Delete tùy thuộc vào câu lệnh
        database.close();
    }
    //truy vấn có kết quả
    public Cursor GetData(String sql){
        SQLiteDatabase database=getReadableDatabase();
        return database.rawQuery(sql,null);
    }
    // Phương thức để thực hiện câu lệnh SQL với một tham số blob
    public void QueryData(String sql, byte[] param) {
        SQLiteDatabase database = this.getWritableDatabase();
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindBlob(1, param); // Gán blob vào câu lệnh
        statement.executeInsert();
        database.close();
    }

    //dùng trong trang thêm thông tin nhóm sp
    public void QueryData(String sql, Object... args) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql, args); // Sử dụng args để truyền tham số
        db.close();
    }
    // Thêm phương thức xóa nhóm sản phẩm
    public void deleteNhomSanPham(String maSo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("nhomsanpham", "maso = ?", new String[]{maSo});
        db.close();
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS nhomsanpham ("
                + "maso INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "tennsp NVARCHAR(200), "
                + "anh BLOB)");
    }

    public List<Order> getDonHangByTenKh(String tenKh) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT * FROM Dathang WHERE tenkh = ?";
            cursor = db.rawQuery(sql, new String[]{tenKh});
            if (cursor.moveToFirst()) {
                do {
                    Order order = new Order(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id_dathang")),
                            cursor.getString(cursor.getColumnIndexOrThrow("tenkh")),
                            cursor.getString(cursor.getColumnIndexOrThrow("tenNguoiNhan")),
                            cursor.getString(cursor.getColumnIndexOrThrow("diachi")),
                            cursor.getString(cursor.getColumnIndexOrThrow("sdt")),
                            cursor.getFloat(cursor.getColumnIndexOrThrow("tongthanhtoan")),
                            cursor.getString(cursor.getColumnIndexOrThrow("ngaydathang"))
                    );
                    android.util.Log.d("Database", "Đơn hàng: id=" + order.getId() + ", tongthanhtoan=" + order.getTongTien() + ", ngaydathang=" + order.getNgayDatHang());
                    orders.add(order);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return orders;
    }


    public List<Order> getAllDonHang() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM Dathang";
            cursor = db.rawQuery(query, null); // Cần đối số thứ 2 là null

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0); // id_dathang
                    String tenKh = cursor.getString(1); // tenkh
                    String diaChi = cursor.getString(2); // diachi
                    String tenNguoiNhan = cursor.getString(3); // diachi
                    String sdt = cursor.getString(4); // sdt
                    float tongThanhToan = cursor.getFloat(5); // tongthanhtoan
                    String ngayDatHang = cursor.getString(6); // ngaydathang

                    // Tạo đối tượng Order và thêm vào danh sách
                    orders.add(new Order(id, tenKh, diaChi, sdt, cursor.getString(cursor.getColumnIndexOrThrow("sdt")), tongThanhToan, ngayDatHang));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace(); // In ra lỗi nếu có
        } finally {
            if (cursor != null) {
                cursor.close(); // Đóng cursor để tránh rò rỉ bộ nhớ
            }
            db.close(); // Đóng database sau khi hoàn thành
        }

        return orders;
    }

}
