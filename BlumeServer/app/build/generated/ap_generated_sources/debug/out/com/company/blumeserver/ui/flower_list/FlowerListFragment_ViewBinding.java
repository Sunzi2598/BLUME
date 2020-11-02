// Generated code from Butter Knife. Do not modify!
package com.company.blumeserver.ui.flower_list;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.company.blumeserver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FlowerListFragment_ViewBinding implements Unbinder {
  private FlowerListFragment target;

  @UiThread
  public FlowerListFragment_ViewBinding(FlowerListFragment target, View source) {
    this.target = target;

    target.recycler_flower_list = Utils.findRequiredViewAsType(source, R.id.recycler_flower_list, "field 'recycler_flower_list'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    FlowerListFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recycler_flower_list = null;
  }
}
