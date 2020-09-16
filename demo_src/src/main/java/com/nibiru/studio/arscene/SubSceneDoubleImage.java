package com.nibiru.studio.arscene;

import com.nibiru.studio.CalculateUtils;

import x.core.ui.XDoubleSideImage;

/**
 * Created by Administrator on 2018/5/9.
 */

public class SubSceneDoubleImage extends BaseScene {

    XDoubleSideImage xDoubleSideImage;

    @Override
    public void onCreate() {
        super.onCreate();
        xDoubleSideImage = new XDoubleSideImage("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        xDoubleSideImage.setSize(0.2f, 0.2f);
        xDoubleSideImage.setCenterPosition(-0.1f, 0, CalculateUtils.CENTER_Z);
        xDoubleSideImage.setPageAnimateType(XDoubleSideImage.PageAnimateType.Right);
        addActor(xDoubleSideImage);

    }

    int count = 0;
    int angle = 0;

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(count == 1) {
            angle += 1;
            if(angle > 180) {
                angle = 0;
            }
            xDoubleSideImage.pageAnimate(angle);
            count = 0;
        }
        count++;
    }

}
