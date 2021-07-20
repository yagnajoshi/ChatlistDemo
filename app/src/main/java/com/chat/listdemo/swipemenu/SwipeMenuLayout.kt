package com.chat.listdemo.swipemenu

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Scroller
import com.chat.listdemo.R
import java.util.*

/*
*On implementation of this SwipeMenuLayout Class, It provides custom views of swipe menu to List item or any views.
* On Swipe getsture left or right it will reveal hidden view of utility buttons.
* */
class SwipeMenuLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private val mMatchParentChildren = ArrayList<View>(1)
    private var mLeftViewResID = 0
    private var mRightViewResID = 0
    private var mContentViewResID = 0
    private var mLeftView: View? = null
    private var mRightView: View? = null
    private var mContentView: View? = null
    private var mContentViewLp: MarginLayoutParams? = null
    private var isSwipeing = false
    private var mLastP: PointF? = null
    private var mFirstP: PointF? = null
    var fraction = 0.3f
    var isCanLeftSwipe = true
    var isCanRightSwipe = true
    private var mScaledTouchSlop = 0
    private var mScroller: Scroller? = null
    private val distanceX = 0f
    private var finalyDistanceX = 0f

    /**
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

        val viewConfiguration = ViewConfiguration.get(context)
        mScaledTouchSlop = viewConfiguration.scaledTouchSlop
        mScroller = Scroller(context)

        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.EasySwipeMenuLayout,
            defStyleAttr,
            0
        )
        try {
            val indexCount = typedArray.indexCount
            for (i in 0 until indexCount) {
                val attr = typedArray.getIndex(i)
                if (attr == R.styleable.EasySwipeMenuLayout_leftMenuView) {
                    mLeftViewResID =
                        typedArray.getResourceId(R.styleable.EasySwipeMenuLayout_leftMenuView, -1)
                } else if (attr == R.styleable.EasySwipeMenuLayout_rightMenuView) {
                    mRightViewResID =
                        typedArray.getResourceId(R.styleable.EasySwipeMenuLayout_rightMenuView, -1)
                } else if (attr == R.styleable.EasySwipeMenuLayout_contentView) {
                    mContentViewResID =
                        typedArray.getResourceId(R.styleable.EasySwipeMenuLayout_contentView, -1)
                } else if (attr == R.styleable.EasySwipeMenuLayout_canLeftSwipe) {
                    isCanLeftSwipe =
                        typedArray.getBoolean(R.styleable.EasySwipeMenuLayout_canLeftSwipe, true)
                } else if (attr == R.styleable.EasySwipeMenuLayout_canRightSwipe) {
                    isCanRightSwipe =
                        typedArray.getBoolean(R.styleable.EasySwipeMenuLayout_canRightSwipe, true)
                } else if (attr == R.styleable.EasySwipeMenuLayout_fraction) {
                    fraction = typedArray.getFloat(R.styleable.EasySwipeMenuLayout_fraction, 0.5f)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //childView
        isClickable = true
        var count = childCount
        //frameLayout
        val measureMatchParentChildren =
            MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY ||
                    MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY
        mMatchParentChildren.clear()
        var maxHeight = 0
        var maxWidth = 0
        var childState = 0
        //childViews
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                val lp = child.layoutParams as MarginLayoutParams
                maxWidth = Math.max(
                    maxWidth,
                    child.measuredWidth + lp.leftMargin + lp.rightMargin
                )
                maxHeight = Math.max(
                    maxHeight,
                    child.measuredHeight + lp.topMargin + lp.bottomMargin
                )
                childState = combineMeasuredStates(childState, child.measuredState)
                if (measureMatchParentChildren) {
                    if (lp.width == LayoutParams.MATCH_PARENT ||
                        lp.height == LayoutParams.MATCH_PARENT
                    ) {
                        mMatchParentChildren.add(child)
                    }
                }
            }
        }
        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, suggestedMinimumHeight)
        maxWidth = Math.max(maxWidth, suggestedMinimumWidth)
        setMeasuredDimension(
            resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
            resolveSizeAndState(
                maxHeight, heightMeasureSpec,
                childState shl MEASURED_HEIGHT_STATE_SHIFT
            )
        )
        count = mMatchParentChildren.size
        if (count > 1) {
            for (i in 0 until count) {
                val child = mMatchParentChildren[i]
                val lp = child.layoutParams as MarginLayoutParams
                val childWidthMeasureSpec: Int
                childWidthMeasureSpec = if (lp.width == LayoutParams.MATCH_PARENT) {
                    val width = Math.max(
                        0, measuredWidth
                                - lp.leftMargin - lp.rightMargin
                    )
                    MeasureSpec.makeMeasureSpec(
                        width, MeasureSpec.EXACTLY
                    )
                } else {
                    getChildMeasureSpec(
                        widthMeasureSpec,
                        lp.leftMargin + lp.rightMargin,
                        lp.width
                    )
                }
                val childHeightMeasureSpec: Int
                childHeightMeasureSpec = if (lp.height == FrameLayout.LayoutParams.MATCH_PARENT) {
                    val height = Math.max(
                        0, measuredHeight
                                - lp.topMargin - lp.bottomMargin
                    )
                    MeasureSpec.makeMeasureSpec(
                        height, MeasureSpec.EXACTLY
                    )
                } else {
                    getChildMeasureSpec(
                        heightMeasureSpec,
                        lp.topMargin + lp.bottomMargin,
                        lp.height
                    )
                }
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        val left = 0 + paddingLeft
        val right = 0 + paddingLeft
        val top = 0 + paddingTop
        val bottom = 0 + paddingTop
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (mLeftView == null && child.id == mLeftViewResID) {
                mLeftView = child
                mLeftView!!.isClickable = true
            } else if (mRightView == null && child.id == mRightViewResID) {
                mRightView = child
                mRightView!!.isClickable = true
            } else if (mContentView == null && child.id == mContentViewResID) {
                mContentView = child
                mContentView!!.isClickable = true
            }
        }
        var cRight = 0
        if (mContentView != null) {
            mContentViewLp = mContentView!!.layoutParams as MarginLayoutParams
            val cTop = top + mContentViewLp!!.topMargin
            val cLeft = left + mContentViewLp!!.leftMargin
            cRight = left + mContentViewLp!!.leftMargin + mContentView!!.measuredWidth
            val cBottom = cTop + mContentView!!.measuredHeight
            mContentView!!.layout(cLeft, cTop, cRight, cBottom)
        }
        if (mLeftView != null) {
            val leftViewLp = mLeftView!!.layoutParams as MarginLayoutParams
            val lTop = top + leftViewLp.topMargin
            val lLeft =
                0 - mLeftView!!.measuredWidth + leftViewLp.leftMargin + leftViewLp.rightMargin
            val lRight = 0 - leftViewLp.rightMargin
            val lBottom = lTop + mLeftView!!.measuredHeight
            mLeftView!!.layout(lLeft, lTop, lRight, lBottom)
        }
        if (mRightView != null) {
            val rightViewLp = mRightView!!.layoutParams as MarginLayoutParams
            val lTop = top + rightViewLp.topMargin
            val lLeft = mContentView!!.right + mContentViewLp!!.rightMargin + rightViewLp.leftMargin
            val lRight = lLeft + mRightView!!.measuredWidth
            val lBottom = lTop + mRightView!!.measuredHeight
            mRightView!!.layout(lLeft, lTop, lRight, lBottom)
        }
    }

    var result: State? = null
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {

                isSwipeing = false
                if (mLastP == null) {
                    mLastP = PointF()
                }
                mLastP!![ev.rawX] = ev.rawY
                if (mFirstP == null) {
                    mFirstP = PointF()
                }
                mFirstP!![ev.rawX] = ev.rawY
                if (viewCache != null) {
                    if (viewCache !== this) {
                        viewCache!!.handlerSwipeMenu(State.CLOSE)
                    }
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_MOVE -> {

                //   System.out.println(">>>>dispatchTouchEvent() ACTION_MOVE getScrollX:" + getScrollX());
                val distanceX = mLastP!!.x - ev.rawX
                val distanceY = mLastP!!.y - ev.rawY
                if (Math.abs(distanceY) > mScaledTouchSlop && Math.abs(distanceY) > Math.abs(
                        distanceX
                    )
                ) {
                    //break
                }
                //                if (Math.abs(distanceX) <= mScaledTouchSlop){
//                    break;
//                }
                // Log.i(TAG, ">>>>>distanceX:" + distanceX);
                scrollBy(distanceX.toInt(), 0) //滑动使用scrollBy
                if (scrollX < 0) {
                    if (!isCanRightSwipe || mLeftView == null) {
                        scrollTo(0, 0)
                    } else {
                        if (scrollX < mLeftView!!.left) {
                            scrollTo(mLeftView!!.left, 0)
                        }
                    }
                } else if (scrollX > 0) {
                    if (!isCanLeftSwipe || mRightView == null) {
                        scrollTo(0, 0)
                    } else {
                        if (scrollX > mRightView!!.right - mContentView!!.right - mContentViewLp!!.rightMargin) {
                            scrollTo(
                                mRightView!!.right - mContentView!!.right - mContentViewLp!!.rightMargin,
                                0
                            )
                        }
                    }
                }
                if (Math.abs(distanceX) > mScaledTouchSlop //                        || Math.abs(getScrollX()) > mScaledTouchSlop
                ) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                mLastP!![ev.rawX] = ev.rawY
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                //     System.out.println(">>>>dispatchTouchEvent() ACTION_CANCEL OR ACTION_UP");
                finalyDistanceX = mFirstP!!.x - ev.rawX
                if (Math.abs(finalyDistanceX) > mScaledTouchSlop) {
                    //  System.out.println(">>>>P");
                    isSwipeing = true
                }
                result = isShouldOpen(scrollX)
                handlerSwipeMenu(result)
            }
            else -> {
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        //  Log.d(TAG, "<<<<dispatchTouchEvent() called with: " + "ev = [" + event + "]");
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {

                if (Math.abs(finalyDistanceX) > mScaledTouchSlop) {
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                if (isSwipeing) {
                    isSwipeing = false
                    finalyDistanceX = 0f
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    /**
     *
     * @param result
     */
    private fun handlerSwipeMenu(result: State?) {
        if (result == State.LEFT_OPEN) {
            mScroller!!.startScroll(scrollX, 0, mLeftView!!.left - scrollX, 0)
            viewCache = this
            stateCache = result
        } else if (result == State.RIGHT_OPEN) {
            viewCache = this
            mScroller!!.startScroll(
                scrollX,
                0,
                mRightView!!.right - mContentView!!.right - mContentViewLp!!.rightMargin - scrollX,
                0
            )
            stateCache = result
        } else {
            mScroller!!.startScroll(scrollX, 0, -scrollX, 0)
            viewCache = null
            stateCache = null
        }
        invalidate()
    }

    override fun computeScroll() {
        //判断Scroller是否执行完毕：
        if (mScroller!!.computeScrollOffset()) {
            scrollTo(mScroller!!.currX, mScroller!!.currY)
            //通知View重绘-invalidate()->onDraw()->computeScroll()
            invalidate()
        }
    }

    /**
     * scrollx
     *
     * @param
     * @param scrollX
     * @return
     */
    private fun isShouldOpen(scrollX: Int): State? {
        if (mScaledTouchSlop >= Math.abs(finalyDistanceX)) {
            return stateCache
        }
        Log.i(TAG, ">>>finalyDistanceX:$finalyDistanceX")
        if (finalyDistanceX < 0) {
            //➡滑动
            //1、展开左边按钮
            //获得leftView的测量长度
            if (getScrollX() < 0 && mLeftView != null) {
                if (Math.abs(mLeftView!!.width * fraction) < Math.abs(getScrollX())) {
                    return State.LEFT_OPEN
                }
            }
            //2、关闭右边按钮
            if (getScrollX() > 0 && mRightView != null) {
                return State.CLOSE
            }
        } else if (finalyDistanceX > 0) {

            if (getScrollX() > 0 && mRightView != null) {
                if (Math.abs(mRightView!!.width * fraction) < Math.abs(getScrollX())) {
                    return State.RIGHT_OPEN
                }
            }
            if (getScrollX() < 0 && mLeftView != null) {
                return State.CLOSE
            }
        }
        return State.CLOSE
    }

    override fun onDetachedFromWindow() {
        if (this === viewCache) {
            viewCache!!.handlerSwipeMenu(State.CLOSE)
        }
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (this === viewCache) {
            viewCache!!.handlerSwipeMenu(stateCache)
        }
        // Log.i(TAG, ">>>>>>>>onAttachedToWindow");
    }

    fun resetStatus() {
        if (viewCache != null) {
            if (stateCache != null && stateCache != State.CLOSE && mScroller != null) {
                mScroller!!.startScroll(viewCache!!.scrollX, 0, -viewCache!!.scrollX, 0)
                viewCache!!.invalidate()
                viewCache = null
                stateCache = null
            }
        }
    }

    private val isLeftToRight: Boolean
        private get() = if (distanceX < 0) {
            true
        } else {
            false
        }

    companion object {
        private const val TAG = "SwipeMenuLayout"
        var viewCache: SwipeMenuLayout? = null
            private set
        var stateCache: State? = null
            private set
    }

    init {
        init(context, attrs, defStyleAttr)
    }

    enum class State {
        LEFT_OPEN, RIGHT_OPEN, CLOSE
    }
}