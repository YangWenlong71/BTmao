package net.owlsmart.cili.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import net.owlsmart.cili.R;

/**
 * Created by chenliu on 2016/8/17.<br/>
 * 描述：自动换行Layout
 * </br>
 */
public class AutoNewLineLayout extends ViewGroup {

    /**
     * 两个子控件之间的横向间隙
     */
    protected float horizontalSpace = 0;

    /**
     * 两个子控件之间的垂直间隙
     */
    protected float vertivalSpace = 0;

    public AutoNewLineLayout(Context context) {
        this(context, null, 0);
    }

    public AutoNewLineLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoNewLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AutoNewLineLayout);
        horizontalSpace = ta.getDimension(R.styleable.AutoNewLineLayout_horizontalSpace, 0);
        vertivalSpace = ta.getDimension(R.styleable.AutoNewLineLayout_vertivalSpace, 0);
        ta.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //AT_MOST
        int width = 0;
        int height = 0;
        int rawWidth = 0;//当前行总宽度
        int rawHeight = 0;// 当前行高

        int rowIndex = 0;//当前行位置
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if(child.getVisibility() == GONE){
                if(i == count - 1){
                    //最后一个child
                    height += rawHeight;
                    width = Math.max(width, rawWidth);
                }
                continue;
            }

            //这里调用measureChildWithMargins 而不是measureChild
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth()  + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if(rawWidth + childWidth  + (rowIndex > 0 ? horizontalSpace : 0)> widthSpecSize - getPaddingLeft() - getPaddingRight()){
                //换行
                width = Math.max(width, rawWidth);
                rawWidth = childWidth;
                height += rawHeight + vertivalSpace;
                rawHeight = childHeight;
                rowIndex = 0;
            } else {
                rawWidth += childWidth;
                if(rowIndex > 0){
                    rawWidth += horizontalSpace;
                }
                rawHeight = Math.max(rawHeight, childHeight);
            }

            if(i == count - 1){
                width = Math.max(rawWidth, width);
                height += rawHeight;
            }

            rowIndex++;
        }

        setMeasuredDimension(
                widthSpecMode == MeasureSpec.EXACTLY ? widthSpecSize : width + getPaddingLeft() + getPaddingRight(),
                heightSpecMode == MeasureSpec.EXACTLY ? heightSpecSize : height + getPaddingTop() + getPaddingBottom()
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int viewWidth = r - l;
        int leftOffset = getPaddingLeft();
        int topOffset = getPaddingTop();
        int rowMaxHeight = 0;
        int rowIndex = 0;//当前行位置
        View childView;
        for( int w = 0, count = getChildCount(); w < count; w++ ){
            childView = getChildAt(w);
            if(childView.getVisibility() == GONE) continue;

            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            // 如果加上当前子View的宽度后超过了ViewGroup的宽度，就换行
            int occupyWidth = lp.leftMargin + childView.getMeasuredWidth() + lp.rightMargin;
            if(leftOffset + occupyWidth + getPaddingRight() > viewWidth){
                leftOffset = getPaddingLeft();  // 回到最左边
                topOffset += rowMaxHeight + vertivalSpace;  // 换行
                rowMaxHeight = 0;

                rowIndex = 0;
            }

            int left = leftOffset + lp.leftMargin;
            int top = topOffset + lp.topMargin;
            int right = leftOffset+ lp.leftMargin + childView.getMeasuredWidth();
            int bottom =  topOffset + lp.topMargin + childView.getMeasuredHeight();
            childView.layout(left, top, right, bottom);

            // 横向偏移
            leftOffset += occupyWidth;
            // 试图更新本行最高View的高度
            int occupyHeight = lp.topMargin + childView.getMeasuredHeight() + lp.bottomMargin;
            if(rowIndex != count - 1){
                leftOffset += horizontalSpace;
            }
            rowMaxHeight = Math.max(rowMaxHeight, occupyHeight);
            rowIndex++;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }


}

