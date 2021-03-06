package org.tensorflow.lite.examples.classification.view;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.tensorflow.lite.examples.classification.ClassifierActivity;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.tflite.Classifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static org.tensorflow.lite.examples.classification.view.MainActivity.mainContext;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;

    View rootView;
    public Bitmap galleryBitmap = null;

    LinearLayout cameraButton;
    LinearLayout albumButton;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        cameraButton = rootView.findViewById(R.id.layout_camera);
        albumButton = rootView.findViewById(R.id.layout_album);

        cameraButton.setOnClickListener(onClickListener);
        albumButton.setOnClickListener(onClickListener);
        return rootView;
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent;
            switch (id) {
                case R.id.layout_camera:
                    intent = new Intent(rootView.getContext(), ClassifierActivity.class);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.layout_album:
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //????????? ?????? ???????????? ???????????? ??????

        if(requestCode== 1 && resultCode==RESULT_OK && data!=null) {
            //response??? getData , return data ?????? ?????????????????? ??????
            HashMap<String,String> map = new HashMap<>();
            map.put("0", "?????????");
            map.put("1", "?????????");
            map.put("2", "?????????");
            map.put("3", "??????");
            map.put("4", "??????");
            map.put("5", "????????????");
            map.put("6", "??????");
            map.put("7", "??????");
            map.put("8", "??????");
            map.put("9", "?????????");
            map.put("10", "?????????");
            map.put("11", "????????????");

            Uri photoUri = data.getData();
            //bitmap ??????
            try {
                galleryBitmap = MediaStore.Images.Media.getBitmap(rootView.getContext().getContentResolver(),photoUri);
                Classifier classifier = Classifier.create(getActivity(), Classifier.Model.QUANTIZED_MOBILENET, Classifier.Device.CPU, 1);
                final List<Classifier.Recognition> results = classifier.recognizeImage(galleryBitmap, 0);
                Intent intent = new Intent(mainContext, ResultActivity.class);
                intent.putExtra("result", map.get(results.get(0).getTitle()));
                startActivityForResult(intent, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }

}