# NodePublisher API
NodePublisher 用于RTMP直播推流, 支持H.264+AAC/SPEEX编码, 16:9 / 4:3 / 1:1画面, 内置GPU加速的美颜滤镜.

Table of Contents
=================

   * [NodePublisher API](#nodepublisher-api)
      * [属性](#属性)
         * [setOutputUrl()](#setoutputurl)
         * [setPageUrl()](#setpageurl)
         * [setSwfUrl()](#setswfurl)
         * [setConnArgs()](#setconnargs)
         * [setCameraPreview(NodeCameraView cameraPreview, int cameraID, boolean frontMirror)](#setcamerapreviewnodecameraview-camerapreview-int-cameraid-boolean-frontmirror)
         * [setZoomScale(int zoomScale)](#setzoomscaleint-zoomscale)
         * [setAutoFocus](#setautofocus)
         * [setFlashEnable()](#setflashenable)
         * [setAudioParam(int bitrate, int profile)](#setaudioparamint-bitrate-int-profile)
         * [setVideoParam(int preset, int fps, int bitrate, int profile, boolean frontMirror)](#setvideoparamint-preset-int-fps-int-bitrate-int-profile-boolean-frontmirror)
         * [setBeautyLevel(int beautyLevel)](#setbeautylevelint-beautylevel)
         * [setAudioEnable(boolean audioEnable)](#setaudioenableboolean-audioenable)
         * [setVideoEnable(boolean videoEnable)](#setvideoenableboolean-videoenable)
         * [setDenoiseEnable(boolean denoiseEnable)](#setdenoiseenableboolean-denoiseenable)
         * [setAecEnable(boolean aecEnable)](#setaecenableboolean-aecenable)
         * [setKeyFrameInterval(int keyFrameInterval)](#setkeyframeintervalint-keyframeinterval)
         * [setPublishType(int publishType)](#setpublishtypeint-publishtype)
         * [setNodePublisherDelegate(NodePublisherDelegate delegate)](#setnodepublisherdelegatenodepublisherdelegate-delegate)
      * [方法](#方法)
         * [switchCamera()](#switchcamera)
         * [startPreview()](#startpreview)
         * [stopPreview()](#stoppreview)
         * [capturePicture(CapturePictureListener listener)](#capturepicturecapturepicturelistener-listener)
         * [start()](#start)
         * [stop()](#stop)
         * [release()](#release)
      * [事件回调](#事件回调)
         * [2000](#2000)
         * [2001](#2001)
         * [2002](#2002)
         * [2004](#2004)
         * [2005](#2005)
         * [2100](#2100)
         * [2101](#2101)
      * [VIDEO_PPRESET](#video_ppreset)


## 属性
### setOutputUrl()
设置视频输出地址, 支持RTMP/RTMPT/HTTP形式,FLV封装

### setPageUrl()
设置RTMP协议下pageUrl地址.

### setSwfUrl()
设置RTMP协议集下swfUrl地址

### setConnArgs()
设置RTMP协议集下connect命令发出时附带的参数. RTMPDUMP风格
>Append arbitrary AMF data to the Connect message. The type must be B for Boolean, N for number, S for string, O for object, or Z for null. For Booleans the data must be either 0 or 1 for FALSE or TRUE, respectively. Likewise for Objects the data must be 0 or 1 to end or begin an object, respectively. Data items in subobjects may be named, by prefixing the type with 'N' and specifying the name before the value, e.g. NB:myFlag:1. This option may be used multiple times to construct arbitrary AMF sequences. E.g.
**S:info O:1 NS:uid:10012 NB:vip:1 NN:num:209.12 O:0**

### setCameraPreview(NodeCameraView cameraPreview, int cameraID, boolean frontMirror)
设置摄像头预览视图
- 参数1 : 需要传入cn.nodemedia.NodeCameraView对象
- 参数2 : 摄像头ID,使用NodePublisher.CAMERA_BACK或NodePublisher.CAMERA_FRONT
- 参数3 : 预览时画面是否镜像显示

### setZoomScale(int zoomScale)
设置摄像头缩放等级, 0-100
>GPU算法, 非系统API, 所有机型上缩放效果比例一致

### setAutoFocus(boolean autoFocus)
当为true时,摄像头将全时自动对焦. 当为false时,每请求一次,对焦一次,之后锁定焦距

### setFlashEnable()
设置是否一直开启闪光灯

### setAudioParam(int bitrate, int profile)
设置音频编码参数
- 参数1 : 音频比特率
- 参数2 : 音频编码格式,支持
	- NodePublisher.AUDIO_PROFILE_LCAAC
	- NodePublisher.AUDIO_PROFILE_HEAAC
	- NodePublisher.AUDIO_PROFILE_SPEEX

### setVideoParam(int preset, int fps, int bitrate, int profile, boolean frontMirror)
设置视频编码参数
- 参数1 : [视频分辨率预设](VIDEO_PPRESET)
- 参数2 : 视频帧率
- 参数3 : 视频比特率
- 参数4 : 视频编码规格
	- NodePublisher.VIDEO_PROFILE_BASELINE
	- NodePublisher.VIDEO_PROFILE_MAIN
	- NodePublisher.VIDEO_PROFILE_HIGH
- 参数5 : 视频输出画面是否进行镜像翻转

### setBeautyLevel(int beautyLevel)
设置美颜等级,0-5 , 0关闭, 1-5级别

### setAudioEnable(boolean audioEnable)
设置音频是否开启传输

### setVideoEnable(boolean videoEnable)
设置视频是否开始传输

### setDenoiseEnable(boolean denoiseEnable)
设置是否开启噪音抑制

### setAecEnable(boolean aecEnable)
设置是否开启回音消除
>当播放使用NodePlayer, 双方音频编码为SPEEX时有效

### setKeyFrameInterval(int keyFrameInterval)
设置视频关键帧间隔的帧数

### setPublishType(int publishType)
设置发布类型
- NodePublisher.PUBLISH_TYPE_LIVE
- NodePublisher.PUBLISH_TYPE_RECORD
- NodePublisher.PUBLISH_TYPE_APPEND

### setNodePublisherDelegate(NodePublisherDelegate delegate)
设置[事件回调](事件回调)代理

## 方法

### switchCamera()
切换前后摄像头

### startPreview()
摄像头开始预览

### stopPreview()
摄像头停止预览

### capturePicture(CapturePictureListener listener)
截图,传入监听器, 当截图完成后回调Bitmap. 需要自行处理Bitmap,如另存为jpeg.

### start()
开始推流

### stop()
停止推流

### release()
释放底层资源

## 事件回调
### 2000
正在发布视频

### 2001
视频发布成功

### 2002
视频发布失败

### 2004
视频发布结束

### 2005
视频发布中途,网络异常,发布中断

### 2100
网络阻塞, 发布卡顿

### 2101
网络恢复, 发布流畅

## VIDEO_PPRESET
```
public static final int VIDEO_PPRESET_16X9_270 = 0;
public static final int VIDEO_PPRESET_16X9_360 = 1;
public static final int VIDEO_PPRESET_16X9_480 = 2;
public static final int VIDEO_PPRESET_16X9_540 = 3;
public static final int VIDEO_PPRESET_16X9_720 = 4;

public static final int VIDEO_PPRESET_4X3_270 = 10;
public static final int VIDEO_PPRESET_4X3_360 = 11;
public static final int VIDEO_PPRESET_4X3_480 = 12;
public static final int VIDEO_PPRESET_4X3_540 = 13;
public static final int VIDEO_PPRESET_4X3_720 = 14;

public static final int VIDEO_PPRESET_1X1_270 = 20;
public static final int VIDEO_PPRESET_1X1_360 = 21;
public static final int VIDEO_PPRESET_1X1_480 = 22;
public static final int VIDEO_PPRESET_1X1_540 = 23;
public static final int VIDEO_PPRESET_1X1_720 = 24;
```