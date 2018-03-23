package com.imnjh.imagepicker.widget;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.imnjh.imagepicker.R;


/**
 * Created by Martin on 2017/1/17.
 */
public class PickerBottomLayout extends FrameLayout {

  public ImageView cropImage;

  public ImageView filterImage;

  public TextView originalSize;

  public View originalContainer;

  public ImageView send;

  public ImageView delete;

  private int pickTextRes = R.string.general_share;

  public PickerBottomLayout(Context context) {
    this(context, null);
  }

  public PickerBottomLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PickerBottomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    inflate(context, R.layout.picker_bottom_layout, this);
    send = (ImageView) findViewById(R.id.send);
    originalContainer = findViewById(R.id.original_container);
    cropImage = (ImageView) findViewById(R.id.cropImage);
    filterImage = (ImageView) findViewById(R.id.FilterImage);
    delete = (ImageView) findViewById(R.id.delete);
  }

  /*public void updateSelectedCount(int count) {
    if (count == 0) {
      send.setTextColor(getResources().getColor(R.color.gray));
      send.setEnabled(false);
      send.setText("Send");
      originalContainer.setVisibility(View.GONE);
    } else {
      send.setTextColor(getResources().getColor(R.color.color_48baf3));
      send.setEnabled(true);
      send.setText("Send" + " "
          + getResources().getString(R.string.bracket_num, count));
      originalContainer.setVisibility(View.VISIBLE);
    }
  }*/

  public void updateSelectedSize(String size) {
    if (TextUtils.isEmpty(size)) {
      originalContainer.setVisibility(View.GONE);
      //originalCheckbox.setChecked(false);
    } else {
      originalContainer.setVisibility(View.VISIBLE);
      originalSize.setText(getResources().getString(R.string.general_original) + " "
          + getResources().getString(R.string.bracket_str, size));
    }
  }

  public void hide() {
    animate().translationY(getHeight())
        .setInterpolator(new AccelerateInterpolator(2));
  }

  public void show() {
    animate().translationY(0)
        .setInterpolator(new AccelerateInterpolator(2));
  }

  public void setCustomPickText(@StringRes int pickTextRes) {
    this.pickTextRes = pickTextRes;
  }
}
