package addresshelper.kazumi.tool;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import addresshelper.kazumi.R;
import addresshelper.kazumi.bean.LocationBean;
import addresshelper.kazumi.item.AddressHelperItem;
import addresshelper.kazumi.item.TempAddressHelperItem;

/**
 * Created by KAZUMI on 2017-09-04.
 * ====
 * 本体.
 */

public class AddressUtil {

    private static final String TAG = "AddressUtil";
    private static String addressData = "";
    private static String provinceName = "";
    private static String cityName = "";
    private static String districtName = "";
    private static LocationBean locationBean;
    private static LinkedList<AddressHelperItem> addressHelperItems = new LinkedList<>();
    private static LinkedList<AddressHelperItem> nullAddressHelperItems = new LinkedList<>();
    private static LinkedList<TempAddressHelperItem> tempAddressHelperItems = new LinkedList<>();
    private static String phoneNumber = "";

    // 初始化
    public static LinkedList<AddressHelperItem> with(String data, Context context) {

        // 赋值
        addressData = data;
        addressHelperItems.clear();
        nullAddressHelperItems.clear();
        tempAddressHelperItems.clear();

        if (addressData.equals("") | null == addressData) {
            Log.d(TAG, "传入地址为 null 或为空字符串");
            return nullAddressHelperItems;
        }

        try {
            locationBean = new GsonBuilder().registerTypeAdapter(String.class,
                    new StringConverter()).create().fromJson(StreamUtil.get(context, R.raw.location), LocationBean.class);
        } catch (JsonSyntaxException e) {
            Log.d(TAG, "解析失败: " + e);
            return nullAddressHelperItems;
        }

        return findprovinceName();
    }

    // 寻找省份
    private static LinkedList<AddressHelperItem> findprovinceName() {

        boolean isProviceFound = false;
        int tempPosition = 0;

        for (int i = 0; i < locationBean.Provice.size(); i++) {

            // 遍历字符串, 检查是否有省份信息
            if (addressData.indexOf(locationBean.Provice.get(i).Name) >= 0) {

                // 赋予省变量
                provinceName = locationBean.Provice.get(i).Name;

                Log.d(TAG, "找到省: " + locationBean.Provice.get(i).Name);
                isProviceFound = true;
                tempPosition = i;
            }
        }

        if (!isProviceFound) {
            Log.d(TAG, "没有找到省");
            return findCityName(-1);
        } else {
            return findCityName(tempPosition);
        }
    }

    // 寻找市
    private static LinkedList<AddressHelperItem> findCityName(int provicePosition) {

        /**
         * provicePosition
         *
         * >= 0, 有省份数据
         * < 0, 没有省份数据, 需要来个循环
         */
        if (provicePosition >= 0) {

            boolean isFoundCity = false;
            int tempPosition = 0;

            for (int i = 0; i < locationBean.Provice.get(provicePosition).City.size(); i++) {

                // 遍历字符串, 检查是否有市信息
                if (addressData.indexOf(locationBean.Provice.get(provicePosition).City.get(i).Name) >= 0) {

                    // 赋予市变量
                    cityName = locationBean.Provice.get(provicePosition).City.get(i).Name;
                    Log.d(TAG, "找到市: " + cityName);

                    isFoundCity = true;
                    tempPosition = i;

                }
            }

            // 有省, 没市 (如广东、开平)
            if (!isFoundCity) {
                return findDistrictName(provicePosition, -1);
            } else {
                return findDistrictName(provicePosition, tempPosition);
            }

        } else {

            boolean isFoundCity = false;
            int tempI = 0;
            int tempJ = 0;

            for (int i = 0; i < locationBean.Provice.size(); i++) {

                // 赋予省变量
                provinceName = locationBean.Provice.get(i).Name;

                for (int j = 0; j < locationBean.Provice.get(i).City.size(); j++) {

                    // 遍历字符串, 检查是否有市信息
                    if (addressData.indexOf(locationBean.Provice.get(i).City.get(j).Name) >= 0) {

                        // 赋予市变量
                        cityName = locationBean.Provice.get(i).City.get(j).Name;

                        tempI = i;
                        tempJ = j;
                        isFoundCity = true;
                    }
                }
            }

            if (!isFoundCity) {
                return findDistrictName(-1, -1);
            } else {
                return findDistrictName(tempI, tempJ);
            }
        }
    }

    // 寻找区
    private static LinkedList<AddressHelperItem> findDistrictName(int provicePosition, int cityPosition) {

        // 省、市两者皆有 (如广东、江门)
        if (provicePosition >= 0 && cityPosition >= 0) {
            for (int i = 0; i < locationBean.Provice.get(provicePosition).City.get(cityPosition).Region.size(); i++) {

                // 遍历字符串, 检查是否有市信息
                if (addressData.indexOf(locationBean.Provice.get(provicePosition).City.get(cityPosition).Region.get(i).Name) >= 0) {

                    districtName = locationBean.Provice.get(provicePosition).City.get(cityPosition).Region.get(i).Name;

                    Log.d(TAG, "找到市: " + districtName);

                    tempAddressHelperItems.add(new TempAddressHelperItem(provinceName, cityName, districtName));
                }
            }

        }

        // 有省, 没有市 (县级市, 或自以为是市. 如广东、开平 (开平算个屁市, 就人多))
        else if (provicePosition >= 0 && cityPosition < 0) {

            // 检索省下的所有区
            for (int i = 0; i < locationBean.Provice.get(provicePosition).City.size(); i++) {

                for (int j = 0; j < locationBean.Provice.get(provicePosition).City.get(i).Region.size(); j++) {

                    if (addressData.indexOf(locationBean.Provice.get(provicePosition).City.get(i).Region.get(j).Name) >= 0) {

                        cityName = locationBean.Provice.get(provicePosition).City.get(i).Name;
                        districtName = locationBean.Provice.get(provicePosition).City.get(i).Region.get(j).Name;

                        tempAddressHelperItems.add(new TempAddressHelperItem(provinceName, cityName, districtName));
                    }
                }
            }
        }

        // 没有省, 也没有市, 专治同城客户发地址 (比如: "发到 蓬江区甘化路", 前面不附省市, 但是准确率很低, 太多同名的了, 特别是东北地区一堆东西南北区)
        else if (provicePosition < 0 && cityPosition < 0) {

            // 检索省
            for (int i = 0; i < locationBean.Provice.size(); i++) {

                // 检索市
                for (int j = 0; j < locationBean.Provice.get(i).City.size(); j++) {

                    // 检索区
                    for (int k = 0; k < locationBean.Provice.get(i).City.get(j).Region.size(); k++) {


                        if (addressData.indexOf(locationBean.Provice.get(i).City.get(j).Region.get(k).Name) >= 0) {

                            provinceName = locationBean.Provice.get(i).Name;
                            cityName = locationBean.Provice.get(i).City.get(j).Name;
                            districtName = locationBean.Provice.get(i).City.get(j).Region.get(k).Name;

                            tempAddressHelperItems.add(new TempAddressHelperItem(provinceName, cityName, districtName));
                        }
                    }
                }
            }
        }

        return getAllItem();
    }

    // 显示最终结果
    private static LinkedList<AddressHelperItem> getAllItem() {
        Log.d(TAG, "最终结果↓ ");
        Log.d(TAG, "====================");
        Log.d(TAG, "总共找到: " + tempAddressHelperItems.size());

        // 移除省市区
        for (int i = 0; i < tempAddressHelperItems.size(); i++) {

            // 从数据中移除省
            if (addressData.indexOf(tempAddressHelperItems.get(i).getProviceName() + "省") >= 0) {
                addressData = addressData.replace(tempAddressHelperItems.get(i).getProviceName() + "省", "");
            } else {
                addressData = addressData.replace(tempAddressHelperItems.get(i).getProviceName(), "");
            }

            // 从数据中移除市
            if (addressData.indexOf(tempAddressHelperItems.get(i).getCityName() + "市") >= 0) {
                addressData = addressData.replace(tempAddressHelperItems.get(i).getCityName() + "市", "");
            } else {
                addressData = addressData.replace(tempAddressHelperItems.get(i).getCityName(), "");
            }

            // 从数据中移除区
            if (addressData.indexOf(tempAddressHelperItems.get(i).getDistrictName() + "区") >= 0) {
                addressData = addressData.replace(tempAddressHelperItems.get(i).getDistrictName() + "区", "");
            } else if (addressData.indexOf(tempAddressHelperItems.get(i).getDistrictName() + "县") >= 0) {
                addressData = addressData.replace(tempAddressHelperItems.get(i).getDistrictName() + "县", "");
            } else if (addressData.indexOf(tempAddressHelperItems.get(i).getDistrictName() + "市") >= 0) {           // 针对县级市, 如开平
                addressData = addressData.replace(tempAddressHelperItems.get(i).getDistrictName() + "市", "");
            } else {
                addressData = addressData.replace(tempAddressHelperItems.get(i).getDistrictName(), "");
            }

            // 输出剩余字符串
            Log.d(TAG, "剩余字符串: " + addressData);
        }

        // 识别手机号码
        return findMobile();
    }

    // 识别手机号码
    private static LinkedList<AddressHelperItem> findMobile() {

        String tempData = "";

        Pattern pattern = Pattern.compile("1(3[0-9]|4[57]|5[0-35-9]|7[01678]|8[0-9])\\d{8}");
        final Matcher matcher = pattern.matcher(addressData);

        while (matcher.find()) {

            tempData = matcher.group(0) + "";

            final String tempMobile = tempData;

            phoneNumber = tempMobile;

            // 从字符串中移除手机号码
            addressData = addressData.replace(tempData, "");
        }

        Log.d(TAG, "移除手机号码后: " + addressData);

        // 识别身份证号码
        return findCardId();
    }

    // 识别身份证号码
    private static LinkedList<AddressHelperItem> findCardId() {

        Pattern pattern = Pattern.compile("[1-9][0-9]\\d{15}[0-9]|[1-9][0-9]\\d{15}X");
        Matcher matcher = pattern.matcher(addressData);

        String temp = "";

        while (matcher.find()) {
            temp = matcher.group(0);
            // 从字符串中移除身份证号码
            addressData = addressData.replace(temp, "");
        }

//        if (temp.length() == 15){
//            item_ temp;
//        } else if (temp.length() == 18){
//            return temp;
//        } else {
//            return "";
//        }

        Log.d(TAG, "移除身份证号码后: " + addressData);

        return findLocalAndName();
    }

    // 识别详细地址与收件人
    private static LinkedList<AddressHelperItem> findLocalAndName() {

        /**
         * 检查空格个数
         *
         * > 1, 不用作分隔符
         * <= 1, 用做分隔符
         */

        int num = 0;
        for (int i = 0; i < addressData.length(); i++) {
            if (addressData.substring(i, (i + 1)).indexOf(' ') != -1) {
                num += 1;
            }
        }

        Log.d(TAG, "空格数量: " + num);

        if (num == 1) {
            Log.d(TAG, "使用 空格 作为分隔符");
            return splitData(" ");
        } else {
            /**
             * 寻找其它分隔符号
             */
            // 寻找「,」「，」, 统一合并成「，」
            if (addressData.indexOf(",") >= 0 | addressData.indexOf("，") >= 0) {

                addressData = addressData.replace(",", "，");
                Log.d(TAG, "使用 ， 作为分隔符");
                return splitData("，");
            }

            // 寻找「.」「。」，统一合并成「。」
            else if (addressData.indexOf(".") >= 0 | addressData.indexOf("。") >= 0) {
                addressData = addressData.replace(".", "。");
                Log.d(TAG, "使用 。 作为分隔符");
                return splitData("。");
            }

            // 寻找「;」「；」，统一合并成「；」
            else if (addressData.indexOf(";") >= 0 | addressData.indexOf("；") >= 0) {
                addressData = addressData.replace(";", "；");
                Log.d(TAG, "使用 ； 作为分隔符");
                return splitData("；");
            } else {
                return nullAddressHelperItems;
            }
        }
    }

    // 分隔成两个字符串
    private static LinkedList<AddressHelperItem> splitData(String splitSymbol) {

        int maxSplit = 2;
        final String[] sourceStrArray = addressData.split(splitSymbol, maxSplit);
        LinkedList<AddressHelperItem> tempItems = new LinkedList<>();

        Log.d(TAG, "splitData: " + sourceStrArray.length);

        for (int i = 0; i < sourceStrArray.length; i++) {
            if (sourceStrArray.length >= 2) {
                Log.d(TAG, "详细地址与收件人识别结果: " + sourceStrArray[i] + "");

                if (sourceStrArray[0].length() > sourceStrArray[1].length()) {
                    for (int j = 0; j < tempAddressHelperItems.size(); j++) {
                        addressHelperItems.add(new AddressHelperItem(
                                tempAddressHelperItems.get(j).getProviceName(),
                                tempAddressHelperItems.get(j).getCityName(),
                                tempAddressHelperItems.get(j).getDistrictName(),
                                removeRedundantAddressString(sourceStrArray[0] + ""),
                                phoneNumber,
                                removeRedundantNameString(sourceStrArray[1] + "")
                        ));
                    }
                    tempItems = addressHelperItems;
                    return addressHelperItems;
                } else {
                    for (int j = 0; j < tempAddressHelperItems.size(); j++) {
                        addressHelperItems.add(new AddressHelperItem(
                                tempAddressHelperItems.get(j).getProviceName(),
                                tempAddressHelperItems.get(j).getCityName(),
                                tempAddressHelperItems.get(j).getDistrictName(),
                                removeRedundantAddressString(sourceStrArray[1] + ""),
                                phoneNumber,
                                removeRedundantNameString(sourceStrArray[0] + "")
                        ));
                    }
                    tempItems = addressHelperItems;
                    return addressHelperItems;
                }
            } else {
                Log.d(TAG, "splitData: " + sourceStrArray[0]);
            }
        }

        return tempItems;
    }

    // 移除收货地址多余数据

    private static String removeRedundantAddressString(String str) {
        Log.d(TAG, "处理前: " + str);
        String temp = str
                .replace(":", "")
                .replace("：", "")
                .replace(" ", "")
                .replace("收货地址", "")
                .replace("送货地址", "")
                .replace("送货", "")
                .replace("收货", "")
                .replace("收", "")
                .replace("送到", "")
                .replace("发到", "")
                .replace("运到", "")
                .replace("地址", "")
                .replace("到", "")
                .replace("运", "")
                .replace("发", "");
        Log.d(TAG, "处理后: " + temp);
        return temp;
    }

    // 移除收件人多余数据
    private static String removeRedundantNameString(String str) {
        return str
                .replace("收", "")
                .replace("接", "")
                .replace("件", "")
                .replace(":", "")
                .replace("：", "")
                .replace(" ", "")
                .replace(";", "")
                .replace("；", "")
                .replace("。", "")
                .replace(".", "")
                .replace(",", "")
                .replace("，", "")
                ;
    }
}
