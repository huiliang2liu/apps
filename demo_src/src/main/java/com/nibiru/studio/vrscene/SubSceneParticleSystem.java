package com.nibiru.studio.vrscene;

import x.core.ui.XBaseScene;
import x.core.ui.XParticleSystemActor;
import x.core.ui.XUI;

public class SubSceneParticleSystem extends XBaseScene
{

    @Override
    public void onCreate() {

        XParticleSystemActor particleSystemActor = new XParticleSystemActor("rain_drop.png", XUI.Location.ASSETS);
        particleSystemActor.addSphereEmitter(5.0f);
        particleSystemActor.addScaleAffector(0.1f,0.1f,0.1f);
        particleSystemActor.addLostAffector(0,-0.03f,0);
        particleSystemActor.setCenterPosition(0,0,-6);
        addActor(particleSystemActor);
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
}
