package com.androidteam.campic.MainModule.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.androidteam.campic.MainModule.adapters.PreviewAdapter;
import com.androidteam.campic.MainModule.cache.CacheManager;
import com.androidteam.campic.R;
import com.imnjh.imagepicker.CapturePhotoHelper;
import com.imnjh.imagepicker.PhotoLoadListener;
import com.imnjh.imagepicker.PickerAction;
import com.imnjh.imagepicker.SImagePicker;
import com.imnjh.imagepicker.activity.CropImageActivity;
import com.imnjh.imagepicker.activity.PickerPreviewActivity;
import com.imnjh.imagepicker.adapter.PhotoAdapter;
import com.imnjh.imagepicker.control.AlbumController;
import com.imnjh.imagepicker.control.PhotoController;
import com.imnjh.imagepicker.model.Album;
import com.imnjh.imagepicker.model.Photo;
import com.imnjh.imagepicker.util.CollectionUtils;
import com.imnjh.imagepicker.widget.GridInsetDecoration;
import com.imnjh.imagepicker.widget.PreviewViewPager;
import com.imnjh.imagepicker.widget.SquareRelativeLayout;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment implements PickerAction {

    public static final String PARAM_CUSTOM_PICK_TEXT_RES = "PARAM_CUSTOM_PICK_TEXT_RES";
    public static final int REQUEST_CODE_PICKER_PREVIEW = 100;
    public static final int REQUEST_CODE_CROP_IMAGE = 101;

    private Activity activity;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private PreviewViewPager currentImageViewPager;
    private PreviewAdapter previewAdapter;
    private GridLayoutManager layoutManager;
    private int maxCount;
    private int mode;
    private int rowCount;
    private @StringRes
    int pickRes;
    private AppCompatSpinner albumSpinner;
    private final PhotoController photoController = new PhotoController();
    private final AlbumController albumController = new AlbumController();

    private final AlbumController.OnDirectorySelectListener directorySelectListener = new AlbumController.OnDirectorySelectListener() {
                @Override
                public void onSelect(Album album) {
                    photoController.resetLoad(album);
                }

                @Override
                public void onReset(Album album) {
                    photoController.load(album);
                }
            };

    private final PhotoAdapter.OnPhotoActionListener selectionChangeListener = new PhotoAdapter.OnPhotoActionListener() {

                @Override
                public void onSelect(String filePath) {
                    Log.d("selectionChangeListener","select");
                }

                @Override
                public void onDeselect(String filePath) {
                    Log.d("selectionChangeListener","onDeselect");
                }

                @Override
                public void onPreview(final int position, Photo photo, final View view) {
                    photoController.getAllPhoto(new PhotoLoadListener() {
                        @Override
                        public void onLoadComplete(ArrayList<Uri> photoUris) {
                            if (!CollectionUtils.isEmpty(photoUris)) {
                                PickerPreviewActivity.startPicturePreviewFromPicker(activity,
                                        PickerPreviewActivity.uris = photoUris,
                                        PickerPreviewActivity.selected = photoController.getSelectedPhoto(), position,
                                        true, maxCount, rowCount,
                                        pickRes, PickerPreviewActivity.AnchorInfo.newInstance(view), REQUEST_CODE_PICKER_PREVIEW,
                                        CacheManager
                                                .getInstance()
                                                .getImageInnerCache().toString());
                            }
                            if (null != currentImageViewPager) {
                                currentImageViewPager.setCurrentItem(position, true);
                            }
                        }

                        @Override
                        public void onLoadError() {
                        }
                    });
                }
            };

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    View view ;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        this.view = view;
        mode = SImagePicker.MODE_AVATAR;
        maxCount = 1000;
        rowCount = 4;
        initUI(view);
    }

    @SuppressLint("RestrictedApi")
    private void initUI(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        currentImageViewPager = (PreviewViewPager) view.findViewById(R.id.currentImageViewPager);

        toolbar.setNavigationIcon(R.drawable.ic_general_cancel_left);
        Drawable drawable = toolbar.getNavigationIcon();
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(activity, R.color.GoogleDesignDarkGreenColor));
            toolbar.setOverflowIcon(drawable);
        }

        layoutManager = new GridLayoutManager(activity, rowCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridInsetDecoration());
        photoController.onCreate(activity, recyclerView, selectionChangeListener, maxCount, rowCount, mode);

        photoController.setOnLoadCompleteListener(new PhotoController.OnLoadCompleteListener() {
            @Override
            public void loadCompleted() {
                photoController.getAllPhoto(new PhotoLoadListener() {
                    @Override
                    public void onLoadComplete(ArrayList<Uri> photoUris) {
                        previewAdapter = new PreviewAdapter(activity, photoUris);
                        currentImageViewPager.setAdapter(previewAdapter);
                    }

                    @Override
                    public void onLoadError() {
                        Log.d("LoadError", "Eror");
                    }
                });
            }
        });

        photoController.loadAllPhoto(activity);
        albumSpinner = (AppCompatSpinner) LayoutInflater.from(activity).inflate(R.layout.common_toolbar_spinner, toolbar, false);
        toolbar.addView(albumSpinner);

        albumController.onCreate(activity, albumSpinner, directorySelectListener);
        albumController.loadAlbums();
    }

    public void refreshLoader() {
      activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
              albumController.loadAlbums();
              //recyclerView.getAdapter().notifyDataSetChanged();
          }
      });
    }

    private void setResultAndFinish(ArrayList<String> selected, boolean original, int resultCode) {
        /*if (fileChooseInterceptor != null
                && !fileChooseInterceptor.onFileChosen(activity, selected, original, resultCode, this)) {
            // Prevent finish if interceptor returns false.
            return;
        }*/

        proceedResultAndFinish(selected, original, resultCode);
    }

    @Override
    public void proceedResultAndFinish(ArrayList<String> selected, boolean original, int resultCode) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICKER_PREVIEW) {
            if (data != null) {
                ArrayList<String> selected =
                        data.getStringArrayListExtra(PickerPreviewActivity.KEY_SELECTED);
                boolean selectOriginal =
                        data.getBooleanExtra(PickerPreviewActivity.KEY_SELECTED_ORIGINAL, false);
                if (resultCode == Activity.RESULT_CANCELED) {
                    photoController.setSelectedPhoto(selected);
                } else if (resultCode == Activity.RESULT_OK) {
                    setResultAndFinish(selected, selectOriginal, Activity.RESULT_OK);
                }
            }
        } else if (requestCode == REQUEST_CODE_CROP_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                String path = data.getStringExtra(CropImageActivity.RESULT_PATH);
                ArrayList<String> result = new ArrayList<>();
                result.add(path);
                setResultAndFinish(result, true, Activity.RESULT_OK);
            }
        } else if (requestCode == CapturePhotoHelper.CAPTURE_PHOTO_REQUEST_CODE) {
           /* if (resultCode == Activity.RESULT_CANCELED) {
                if (capturePhotoHelper.getPhoto() != null && capturePhotoHelper.getPhoto().exists()) {
                    capturePhotoHelper.getPhoto().delete();
                }
            } else if (resultCode == Activity.RESULT_OK) {
                if (mode == SImagePicker.MODE_AVATAR) {
                    File photoFile = capturePhotoHelper.getPhoto();
                    if (photoFile != null) {
                        CropImageActivity.startImageCrop(this, photoFile.getAbsolutePath(),
                                REQUEST_CODE_CROP_IMAGE,
                                avatarFilePath);
                    }
                } else {
                    File photoFile = capturePhotoHelper.getPhoto();
                    ArrayList<String> result = new ArrayList<>();
                    result.add(photoFile.getAbsolutePath());
                    setResultAndFinish(result, true, Activity.RESULT_OK);
                }*/
            //}
        }
    }

    private void refreshCheckbox() {
        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        int lastVisible = layoutManager.findLastVisibleItemPosition();
        for (int i = firstVisible; i <= lastVisible; i++) {
            View view = layoutManager.findViewByPosition(i);
            if (view instanceof SquareRelativeLayout) {
                SquareRelativeLayout item = (SquareRelativeLayout) view;
                if (item != null) {
                    String photoPath = (String) item.getTag();
                    if (photoController.getSelectedPhoto().contains(photoPath)) {
                        item.checkBox.setText(String.valueOf(photoController.getSelectedPhoto()
                                .indexOf(photoPath) + 1));
                        item.checkBox.refresh(false);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (null != albumController) {
            albumController.onDestroy();
        }
        if (null != photoController) {
            photoController.onDestroy();
        }
        super.onDestroy();
    }
}
