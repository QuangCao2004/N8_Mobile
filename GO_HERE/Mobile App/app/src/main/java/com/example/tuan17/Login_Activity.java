package com.example.tuan17;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Login_Activity extends androidx.appcompat.app.AppCompatActivity {

    private Database database;
    private String tendn;
    private Handler handler = new Handler();
    private Runnable timeoutRunnable;
    private static final long TIMEOUT_DURATION = 300000; // 300 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);
        EditText tdn = findViewById(R.id.tdn);
        EditText mk = findViewById(R.id.mk);
        TextView dangki = findViewById(R.id.dangki);
        TextView qmk = findViewById(R.id.qmk);
        VideoView videoView = findViewById(R.id.video_view);

        database = new Database(this, "banhang.db", null, 1);

        // Navigate to forgot password activity
        qmk.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DoiMatKhau_Activity.class);
            startActivity(intent);
        });

        // Navigate to register activity
        dangki.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DangKiTaiKhoan_Activity.class);
            startActivity(intent);
        });

        // Set up and play video
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.login_video); // Replace with your video resource
        videoView.setVideoURI(videoUri);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(mp -> videoView.start()); // Auto-start video
        videoView.setOnCompletionListener(mp -> videoView.start()); // Loop video
        videoView.setZOrderOnTop(false); // Ensure login form is above video

        // Handle login button click
        btnLogin.setOnClickListener(v -> {
            String username = tdn.getText().toString().trim();
            String password = mk.getText().toString().trim();

            if (validateLogin(username, password)) {
                tendn = username;

                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("tendn", tendn);
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                startAutoLogoutTimer();
                videoView.stopPlayback(); // Stop video on successful login

                String quyen = getUserQuyen(username);
                Intent intent;

                if (quyen.equals("admin")) {
                    intent = new Intent(Login_Activity.this, TrangchuAdmin_Activity.class);
                    Toast.makeText(this, "Đăng nhập với quyền Admin", Toast.LENGTH_SHORT).show();
                } else if (quyen.equals("user")) {
                    intent = new Intent(Login_Activity.this, TrangchuNgdung_Activity.class);
                    intent.putExtra("tendn", tendn);
                    Toast.makeText(this, "Đăng nhập với quyền User", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Quyền không xác định", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Login_Activity.this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validate login credentials
    private boolean validateLogin(String username, String password) {
        Cursor cursor = database.getReadableDatabase().rawQuery(
                "SELECT * FROM taikhoan WHERE tendn = ? AND matkhau = ?",
                new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    // Get user role
    private String getUserQuyen(String username) {
        String quyen = "";
        Cursor cursor = database.getReadableDatabase().rawQuery(
                "SELECT quyen FROM taikhoan WHERE tendn = ?",
                new String[]{username});

        if (cursor.moveToFirst()) {
            int quyenColumnIndex = cursor.getColumnIndex("quyen");
            if (quyenColumnIndex != -1) {
                quyen = cursor.getString(quyenColumnIndex);
            } else {
                Log.e("Error", "Column 'quyen' not found in result set");
            }
        } else {
            Log.e("Error", "No user found with username: " + username);
        }
        cursor.close();
        return quyen;
    }

    // Start auto-logout timer
    private void startAutoLogoutTimer() {
        handler.removeCallbacks(timeoutRunnable);

        timeoutRunnable = () -> {
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.putString("tendn", null);
            editor.apply();

            Intent intent = new Intent(Login_Activity.this, TrangchuNgdung_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        };

        handler.postDelayed(timeoutRunnable, TIMEOUT_DURATION);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        startAutoLogoutTimer();
    }
}