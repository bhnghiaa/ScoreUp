package com.example.scoreup;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class Instruction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
         TextView textView = findViewById(R.id.textView14);
        textView.setText("Đăng kí tài khoản\n" +
                "Nhập username, email, password, confirm password\n" +
                "Bấm register để tiến hành đăng ký\n" +
                "Đăng nhập\n" +
                "Nhập user name, password\n" +
                "Bấm login để tiến hành đăng nhập\n" +
                "Xem thông tin khóa học\n" +
                "B1: Chọn giao diện Home\n" +
                "B2: Chọn Details để xem thông tin khóa học\n" +
                "B3: Chọn Learn more để xem thông tin chi tiết\n" +
                "Thi thử TOEIC\n" +
                "B1: Chọn giao diện Home\n" +
                "B2: Lựa chọn khóa học\n" +
                "B3: Chọn Full Test để bắt đầu bài thi \n" +
                "B4: Lựa chọn phần toeic muốn thi. Bấm start để bắt đầu\n" +
                "B5: Tiến hành làm bài (bấm biểu tượng ≡ để xem thông tin về câu hỏi đang làm)\n" +
                "B6: Bấm finish để kết thúc\n" +
                "Đặt lại mật khẩu\n" +
                "B1: Chọn Personal Information \n" +
                "B2: Nhập mật khẩu cũ và mật khẩu mới\n" +
                "B3: Bấm save để lưu thông tin\n" +
                "Cài đặt ngôn ngữ\n" +
                "B1: Bấm vào Settings\n" +
                "B2: Lựa chọn ngôn ngữ\n" +
                "B3: Bấm Save để cập nhật ngôn ngữ mới\n" +
                "Xoay ngang màn hình\n" +
                "Lựa chọn chế độ xoay ngang trên giao diện điện thoại để tiến hành truy cập ứng dụng dưới hình thức xoay ngang\n");
    }
}