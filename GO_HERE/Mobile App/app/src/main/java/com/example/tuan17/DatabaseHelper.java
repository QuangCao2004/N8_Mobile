package com.example.tuan17;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "banhang.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tạo bảng Chitietdonhang
        db.execSQL("CREATE TABLE IF NOT EXISTS Chitietdonhang (" +
                "id_chitiet INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_dathang INTEGER, " +
                "masp INTEGER, " +
                "soluong INTEGER, " +
                "dongia REAL, " +
                "anh TEXT, " +
                "FOREIGN KEY(id_dathang) REFERENCES Dathang(id_dathang));");

        Log.d("DatabaseHelper", "Tables created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Chitietdonhang");

        onCreate(db);
        Log.d("DatabaseHelper", "Database upgraded from version " + oldVersion + " to " + newVersion);
    }



    // Đổi mức truy cập của phương thức này thành public

    public List<ChiTietDonHang> getChiTietByOrderId(int orderId) {
        List<ChiTietDonHang> chiTietList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        // Thực hiện truy vấn
        Cursor cursor = database.rawQuery("SELECT Chitietdonhang.* FROM Chitietdonhang INNER JOIN sanpham ON sanpham.masp = Chitietdonhang.masp INNER JOIN Dathang ON Dathang.id_dathang = Chitietdonhang.id_dathang WHERE Dathang.id_dathang = ?", new String[]{String.valueOf(orderId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Kiểm tra chỉ mục của các cột trước khi truy cập
                int idIndex = cursor.getColumnIndex("id_chitiet");
                int dathangIdIndex = cursor.getColumnIndex("id_dathang");
                int maSpIndex = cursor.getColumnIndex("masp");
                int soLuongIndex = cursor.getColumnIndex("soluong");
                int donGiaIndex = cursor.getColumnIndex("dongia");
                int anhIndex = cursor.getColumnIndex("anh");

                // Kiểm tra các chỉ mục trước khi sử dụng
                if (idIndex != -1 && maSpIndex != -1 && soLuongIndex != -1 && donGiaIndex != -1 && anhIndex != -1 && dathangIdIndex != -1) {
                    // Khởi tạo và thêm ChiTietDonHang vào danh sách
                    ChiTietDonHang chiTiet = new ChiTietDonHang(
                            cursor.getInt(idIndex),
                            cursor.getInt(dathangIdIndex),
                            cursor.getInt(maSpIndex),
                            cursor.getInt(soLuongIndex),
                            cursor.getFloat(donGiaIndex),
                            cursor.getBlob(anhIndex) // Sử dụng getBlob để lấy ảnh dưới dạng byte[]
                    );
                    chiTietList.add(chiTiet);
                } else {
                    // In thông báo cảnh báo nếu có cột không hợp lệ
                    Log.w("Database", "One of the column indexes is -1. Check if the column exists in the database.");
                }
            }
            cursor.close();
        }
        return chiTietList;
    }

    public List<ChiTietDonHang> getAllChiTietDonHang() {
        List<ChiTietDonHang> chiTietList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(); // Sử dụng dbHelper để lấy cơ sở dữ liệu

        Cursor cursor = db.rawQuery("SELECT * FROM Chitietdonhang", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idChitietIndex = cursor.getColumnIndex("id_chitiet");
                int idDathangIndex = cursor.getColumnIndex("id_dathang");
                int maspIndex = cursor.getColumnIndex("masp");
                int soluongIndex = cursor.getColumnIndex("soluong");
                int dongiaIndex = cursor.getColumnIndex("dongia");
                int anhIndex = cursor.getColumnIndex("anh");

                // Kiểm tra các chỉ số cột trước khi sử dụng
                if (idChitietIndex != -1 && idDathangIndex != -1 && maspIndex != -1 &&
                        soluongIndex != -1 && dongiaIndex != -1 && anhIndex != -1) {

                    int idChitiet = cursor.getInt(idChitietIndex);
                    int idDathang = cursor.getInt(idDathangIndex);
                    int masp = cursor.getInt(maspIndex);
                    int soluong = cursor.getInt(soluongIndex);
                    float dongia = cursor.getFloat(dongiaIndex);
                    byte[] anh = cursor.getBlob(anhIndex);

                    // Thêm vào danh sách chi tiết đơn hàng
                    ChiTietDonHang chiTiet = new ChiTietDonHang(idChitiet, idDathang, masp, soluong, dongia, anh);
                    chiTietList.add(chiTiet);

                    // Kiểm tra dữ liệu đã lấy được
                    Log.d("Chitietdonhang", "ID: " + idChitiet + ", ID ĐH: " + idDathang +
                            ", Mã SP: " + masp + ", Số lượng: " + soluong + ", Đơn giá: " + dongia);
                } else {
                    Log.w("Chitietdonhang", "One of the column indexes is -1. Check if the column exists in the database.");
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        return chiTietList; // Trả về danh sách chi tiết đơn hàng
    }

    public String getTenSanPhamByMaSp(int masp) {
        String tensp = null;
        SQLiteDatabase db = this.getReadableDatabase();

        // Thực hiện truy vấn
        Cursor cursor = db.rawQuery("SELECT tensp FROM sanpham WHERE masp = ?", new String[]{String.valueOf(masp)});

        // Kiểm tra cursor không null và di chuyển đến bản ghi đầu tiên
        if (cursor != null && cursor.moveToFirst()) {
            // Lấy tên sản phẩm từ cursor
            int tenspIndex = cursor.getColumnIndex("tensp");
            if (tenspIndex != -1) {
                tensp = cursor.getString(tenspIndex);
            } else {
                Log.e("Database Error", "Column 'tensp' not found.");
            }
        } else {
            Log.e("Database Error", "Cursor is empty or null.");
        }

        // Đóng cursor
        if (cursor != null) {
            cursor.close();
        }

        return tensp; // Trả về tên sản phẩm
    }

    public List<SanPham> getProductsByNhomSpId(String nhomSpId) {
        List<SanPham> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn để lấy sản phẩm theo nhomSpId
        Cursor cursor = db.rawQuery("SELECT * FROM sanpham WHERE maso = ?", new String[]{nhomSpId});

        if (cursor.moveToFirst()) {
            do {
                String masp = cursor.getString(cursor.getColumnIndexOrThrow("masp"));
                String tensp = cursor.getString(cursor.getColumnIndexOrThrow("tensp"));
                Float dongia = cursor.getFloat(cursor.getColumnIndexOrThrow("dongia"));
                String mota = cursor.getString(cursor.getColumnIndexOrThrow("mota"));
                String ghichu = cursor.getString(cursor.getColumnIndexOrThrow("ghichu"));
                int soluongkho = cursor.getInt(cursor.getColumnIndexOrThrow("soluongkho"));
                String mansp = cursor.getString(cursor.getColumnIndexOrThrow("maso"));
                byte[] anh = cursor.getBlob(cursor.getColumnIndexOrThrow("anh"));

                SanPham sanPham = new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, mansp, anh);
                productList.add(sanPham);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return productList;
    }

    // Phương thức tìm kiếm sản phẩm theo tên
    public ArrayList<SanPham> searchSanPhamByName(String name) {
        ArrayList<SanPham> sanPhamList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Sử dụng LIKE để tìm kiếm gần đúng
        String query = "SELECT * FROM sanpham WHERE tensp LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + name + "%"});

        if (cursor.moveToFirst()) {
            do {
                // Lấy chỉ số cột
                int maspIndex = cursor.getColumnIndex("masp");
                int tenspIndex = cursor.getColumnIndex("tensp");
                int dongiaIndex = cursor.getColumnIndex("dongia");
                int motaIndex = cursor.getColumnIndex("mota");
                int ghichuIndex = cursor.getColumnIndex("ghichu");
                int soluongkhoIndex = cursor.getColumnIndex("soluongkho");
                int manhomsanphamIndex = cursor.getColumnIndex("maso");
                int anhIndex = cursor.getColumnIndex("anh");

                // Kiểm tra và lấy giá trị
                if (maspIndex != -1 && tenspIndex != -1) {
                    String masp = cursor.getString(maspIndex);
                    String tensp = cursor.getString(tenspIndex);
                    float dongia = (dongiaIndex != -1) ? cursor.getFloat(dongiaIndex) : 0.0f; // Sửa lại kiểu dữ liệu thành float
                    String mota = (motaIndex != -1) ? cursor.getString(motaIndex) : "";
                    String ghichu = (ghichuIndex != -1) ? cursor.getString(ghichuIndex) : "";
                    int soluongkho = (soluongkhoIndex != -1) ? cursor.getInt(soluongkhoIndex) : 0;
                    String manhomsanpham = (manhomsanphamIndex != -1) ? cursor.getString(manhomsanphamIndex) : "";
                    byte[] anh = (anhIndex != -1) ? cursor.getBlob(anhIndex) : null;

                    SanPham sanPham = new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, manhomsanpham, anh);
                    sanPhamList.add(sanPham);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return sanPhamList;
    }

}