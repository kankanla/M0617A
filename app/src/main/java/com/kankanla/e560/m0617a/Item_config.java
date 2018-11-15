package com.kankanla.e560.m0617a;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by E560 on 2017/07/22.
 */

public class Item_config extends DialogFragment {
    private String id;
    private View view;
    private Button button;

    public void item_id(String id) {
        this.id = id;
    }

    //    インタフェース、ダイアログ画面が閉じだときにライフサイクルで実行します。
    public interface CallBack {
        public void re_view_item();
    }

    CallBack callBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("----------------------------onAttach-----------------------------");
        callBack = (CallBack) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("----------------------------onAttach-----------------------------");
        callBack = (CallBack) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = inflater.inflate(R.layout.item_config_dialog, null);
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.item_config_Save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView textView = (TextView) view.findViewById(R.id.editText2);
                save_commant(id, textView.getText().toString());
                onDismiss(getDialog());
            }
        });

        builder.setNegativeButton(getString(R.string.item_config_Delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                del_item(id);
                onDismiss(getDialog());
            }
        });

        TextView textView = (TextView) view.findViewById(R.id.editText2);
        if (show_comment(id).equals(getString(R.string.up_comment))) {
            textView.setText(" ");
        } else {
            textView.setText(show_comment(id).trim());
        }

        // 设置自动启动/开始
        Switch aSwitch = (Switch) view.findViewById(R.id.switch1);
        aSwitch.setText(R.string.item_config_AutoStart);
        aSwitch.setChecked(find_autocheck(id));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    set_autocheck(id, 1);
                } else {
                    set_autocheck(id, 0);
                }
            }
        });

        //设置是否有声音
        Switch bSwitch = (Switch) view.findViewById(R.id.switch2);
        bSwitch.setText(R.string.item_config_noSound);
        bSwitch.setChecked(find_No_Sound(id));
        bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    set_No_Sound(id, 1);
                } else {
                    set_No_Sound(id, 0);
                }
            }
        });
        TextView select_Sound1 = (TextView) view.findViewById(R.id.select_sound1);
        select_Sound1.setText(getString(R.string.config_sound_title));
        TextView select_Sound2 = (TextView) view.findViewById(R.id.select_sound2);
        select_Sound2.setText(get_sound_url_title(id));
        select_Sound2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lin2("22");
            }
        });
        admo();
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 999) {
            Uri uri = (Uri) data.getExtras().get(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
            String name = ringtone.getTitle(getActivity());
            System.out.println(name);
            System.out.println(uri);
            set_sound_url(id, uri.toString());

            TextView select_Sound = (TextView) view.findViewById(R.id.select_sound2);
            select_Sound.setText(get_sound_url_title(id));
        }
    }

    protected void lin2(String id) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.select_sound_title));
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false); // サイレントは見せない
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM); // アラーム音
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);// デフォルトは表示しない
        startActivityForResult(intent, 999);
    }

    protected void admo() {
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.config);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        AdView adView = new AdView(getActivity());
        adView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.common_google_signin_btn_text_light_default));
        adView.setLayoutParams(layoutParams);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        viewGroup.addView(adView);
    }

    protected void set_sound_url(String id, String uri) {
        SQL_db sql_db = new SQL_db(getActivity());
        sql_db.up_sound_url(id, uri);
    }

    protected String get_sound_url(String id) {
        SQL_db sql_db = new SQL_db(getActivity());
        String uri = sql_db.get_sound_url(id);
        if (uri == null) {
            Uri uri2 = RingtoneManager.getActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_ALARM);
            return uri2.toString();
        } else {
            return uri;
        }
    }

    protected String get_sound_url_title(String id) {
        System.out.println(Uri.parse(get_sound_url(id)));
        Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), Uri.parse(get_sound_url(id)));
        String title = ringtone.getTitle(getActivity());
        System.out.println(title);
        return title;
    }

    protected String show_comment(String id) {
        SQL_db sql_db = new SQL_db(getActivity());
        return sql_db.show_comment(id);
    }

    protected void save_commant(String id, String comment) {
        SQL_db sql_db = new SQL_db(getActivity());
        sql_db.up_comment(id, comment);
    }

    protected void set_autocheck(String id, Integer chk) {
        SQL_db sql_db = new SQL_db(getActivity());
        sql_db.setAuto_Status(id, chk);
    }

    protected boolean find_autocheck(String id) {
        SQL_db sql_db = new SQL_db(getActivity());
        int i = sql_db.getAuto_Status(id);
        if (i == 1) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean find_No_Sound(String id) {
        SQL_db sql_db = new SQL_db(getActivity());
        int i = sql_db.get_No_Sound(id);
        if (i == 1) {
            return true;
        } else {
            return false;
        }
    }

    protected void set_No_Sound(String id, Integer chk) {
        SQL_db sql_db = new SQL_db(getActivity());
        sql_db.set_No_Sound(id, chk);
    }

    protected void del_item(String id) {
        SQL_db sql_db = new SQL_db(getActivity());
        sql_db.delete_id_item(id);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        callBack.re_view_item();
    }
}
