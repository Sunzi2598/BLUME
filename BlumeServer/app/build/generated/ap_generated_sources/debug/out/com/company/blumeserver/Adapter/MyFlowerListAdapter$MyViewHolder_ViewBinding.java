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

public class MyFlowerListAdapter$MyViewHolder_ViewBinding implements Unbinder {
  private MyFlowerListAdapter.MyViewHolder target;

  @UiThread
  public MyFlowerListAdapter$MyViewHolder_ViewBinding(MyFlowerListAdapter.MyViewHolder target,
      View source) {
    this.target = target;

    target.txt_flower_name = Utils.findRequiredViewAsType(source, R.id.txt_flower_name, "field 'txt_flower_name'", TextView.class);
    target.txt_flower_price = Utils.findRequiredViewAsType(source, R.id.txt_flower_price, "field 'txt_flower_price'", TextView.class);
    target.img_flower_image = Utils.findRequiredViewAsType(source, R.id.img_flower_image, "field 'img_flower_image'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MyFlowerListAdapter.MyViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.txt_flower_name = null;
    target.txt_flower_price = null;
    target.img_flower_image = null;
  }
}
