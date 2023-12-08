package com.example.modernartui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog {

    private static AlertDialog alertDialog;

    public static void showDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_dialog, null);

        TextView textView = view.findViewById(R.id.dialogText);
        textView.setText("Thông tin nhóm N02.4 gồm các thành viên:\n1/Phạm Tấn Đạt - 3121410149\n2/Bùi Ngọc Thức - 3121410491\n3/Trần Như Phú Quang - 3121410401\n4/Huỳnh Quốc Hưng - 3121410239");

        Button closeButton = view.findViewById(R.id.btnClose);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        builder.setView(view);

        alertDialog = builder.create();

        alertDialog.show();
    }
}
