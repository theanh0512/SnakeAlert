package fyp.theanh.snakealertapplication;

import android.widget.ArrayAdapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

import java.util.ArrayList;

/**
 * Created by Administrator PC on 3/11/2016.
 */
public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final List<String> __stringArray;
    public CustomList(Activity context,List<String> stringArray) {
        super(context, R.layout.activity_snake_listview, stringArray);
        this.context = context;
        this.__stringArray = stringArray;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.activity_snake_listview, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        String[] stringArray = separateString(__stringArray.get(position));
        String text = stringArray[0];
        String imagePath = stringArray[1];

        txtTitle.setText(text);
        Picasso.with(context).load(imagePath).resize(70,70).placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error_fallback).into(imageView);
        return rowView;
    }

    private String[] separateString(String string){
        String[] stringArray = string.trim().split("\\s*,\\s*");
        return stringArray;
    }
}
