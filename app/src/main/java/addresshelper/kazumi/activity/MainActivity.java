package addresshelper.kazumi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.LinkedList;

import addresshelper.kazumi.R;
import addresshelper.kazumi.bean.LocationBean;
import addresshelper.kazumi.item.AddressHelperItem;
import addresshelper.kazumi.tool.AddressUtil;

public class MainActivity extends Activity {

    private EditText et_data;
    private Button btn_clear, btn_go;
    private TextView tv_result;

    private LocationBean locationBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_data = (EditText) findViewById(R.id.et_data);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_go = (Button) findViewById(R.id.btn_go);
        tv_result = (TextView) findViewById(R.id.tv_result);

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_data.setText("");
            }
        });

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv_result.setText("");

                LinkedList<AddressHelperItem> mData = AddressUtil.with(et_data.getText().toString().trim(), MainActivity.this);

                for (int i = 0; i < mData.size(); i++) {
                    tv_result.setText(tv_result.getText().toString().trim() + "\n\n第 " + i + "条数据:\n" +
                            "姓名: " + mData.get(i).getName() + "\n" +
                            "手机号码: " + mData.get(i).getMobile() + "\n" +
                            "省: " + mData.get(i).getProviceName() + "\n" +
                            "市: " + mData.get(i).getCityName() + "\n" +
                            "区: " + mData.get(i).getDistrictName() + "\n" +
                            "详细地址: " + mData.get(i).getLocation()
                    );
                }
            }
        });
    }
}
