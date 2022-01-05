package com.techov8.engineerguys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techov8.engineerguys.ui.Profile.User;

import java.util.HashMap;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText referal, mobile;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button register;
    private TextView loginUser;

    private DatabaseReference mRootRef, mref, muser;
    private FirebaseAuth mAuth;

    ProgressDialog pd;

    public static String referedId = "";
    public static Boolean isfromRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        referal = findViewById(R.id.referal);
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        loginUser = findViewById(R.id.login_user);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mref = FirebaseDatabase.getInstance().getReference();
        muser = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);


        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }

    private void registerUser(final String referal, final String name, final String email, String password, String mobile) {

        pd.setMessage("Please Wait!");
        pd.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {


                String id = UUID.randomUUID().toString().substring(0, 3);
                String referId = name.toLowerCase() + "_" + id;
                /*HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("email", email);
                map.put("id", mAuth.getCurrentUser().getUid());
                map.put("bio", "");
                map.put("imageurl", "default");
                map.put("refer_id", referId);
                map.put("no_of_coins", 0);

                 */


                // fcm settings for perticular user

                // final String[] token = new String[1];

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    // Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();

                               // Toast.makeText(RegisterActivity.this, token, Toast.LENGTH_SHORT).show();
                                // Log and toast
                                //  String msg = getString(R.string.msg_token_fmt, token);
                                Log.e("fcm", "--" + token);


                                User user = new User(name, email, token, "0", mobile, " ", mAuth.getCurrentUser().getUid(), referId,referal,"1");

                                SharedPreferences prefs = getSharedPreferences("Refer_data", 0);
                                SharedPreferences.Editor editor = prefs.edit();
                               editor.putString("id", referId);

                              editor.apply();

                                mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {


                                            pd.dismiss();

                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                            intent.putExtra("referal", referal);
                                            intent.putExtra("isFromRegister", "Yes");

                                            startActivity(intent);
                                            finish();
//


                                        }
                                    }
                                });


                            }
                        });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
