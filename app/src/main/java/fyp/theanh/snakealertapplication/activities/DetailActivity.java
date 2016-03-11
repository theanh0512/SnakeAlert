package fyp.theanh.snakealertapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import fyp.theanh.snakealertapplication.R;
import fyp.theanh.snakealertapplication.models.Snake;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
    public static class DetailFragment extends Fragment {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private Snake mSnake;
        private String imagePath;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onStart() {
//            new DownloadImageTask((ImageView) getActivity().findViewById(R.id.detail_image)).execute(imagePath);
            super.onStart();
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_detail, menu);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if (intent != null && intent.hasExtra("Snake")) {
                mSnake = (Snake) intent.getSerializableExtra("Snake");
                ((TextView) rootView.findViewById(R.id.detail_name)).setText(separateName(mSnake.getName()));
                ((TextView) rootView.findViewById(R.id.detail_location)).setText("Found at: " + mSnake
                        .getLocation());
                ((TextView) rootView.findViewById(R.id.detail_specification)).setText
                        (mSnake.getSpecification() + "\n");
                ((TextView) rootView.findViewById(R.id.detail_firstAid)).setText(Html
                        .fromHtml(mSnake.getFirstAid()));
                ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_image);
                imagePath = mSnake.getImagePath();
                Picasso.with(getActivity()).load(imagePath).resize(600,
                        800).onlyScaleDown().centerInside().placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error_fallback).into(imageView);
            }
            return rootView;
        }

        private String separateName(String name){
            String nameSeparated = name.replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
            return nameSeparated;
        }

//        private Intent createShareForecastIntent() {
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
//            shareIntent.setType("text/plain");
//            return shareIntent;
//        }

//        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//            ImageView bmImage;
//
//            public DownloadImageTask(ImageView bmImage) {
//                this.bmImage = bmImage;
//            }
//
//            protected Bitmap doInBackground(String... urls) {
//                String urldisplay = urls[0];
//                Bitmap mIcon11 = null;
//                try {
//                    InputStream in = new java.net.URL(urldisplay).openStream();
//                    mIcon11 = BitmapFactory.decodeStream(in);
//                } catch (Exception e) {
//                    Log.e("Error", e.getMessage());
//                    e.printStackTrace();
//                }
//                return mIcon11;
//            }
//
//            protected void onPostExecute(Bitmap result) {
//                bmImage.setImageBitmap(result);
//            }
//        }
    }
}
