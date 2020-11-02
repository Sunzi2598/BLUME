// Generated code from Butter Knife. Do not modify!
package com.company.blumeserver.Adapter;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.SwitchCompat;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.company.blumeserver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MyShipperAdapter$MyViewHolder_ViewBinding implements Unbinder {
  private MyShipperAdapter.MyViewHolder target;

  @UiThread
  public MyShipperAdapter$MyViewHolder_ViewBinding(MyShipperAdapter.MyViewHolder target,
      View source) {
    this.target = target;

    target.txt_name = Utils.findRequiredViewAsType(source, R.id.txt_name, "field 'txt_name'", TextView.class);
    target.txt_phone = Utils.findRequiredViewAsType(source, R.id.txt_phone, "field 'txt_phone'", TextView.class);
    target.btn_enable = Utils.findRequiredViewAsType(source, R.id.btn_enable, "field 'btn_enable'", SwitchCompat.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MyShipperAdapter.MyViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.txt_name = null;
    target.txt_phone = null;
    target.btn_enable = null;
  }
}
