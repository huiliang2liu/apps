package com.base.time.calendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.R;
import com.base.adapter.RecyclerViewAdapter;
import com.base.time.TimeUtil;
import com.base.time.Week;
import com.base.util.L;
import com.base.widget.Decoration;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarView extends RecyclerView {
    private static final String TAG = "CalendarView";
    private int year;
    private int month;
    private String day;
    private RecyclerViewAdapter<Object> adapter;
    private String[] week;
    private int width = 150;
    private int height = 100;
    private int lineWidth = 1;
    private Drawable lineDrawable;
    private Drawable weekDrawable = new ColorDrawable(Color.RED);
    private ColorStateList weekColor = ColorStateList.valueOf(Color.WHITE);
    private Drawable textSelectDrawable = new ColorDrawable(Color.YELLOW);
    private Drawable textDrawable = new ColorDrawable(Color.WHITE);
    private ColorStateList textSelectColor = ColorStateList.valueOf(Color.RED);
    private ColorStateList textColor = ColorStateList.valueOf(Color.BLACK);
    private ColorStateList otheTextColor = ColorStateList.valueOf(Color.argb(0x80, 0x00, 0x00, 0x00));
    private int weekSize = 20;
    private Typeface weekTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
    private int daySize = 15;
    private Typeface dayTypeface = Typeface.defaultFromStyle(Typeface.NORMAL);
    private Typeface daySelectTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
    private int myYear;
    private int myMonth;

    public CalendarView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setOverScrollMode(OVER_SCROLL_NEVER);
        width = getContext().getResources().getDisplayMetrics().widthPixels / 7;
        if (attrs != null) {
            TypedArray a = getResources().obtainAttributes(attrs, R.styleable.CalendarView);
            width = a.getDimensionPixelSize(R.styleable.CalendarView_calendar_item_width, width);
            height = a.getDimensionPixelSize(R.styleable.CalendarView_calendar_item_height, height);
            lineWidth = a.getDimensionPixelSize(R.styleable.CalendarView_calendar_line, lineWidth);
            lineDrawable = a.getDrawable(R.styleable.CalendarView_calendar_line_color);
            Drawable drawable = a.getDrawable(R.styleable.CalendarView_calendar_week_drawable);
            if (drawable != null)
                weekDrawable = drawable;
            ColorStateList colorStateList = a.getColorStateList(R.styleable.CalendarView_calendar_week_color);
            if (colorStateList != null)
                weekColor = colorStateList;
            drawable = a.getDrawable(R.styleable.CalendarView_calendar_text_select_drawable);
            if (drawable != null)
                textSelectDrawable = drawable;
            drawable = a.getDrawable(R.styleable.CalendarView_calendar_text_drawable);
            if (drawable != null)
                textDrawable = drawable;
            colorStateList = a.getColorStateList(R.styleable.CalendarView_calendar_text_select_color);
            if (colorStateList != null)
                textSelectColor = colorStateList;
            colorStateList = a.getColorStateList(R.styleable.CalendarView_calendar_text_color);
            if (colorStateList != null)
                textColor = colorStateList;
            colorStateList = a.getColorStateList(R.styleable.CalendarView_calendar_othe_text_color);
            if (colorStateList != null)
                otheTextColor = colorStateList;
            weekSize = a.getDimensionPixelSize(R.styleable.CalendarView_calendar_week_size, weekSize);
            daySize = a.getDimensionPixelSize(R.styleable.CalendarView_calendar_day_size, daySize);
            weekTypeface = Typeface.defaultFromStyle(a.getInt(R.styleable.CalendarView_calendar_week_typeface, 1));
            dayTypeface = Typeface.defaultFromStyle(a.getInt(R.styleable.CalendarView_calendar_day_typeface, 0));
            daySelectTypeface = Typeface.defaultFromStyle(a.getInt(R.styleable.CalendarView_calendar_day_select_typeface, 1));
            a.recycle();
        }
        if (lineDrawable != null) {
            Decoration decoration = new Decoration(getContext(), Decoration.Orientation.GRID);
            decoration.setLine(lineWidth);
            decoration.setDrawable(lineDrawable);
            addItemDecoration(decoration);
        } else
            lineWidth = 0;
        myYear = calendar.get(Calendar.YEAR);
        myMonth = calendar.get(Calendar.MONTH) + 1;
        day = String.valueOf(calendar.get(Calendar.DATE));
        GridLayoutManager manager = new GridLayoutManager(getContext(), 7);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        setLayoutManager(manager);
        adapter = new RecyclerViewAdapter<Object>(this) {
            @Override
            public com.base.adapter.tag.ViewHolder<Object> getViewHolder(int itemType) {
                return new VH(itemType);
            }

            @Override
            public int getItemViewType(int position) {
                return position < 7 ? 0 : 1;
            }

            @Override
            public int getView(int itemType) {
                return R.layout.calendar_day_item;
            }
        };
        week = getResources().getStringArray(R.array.week);
        setMonthAndYear(myYear, myMonth);
    }

    public void setItem(int width, int height) {
        this.width = width;
        this.height = height;
        requestLayout();
    }

    private class VH extends com.base.adapter.tag.ViewHolder<Object> {
        private int type;
        private TextView textView;

        private VH(int type) {
            this.type = type;
        }

        @Override
        public void setContext(Object s) {
            if (s instanceof String) {
                textView.setText((String) s);
                textView.setTextSize(weekSize);
                if (weekTypeface != null)
                    textView.setTypeface(weekTypeface);
                if (weekColor != null)
                    textView.setTextColor(weekColor);
                if (weekDrawable != null)
                    if (Build.VERSION.SDK_INT > 15)
                        textView.setBackground(weekDrawable);
                    else textView.setBackgroundDrawable(weekDrawable);
            } else {
                DayEntity entity = (DayEntity) s;
                textView.setText(entity.text);
                textView.setTextSize(daySize);
                if (textDrawable != null)
                    if (Build.VERSION.SDK_INT > 15)
                        textView.setBackground(textDrawable);
                    else textView.setBackgroundDrawable(textDrawable);
                if (dayTypeface != null)
                    textView.setTypeface(dayTypeface);
                if (entity.month) {
                    if (myYear == year && myMonth == month && entity.text.equals(day)) {
                        if (daySelectTypeface != null)
                            textView.setTypeface(daySelectTypeface);
                        if (textSelectColor != null)
                            textView.setTextColor(textSelectColor);
                        if (textSelectDrawable != null)
                            if (Build.VERSION.SDK_INT > 15)
                                textView.setBackground(textSelectDrawable);
                            else textView.setBackgroundDrawable(textSelectDrawable);
                    } else {
                        if (textColor != null)
                            textView.setTextColor(textColor);
                    }

                } else {
                    if (otheTextColor != null)
                        textView.setTextColor(otheTextColor);
                }

            }
        }

        @Override
        public void bindView() {
            textView = findViewById(R.id.calendar_day_tv);
            textView.setWidth(width);
            textView.setHeight(height);
        }
    }


    public void setMonthAndYear(int year, int month) {
        this.year = year;
        this.month = month;
        adapter.clean();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        String week = Week.fullName(calendar.getTime());
        int first = TimeUtil.getLastMonth(week);
        int days = TimeUtil.monthDay(month, year);
        calendar.set(year, month, days);
        int last = TimeUtil.getNextMonth(week, days);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int l = (days + last + first) / 7;
        if (layoutParams != null) {
            layoutParams.width = width * 7 + 6 * lineWidth;
            layoutParams.height = height * (l + 1) + l * lineWidth;
            setLayoutParams(layoutParams);
        }
        for (String w : this.week)
            adapter.addItem(w);
        if (first > 0) {
            int fm = month - 1;
            int fy = year;
            if (fm < 1) {
                fm = 12;
                fy -= 1;
            }
            int fds = TimeUtil.monthDay(fm, fy);
            for (int i = first - 1; i > -1; i--) {
                DayEntity entity = new DayEntity();
                entity.text = String.valueOf(fds - i);
                entity.month = false;
                adapter.addItem(entity);
            }
        }
        for (int i = 1; i <= days; i++) {
            DayEntity entity = new DayEntity();
            entity.text = String.valueOf(i);
            entity.month = true;
            adapter.addItem(entity);
        }
        if (last > 0) {
            for (int i = 1; i <= last; i++) {
                DayEntity entity = new DayEntity();
                entity.text = String.valueOf(i);
                entity.month = false;
                adapter.addItem(entity);
            }
        }
    }

    private class DayEntity {
        private String text;
        private boolean month = false;
    }

}
