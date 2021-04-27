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
import com.otaliastudios.cameraview.filter.Filters;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class CameraFragment extends Fragment {

    private CameraViewModel cameraViewModel;
    private CameraView camera;
    private ImageButton capture_btn;
    private Button BaseFilterBtn;
    private int filterID = 0;
    public String savepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/CameraRec/";
    final int PERMISSIONS_REQUEST_CODE = 1;
    private File dir;
    private File saved;

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
        BaseFilterBtn = (Button)root.findViewById(R.id.filter_btn);
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
                camera.takePicture();
                Toast.makeText(getContext(), "captured",Toast.LENGTH_LONG).show();
            }
        });

        BaseFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filterID == 0){
                    filterID = 1;
                    BaseFilterBtn.setText("SEPIA");
                    camera.setFilter(Filters.SEPIA.newInstance());
                }else if(filterID == 1){
                    filterID = 2;
                    BaseFilterBtn.setText("GrayScale");
                    camera.setFilter(Filters.GRAYSCALE.newInstance());
                }else if(filterID == 2){
                    filterID = 0;
                    BaseFilterBtn.setText("Base");
                    camera.setFilter(Filters.NONE.newInstance());
                }

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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflator){
        inflator.inflate(R.menu.filterlistmenu, menu);
        super.onCreateOptionsMenu(menu, inflator);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.grayscale:

                break;
            case R.id.sepia:
                break;
            case R.id.base:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ready implements FileCallback{

        @Override
        public void onFileReady(@Nullable File file) {
            Toast.makeText(getContext(), "File saved",Toast.LENGTH_LONG).show();
        }
    }
}