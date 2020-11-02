// Generated code from Butter Knife. Do not modify!
package com.company.blumeserver.ui.most_popular;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.company.blumeserver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MostPopularFragment_ViewBinding implements Unbinder {
  private MostPopularFragment target;

  @UiThread
  public MostPopularFragment_ViewBinding(MostPopularFragment target, View source) {
    this.target = target;

    target.recycler_most_popular = Utils.findRequiredViewAsType(source, R.id.recycler_most_popular, "field 'recycler_most_popular'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MostPopularFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recycler_most_popular = null;
  }
}
