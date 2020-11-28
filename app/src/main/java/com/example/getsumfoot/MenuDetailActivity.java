package com.example.getsumfoot;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.getsumfoot.data.MenuDescription;
import com.facebook.drawee.view.SimpleDraweeView;

public class MenuDetailActivity extends Activity implements View.OnClickListener {

    private MenuDescription menuDescription;
    private TextView TextView_title, TextView_content;
    private SimpleDraweeView imageView;
    private Button buttonReturnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkPopup);
        setComp();
        getMenuDetail();
        setMenuDescription();
    }

    //기본 컴포넌트 셋팅
    public void setComp() {
        TextView_title = findViewById(R.id.TextView_title);
        TextView_content = findViewById(R.id.TextView_content);
        imageView = findViewById(R.id.ImageView_image);

        buttonReturnMain = findViewById(R.id.button_return_main);
        buttonReturnMain.setOnClickListener(this);
    }

    //이전 액티비티에서 받아오는 인텐트
    public void getMenuDetail() {
        Intent intent = getIntent();
        if(intent != null) {
            Bundle bld = intent.getExtras();

            Object obj = bld.get("news");
            if(obj != null && obj instanceof MenuDescription) {
                this.menuDescription = (MenuDescription) obj;
            }
        }
    }

    //이전 액티비티에서 받아오는 인텐트에서 정보를 확인
    public void setMenuDescription() {
        if(this.menuDescription != null) {
            String title = this.menuDescription.getTitle();
            if(title != null) {
                TextView_title.setText(title);
            }
            String description = this.menuDescription.getDescription();
            if(description != null) {
                TextView_content.setText(description);
            }
            Uri uri = Uri.parse(menuDescription.getUrlToImage());
            if(uri !=null){
                imageView.setImageURI(uri);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button_return_main) {
            Intent intent = new Intent(this, MenuInfoActivity.class);
            startActivity(intent);
        }
    }
}
