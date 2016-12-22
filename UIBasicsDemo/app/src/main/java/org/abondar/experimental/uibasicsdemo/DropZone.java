package org.abondar.experimental.uibasicsdemo;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.TextView;

/**
 * Created by abondar on 12/21/16.
 */
public class DropZone extends Fragment {
    private View dropTarget;
    private TextView dropMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dropzone, container, false);
        dropMessage = (TextView) v.findViewById(R.id.dropmsg);
        dropTarget = v.findViewById(R.id.droptarget);
        dropTarget.setOnDragListener(new View.OnDragListener() {
            private static final String DROP_TAG = "DROP_TAG";
            private int dropCount = 0;
            private ObjectAnimator anim;

            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                int action = dragEvent.getAction();
                boolean res = true;

                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.v(DROP_TAG, "drag started ");
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.v(DROP_TAG, "drag entered target");
                        anim = ObjectAnimator.ofFloat((Object) view, "alpha", 1f, 0.5f);
                        anim.setInterpolator(new CycleInterpolator(40));
                        anim.setDuration(30 * 10000);
                        anim.start();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.v(DROP_TAG, "drop exited target");
                        if (anim != null) {
                            anim.end();
                            anim = null;
                        }
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.v(DROP_TAG, "drag is moving to target");
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.v(DROP_TAG, "drag drop in target");
                        if (anim != null) {
                            anim.end();
                            anim = null;
                        }

                        dropCount++;
                        String message = dropCount + " drop";
                        if (dropCount > 1) {
                            message += "s";
                        }
                        dropMessage.setText(message);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.v(DROP_TAG, "drag drop ended");
                        if (anim != null) {
                            anim.end();
                            anim = null;
                        }
                        break;
                    default:
                        Log.v(DROP_TAG,"other action in dropzone "+action);
                        return false;
                }
                return res;
            }
        });
        return v;
    }
}
