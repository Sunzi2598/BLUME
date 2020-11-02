// Generated code from Butter Knife. Do not modify!
package com.company.blumeserver.Common;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.company.blumeserver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BottomSheetOrderFragment_ViewBinding implements Unbinder {
  private BottomSheetOrderFragment target;

  private View view7f09012b;

  private View view7f090161;

  private View view7f090160;

  private View view7f090065;

  @UiThread
  public BottomSheetOrderFragment_ViewBinding(final BottomSheetOrderFragment target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.placed_filter, "method 'onPlacedFilterClick'");
    view7f09012b = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onPlacedFilterClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.shipping_filter, "method 'onShippingFilterClick'");
    view7f090161 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onShippingFilterClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.shipped_filter, "method 'onShippedFilterClick'");
    view7f090160 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onShippedFilterClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.cancelled_filter, "method 'onCancelledFilterClick'");
    view7f090065 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onCancelledFilterClick();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    target = null;


    view7f09012b.setOnClickListener(null);
    view7f09012b = null;
    view7f090161.setOnClickListener(null);
    view7f090161 = null;
    view7f090160.setOnClickListener(null);
    view7f090160 = null;
    view7f090065.setOnClickListener(null);
    view7f090065 = null;
  }
}
