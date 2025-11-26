package com.example.tuan17;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ThemSanPham_Activity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Mã yêu cầu cho việc chọn ảnh
    EditText tensp, dongia, mota, ghichu, soluongkho;
    Spinner mansp;
    Database database;
    ImageView imgsp;
    ArrayList<SanPham> mangSP;
    ArrayList<NhomSanPham> mangNSPList;
    SanPhamAdapter adapter;
    ImageButton back;
    private Uri imageUri; // Biến để lưu trữ URI của ảnh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_san_pham);

        // Khởi tạo các view
        tensp = findViewById(R.id.tensp);
        imgsp = findViewById(R.id.imgsp);
        mota = findViewById(R.id.mota);
        ghichu = findViewById(R.id.ghichu);
        dongia = findViewById(R.id.dongia);
        soluongkho = findViewById(R.id.soluongkho);
        mansp = findViewById(R.id.spn);
        Button chonimgbs = findViewById(R.id.btnAddImg);
        Button btnthem = findViewById(R.id.btnadd);

        // Khởi tạo cơ sở dữ liệu
        database = new Database(this, "banhang.db", null, 1);
        database.QueryData("CREATE TABLE IF NOT EXISTS sanpham(masp INTEGER PRIMARY KEY AUTOINCREMENT, tensp NVARCHAR(200), dongia FLOAT, mota TEXT, ghichu TEXT, soluongkho INTEGER, maso INTEGER, anh BLOB)");

        // Tải danh sách nhóm sản phẩm
        loadTenNhomSanPham();

        // Thiết lập sự kiện cho nút chọn ảnh
        chonimgbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawableImagePicker(); // Gọi hàm mở hình ảnh từ drawable
            }
        });

        // Thiết lập sự kiện cho nút thêm sản phẩm
        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSanPham();
            }
        });
    }

    private void loadTenNhomSanPham() {
        mangNSPList = new ArrayList<>();
        Cursor cursor = database.GetData("SELECT maso, tennsp FROM nhomsanpham"); // Lấy maso và tennhom

        while (cursor.moveToNext()) {
            String maso = cursor.getString(0); // Cột 0
            String tennhom = cursor.getString(1); // Cột 1
            mangNSPList.add(new NhomSanPham(maso, tennhom, null)); // null nếu không cần ảnh
        }

        // Tạo adapter cho Spinner
        ArrayAdapter<NhomSanPham> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mangNSPList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mansp.setAdapter(adapter);
    }

    private void addSanPham() {
        // Lấy dữ liệu từ các trường
        String tenNsp = tensp.getText().toString().trim();
        String motaStr = mota.getText().toString().trim();
        String ghichuStr = ghichu.getText().toString().trim();
        String dongiaStr = dongia.getText().toString().trim();
        String soluongStr = soluongkho.getText().toString().trim();
        String maso = mangNSPList.get(mansp.getSelectedItemPosition()).getMa(); // Lấy maso từ Spinner

        // Kiểm tra dữ liệu không rỗng
        if (tenNsp.isEmpty() || motaStr.isEmpty() || ghichuStr.isEmpty() || dongiaStr.isEmpty() || soluongStr.isEmpty()) {
            Toast.makeText(ThemSanPham_Activity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Khởi tạo giá trị cho imageBytes
        byte[] imageBytes = null;
        if (imageUri != null) {
            imageBytes = getBytesFromUri(imageUri);
            if (imageBytes == null) {
                Toast.makeText(ThemSanPham_Activity.this, "Lỗi khi lấy ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Chuyển đổi giá trị số
        float dongiaFloat;
        int soluongInt;
        try {
            dongiaFloat = Float.parseFloat(dongiaStr);
            soluongInt = Integer.parseInt(soluongStr);
        } catch (NumberFormatException e) {
            Toast.makeText(ThemSanPham_Activity.this, "Giá trị không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thêm sản phẩm vào cơ sở dữ liệu
        database.QueryData("INSERT INTO sanpham(tensp, dongia, mota, ghichu, soluongkho, maso, anh) VALUES (?, ?, ?, ?, ?, ?, ?)",
                new Object[]{tenNsp, dongiaFloat, motaStr, ghichuStr, soluongInt, maso, imageBytes});

        Toast.makeText(ThemSanPham_Activity.this, "Thêm sản phẩm thành công!", Toast.LENGTH_LONG).show();

        // Chuyển đến Activity thứ hai
        Intent intent = new Intent(getApplicationContext(), Sanpham_admin_Activity.class);
        startActivity(intent);
        finish();
    }

    // Mở dialog chọn hình ảnh từ drawable
    private void openDrawableImagePicker() {
        final String[] imageNames = {"huawei1","huawei2","iphone1", "iphone2","iphone3","iphone4", "lg1","lg2","nokia1", "oppo1","oppo2","samsung1", "samsung2","samsung3","vivo1","vivo2"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ảnh từ drawable");
        builder.setItems(imageNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Lấy tên hình ảnh đã chọn
                String selectedImageName = imageNames[which];

                // Lấy ID tài nguyên drawable
                int resourceId = getResources().getIdentifier(selectedImageName, "drawable", getPackageName());

                // Cập nhật ImageView
                imgsp.setImageResource(resourceId);
                imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId); // Cập nhật URI
            }
        });
        builder.show();
    }

    // Chuyển đổi URI thành mảng byte
    private byte[] getBytesFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray(); // Trả về mảng byte của ảnh
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}