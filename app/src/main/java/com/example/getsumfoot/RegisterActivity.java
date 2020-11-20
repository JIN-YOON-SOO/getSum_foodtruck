package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    EditText et_email, et_password, et_password_check, et_name;
    CheckBox cb_seller;
    Button btn_signup, btn_cancel;
    private FirebaseAuth firebaseAuth;
   // private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //파이어베이스 접근 설정
        firebaseAuth =  FirebaseAuth.getInstance();
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_password_check = findViewById(R.id.et_password_check);
        cb_seller = findViewById(R.id.cb_seller);
        btn_signup = (Button)findViewById(R.id.btn_signup);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);

        et_password.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        et_password_check.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        //가입버튼 클릭리스너   -->  firebase에 데이터를 저장한다.
        btn_signup.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //가입 정보 가져오기
                final String email = et_email.getText().toString().trim();
                String pwd = et_password.getText().toString().trim();
                String pwdcheck = et_password_check.getText().toString().trim();


                if(pwd.equals(pwdcheck)) {
                    Log.d(TAG, "등록 버튼 " + email);
                    final ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);
                    mDialog.setMessage("가입중입니다...");
                    mDialog.show();

                    try{
                        //파이어베이스에 신규계정 등록하기
                        firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                //가입 성공시
                                if (task.isSuccessful()) {
                                    mDialog.dismiss();

                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String uid = Objects.requireNonNull(user).getUid();
                                    String name = et_name.getText().toString();
                                    boolean is_seller = cb_seller.isChecked();

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    Map<String, Object> childUpdates = new HashMap<>();

                                    childUpdates.put("/Users/"+uid+"is_seller",is_seller);

                                    if(is_seller){
                                        childUpdates.put("/Seller/"+uid+"/name",name);
                                        childUpdates.put("/Seller/"+uid+"/is_open", false);
                                        childUpdates.put("/Seller/"+uid+"/keyword","");
                                        childUpdates.put("/Seller/"+uid+"/time_open","");
                                        childUpdates.put("/Seller/"+uid+"/time_close","");
                                    }else{
                                        childUpdates.put("/Customer/"+uid+"/name", name);
                                    }
                                    ref.updateChildren(childUpdates);

//                                    String name = et_name.getText().toString();
//                                    String isSeller = String.valueOf(cb_seller.isChecked());
//                                    // to use class instead
////                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
////                                    String uid = currentUser.getUid();
////                                    mDatabase = FirebaseDatabase.getInstance().getReference();
////                                    String name = et_name.getText().toString();
////                                    boolean isSeller = cb_seller.isChecked();
////                                    User user = new User(name,isSeller);
////                                    mDatabase.child("Users").child(uid).setValue(user);
//
//
//
//                                    //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
//                                    HashMap<Object,String> hashMap = new HashMap<>();
//
//                                    hashMap.put("name", name);
//                                    hashMap.put("isSeller", isSeller);
//
//                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
//                                    DatabaseReference reference = database.getReference("Users");
//                                    reference.child(uid).setValue(hashMap);

                                    //Class<?> c = isSeller.equals("true") ? MyPageSellerModifyActivity.class : MainActivity.class;
                                    //가입이 이루어져을시 가입 화면을 빠져나감. seller면 정보수정페이지

                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_LONG).show();

                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_LONG).show();
                                    return;  //해당 메소드 진행을 멈추고 빠져나감.
                                }
                            }
                        });

                        //비밀번호 오류시
                        }catch(Exception e){
                        mDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "다시 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;  //해당 메소드 진행을 멈추고 빠져나감.
                    }
                    }
                    else{

                    Toast.makeText(RegisterActivity.this, "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        //취소 버튼 클릭 리스너
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}