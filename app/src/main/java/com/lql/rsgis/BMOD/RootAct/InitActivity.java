package com.lql.rsgis.BMOD.RootAct;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.lql.rsgis.BMOD.MapModule.View.MapActivity1;
import com.lql.rsgis.BMOD.ProjectsModule.Model.ProjectInfo;
import com.lql.rsgis.BMOD.ProjectsModule.View.MainActivity;
import com.lql.rsgis.Config.AppWorksSpaceInit;
import com.lql.rsgis.Config.SystemDirPath;
import com.lql.rsgis.Permission.PermissionsActivity;
import com.lql.rsgis.Permission.PermissionsChecker;
import com.lql.rsgis.R;
import com.lql.rsgis.Utils.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gisluq.lib.Util.AppUtils;

/**
 *  应用程序初始化页面
 */
public class InitActivity extends AppCompatActivity {

    private static String TAG = "InitActivity";
    private final int SPLASH_DISPLAY_LENGHT = 2000; // 延迟时间
    private Context context = null;

    private static final int REQUEST_CODE = 0; // 请求码
    // 所需的全部权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//写入存储
            Manifest.permission.ACCESS_FINE_LOCATION,//位置信息
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA //相机
    };
    private static PermissionsChecker mPermissionsChecker; // 权限检测器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_init);

        mPermissionsChecker = new PermissionsChecker(this);
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }else {
            appInit();
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    //判断是否为平板设备
//                    boolean ispad = SysUtils.isPad(context);
//                    if (ispad){
                        startActivity();
//                    }else{
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setMessage("检测到当前设备并非平板，继续安装此应用程序将会出现异常，是否任然继续安装此应用程序？");
//                        builder.setTitle("系统提示");
//                        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                InitActivity.this.finish();
//                            }
//                        });
//                        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                startActivity();
//                                dialog.dismiss();
//                                ToastUtils.showShort(context,"应用程序打开失败，请使用平台后再试");
//                            }
//                        });
//                        builder.setCancelable(false);//点击外部不消失
//                        builder.create().show();
//                    }
                }catch (Exception e){
                    Log.e(TAG,e.toString());
                }
            }
        }, SPLASH_DISPLAY_LENGHT);

        TextView textView = (TextView)this.findViewById(R.id.activity_init_versionTxt);
        String version = AppUtils.getVersionName(this);
        textView.setText("版本号:"+version);
    }

    /**
     * 跳转
     */
    private void startActivity() {
        List<ProjectInfo> projectInfos = getProjectInfos();


        Intent mainIntent = new Intent(context,MapActivity1.class);
        mainIntent.putExtra("DirName",projectInfos.get(0).DirName);
        mainIntent.putExtra("DirPath",projectInfos.get(0).DirPath);
        context.startActivity(mainIntent);
        ((Activity)context).finish();
    }

    /**
     *  应用程序初始化
     */
    private void appInit() {
        boolean isOk = AppWorksSpaceInit.init(context);//初始化系统文件夹路径
    }

    /**
     * 弹出权限获取提示信息
     */
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }



    /**
     * 获取工程信息列表
     * @return
     */
    private List<ProjectInfo> getProjectInfos() {
        List<FileUtils.FileInfo> fileInfos = FileUtils.getFileListInfo(SystemDirPath.getProjectPath(context),"folder");
        // 获取文件名列表
        List<String> fileNames = new ArrayList<>();
        if (fileInfos!=null){
            for (int i=0;i<fileInfos.size();i++){
                fileNames.add(fileInfos.get(i).FileName);
            }
        }
        Collections.sort(fileNames);//排序

        List<ProjectInfo> infos = new ArrayList<>();
        if (fileInfos!=null){

            for (int i=0;i<fileNames.size();i++){
                String name = fileNames.get(i);
                for (int j=0;j<fileInfos.size();j++){
                    FileUtils.FileInfo fileInfo = fileInfos.get(j);
                    if (fileInfo.FileName.equals(name)){
                        ProjectInfo projectInfo = new ProjectInfo();
                        projectInfo.DirName = fileInfo.FileName;
                        projectInfo.DirPath = fileInfo.FilePath;
                        infos.add(projectInfo);
                    }
                }
            }
        }
        return infos;
    }
}
