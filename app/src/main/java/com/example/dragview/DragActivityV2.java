package com.example.dragview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity presents a screen on which images can be added and moved around.
 * It also defines areas on the screen where the dragged views can be dropped. Feedback is
 * provided to the user as the objects are dragged over these drop zones.
 *
 * <p> Like the DragActivity in the previous version of the DragView example application, the
 * code here is derived from the Android Launcher code.
 * 
 */

public class DragActivityV2 extends AppCompatActivity
    implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener
{


private static final int ENABLE_S2_MENU_ID = Menu.FIRST;
private static final int DISABLE_S2_MENU_ID = Menu.FIRST + 1;
private static final int ADD_OBJECT_MENU_ID = Menu.FIRST + 2;
private static final int CHANGE_TOUCH_MODE_MENU_ID = Menu.FIRST + 3;

private DragController mDragController;   // Object that sends out drag-drop events while a view is being moved.
private DragLayer mDragLayer;             // The ViewGroup that supports drag-drop.

    private DragController mDragController2;
private DragLayer mDragLayer2;
private DropSpot mSpot2;                  // The DropSpot that can be turned on and off via the menu.
private boolean mLongClickStartsDrag = true;    // If true, it takes a long click to start the drag operation.
                                                // Otherwise, any touch event starts a drag.
private DeleteZone mDeleteZone;

public static final boolean Debugging = false;
private int[] lastTouchDownXY = new int[2];

protected void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    mDragController = new DragController(this);
    mDragController2 = new DragController(this);
    setContentView(R.layout.main);
    setupViews ();
}

public boolean onCreateOptionsMenu (Menu menu) 
{
    super.onCreateOptionsMenu(menu);
    
    menu.add(0, ENABLE_S2_MENU_ID, 0, "Enable Spot2").setShortcut('1', 'c');
    menu.add(0, DISABLE_S2_MENU_ID, 0, "Disable Spot2").setShortcut('2', 'c');
    menu.add(0, ADD_OBJECT_MENU_ID, 0, "Add View").setShortcut('9', 'z');
    menu.add (0, CHANGE_TOUCH_MODE_MENU_ID, 0, "Change Touch Mode");

    return true;
}

public void onClick(View v) 
{
//    Log.d("thp", "onclick in mainactivity");
//    mDragController.printTargets();
//    if (mLongClickStartsDrag) {
//       // Tell the user that it takes a long click to start dragging.
//       toast ("Press and hold to drag an image.");
//    }
}


public boolean onLongClick(View v) 
{


    float x = lastTouchDownXY[0];
    float y = lastTouchDownXY[1];
    Log.d("thp", "onLongClick" + x + " " + y + " " + v.getWidth());
    if (x > (v.getWidth() - 150) && y < 150) {
        Log.d("thp", "onLongClick" + x + " " + y + " " + v.getWidth());
        return startDrag(v);
    }

//    if (mLongClickStartsDrag) {
//
//        //trace ("onLongClick in view: " + v + " touchMode: " + v.isInTouchMode ());
//
//        // Make sure the drag was started by a long press as opposed to a long click.
//        // (Note: I got this from the Workspace object in the Android Launcher code.
//        //  I think it is here to ensure that the device is still in touch mode as we start the drag operation.)
//        if (!v.isInTouchMode()) {
//           toast ("isInTouchMode returned false. Try touching the view again.");
//           return false;
//        }
//        return startDrag (v);
//    }

    // If we get here, return false to indicate that we have not taken care of the event.
    return false;
}

    private List<View> getAllChildrenBFS(View v) {
        List<View> visited = new ArrayList<View>();
        List<View> unvisited = new ArrayList<View>();
        unvisited.add(v);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            visited.add(child);
            if (!(child instanceof ViewGroup)) continue;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i=0; i<childCount; i++) unvisited.add(group.getChildAt(i));
        }

        return visited;
    }
/**
 * Perform an action in response to a menu item being clicked.
 *
 */

public boolean onOptionsItemSelected (MenuItem item) 
{
    //mPaint.setXfermode(null);
    //mPaint.setAlpha(0xFF);

    switch (item.getItemId()) {
      case ENABLE_S2_MENU_ID:

          List<View> views = getAllChildrenBFS(mDragLayer);
          int[] viewCoordinates = new int[2];
          for (View v: views) {
              v.getLocationOnScreen(viewCoordinates);
              Log.e("thp", v.getClass().getName() + " x " + viewCoordinates[0] + " y " + viewCoordinates[1]);
          }
//            if (mSpot2 != null) mSpot2.setDragLayer (mDragLayer);
            return true;
      case DISABLE_S2_MENU_ID:
            if (mSpot2 != null) mSpot2.setDragLayer (null);
            return true;
      case ADD_OBJECT_MENU_ID:
            // Add a new object to the DragLayer and see if it can be dragged around.
            ImageView newView = new ImageView (this);
            newView.setImageResource (R.drawable.hello);
            int w = 200;
            int h = 200;
            int left = 80;
            int top = 100;
            DragLayer.LayoutParams lp = new DragLayer.LayoutParams (w, h, left, top);
            mDragLayer.addView (newView, lp);

//          Log.d("thp", "DragLayer2.adddd thp"+ newView.getId ());
            newView.setOnClickListener(this);
            newView.setOnLongClickListener(this);
            newView.setOnTouchListener(this);
            return true;
      case CHANGE_TOUCH_MODE_MENU_ID:
            mLongClickStartsDrag = !mLongClickStartsDrag;
            String message = mLongClickStartsDrag ? "Changed touch mode. Drag now starts on long touch (click)." 
                                                  : "Changed touch mode. Drag now starts on touch (click).";
            Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();
            return true;
    }

    return super.onOptionsItemSelected(item);
}

/**
 * This is the starting point for a drag operation if mLongClickStartsDrag is false.
 * It looks for the down event that gets generated when a user touches the screen.
 * Only that initiates the drag-drop sequence.
 *
 */    

public boolean onTouch (View v, MotionEvent ev)
{

    //this is a new object that hasn't been added to the dragLayer yet
    if (v.getId() == R.id.image_template || v.getId() == R.id.text_template || v.getId() == R.id.box_template) {
        return false;
    }

    if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
        lastTouchDownXY[0] = (int) ev.getX();
        lastTouchDownXY[1] = (int) ev.getY();
    }

    if (lastTouchDownXY[0] > v.getWidth() - 200 && lastTouchDownXY[1] > v.getHeight() - 200) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            int w = (int) ev.getX();
            int h =  (int) ev.getY();
            int left = (int)   v.getX();
            int top = (int) v.getY();
            DragLayer.LayoutParams lp = new DragLayer.LayoutParams (w, h, left, top);
            mDragLayer.updateViewLayout(v, lp);
        }
    }
    return  false;
//    // If we are configured to start only on a long click, we are not going to handle any events here.
//    if (mLongClickStartsDrag) return false;
//
//    boolean handledHere = false;
//
//    final int action = ev.getAction();
//    Log.d ("thp", "onTouch old");
//    // In the situation where a long click is not needed to initiate a drag, simply start on the down event.
//    if (action == MotionEvent.ACTION_DOWN) {
//       handledHere = startDrag (v);
//    }
//
//    return handledHere;
}

public boolean startDrag (View v)
{
    // Let the DragController initiate a drag-drop sequence.
    // I use the dragInfo to pass along the object being dragged.
    // I'm not sure how the Launcher designers do this.
    Object dragInfo = v;

    if (v.getId() == R.id.image_template || v.getId() == R.id.text_template || v.getId() == R.id.box_template) {
        mDragController2.startDrag (v, mDragLayer2, dragInfo, DragController.DRAG_ACTION_MOVE);
    } else {
        mDragController.startDrag (v, mDragLayer, dragInfo, DragController.DRAG_ACTION_MOVE);
    }
    return true;
}


/**
 * Finds all the views we need and configure them to send click events to the activity.
 *
 */
private void setupViews() 
{
    DragController dragController = mDragController;

    mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
    mDragLayer.setDragController(dragController);
    mDragLayer.setOnClickListener(this);
    mDragLayer.setOnLongClickListener(this);
    mDragLayer.setOnTouchListener(this);
//    dragController.setDragLayer(mDragLayer);
    dragController.addDropTarget (mDragLayer);

    mDeleteZone = (DeleteZone) findViewById (R.id.delete_zone_view);
    if (mDeleteZone != null) {
        mDeleteZone.setOnDragListener(dragController);
    }
    dragController.addDropTarget (mDeleteZone);

    //this is the drag layer for the templates at the bottom
    DragController dragController2 = mDragController2;
    mDragLayer2 = (DragLayer) findViewById(R.id.drag_layer_templates);
    mDragLayer2.setDragController(dragController2);
    dragController2.addDropTarget (mDragLayer);

    //set the correct postions
    ImageView imageTemplate = (ImageView) findViewById (R.id.image_template);
    ImageView imageTemplateFixed = (ImageView) findViewById (R.id.image_template_fixed);

    int margin = 60;
    int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
    int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
    Log.d("thp", "DragLayer2.onDragExit thp"+w);
    int left = margin;
    int top = 0;
    DragLayer.LayoutParams lp = new DragLayer.LayoutParams (w, h, left, top);
    mDragLayer2.updateViewLayout(imageTemplate, lp);
    mDragLayer2.updateViewLayout(imageTemplateFixed, lp);
    imageTemplate.setOnClickListener(this);
    imageTemplate.setOnLongClickListener(this);
    imageTemplate.setOnTouchListener(this);

    ImageView textTemplate = (ImageView) findViewById (R.id.text_template);
    ImageView textTemplateFixed = (ImageView) findViewById (R.id.text_template_fixed);

    w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
    h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
    left = 2*margin+w;
    top = 0;
    lp = new DragLayer.LayoutParams (w, h, left, top);
    mDragLayer2.updateViewLayout(textTemplate, lp);
    mDragLayer2.updateViewLayout(textTemplateFixed, lp);
    textTemplate.setOnClickListener(this);
    textTemplate.setOnLongClickListener(this);
    textTemplate.setOnTouchListener(this);

    ImageView boxTemplate = (ImageView) findViewById (R.id.box_template);
    ImageView boxTemplateFixed = (ImageView) findViewById (R.id.box_template_fixed);

    w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
    h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
    left = 3*margin+ 2*w;
    top = 0;
    lp = new DragLayer.LayoutParams (w, h, left, top);
    mDragLayer2.updateViewLayout(boxTemplate, lp);
    mDragLayer2.updateViewLayout(boxTemplateFixed, lp);
    boxTemplate.setOnClickListener(this);
    boxTemplate.setOnLongClickListener(this);
    boxTemplate.setOnTouchListener(this);

    //old stuff
//    ImageView i1 = (ImageView) findViewById (R.id.Image1);
//    ImageView i2 = (ImageView) findViewById (R.id.Image2);
//
//    i1.setOnClickListener(this);
//    i1.setOnLongClickListener(this);
//    i1.setOnTouchListener(this);
//
//    i2.setOnClickListener(this);
//    i2.setOnLongClickListener(this);
//    i2.setOnTouchListener(this);




    // Give the user a little guidance.
    String message = mLongClickStartsDrag ? "Press and hold to start dragging." 
                                          : "Touch a view to start dragging.";
    Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();

}

/**
 * Show a string on the screen via Toast.
 * 
 * @param msg String
 * @return void
 */

public void toast (String msg)
{
    Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
} // end toast

/**
 * Send a message to the debug log and display it using Toast.
 */

public void trace (String msg) 
{
    if (!Debugging) return;
    Log.d ("DragActivity", msg);
    toast (msg);
}

} // end class
