// Generated code from Butter Knife. Do not modify!
package com.company.blumeserver.ui.best_deals;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.company.blumeserver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BestDealsFragment_ViewBinding implements Unbinder {
  private BestDealsFragment target;

  @UiThread
  public BestDealsFragment_ViewBinding(BestDealsFragment target, View source) {
    this.target = target;

    target.recycler_best_deal = Utils.findRequiredViewAsType(source, R.id.recycler_best_deal, "field 'recycler_best_deal'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BestDealsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recycler_best_deal = null;
  }
}
