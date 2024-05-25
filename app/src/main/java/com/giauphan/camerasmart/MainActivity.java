package com.giauphan.camerasmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.thingclips.smart.android.user.api.ILoginCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etCountryside, etPassword;
    private Button btnLogin , btnRegister;

    @Override
    protected  void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        User user = SessionManager.getSession(MainActivity.this,"user",User.class);

        if (user != null){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            return;
        }
        setContentView(R.layout.activity_main);

        initViews();

        btnLogin.setOnClickListener(view -> {
                String CountryCode = etCountryside.getText().toString();
                String Email = etEmail.getText().toString();
                String Password = etPassword.getText().toString();

                ThingHomeSdk.getUserInstance().loginWithEmail(CountryCode,Email,Password,loginCallback);

        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    private final ILoginCallback loginCallback = new ILoginCallback() {
        @Override
        public void onSuccess(User user) {
            Toast.makeText(MainActivity.this,"Login SuccessFully",Toast.LENGTH_LONG).show();

            SessionManager.saveSession(MainActivity.this,"user", user);

            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }

        @Override
        public void onError(String code, String error) {
            Toast.makeText(MainActivity.this,"Login Failed "+error,Toast.LENGTH_LONG).show();
        }
    };

    private  void initViews(){
        etCountryside = findViewById(R.id.et_contrycode);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        btnLogin = findViewById(R.id.btn_Login);
        btnRegister = findViewById(R.id.btn_Register);

    }

}
