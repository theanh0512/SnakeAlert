package fyp.theanh.snakealertapplication.models;

import java.io.Serializable;

/**
 * Created by user on 1/10/2016.
 */
public class SnakeKind implements Serializable {
    public String _name;
    public String _specification;
    public String _firstAid;
    public String _imagePath;

    public SnakeKind(String name, String specification, String firstAid, String imagePath) {
        _name = name;
        _specification = specification;
        _firstAid = firstAid;
        _imagePath = imagePath;
    }

    public String getName() {
        return _name;
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
}
