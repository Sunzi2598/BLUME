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

public class MyOrderAdapter$MyViewHolder_ViewBinding implements Unbinder {
  private MyOrderAdapter.MyViewHolder target;

  @UiThread
  public MyOrderAdapter$MyViewHolder_ViewBinding(MyOrderAdapter.MyViewHolder target, View source) {
    this.target = target;

    target.img_flower_image = Utils.findRequiredViewAsType(source, R.id.img_flower_image, "field 'img_flower_image'", ImageView.class);
    target.txt_name = Utils.findRequiredViewAsType(source, R.id.txt_name, "field 'txt_name'", TextView.class);
    target.txt_time = Utils.findRequiredViewAsType(source, R.id.txt_time, "field 'txt_time'", TextView.class);
    target.txt_order_status = Utils.findRequiredViewAsType(source, R.id.txt_order_status, "field 'txt_order_status'", TextView.class);
    target.txt_order_number = Utils.findRequiredViewAsType(source, R.id.txt_order_number, "field 'txt_order_number'", TextView.class);
    target.txt_num_item = Utils.findRequiredViewAsType(source, R.id.txt_num_item, "field 'txt_num_item'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MyOrderAdapter.MyViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.img_flower_image = null;
    target.txt_name = null;
    target.txt_time = null;
    target.txt_order_status = null;
    target.txt_order_number = null;
    target.txt_num_item = null;
  }
}
