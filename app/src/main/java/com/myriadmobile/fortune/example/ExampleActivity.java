package com.myriadmobile.fortune.example;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.myriadmobile.fortune.FortuneItem;
import com.myriadmobile.fortune.FortuneView;
import com.myriadmobile.fortune.R;

import java.util.ArrayList;
import java.util.Random;


public class ExampleActivity extends Activity {

    FortuneView fortuneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        fortuneView = (FortuneView) findViewById(R.id.dialView);

        ArrayList<FortuneItem> dis = new ArrayList<>();

//        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_always_landscape_portrait)));
//        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_add)));
//        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_agenda)));
//        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_camera)));
//        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_compass)));
//        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_help)));
//        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_mapmode)));
//        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_save)));

//        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_always_landscape_portrait), FortuneItem.HingeType.Fixed));
//        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_add), FortuneItem.HingeType.Fixed));
//        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_agenda), FortuneItem.HingeType.Fixed));
//        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_camera), FortuneItem.HingeType.Fixed));
//        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_compass), FortuneItem.HingeType.Fixed));
//        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_help), FortuneItem.HingeType.Fixed));
//        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_mapmode), FortuneItem.HingeType.Fixed));
//        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_save), FortuneItem.HingeType.Fixed));

        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.image_0)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.image_1)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.image_2)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.image_3)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.image_4)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.image_5)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.image_6)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.image_7)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.image_8)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.image_9)));

//        dis.add(new DialItem(Color.BLACK, 1));
//        dis.add(new DialItem(Color.BLUE, 1));
//        dis.add(new DialItem(Color.RED, 1));
//        dis.add(new DialItem(Color.GREEN, 1));
//        dis.add(new DialItem(Color.MAGENTA, 1));

        fortuneView.addFortuneItems(dis);

        findViewById(R.id.btRandom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random ran = new Random();
                int randomInt = ran.nextInt(fortuneView.getTotalItems());
                //fortuneView.setSelectedItem((fortuneView.getSelectedIndex() == fortuneView.getTotalItems() - 1 ? 0 : fortuneView.getSelectedIndex() + 1));
                //fortuneView.setSelectedItem((fortuneView.getSelectedIndex() == 0 ? fortuneView.getTotalItems() - 1 : fortuneView.getSelectedIndex() - 1));
                fortuneView.setSelectedItem(randomInt);
            }
        });

    }


}
