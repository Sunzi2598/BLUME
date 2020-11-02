// Generated code from Butter Knife. Do not modify!
package com.company.blumeserver.ui.category;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.company.blumeserver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CategoryFragment_ViewBinding implements Unbinder {
  private CategoryFragment target;

  @UiThread
  public CategoryFragment_ViewBinding(CategoryFragment target, View source) {
    this.target = target;

    target.recycler_menu = Utils.findRequiredViewAsType(source, R.id.recycler_menu, "field 'recycler_menu'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CategoryFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recycler_menu = null;
  }
}
