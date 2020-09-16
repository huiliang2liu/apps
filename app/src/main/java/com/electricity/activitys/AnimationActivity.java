package com.electricity.activitys;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.widget.book.CurlPage;
import com.base.widget.book.CurlView;
import com.electricity.R;
import com.screen.iface.BackColor;
import com.screen.iface.CustomAdapt;
import com.screen.iface.FullScreen;
import com.screen.iface.TextStyle;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;

public class AnimationActivity extends FragmentActivity implements BackColor, FullScreen, CustomAdapt, TextStyle {

    private static final String TAG = "AnimationActivity";
    private CurlView cv_content;
    private String[] imgArray = {"000.jpg", "001.jpg", "002.jpg", "003.jpg"};
    private int cv_height;
    private ArrayList<String> imgList = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        ButterKnife.bind(this);
        cv_content = (CurlView) findViewById(R.id.cv_content);
        copyImage();
        showImage();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Log.e(TAG, String.format("%f,%d,%f,%f", displayMetrics.density, displayMetrics.densityDpi, displayMetrics.scaledDensity, displayMetrics.xdpi));
    }

    public void text(View view) {
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        else
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        TextView textView = (TextView) view;

        Log.e(TAG, String.format("%f,%d", textView.getTextSize(), textView.getHeight()));
        Paint.FontMetrics fontMetricsInt = textView.getPaint().getFontMetrics();
        Log.e(TAG, String.format("%f,%f,%f,%f,%f", fontMetricsInt.top, fontMetricsInt.ascent, fontMetricsInt.leading, fontMetricsInt.descent, fontMetricsInt.bottom));
//        textView.setTypeface(Typeface.createFromAsset(getAssets(), "ali.ttf   }
    }

    private void copyImage() {
        cv_height = cv_content.getMeasuredHeight();
        for (int i = 0; i < imgArray.length; i++) {
            String imgName = imgArray[i];
            String imgPath = imgName;
            imgList.add(imgPath);
            if (i == 0) {
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getResources().getAssets().open(imgPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cv_height = (int) (bitmap.getHeight() * 1.2);
            }
        }
    }

    private void showImage() {
        ViewGroup.LayoutParams params = cv_content.getLayoutParams();
        params.height = cv_height;
        cv_content.setLayoutParams(params);
        cv_content.setPageProvider(new PageProvider(imgList));
        cv_content.setSizeChangedObserver(new SizeChangedObserver());
        cv_content.setCurrentIndex(0);
        cv_content.setBackgroundColor(Color.LTGRAY);
    }

    private class PageProvider implements CurlView.PageProvider {
        private ArrayList<String> mPathArray = new ArrayList<String>();

        public PageProvider(ArrayList<String> pathArray) {
            mPathArray = pathArray;
        }

        @Override
        public int getPageCount() {
            return mPathArray.size();
        }

        private Bitmap loadBitmap(int width, int height, int index) {
            Bitmap b = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            b.eraseColor(0xFFFFFFFF);
            Canvas c = new Canvas(b);
            //Drawable d = getResources().getDrawable(mBitmapIds[index]);
            Bitmap image = null;
            try {
                image = BitmapFactory.decodeStream(getResources().getAssets().open(mPathArray.get(index)));
                return image;
            } catch (IOException e) {
                e.printStackTrace();
            }
            BitmapDrawable d = new BitmapDrawable(getResources(), image);

            int margin = 0;
            int border = 1;
            Rect r = new Rect(margin, margin, width - margin, height - margin);

            int imageWidth = r.width() - (border * 2);
            int imageHeight = imageWidth * d.getIntrinsicHeight()
                    / d.getIntrinsicWidth();
            if (imageHeight > r.height() - (border * 2)) {
                imageHeight = r.height() - (border * 2);
                imageWidth = imageHeight * d.getIntrinsicWidth()
                        / d.getIntrinsicHeight();
            }

            r.left += ((r.width() - imageWidth) / 2) - border;
            r.right = r.left + imageWidth + border + border;
            r.top += ((r.height() - imageHeight) / 2) - border;
            r.bottom = r.top + imageHeight + border + border;

            Paint p = new Paint();
            p.setColor(0xFFC0C0C0);
            c.drawRect(r, p);
            r.left += border;
            r.right -= border;
            r.top += border;
            r.bottom -= border;
            d.setBounds(r);
            d.draw(c);
            return b;
        }

        @Override
        public void updatePage(CurlPage page, int width, int height, int index) {
            Log.e(TAG, "======" + index);
            Bitmap front = loadBitmap(width, height, index);
            page.setTexture(front, CurlPage.SIDE_BOTH);
        }
    }


    /**
     * CurlView size changed observer.
     */
    private class SizeChangedObserver implements CurlView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int w, int h) {
            cv_content.setViewMode(CurlView.SHOW_ONE_PAGE);
            cv_content.setMargins(0f, 0f, 0f, 0f);
        }
    }

    @Override
    public int backColor() {
        int a = (int) (Math.random() * 10);
        return a % 2 == 0 ? Color.WHITE : Color.RED;
    }


    @Override
    public int width() {
        return 540;
    }

    @Override
    public void setTextStyle(TextView textView, String textStyle) {
        Log.e(TAG, textStyle);
        textView.setTextSize(50);
        textView.setTextColor(Color.GREEN);
    }
}
