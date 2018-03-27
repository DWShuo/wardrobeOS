package com.dws.wardrobeos.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.audiofx.EnvironmentalReverb;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.dws.wardrobeos.R;
import com.dws.wardrobeos.models.Action;
import com.dws.wardrobeos.models.Album;
import com.dws.wardrobeos.models.AlbumFile;
import com.dws.wardrobeos.models.ClothItem;

import org.dmfs.android.colorpicker.ColorPickerDialogFragment;
import org.dmfs.android.colorpicker.palettes.ArrayPalette;
import org.dmfs.android.colorpicker.palettes.ColorFactory;
import org.dmfs.android.colorpicker.palettes.ColorShadeFactory;
import org.dmfs.android.colorpicker.palettes.CombinedColorFactory;
import org.dmfs.android.colorpicker.palettes.FactoryPalette;
import org.dmfs.android.colorpicker.palettes.Palette;
import org.dmfs.android.colorpicker.palettes.RainbowColorFactory;
import org.dmfs.android.colorpicker.palettes.RandomPalette;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class NewActivity extends AppCompatActivity implements ColorPickerDialogFragment.ColorDialogResultListener, ActionSheet.ActionSheetListener {

    private static String TAG = "NewActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.photo) AppCompatImageView photoView;
    @BindView(R.id.spinner_type) AppCompatSpinner spinnerType;
    @BindView(R.id.spinner_brand) AppCompatSpinner spinnerBrand;
    @BindView(R.id.et_about) AppCompatEditText etAbout;
    @BindView(R.id.btn_color) AppCompatButton btnColor;

    private ArrayList<AlbumFile> mAlbumFiles;

    private final static int[] COLORS = new int[] {
            0xff000000, 0xff0000ff, 0xff00ff00, 0xffff0000, 0xffffff00, 0xff00ffff, 0xffff00ff, 0xff404040,
            0xff808080, 0xff8080ff, 0xff80ff80, 0xffff8080, 0xffffff80, 0xff80ffff, 0xffff80ff, 0xffffffff };

    private final static int[] MATERIAL_COLORS_PRIMARY = {
            0xffe91e63, 0xfff44336, 0xffff5722, 0xffff9800, 0xffffc107, 0xffffeb3b, 0xffcddc39, 0xff8bc34a,
            0xff4caf50, 0xff009688, 0xff00bcd4, 0xff03a9f4, 0xff2196f3, 0xff3f51b5, 0xff673ab7, 0xff9c27b0 };

    private static final int MATERIAL_COLORS_SECONDARY[] = {
            0xffad1457, 0xffc62828, 0xffd84315, 0xffef6c00, 0xffff8f00, 0xfff9a825, 0xff9e9d24, 0xff558b2f,
            0xff2e7d32, 0xff00695c, 0xff00838f, 0xff0277bd, 0xff1565c0, 0xff283593, 0xff4527a0, 0xff6a1b9a };

    private String mSelectedPalette = null;
    private int mSelectedColor = Color.parseColor("#689F38");

    private static int REQUEST_PERMISSION = 1884;
    private static int CAMERA_REQUEST_CODE = 1880;
    private boolean permissionResult = false;
    private String mPhotoName;
    private Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.type_array, R.layout.spinner_style);
        typeAdapter.setDropDownViewResource(R.layout.spinner_style);
        spinnerType.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> brandAdapter = ArrayAdapter.createFromResource(this, R.array.brand_array, R.layout.spinner_style);
        brandAdapter.setDropDownViewResource(R.layout.spinner_style);
        spinnerBrand.setAdapter(brandAdapter);

        // * permission

        String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!checkPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION);
        } else {
            permissionResult = true;
        }
    }

    @OnClick(R.id.btn_save) void save() {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        ClothItem clothItem = realm.createObject(ClothItem.class);
        Log.d("Test", (String)spinnerBrand.getSelectedItem());
        clothItem.setBrand((String)spinnerBrand.getSelectedItem());
        clothItem.setColor(mSelectedColor);
        Log.d("Test", String.valueOf(mSelectedColor));
        clothItem.setType((String)spinnerType.getSelectedItem());
        Log.d("Test", (String)spinnerType.getSelectedItem());
        clothItem.setInfo(etAbout.getText().toString());
        if (file != null) {
            clothItem.setPhoto(getName(file.getPath()));
            Log.d("Test", getName(file.getPath()));
            if (isExternalStorageWritable()) {

            } else {
                Toast.makeText(this, "Can't save photo to SD card", Toast.LENGTH_SHORT).show();
                clothItem.setPhoto("");
            }
            etAbout.setText("");
            photoView.setImageResource(R.mipmap.clothing_placeholder);
        } else {
            clothItem.setPhoto("");
        }

        realm.commitTransaction();
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @OnClick(R.id.photo) void photo() {
        if (permissionResult) {
            ActionSheet.createBuilder(this, getSupportFragmentManager())
                    .setCancelButtonTitle("Cancel")
                    .setOtherButtonTitles("From Gallery", "Take a picture")
                    .setCancelableOnTouchOutside(true)
                    .setListener(this).show();

        } else {

            Toast.makeText(this, "Permission disallowed to get photo from the phone", Toast.LENGTH_SHORT).show();

        }

    }

    @OnClick(R.id.btn_back) void back() {
        finish();
    }

    @OnClick(R.id.btn_color) void color() {

        ColorPickerDialogFragment d = new ColorPickerDialogFragment();

        ArrayList<Palette> palettes = new ArrayList<Palette>();

        palettes.add(new ArrayPalette("material_primary", "Material Colors", MATERIAL_COLORS_PRIMARY, 4));
        palettes.add(new ArrayPalette("material_secondary", "Dark Material Colors", MATERIAL_COLORS_SECONDARY, 4));

        // add a palette from the resources
        palettes.add(ArrayPalette.fromResources(this, "base", R.string.base_palette_name, R.array.base_palette_colors, R.array.base_palette_color_names));

        palettes.add(new ArrayPalette("base2", "Base 2", COLORS));

        // Android Material color schema
        // Add a palette with rainbow colors
        palettes.add(new FactoryPalette("rainbow", "Rainbow", ColorFactory.RAINBOW, 16));

        // Add a palette with many darker rainbow colors
        palettes.add(new FactoryPalette("rainbow2", "Dirty Rainbow", new RainbowColorFactory(0.5f, 0.5f), 16));

        // Add a palette with pastel colors
        palettes.add(new FactoryPalette("pastel", "Pastel", ColorFactory.PASTEL, 16));

        // Add a palette with red+orange colors
        palettes.add(new FactoryPalette("red/orange", "Red/Orange", new CombinedColorFactory(ColorFactory.RED, ColorFactory.ORANGE), 16));

        // Add a palette with yellow+green colors
        palettes.add(new FactoryPalette("yellow/green", "Yellow/Green", new CombinedColorFactory(ColorFactory.YELLOW, ColorFactory.GREEN), 16));

        // Add a palette with cyan+blue colors
        palettes.add(new FactoryPalette("cyan/blue", "Cyan/Blue", new CombinedColorFactory(ColorFactory.CYAN, ColorFactory.BLUE), 16));

        // Add a palette with purple+pink colors
        palettes.add(new FactoryPalette("purble/pink", "Purple/Pink", new CombinedColorFactory(ColorFactory.PURPLE, ColorFactory.PINK), 16));

        // Add a palette with red colors
        palettes.add(new FactoryPalette("red", "Red", ColorFactory.RED, 16));
        // Add a palette with green colors
        palettes.add(new FactoryPalette("green", "Green", ColorFactory.GREEN, 16));
        // Add a palette with blue colors
        palettes.add(new FactoryPalette("blue", "Blue", ColorFactory.BLUE, 16));

        // Add a palette with few random colors
        palettes.add(new RandomPalette("random1", "Random 1", 1));
        // Add a palette with few random colors
        palettes.add(new RandomPalette("random4", "Random 4", 4));
        // Add a palette with few random colors
        palettes.add(new RandomPalette("random9", "Random 9", 9));
        // Add a palette with few random colors
        palettes.add(new RandomPalette("random16", "Random 16", 16));

        // Add a palette with random colors
        palettes.add(new RandomPalette("random25", "Random 25", 25));

        // Add a palette with many random colors
        palettes.add(new RandomPalette("random81", "Random 81", 81));

        // Add a palette with secondary colors
        palettes.add(new FactoryPalette("secondary1", "Secondary 1", new CombinedColorFactory(new ColorShadeFactory(18),
                new ColorShadeFactory(53), new ColorShadeFactory(80), new ColorShadeFactory(140)), 16, 4));

        // Add another palette with secondary colors
        palettes.add(new FactoryPalette("secondary2", "Secondary 2", new CombinedColorFactory(new ColorShadeFactory(210),
                new ColorShadeFactory(265), new ColorShadeFactory(300), new ColorShadeFactory(340)), 16, 4));

        // set the palettes
        d.setPalettes(palettes.toArray(new Palette[palettes.size()]));

        // set the initial palette
        d.selectPaletteId(mSelectedPalette);

        // show the fragment
        d.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onColorChanged(int color, String paletteId, String colorName, String paletteName) {
        mSelectedPalette = paletteId;
        mSelectedColor = color;
        btnColor.setBackgroundColor(color);
    }

    @Override
    public void onColorDialogCancelled() {

    }

    private void selectAlbum() {
        Album.album(this)
                .singleChoice()
                .requestCode(200)
                .columnCount(2)
                .camera(true)
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(int requestCode, @NonNull ArrayList<AlbumFile> result) {
                        mAlbumFiles = result;
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(int requestCode, @NonNull String result) {
                        Toast.makeText(NewActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }

    private boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission:permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                permissionResult = true;
            } else {
                permissionResult = false;
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {

        if (index == 0) {

        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Drawable icon = null;
            InputStream is;
            try {
                is = getContentResolver().openInputStream(file);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 50;
                Bitmap preview_bitmap = BitmapFactory.decodeStream(is, null, options);
                icon = new BitmapDrawable(getResources(), preview_bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            photoView.setImageDrawable(icon);
        }
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            file = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".com.dws.wardrobeos.provider", getOutputMediaFile());
        } else {
            file = Uri.fromFile(getOutputMediaFile());
        }

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraDemo");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

   public static String getName(String filePath) {
        if (filePath == null || filePath.length() == 0) {
            return "";
        }
        int extract = filePath.lastIndexOf("?");
        if (extract > 0) {
            filePath = filePath.substring(0, extract);
        }
        int namePos = filePath.lastIndexOf(File.separatorChar);
        return (namePos >=0) ? filePath.substring(namePos + 1) : filePath;
   }
}
