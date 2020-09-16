package com.nibiru.studio.vrscene;

import android.graphics.Color;
import android.util.Log;

import com.nibiru.service.NibiruSelectionTask;
import com.nibiru.service.NibiruTask;

import java.io.File;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XLabel;
import x.core.ui.XToast;
import x.core.util.XLog;

/**
 * 演示系统基本功能接口的使用，包括：选取文件，播放视频，浏览图片，打开文件管理器，打开wifi设置，浏览网站，打开外设管理
 */

/**
 * Show system basic function APIs, including selecting file, video play, image browser, opening file manager, opening wifi, browsing website and opening peripheral management
 */


public class SubSceneOSAPI extends XBaseScene implements IXActorEventListener {

    float mStartY = 1.6f;
    float mStep = -0.5f;

    //初始化Label的内容
    //Initialize Label contents
    String[] labelList = new String[]{
            "Select Video File",
            "Play Selected Video",
            "Select Image File",
            "View Selected Image",
            "Start File Manager",
            "Open Wifi Settings",
            "Browse Website",
            "Open Device Driver",
            "Get FPS"

    };

    //添加Label的公共方法，index决定Label所在的位置
    //Add public method for Label, index decides the position of Label
    public XLabel addLabel(String value, int index){
        XLabel infoLabel1 = new XLabel(value);
        infoLabel1.setName(value);
        infoLabel1.setAlignment(XLabel.XAlign.Center);
        infoLabel1.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        infoLabel1.setCenterPosition(0, mStartY + index * mStep , -4f);
        infoLabel1.setTextColor(Color.WHITE);
        infoLabel1.setEnableGazeAnimation(true);
        infoLabel1.setEventListener(this);
        infoLabel1.setSize(0.8f , 0.3f);
        addActor(infoLabel1);

        return infoLabel1;
    }

    XLabel mPlayVideoLabel;
    XLabel mViewImageLabel;

    @Override
    public void onCreate() {
        setEnableFPS(true);
        for( int i = 0; i < labelList.length; i++ ){
            XLabel label = addLabel(labelList[i], i);

            //在未选择视频和图片文件前，先隐藏播放和浏览按钮
            //Hide play and browse buttons before selecting videos and images
            if( "Play Selected Video".equals( labelList[i]) ){
                mPlayVideoLabel = label;
                label.setEnabled(false);
            }else if( "View Selected Image".equals( labelList[i]) ){
                mViewImageLabel = label;
                label.setEnabled(false);
            }
        }


    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onGazeEnter(XActor actor) {

    }

    @Override
    public void onGazeExit(XActor actor) {

    }

    @Override
    public boolean onGazeTrigger(XActor actor) {
        String name = actor.getName();

        //演示选择视频文件
        //Select video file
        if( labelList[0].equals(name) ){
            //select video file
            selectFile(NibiruSelectionTask.FILE_TYPE_VIDEO);

        }
        //演示播放选择的视频文件
        //Play video file
        else if( labelList[1].equals(name) ){
            //如果视频文件不存在弹出Toast提示
            //If the video file does not exist, toast will show
            if( selectedVideoPath == null || !new File(selectedVideoPath).exists() || !isVideo(selectedVideoPath.toLowerCase())){
                XToast toast = XToast.makeToast(this, "Select a valid video file first", 2000);
                toast.setGravity(0.0f, 0f, -3.0f);
                toast.show(true);
                return true;
            }else{
                playVideo(selectedVideoPath);
            }
        }
        //选择图片文件
        //Select image file
        else if( labelList[2].equals(name) ){
            //select image file
            selectFile(NibiruSelectionTask.FILE_TYPE_IMAGE);
        }
        //浏览选择的图片文件
        //Browse selected image files
        else if( labelList[3].equals(name) ){
            //view image file
            if( selectedImagePath == null || !new File(selectedImagePath).exists() || !isImage(selectedImagePath.toLowerCase())){
                XToast toast = XToast.makeToast(this, "Select a valid image file first", 2000);
                toast.setGravity(0.0f, -1f, -3.0f);
                toast.show(true);
                return true;
            }else{
                viewImage(selectedImagePath);
            }
        }
        //打开文件管理器
        //Open file manager
        else if( labelList[4].equals(name) ){
            //view file path
            //查看SD卡根目录
            openFile("/sdcard");
        }
        //打开wifi设置界面
        //Open wifi setting
        else if( labelList[5].equals(name) ){
            openSettings(NibiruTask.SETTINGS_TYPE_WIFI);
        }
        //打开网页
        //Open webstie
        else if (labelList[6].equals(name) ){
            String url = "http://www.inibiru.com";

            openExplorer(url);
        }
        //打开外设管理
        //Open peropheral manager
        else if( labelList[7].equals(name) ){
            openDeviceDriver();
        } else if( labelList[8].equals(name) ) {
            float[] fps = getFPS();
            if(fps != null && fps.length > 1) {
                ((XLabel) actor).setTextContent("Get FPS: APP:" + fps[0] + ",DTR:" + fps[1]);
            }
        }

        return false;
    }

    @Override
    public void onAnimation(XActor actor, boolean isSelected) {

    }


    /*
    参数说明：
    参数命名规则
        KEY-VALUE命名规则：
        KEY: 功能_KEY_子功能
        VALUE: 功能_子功能_VALUE定义

    具体功能
    KEY ： NibiruTask.VIDEO_KEY_CONTROL
    VALUE：
    NibiruTask.VIDEO_CONTROL_START 打开视频
    NibiruTask.VIDEO_CONTROL_PAUSE 暂停视频播放
    NibiruTask.VIDEO_CONTROL_RESUME 继续视频播放
    NibiruTask.VIDEO_CONTROL_CLOSE 退出视频播放
    NibiruTask.VIDEO_CONTROL_SEEKTO 快进快退
    NibiruTask.VIDEO_CONTROL_HIDE_CONTROLLER 是否隐藏播控面板

    打开视频播放器的其他参数
            是否开始循环播放
            KEY ：
            NibiruTask.VIDEO_KEY_LOOP
            VALUE：
            NibiruTask.VIDEO_KEY_LOOP_ON 开
            NibiruTask.VIDEO_KEY_LOOP_OFF关

            解码方式
            KEY：
            NibiruTask.VIDEO_KEY_PARAMETERS_DECODE
            VALUE：
            NibiruTask.VIDEO_PARAMETERS_DECODE_HARDWARE 硬解码
            NibiruTask.VIDEO_PARAMETERS_DECODE_SOFTWARE 软解码


            播放类型
            KEY：
            NibiruTask.VIDEO_KEY_PARAMETERS_TYPE
            VALUE：
            NibiruTask.VIDEO_PARAMETERS_TYPE_2D 2d视频格式
            NibiruTask.VIDEO_PARAMETERS_TYPE_3D 3d视频格式

            播放模式
            KEY：
            NibiruTask.VIDEO_KEY_PARAMETERS_MODE
            VALUE：
            NibiruTask.VIDEO_PARAMETERS_MODE_NORMAL 普通平面模式
            NibiruTask.VIDEO_PARAMETERS_MODE_360 360度全屏视频
            NibiruTask.VIDEO_PARAMETERS_MODE_180 180度视频
            NibiruTask.VIDEO_PARAMETERS_MODE_FULLDOME 球幕视频格式

            播放路径
            KEY：
            NibiruTask.VIDEO_KEY_PATH
            VALUE：
            视频的本地路径地址或者网络地址String值

    快进快退其他参数
            快进快退时长
            KEY：
            NibiruTask.VIDEO_KEY_PATH
            VALUE：
            快进快退时长long型

    是否隐藏播放器播控面板其他参数
            是否隐藏
            KEY：
            NibiruTask.VIDEO_KEY_PARAMETER_CONTROL_PANEL
            VALUE：
            NibiruTask.VIDEO_PARAMETER_CONTROL_PANEL_HIDE 隐藏
            NibiruTask.VIDEO_PARAMETER_CONTROL_PANEL_SHOW 显示

    暂停视频播放，继续视频播放，退出视频播放不需要其他参数

    * */

    /*
    Parameter descriptions:
    Parameter naming rule
        KEY-VALUE naming rule:
        KEY: Function_KEY_Subfunction
        VALUE: Function_Subfunction_VALUE definition

    Detailed function
    KEY: NibiruTask.VIDEO_KEY_CONTROL
    VALUE：
    NibiruTask.VIDEO_CONTROL_START Open video
    NibiruTask.VIDEO_CONTROL_PAUSE Pause playing video
    NibiruTask.VIDEO_CONTROL_RESUME Resume playing
    NibiruTask.VIDEO_CONTROL_CLOSE Stop video playing
    NibiruTask.VIDEO_CONTROL_SEEKTO Fast forward and fast backward
    NibiruTask.VIDEO_CONTROL_HIDE_CONTROLLER Whether to hide play and control panel

    Open othe parameters of video player
            Whether to enable loop
            KEY:
            NibiruTask.VIDEO_KEY_LOOP
            VALUE:
            NibiruTask.VIDEO_KEY_LOOP_ON  On
            NibiruTask.VIDEO_KEY_LOOP_OFF Off

            Decoding method
            KEY：
            NibiruTask.VIDEO_KEY_PARAMETERS_DECODE
            VALUE：
            NibiruTask.VIDEO_PARAMETERS_DECODE_HARDWARE Hardware decode
            NibiruTask.VIDEO_PARAMETERS_DECODE_SOFTWARE Software decode


            Playback type
            KEY：
            NibiruTask.VIDEO_KEY_PARAMETERS_TYPE
            VALUE：
            NibiruTask.VIDEO_PARAMETERS_TYPE_2D 2d video format
            NibiruTask.VIDEO_PARAMETERS_TYPE_3D 3d video format

            Playback mode
            KEY：
            NibiruTask.VIDEO_KEY_PARAMETERS_MODE
            VALUE：
            NibiruTask.VIDEO_PARAMETERS_MODE_NORMAL Normal plane mode
            NibiruTask.VIDEO_PARAMETERS_MODE_360 360 degree panorama video
            NibiruTask.VIDEO_PARAMETERS_MODE_180 180 degree video
            NibiruTask.VIDEO_PARAMETERS_MODE_FULLDOME Fulldome video format

            Playback path
            KEY：
            NibiruTask.VIDEO_KEY_PATH
            VALUE：
            Local path or website address String value of video

    Other parameters for fast forward and backward
            Duration of fast forward and backward
            KEY：
            NibiruTask.VIDEO_KEY_PATH
            VALUE：
            long type of fast forward and backward duration

    Other parameters for whether to hide play and control panel
            Whether to hide
            KEY：
            NibiruTask.VIDEO_KEY_PARAMETER_CONTROL_PANEL
            VALUE：
            NibiruTask.VIDEO_PARAMETER_CONTROL_PANEL_HIDE Hide
            NibiruTask.VIDEO_PARAMETER_CONTROL_PANEL_SHOW Show

    Pause video play, resume the play and stop the play do not need other parameters
    * */
    private void playVideo(String path) {
        NibiruTask nibiruTask = new NibiruTask(NibiruTask.TASK_ACTION_VIDEO_PLAY);

        //播放视频
        //Play video
        nibiruTask.addParameter(NibiruTask.VIDEO_KEY_CONTROL, NibiruTask.VIDEO_CONTROL_START);

        //开启循环播放
        //Enable loop
        nibiruTask.addParameter(NibiruTask.VIDEO_KEY_LOOP, NibiruTask.VIDEO_KEY_LOOP_ON);

        //使用硬解码
        //Use hardware decode
        nibiruTask.addParameter(NibiruTask.VIDEO_KEY_PARAMETERS_DECODE, NibiruTask.VIDEO_PARAMETERS_DECODE_HARDWARE);

        //使用360全景模式播放
        //Use 360 panorama mode to play
        nibiruTask.addParameter(NibiruTask.VIDEO_KEY_PARAMETERS_MODE, NibiruTask.VIDEO_PARAMETERS_MODE_360);

        //视频为2D 360全景视频（单球）
        //Video is 2D 360 pano video (mono)
        nibiruTask.addParameter(NibiruTask.VIDEO_KEY_PARAMETERS_TYPE, NibiruTask.VIDEO_PARAMETERS_TYPE_2D);

        //设置视频文件路径，支持URL设置
        //Set video file path, support URL setting
        nibiruTask.addParameter(NibiruTask.VIDEO_KEY_PATH, path);

        //两种打开操作，如果是在Scene或GlobalApplication可以直接startNibiruTask
        //Two opening operations, if it's in Scene or GlobalApplication, call startNibiruTask directly
        // 如果在服务类或者其他类中可以使用NibiruStudioUtils.getInstance(getActivity()).startNibiruTask
        // If it's in service class or other classes, call NibiruStudioUtils.getInstance(getActivity()).startNibiruTask
        //NibiruStudioUtils.getInstance(getActivity()).startNibiruTask(nibiruTask);
        startNibiruTask(nibiruTask);
    }

    /*
    打开文件管理器参数：
        打开的文件或者文件夹的地址
        KEY：NibiruTask.OPEN_FILE_KEY_PATH
        VALUE：文件的路径地址String类型
    *   */
    /*
    Parameters for opening file manager:
        File or folder address
        KEY：NibiruTask.OPEN_FILE_KEY_PATH
        VALUE: file path, String type
    *   */
    private void openFile(String path) {
        NibiruTask nibiruTask = new NibiruTask(NibiruTask.TASK_ACTION_OPEN_FILE);

        //默认打开的文件夹路径
        //Default folder path
        nibiruTask.addParameter(NibiruTask.OPEN_FILE_KEY_PATH, path);

        //打开文件模式/选取文件路径模式，参数true为打开模式，false为选取模式
        //Open File mode or Select File mode, true means open file, false means select file
        nibiruTask.addParameter(NibiruTask.FILE_KEY_OPEN_FILE, Boolean.toString(false));

        startNibiruTask(nibiruTask);
    }

    /*
    打开图库参数：
        打开图片的地址
        KEY：NibiruTask.SHOW_IMAGE_KEY_PATH
        VALUE：图片的路径地址String类型

        打开图片的格式
        KEY：NibiruTask.SHOW_IMAGE_KEY_TYPE
        VALUE：
        NibiruTask.SHOW_IMAGE_2D 普通图片
        NibiruTask.SHOW_IMAGE_3D 3d图片
        NibiruTask.SHOW_IMAGE_360 360全景图片
    * */
    /*
    Parameters for opening gallery:
        Image address
        KEY：NibiruTask.SHOW_IMAGE_KEY_PATH
        VALUE: image path, String type

        Image format
        KEY：NibiruTask.SHOW_IMAGE_KEY_TYPE
        VALUE：
        NibiruTask.SHOW_IMAGE_2D Normal image
        NibiruTask.SHOW_IMAGE_3D 3D image
        NibiruTask.SHOW_IMAGE_360 360 pano image
    * */
    private void viewImage(String path) {
        //设置类型
        //Set type
        NibiruTask nibiruTask = new NibiruTask(NibiruTask.TASK_ACTION_SHOW_IMAGE);

        //设置图片文件路径
        //Set image file path
        nibiruTask.addParameter(NibiruTask.SHOW_IMAGE_KEY_PATH, path);

        //设置图片按2D方式查看（不进行左右拆分）
        //Browse in 2D mode (without left and right split)
        nibiruTask.addParameter(NibiruTask.SHOW_IMAGE_KEY_TYPE, NibiruTask.SHOW_IMAGE_2D);

        startNibiruTask(nibiruTask);
    }

    /*
        打开设置参数：
            具体打开设置内容
                KEY：NibiruTask.SETTINGS_KEY_TYPE
                VALUE：
                        NibiruTask.SETTINGS_TYPE_MAIN  设置主界面
                        NibiruTask.SETTINGS_TYPE_WIFI   设置WIFI界面
                        NibiruTask.SETTINGS_TYPE_BLUETOOTH  设置蓝牙界面
                        NibiruTask.SETTINGS_TYPE_SYSTEM     设置系统界面
                        NibiruTask.SETTINGS_TYPE_GENERAL    设置通用界面
    * */
    /*
        Parameters for opening settings:
            Detailed contents of opening settings
                KEY：NibiruTask.SETTINGS_KEY_TYPE
                VALUE：
                        NibiruTask.SETTINGS_TYPE_MAIN  Main interface
                        NibiruTask.SETTINGS_TYPE_WIFI   WIFI nterface
                        NibiruTask.SETTINGS_TYPE_BLUETOOTH  Bluetooth interface
                        NibiruTask.SETTINGS_TYPE_SYSTEM     System interface
                        NibiruTask.SETTINGS_TYPE_GENERAL    General interface
    * */
    private void openSettings(String ui) {
        NibiruTask nibiruTask = new NibiruTask(NibiruTask.TASK_ACTION_SETTINGS);
        nibiruTask.addParameter(NibiruTask.SETTINGS_KEY_TYPE, ui);
        startNibiruTask(nibiruTask);
    }

    /*
        打开浏览器参数：
            打开浏览器的地址
            KEY：NibiruTask.EXPLORER_KEY_URL
            VALUE：网址String类型

            是否显示地址栏
            KEY：NibiruTask.EXPLORER_KEY_ACTIONBAR
            VALUE：
            NibiruTask.EXPLORER_ACTIONBAR_SHOW 显示地址栏
            NibiruTask.EXPLORER_ACTIONBAR_HIDE隐藏地址栏
    * */
     /*
        Parameters for opening browser:
            Browser address
            KEY: NibiruTask.EXPLORER_KEY_URL
            VALUE: URL, String type

            Whether to display address bar
            KEY：NibiruTask.EXPLORER_KEY_ACTIONBAR
            VALUE:
            NibiruTask.EXPLORER_ACTIONBAR_SHOW  Show
            NibiruTask.EXPLORER_ACTIONBAR_HIDE Hide
    * */
    private void openExplorer(String url) {
        NibiruTask nibiruTask = new NibiruTask(NibiruTask.TASK_ACTION_EXPLORER);

        //是否显示地址栏
        //Whether to show address bar
        nibiruTask.addParameter(NibiruTask.EXPLORER_KEY_ACTIONBAR, NibiruTask.EXPLORER_ACTIONBAR_SHOW);

        //设置跳转的URL
        //Set URL to jump
        nibiruTask.addParameter(NibiruTask.EXPLORER_KEY_URL, url);

        startNibiruTask(nibiruTask);
    }

    /*打开外设驱动，不需要额外参数*/
    /*Open peripheral drive, additional parameters are not required*/
    private void openDeviceDriver() {
        NibiruTask nibiruTask = new NibiruTask(NibiruTask.TASK_ACTION_DEVICE_DRIVER);
        startNibiruTask(nibiruTask);
    }

    String selectedVideoPath = null;
    String selectedImagePath = null;

    //选择文件的回调
    //Callback for selecting file
    class FileSelectionRunnable implements NibiruSelectionTask.NibiruSelectionCallback{

        int type;

        FileSelectionRunnable(int type){
            this.type = type;
        }
        @Override
        public void onSelectionResult(NibiruSelectionTask nibiruSelectionTask) {

            //获取返回码
            //Get result code
            NibiruSelectionTask.SELECTION_RESULT resultCode = nibiruSelectionTask.getResultCode();

            //获取用户选择的文件路径
            //Get path of file selected by user
            final String filepath = nibiruSelectionTask.getResultValue().get(NibiruSelectionTask.FILE_KEY_SELECTION_RESULT);

            Log.d(XLog.TAG, "File Select Result: "+resultCode+" path: "+filepath);

            //如果返回码为OK，表示选择成功，保存路径
            //If the result code is OK, the selection is successful, save the path
            if( resultCode == NibiruSelectionTask.SELECTION_RESULT.OK ){
                if( type == NibiruSelectionTask.FILE_TYPE_IMAGE ){
                    selectedImagePath = filepath;

                    runOnRenderThread(new Runnable() {
                        @Override
                        public void run() {
                            XActor actor = findActor(labelList[2]);
                            if( actor != null && actor instanceof XLabel){
                                ((XLabel)actor).setTextContent("Get Image Path: "+filepath);
                            }

                            //显示浏览图片的按钮
                            //Show image browsing button
                            if( mViewImageLabel != null ){
                                mViewImageLabel.setEnabled(true);
                            }
                        }
                    });
                }else if( type == NibiruSelectionTask.FILE_TYPE_VIDEO ){
                    selectedVideoPath = filepath;

                    runOnRenderThread(new Runnable() {
                        @Override
                        public void run() {
                            XActor actor = findActor(labelList[0]);
                            if( actor != null && actor instanceof XLabel){
                                ((XLabel)actor).setTextContent("Get Video Path: "+filepath);
                            }

                            //显示播放视频的按钮
                            //Show video playing button
                            if( mPlayVideoLabel != null ){
                                mPlayVideoLabel.setEnabled(true);
                            }
                        }
                    });
                }

            }else{
                if( type == NibiruSelectionTask.FILE_TYPE_IMAGE ){
                    selectedImagePath = null;

                    runOnRenderThread(new Runnable() {
                        @Override
                        public void run() {
                            XActor actor = findActor(labelList[2]);
                            if( actor != null && actor instanceof XLabel){
                                ((XLabel)actor).setTextContent("Get Image Path: null");
                            }

                            //隐藏浏览图片的按钮
                            //Hide image browsing button
                            if( mViewImageLabel != null ){
                                mViewImageLabel.setEnabled(false);
                            }
                        }
                    });
                }else if( type == NibiruSelectionTask.FILE_TYPE_VIDEO ){
                    selectedVideoPath = null;

                    runOnRenderThread(new Runnable() {
                        @Override
                        public void run() {
                            XActor actor = findActor(labelList[0]);
                            if( actor != null && actor instanceof XLabel){
                                ((XLabel)actor).setTextContent("Get Video Path: null");
                            }

                            //隐藏播放视频的按钮
                            //Hide video playing button
                            if( mPlayVideoLabel != null ){
                                mPlayVideoLabel.setEnabled(false);
                            }
                        }
                    });
                }
            }
        }
    }
    /*
        选取文件：
            打开的文件或者文件夹起始路径地址
            KEY：NibiruTask.OPEN_FILE_KEY_PATH
            VALUE：文件的起始路径地址String类型

            获取文件地址的文件类型（可选）
            KEY：NibiruSelectionTask.FILE_KEY_TYPE
            VALUE：NibiruSelectionTask.FILE_TYPE_VIDEO 视频
            NibiruSelectionTask.FILE_TYPE_IMAGE 图片
            NibiruSelectionTask.FILE_TYPE_APK 安装包

        通过setCallback回调返回的结果
            getResultCode返回结果Code值
            枚举类型 NibiruSelectionTask.SELECTION_RESULT 具体值UNKNOWN, OK, CANCEL, ERROR;

            getResultValue具体返回值，以KEY-VALUE形式保存  文件路径返回KEY：NibiruSelectionTask.FILE_KEY_SELECTION_RESULT
    * */
    /*
        Select file:
            Start path of opened file or folder
            KEY: NibiruTask.OPEN_FILE_KEY_PATH
            VALUE: start path of file, String type

            File type of the obtained file path (optional)
            KEY: NibiruSelectionTask.FILE_KEY_TYPE
            VALUE: NibiruSelectionTask.FILE_TYPE_VIDEO Video
            NibiruSelectionTask.FILE_TYPE_IMAGE Image
            NibiruSelectionTask.FILE_TYPE_APK Installation package

        Results from setCallback callback
            getResultCode returns Code value
            Enumeration type NibiruSelectionTask.SELECTION_RESULT Specific value UNKNOWN, OK, CANCEL, ERROR;

            The detailed return values of getResultValue are saved in KEY-VALUE format  File path return KEY: NibiruSelectionTask.FILE_KEY_SELECTION_RESULT
    * */
    private void selectFile(int type) {
        NibiruSelectionTask nibiruSelectionTask = new NibiruSelectionTask(NibiruSelectionTask.TASK_SELECTION_ACTION_FILE);

        //选择文件的类型
        //Selected file type
        nibiruSelectionTask.addParameter(NibiruSelectionTask.FILE_KEY_TYPE, type);

        //起始路径
        //Starting path
        nibiruSelectionTask.addParameter(NibiruSelectionTask.FILE_KEY_PATH, "/sdcard");

        //设置选择文件完成回调
        //Set file selecting to complete the callback
        nibiruSelectionTask.setCallback(new FileSelectionRunnable(type));

        startNibiruTask(nibiruSelectionTask);
    }

    private boolean isVideo(String path) {
        return (path.endsWith(".mp4") || path.endsWith(".mkv") || path.endsWith(".3gp") || path.endsWith(".wmv") || path.endsWith(".avi"));
    }

    private boolean isImage(String path) {
        return (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".bmp") || path.endsWith(".gif"));
    }
}
