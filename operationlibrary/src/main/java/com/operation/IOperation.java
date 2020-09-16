package com.operation;

public interface IOperation {
    /**
     * description：发送按键事件
     *
     * @param keys
     */
    void keyevent(int... keys);

    /**
     * description：发送滑动事件
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param time
     */
    void move(int startX, int startY, int endX, int endY, int time);

    /**
     * description：发送点击事件
     *
     * @param x
     * @param y
     */
    void click(int x, int y);

    /**
     * description：发送文本
     *
     * @param text
     */
    void text(String text);

    class OperationBuilder {
        public IOperation build() {
            return new AdbOperationImpl();
        }
    }
}
