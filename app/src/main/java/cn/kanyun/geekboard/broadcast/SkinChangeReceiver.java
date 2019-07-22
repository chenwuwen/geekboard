package cn.kanyun.geekboard.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import cn.kanyun.geekboard.R;
import cn.kanyun.geekboard.activity.VerificationActivity;
import cn.kanyun.geekboard.entity.Constant;
import cn.kanyun.geekboard.widget.PreviewPopup;

/**
 * 皮肤切换广播接受者
 */
public class SkinChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "SkinChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constant.BOARD_SKIN_SWITCH)) {
            Log.d(TAG, "广播接受者接收到了皮肤切换的广播");
            if (verifyBoardUseStatus(context)) {
                PreviewPopup previewPopup = new PreviewPopup(context);
                //设置是否点击popup外部时dismiss
                previewPopup.setOutSideDismiss(true);
//                设置是否允许back键dismiss
                previewPopup.setBackPressEnable(true);

//                获取输入框
                EditText editText = previewPopup.getContentView().findViewById(R.id.preview_input);
//               EditText获取焦点
//                editText.setFocusable(true);
//                editText.setFocusableInTouchMode(true);
//                editText.requestFocus();

                Activity activity = previewPopup.getContext();
//                键盘弹出时,禁止把布局顶上去 https://www.jianshu.com/p/6b90006dbdfe
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//                是否自动弹起输入法
                previewPopup.setAutoShowInputMethod(editText, true);
//                显示Popup
                previewPopup.showPopupWindow();


            } else {
                Toast.makeText(context, "请切换到GeekBoard后进行预览", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * 验证键盘当前是否正在使用
     *
     * @return
     */
    private boolean verifyBoardUseStatus(Context context) {
        String curInputMethodId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        return curInputMethodId.equals(VerificationActivity.selfKeyBoardServiceId);
    }
}
