package com.example.exercise4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 200;
    private static final int NOTIFICATION_REMINDER = 300;

    private static final  int MY_READ_PERMISSION_CODE = 400;
    private Button customTimeButton;
    private TextView customTimeTextView;
    private GalleryAdapter galleryAdapter;
    private List<String> images;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PackageManager.PERMISSION_GRANTED);
        createNotificationChannel();
        showTimePickerDialog();
        customTimeButton = findViewById(R.id.customTimeButton);
        customTimeTextView = findViewById(R.id.customTimeTextView);
        recyclerView = findViewById(R.id.gallery_images);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_READ_PERMISSION_CODE);
        }else {
            loadImages();
        }
        customTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
    }
    private void loadImages() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        images = ImageGallery.listOfImages(this);
        galleryAdapter = new GalleryAdapter(this, images, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String path) {
                Toast.makeText(MainActivity.this,""+path,Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(galleryAdapter);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_READ_PERMISSION_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Read external storage permission granted",Toast.LENGTH_SHORT).show();
                loadImages();
            }else {
                Toast.makeText(this,"Read external storage permission denied",Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    // Schedule the notification with the selected time
                    scheduleNotification(hourOfDay, minute);
                    displaySelectedTime(hourOfDay,minute);
                },
                currentHour, // Default hour
                currentMinute, // Default minute
                false // 24-hour format
        );
        timePickerDialog.setTitle("Select Notification Time");
        timePickerDialog.show();
    }
    private void displaySelectedTime(int hourOfDay, int minute) {
        Calendar selectedTime = Calendar.getInstance();
        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        selectedTime.set(Calendar.MINUTE, minute);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedTime.getTime());

        customTimeTextView.setText("Custom Notify Time: " + formattedDate);
    }


    private void scheduleNotification(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If the selected time is already past, set it for the next day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent notifyIntent = new Intent(MainActivity.this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, NOTIFICATION_REMINDER, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, // Repeat every day
                pendingIntent
        );

        Toast.makeText(this, "Notification scheduled for " +
                String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute), Toast.LENGTH_SHORT).show();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RemindPhotoChannel";
            String description = "Channel for remind taking photo";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyPhoto", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.cameraButton) {
            // Check if the CAMERA permission is granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, open the camera
                openCamera();
            } else {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            return;
        }
    }

    // Add onActivityResult method to handle the result if needed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle the captured image, if needed
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveImageToGallery(imageBitmap);
        }
    }

    private void saveImageToGallery(Bitmap imageBitmap) {
        // Lấy đường dẫn để lưu ảnh vào thư mục Pictures trên bộ nhớ ngoại vi
        File storageDir = (File) Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // Tạo định dạng thời gian để đặt tên file theo định dạng "yyyyMMdd_HHmmss"
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        // Đặt tên file kết hợp với timeStamp để có tên file duy nhất
        String fileName = "IMG_" + timeStamp + ".jpg";
        // Tạo đối tượng File với đường dẫn và tên file đã xác định
        File imageFile = new File(storageDir, fileName);

        try {
            // Mở một OutputStream để ghi dữ liệu vào file
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            // Nén và ghi dữ liệu từ Bitmap vào file dưới định dạng JPEG với chất lượng 100
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            // Đảm bảo rằng tất cả dữ liệu đã được ghi và đóng OutputStream
            outputStream.flush();
            outputStream.close();
            // Tạo Intent để cập nhật thư viện ảnh với file mới thêm vào
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(imageFile));
            // Gửi Intent để hệ thống biết về file mới
            sendBroadcast(mediaScanIntent);
            //Chạy laij hàm loadImages()
            loadImages();

            // Hiển thị thông báo ngắn cho người dùng về việc lưu ảnh thành công
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Xử lý các ngoại lệ, không có hành động cụ thể ở đây
        }
    }

}