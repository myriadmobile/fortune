package com.myriadmobile.fortune.example;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.myriadmobile.fortune.FortuneItem;
import com.myriadmobile.fortune.FortuneView;
import com.myriadmobile.fortune.R;

import java.util.ArrayList;


public class ExampleActivity extends Activity {

    FortuneView fortuneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        fortuneView = (FortuneView) findViewById(R.id.dialView);

        ArrayList<FortuneItem> dis = new ArrayList<FortuneItem>();
        /*
        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_always_landscape_portrait)));
        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_add)));
        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_agenda)));
        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_camera)));
        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_compass)));
        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_help)));
        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_mapmode)));
        dis.add(new DialItem(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_save)));
        */

        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.black)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.black)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.black)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.black)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.black)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.black)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.black)));
        dis.add(new FortuneItem(BitmapFactory.decodeResource(getResources(), R.drawable.black)));


        /*
        dis.add(new DialItem(Color.BLACK, 1));
        dis.add(new DialItem(Color.BLUE, 1));
        dis.add(new DialItem(Color.RED, 1));
        dis.add(new DialItem(Color.GREEN, 1));
        dis.add(new DialItem(Color.MAGENTA, 1));
        */

        fortuneView.addFortuneItems(dis);

    }


}
