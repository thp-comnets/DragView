/*
 * This is a modified version of a class from the Android Open Source Project. 
 * The original copyright and license information follows.
 * 
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dragview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.security.PrivateKey;

/**
 * A ViewGroup that coordinates dragging across its dscendants.
 *
 * <p> This class used DragLayer in the Android Launcher activity as a model.
 * It is a bit different in several respects:
 * (1) It extends MyAbsoluteLayout rather than FrameLayout; (2) it implements DragSource and DropTarget methods
 * that were done in a separate Workspace class in the Launcher.
 */
public class DragLayer extends MyAbsoluteLayout implements DragSource, DropTarget, View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {
    DragController mDragController;
    private int[] lastTouchDownXY = new int[2];
//    private int[] initalImageSizeXY = new int[2];
    private int[] initalImageLocationXY = new int[2];
    private  int iRelX, iRelY;
    private int offsetX, offsetY;
    /**
     * Used to create a new DragLayer from XML.
     *
     * @param context The application's context.
     * @param attrs The attribtues set containing the Workspace's customization values.
     */
    public DragLayer (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDragController(DragController controller) {
        mDragController = controller;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mDragController.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragController.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDragController.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        return mDragController.dispatchUnhandledMove(focused, direction);
    }

/**
 */
// DragSource interface methods

    /**
     * This method is called to determine if the DragSource has something to drag.
     *
     * @return True if there is something to drag
     */

    public boolean allowDrag () {
        // In this simple demo, any view that you touch can be dragged.
        return true;
    }

/**
 * setDragController
 *
 */


    public void onDropCompleted (View target)
    {
        this.removeView(target);
//    Log.d("thp", "DragLayer2.onDropCompleted thp"+ target.getId ());
//    toast ("DragLayer2.onDropCompleted: " + target.getId () + " Check that the view moved.");
    }

/**
 */
// DropTarget interface implementation

    /**
     * Handle an object being dropped on the DropTarget.
     * This is the where a dragged view gets repositioned at the end of a drag.
     *
     * @param source DragSource where the drag started
     * @param x X coordinate of the drop location
     * @param y Y coordinate of the drop location
     * @param xOffset Horizontal offset with the object being dragged where the original
     *          touch happened
     * @param yOffset Vertical offset with the object being dragged where the original
     *          touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     *
     */
    public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset,
                       DragView dragView, Object dragInfo)
    {
        View v = (View) dragInfo;
        toast ("DragLayer2.onDrop accepts view: " + v.getId ()
                + "x, y, xO, yO :" + new Integer (x) + ", " + new Integer (y) + ", "
                + new Integer (xOffset) + ", " + new Integer (yOffset));

        DragLayer dd = (DragLayer) source;

        int w = dragView.getWidth();
        int h = dragView.getHeight();
        //new object, set default size
        if (dd.getId() == R.id.drag_layer_templates) {
            w = 600;
            h = 600;
        }

        int left = x - xOffset;
        int top = y - yOffset;
        DragLayer.LayoutParams lp = new DragLayer.LayoutParams (w, h, left, top);

        View vv = (View) source;
        // this is a new element, add it!
        if (vv.getId() == R.id.drag_layer_templates) {

        View newView = null;
            switch (v.getId()) {
                case R.id.image_template:
                    Bitmap mainImage = BitmapFactory.decodeResource(getResources(), R.drawable.photo1);
                    Bitmap dragImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_open_with_white_48dp);
                    Bitmap resizeImage = BitmapFactory.decodeResource(getResources(), R.drawable.icon_enlarge_48dp);
                    Bitmap mergedImages = createSingleImageFromMultipleImages(mainImage, dragImage, resizeImage);
                    ImageView newImageView = new ImageView (getContext());
                    newImageView.setImageBitmap(mergedImages);
                    newView = newImageView;

                    break;
                case R.id.text_template:
                    EditText newEditText = new EditText(getContext());
                    newEditText.setHint("test");
                    newEditText.setBackground(getResources().getDrawable(R.drawable.box));
                    newView = newEditText;
                    break;
                case R.id.box_template:
                    View newBoxView = new View (getContext());
                    newBoxView.setBackground(getResources().getDrawable(R.drawable.box));
                    newView = newBoxView;
                    break;
                default:
                    Toast.makeText (getContext(), "Something went wrong with adding the object", Toast.LENGTH_LONG).show ();
                    return;
            }
            if (newView == null) {
                Toast.makeText (getContext(), "Something went wrong with adding the object", Toast.LENGTH_LONG).show ();
                return;
            }

            this.addView (newView, lp);
            newView.setOnClickListener(this);
            newView.setOnLongClickListener(this);
            newView.setOnTouchListener(this);
            v = newView;
        }

        this.updateViewLayout(v, lp);
    }
    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage, Bitmap thirdImage){

        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, firstImage.getWidth() - secondImage.getWidth()-20, 20, null);
        canvas.drawBitmap(thirdImage, firstImage.getWidth() - secondImage.getWidth()-20, firstImage.getHeight() - thirdImage.getHeight() -20, null);
        return result;
    }
    public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset,
                            DragView dragView, Object dragInfo)
    {
    }

    public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset,
                           DragView dragView, Object dragInfo)
    {
    }

    public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset,
                           DragView dragView, Object dragInfo)
    {
//    Log.d("thp", "DragLayer2.onDragExit thp");
    }

    /**
     * Check if a drop action can occur at, or near, the requested location.
     * This may be called repeatedly during a drag, so any calls should return
     * quickly.
     *
     * @param source DragSource where the drag started
     * @param x X coordinate of the drop location
     * @param y Y coordinate of the drop location
     * @param xOffset Horizontal offset with the object being dragged where the
     *            original touch happened
     * @param yOffset Vertical offset with the object being dragged where the
     *            original touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     * @return True if the drop will be accepted, false otherwise.
     */
    public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset,
                              DragView dragView, Object dragInfo)
    {
        return true;
    }

    /**
     * Estimate the surface area where this object would land if dropped at the
     * given location.
     *
     * @param source DragSource where the drag started
     * @param x X coordinate of the drop location
     * @param y Y coordinate of the drop location
     * @param xOffset Horizontal offset with the object being dragged where the
     *            original touch happened
     * @param yOffset Vertical offset with the object being dragged where the
     *            original touch happened
     * @param dragView The DragView that's being dragged around on screen.
     * @param dragInfo Data associated with the object being dragged
     * @param recycle {@link Rect} object to be possibly recycled.
     * @return Estimated area that would be occupied if object was dropped at
     *         the given location. Should return null if no estimate is found,
     *         or if this target doesn't provide estimations.
     */
    public Rect estimateDropLocation(DragSource source, int x, int y, int xOffset, int yOffset,
                                     DragView dragView, Object dragInfo, Rect recycle)
    {
        return null;
    }

    public void toast (String msg)
    {
        if (!DragActivityV2.Debugging) return;
        Toast.makeText (getContext (), msg, Toast.LENGTH_SHORT).show ();
    }

    //this is the same as in DragActivity. We need it to handle the touch events
    public boolean startDrag (View v) {
        Object dragInfo = v;
        mDragController.startDrag (v, this, dragInfo, DragController.DRAG_ACTION_MOVE);
        return true;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
//
//    private int getStatusBarHeight() {
//        int result = 0;
//        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = getContext().getResources().getDimensionPixelSize(resourceId);
//        }
//        return result;
//    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        int x = (int) motionEvent.getRawX();
        int y = (int) motionEvent.getRawY();
//        final int screenX = clamp((int)motionEvent.getRawX(), 0, mDisplayMetrics.widthPixels);
//        final int screenY = clamp((int)motionEvent.getRawY(), 0, mDisplayMetrics.heightPixels);

        // save the X,Y coordinates
        if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
            lastTouchDownXY[0] = (int) motionEvent.getX();
            lastTouchDownXY[1] = (int) motionEvent.getY();
            iRelX = (int) motionEvent.getRawX();
            iRelY = (int) motionEvent.getRawY();
//            initalImageSizeXY[0] = pxToDp(view.getWidth());
//            initalImageSizeXY[1] = pxToDp(view.getHeight());
            view.getLocationOnScreen(initalImageLocationXY);


//             Log.d("thp", "ontouch" + initalImageLocationXY[0] + " " +initalImageLocationXY[1] +" " + getStatusBarHeight());
        }

        int width= view.getLayoutParams().width;
        int height = view.getLayoutParams().height;



        if (lastTouchDownXY[0] > view.getWidth() - 200 && lastTouchDownXY[1] > view.getHeight() - 200) {
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

//                offsetX =  (initalImageLocationXY [0] - (int) view.getX());     // subtract out this View's relative location within its parent View...
//                offsetY =  (initalImageLocationXY[1] - (int) view.getY());
//                x -= offsetX;                                  // remove parent View's screen offset (calc'd above)
//                y -= offsetY;
//                x -= lastTouchDownXY[0];                                     // remove stored touch offset
//                y -= lastTouchDownXY[1];

//                view.setX(iRelX);
//                view.setY(iRelY);

//                WindowManager.LayoutParams lp = mLayoutParams;
//                int w = 200;//dpToPx(x) - initalImageLocationXY[0];
//                int h =  200;//dpToPx(y) - initalImageLocationXY[1];

//                DragLayer.LayoutParams lp = new DragLayer.LayoutParams(w, h, 0, 0);
//                lp.x = x - lastTouchDownXY[0] - initalImageLocationXY[0];
//                lp.y = y - lastTouchDownXY[1] - initalImageLocationXY[1];
//                this.updateViewLayout(view, lp);


//                if ((initalImageSizeXY[0] - x) > 0 ) {
//                    view.getLayoutParams().width = initalImageSizeXY[0] - initalImageLocationXY[0] + initalImageSizeXY[0] - x ;
                int w = (int) motionEvent.getX();//dpToPx(x) - initalImageLocationXY[0];
                int h =  (int) motionEvent.getY();
                int left = (int)   view.getX();
                int top = (int) view.getY();
                DragLayer.LayoutParams lp = new DragLayer.LayoutParams (w, h, left, top);
                this.updateViewLayout(view, lp);
//                    view.getLayoutParams().width =  x ;
//                    view.getLayoutParams().height = y;
////
//                    view.requestLayout();
//                }
                Log.d("thp", "ontouch " + y + " " +   view.getX());
            }


//            return false;
        }
        // If we are configured to start only on a long click, we are not going to handle any events here.
        boolean mLongClickStartsDrag = true; //thp: FIXME
        if (mLongClickStartsDrag) return false;
//
//
//        boolean handledHere = false;
//
//        final int action = motionEvent.getAction();
//
//        // In the situation where a long click is not needed to initiate a drag, simply start on the down event.
//        if (action == MotionEvent.ACTION_DOWN) {
//            handledHere = startDrag(view);
//        }
//
//        return handledHere;
        return false;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        float x = lastTouchDownXY[0];
        float y = lastTouchDownXY[1];

        if (x > (view.getWidth() - 150) && y < 150) {
            Log.d("thp", "onLongClick" + x + " " + y + " " + view.getWidth());
            return startDrag(view);
        }
//        if (x > view.getWidth() - 150 && y > (view.getHeight() - 150)) {
//
//
//
//            Log.d("thp", "onLongClick" + x + " " + y + " " + view.getWidth());
//            return false;
//        }
//        return startDrag (view);
        return false;
    }
    private static int clamp(int val, int min, int max) {
        if (val < min) {
            return min;
        } else if (val >= max) {
            return max - 1;
        } else {
            return val;
        }
    }
    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
