#不忽略非公共的库类
-dontskipnonpubliclibraryclasses
#// 指定代码的压缩级别
-optimizationpasses 5
#//包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#优化 不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/,!class/merging/
#保持注解
-keepattributes Annotation

#忽略警告
-ignorewarning
#这句话很重要，帮忙定位错误的
-keepattributes SourceFile,LineNumberTable

#记录生成的日志数据,gradle build时在本项目根目录输出
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt

# 保持哪些类不被混淆
-keep public class * extends android.support.v4.app.FragmentActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.support.v4.app.Fragment

#保持自定义控件类 不被混淆--可以去掉的东西（误删，后期使用）——开始
    -keepclasseswithmembers class * {
       public <init>(android.content.Context, android.util.AttributeSet);
    }
    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet, int);
    }
    -keepclassmembers class * extends android.app.Activity {
       public void *(android.view.View);
    }
#结束

 # 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

 # 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#如果有引用v4或者v7包，需添加
-keep public class * extends android.support.**


#不混淆资源类
-keepclassmembers class **.R$* {*;}

#保留java注入
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#gson解析不被混淆
#-keep class com.google.**{*;}
#-keepclassmembers class * implements java.io.Serializable {
#   static final long serialVersionUID;
#  private static final java.io.ObjectStreamField[] serialPersistentFields;
# private void writeObject(java.io.ObjectOutputStream);
#private void readObject(java.io.ObjectInputStream);
#java.lang.Object writeReplace();
#java.lang.Object readResolve();
#}

-keep class com.nibiru.interaction.** {*;}
-keep class com.google.** {*;}
-keep class com.nibiru.lib.** {*;}
-keep class com.nibiru.vrconfig.** {*;}
-keep class org.bouncycastle.** {*;}
-keep class com.nostra13.** {*;}
-keep class javax.** {*;}
-keep class android.app.** {*;}
-keep class x.core.** {*;}
-keep class com.hp.** {*;}
-keep class com.drew.** {*;}

-keep class com.dlodlo.**{*;}
-keep class com.google.** {*;}
-keep class com.sixdof.**{*;}
-keep class ruiyue.**{*;}
-keep class x.core.ui.view.**{*;}

#混淆第三方jar包，其中xxx为jar包名
#-libraryjars libs/xxx.jar
#不混淆某个包内的所有文件
#-keep class com.xxx.**{*;}
#忽略某个包的警告
#-dontwarn com.xxx**

#不混淆泛型
-keepattributes Signature

#不混淆Serializable
#-keepnames class * implements java.io.Serializable

##不混淆某个包内的所有文件
#-keep class org.apache.commons.codec**{*;}
#-keep class com.tencent.**{*;}
#-keep class uk.co.senab.photoview.**{*;}
#-keep class com.nostra13.**{*;}


#在app的proguard-rules.pro忽略依赖库中的文件写法如下：
#-libraryjars  ../XXX(此处为library名称)/src/main/jniLibs/armeabi/xxxxx.so
#-libraryjars ../XXX(此处为library名称)/src/main/jniLibs/armeabi-v7a/xxxxx.so
#-libraryjars ../XXX(此处为library名称)/src/main/jniLibs/x86/xxxxx.so

#同理依赖库中jar包在app中忽略混淆的写法
#-libraryjars ../XXX(此处为library名称)/libs/xxx.jar
#-libraryjars ../XXX(此处为library名称)/libs/xxx.jar

#NibiruVRLib2_rel
#-libraryjars ../NibiruVRLib2_rel/libs/armeabi-v7a/libnvr_local.so
#-libraryjars ../NibiruVRLib2_rel/libs/x86/libnvr_local.so
#-libraryjars ../NibiruVRLib2_rel/libs/x86_64/libnvr_local.so
#-libraryjars ../NibiruVRLib2_rel/libs/nibiruvrlib_2_1_3.jar
#-libraryjars ../NibiruVRLib2_rel/libs/nibiru_vr_sdk_v1.jar
#
##NibiruVRXEngineLib_rel
#-libraryjars ../NibiruVRXEngineLib_rel/libs/armeabi-v7a/libfreetype.so
#-libraryjars ../NibiruVRXEngineLib_rel/libs/armeabi-v7a/libpngo.so
#-libraryjars ../NibiruVRXEngineLib_rel/libs/armeabi-v7a/libtingxml.so
#-libraryjars ../NibiruVRXEngineLib_rel/libs/armeabi-v7a/libXEngine.so
#-libraryjars ../NibiruVRXEngineLib_rel/libs/nibiru_studio_1_3_11.jar
#


-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}

-keep interface com.nibiru.service.NibiruBaseVerifyService.IServiceErrorListener{
   public <fields>;
   public <methods>;
   protected <methods>;
   protected <fields>;
}

-keep class com.nibiru.service.** {*;}
-keep class com.nibiru.voicesdk.** {*;}
-keep class ruiyue.gesture.sdk.** {*;}

-keep class com.qualcomm.svrapi.SvrApi{
   <fields>;
   <methods>;
   native <methods>;
}
-keep class com.qualcomm.svrapi.** {*;}
-keep class com.qualcomm.snapdragonvrservice.** {*;}
