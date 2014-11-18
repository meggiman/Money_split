package ch.ethz.itet.pps.budgetSplit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.util.List;

/**
 * Created by Chrissy on 17.11.2014.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private boolean isChecked;
    private List<Checkable> checkableViews;
    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean b) {
        this.isChecked = isChecked;
        if (checkableViews != null) { // careful!!
            for (Checkable c : checkableViews) {
                // Pass the information to all the child Checkable widgets
                c.setChecked(isChecked);
            }
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        this.isChecked = !this.isChecked;
        for (Checkable c : checkableViews) {
            // Pass the information to all the child Checkable widgets
            c.toggle();
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = this.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            findCheckableChildren((View) this.getChildAt(i));
        }
    }

    /**
     * Add to our checkable list all the children of the view that implement the
     * interface Checkable
     */
    private void findCheckableChildren(View v) {
        if (v instanceof Checkable) {
            this.checkableViews.add((Checkable) v);
        }
    }
}
