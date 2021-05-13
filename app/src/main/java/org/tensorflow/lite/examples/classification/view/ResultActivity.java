package org.tensorflow.lite.examples.classification.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tensorflow.lite.examples.classification.CrawlVo;
import org.tensorflow.lite.examples.classification.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ResultActivity extends AppCompatActivity {

    List<String> imgUrls  = new ArrayList<String>();

    HashMap<String,String> map = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        map.put("0", "스투키");
        map.put("1", "장미");
        map.put("2", "해바라기");
        map.put("3", "폼폼");
        map.put("4", "카네이션");
        map.put("5", "명자란");

        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        String result = (String)intent.getExtras().get("result");
        String flowerName = map.get(result);

        TextView textView = findViewById(R.id.text_explain);

        ImageView imageView = findViewById(R.id.img_input);

        BackgroundTask async=new BackgroundTask(flowerName);
        try {
            CrawlVo vo = async.execute().get();
            imgUrls = vo.getImgSrcList();
//            picture.setImageBitmap(vo.getImgSrcList().get(2));
            textView.setText(vo.getContents());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Picasso.get()
                .load("https://helpx.adobe.com/content/dam/help/en/photoshop/using/convert-color-image-black-white/jcr_content/main-pars/before_and_after/image-before/Landscape-Color.jpg")
                .into(imageView);

//        imgUrls.add("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/View_from_the_Window_at_Le_Gras%2C_Joseph_Nic%C3%A9phore_Ni%C3%A9pce%2C_uncompressed_UMN_source.png/270px-View_from_the_Window_at_Le_Gras%2C_Joseph_Nic%C3%A9phore_Ni%C3%A9pce%2C_uncompressed_UMN_source.png");
//        imgUrls.add("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/View_from_the_Window_at_Le_Gras%2C_Joseph_Nic%C3%A9phore_Ni%C3%A9pce%2C_uncompressed_UMN_source.png/270px-View_from_the_Window_at_Le_Gras%2C_Joseph_Nic%C3%A9phore_Ni%C3%A9pce%2C_uncompressed_UMN_source.png");
//        imgUrls.add("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/View_from_the_Window_at_Le_Gras%2C_Joseph_Nic%C3%A9phore_Ni%C3%A9pce%2C_uncompressed_UMN_source.png/270px-View_from_the_Window_at_Le_Gras%2C_Joseph_Nic%C3%A9phore_Ni%C3%A9pce%2C_uncompressed_UMN_source.png");
//        imgUrls.add("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/View_from_the_Window_at_Le_Gras%2C_Joseph_Nic%C3%A9phore_Ni%C3%A9pce%2C_uncompressed_UMN_source.png/270px-View_from_the_Window_at_Le_Gras%2C_Joseph_Nic%C3%A9phore_Ni%C3%A9pce%2C_uncompressed_UMN_source.png");
//        imgUrls.add("https://helpx.adobe.com/content/dam/help/en/photoshop/using/convert-color-image-black-white/jcr_content/main-pars/before_and_after/image-before/Landscape-Color.jpg");

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(230,0,230,0);
        viewPager.setPageMargin(getResources().getDisplayMetrics().widthPixels / -9);
        viewPager.setAdapter(new ViewpagerAdapter(this, imgUrls));
    }

}

class BackgroundTask extends AsyncTask<Integer , Integer , CrawlVo> {

    private String flowerName;

    public BackgroundTask(String flowerName){
        this.flowerName = flowerName;
    }

    @Override
    protected CrawlVo doInBackground(Integer... integers) {

        String url = "https://ko.wikipedia.org/wiki/"+flowerName;
        Log.e("ksy", flowerName);
        Document doc = null;

        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements pElements = doc.getElementsByTag("P");
        String desc = pElements.get(0).text();


        String imgSrc = "https://www.google.co.kr/search?q=" + flowerName + "&sxsrf=ALeKk01jh0eiM_TSzZd57Tq41MUha2oStA:1620287330088&source=lnms&tbm=isch&sa=X&ved=2ahUKEwj3r_nVyLTwAhUn7GEKHc2hAFcQ_AUoAXoECAEQAw&biw=1536&bih=719&dpr=1.25";
        try {
            doc = Jsoup.connect(imgSrc).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> imgSrcList = new ArrayList<String>();
        Elements imgElements = doc.getElementsByAttributeValue("class", "rg_i Q4LuWd");

        for (int i = 0; i < imgElements.size(); i++) {
            Element tmpEle = imgElements.get(i);
            String imgUrl = tmpEle.attr("data-src");

            if (imgUrl.length() > 10) {
                imgSrcList.add(imgUrl);
            }

            if (imgSrcList.size() == 5) break;
        }

        System.out.print(desc);

        System.out.println(imgSrcList);


        String imgUrl = null;
        CrawlVo vo = new CrawlVo();

            List<String> stringList = new ArrayList<>();

            for (int i = 0; i < imgSrcList.size(); i++) {

                imgUrl = imgSrcList.get(i);
//                ImgUrl = new URL(imgSrcList.get(0));

//                Bitmap bmp = BitmapFactory.decodeStream(ImgUrl.openConnection().getInputStream());
                stringList.add(imgUrl);


            }

            vo.setImgSrcList(stringList);
            vo.setContents(desc);


        return vo;
    }
}