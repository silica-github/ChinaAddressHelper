package addresshelper.kazumi.item;

/**
 * Created by KAZUMI on 2017-09-04 0004.
 */

public class TempAddressHelperItem {

    public String proviceName;
    public String cityName;
    public String districtName;

    public TempAddressHelperItem(String proviceName, String cityName, String districtName) {
        this.proviceName = proviceName;
        this.cityName = cityName;
        this.districtName = districtName;
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
}
