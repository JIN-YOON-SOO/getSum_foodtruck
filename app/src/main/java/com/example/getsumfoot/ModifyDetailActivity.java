package com.example.getsumfoot;

//import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ModifyDetailActivity extends Activity {
    EditText et_menu_detail;
    Button btn_cancel, btn_modify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_detail);

        et_menu_detail = findViewById(R.id.et_menu_detail);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_modify = findViewById(R.id.btn_modify);

        Intent intent = getIntent();
        String data = intent.getStringExtra("oldDescription");
        et_menu_detail.setText(data);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModifyDetailActivity.this, MyPageSellerModifyActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = et_menu_detail.getText().toString();
//                if(desc=="") {
//                    Toast.makeText(ModifyDetailActivity.this, "값을 입력하세요", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                Intent intent = new Intent();
                intent.putExtra("menu_description", desc);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}