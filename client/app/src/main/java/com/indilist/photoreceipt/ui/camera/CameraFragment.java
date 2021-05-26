package com.indilist.photoreceipt.ui.camera;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.indilist.photoreceipt.MainActivity;
import com.indilist.photoreceipt.R;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.filter.Filter;
import com.otaliastudios.cameraview.filter.Filters;
import com.otaliastudios.cameraview.filter.MultiFilter;
import com.otaliastudios.cameraview.filters.BlackAndWhiteFilter;
import com.otaliastudios.cameraview.filters.GrayscaleFilter;

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

        Bright_percent = (TextView)root.findViewById(R.id.bright_percent);
        BrightBar = (SeekBar)root.findViewById(R.id.bright_bar);
        BrightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                filter.setBrightness_param(i);
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(2);
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
                format.setMaximumFractionDigits(2);
                Contrast_percent.setText(format.format(i * 0.02f));
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
            public void onPictureTaken(@NonNull PictureResult result) {
                long time = Calendar.getInstance().getTimeInMillis();
                String filename = "recipe"+time + ".jpg";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
                ContentResolver contentResolver = getContext().getContentResolver();
                Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                Uri item = contentResolver.insert(collection, values);
                try {
                    ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w");
                    FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
                    //result.toFile(fos, new ready());
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
            }
        });

        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePictureSnapshot();
                Toast.makeText(getContext(), "captured",Toast.LENGTH_LONG).show();
            }
        });


        camera.setFilter(filter);

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

}