package com.androidteam.campic.MainModule.fragments;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.androidteam.campic.Helper.AspectRatioFragment;
import com.androidteam.campic.Helper.MyBounceInterpolator;
import com.androidteam.campic.MainModule.cache.CacheManager;
import com.androidteam.campic.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.cameraview.AspectRatio;
import com.google.android.cameraview.CameraView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback,
        AspectRatioFragment.Listener,View.OnClickListener {

    private static final String TAG = CameraFragment.class.getSimpleName();
    private static final String FRAGMENT_DIALOG = "dialog";

    public interface OnPictureTakenListener {
        void pictureTaken();
    }
    public OnPictureTakenListener pictureTakenListener;

    private static final int[] FLASH_OPTIONS = {
                    CameraView.FLASH_AUTO,
                    CameraView.FLASH_OFF,
                    CameraView.FLASH_ON
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto_white_24dp,
            R.drawable.ic_flash_off_white_24dp,
            R.drawable.ic_flash_on_white_24dp
    };

    private static final int[] SWITCH_CAMERA_ICONS = {
            R.drawable.ic_camera_front_white_24dp,
            R.drawable.ic_camera_rear_white_24dp,
    };

    private int mCurrentFlash;
    private ImageView flashCameraButton;
    private ImageView switchCameraButton;
    private ImageView clickedImage;
    private ImageView cameraClick ;
    private CameraView mCameraView;
    private Handler mBackgroundHandler;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCameraView = view.findViewById(R.id.camera);
        cameraClick = (ImageView) view.findViewById(R.id.click);
        switchCameraButton = (ImageView) view.findViewById(R.id.switchCamera);
        flashCameraButton = (ImageView) view.findViewById(R.id.flash);
        ImageView aspectRatio = (ImageView) view.findViewById(R.id.aspect_ratio);
        clickedImage = (ImageView) view.findViewById(R.id.clickedImage);

        cameraClick.setOnClickListener(this);
        flashCameraButton.setOnClickListener(this);
        switchCameraButton.setOnClickListener(this);
        aspectRatio.setOnClickListener(this);

        mCameraView.addCallback(mCallback);
        enableCamera();
        enableCameraClick();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mCameraView) {
            mCameraView.start();
        }
    }

    @Override
    public void onPause() {
        if (mCameraView != null) {
            mCameraView.stop();
        }
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            enableCamera();
            enableCameraClick();
        } else {
            if (mCameraView != null) {
                mCameraView.stop();
            }
        }
    }

    @Override
    public void onDestroyView() {
        stopBackgroundHandler();
        super.onDestroyView();
    }

    private void stopBackgroundHandler() {
        if (mCameraView != null) {
            mCameraView.stop();
        }
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }

    @Override
    public void onAspectRatioSelected(@NonNull AspectRatio ratio) {
        if (mCameraView != null) {
            Toast.makeText(getContext(), "Aspect Ratio : " + ratio.toString(), Toast.LENGTH_SHORT).show();
            mCameraView.setAspectRatio(ratio);
        }
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    private CameraView.Callback mCallback = new CameraView.Callback() {
        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
            enableCamera();
            enableCameraControls();
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
            disableCameraClick();
            disableCameraControls();
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "onPictureTaken " + data.length);
            blinkAnimation();

            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    handlePictureClicked(data);
                }
            });
        }
    };

    public void setPictureTakenListener(OnPictureTakenListener pictureTakenListener) {
        this.pictureTakenListener = pictureTakenListener;
    }

    private void handlePictureClicked(byte[] data) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
       // String filePath =  CacheManager.getInstance().getImageInnerCache().getInnerCacheDir("CamPicCamera");
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + CacheManager.ROOT_STORE;


        String fileName = File.separator + timeStamp + ".jpeg";
        final File file = new File(filePath , fileName);

        OutputStream os = null;

        try {
            os = new FileOutputStream(file);
            os.write(data);
            os.close();
        }
        catch (IOException e) {
            Log.w(TAG, "Cannot write to " + file, e);
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // Ignore
                    Log.d(TAG,e.getLocalizedMessage());
                }
            }
        }

        enableCameraClick();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(getActivity())
                        .load(file)
                        .asBitmap()
                        .centerCrop()
                        .into(new BitmapImageViewTarget(clickedImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                clickedImage.setImageDrawable(circularBitmapDrawable);

                                clickedImage.setVisibility(View.VISIBLE);
                                final Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

                                // Use bounce interpolator with amplitude 0.2 and frequency 20
                                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                                myAnim.setInterpolator(interpolator);
                                clickedImage.startAnimation(myAnim);
                            }
                        });

            }
        });
    }

    private void blinkAnimation() {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.5f);
        fadeOut.setDuration(50);
        fadeOut.setFillAfter(true);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //Handle onAnimationStart
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AlphaAnimation fadeIn = new AlphaAnimation(0.5f, 1.0f);
                fadeIn.setDuration(50);
                fadeIn.setFillAfter(true);
                mCameraView.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //Handle onAnimationRepeat
            }
        });
        mCameraView.startAnimation(fadeOut);
    }

    private void disableCameraControls() {
        if (null != flashCameraButton) {
            flashCameraButton.setEnabled(false);
            flashCameraButton.setAlpha(0.5f);
        }

        if (null != switchCameraButton) {
            switchCameraButton.setEnabled(false);
            switchCameraButton.setAlpha(0.5f);
        }
    }

    private void enableCameraControls() {
        if (null != flashCameraButton) {
            flashCameraButton.setEnabled(true);
            flashCameraButton.setAlpha(1.0f);
        }

        if (null != switchCameraButton) {
            switchCameraButton.setEnabled(true);
            switchCameraButton.setAlpha(1.0f);
        }
    }

    private void disableCameraClick() {
        if (null != cameraClick) {
            cameraClick.setEnabled(false);
            cameraClick.setAlpha(0.5f);
        }
    }

    /**
     * Enable camera click
     */
    private void enableCameraClick() {
        if (null != getActivity()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null != cameraClick) {
                        cameraClick.setEnabled(true);
                        cameraClick.setAlpha(1.0f);
                    }
                }
            });
        }
    }

    /**
     * start camera view
     */
    public void enableCamera() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCameraView != null && !mCameraView.isCameraOpened()) {
                    mCameraView.start();
                }
            }
        }, 300);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.click:
                if (mCameraView != null) {
                    if (!mCameraView.isCameraOpened()) {
                        mCameraView.start();
                    }
                    mCameraView.takePicture();
                    disableCameraClick();
                }
                break;

            case R.id.aspect_ratio:
                FragmentManager fragmentManager = this.getChildFragmentManager();
                if (mCameraView != null && fragmentManager.findFragmentByTag(FRAGMENT_DIALOG) == null) {
                    final Set<AspectRatio> ratios = mCameraView.getSupportedAspectRatios();
                    final AspectRatio currentRatio = mCameraView.getAspectRatio();
                    AspectRatioFragment aspectRatioFragment = AspectRatioFragment.newInstance(ratios, currentRatio);
                    aspectRatioFragment.setmListener(this);
                    aspectRatioFragment.show(fragmentManager,FRAGMENT_DIALOG);
                }
                break;
            case R.id.flash:
                if (mCameraView != null) {
                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                    flashCameraButton.setImageResource(FLASH_ICONS[mCurrentFlash]);
                    mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
                }
                break;
            case R.id.switchCamera:
                if (mCameraView != null) {
                    int facing = mCameraView.getFacing();
                    mCameraView.setFacing(facing == CameraView.FACING_FRONT ? CameraView.FACING_BACK : CameraView.FACING_FRONT);
                    switchCameraButton.setImageResource(facing == CameraView.FACING_FRONT ? SWITCH_CAMERA_ICONS[1] : SWITCH_CAMERA_ICONS[0]);
                    enableCameraControls();
                }
                break;
        }
    }
}
