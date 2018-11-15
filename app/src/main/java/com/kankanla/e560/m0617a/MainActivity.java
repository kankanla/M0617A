package com.kankanla.e560.m0617a;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Item_config.CallBack {
    private TextView t1, t2, t3, t4;
    private ListView listView;
    private Button b1, b2, b3, b4;
    private SQL_db sql_db;
    private String timer_id;
    private int countss;
    private Handler handler;
    private Timer timer;
    private Cursor cursor;
    private ListAdapter listAdapter;
    private int longchk;
    private final int RUN_NOW = 1;
    private final int RUN_STOP = 0;
    private final int TIMEMODE_DOWN = 0;
    private final int TIMEMODE_UP = 1;
    private int Time_mode = TIMEMODE_DOWN;
    private int RUN_STATUS;
    private int lin_ID;
    private SoundPool soundPool;
    private Ringtone ringtone;
    private int ringtone_time = 5;

    {
        handler = new Handler();
        listAdapter = new ListAdapter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find_button_view();
        set_button_click();
        set_database();
        show_list_adapter();
        lin("12");
        admo();
    }

    protected void set_database() {
        sql_db = new SQL_db(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        show_list_adapter();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        show_list_adapter();
    }

    //    更新时间列表
    protected void show_list_adapter() {
        cursor = sql_db.show_item();
        listAdapter.setCursor(cursor);
        listView.setAdapter(listAdapter);

//       アイテムの詳細設定
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
//          長押し、ITEMの設定ダイアログを表示します
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longchk = 1;
                if (RUN_STATUS == RUN_STOP) {
                    cursor.moveToPosition(position);
                    Item_config item_config = new Item_config();
                    item_config.item_id(cursor.getString(cursor.getColumnIndex("_id")));
                    item_config.show(getFragmentManager(), "abc");
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (RUN_STATUS == RUN_STOP) {
                    if (longchk == 0) {
                        cursor.moveToPosition(position);
                        countss = Integer.parseInt(cursor.getString(cursor.getColumnIndex("itme_time")));
                        timer_id = cursor.getString(cursor.getColumnIndex("_id"));
                        show_timer();
                        String xx = cursor.getString(cursor.getColumnIndex("auto_start"));
                        if (xx.equals("1")) {
                            start_function();
                        }
                    } else {
                        longchk = 0;
                    }
                }
            }
        });
    }

    protected ArrayList<Integer> slipt_time(int time) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        int hh = time / 60;
        int ss = time - hh * 60;
        int hh1 = hh % 100 / 10;
        int hh2 = hh % 10;
        int ss1 = ss % 100 / 10;
        int ss2 = ss % 10;
        arrayList.add(hh1);
        arrayList.add(hh2);
        arrayList.add(ss1);
        arrayList.add(ss2);
        return arrayList;
    }

    protected void show_timer() {
        if (countss > 55 * 60) {
            countss = 55 * 60;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hh = countss / 60;
                int ss = countss - hh * 60;
                int hh1 = hh % 100 / 10;
                int hh2 = hh % 10;
                int ss1 = ss % 100 / 10;
                int ss2 = ss % 10;
                t1.setText(String.valueOf(hh1));
                t2.setText(String.valueOf(hh2));
                t3.setText(String.valueOf(ss1));
                t4.setText(String.valueOf(ss2));
                if (countss == 0) {
                    if (timer != null) {
                        timer.cancel();
                        b1.setEnabled(true);
                        b2.setEnabled(true);
                        b4.setEnabled(true);
                        RUN_STATUS = RUN_STOP;
                        screen_off();
                    }
                } else if (countss == ringtone_time) {
                    if (Time_mode == TIMEMODE_DOWN && RUN_STATUS == RUN_NOW && sql_db.get_No_Sound(timer_id) == 0) {
                        lin3();
                    }
                }
            }
        });
    }

    protected void lin3() {
        SQL_db sql_db = new SQL_db(this);
        String uri = sql_db.get_sound_url(timer_id);
        if (uri == null) {
            ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getValidRingtoneUri(this));
        } else {
            ringtone = RingtoneManager.getRingtone(this, Uri.parse(uri));
        }
        ringtone.play();
    }

    protected void lin(String id) {
        AssetManager assetManager = getAssets();
        AssetFileDescriptor assetFileDescriptor = null;
        AssetFileDescriptor assetFileDescriptor2 = null;
        try {
            assetFileDescriptor = assetManager.openFd("Ring01.wav");
            assetFileDescriptor2 = assetManager.openFd("Darling_Ranch_Sting.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder builder = new SoundPool.Builder();
            AudioAttributes.Builder builder1 = new AudioAttributes.Builder();
            builder1.setUsage(AudioAttributes.USAGE_ALARM);
            builder1.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);
            builder.setAudioAttributes(builder1.build());
            builder.setMaxStreams(2);
            soundPool = builder.build();
            lin_ID = soundPool.load(assetFileDescriptor, 12);
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
            lin_ID = soundPool.load(assetFileDescriptor2, 1);
        }
    }

    protected void screen_on() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected void screen_off() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    protected void admo() {
//        xmlns:ads="http://schemas.android.com/apk/res-auto"
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.main_pg);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        AdView adView = new AdView(this);
//        adView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        adView.setLayoutParams(layoutParams);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adView.setAdSize(AdSize.BANNER);
//        アプリ ID: ca-app-pub-0547405774182700~3369374072
//        広告ユニット ID: ca-app-pub-0547405774182700/8454002186
//        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        adView.setAdUnitId("ca-app-pub-0547405774182700/8454002186");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        viewGroup.addView(adView);
    }

    protected void start_function() {
        screen_on();
        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();

        if (countss == 0) {
//                  如果countss为0时开始正计时
            timer.schedule(new Ctimer_upload(), 0, 1000);
        } else {
//                  如果countss大于0时开始到计时l
            if (!sql_db.find_time(String.valueOf(countss))) {
//                        增加一个条目在数据库
                timer_id = sql_db.create_item(String.valueOf(countss), String.valueOf(countss));
            } else {
//                        更新数据库中的一个条目
                sql_db.up_last_acctime(String.valueOf(countss));
                sql_db.up_count(String.valueOf(countss));
            }
            show_list_adapter();
            Ctimer_down ctimer_down = new Ctimer_down();
            timer.schedule(ctimer_down, 0, 1000);
        }

        b4.setEnabled(false);
        b1.setEnabled(false);
        b2.setEnabled(false);
        b3.setText(R.string.timer_stop);
        RUN_STATUS = RUN_NOW;
    }


    protected void set_button_click() {
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_function();
            }
        });

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b3.getText().toString() == getString(R.string.timer_clean)) {
                    countss = countss + 60 * 10;
                    show_timer();
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countss = countss + 60;
                show_timer();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countss = countss + 1;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        show_timer();
                    }
                });
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_timer();
                if (b3.getText() == getString(R.string.timer_clean)) {
                    countss = 0;
                    show_timer();
                    b3.setText(R.string.timer_clean);
                }
                if (b3.getText() == getString(R.string.timer_stop)) {
                    timer.cancel();
                    RUN_STATUS = RUN_STOP;
                    if (ringtone != null) {
                        ringtone.stop();
                    }
                    b3.setText(R.string.timer_clean);
                    b4.setEnabled(true);
                    screen_off();
                }
            }
        });
    }

    @Override
    public void re_view_item() {
        show_list_adapter();
        longchk = 0;
    }

    protected class Ctimer_down extends TimerTask {
        @Override
        public void run() {
            Time_mode = TIMEMODE_DOWN;
            countss = countss - 1;
            show_timer();
        }
    }

    protected class Ctimer_upload extends TimerTask {
        @Override
        public void run() {
            Time_mode = TIMEMODE_UP;
            countss = countss + 1;
            show_timer();
        }
    }

    protected void find_button_view() {
        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);
        b3 = (Button) findViewById(R.id.b3);
        b4 = (Button) findViewById(R.id.b4);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        t4 = (TextView) findViewById(R.id.t4);
        b1.setText(R.string.time_Minute);
        b2.setText(R.string.time_second);
        b4.setText(R.string.timer_start);
        listView = (ListView) findViewById(R.id.list_view);
    }


    protected class ListAdapter extends BaseAdapter {
        private Cursor cursor;

        public void setCursor(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return cursor.moveToPosition(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            cursor.moveToPosition(position);
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.adapter_list_view, null);
            } else {
                view = convertView;
            }

            ArrayList<Integer> arrayList = slipt_time(Integer.parseInt(cursor.getString(1)));
            TextView lv1 = (TextView) view.findViewById(R.id.lv1);
            TextView lv2 = (TextView) view.findViewById(R.id.lv2);
            TextView lv3 = (TextView) view.findViewById(R.id.lv3);
            TextView lv4 = (TextView) view.findViewById(R.id.lv4);
            lv1.setText(String.valueOf(arrayList.get(0)));
            lv2.setText(String.valueOf(arrayList.get(1)));
            lv3.setText(String.valueOf(arrayList.get(2)));
            lv4.setText(String.valueOf(arrayList.get(3)));

            TextView textView2 = (TextView) view.findViewById(R.id.list_item_t2);
            ImageView imageView3 = (ImageView) view.findViewById(R.id.list_item_t3);
            TextView textView4 = (TextView) view.findViewById(R.id.list_item_t4);
            ImageView imageView5 = (ImageView) view.findViewById(R.id.list_item_t5);
            if (cursor.getString(cursor.getColumnIndex("comment")).trim().isEmpty()) {
                textView2.setText(getString(R.string.up_comment));
            } else {
                textView2.setText("  " + cursor.getString(cursor.getColumnIndex("comment")).trim());
            }

            String aut = cursor.getString(cursor.getColumnIndex("auto_start"));
//            textView3.setText("自動スタート");
            imageView3.setMaxHeight(9);
            if (aut.equals("1")) {
                imageView3.setImageResource(R.mipmap.auto_start);
            } else {
                imageView3.setImageResource(R.mipmap.no_auto_start);
            }

//            textView5.setText("ノーサウンド");
            String nos = cursor.getString(cursor.getColumnIndex("no_sound"));
            if (nos.equals("1")) {
                imageView5.setImageResource(R.mipmap.sound_off);
            } else {
                imageView5.setImageResource(R.mipmap.sound_on);
            }

            textView4.setTextColor(Color.argb(255, 255, 106, 25));

            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("count"))) < 90) {
                textView4.setText("" + cursor.getString(cursor.getColumnIndex("count")));
            } else {
                textView4.setText("9+");
            }

            TextView textView = (TextView) view.findViewById(R.id.sound_title);
            textView.setText(get_sound_url_title(cursor.getString(cursor.getColumnIndex("_id"))));

            return view;
        }

        protected String get_sound_url_title(String id) {
            System.out.println(Uri.parse(get_sound_url(id)));
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(get_sound_url(id)));
            String title = ringtone.getTitle(getApplicationContext());
            System.out.println(title);
            return title;
        }

        protected String get_sound_url(String id) {
            SQL_db sql_db = new SQL_db(getApplicationContext());
            String uri = sql_db.get_sound_url(id);
            if (uri == null) {
                Uri uri2 = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_ALARM);
                return uri2.toString();
            } else {
                return uri;
            }
        }
    }

}
