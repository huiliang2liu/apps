/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.base.R;

import java.math.BigDecimal;

public class Sidebar extends View {
    private Paint paint;
    //	private TextView header;
    private float height;
    private Context context;
    private SidebarListener change;

    public void setSidebarListener(SidebarListener sidebarListener) {
        this.change = sidebarListener;
    }

    public Sidebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private String[] sections = new String[]{"↑", "☆", "A", "B", "C", "D",
            "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.DKGRAY);
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(BigDecimal.valueOf(context.getResources().getDisplayMetrics().scaledDensity * 10).setScale(0, BigDecimal.ROUND_CEILING).intValue());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float center = getWidth() / 2;
        height = getHeight() / sections.length;
        for (int i = sections.length - 1; i > -1; i--) {
            canvas.drawText(sections[i], center, height * (i + 1), paint);
        }
    }

    private int sectionForPoint(float y) {
        int index = (int) (y / height);
        if (index < 0) {
            index = 0;
        }
        if (index > sections.length - 1) {
            index = sections.length - 1;
        }
        return index;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
//			if (header == null) {
//				header = (TextView) ((View) getParent())
//						.findViewById(R.id.floating_header);
//			}
//			header.setVisibility(View.VISIBLE);
//			String headerString = sections[sectionForPoint(event.getY())];
//			if (change != null)
//				change.onClickL(headerString);
//			header.setText(headerString);
                if(change!=null)
                    change.onSidebarDown(sections[sectionForPoint(event.getY())]);
                setBackgroundResource(R.drawable.sidebar_background_pressed);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                return true;
            }
            case MotionEvent.ACTION_UP:
//			header.setVisibility(View.INVISIBLE);
                if(change!=null)
                    change.onSidebarUp(sections[sectionForPoint(event.getY())]);
                setBackgroundColor(Color.TRANSPARENT);
                return true;
            case MotionEvent.ACTION_CANCEL:
                if(change!=null)
                    change.onSidebarUp(sections[sectionForPoint(event.getY())]);
//			header.setVisibility(View.INVISIBLE);
                setBackgroundColor(Color.TRANSPARENT);
                return true;
        }
        return super.onTouchEvent(event);
    }

    public interface SidebarListener {
        public void onSidebarDown(String string);
        public void onSidebarUp(String string);
    }
}
