package com.giauphan.camerasmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thingclips.smart.android.user.api.IRegisterCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etCountryside, etPassword,etVerificationCode;
    private Button btnVerificationCode , btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        initViews();

        btnVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String registerEmail = etEmail.getText().toString();
                String registerContryCode = etCountryside.getText().toString();
                getValidationCode(registerContryCode,registerEmail);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String registerEmail = etEmail.getText().toString();
                String registerContryCode = etCountryside.getText().toString();
                String registerPassword = etPassword.getText().toString();
                String inputVerificationCode = etVerificationCode.getText().toString();

                ThingHomeSdk.getUserInstance().registerAccountWithEmail(registerContryCode,registerEmail,registerPassword,inputVerificationCode,registeCallback);
            }
        });
    }

    IRegisterCallback registeCallback = new IRegisterCallback() {
        @Override
        public void onSuccess(User user) {
            Toast.makeText(RegisterActivity.this,"Register SuccessFully",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(String code, String error) {
            Toast.makeText(RegisterActivity.this,"Register Failed "+error,Toast.LENGTH_LONG).show();
        }
    };

IResultCallback validateCallback = new IResultCallback() {
    @Override
    public void onError(String code, String error) {
        Log.e("Register error","Error: "+error +"code:"+ code);
        Toast.makeText(RegisterActivity.this,"Failed Validate code "+error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess() {
        Toast.makeText(RegisterActivity.this," SuccessFully  sent verification code!",Toast.LENGTH_LONG).show();
        etVerificationCode.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.VISIBLE);
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(intent);
    }
};
    private  void getValidationCode(String contryCode, String email){
        ThingHomeSdk.getUserInstance().getRegisterEmailValidateCode(contryCode,email,validateCallback);
    }

    private  void initViews(){
        etCountryside = findViewById(R.id.et_country_code);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etVerificationCode = findViewById(R.id.et_validate_code);
        btnVerificationCode = findViewById(R.id.btn_validate);
        btnRegister = findViewById(R.id.btn_Register);

    }
}