package cn.kanyun.geekboard;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;


/**
 * Created by Ruby on 02/06/2016.
 */
public class MainActivity extends AppCompatActivity {
    RadioGroup radioGroupColour,radioGroupLayout;
    SeekBar seekBar;
    Context context;



   final String RADIO_INDEX_COLOUR = "RADIO_INDEX_COLOUR";
   final String RADIO_INDEX_LAYOUT = "RADIO_INDEX_LAYOUT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart1", true);

                //  If the activity has never started before...
                if (isFirstStart) {


                    Button change = (Button) findViewById(R.id.change_button);
                    change.setVisibility(View.GONE);

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart1", false);

                    //  Apply changes
                    e.apply();

                } else {
                    //Dev
                    //SharedPreferences.Editor e = getPrefs.edit();
                    //
                    //e.putBoolean("firstStart1", true);
                    //REMOVE BEFORE PUBLISHING ^
                    //
                    //e.apply();
                }

            }
        });

        // Start the thread
        t.start();

        //debug only
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);




        seekBar = (SeekBar) findViewById(R.id.size_seekbar);
        // perform seek bar change listener event used for getting the progress value
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = seekBar.getProgress();

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(MainActivity.this, "Seek bar progress is :" + progressChangedValue,
//                        Toast.LENGTH_SHORT).show();
                SavePreferences("SIZE", progressChangedValue);


            }
        });

        radioGroupColour = (RadioGroup) findViewById(R.id.radiogroupcolour);
        radioGroupColour.setOnCheckedChangeListener(radioGroupOnCheckedChangeListenerColour);

        radioGroupLayout = (RadioGroup) findViewById(R.id.radiogrouplayout);
        radioGroupLayout.setOnCheckedChangeListener(radioGroupOnCheckedChangeListenerLayout);


        LoadPreferences();

    }



    RadioGroup.OnCheckedChangeListener radioGroupOnCheckedChangeListenerColour =
            new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    RadioButton checkedRadioButtonColour = (RadioButton) radioGroupColour.findViewById(checkedId);
                    int checkedIndexColour = radioGroupColour.indexOfChild(checkedRadioButtonColour);
                    SavePreferences(RADIO_INDEX_COLOUR, checkedIndexColour);

                }
            };

    RadioGroup.OnCheckedChangeListener radioGroupOnCheckedChangeListenerLayout =
            new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {


                    RadioButton checkedRadioButtonLayout = (RadioButton) radioGroupLayout.findViewById(checkedId);
                    int checkedIndexLayout = radioGroupLayout.indexOfChild(checkedRadioButtonLayout);
                    SavePreferences(RADIO_INDEX_LAYOUT, checkedIndexLayout);

                }
            };


    /**
     * 保存配置到配置文件
     * @param key
     * @param value
     */
    private void SavePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public void changeButton(View v) {

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showInputMethodPicker();

//        Button enable = (Button) findViewById(R.id.enable_button);
//        enable.setText("Change Keyboard");
//
//        String id = Settings.Secure.getString(
//                getContentResolver(),
//                Settings.Secure.DEFAULT_INPUT_METHOD
//        );
//
//        if(!(id.equals("com.gazlaws.codeboard/.CodeBoardIME"))){
//            InputMethodManager imm = (InputMethodManager)
//                    getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showInputMethodPicker();
//        }

    }

    public void previewToggle(View v) {
        CheckBox preview = (CheckBox) findViewById(R.id.check_preview);
        if (preview.isChecked()) {
            SavePreferences("PREVIEW", 1);
        } else SavePreferences("PREVIEW", 0);
        closeKeyboard(v);

    }

    public void soundToggle(View v) {
        CheckBox preview = (CheckBox) findViewById(R.id.check_sound);
        if (preview.isChecked()) {
            SavePreferences("SOUND", 1);
        } else SavePreferences("SOUND", 0);
        closeKeyboard(v);
    }

    /**
     * 振动开关
     * @param v
     */
    public void vibratorToggle(View v) {
        CheckBox preview = (CheckBox) findViewById(R.id.check_vibrator);
        if (preview.isChecked()) {
            SavePreferences("VIBRATE", 1);
        } else SavePreferences("VIBRATE", 0);
        closeKeyboard(v);
    }

    public void arrowToggle(View v) {
        CheckBox preview = (CheckBox) findViewById(R.id.check_no_arrow);
        if (preview.isChecked()) {
            SavePreferences("ARROW_ROW", 0);
        } else SavePreferences("ARROW_ROW", 1);
        closeKeyboard(v);
    }


    public void closeKeyboard(View v) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

    /**
     * 加载键盘配置
     */
    private void LoadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);

        int savedRadioColour = sharedPreferences.getInt(RADIO_INDEX_COLOUR, 0);
        RadioButton savedCheckedRadioButtonColour = (RadioButton) radioGroupColour.getChildAt(savedRadioColour);
        savedCheckedRadioButtonColour.setChecked(true);

        int savedRadioLayout = sharedPreferences.getInt(RADIO_INDEX_LAYOUT, 0);
        RadioButton savedCheckedRadioButtonLayout = (RadioButton) radioGroupLayout.getChildAt(savedRadioLayout);
        savedCheckedRadioButtonLayout.setChecked(true);

        int setPreview  = sharedPreferences.getInt("PREVIEW", 0);
        int setSound    = sharedPreferences.getInt("SOUND"  , 1);
        int setVibrator = sharedPreferences.getInt("VIBRATE", 1);
        int setSize     = sharedPreferences.getInt("SIZE"   , 2);

        int setArrow = sharedPreferences.getInt("ARROW_ROW", 1);
        CheckBox preview = (CheckBox) findViewById(R.id.check_preview);

        CheckBox sound   = (CheckBox) findViewById(R.id.check_sound);
//      按键振动
        CheckBox vibrate = (CheckBox) findViewById(R.id.check_vibrator);
        CheckBox noarrow = (CheckBox) findViewById(R.id.check_no_arrow);
        SeekBar size = (SeekBar) findViewById(R.id.size_seekbar);

        if (setPreview == 1)
            preview.setChecked(true);
        else
            preview.setChecked(false);

        if (setSound == 1)
            sound.setChecked(true);
        else
            sound.setChecked(false);

        if (setVibrator == 1)
            vibrate.setChecked(true);
        else
            vibrate.setChecked(false);

        if (setArrow == 1)
            noarrow.setChecked(false);
        else
            noarrow.setChecked(true);

        size.setProgress(setSize);

    }

    /**
     * 打开应用市场
     * @param v
     */
    public void openPlay(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("market://details?id=com.gazlaws.codeboard"));
        startActivity(i);
    }

    /**
     * 打开个人主页
     * @param v
     */
    public void openUserHome(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://github.com/chenwuwen"));
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "未安装相关应用程序", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 使用教程
     * @param v
     */
    public void openTutorial(View v){
        Intent i = new Intent(MainActivity.this, IntroActivity.class);
        startActivity(i);
    }

    /**
     * 意见反馈
     * @param v
     */
    public void feedback(View v) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        通过是否为NULL判断网络
//        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
//        NetworkInfo gprsInfo = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);

        NetworkInfo activeNetWork = connectivityManager.getActiveNetworkInfo();
        if (activeNetWork != null) {
            if (activeNetWork.getType() == connectivityManager.TYPE_WIFI) {
//                Toast.makeText(context, "当前连接的是WIFI",Toast.LENGTH_SHORT).show();
            }else {
//                Toast.makeText(context, "当前连接的是GPRS",Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(context,FeedBackActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(context, "当前没有网络连接",Toast.LENGTH_SHORT).show();
        }
    }


}






