package addresshelper.kazumi.item;

/**
 * Created by KAZUMI on 2017-09-04.
 */

public class AddressHelperItem {

    public String proviceName;
    public String cityName;
    public String districtName;
    public String location;
    public String mobile;
    public String name;

    public AddressHelperItem(String proviceName, String cityName, String districtName, String location, String mobile, String name) {
        this.proviceName = proviceName;
        this.cityName = cityName;
        this.districtName = districtName;
        this.location = location;
        this.mobile = mobile;
        this.name = name;
    }

    public String getProviceName() {
        return proviceName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public String getLocation() {
        return location;
    }

    public String getMobile() {
        return mobile;
    }

    public String getName() {
        return name;
    }
}
