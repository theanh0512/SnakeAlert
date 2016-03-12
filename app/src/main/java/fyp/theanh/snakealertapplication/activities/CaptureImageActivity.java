package fyp.theanh.snakealertapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fyp.theanh.snakealertapplication.R;


public class CaptureImageActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static Uri imageUri;
    public static boolean called = false;
    public static String mCurrentPhotoPath;
    public static GoogleApiClient mGoogleApiClient;
    public static File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        } else {
            imageUri = Uri.parse(savedInstanceState.getString("image_uri"));
            mCurrentPhotoPath = savedInstanceState.getString("currentPhotoPath");
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (called) {
            outState.putString("image_uri", imageUri.toString());
            outState.putString("currentPhotoPath", mCurrentPhotoPath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_capture_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        Location mLastLocation;
        String lat, lng;
        Button photoButton, uploadButton;
        String location;

        ImageView imageView;
        boolean captured = false;

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            getActivity().setRequestedOrientation(
//                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_capture_image, container, false);
            this.imageView = (ImageView) rootView.findViewById(R.id.capturedImageView);
            if (imageUri != null) {
                imageView.setImageURI(null);
                imageView.setImageURI(imageUri);
            }
            ;
            photoButton = (Button) rootView.findViewById(R.id.button_capture);
            photoButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                    if (mLastLocation == null) {
                        openLocationSetting();
                    } else {
                        lat = String.valueOf(mLastLocation.getLatitude());
                        lng = String.valueOf(mLastLocation.getLongitude());
                        getLocationName();

                        captureImage();
                    }
                }
            });
            uploadButton = (Button) rootView.findViewById(R.id.button_upload);
            if (!captured) uploadButton.setEnabled(false);

            uploadButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UploadActivity.class);
                    intent.putExtra("photo path", mCurrentPhotoPath);
                    startActivity(intent);
                }
            });
            return rootView;
        }

        private void openLocationSetting() {
            Toast.makeText(getActivity(), "Location Disabled! Turn on location in " +
                            "location setting please!",
                    Toast.LENGTH_SHORT).show();
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }

        private void getLocationName() {
            Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {
                if (addresses.get(0).getLocality() != null)
                    location = addresses.get(0).getLocality();
                else location = addresses.get(0).getFeatureName();
                location = location.replaceAll("\\s+", "");
            }
        }

        private void captureImage() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Create the File where the photo should go
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, 100);
                }
            }
        }

        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("ddMMyy").format(new Date());
            String imageFileName = "_loc_" + location + "_l_" + lat + "_g_" + lng + "_t_" + timeStamp +
                    "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);

            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();
            Log.e("Getpath", "Cool" + mCurrentPhotoPath);
            return image;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 100 && resultCode == RESULT_OK) {
                setPic();
                captured = true;
                uploadButton.setEnabled(true);
            }
        }

        private void setPic() {
            called = true;
            // Get the dimensions of the View
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = 3;

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            //bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            bitmap = getUnRotatedImage(mCurrentPhotoPath, bitmap);
            imageUri = getImageUri(getActivity(), bitmap);
            //imageView.setImageBitmap(bitmap);
            imageView.setImageURI(null);
            imageView.setImageURI(imageUri);
        }

        public Bitmap rotateBitmap(Bitmap source, float angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }

        public final static Bitmap getUnRotatedImage(String imagePath, Bitmap rotatedBitmap) {
            int rotate = 0;
            try {
                File imageFile = new File(imagePath);
                ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            Bitmap newBitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(),
                    rotatedBitmap.getHeight(), matrix, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            return newBitmap;
        }

        public Uri getImageUri(Context inContext, Bitmap inImage) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                    inImage, "Title", null);
            return Uri.parse(path);
        }

    }
}