// Generated code from Butter Knife. Do not modify!
package com.vinci.samples.simple;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final com.vinci.samples.simple.MainActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131165250);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131165250' for field 'mGridView' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mGridView = (android.widget.GridView) view;
  }

  public static void reset(com.vinci.samples.simple.MainActivity target) {
    target.mGridView = null;
  }
}
