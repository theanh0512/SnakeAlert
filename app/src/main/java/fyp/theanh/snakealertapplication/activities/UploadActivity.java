package fyp.theanh.snakealertapplication.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fyp.theanh.snakealertapplication.R;
import fyp.theanh.snakealertapplication.models.SnakeKind;


public class UploadActivity extends ActionBarActivity {
    public static List<SnakeKind> snakeKinds = new ArrayList<SnakeKind>();
    public static ImageAdapter mSnakeKindImageAdapter;
    public String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mCurrentPhotoPath = bundle.getString("photo path");
        }

    }

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setmCurrentPhotoPath(String newPath) {
        mCurrentPhotoPath = newPath;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        public GridView gridview;
        boolean uploaded = false;
        File newFile;
        Button uploadButton;
        String ba1;
        int serverResponseCode = 0;
        ProgressDialog dialog = null;
        String snakeKind = null;
        public static String upload_url = "http://www.snakealertapp.com/upload_image.php";
        Bitmap myBitmap;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_upload, container, false);
            uploadButton = (Button) rootView.findViewById(R.id.button_upload2);
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        copy(((UploadActivity) getActivity()).getmCurrentPhotoPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (newFile.exists()) {
                        ((UploadActivity) getActivity()).setmCurrentPhotoPath(newFile
                                .getAbsolutePath());
                    }
                    if (snakeKind != null) {
                        dialog = ProgressDialog.show(getActivity(), "", "Uploading file...", true);
                        new Thread(new Runnable() {
                            public void run() {
                                uploadFile(((UploadActivity) getActivity()).getmCurrentPhotoPath());
                            }
                        }).start();
                    } else {
                        Toast.makeText(getActivity(), "Please select the kind of the snake you " +
                                        "took picture. If you don't know, select unknown picture",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            gridview = (GridView) rootView.findViewById(R.id.gridView);
            mSnakeKindImageAdapter = new ImageAdapter(getActivity());
            gridview.setAdapter(mSnakeKindImageAdapter);
            gridview.setDrawSelectorOnTop(false);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    snakeKind = snakeKinds.get(position).getName();
                }
            });
            return rootView;
        }

        public void copy(String path) throws IOException {
            String currentName = path.substring(path.lastIndexOf("/") + 1);
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File srcFile = new File(storageDir, currentName);
            if (srcFile.exists()) {
                myBitmap = BitmapFactory.decodeFile(srcFile.getAbsolutePath());
                myBitmap = CaptureImageActivity.PlaceholderFragment.getUnRotatedImage(srcFile
                        .getAbsolutePath(), myBitmap);
            }
            newFile = new File(storageDir, "k_" + snakeKind + currentName);
//            InputStream in = new FileInputStream(srcFile);
//            OutputStream out = new FileOutputStream(newFile);
//
//            // Transfer bytes from in to out
//            byte[] buf = new byte[1024];
//            int len;
//            while ((len = in.read(buf)) > 0) {
//                out.write(buf, 0, len);
//            }
//            in.close();
//            out.close();
//            //create a file to write bitmap data

            //Convert bitmap to byte array
            Bitmap bitmap = myBitmap;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }


        @Override
        public void onStart() {
            super.onStart();
        }

        public int uploadFile(String sourceFileUri) {

            String fileName = sourceFileUri;
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = newFile;

            if (!sourceFile.isFile()) {
                dialog.dismiss();
                Log.e("uploadFile", "Source File not exist :" + ((UploadActivity) getActivity())
                        .getmCurrentPhotoPath());
                return 0;
            } else {
                try {

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upload_url);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=" + fileName + "" + lineEnd);
                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                    if (serverResponseCode == 200) {

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {

                                Toast.makeText(getActivity(), "File Upload Complete.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            uploadButton.setEnabled(false);
                            Intent intent = new Intent(getActivity(), ListOfSnakesActivity.class);
                            startActivity(intent);
                        }
                    });


                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (MalformedURLException ex) {

                    dialog.dismiss();
                    ex.printStackTrace();

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(getActivity(), "MalformedURLException",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {

                    dialog.dismiss();
                    e.printStackTrace();

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(getActivity(), "Got Exception : see logcat ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("Error Uploading", "Exception : "
                            + e.getMessage(), e);
                }
                dialog.dismiss();
                return serverResponseCode;

            } // End else block
        }


    }

    static class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private String imagePath;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        //to do: count number of snakes
        public int getCount() {
            return snakeKinds.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(320, 320));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

//            imageView.setImageResource(snakeKinds.get(position).getImagePath());
            if (snakeKinds.size() != 0) {
                imagePath = snakeKinds.get(position).getImagePath();
                Picasso.with(mContext).load(imagePath).placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error_fallback).into(imageView);
            }
            return imageView;
        }
    }
}
