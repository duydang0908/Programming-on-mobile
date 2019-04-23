package com.example.duyda.onlinesaleshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duyda.onlinesaleshop.Common.Common;
import com.example.duyda.onlinesaleshop.Models.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity {


    Button btnSignIn;
    MaterialEditText edtEmail, edtPassword;

    TextView tvForgotPwd;

    FirebaseAuth auth;

    CheckBox checkBoxRemember;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/my_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());


        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        checkBoxRemember = findViewById(R.id.ckbRemember);

        tvForgotPwd = findViewById(R.id.txtForgetPwd);

        btnSignIn = findViewById(R.id.btnSignIn);

        Paper.init(this);

        auth = FirebaseAuth.getInstance();

        tvForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFogotPwdDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    if (checkBoxRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, edtEmail.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                    }
                    final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
                    mDialog.setMessage("Please stand by...");
                    mDialog.show();
                    final String email = edtEmail.getText().toString();
                    final String password = edtPassword.getText().toString();
                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                        Toast.makeText(LoginActivity.this, "Fill full information", Toast.LENGTH_SHORT).show();
                    else {
                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    mDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Failed login", Toast.LENGTH_LONG).show();
                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Success login", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                    finish();
                                }

                            }
                        });
                    }
                } else
                    Toast.makeText(LoginActivity.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void showFogotPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your email");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_pwd_layout, null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtEmail = forgot_view.findViewById(R.id.edtEmail);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auth.sendPasswordResetEmail(edtEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        builder.show();

    }


}
