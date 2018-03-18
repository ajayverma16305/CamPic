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
import com.imnjh.imagepicker.FileChooseInterceptor;
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
import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment implements PickerAction{

    public static final String EXTRA_RESULT_SELECTION = "EXTRA_RESULT_SELECTION";
    public static final String EXTRA_RESULT_ORIGINAL = "EXTRA_RESULT_ORIGINAL";
    public static final String PARAM_MODE = "PARAM_MODE";
    public static final String PARAM_MAX_COUNT = "PARAM_MAX_COUNT";
    public static final String PARAM_SELECTED = "PARAM_SELECTED";
    public static final String PARAM_ROW_COUNT = "PARAM_ROW_COUNT";
    public static final String PARAM_SHOW_CAMERA = "PARAM_SHOW_CAMERA";
    public static final String PARAM_CUSTOM_PICK_TEXT_RES = "PARAM_CUSTOM_PICK_TEXT_RES";
    public static final String PARAM_FILE_CHOOSE_INTERCEPTOR = "PARAM_FILE_CHOOSE_INTERCEPTOR";

    public static final int REQUEST_CODE_PICKER_PREVIEW = 100;
    public static final int REQUEST_CODE_CROP_IMAGE = 101;
    public static final String AVATAR_FILE_NAME = "avatar.png";

    private Activity activity;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private PreviewViewPager currentImageViewPager;
    private PreviewAdapter previewAdapter;
    private GridLayoutManager layoutManager;
    private int maxCount;
    private int mode;
    private int rowCount;
    private boolean showCamera = false;
    private String avatarFilePath;
    private @StringRes
    int pickRes;
    private @StringRes int pickNumRes;
    private FileChooseInterceptor fileChooseInterceptor;
    private CapturePhotoHelper capturePhotoHelper;

    private AppCompatSpinner albumSpinner;
    private final PhotoController photoController = new PhotoController();
    private final AlbumController albumController = new AlbumController();
    private final AlbumController.OnDirectorySelectListener directorySelectListener =
            new AlbumController.OnDirectorySelectListener() {
                @Override
                public void onSelect(Album album) {
                    photoController.resetLoad(album);
                }

                @Override
                public void onReset(Album album) {
                    photoController.load(album);
                }
            };

    private final PhotoAdapter.OnPhotoActionListener selectionChangeListener =
            new PhotoAdapter.OnPhotoActionListener() {

                @Override
                public void onSelect(String filePath) {
                }

                @Override
                public void onDeselect(String filePath) {
                   // refreshCheckbox();
                }

                @Override
                public void onPreview(final int position, Photo photo, final View view) {
                    if(null != currentImageViewPager){
                        currentImageViewPager.setCurrentItem(position,true);
                    }

                    if (mode == SImagePicker.MODE_IMAGE) {
                        photoController.getAllPhoto(new PhotoLoadListener() {
                            @Override
                            public void onLoadComplete(ArrayList<Uri> photoUris) {
                                if (!CollectionUtils.isEmpty(photoUris)) {
                                    PickerPreviewActivity.startPicturePreviewFromPicker(activity,
                                            PickerPreviewActivity.uris = photoUris,
                                            PickerPreviewActivity.selected = photoController.getSelectedPhoto(), position,
                                            true, maxCount, rowCount,
                                            pickRes, PickerPreviewActivity.AnchorInfo.newInstance(view),
                                            REQUEST_CODE_PICKER_PREVIEW, CacheManager.getInstance().getImageInnerCache()
                                                    .getAbsolutePath(AVATAR_FILE_NAME));
                                }
                            }

                            @Override
                            public void onLoadError() {

                            }
                        });
                    } else if (mode == SImagePicker.MODE_AVATAR) {
                        photoController.getAllPhoto(new PhotoLoadListener() {
                            @Override
                            public void onLoadComplete(ArrayList<Uri> photoUris) {
                                if (!CollectionUtils.isEmpty(photoUris)) {
                                    PickerPreviewActivity.startPicturePreviewFromPicker(activity,
                                            PickerPreviewActivity.uris = photoUris,
                                            PickerPreviewActivity.selected =
                                                    photoController.getSelectedPhoto(), position,
                                            true, maxCount, rowCount,
                                            pickRes, PickerPreviewActivity.AnchorInfo.newInstance(view),
                                            REQUEST_CODE_PICKER_PREVIEW, CacheManager.getInstance().getImageInnerCache()
                                                    .getAbsolutePath(AVATAR_FILE_NAME));
                                }
                            }

                            @Override
                            public void onLoadError() {
                            }
                        });
                    }
                }
            };

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        mode = activity.getIntent().getIntExtra(PARAM_MODE, SImagePicker.MODE_AVATAR);
        maxCount = activity.getIntent().getIntExtra(PARAM_MAX_COUNT, 1);
        avatarFilePath = activity.getIntent().getStringExtra(CropImageActivity.PARAM_AVATAR_PATH);
        rowCount = activity.getIntent().getIntExtra(PARAM_ROW_COUNT, 4);
        showCamera = activity.getIntent().getBooleanExtra(PARAM_SHOW_CAMERA, false);
        initUI(view);
    }

    @SuppressLint("RestrictedApi")
    private void initUI(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        currentImageViewPager = (PreviewViewPager) view.findViewById(R.id.currentImageViewPager);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_general_cancel_left);
        Drawable drawable = toolbar.getNavigationIcon();
        if(drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(activity,R.color.GoogleDesignDarkGreenColor));
            toolbar.setOverflowIcon(drawable);
        }
        layoutManager = new GridLayoutManager(activity, rowCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridInsetDecoration());
        if (!showCamera) {
            photoController.onCreate(activity, recyclerView, selectionChangeListener, maxCount, rowCount,
                    mode);
        } else {
            capturePhotoHelper = new CapturePhotoHelper(this);
            photoController.onCreate(activity, recyclerView, selectionChangeListener, maxCount, rowCount,
                    mode, capturePhotoHelper);
        }
        photoController.setOnLoadCompleteListener(new PhotoController.OnLoadCompleteListener() {
            @Override
            public void loadCompleted() {
                photoController.getAllPhoto(new PhotoLoadListener() {
                    @Override
                    public void onLoadComplete(ArrayList<Uri> photoUris) {
                        previewAdapter = new PreviewAdapter(activity,photoUris);
                        currentImageViewPager.setAdapter(previewAdapter);
                    }

                    @Override
                    public void onLoadError() {
                        Log.d("LoadError","Eror");
                    }
                });
            }
        });
        photoController.loadAllPhoto(activity);

        fileChooseInterceptor = activity.getIntent().getParcelableExtra(PARAM_FILE_CHOOSE_INTERCEPTOR);
        ArrayList<String> selected = activity.getIntent().getStringArrayListExtra(PARAM_SELECTED);
        if (!CollectionUtils.isEmpty(selected)) {
            photoController.setSelectedPhoto(selected);
        }
        pickRes = activity.getIntent().getIntExtra(PARAM_CUSTOM_PICK_TEXT_RES, 0);
        albumSpinner = (AppCompatSpinner) LayoutInflater.from(activity).inflate(R.layout.common_toolbar_spinner,
                        toolbar, false);

        toolbar.addView(albumSpinner);
        albumController.onCreate(activity, albumSpinner, directorySelectListener);
        albumController.loadAlbums();
    }

    private void setResultAndFinish(ArrayList<String> selected, boolean original, int resultCode) {
        if (fileChooseInterceptor != null
                && !fileChooseInterceptor.onFileChosen(activity, selected, original, resultCode, this)) {
            // Prevent finish if interceptor returns false.
            return;
        }
        proceedResultAndFinish(selected, original, resultCode);
    }

    @Override
    public void proceedResultAndFinish(ArrayList<String> selected, boolean original, int resultCode) {

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
            if (resultCode == Activity.RESULT_CANCELED) {
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
                }
            }
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
        if(null != albumController){
            albumController.onDestroy();
        }
        if(null != photoController){
            photoController.onDestroy();
        }
        super.onDestroy();
    }
}
