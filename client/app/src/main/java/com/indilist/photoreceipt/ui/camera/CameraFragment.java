package com.indilist.photoreceipt.ui.camera;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;

public class CameraFragment extends Fragment {

    private CameraViewModel cameraViewModel;
    private CameraView camera;
    private ImageButton capture_btn;
    private Button filterBtn;
    public String savepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/CameraRec/";
    final int PERMISSIONS_REQUEST_CODE = 1;
    private File dir;
    private File saved;
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
                }else{
                    filterMenuOn = false;
                    filterMenu.setVisibility(View.INVISIBLE);
                }
            }
        });
        //camera.setFilter(filter);
        camera.setFilter(filter);
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
        dir = getAbsoluteFile("", getContext());
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
                long time = Calendar.getInstance().getTimeInMillis();
                String filename = "/"+Long.toString(time) + ".jpg";
                File f = new File(dir, filename);
                File file = makeFile(dir, dir+filename);
                result.toFile(file, new ready());
            }
        });

        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePictureSnapshot();
                Toast.makeText(getContext(), "captured",Toast.LENGTH_LONG).show();
            }
        });



        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    private File getAbsoluteFile(String relativePath, Context context){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            return new File(context.getExternalFilesDir(null), relativePath);
        }else{
            return new File(context.getFilesDir(), relativePath);
        }
    }

    private File makeFile(File dir, String filePath){
        File file = null;
        boolean issuccess = false;
        if(dir.isDirectory()){
            file = new File(filePath);
            if(file!=null && !file.exists()){
                Log.e("err", "!file.exists");
            }try{
                issuccess = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                Log.e("err","파일생성 여부 = " + issuccess);
            }

        }
        return file;
    }


    public class ready implements FileCallback{

        @Override
        public void onFileReady(@Nullable File file) {
            Toast.makeText(getContext(), "File saved",Toast.LENGTH_LONG).show();
        }
    }
}