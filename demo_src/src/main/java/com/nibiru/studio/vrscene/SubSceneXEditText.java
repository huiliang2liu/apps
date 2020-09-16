package com.nibiru.studio.vrscene;

import android.graphics.Color;
import android.util.Log;

import com.nibiru.studio.xrdemo.R;

import x.core.listener.IXEditTextListener;
import x.core.listener.IXMultiIMEStateListener;
import x.core.ui.XBaseScene;
import x.core.ui.XEditText;
import x.core.ui.XLabel;
import x.core.ui.XLabel.XAlign;
import x.core.ui.XLabel.XArrangementMode;
import x.core.ui.XMultiLanguageInputMethod;

/**
 * 演示文本输入框，文本输入框在Label基础上支持设置背景框，并且自动绑定IME，点击即可弹出键盘，可设置键盘语言
 */

/**
 * Show text edit box, which supports background frame on the basis of Label. It binds IME automatically. Click it to pop up the keyboard and to set keyboard language.
 */
public class SubSceneXEditText extends XBaseScene {

    XEditText mEditText;
    XEditText mEditPwd;
    XLabel inputResult;
    XEditText mEditTextTest2;

    @Override
    public void onCreate() {

        init();
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

    public void init() {

        XLabel titleLabel = new XLabel("Example：EditText");
        titleLabel.setAlignment(XAlign.Center);
        titleLabel.setArrangementMode(XArrangementMode.SingleRowNotMove);
        titleLabel.setCenterPosition(0, 1.0f, -4f);
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setSize(0.8f, 0.3f);
        addActor(titleLabel);

        //Example: EditText
        //设置编辑框的样式
        //Set edit box style
        XEditText.XEditStyle editStyle = new XEditText.XEditStyle();
        //背景色为白色
        //Set background color to white
        editStyle.backgroundColor = Color.WHITE;
        //边框为黑色
        //Set stroke to black
        editStyle.strokeColor = Color.argb(255, 0, 0, 0);
        //设置边框宽度
        //Set stroke width
        editStyle.strokeWidth = 0.0F;
        //设置文本颜色
        //Set text color
        editStyle.textColor = Color.BLACK;
        //设置光标颜色，默认Color.GRAY
        //Set cursor color, default: Color.BLACK
        editStyle.cursorColor = Color.BLACK;
        //设置光标粗细，默认为2.5
        //Set cursor width, default: 2.5
        editStyle.cursorWidth = 3.5f;
        //设置输入器显示状态下选中点在edittext区域内时的选中点颜色，默认Color.GRAY
        //Set gaze color in edittext when the input device is displayed, default: Color.GRAY
        editStyle.gazeColor = Color.GREEN;

//        //设置输入文本的布局，默认为裁剪模式，也就是超过长度显示省略号
//        //Set the layout of input text, it's clip mode by default, i.e. ellipsis will be displayed for the overlength part
//        editStyle.xArrangementMode = XArrangementMode.SingleRowMove;

//        //设置提示文字的布局，默认为裁剪模式，也就是超过长度显示省略号
//        //Set the layout of the hint text, it's clip mode by default, i.e. ellipsis will be displayed for the overlength part
        editStyle.xHintArrangementMode = XArrangementMode.SingleRowClip;


//        //设置输入文本的布局，默认为裁剪模式，也就是超过长度显示省略号
//        //Set the layout of input text, it's clip mode by default, i.e. ellipsis will be displayed for the overlength part
        editStyle.xArrangementMode = XArrangementMode.SingleRowMove;

//        //设置提示文字的布局，默认为裁剪模式，也就是超过长度显示省略号
//        //Set the layout of the hint text, it's clip mode by default, i.e. ellipsis will be displayed for the overlength part
//        editStyle.xHintArrangementMode = XArrangementMode.SingleRowClip;

        //支持设置string.xml定义的资源字符串，方便国际化
        //Support setting resource strings by string.xml for internationalization
        mEditText = new XEditText(R.string.edittext_hint_test, editStyle);
        mEditText.setName("XEditText");
//        mEditText.setTextContent("Test Text Content Here");

        //支持在输入器外部点击确认键时，隐藏当前键盘
        //Support dismiss inputmethod panel when press OK outside of IME.
        mEditText.setEnableHideIMEOutside(true);

        mEditText.setCenterPosition(0, 0f, -3.85f);
        mEditText.setSize(2.0f, 0.3f);

        mEditText.setEditTextListener(new IXEditTextListener() {
            @Override
            public void afterTextChanged(XEditText editText, XMultiLanguageInputMethod.XInputKeyCode keyCode, String text) {
                if (keyCode == XMultiLanguageInputMethod.XInputKeyCode.KeyType_Enter ){
                    //演示回车键按下时隐藏输入器
                    //Hide input device when Enter is pressed

                    editText.getIME().dismiss();

                    //演示清空输入内容，此时将显示提示文字
                    //The hint text will be displayed when the input content is cleared
//                    editText.setTextContent("");
                }
            }
        });

        //设置文本大小，设置值为占整个EditText高度的比例值，比如0.6为文字占整个EditText高度的60%，并居中显示
        //Set text size, the value is the ratio of text size to EditText height, e.g. 0.6 stands that the text size is 60% of EditText height, and it is displayed in the middle
        mEditText.setTextSize(0.6f);

        //设置最大字符长度
        //Set the maximum character length

        mEditText.setMaxLength(40);

        addActor(mEditText);

        mEditPwd = new XEditText("Input Password", editStyle);
        mEditPwd.setName("XEditPassword");
        mEditPwd.setCenterPosition(0, -0.5f, -3.85f);
        mEditPwd.setEditTextListener(new IXEditTextListener() {
            @Override
            public void afterTextChanged(XEditText editText, XMultiLanguageInputMethod.XInputKeyCode keyCode, String text) {
                Log.d("test", "pwd after input: "+text);
            }
        });
        mEditPwd.setSize(2.0f, 0.3f);

        //设置密码类型，密码类型的输入框默认键盘为英文键盘
        //Set password type, the input box is English keyboard by default
        mEditPwd.setTextType(XEditText.TextType.PASSWORD);

        //不使用光标模式，默认开启
        //Disable cursor mode, it's enabled by default
        mEditPwd.setEnableCursor(false);

        mEditPwd.setMaxLength(20);
        addActor(mEditPwd);

        XEditText edit = new XEditText("Input Test1", editStyle);

        //如果要显示已存在的字符串，调用setTextContent方法
        //If the existing strings have to be displayed, call setTextContent
        edit.setTextContent("Previous Text");

        edit.setName("XEdit1");
        edit.setCenterPosition(0, -1.0f, -3.85f);
        edit.setSize(2.0f, 0.3f);

        //设置首选键盘为英文键盘，如果不设置，默认将使用系统语言的键盘
        //Set English keyboard as the preferred keyboard, if it's not set, the system language keyboard will be used by default
//        edit.setIMEFirstLanguage(GlobalIME.LANGUAGE.ENGLISH);

        edit.setMaxLength(20);
        //加入输入监听，在用户使用输入器输入时回调输入的内容
        //Add listener for input, call back the input content when it's input by user
        edit.setEditTextListener(new IXEditTextListener() {
            @Override
            public void afterTextChanged(XEditText editText, XMultiLanguageInputMethod.XInputKeyCode keyCode, String text) {
                Log.d("test", "after keycode: "+keyCode+" text input: "+text);
            }
        });
        addActor(edit);

        XEditText.XEditStyle editStyle2 = new XEditText.XEditStyle();
        //设置背景颜色，设置值为Color生成的值
        //Set background color, the value is generated by Color
        editStyle2.backgroundColor = Color.WHITE;
        //设置边框颜色
        //Set stroke color
        editStyle2.strokeColor = Color.argb(255, 0, 0, 0);
        //设置边框宽度
        //Set stroke width
        editStyle2.strokeWidth = 0.0f;
        //设置超出最大显示长度时进行裁剪，不开启跑马灯
        //Disable marquee and carry out clipping, when it's overlength
        editStyle2.xArrangementMode = XArrangementMode.SingleRowClipRight;
        //设置文本颜色
        //Set text color
        editStyle2.textColor = Color.BLUE;
        //设置光标颜色
        //Set cursor color
//        editStyle2.cursorColor = Color.GREEN;
        //设置选中点颜色
        //Set Gaze point color
        editStyle2.gazeColor = Color.GREEN;

        mEditTextTest2 = new XEditText(R.string.edittext_hint_number, editStyle2);

        //设置为数字键盘
        //Set to numeric keyboard
        mEditTextTest2.setNumberKeyboard(true);

        mEditTextTest2.setName("XEdit2");
        mEditTextTest2.setCenterPosition(0, -1.5f, -3.85f);
        mEditTextTest2.setSize(2.0f, 0.3f);
        mEditTextTest2.setEditTextListener(new IXEditTextListener() {
            @Override
            public void afterTextChanged(XEditText editText, XMultiLanguageInputMethod.XInputKeyCode keyCode, String text) {
                if (keyCode == XMultiLanguageInputMethod.XInputKeyCode.KeyType_Enter ){
                    //演示回车键按下时隐藏输入器
                    //Hide input device when Enter is pressed
//                    XLog.logInfo("prepare dismiss");
                    editText.getIME().dismiss();

                    //演示清空输入内容，此时将显示提示文字
                    //Display the hint text when input content is cleared
//                    editText.setTextContent("");
                }
            }
        });
        mEditTextTest2.setMaxLength(20);

        //设置输入器相对EditText的位置偏移量
        //Set the offset of input device to EditText
        mEditTextTest2.setIMEOffset(0, 0.2f, 0);

        //给输入器加入监听，在输入器隐藏时更新Label为输入的内容
        //Add listener to input device, update Label to the input content when the input device is hidden
        mEditTextTest2.setIMEStateListener(new IXMultiIMEStateListener() {
            @Override
            public void onIMEShow() {

            }

            //在输入器隐藏时更新Label为输入的内容
            //Update Label to the input content when the input device is hidden
            @Override
            public void onIMEHide() {
                if( mEditTextTest2 != null && inputResult != null ){
                    inputResult.setTextContent(mEditTextTest2.getTextContent());
                }
            }
        });
        addActor(mEditTextTest2);

        inputResult = new XLabel("Test2: No Input");
        inputResult.setAlignment(XAlign.Center);
        inputResult.setArrangementMode(XArrangementMode.SingleRowNotMove);
        inputResult.setCenterPosition(0, -2.0f, -4f);
        inputResult.setTextColor(Color.WHITE);
        inputResult.setSize(0.8f, 0.3f);

        addActor(inputResult);

    }

//     public boolean onKeyDown(int keycode){
//        if( keycode == NibiruKeyEvent.KEYCODE_ENTER ){
//            if( mEditText != null ){
//                mEditText.setEnabled(!mEditText.isEnabled());
//            }
//        }
//
//        return super.onKeyDown(keycode);
//     }

}