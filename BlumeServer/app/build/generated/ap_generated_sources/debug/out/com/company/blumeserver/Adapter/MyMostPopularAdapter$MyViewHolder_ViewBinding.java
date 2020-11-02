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

public class MyMostPopularAdapter$MyViewHolder_ViewBinding implements Unbinder {
  private MyMostPopularAdapter.MyViewHolder target;

  @UiThread
  public MyMostPopularAdapter$MyViewHolder_ViewBinding(MyMostPopularAdapter.MyViewHolder target,
      View source) {
    this.target = target;

    target.category_image = Utils.findRequiredViewAsType(source, R.id.img_category, "field 'category_image'", ImageView.class);
    target.category_name = Utils.findRequiredViewAsType(source, R.id.txt_category, "field 'category_name'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MyMostPopularAdapter.MyViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.category_image = null;
    target.category_name = null;
  }
}
