package risks.yn.a606a.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.YinanSoft.Utils.ToastUtil;

import java.util.List;

import risks.yn.a606a.MyApplication;
import risks.yn.a606a.R;
import risks.yn.a606a.adapter.QueryRiskAdapter;
import risks.yn.a606a.bean.RiskBean;

public class RiskJiLu extends AppCompatActivity {
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.risk_ji_lu);
        lv = (ListView) findViewById(R.id.msg_lv);
        final List<RiskBean> riskBeen = MyApplication.dbManager.selectRisk();
//        ToastUtil.showToast(this, riskBeen.toString());
        QueryRiskAdapter adapter = new QueryRiskAdapter(riskBeen, this);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String message = riskBeen.get(position).getMessage();
                Intent intent = new Intent(RiskJiLu.this, RiskMessageAct.class);
                intent.putExtra("message", message);
                startActivity(intent);
            }
        });
        
    }
}
