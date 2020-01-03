package com.example.familymanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.familymanagement.SignIn.currentUser;

public class ChangeInfo extends Activity {
    EditText editUserName, editName, editEmail, editPassword;
    DatabaseReference databaseAccount;
    Button btnsmchange;
    TextView notice;
    ImageButton back;
    BCrypt bCrypt = new BCrypt();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_info);
        anhxa();
        editUserName.setText(currentUser.getUsername());
        editEmail.setText(currentUser.getEmail());
        editName.setText(currentUser.getName());
        databaseAccount = FirebaseDatabase.getInstance().getReference("Account");
        btnsmchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeInfo();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeInfo.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void anhxa() {
        editName = (EditText) findViewById(R.id.ed_name);
        editEmail = (EditText) findViewById(R.id.ed_email);
        editUserName = (EditText) findViewById(R.id.ed_username);
        editPassword = (EditText) findViewById(R.id.ed_passw);
        btnsmchange = (Button) findViewById(R.id.btn_sm_change_info);
        notice = (TextView) findViewById(R.id.tv_notice_change_info);
        back = (ImageButton) findViewById(R.id.imbbackchangeinfo);
    }

    private void ChangeInfo() {
        final String name = editName.getText().toString().trim();
        final String username = editUserName.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        final String password = editPassword.getText().toString().trim();
        if (name.isEmpty() && username.isEmpty() && email.isEmpty()) {
            notice.setText("Xin điền đầy đủ thông tin!");
        } else if (name.isEmpty()) {
            notice.setText("Bạn chưa điền tên");
            editName.setError("Bạn chưa điền tên");
            editName.requestFocus();
        } else if (username.isEmpty()) {
            notice.setText("Bạn chưa điền username");
            editUserName.setError("Bạn chưa điền username");
            editUserName.requestFocus();
        } else if (email.isEmpty()) {
            notice.setText("Bạn chưa điền Email");
            editEmail.setError("Bạn chưa điền Email");
            editEmail.requestFocus();
        } else if (!bCrypt.checkpw(password, currentUser.getPassword())) {
            notice.setText("Mật khẩu sai");
            editPassword.setError("Mật khẩu sai");
            editPassword.requestFocus();
        } else {
            currentUser.setEmail(email);
            currentUser.setUsername(username);
            currentUser.setName(name);
            editPassword.setText("");

            databaseAccount.child(currentUser.getId()).setValue(currentUser);
            //Luu lai mat khau moi
            // Hiện thông báo
            Toast.makeText(ChangeInfo.this, "Thay đổi thông tin thành công", Toast.LENGTH_SHORT).show();
            //Thông báo đổi mật khẩu thành công
            notice.setText("Thay đổi thông tin thành công");
        }

    }

}
