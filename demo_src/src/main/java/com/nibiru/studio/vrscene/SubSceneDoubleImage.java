package com.nibiru.studio.vrscene;

import x.core.ui.XBaseScene;
import x.core.ui.XDoubleSideImage;

/**
 * Created by Administrator on 2018/5/9.
 */

public class SubSceneDoubleImage extends XBaseScene {

    XDoubleSideImage xDoubleSideImage;

    @Override
    public void onCreate() {
        xDoubleSideImage = new XDoubleSideImage("ic_panoramic_video_focused.png", "ic_panoramic_video_default.png");
        xDoubleSideImage.setSize(2f, 2f);
        xDoubleSideImage.setCenterPosition(-1f, 0, -4f);
        xDoubleSideImage.setPageAnimateType(XDoubleSideImage.PageAnimateType.Right);
        addActor(xDoubleSideImage);

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
