package com.example.familymanagement;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends Activity {

    EditText editTk, editMk, editReMk, editName, editEmail, editPhone;
    RadioGroup RadGroupRole;      // chưa xử lí

    DatabaseReference databaseAccount;
    BCrypt bCrypt = new BCrypt();

    boolean added = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        editName = (EditText) findViewById(R.id.edit_name);
        editTk = (EditText) findViewById(R.id.edit_username);
        editMk = (EditText) findViewById(R.id.edit_mk_signup);
        editReMk = (EditText) findViewById(R.id.edit_retypeMk_signup);
        editEmail = (EditText) findViewById(R.id.edit_email);

        databaseAccount = FirebaseDatabase.getInstance().getReference("Account");
    }

    public void signUp(View view) {
        final String name = editName.getText().toString().trim();
        final String tk = editTk.getText().toString().trim();
        final String mk = editMk.getText().toString().trim();
        final String reMk = editReMk.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();

        if (tk.isEmpty() && mk.isEmpty() && reMk.isEmpty() && name.isEmpty() && email.isEmpty()) {
            Toast.makeText(this, "Xin điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
        } else if (name.isEmpty()) {
            editName.setError("Bạn chưa điền họ tên");
            editName.requestFocus();
        } else if (tk.isEmpty()) {
            editTk.setError("Bạn chưa điền tài khoản");
            editTk.requestFocus();
        } else if (mk.isEmpty()) {
            editMk.setError("Bạn chưa điền mật khẩu");
            editMk.requestFocus();
        } else if (mk.isEmpty()) {
            editMk.setError("Mật khẩu phải có độ dài lớn hơn 6");
            editMk.requestFocus();
        } else if (reMk.isEmpty()) {
            editReMk.setError("Bạn chưa nhập lại mật khẩu");
            editReMk.requestFocus();
        } else if (!mk.equals(reMk)) {
            Toast.makeText(this, "Nhập lại mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            editEmail.setError("Bạn chưa điền Email");
            editEmail.requestFocus();
        } else {

            // Data manipulation
            databaseAccount.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // kiểm tra username đã tồn tại chưa
                    for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                        Account account = accountSnapshot.getValue(Account.class);
                        if (account.getUsername().equals(tk) && !added) {
                            editTk.setError("Tên đăng nhập đã tồn tại");
                            editTk.requestFocus();
                            return;
                        }
                    }

                    if (!added) {
                        // Nếu username chưa tồn tại thì thêm vào database

                        String pw = bCrypt.hashpw(mk, bCrypt.gensalt());
                        String id = databaseAccount.push().getKey();
                        Account account = new Account(id, name, tk, pw, email);
                        databaseAccount.child(id).setValue(account);
                        Toast.makeText(SignUp.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        added = true;
                        Intent intent = new Intent(SignUp.this, SignIn.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
    }
}

