package com.example.george.ark.activites;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.ark.ListOnline;
import com.example.george.ark.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mLoginBtn;
    private TextView mRegistrBtn;
    private EditText emailEdit;
    private EditText passwordEdit;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mLoginBtn = (Button) findViewById(R.id.login_create);
        mRegistrBtn= (TextView) findViewById(R.id.register_acc);
        emailEdit = (EditText) findViewById(R.id.email);
        passwordEdit = (EditText) findViewById(R.id.password);

        mRegistrBtn.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.register_acc:
                Intent reg = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(reg);
                finish();
                break;
            case R.id.login_create:
                String emailUp = emailEdit.getText().toString();
                String passwordUp = passwordEdit.getText().toString();

                if (!TextUtils.isEmpty(emailUp)&& !TextUtils.isEmpty(passwordUp)){
                    mAuth.signInWithEmailAndPassword(emailUp, passwordUp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Intent intent = new Intent(LoginActivity.this, ListOnline.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Ошибка, проверьте правильность заполнения форм", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
        }

    }
}
