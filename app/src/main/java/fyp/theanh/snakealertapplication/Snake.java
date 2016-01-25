package fyp.theanh.snakealertapplication;

import java.io.Serializable;

/**
 * Created by user on 12/24/2015.
 */
public class Snake implements Serializable {
    public String _name;
    public String _location;
    public String _specification;
    public String _firstAid;
    public double _lng;
    public double _lat;
    public String _imagePath;

    public Snake(String name, String location, String specification, String firstAid,
                 String imagePath, double lat, double lng) {
        _name = name;
        _location = location;
        _specification = specification;
        _firstAid = firstAid;
        _imagePath = imagePath;
        _lat = lat;
        _lng = lng;
    }

    public String getName() {
        return _name;
    }

    public String getLocation() {
        return _location;
    }

    public String getSpecification() {
        return _specification;
    }

    public String getFirstAid() {
        return _firstAid;
    }

    public String getImagePath() {
        return _imagePath;
    }

    public double getLat() {
        return _lat;
    }

    public double getLng() {
        return _lng;
    }
}
