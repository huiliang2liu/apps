package com.threelogin;

/**
 * User：liuhuiliang
 * Date:2019-12-16
 * Time:11:51
 * Descripotion
 */
public interface LoginListener {
    /**
     * description：登录成功
     * @param:entity
     */
    void onLoginSuccess(UserEntity entity);

    /**
     * description：登录失败
     */
    void onLoginFailure();
}
