package com.example.getsumfoot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MyPage_Seller_modify extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) { //button btn_open_hour click->timepicker
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page__seller_modify);
    }
    /*btn_add_img-
    * Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType
                                    (android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                            startActivityForResult(intent, RequestCode.PICK_IMAGE);
                            *
      @Override //갤러리에서 이미지 불러온 후 행동
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_IMAGE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    // 이미지뷰에 세팅
                    mPhotoCircleImageView.setImageBitmap(img);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
}