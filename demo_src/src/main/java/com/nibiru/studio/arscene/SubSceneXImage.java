package com.nibiru.studio.arscene;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;

import com.nibiru.studio.CalculateUtils;
import com.nibiru.studio.xrdemo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import x.core.listener.IXActorEventListener;
import x.core.ui.XActor;
import x.core.ui.XImage;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;
import x.core.ui.XUI;
import x.core.util.XLog;

/**
 * 演示图片控件，图片控件支持多种图片来源：assets路径，sdcard路径，Android resource ID, Android Drawable，Bitmap
 * 支持JPG和PNG格式图片
 * 网络路径图片可先下载到存储设备，通过sdcard路径加载，或者直接通过Bitmap加载
 */

/**
 * Show image control, which supports multiple sources: assets path, sdcard path, Android resource ID, Android Drawable and Bitmap
 * Support JPG and PNG formats
 * Images from network path can be downloaded to the device first and then be loaded through sdcard path or loaded directly through Bitmap
 */
public class SubSceneXImage extends BaseScene {

    private XImage xImage1;
    private XImage xImage2;
    private XImage xImage3;
    private XImage xImage4;
    private XImage xImage5;
    private XImage xImage6;

    private String imageUrl = "http://img.cloud.1919game.net/200811260457354740.png";

    @Override
    public void onCreate() {
        super.onCreate();
        init();
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

    //拷贝Assets图片到SD卡中，这里用于SD卡图片路径不存在时拷贝Assets路径下的默认图片
    //Copy the default image in Assets to SD Card, when the SD Card path doesn't exist
    private void copyAssetsToSdcard(String from, String to){

        try {
            int bytesum = 0;
            int byteread = 0;

            InputStream inStream = getResources().getAssets().open(from);
            OutputStream fs = new BufferedOutputStream(new FileOutputStream(to));
            byte[] buffer = new byte[1024];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();

        } catch (Exception e) {
        }

    }

    public void init() {

        XLabel titleLabel = new XLabel("Example：Image");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 0.2f, CalculateUtils.CENTER_Z);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.08f, 0.03f);
        addActor(titleLabel);

        XLabel mTip = new XLabel("Images from Assets, SDCard, Resource, Network, Drawable");
        mTip.setAlignment(XAlign.Center);
        mTip.setArrangementMode(XArrangementMode.SingleRowNotMove);
        mTip.setCenterPosition(0, 0.15f, CalculateUtils.CENTER_Z);
        mTip.setSize(0.38f, 0.02f);
        addActor(mTip);

        //初始化来自assets路径下的图片，支持PNG/JPG格式
        //Initialize images from assets, supporting PNG/JPG formats
        //Assets Image, support assets png file and plist image name
//        xImage1 = new XImage("ic_image_focused.png");

        //Assets Image，support jpg file
        xImage1 = new XImage("jpg_test.jpg");

        xImage1.setCenterPosition(-0.3f, 0.08f, CalculateUtils.CENTER_Z);
        xImage1.setSize(0.1f, 0.1f);
        xImage1.setRenderOrder(6);
        xImage1.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {

            }

            @Override
            public void onGazeExit(XActor actor) {

            }

            @Override
            public boolean onGazeTrigger(XActor actor) {

                //更新为Bitmap
                //Update to Bitmap
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                //setImageBitmap只更新当前图片，如果初始化指定了选中和非选中图片，更新需要调用setImageBitmap(Bitmap selectedBitmap, Bitmap unselectedBitmap)
                //setImageBitmap only updates the current image, if the initialization has specified selected and unselected images, call setImageBitmap(Bitmap selectedBitmap, Bitmap unselectedBitmap) to update
                xImage1.setImageBitmap(bitmap);
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });
        xImage1.setEnableGazeAnimation(true);
        //设置控件朝向眼睛
        //Set control towards the eyes
        xImage1.towardOrigin();
        addActor(xImage1);

        //演示SD卡路径下的图片加载，当SD卡没有图片存在时，从Assets拷贝一张默认图片到SD路径用于显示
        //Demonstrate loading image under SD Card. When there's no image in SD Card, copy a default image in Assets to SD Card for display
        //Sdcard Image, Please put image file named icon.png to sdcard path, or we will copy an image from assets.
        String filePath = Environment.getExternalStorageDirectory() + "/" + "icon.png";
        File file = new File(filePath);

        if( !file.exists() ){
            //当SD卡没有图片存在时，从Assets拷贝一张默认图片到SD路径用于显示
            //When there's no image in SD Card, copy a default image in Assets to SD Card for display
            copyAssetsToSdcard("yejingtu.png", filePath);
        }

        xImage2 = new XImage(file.getAbsolutePath());
        xImage2.setLocation(XUI.Location.SDCARD);
        xImage2.setCenterPosition(-0.15f, 0.08f, CalculateUtils.CENTER_Z);
        xImage2.setSize(0.1f, 0.1f);
        xImage2.setRenderOrder(6);
        xImage2.setEnableGazeAnimation(true);
        addActor(xImage2);

        //演示Android Resource图片加载
        //Demonstrate loading Android Resource image
        //Resource Image
        xImage3 = new XImage(R.mipmap.icon);
        xImage3.setName("Image_Resource_ID");
        xImage3.setCenterPosition(0f, 0.08f, CalculateUtils.CENTER_Z);
        xImage3.setSize(0.1f, 0.1f);
        xImage3.setRenderOrder(6);
        xImage3.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor xActor) {

            }

            @Override
            public void onGazeExit(XActor xActor) {

            }

            @Override
            public boolean onGazeTrigger(XActor xActor) {
                //点击更新图片,setImageName只是更新当前图片，如果初始化指定了选中和非选中图片，更新需要调用setImageName(String imgSelectedName, String imgUnselectedName)
                //Click to update image, setImageName is only for updating the current image. If the initialization has specified the selected and unselected image, setImageName(String imgSelectedName, String imgUnselectedName) should be called to update
                xImage3.setImageName("jpg_test.jpg");
                return true;
            }

            @Override
            public void onAnimation(XActor xActor, boolean b) {

            }
        });
        xImage3.setEnableGazeAnimation(true);
        addActor(xImage3);

        //Network URL Image，构造中设置的为网络加载时的默认图片
        //Network URL Image, the setting image is the default image when network loads
        xImage4 = new XImage("yejingtu.png");
        xImage4.setName("Image_Network");
        xImage4.setCenterPosition(0.15f, 0.08f, CalculateUtils.CENTER_Z);
        xImage4.setSize(0.1f, 0.1f);
        xImage4.setRenderOrder(6);
        xImage4.setEnableGazeAnimation(true);
        //设置控件朝向眼睛
        //Set the control towards the eyes
        xImage4.towardOrigin();
        addActor(xImage4);

        new Thread(new Runnable() {
            @Override
            public void run() {

                ImageLoader.getInstance().loadImage(imageUrl, new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        //如果加载失败，显示失败图片
                        //If it fails to load, display the failure image
                        XLog.logErrorInfo(this.getClass().toString() + failReason);
                        loadFailedImage();
                    }
                    @Override
                    public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {

                        //如果加载成功，直接使用Bitmap设置，更新图片需要在Render线程中
                        //If loaded successfully, use Bitmap setting directly, updating image should be in the Render thread
                        if (imageUri.equals(imageUrl) && loadedImage != null && isRunning() ) {

                            //setImageBitmap只是更新当前显示图片，如果初始化指定了选中和非选中图片，更新需要调用setImageBitmap(Bitmap selectedBitmap, Bitmap unselectedBitmap)
                            //setImageBitmap only updates the current displayed image, if the initialization has specified selected and unselectd image, call setImageBitmap(Bitmap selectedBitmap, Bitmap unselectedBitmap)
                            runOnRenderThread(new Runnable() {
                                @Override
                                public void run() {
                                    xImage4.setImageBitmap(loadedImage);
                                }
                            });
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        XLog.logErrorInfo("cancel Load Image");
                        loadFailedImage();
                    }
                });
            }
        }).start();

        Drawable drawable = getResources().getDrawable(R.drawable.ximage_drawable);
        xImage6 = new XImage(drawable);
        xImage6.setName("Image_Drawable");
        xImage6.setCenterPosition(0.3f, 0.08f, CalculateUtils.CENTER_Z);
        xImage6.setSize(0.1f, 0.1f);
        xImage6.setRenderOrder(6);
        xImage6.setEnableGazeAnimation(true);
        xImage6.towardOrigin();
        addActor(xImage6);

        XLabel mClipTip = new XLabel("Images Clip 50% From Top");
        mClipTip.setAlignment(XAlign.Center);
        mClipTip.setArrangementMode(XArrangementMode.SingleRowNotMove);
        mClipTip.setCenterPosition(0, -0.04f, CalculateUtils.CENTER_Z);
        mClipTip.setSize(0.38f, 0.02f);
        addActor(mClipTip);

        xImage5 = new XImage("yejingtu.png");
        xImage5.setName("Image_Clip");
        xImage5.setCenterPosition(0f, -0.1f, CalculateUtils.CENTER_Z);
        xImage5.setSize(0.1f, 0.12f);
        xImage5.setRenderOrder(6);
        xImage5.setEnableGazeAnimation(true);
        addActor(xImage5);
        xImage5.setProgress(0.5f, XImage.ClipDirection.RectClipStrategy_Top);

    }


    private void loadFailedImage(){
        if( !isRunning() )return;
        runOnRenderThread(new Runnable() {
            @Override
            public void run() {
                if( xImage4 != null ){
                    xImage4.setImageName("ic_image_focused.png");
                }
            }
        });
    }

}