package com.example.familymanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends Activity {

    EditText editTk, editMk;
    CheckBox checksave;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseAccount;
    static Account currentUser;
    BCrypt bCrypt=new BCrypt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sharedPreferences=getSharedPreferences("datalogin",MODE_PRIVATE);
        databaseAccount = FirebaseDatabase.getInstance().getReference("Account");
        //Tự động đăng nhập
        if(sharedPreferences.getBoolean("checked",false)){
            CheckAccount(sharedPreferences.getString("taikhoan", ""),sharedPreferences.getString("matkhau", ""),sharedPreferences.getBoolean("checked",false));
        }
        else {
            setContentView(R.layout.signin);

            editTk = (EditText) findViewById(R.id.edit_tk_signin);
            editMk = (EditText) findViewById(R.id.edit_mk_signin);
            checksave = (CheckBox) findViewById(R.id.cbsaveaccount);

            //lay tai khoan va mat khau duoc luu truoc do neu co
            editTk.setText(sharedPreferences.getString("taikhoan", ""));
            editMk.setText(sharedPreferences.getString("matkhau", ""));
            checksave.setChecked(sharedPreferences.getBoolean("checked", false));
            String mk = editMk.getText().toString().trim();
            String salt=bCrypt.gensalt(12);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void toSignUpActivity(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    public void toMainActivity(View view) {
        // Nếu thông tin tài khoản và mật khẩu là chính xác thì chuyển sang MainActivity
        // Ngược lại: Yêu cầu người dùng nhập lại
        final String tk = editTk.getText().toString().trim();//trim de bo khoang trong o dau va cuoi
        final String mk = editMk.getText().toString().trim();//trim de bo khoang trong o dau va cuoi

        if (tk.isEmpty() && mk.isEmpty()) {
            Toast.makeText(this, "Xin điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
        } else if (tk.isEmpty()) {
            editTk.setError("Bạn chưa điền tài khoản");
            editTk.requestFocus();
        } else if (mk.isEmpty()) {
            editMk.setError("Bạn chưa điền mật khẩu");
            editMk.requestFocus();
        } else {
            boolean ischecksave=false;
            if(checksave.isChecked()) ischecksave=true;
            //database Manipulation
            CheckAccount(tk,mk,ischecksave);
        }
    }
    private void CheckAccount(final String tk, final String mk, final boolean ischeck){
        databaseAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean success = false;
                for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                    Account account = accountSnapshot.getValue(Account.class);
                    try {

                        if (account.getUsername().equals(tk) && bCrypt.checkpw(mk, account.getPassword())) {
                            currentUser = account;
                            success = true;
                            // Luu tai khoan
                            // néu có check lưu tài khoản
                            if (ischeck) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("taikhoan", tk);
                                editor.putString("matkhau", mk);
                                editor.putBoolean("checked", true);
                                editor.commit();
                            } else {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("taikhoan");
                                editor.remove("matkhau");
                                editor.remove("checked");
                                editor.commit();
                            }
                            Toast.makeText(SignIn.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            startActivity(intent);
                        }
                    } catch (Exception e){
                        Toast.makeText(SignIn.this, "Xảy ra lỗi", Toast.LENGTH_SHORT).show();

                    }
                }

                if (!success) {
                    Toast.makeText(SignIn.this, "Đăng nhập thất bại. Hãy thử lại!", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("checked");
                    editor.commit();
                    Intent intent = new Intent(SignIn.this, SignIn.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignIn.this, databaseError.toString(), Toast.LENGTH_LONG).show();

            }
        });

    }
}
