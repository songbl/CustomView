package com.sbl.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class CountDownView extends View {

    //圆环的颜色
    private int ringColor;
    //文本的颜色
    private int textColor;
    //圆环宽度
    private float ringWidth;
    //文本内容
    private String textContents;
    //文本大小
    private float textSize;
    //宽度
    private int mWidth;
    //高度
    private int mHeight;
    //圆环的矩形区域
    private RectF mRectF;
    private Paint mPaint;
    //倒计时时间
    private int mCountdownTime;
    private float mCurrentProgress;
    private CountDownFinishListener mListener;
    Paint textPaint ;
    Paint bgPaint ;
    Paint circlePaint;//最外层圆
    Paint testPaint;
    Paint testPaint2;
    // 终止时间，单位毫秒
    private  long mEndTime;
    // 倒计时持续时间，单位毫秒
    private  long mTotalLastTime;



    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.countdown);
        textColor = a.getColor(R.styleable.countdown_textColor,context.getResources().getColor(R.color.colorAccent));
        textContents = a.getString(R.styleable.countdown_textContents);
        textSize = a.getDimension(R.styleable.countdown_textSize,32);
        ringColor = a.getColor(R.styleable.countdown_ringColor,context.getResources().getColor(R.color.colorPrimary));
        ringWidth = a.getFloat(R.styleable.countdown_ringWidth,dp2px(context,40));
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ringColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ringWidth);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(0xFFaaaaaa);
        circlePaint.setStyle(Paint.Style.STROKE);//只绘制图形轮廓（描边）
        circlePaint.setStrokeWidth(ringWidth);

        textPaint= new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);//字居中
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);

        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);//只绘制图形内容;Paint.Style.FILL_AND_STROKE 既绘制轮廓也绘制内容
        bgPaint.setAntiAlias(true);
        bgPaint.setStrokeWidth(ringWidth);
        bgPaint.setColor(getResources().getColor(R.color.bgPath));

        testPaint = new Paint();
        testPaint.setStyle(Paint.Style.STROKE);
        testPaint.setAntiAlias(true);
        testPaint. setStrokeWidth(dp2px(getContext(),1));
        testPaint.setColor(getResources().getColor(R.color.test));

        testPaint2 = new Paint();
        testPaint2.setStyle(Paint.Style.FILL);
        testPaint2.setAntiAlias(true);
        testPaint2. setStrokeWidth(dp2px(getContext(),1));
        testPaint2.setColor(getResources().getColor(R.color.test2));
    }

    public void setCountdownTime(int mCountdownTime) {
        this.mCountdownTime = mCountdownTime;
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        /**
         * 为了避免画圆边界地方有重叠的。切
         */
        mRectF = new RectF( 2*ringWidth ,  2*ringWidth ,
                mWidth - 2*ringWidth , mHeight - 2*ringWidth );

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final long lastTime = mEndTime - System.currentTimeMillis();
        mPaint.setShader(new SweepGradient(mRectF.centerX(),
                mRectF.centerY(), 0xFFF2A159, 0xFFFE7761));
        if (lastTime>0) {
            //进度圆->描边的宽度会占矩形内一半
            canvas.drawArc(mRectF, -90, mCurrentProgress - 360, false, mPaint);
            canvas.drawRect(mRectF,testPaint);
            //背景内层圆圈；可能把进度圆挡住（互相都有可能）
            canvas.drawCircle(mRectF.centerX(),mRectF.centerY(),(mRectF.bottom-mRectF.top)/2-ringWidth+bgPaint.getStrokeWidth()/2,bgPaint);
            //最外层圆
          canvas.drawCircle(mRectF.centerX(),mRectF.centerY(),(mRectF.bottom-mRectF.top)/2+ringWidth,circlePaint);
            //绘制文本
            String text = mCountdownTime - (int) (mCurrentProgress / 360f * mCountdownTime) + "";
            //文字居中显示
            Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
            /**
             * top = top线的y坐标 - baseline线的y坐标；//负数
               bottom = bottom线的y坐标 - baseline线的y坐标；//正数
             */
            int baseline = (int) ((mRectF.bottom + mRectF.top - fontMetrics.bottom - fontMetrics.top) / 2);
            canvas.drawText(text, mRectF.centerX(), baseline, textPaint);//字是在xy的左下角开始
            mCurrentProgress = (lastTime * 1.0f / mTotalLastTime) * 360f;
            invalidate();
        } else {
            mListener.countDownFinished();
        }
    }



    public void startCountDown(long lastTime){
        this.mTotalLastTime = lastTime;
        this.mEndTime = System.currentTimeMillis() + lastTime;
    }

//    /**
//     * 开始倒计时
//     */
//    public void startCountDown() {
//        setClickable(false);
//        ValueAnimator valueAnimator = getValA(mCountdownTime * 1000);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float i = Float.valueOf(String.valueOf(animation.getAnimatedValue()));
//                Log.e("songbl","===="+mCurrentProgress);
//                mCurrentProgress = (int) (360 * (i / 100f));
//                invalidate();
//            }
//        });
//        valueAnimator.start();
//        valueAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                //倒计时结束回调
//                if (mListener != null) {
//                    mListener.countDownFinished();
//                }
//                setClickable(true);
//            }
//
//        });
//    }

//    private ValueAnimator getValA(long countdownTime) {
//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 100);
//        valueAnimator.setDuration(countdownTime);
//        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.setRepeatCount(0);
//        return valueAnimator;
//    }
    public void setAddCountDownListener(CountDownFinishListener mListener) {
        this.mListener = mListener;
    }
    //结束回调
    public interface CountDownFinishListener {
        void countDownFinished();
    }

    /**
     * dp转换成px
     */
    private float dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
