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

public class MyAddonAdapter$MyViewHolder_ViewBinding implements Unbinder {
  private MyAddonAdapter.MyViewHolder target;

  @UiThread
  public MyAddonAdapter$MyViewHolder_ViewBinding(MyAddonAdapter.MyViewHolder target, View source) {
    this.target = target;

    target.txt_name = Utils.findRequiredViewAsType(source, R.id.txt_name, "field 'txt_name'", TextView.class);
    target.txt_price = Utils.findRequiredViewAsType(source, R.id.txt_price, "field 'txt_price'", TextView.class);
    target.img_delete = Utils.findRequiredViewAsType(source, R.id.img_delete, "field 'img_delete'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MyAddonAdapter.MyViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.txt_name = null;
    target.txt_price = null;
    target.img_delete = null;
  }
}
