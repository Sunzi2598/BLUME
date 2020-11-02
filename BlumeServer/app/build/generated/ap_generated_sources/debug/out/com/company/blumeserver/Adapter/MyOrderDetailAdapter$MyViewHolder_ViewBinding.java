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

public class MyOrderDetailAdapter$MyViewHolder_ViewBinding implements Unbinder {
  private MyOrderDetailAdapter.MyViewHolder target;

  @UiThread
  public MyOrderDetailAdapter$MyViewHolder_ViewBinding(MyOrderDetailAdapter.MyViewHolder target,
      View source) {
    this.target = target;

    target.txt_flower_name = Utils.findRequiredViewAsType(source, R.id.txt_flower_name, "field 'txt_flower_name'", TextView.class);
    target.txt_flower_addon = Utils.findRequiredViewAsType(source, R.id.txt_flower_addon, "field 'txt_flower_addon'", TextView.class);
    target.txt_flower_quantity = Utils.findRequiredViewAsType(source, R.id.txt_flower_quantity, "field 'txt_flower_quantity'", TextView.class);
    target.img_flower_image = Utils.findRequiredViewAsType(source, R.id.img_flower_image, "field 'img_flower_image'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MyOrderDetailAdapter.MyViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.txt_flower_name = null;
    target.txt_flower_addon = null;
    target.txt_flower_quantity = null;
    target.img_flower_image = null;
  }
}
