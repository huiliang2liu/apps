package com.nibiru.studio.arscene;

import com.nibiru.studio.CalculateUtils;

import x.core.ui.XParticleSystemActor;
import x.core.ui.XUI;

public class SubSceneParticleSystem extends BaseScene
{

    @Override
    public void onCreate() {
        XParticleSystemActor particleSystemActor = new XParticleSystemActor("rain_drop.png", XUI.Location.ASSETS);
        particleSystemActor.addSphereEmitter(1.0f);
        particleSystemActor.addScaleAffector(0.01f,0.01f,0.01f);
        particleSystemActor.addLostAffector(0,-0.03f,0);
        particleSystemActor.setCenterPosition(0,0, CalculateUtils.CENTER_Z);
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
