package com.nibiru.studio.vrscene;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.nibiru.service.NibiruKeyEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import x.core.listener.IXActorEventListener;
import x.core.listener.IXModelLoadListener;
import x.core.ui.XActor;
import x.core.ui.XBaseScene;
import x.core.ui.XLabel;
import x.core.ui.XModelActor;
import x.core.ui.XStaticModelActor;
import x.core.util.XLog;

/**
 * 演示静态模型，目前支持AMF 3DS AC ASE ASSBIN ASSXML B3D BVH COLLADA DXF CSM
 * HMP IRRMESH IRR LWO LWS MD2 MD3 MD5 MDC MDL NFF NDO OFF OBJ OGRE OPENGEX PLY
 * MS3D COB BLEND IFC XGL FBX Q3D Q3BSP RAW SIB SMD STL TERRAGEN 3D X X3D GLTF 3MF MMD
 * STEP
 * 暂时不支持骨骼动画
 */

/**
 * Show static model, Obj model is supported to display
 */
public class StaticModelTest extends XBaseScene implements IXModelLoadListener {

    private JSONArray centerPositionarray;
    private JSONArray rotationarray;
    private double scalefactor;
    private String modelpath;

    @Override
    public void onCreate() {
        String info = readFile2String(new File("sdcard/models/model.txt"), "utf-8");
        if (!TextUtils.isEmpty(info)) {
            try {
                JSONObject jsonObj = new JSONObject(info);
                JSONObject sceneObject = jsonObj.getJSONObject("ModelBean");
                if (sceneObject != null) {
                    centerPositionarray = sceneObject.getJSONArray("centerposion");
                    rotationarray = sceneObject.getJSONArray("rotation");
                    scalefactor = sceneObject.getDouble("scalefactor");
                    modelpath = sceneObject.getString("modelpath");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        init();
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String readFile2String(final File file, final String charsetName) {
        byte[] bytes = readFile2BytesByStream(file);
        if (bytes == null) return null;
        if (isSpace(charsetName)) {
            return new String(bytes);
        } else {
            try {
                return new String(bytes, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    private static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    private static byte[] is2Bytes(final InputStream is) {
        if (is == null) return null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            byte[] b = new byte[8192];
            int len;
            while ((len = is.read(b, 0, 8192)) != -1) {
                os.write(b, 0, len);
            }
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] readFile2BytesByStream(final File file) {
        if (!isFileExists(file)) return null;
        try {
            return is2Bytes(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
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

    XStaticModelActor actor;
    XLabel tip;

    public void init() {
        XLabel titleLabel = new XLabel("ModelPath: " + modelpath);
        titleLabel.setAlignment(XLabel.XAlign.Center);
        titleLabel.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 1.5f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        //初始化提示文本
        tip = new XLabel("Prepare Loading Model...");
        tip.setAlignment(XLabel.XAlign.Center);
        tip.setArrangementMode(XLabel.XArrangementMode.SingleRowNotMove);
        tip.setCenterPosition(0, 0f, -4f);
        tip.setTextColor(Color.WHITE);
        tip.setSize(0.8f, 0.2f);
        addActor(tip);

        if (TextUtils.isEmpty(modelpath)) {
            tip.setTextContent("Ops! Load Model Failed. Please check the file path and location");
            return;
        }
        //静态OBJ模型默认支持SD卡加载
        actor = new XStaticModelActor(modelpath);
        try {
            actor.setStaticModelTranslate((float) centerPositionarray.getDouble(0), (float) centerPositionarray.getDouble(1), (float) centerPositionarray.getDouble(2));
            actor.setRotation((float) rotationarray.getDouble(0), (float) rotationarray.getDouble(1), (float) rotationarray.getDouble(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //平移后设置旋转中心
        actor.setStaticModelScale((float) scalefactor);
        //设置模型加载监听
        actor.setModelLoadListener(this);
        //设置模型选中监听（可选）
        actor.setEventListener(new IXActorEventListener() {
            @Override
            public void onGazeEnter(XActor actor) {
                XLog.logInfo("on gaze enter: " + actor.getActorDesc());
            }

            @Override
            public void onGazeExit(XActor actor) {
                XLog.logInfo("on gaze exit: " + actor.getActorDesc());
            }

            @Override
            public boolean onGazeTrigger(XActor actor) {
                XLog.logInfo("on gaze trigger: " + actor.getActorDesc());
                return false;
            }

            @Override
            public void onAnimation(XActor actor, boolean isSelected) {

            }
        });
        //为显示效果可以隐藏选中点
        hideGaze();
        addActor(actor);
    }

    //    旋转模型
    public void update(float delta) {
        super.update(delta);
        //如果尚未加载完成，不进行旋转
        if (actor != null) {
            if (actor.getModelLoadState() != XModelActor.MODEL_LOAD_STATE.COMPLETED) return;
        }
        if (actor != null && actor.isCreated()) {
            //绕Y轴旋转
            //Rotate around y axis
            //每秒转15角度(FPS60)
            //Rotate 15 degree per second (FPS60)
            actor.rotationByY(15 / 60.0f);
        }
    }

    //开始加载模型
    //Start to load model
    @Override
    public void onStartLoad(XModelActor actor) {
        if (tip != null) {
            tip.setTextContent("Loading Model...wait...");
        }
    }

    //模型加载完成
    //The model loading is done
    @Override
    public void onLoadCompleted(XModelActor actor) {
        if (tip != null) {
            tip.setEnabled(false);
        }
    }

    //模型加载失败
    //Fail to load the model
    @Override
    public void onLoadFailed(XModelActor actor) {
        if (tip != null) {
            tip.setTextContent("Ops! Load Model Failed. Please check the file path and location");
        }
    }
}
