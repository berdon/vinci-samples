// Generated code from Butter Knife. Do not modify!
package com.vinci.samples.simple;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.vinci.samples.simple.MainActivity.ViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131165221);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131165221' for field 'mImageView' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mImageView = (android.widget.ImageView) view;
  }

  public static void reset(com.vinci.samples.simple.MainActivity.ViewHolder target) {
    target.mImageView = null;
  }
}
