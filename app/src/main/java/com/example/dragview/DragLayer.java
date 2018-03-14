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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
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
public class DragLayer extends MyAbsoluteLayout implements DragSource, DropTarget {
    //    public class DragLayer extends MyAbsoluteLayout implements DragSource, DropTarget, View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {
    DragController mDragController;
    private int[] lastTouchDownXY = new int[2];
    OnClickListener mOnClickListener;
    OnLongClickListener mOnLongClickListener;
    OnTouchListener mOnTouchListener;

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

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mOnTouchListener = listener;
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


    public boolean allowDrag () {
        // In this simple demo, any view that you touch can be dragged.
        return true;
    }

    public void onDropCompleted (View target)
    {
        this.removeView(target);

    }

    private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
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
//        //new object, set default size
//        if (dd.getId() == R.id.drag_layer_templates) {
//            w = 600;
//            h = 600;
//        }

        int left = x - xOffset;
        int top = y - yOffset;
        DragLayer.LayoutParams lp;

        View vv = (View) source;
        // this is a new element, add it!
        if (vv.getId() == R.id.drag_layer_templates) {

            View newView = null;
            switch (v.getId()) {
                case R.id.image_template:
                    Bitmap mainImage = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888);
                    mainImage.eraseColor(Color.WHITE);
                    mainImage= addWhiteBorder(mainImage, 5);
                    Bitmap plusImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_black_48dp);
                    plusImage = Bitmap.createScaledBitmap(plusImage, 144, 144, true);
                    Bitmap dragImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_open_with_black_48dp);
                    dragImage = Bitmap.createScaledBitmap(dragImage, 48, 48, true);
                    Bitmap resizeImage = BitmapFactory.decodeResource(getResources(), R.drawable.icon_enlarge_black_48dp);
                    resizeImage = Bitmap.createScaledBitmap(resizeImage, 48, 48, true);
                    Bitmap mergedImages = createSingleImageFromMultipleImages(mainImage, plusImage, dragImage, resizeImage);
                    ImageView newImageView = new ImageView (getContext());
//                    newImageView.setBackgroundResource(R.drawable.photo1);

                    Drawable d = new BitmapDrawable(getResources(), mergedImages);
                    h = d.getIntrinsicHeight();
                    w = d.getIntrinsicWidth();
                    newImageView.setBackground(d);
                    newImageView.getAdjustViewBounds();
//                    newImageView.setImageBitmap(mergedImages);
                    newView = newImageView;

                    break;
                case R.id.text_template:
                    EditText newEditText = new EditText(getContext());
                    newEditText.setHint("Add text");
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
            lp = new DragLayer.LayoutParams (w, h, left, top);
            this.addView (newView, lp);
            newView.setOnClickListener(mOnClickListener);
            newView.setOnLongClickListener(mOnLongClickListener);
            newView.setOnTouchListener(mOnTouchListener);
            v = newView;
        }
        lp = new DragLayer.LayoutParams (w, h, left, top);
        this.updateViewLayout(v, lp);
    }
    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage, Bitmap thirdImage, Bitmap fourthImage){

        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0, 0, null);
        canvas.drawBitmap(secondImage, firstImage.getWidth() /2 - secondImage.getWidth()/2, firstImage.getHeight()/2 - secondImage.getHeight()/2, null);
        canvas.drawBitmap(thirdImage, firstImage.getWidth() - thirdImage.getWidth()-10, 10, null);
        canvas.drawBitmap(fourthImage, firstImage.getWidth() - fourthImage.getWidth()-10, firstImage.getHeight() - fourthImage.getHeight() -10, null);
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

}
