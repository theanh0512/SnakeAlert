package fyp.theanh.snakealertapplication.activities;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import fyp.theanh.snakealertapplication.R;


public class HelpActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_help, container, false);
            TextView textView = (TextView)rootView.findViewById(R.id.textView_help);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("1. Click ");
            builder.setSpan(new ImageSpan(getActivity(), R.drawable.capture_icon), builder.length() - 1, builder.length(), 0);
            builder.append(" if you found a snake.\n" +
                    "After taking picture, choose the kind of snake and upload it to server. If you don\'t" +
                    "know which snake it is, choose unknown picture.\n" +
                    "\n2. Click ");
            builder.setSpan(new ImageSpan(getActivity(), R.drawable.list_of_snakes_icon), builder.length() - 1, builder.length(), 0);
            builder.append(" to see the list of reported snakes.\n" +
                    " Click any snake to see it\'s special first aid as well as it\'s specification.\n\n3. Click ");
            builder.setSpan(new ImageSpan(getActivity(), R.drawable.map_icon), builder.length() - 1, builder.length(), 0);
            builder.append("to see the map of reported snakes.\n\n4. Click ");
            builder.setSpan(new ImageSpan(getActivity(), R.drawable.firstaid_icon), builder.length() - 1, builder.length(), 0);
            builder.append(" to see General first aid for snake bites.");
            textView.setText(builder);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(15);
            return rootView;
        }
    }
}
