## ChinaAddressHelper  
中国境内 (包含港澳台) 收货信息识别，支持省市区反查。  
  
## 使用  
[1] 将工程集成到你的工程中 (稍后上传 jcenter)。  
[2] 在你需要的地方：  
<pre>
LinkedList<AddressHelperItem> mData = AddressUtil.with(<要辨识的字符串>, Context);
</pre>
最终会返回 `LinkedList<AddressHelperItem>`，可从中获得：  
<pre>
姓名：　　 mData.get(i).getName()
手机号码： mData.get(i).getMobile()
省：　　　 mData.get(i).getProviceName()
市：　　　 mData.get(i).getCityName()
区：　　　 mData.get(i).getDistrictName()
详细地址： mData.get(i).getLocation()
</pre>

## 截图  
![ChinaAddressHelper](https://raw.githubusercontent.com/yuki-ryoko/ChinaAddressHelper/master/img/Screenshot_1504496415.png)  
  