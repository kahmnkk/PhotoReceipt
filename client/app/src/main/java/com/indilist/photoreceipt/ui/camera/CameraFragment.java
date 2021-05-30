package com.indilist.photoreceipt.ui.camera;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonObject;
import com.indilist.photoreceipt.DBHelper;
import com.indilist.photoreceipt.MainActivity;
import com.indilist.photoreceipt.R;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Hdr;
import com.otaliastudios.cameraview.engine.meter.WhiteBalanceMeter;
import com.otaliastudios.cameraview.filter.Filter;
import com.otaliastudios.cameraview.filter.Filters;
import com.otaliastudios.cameraview.filter.MultiFilter;
import com.otaliastudios.cameraview.filters.BlackAndWhiteFilter;
import com.otaliastudios.cameraview.filters.GrayscaleFilter;
import com.otaliastudios.cameraview.filters.VignetteFilter;
import com.otaliastudios.cameraview.gesture.Gesture;
import com.otaliastudios.cameraview.gesture.GestureAction;
import com.otaliastudios.cameraview.markers.AutoFocusMarker;
import com.otaliastudios.cameraview.markers.AutoFocusTrigger;
import com.otaliastudios.cameraview.markers.DefaultAutoFocusMarker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;

public class CameraFragment extends Fragment {

    private CameraViewModel cameraViewModel;
    private CameraView camera;
    private ImageButton capture_btn;
    private Button filterBtn;
    final int PERMISSIONS_REQUEST_CODE = 1;
    private LinearLayout filterMenu;
    private Boolean filterMenuOn = false;
    private SeekBar BrightBar;
    private TextView Bright_percent;
    private ProcessingFilter filter = new ProcessingFilter();
    private SeekBar ContrastBar;
    private TextView Contrast_percent;
    private SeekBar SaturationBar;
    private TextView Saturation_percent;
    private ImageButton FacingBtn;
    private ToggleButton hdrToggle;
    private Boolean hdr= false;
    private TextView exposure_percent;
    private SeekBar exposureBar;
    private float EXPOSURE_MAX;
    private float EXPOSURE_MIN;
    private TextView rboost_percent;
    private SeekBar rboostBar;
    private TextView gboost_percent;
    private SeekBar gboostBar;
    private TextView bboost_percent;
    private SeekBar bboostBar;
    private boolean negative;
    private TextView negativeStatus;
    private Switch negativeSwitch;
    private float exposureStatus = 0.f;
    private TextView sepiaStatus;
    private Switch sepiaSwitch;
    private boolean sepia;
    private TextView vignette_percent;
    private SeekBar vignetteBar;
    private DBHelper helper;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cameraViewModel =
                new ViewModelProvider(this).get(CameraViewModel.class);
        View root = inflater.inflate(R.layout.fragment_camera, container, false);
        /*
        final TextView textView = root.findViewById(R.id.text_home);
        cameraViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */
        camera = (CameraView)root.findViewById(R.id.camera);
        capture_btn = (ImageButton)root.findViewById(R.id.capture);
        filterMenu = (LinearLayout)root.findViewById(R.id.filterContainer);
        filterBtn = (Button)root.findViewById(R.id.filter_btn);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!filterMenuOn){
                    filterMenuOn = true;
                    filterMenu.setVisibility(View.VISIBLE);
                    TranslateAnimation ta = new TranslateAnimation(-1000, 0, 0, 0);
                    ta.setDuration(800);
                    //ta.setFillAfter(true);
                    filterMenu.setAnimation(ta);

                }else{
                    filterMenuOn = false;
                    filterMenu.setVisibility(View.INVISIBLE);
                }
            }
        });
        FacingBtn = (ImageButton)root.findViewById(R.id.facing_btn);
        FacingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(camera.getFacing() == Facing.BACK){
                    camera.setFacing(Facing.FRONT);
                }else{
                    camera.setFacing(Facing.BACK);
                }
            }
        });
        Bright_percent = (TextView)root.findViewById(R.id.bright_percent);
        BrightBar = (SeekBar)root.findViewById(R.id.bright_bar);
        BrightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                filter.setBrightness_param(i);
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(1);
                Bright_percent.setText(format.format(i * 0.02f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Contrast_percent = (TextView)root.findViewById(R.id.contrast_percent);
        ContrastBar = (SeekBar)root.findViewById(R.id.contrast_bar);
        ContrastBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                filter.setContrast_param(i);
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(1);
                Contrast_percent.setText(format.format(i * 0.02f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Saturation_percent = (TextView)root.findViewById(R.id.saturation_percent);
        SaturationBar = (SeekBar)root.findViewById(R.id.saturation_bar);
        SaturationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                filter.setSaturation_param(i);
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(1);
                Saturation_percent.setText(format.format(i * 0.02f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        exposure_percent = (TextView)root.findViewById(R.id.exposureCorrection);
        exposureBar = (SeekBar)root.findViewById(R.id.exposure_bar);
        exposureBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int exposure = i - 50;
                float amount = EXPOSURE_MAX / 50.f;
                float result = (float)exposure * amount;
                exposureStatus = result;
                camera.setExposureCorrection(result);
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(2);
                String ev;
                if(result > 0){
                    ev = "EV +";
                }else if(result < 0){
                    ev = "EV ";
                }else{
                    ev = "EV ";
                }
                exposure_percent.setText(ev + format.format(result));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rboost_percent = (TextView)root.findViewById(R.id.rboost_percent);
        rboostBar = (SeekBar)root.findViewById(R.id.rboost_bar);
        rboostBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                filter.setRboost_param(i);
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(1);
                rboost_percent.setText(format.format(i * 0.02f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        gboost_percent = (TextView)root.findViewById(R.id.gboost_percent);
        gboostBar = (SeekBar)root.findViewById(R.id.gboost_bar);
        gboostBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                filter.setGboost_param(i);
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(1);
                gboost_percent.setText(format.format(i * 0.02f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        bboost_percent = (TextView)root.findViewById(R.id.bboost_percent);
        bboostBar = (SeekBar)root.findViewById(R.id.bboost_bar);
        bboostBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                filter.setBboost_param(i);
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(1);
                bboost_percent.setText(format.format(i * 0.02f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        negativeStatus = (TextView)root.findViewById(R.id.negative_status);
        negativeSwitch = (Switch)root.findViewById(R.id.negative_switch);
        negativeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    negativeStatus.setText("ON");
                    filter.setNegative_param(true);
                    negative = true;
                }else{
                    negativeStatus.setText("OFF");
                    filter.setNegative_param(false);
                    negative = false;
                }
            }
        });

        sepiaStatus = (TextView)root.findViewById(R.id.sepia_status);
        sepiaSwitch = (Switch)root.findViewById(R.id.sepia_switch);
        sepiaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sepiaStatus.setText("ON");
                    filter.setSepia_param(true);
                    sepia = true;
                }else{
                    sepiaStatus.setText("OFF");
                    filter.setSepia_param(false);
                    sepia = false;
                }
            }
        });

        vignette_percent = (TextView)root.findViewById(R.id.vignette_percent);
        vignetteBar = (SeekBar)root.findViewById(R.id.vignette_bar);
        vignetteBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                filter.setVignette_param(i);
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(2);
                vignette_percent.setText(format.format(i * 0.01f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        camera.setLifecycleOwner(this);
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onCameraOpened(@NonNull CameraOptions options) {
                super.onCameraOpened(options);
                EXPOSURE_MAX = options.getExposureCorrectionMaxValue();
                EXPOSURE_MIN = options.getExposureCorrectionMinValue();
            }

            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                long time = Calendar.getInstance().getTimeInMillis();
                String foldername = "PhotoRecipe";
                String filename = "recipe"+time + ".jpg";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + foldername);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                Uri item = contentResolver.insert(collection, values);
                try {
                    ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w");
                    FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
                    fos.write(result.getData());
                    fos.close();
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    contentResolver.update(item, values, null, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SQLiteDatabase db = helper.getWritableDatabase();
                try {
                    JSONObject filter = getFilterData();
                    String jsonToString = filter.toString();
                    ContentValues contentVal = new ContentValues();
                    contentVal.put("fname", filename);
                    contentVal.put("filter", jsonToString);
                    db.insert("photo", null, contentVal);
                    db.close();
                    contentVal.clear();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePictureSnapshot();
                Toast.makeText(getContext(), "captured",Toast.LENGTH_LONG).show();
            }
        });

        hdrToggle = (ToggleButton)root.findViewById(R.id.hdrtoggle);
        hdrToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    camera.setHdr(Hdr.ON);
                }else{
                    camera.setHdr(Hdr.OFF);
                }
            }
        });
        //camera.setFilter(new VignetteFilter());
        //camera.setFilter(new GaussianFilter());
        camera.setFilter(filter);
        camera.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        camera.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS);
        camera.setAutoFocusMarker(new DefaultAutoFocusMarker());
        camera.setPlaySounds(false);
        camera.setPictureSnapshotMetering(true);
        helper = new DBHelper(getContext());


        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        requestPermission();

    }

    private void requestPermission(){
        boolean shouldProviceRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(shouldProviceRationale){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            }
            return;
        }
    }

    private JSONObject getFilterData() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("brightness", Math.floor(filter.getBrightness()*10)/10.0);
        obj.put("contrast", Math.floor(filter.getContrast()*10)/10.0);
        obj.put("saturation", Math.floor(filter.getSaturation() * 10)/10.0);
        obj.put("rboost",Math.floor(filter.getRboost() * 10)/10.0);
        obj.put("gboost", Math.floor(filter.getGboost() * 10)/10.0);
        obj.put("bboost", Math.floor(filter.getBboost() * 10) / 10.0);
        obj.put("exposure",Math.floor(exposureStatus * 100)/100.0);
        obj.put("vignette", Math.floor(filter.getVignette() * 100)/100.0);
        if(sepia){
            obj.put("sepia", 1);
        }else{
            obj.put("sepia", 0);
        }
        if(negative){
            obj.put("negative", 1);
        }else{
            obj.put("negative", 0);
        }
        if(hdr){
            obj.put("hdr", 1);
        }else{
            obj.put("hdr", 0);
        }

        return obj;
    }

}