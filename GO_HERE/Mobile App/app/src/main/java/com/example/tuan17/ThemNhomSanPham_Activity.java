package com.example.tuan17;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ThemNhomSanPham_Activity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1; // Mã yêu cầu cho việc chọn ảnh
    EditText tennsp;
    Database database;
    ImageView imgnsp;
    ArrayList<NhomSanPham> mangNSP;

  NhomSanPhamAdapter adapter;
    ImageButton back;
    private Uri imageUri; // Biến để lưu trữ URI của ảnh
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nhom_san_pham);

      tennsp= findViewById(R.id.ten);
        imgnsp = findViewById(R.id.imgnsp);
        Button chonimgbs = findViewById(R.id.btnAddImg);
        Button btnthem = findViewById(R.id.btnadd);

        // Thiết lập OnClickListener cho nút chọn ảnh
        chonimgbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawableImagePicker(); // Gọi hàm mở hình ảnh từ drawable
            }
        });
        mangNSP = new ArrayList<>();
        adapter = new NhomSanPhamAdapter(ThemNhomSanPham_Activity.this, mangNSP, true) {
        };
        database = new Database(this, "banhang.db", null, 1);
        database.QueryData("CREATE TABLE IF NOT EXISTS nhomsanpham(maso INTEGER PRIMARY KEY AUTOINCREMENT, tennsp NVARCHAR(200), anh BLOB)");

        // Thiết lập OnClickListener cho nút chọn ảnh
        chonimgbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawableImagePicker(); // Gọi hàm mở hình ảnh từ drawable
            }
        });

        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy dữ liệu từ các trường
                String tenNsp = tennsp.getText().toString().trim();

                // Kiểm tra dữ liệu không rỗng
                if (tenNsp.isEmpty() ) {
                    Toast.makeText(ThemNhomSanPham_Activity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Khởi tạo biến imageBytes
                byte[] imageBytes = null;

                // Kiểm tra imageUri có khác null không
                if (imageUri != null) {
                    imageBytes = getBytesFromUri(imageUri);
                    if (imageBytes == null) {
                        Toast.makeText(ThemNhomSanPham_Activity.this, "Lỗi khi lấy ảnh!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                database.QueryData("INSERT INTO nhomsanpham(tennsp, anh) VALUES ('"
                        + tenNsp + "', ?)", new Object[]{imageBytes}); // Sử dụng tham số để tránh lỗi SQL Injection   Toast.makeText(ThemNhomSanPham_Activity.this, "Thêm nhóm sản phẩm thành công!", Toast.LENGTH_LONG).show();

                // Chuyển đến Activity thứ hai
                Intent intent = new Intent(getApplicationContext(), Nhomsanpham_admin_Actvity.class);
                startActivity(intent);
                finish(); // Đóng Activity hiện tại
            }
        });



        // Tìm ImageButton
        ImageButton imgBack = findViewById(com.example.tuan17.R.id.imgback);

        // Gán sự kiện click
        imgBack.setOnClickListener(v -> {
            // Đóng activity hiện tại và quay lại màn hình trước (DanhMuc_Activity)
            finish();
        });
    }
    // Mở dialog chọn hình ảnh từ drawable
    private void openDrawableImagePicker() {
        final String[] imageNames = {"iphone", "nokia", "samsung", "lg", "huawei", "gionee", "oppo", "vivo"};

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
                imgnsp.setImageResource(resourceId);
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