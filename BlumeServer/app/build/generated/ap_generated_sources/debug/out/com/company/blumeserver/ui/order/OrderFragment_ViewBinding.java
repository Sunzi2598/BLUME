// Generated code from Butter Knife. Do not modify!
package com.company.blumeserver.ui.order;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.company.blumeserver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class OrderFragment_ViewBinding implements Unbinder {
  private OrderFragment target;

  @UiThread
  public OrderFragment_ViewBinding(OrderFragment target, View source) {
    this.target = target;

    target.recycler_order = Utils.findRequiredViewAsType(source, R.id.recycler_order, "field 'recycler_order'", RecyclerView.class);
    target.txt_order_filter = Utils.findRequiredViewAsType(source, R.id.txt_order_filter, "field 'txt_order_filter'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    OrderFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recycler_order = null;
    target.txt_order_filter = null;
  }
}
