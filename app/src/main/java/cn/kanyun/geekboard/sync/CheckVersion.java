package cn.kanyun.geekboard.sync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 步骤1：创建AsyncTask子类
 * 注：
 * a. 继承AsyncTask类
 * b. 为3个泛型参数指定类型；若不使用，可用java.lang.Void类型代替
 * c. 根据需求，在AsyncTask子类内实现核心方法
 * 具体说明：
 * a. 第一个参数：开始异步任务执行时传入的参数类型，对应excute()中传递的参数
 * b. Progress：异步任务执行过程中，返回下载进度值的类型
 * c. 第三个参数：异步任务执行完成后，返回的结果类型，与doInBackground()的返回值类型保持一致
 * 注：
 * a. 使用时并不是所有类型都被使用
 * b. 若无被使用，可用java.lang.Void类型代替
 * c. 若有不同业务，需额外再写1个AsyncTask的子类
 * 步骤2：创建AsyncTask子类的实例对象（即 任务实例）
 * 注：AsyncTask子类的实例必须在UI线程中创建
 * <p>
 * 步骤3：手动调用execute(Params... params) 从而执行异步线程任务
 * 注：
 * a. 必须在UI线程中调用
 * b. 同一个AsyncTask实例对象只能执行1次，若执行第2次将会抛出异常
 * c. 执行任务中，系统会自动调用AsyncTask的一系列方法：onPreExecute() 、doInBackground()、onProgressUpdate() 、onPostExecute()
 * d. 不能手动调用上述方法
 *
 * 外部如果需要使用AsyncTask任务，需要首先实例化,再调用execute方法，如：
 * CheckVersion check = new CheckVersion()
 * check.execute()
 */


public class CheckVersion extends AsyncTask<Activity, Void, Void> {

    /**
     * github获取仓库最新Release版本号API
     * github API :https://developer.github.com/v3/repos/releases/#get-the-latest-release
     */
    public static final String checkVersionUrl = "https://api.github.com/repos/chenwuwen/geekboard/releases/latest";
    public static final String TAG = "CheckVersion";

    private String responseText;
    private Context context;



    public CheckVersion(Context context) {
        this.context = context;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }



    /**
     * 方法1：onPreExecute（）
     * 作用：执行 线程任务前的操作 可以操作UI
     * 注：根据需求复写
     */
    @Override
    protected void onPreExecute() {
        Log.i(TAG, "准备检查新版本");
    }

    /**
     * 方法2：doInBackground（）
     * 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
     * 注：必须复写，从而自定义线程任务
     * 这里也就是你要异步做的事情
     *
     * @param voids
     * @return
     */
    @Override
    protected Void doInBackground(Activity... activities) {
        OkGo.<String>get(checkVersionUrl)
                // 请求的 tag, 主要用于取消对应的请求
                .tag(this)
                // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheKey("checkVersion")
                // 缓存模式，详细请看缓存介绍
                .cacheMode(CacheMode.NO_CACHE)
//                重试次数
                .retryCount(3)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.i(TAG, "检测新版本成功");
                        setResponseText(response.body());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
        return null;
    }

    /**
     * 方法3：onProgressUpdate（）
     * 作用：在主线程 显示线程任务执行的进度
     * 注：根据需求复写,从而自定义UI操作
     *
     * @param voids
     */
    @Override
    protected void onProgressUpdate(Void... voids) {
        Log.d(TAG, "异步操作返回");

    }


    /**
     * 方法4：onPostExecute（）
     * 作用：接收线程任务执行结果、将执行结果显示到UI组件
     * 注：必须复写，从而自定义UI操作
     *
     * @param result
     */
    @Override
    protected void onPostExecute(Void result) {
        String ret = getResponseText();
        if (StringUtils.isNotBlank(ret)) {
            try {
                JSONObject release = new JSONObject(ret);
//                获取当前软件版本
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                String currentVersion = pInfo.versionName;
                String latestVersion = release.getString("tag_name");
                boolean isPreRelease = release.getBoolean("prerelease");
                if (Double.valueOf(latestVersion) > Double.valueOf(currentVersion)) {

                    // 获取新版本下载地址
                    String downloadUrl = release.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");

//                    AlertDialog.Builder updateDialog =
//                            new AlertDialog.Builder(MainActivity.this);
//                    View updateDialogView = LayoutInflater.from(MainActivity.this)
//                            .inflate(R.layout.update_dialog,null);
//                    updateDialog.setTitle("发现新版本");
//                    updateDialog.show();
                    // 不使用下载管理器。因为下载的APK已重命名，无法安装
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
//                    Toast.makeText(context, context.getString(R.string.update_new_seg1) + latestVersion + context.getString(R.string.update_new_seg2), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException | PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 方法5：onCancelled()
     * 作用：将异步任务设置为：取消状态
     */
    @Override
    protected void onCancelled() {
        Log.d(TAG, "异步任务取消");
    }
}

