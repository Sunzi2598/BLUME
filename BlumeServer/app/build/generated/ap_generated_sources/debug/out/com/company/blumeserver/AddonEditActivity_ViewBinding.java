// Generated code from Butter Knife. Do not modify!
package com.company.blumeserver;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AddonEditActivity_ViewBinding implements Unbinder {
  private AddonEditActivity target;

  private View view7f090057;

  private View view7f090058;

  @UiThread
  public AddonEditActivity_ViewBinding(AddonEditActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AddonEditActivity_ViewBinding(final AddonEditActivity target, View source) {
    this.target = target;

    View view;
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.tool_bar, "field 'toolbar'", Toolbar.class);
    target.edt_name = Utils.findRequiredViewAsType(source, R.id.edt_name, "field 'edt_name'", EditText.class);
    target.edt_price = Utils.findRequiredViewAsType(source, R.id.edt_price, "field 'edt_price'", EditText.class);
    view = Utils.findRequiredView(source, R.id.btn_create, "field 'btn_create' and method 'onCreateNew'");
    target.btn_create = Utils.castView(view, R.id.btn_create, "field 'btn_create'", Button.class);
    view7f090057 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onCreateNew();
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_edit, "field 'btn_edit' and method 'onEdit'");
    target.btn_edit = Utils.castView(view, R.id.btn_edit, "field 'btn_edit'", Button.class);
    view7f090058 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onEdit();
      }
    });
    target.recycler_addon = Utils.findRequiredViewAsType(source, R.id.recycler_addon, "field 'recycler_addon'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AddonEditActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.edt_name = null;
    target.edt_price = null;
    target.btn_create = null;
    target.btn_edit = null;
    target.recycler_addon = null;

    view7f090057.setOnClickListener(null);
    view7f090057 = null;
    view7f090058.setOnClickListener(null);
    view7f090058 = null;
  }
}
