package com.example.getsumfoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.getsumfoot.data.ReviewData;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 사진 먼저 storage에 저장  => url가져 와서 텍스트와 함께 저장
public class ReviewWriteActivity extends AppCompatActivity {
    private ImageView image_view;
    private Uri image_uri, photo_uri, album_uri;
    private String current_photo_path;
    private static final int FROM_CAMERA = 0;
    private static final int FROM_ALBUM = 1;
    private int flag = 0;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(); //파이어베이스 연동
        storage = FirebaseStorage.getInstance();
        databaseReference = database.getReference("ReviewData");

        image_view = findViewById(R.id.insert_image);

        image_view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                makeDialog();
            }
        });
    }
    private void makeDialog() {
        AlertDialog.Builder builder= new AlertDialog.Builder(ReviewWriteActivity.this,R.style.MyAlertDialogStyle);
        builder.setTitle("사진 업로드").setCancelable(false).setPositiveButton("사진촬영", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int id){
                Log.v("알림","다이얼로그 > 사진촬영 선택");
                flag = 0;
                takePhoto();
            }
        }).setNeutralButton("앨범 선택", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int id){
                Log.v("알림","다이얼로그 > 앨범 선택");
                flag = 1;
                selectAlbum();
            }
        }).setNegativeButton("취소 ", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int id){
                Log.v("알림","다이얼로그 > 취소 선택");
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void takePhoto(){ // 촬영 선택
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager()) != null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                }catch(IOException e){
                    e.printStackTrace();
                }
                if(photoFile != null){
                    Uri providerURI = FileProvider.getUriForFile(this,getPackageName(),photoFile);
                    image_uri = providerURI;
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,providerURI);
                    startActivityForResult(intent,FROM_CAMERA);
                }

            }

        }else{
            Log.v("알림","접근 불가");
        }
    }

    private File createImageFile() throws IOException { // 이미지 생성 함수
        String image_filename = System.currentTimeMillis() + ".jpg";
        File image_file = null;
        File storage_dir = new File(Environment.getExternalStorageDirectory()+"/ReviewWrite");

        if(!storage_dir.exists()){
            Log.v("알림","storage directory 존재 X" + storage_dir.toString());
            storage_dir.mkdir();
        }
        Log.v("알림","storage directory 존재 O" + storage_dir.toString());
        image_file = new File(storage_dir,image_filename);
        current_photo_path = image_file.getAbsolutePath();

        return image_file;
    }

    public void galleryAddPic(){ // 찍은 사진 갤러리에 저장
        Intent media_scan_intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(current_photo_path);
        Uri content_uri = Uri.fromFile(file);
        media_scan_intent.setData(content_uri);
        sendBroadcast(media_scan_intent);
        Toast.makeText(this,"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();
    }

    public void selectAlbum(){ // 앨범에서 가져오기
        // 앨범 열기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent,FROM_ALBUM);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            return;
        }

        switch(requestCode){
            case FROM_ALBUM :{
                //앨범에서 가져오기
                if(data.getData() != null){
                    try {
                        photo_uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo_uri);
                        image_view.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case FROM_CAMERA :{
                //카메라 촬영
                try{
                    galleryAddPic();
                    image_view.setImageURI(image_uri);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void makeConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewWriteActivity.this,R.style.MyAlertDialogStyle);
        builder.setTitle("작성완료").setMessage("글을 게시하시겠습니까?").setCancelable(false).setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //DB에 등록
                        final String name = firebaseAuth.getUid();
                        // 사진 storage에 저장 후 url 받아오기
                        String filename = name + "_" + System.currentTimeMillis();
                        StorageReference storageReference =  storage.getReferenceFromUrl("gs://getsumfoot.appspot.com").child("ReviewWrite/"+filename);

                        UploadTask uploadTask;
                        Uri file = null;
                        if(flag == 0){
                            file = Uri.fromFile(new File(current_photo_path));
                        }else if(flag == 1){
                            file = photo_uri;
                        }
                        uploadTask = storageReference.putFile(file);

                        final ProgressDialog progressDialog = new ProgressDialog(ReviewWriteActivity.this,R.style.MyAlertDialogStyle);
                        progressDialog.setMessage("업로드 중");
                        progressDialog.show();


                    }
                });
        }
}
