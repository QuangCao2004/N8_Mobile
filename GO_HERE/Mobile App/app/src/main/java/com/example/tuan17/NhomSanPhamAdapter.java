package com.example.tuan17;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class NhomSanPhamAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NhomSanPham> nhomSanPhamList;
    private boolean showFullDetails;
    private Database database;
    private Uri selectedImageUri;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    public NhomSanPhamAdapter(Activity context, ArrayList<NhomSanPham> nhomSanPhamList, boolean showFullDetails) {
        this.context = context;
        this.nhomSanPhamList = nhomSanPhamList;
        this.showFullDetails = showFullDetails;
        this.database = new Database(context, "banhang.db", null, 1);
    }

    @Override
    public int getCount() {
        return nhomSanPhamList.size();
    }

    @Override
    public Object getItem(int position) {
        return nhomSanPhamList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (showFullDetails) {
            return getViewTrue(position, convertView, parent);
        } else {
            return getViewFalse(position, convertView, parent);
        }
    }

    private View getViewTrue(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ds_nhomsanpham, parent, false);
        }

        NhomSanPham nhomSanPham = nhomSanPhamList.get(position);
        TextView ten = view.findViewById(R.id.ten);
        TextView id = view.findViewById(R.id.idma);
        ImageView anh = view.findViewById(R.id.imgnsp);
        ImageButton xoa = view.findViewById(R.id.imgxoa);
        ImageButton sua = view.findViewById(R.id.imgsua);

        id.setText(nhomSanPham.getMa());
        ten.setText(nhomSanPham.getTennhom());

        byte[] anhByteArray = nhomSanPham.getAnh();
        if (anhByteArray != null && anhByteArray.length > 0) {
            Bitmap imganhbs = BitmapFactory.decodeByteArray(anhByteArray, 0, anhByteArray.length);
            anh.setImageBitmap(imganhbs);
        } else {
            anh.setImageResource(R.drawable.vest);
        }

        sua.setOnClickListener(v -> showEditDialog(nhomSanPham));
        xoa.setOnClickListener(v -> {
            Toast.makeText(context, "Bạn đã nhấn nút xóa", Toast.LENGTH_SHORT).show();
            database.deleteNhomSanPham(nhomSanPham.getMa());
            nhomSanPhamList.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Đã xóa nhóm sản phẩm", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private View getViewFalse(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ds_hienthi_gridview2_nguoidung, parent, false);
        }

        NhomSanPham nhomSanPham = nhomSanPhamList.get(position);
        TextView ten = view.findViewById(R.id.ten);
        TextView id = view.findViewById(R.id.idma);
        ImageView anh = view.findViewById(R.id.imgnsp);

        id.setText(nhomSanPham.getMa());
        ten.setText(nhomSanPham.getTennhom());

        byte[] anhByteArray = nhomSanPham.getAnh();
        if (anhByteArray != null && anhByteArray.length > 0) {
            Bitmap imganhbs = BitmapFactory.decodeByteArray(anhByteArray, 0, anhByteArray.length);
            anh.setImageBitmap(imganhbs);
        } else {
            anh.setImageResource(R.drawable.vest);
        }

        return view;
    }

    private void showEditDialog(NhomSanPham nhomSanPham) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_sua_nhomsanpham, null);
        builder.setView(dialogView);

        EditText editTen = dialogView.findViewById(R.id.ten);
        ImageView imgPreview = dialogView.findViewById(R.id.imgnsp);
        Button chonanh = dialogView.findViewById(R.id.btnAddImg);

        editTen.setText(nhomSanPham.getTennhom());

        chonanh.setOnClickListener(view -> openDrawableImagePicker(imgPreview));

        byte[] anhByteArray = nhomSanPham.getAnh();
        if (anhByteArray != null && anhByteArray.length > 0) {
            Bitmap imganh = BitmapFactory.decodeByteArray(anhByteArray, 0, anhByteArray.length);
            imgPreview.setImageBitmap(imganh);
        } else {
            imgPreview.setImageResource(R.drawable.tc);
        }

        imgPreview.setOnClickListener(imgView -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        });

        builder.setPositiveButton("Lưu", (dialog, which) -> updateNhomSanPham(nhomSanPham, editTen));
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateNhomSanPham(NhomSanPham nhomSanPham, EditText editTen) {
        String newTen = editTen.getText().toString().trim();
        byte[] newAnh = selectedImageUri != null ? getBytesFromUri(selectedImageUri) : null;

        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tennsp", newTen);
        if (newAnh != null) {
            values.put("anh", newAnh);
        }

        db.update("nhomsanpham", values, "maso = ?", new String[]{nhomSanPham.getMa()});
        nhomSanPham.setTennhom(newTen);
        if (newAnh != null) {
            nhomSanPham.setAnh(newAnh);
        }

        notifyDataSetChanged();
    }

    private byte[] getBytesFromUri(Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            return outputStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error reading file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void openDrawableImagePicker(ImageView imgnsp) {
        final String[] imageNames = {"iphone", "nokia", "samsung", "lg", "huawei", "gionee", "oppo", "vivo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chọn ảnh từ drawable");
        builder.setItems(imageNames, (dialog, which) -> {
            String selectedImageName = imageNames[which];
            int resourceId = context.getResources().getIdentifier(selectedImageName, "drawable", context.getPackageName());
            imgnsp.setImageResource(resourceId);
            selectedImageUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId);
        });
        builder.show();
    }
}