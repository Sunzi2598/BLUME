// Generated code from Butter Knife. Do not modify!
package com.company.blumeserver.ui.shipper;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.company.blumeserver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ShipperFragment_ViewBinding implements Unbinder {
  private ShipperFragment target;

  @UiThread
  public ShipperFragment_ViewBinding(ShipperFragment target, View source) {
    this.target = target;

    target.recycler_shipper = Utils.findRequiredViewAsType(source, R.id.recycler_shipper, "field 'recycler_shipper'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ShipperFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recycler_shipper = null;
  }
}
