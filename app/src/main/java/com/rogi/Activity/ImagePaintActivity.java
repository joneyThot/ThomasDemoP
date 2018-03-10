package com.rogi.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rogi.R;
import com.rogi.View.CustomImageView;
import com.rogi.View.DrawableView;
import com.rogi.View.Utils;
import com.rogi.logger.Logger;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImagePaintActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "ImagePaintActivity";
    private SharedPreferences mSharedPreferences;
    private DrawableView drawbleView;
    private CustomImageView touchImageView;
    private ImageButton currPaint;
    private ImageView new_btn, erase_btn, draw_btn, save_btn;
    TextView titleText;
    LinearLayout linPaintColors;
    String selectedColor, TOKEN = "", USERID = "", imgURL = "", media_id = "";
    private int mDeviceWidth = 480;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_image);

        mSharedPreferences = getSharedPreferences(Utils.PREF_NAME, MODE_PRIVATE);
        USERID = mSharedPreferences.getString(Utils.USER_ID, "");
        TOKEN = mSharedPreferences.getString(Utils.TOKEN, "");

        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            mDeviceWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            mDeviceWidth = d.getWidth();
        }

        init();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            imgURL = bundle.getString(Utils.MEDIA_IMAGE_URL);
            media_id = bundle.getString(Utils.MEDIA_ID);
            File file = new File(imgURL.toString());
            if (file.exists()) {
                touchImageView.setImageURI(Uri.parse(imgURL));
            } else {
                if (Utils.validateString(imgURL)) {
                    Transformation transformation = new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            int targetWidth = mDeviceWidth;

                            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                            int targetHeight = (int) (targetWidth * aspectRatio);
                            if (targetHeight > targetWidth) {
                                targetHeight = targetWidth;
                            }
                            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                            if (result != source) {
                                source.recycle();
                            }
                            return result;
                        }

                        @Override
                        public String key() {
                            return "transformation" + " desiredWidth";
                        }
                    };

                    Picasso.with(ImagePaintActivity.this)
                            .load(imgURL)
                            .placeholder(R.mipmap.default_image)
                            .error(R.mipmap.default_image)
                            .transform(transformation)
                            .centerCrop()
                            .resize(mDeviceWidth, (int) (mDeviceWidth))
                            .into(touchImageView);
                }
            }
        }
    }

    private void init() {
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("Media Details");

        drawbleView = (DrawableView) findViewById(R.id.drawble_view);
        drawbleView.setDrawingEnabled(false);

        touchImageView = (CustomImageView) findViewById(R.id.zoom_iv);

        new_btn = (ImageView) findViewById(R.id.new_btn);
        new_btn.setOnClickListener(this);

        erase_btn = (ImageView) findViewById(R.id.erase_btn);
        erase_btn.setOnClickListener(this);

        draw_btn = (ImageView) findViewById(R.id.draw_btn);
        draw_btn.setOnClickListener(this);

        save_btn = (ImageView) findViewById(R.id.save_btn);
        save_btn.setOnClickListener(this);

        linPaintColors = (LinearLayout) findViewById(R.id.linPaintColors);
        currPaint = (ImageButton) linPaintColors.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        touchImageView.setZoomEnable(false);
        drawbleView.setDrawingEnabled(true);
        findViewById(R.id.backLayoutclick).setOnClickListener(this);
    }

    String canvasImagePath;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
             /*case R.id.enable_zoom:
               if (enableZoomBtn.getText().equals("disable zoom")) {
                    touchImageView.setZoomEnable(false);
                    drawbleView.setDrawingEnabled(true);
                    enableZoomBtn.setText("enable zoom");
                } else {
                    touchImageView.setZoomEnable(true);
                    drawbleView.setDrawingEnabled(false);
                    enableZoomBtn.setText("disable zoom");
                }
                break;*/

            case R.id.draw_btn:
                if (Utils.validateString(selectedColor)) {
                    drawbleView.setColor(selectedColor);
                    drawbleView.setErase(false);
                    drawbleView.setBrushSize(5);
                    drawbleView.setLastBrushSize(5);
                } else {
                    drawbleView.setErase(false);
                    drawbleView.setBrushSize(5);
                    drawbleView.setLastBrushSize(5);
                }
                break;

            case R.id.new_btn:
                //new button
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle(R.string.alert);
                newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        drawbleView.startNew();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newDialog.show();
                break;

            case R.id.erase_btn:
                drawbleView.setErase(true);
                drawbleView.setBrushSize(15);
                break;

            case R.id.save_btn:
                //save drawing
                if (ActivityCompat.checkSelfPermission(ImagePaintActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(ImagePaintActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ImagePaintActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    if (Utils.validateString(imgURL)) {
                        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                        saveDialog.setTitle(R.string.alert);
                        saveDialog.setMessage("Are you sure want to upload edited image ?");
                        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                drawbleView.setDrawingCacheEnabled(true);
                                touchImageView.setDrawingCacheEnabled(true);

                                Bitmap bmap = overlay(touchImageView.getDrawingCache(), drawbleView.getDrawingCache());
                                storeImage(bmap);
                                drawbleView.destroyDrawingCache();
                                touchImageView.destroyDrawingCache();

                            }
                        });
                        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        saveDialog.show();
                    } else {
                        Utils.showMessageDialog(ImagePaintActivity.this, getString(R.string.alert), "Please check image is not available.");
                    }

                }
                break;

            case R.id.backLayoutclick:
                finish();
                break;

            default:
                break;
        }
    }

    private void storeImage(Bitmap image) {
        File file = new File(Environment.getExternalStorageDirectory() + "/Media/Image/", media_id + ".jpg");
        if (file == null) {
            Logger.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            canvasImagePath = Environment.getExternalStorageDirectory() + "/Media/Image/" + media_id + ".jpg";
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Utils.SWITCH, 0);
            returnIntent.putExtra(Utils.MEDIA_EDITED_IMAGE_PATH, canvasImagePath);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } catch (FileNotFoundException e) {
            Logger.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Logger.d(TAG, "Error accessing file: " + e.getMessage());
        }

    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {

                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                touchImageView.setImageURI(Uri.parse(picturePath));
                Log.e("MainActivity", "picturepath--" + picturePath + "");

            } catch (Exception e) {
                Log.v("ERROR", e.toString());
            }
        }

    }*/

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    //user clicked paint
    public void paintClicked(View view) {
        //use chosen color

        //set erase false
        drawbleView.setErase(false);
        drawbleView.setPaintAlpha(100);
        drawbleView.setBrushSize(5);

        if (view != currPaint) {
            ImageButton imgView = (ImageButton) view;
            selectedColor = view.getTag().toString();
            drawbleView.setColor(selectedColor);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton) view;
        }
    }

}
