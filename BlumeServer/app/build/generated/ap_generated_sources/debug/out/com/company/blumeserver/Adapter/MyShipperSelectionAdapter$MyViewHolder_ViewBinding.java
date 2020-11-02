// Generated code from Butter Knife. Do not modify!
package com.company.blumeserver.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.company.blumeserver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MyShipperSelectionAdapter$MyViewHolder_ViewBinding implements Unbinder {
  private MyShipperSelectionAdapter.MyViewHolder target;

  @UiThread
  public MyShipperSelectionAdapter$MyViewHolder_ViewBinding(
      MyShipperSelectionAdapter.MyViewHolder target, View source) {
    this.target = target;

    target.txt_name = Utils.findRequiredViewAsType(source, R.id.txt_name, "field 'txt_name'", TextView.class);
    target.txt_phone = Utils.findRequiredViewAsType(source, R.id.txt_phone, "field 'txt_phone'", TextView.class);
    target.img_checked = Utils.findRequiredViewAsType(source, R.id.img_checked, "field 'img_checked'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MyShipperSelectionAdapter.MyViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.txt_name = null;
    target.txt_phone = null;
    target.img_checked = null;
  }
}
