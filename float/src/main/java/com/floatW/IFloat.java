package com.floatW;

import android.animation.Animator;

public interface IFloat {
    void setGravity(int gravity);

    void setX(int x);

    void setY(int y);

    void setAlpha(float alpha);


    void setHideAnimator(Animator animator);

    void setShowAnimator(Animator animator);

    void show();

    void dismiss();
}
