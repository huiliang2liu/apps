package com.nibiru.studio.vrscene;

import android.graphics.Color;
import android.util.Log;

import x.core.ime.GlobalIME;
import x.core.ui.XBaseScene;
import x.core.ui.XEditText;

/**
 * 演示多国语言输入，EditText设置键盘语言后，支持输入不同国家的文本
 */

/**
 * Show multilingual IME, after EditText setting keyboard language, the inputs of multilingual texts are supported
 */
public class SubSceneIME extends XBaseScene {

	XEditText.XEditStyle editStyle;

	final int mRowNum = 8;

	//添加EditText的公共方法，参数为语言和索引值
	//Add the public method: EditText, parameters are language and index value
	XEditText addEditText(GlobalIME.LANGUAGE language, int index){
		if( editStyle == null ) {
			editStyle = new XEditText.XEditStyle();
			editStyle.backgroundColor = -2;
			editStyle.strokeColor = Color.argb(255, 0, 0, 0);
			editStyle.strokeWidth = 0.0F;
			editStyle.textColor = Color.GRAY;
		}


		XEditText editText = new XEditText("Input "+language.name(),editStyle);
		editText.setName("xEditText");
		editText.setCenterPosition(((index / mRowNum == 0) ? -1.5f : 1.5f), 1f - 0.5f * (index % mRowNum) , -4f);
		editText.setSize(2.5f, 0.3f);
		editText.setMaxLength(20);
		//设置EditText的键盘语言，设置后默认为设置语言键盘，可切换为英文键盘，如果系统不支持该语言则默认显示英文键盘
		//Set keyboard language of EditText, after it's set, the default is to set language keyboard, English keyboard can be switched to. If the language is not supported by system, English keyboard is displayed by default
		editText.setIMELanguage(language);

		//如果需要首选英文键盘，调用下面的方法说
		//If English keyboard is preferred, call the following method
//		editText.setIMEFirstLanguage(GlobalIME.LANGUAGE.ENGLISH);

		addActor(editText);

		return editText;
	}

	public void init() {
		//Example: EditText

		int index = 0;

		GlobalIME.LANGUAGE[] languages = GlobalIME.LANGUAGE.values();

		for( int i = 0; i < languages.length; i++ ){
			Log.d("test", "add ime: "+languages[i]);
			addEditText(languages[i], i);
		}
	}

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

}
