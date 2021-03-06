package org.tensorflow.lite.examples.classification.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.tensorflow.lite.examples.classification.ClassifierActivity;
import org.tensorflow.lite.examples.classification.DateReceiver;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.tflite.Classifier;
import org.tensorflow.lite.examples.classification.view.community.CommunityFragment;
import org.tensorflow.lite.examples.classification.view.myplants.MyplantsFragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static Context mainContext;
    public Bitmap galleryBitmap = null;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton mainFloating, cameraFloating, galleryFloting;

    private BroadcastReceiver dateReceiver;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //가로화면 안됨.

        mainContext = this;
        dateReceiver = new DateReceiver();


        //floating action button==================================================
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        mainFloating = (FloatingActionButton)findViewById(R.id.mainFloating);
        cameraFloating = (FloatingActionButton)findViewById(R.id.cameraFloating);
        galleryFloting =(FloatingActionButton)findViewById(R.id.galleryFloating);

        mainFloating.setOnClickListener(onClickListener);
        cameraFloating.setOnClickListener(onClickListener);
        galleryFloting.setOnClickListener(onClickListener);

        //바텀네비게이션 설정==============================================================
        bottomNavigationView = findViewById(R.id.nav_view);

        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new HomeFragment()).commit(); //FrameLayout에 fragment.xml 띄우기

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) { //item을 클릭시 id값을 가져와 FrameLayout에 fragment.xml띄우기
                    case R.id.navigation_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new HomeFragment()).commit();
                        break;
                    case R.id.navigation_community:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new CommunityFragment()).commit();
                        break;
                    case R.id.navigation_myplants:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new MyplantsFragment()).commit();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(dateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(dateReceiver);
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent;
            switch (id) {
                case R.id.mainFloating: anim(); break;
                case R.id.cameraFloating:
                    anim();
                    intent = new Intent(mainContext, ClassifierActivity.class);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.galleryFloating:
                    anim();
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //이미지 뷰를 클릭하면 시작되는 함수

        HashMap<String,String> map = new HashMap<>();
        map.put("0", "스투키");
        map.put("1", "나팔꽃");
        map.put("2", "개나리");
        map.put("3", "튤립");
        map.put("4", "장미");
        map.put("5", "해바라기");
        map.put("6", "백합");
        map.put("7", "수국");
        map.put("8", "철쭉");
        map.put("9", "무궁화");
        map.put("10", "아이비");
        map.put("11", "코스모스");

        if(requestCode== 1 && resultCode==RESULT_OK && data!=null) {
            //response에 getData , return data 부분 추가해주어야 한다

            Uri photoUri = data.getData();
            //bitmap 이용
            try {
                galleryBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),photoUri);
                Classifier classifier = Classifier.create(this, Classifier.Model.QUANTIZED_MOBILENET, Classifier.Device.CPU, 1);
                final List<Classifier.Recognition> results = classifier.recognizeImage(galleryBitmap, 0);
                Intent intent = new Intent(mainContext, ResultActivity.class);
                intent.putExtra("result", map.get(results.get(0).getTitle()));
                startActivityForResult(intent, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void anim() {
        if (isFabOpen) {
            cameraFloating.startAnimation(fab_close);
            galleryFloting.startAnimation(fab_close);

            cameraFloating.setClickable(false);
            galleryFloting.setClickable(false);

            isFabOpen = false;
        } else {
            cameraFloating.startAnimation(fab_open);
            galleryFloting.startAnimation(fab_open);

            cameraFloating.setClickable(true);
            galleryFloting.setClickable(true);
            isFabOpen = true;
        }
    }


}