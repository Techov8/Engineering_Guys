package com.techov8.engineerguys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText referal, mobile;
    private EditText name;
    private EditText email;
    private EditText password;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    ProgressDialog pd;

    public static String referedId = "";
    public static Boolean isfromRegister = false;
    TextView terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        referal = findViewById(R.id.referal);
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        terms = findViewById(R.id.terms);
        Button register = findViewById(R.id.register);
        TextView loginUser = findViewById(R.id.login_user);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);


        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urll = "https://engineering-guyz.github.io/#services";
                Intent in = new Intent(Intent.ACTION_VIEW);
                in.setData(Uri.parse(urll));
                startActivity(in);
            }
        });


        loginUser.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        register.setOnClickListener(v -> {
            String txtReferal = referal.getText().toString();
            String txtmobile = mobile.getText().toString();
            String txtName = name.getText().toString();
            String txtEmail = email.getText().toString();
            String txtPassword = password.getText().toString();
            isfromRegister = true;

            if (TextUtils.isEmpty(txtName)
                    || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                Toast.makeText(RegisterActivity.this, "Empty credentials!", Toast.LENGTH_SHORT).show();
            } else if (txtPassword.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(txtReferal, txtName, txtEmail, txtPassword, txtmobile);
            }
        });
    }

    private void registerUser(final String referal, final String name2, final String email, String password, String mobile) {

        pd.setMessage("Please Wait!");
        pd.show();


        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {


            String id = UUID.randomUUID().toString().substring(0, 3);
            int index=name2.indexOf(" ");
            String dd=name2.substring(0,index);
            String[] d = name2.split(" ", 2);
            String referId = d[0].toLowerCase() + "_" + id;


            // fcm settings for perticular user

            // final String[] token = new String[1];

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            // Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("name", name2);
                        map.put("email", email);
                        map.put("id", FirebaseAuth.getInstance().getUid());
                        map.put("imageurl", "");
                        map.put("refer_id", referId);
                        map.put("is_task_done",false);
                        map.put("referal", referal);
                        map.put("token", token);
                        map.put("mobile", mobile);
                        map.put("no_of_coins", 10);


                        firebaseFirestore.collection("USERS").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).set(map)
                                .addOnSuccessListener(aVoid -> {
                                    pd.dismiss();

                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    intent.putExtra("referal", referal);
                                    intent.putExtra("isFromRegister", "Yes");

                                    startActivity(intent);
                                    finish();
                                }).addOnFailureListener(e -> {

                        });


                    });
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();

                Toast.makeText(RegisterActivity.this,"Error ! "+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });


    }
}
