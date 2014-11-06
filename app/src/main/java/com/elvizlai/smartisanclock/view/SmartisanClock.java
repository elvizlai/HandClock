package com.elvizlai.smartisanclock.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.elvizlai.smartisanclock.R;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Elvizlai on 14-11-6.
 */

//TODO adding shadows
public class SmartisanClock extends View {
    Bitmap mBmpDial;//表盘
    Bitmap mBmpCenter;//中心圆点
    Bitmap mBmpD12;//12
    Bitmap mBmpD3;//3
    Bitmap mBmpHour;//时针
    Bitmap mBmpMinute;//分针
    Bitmap mBmpSecond;//秒针

    BitmapDrawable bmdDial;
    BitmapDrawable bmdCenter;
    BitmapDrawable bmdD12;
    BitmapDrawable bmdD3;
    BitmapDrawable bmdHour;
    BitmapDrawable bmdMinute;
    BitmapDrawable bmdSecond;

    Paint mPaint;

    Handler tickHandler;

    int mWidth;
    int mHeigh;
    int mTempWidth;
    int mTempHeigh;
    int centerX;
    int centerY;

    int availableWidth;
    int availableHeight;

    private String sTimeZoneString;

    public SmartisanClock(Context context, AttributeSet attr) {
        this(context, "GMT+8:00");
    }

    public SmartisanClock(Context context, String sTime_Zone) {
        super(context);
        sTimeZoneString = sTime_Zone;

        mBmpDial = BitmapFactory.decodeResource(getResources(), R.drawable.blank_clock);
        bmdDial = new BitmapDrawable(getResources(), mBmpDial);

        mBmpCenter = BitmapFactory.decodeResource(getResources(), R.drawable.hand_center);
        bmdCenter = new BitmapDrawable(getResources(), mBmpCenter);

        mBmpD12 = BitmapFactory.decodeResource(getResources(), R.drawable.d12);
        bmdD12 = new BitmapDrawable(getResources(), mBmpD12);

        mBmpD3 = BitmapFactory.decodeResource(getResources(), R.drawable.d3);
        bmdD3 = new BitmapDrawable(getResources(), mBmpD3);

        mBmpHour = BitmapFactory.decodeResource(getResources(), R.drawable.hour_hand);
        bmdHour = new BitmapDrawable(getResources(), mBmpHour);

        mBmpMinute = BitmapFactory.decodeResource(getResources(), R.drawable.minute_hand);
        bmdMinute = new BitmapDrawable(getResources(), mBmpMinute);

        mBmpSecond = BitmapFactory.decodeResource(getResources(), R.drawable.sec_hand);
        bmdSecond = new BitmapDrawable(getResources(), mBmpSecond);

        mWidth = mBmpDial.getWidth();
        mHeigh = mBmpDial.getHeight();

        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        availableWidth = dm.widthPixels;
        availableHeight = dm.heightPixels;

        centerX = availableWidth / 2;
        centerY = availableHeight / 3;

        //Log.d("w, h", w + " " + h);

        mPaint = new Paint();
        run();
    }

    public void run() {
        tickHandler = new Handler();
        tickHandler.post(tickRunnable);
    }

    private Runnable tickRunnable = new Runnable() {
        public void run() {
            postInvalidate();//刷新
            tickHandler.postDelayed(tickRunnable, 1000);
        }
    };

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(sTimeZoneString));
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        //Log.d("time", hour + " " + minute + " " + second);

        float secondRotate = second * 6.0f;
        float minuteRotate = minute * 6.0f + second * 0.1f;
        float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f;

        //Log.d("hms", hourRotate + " " + minuteRotate + " " + secondRotate);

        boolean scaled = false;

        if (availableWidth < mWidth || availableHeight < mHeigh) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) mWidth, (float) availableHeight / (float) mHeigh);
            canvas.save();
            canvas.scale(scale, scale, centerX, centerY);
        }

//由于图片的原因，放大后有锯齿，所以注释掉了
//        if (availableWidth > mWidth || availableHeight > mHeigh) {
//            scaled = true;
//            float scale = Math.min((float) availableWidth / (float) mWidth, (float) availableHeight / (float) mHeigh);
//            canvas.save();
//            canvas.scale(scale, scale, centerX, centerY);
//        }

        //表盘
        bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeigh / 2), centerX + (mWidth / 2), centerY + (mHeigh / 2));
        bmdDial.draw(canvas);

        //D12
        mTempWidth = bmdD12.getIntrinsicWidth();
        mTempHeigh = bmdD12.getIntrinsicHeight();
        canvas.drawBitmap(mBmpD12, centerX - (mTempWidth / 2), centerY - bmdDial.getIntrinsicHeight() / 2 * 6 / 9, mPaint);

        //D3
        mTempWidth = bmdD3.getIntrinsicWidth();
        mTempHeigh = bmdD3.getIntrinsicHeight();
        canvas.drawBitmap(mBmpD3, centerX + bmdDial.getIntrinsicWidth() / 2 * 5 / 9, centerY - (mTempHeigh / 2), mPaint);


        //Hour
        mTempWidth = bmdHour.getIntrinsicWidth();
        mTempHeigh = bmdHour.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(hourRotate, centerX, centerY);
        bmdHour.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeigh * 2 / 3), centerX + (mTempWidth / 2), centerY);
        bmdHour.draw(canvas);
        canvas.restore();

        //Minute
        mTempWidth = bmdMinute.getIntrinsicWidth();
        mTempHeigh = bmdMinute.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(minuteRotate, centerX, centerY);
        bmdMinute.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeigh * 6 / 8), centerX + (mTempWidth / 2), centerY);
        bmdMinute.draw(canvas);
        canvas.restore();

        //Center
        mTempWidth = bmdCenter.getIntrinsicWidth();
        mTempHeigh = bmdCenter.getIntrinsicHeight();
        bmdCenter.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeigh / 2), centerX + (mTempWidth / 2), centerY + (mTempHeigh / 2));
        bmdCenter.draw(canvas);

        //Second
        mTempWidth = bmdSecond.getIntrinsicWidth();
        mTempHeigh = bmdSecond.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(secondRotate, centerX, centerY);
        bmdSecond.setBounds(centerX - (mTempWidth / 2), centerY - (mTempHeigh * 7 / 9), centerX + (mTempWidth / 2), centerY + (mTempHeigh / 5));
        bmdSecond.draw(canvas);
        canvas.restore();

        if (scaled) {
            canvas.restore();
        }
    }
}
