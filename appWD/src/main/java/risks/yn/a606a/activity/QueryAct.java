package risks.yn.a606a.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.YinanSoft.Utils.ToastUtil;

import java.util.List;

import risks.yn.a606a.MyApplication;
import risks.yn.a606a.R;
import risks.yn.a606a.Utils.Excel;
import risks.yn.a606a.adapter.QueryRecordAdapter;
import risks.yn.a606a.adapter.QueryUserAdapter;
import risks.yn.a606a.bean.RecordBean;
import risks.yn.a606a.bean.UserBean;


/**
 * Created by Administrator on 2017/4/18.
 */

public class QueryAct extends Activity {
    private ListView lv;
    private TextView total;
    private TextView cardNum;
    private Button outexcel;
    private List<UserBean> userBeanList;
    private QueryUserAdapter queryUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_query);
        lv = (ListView) findViewById(R.id.query_lv);
        total = (TextView) findViewById(R.id.query_total);
        cardNum = (TextView) findViewById(R.id.query_cardnum);
        outexcel = (Button) findViewById(R.id.query_outexcel);
        Intent intent = getIntent();
        String request = intent.getStringExtra("request");
        if (request != null && request.equals("1")) {
            outexcel.setVisibility(View.VISIBLE);
            cardNum.setText("身份证号码");
            userBeanList = MyApplication.dbManager.selectUser();
            queryUserAdapter = new QueryUserAdapter(userBeanList, this);
            lv.setAdapter(queryUserAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent1 = new Intent(QueryAct.this, PersonAct.class);
                    intent1.putExtra("position", position);
                    startActivity(intent1);
                }
            });
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(QueryAct.this);
                    builder.setMessage("确认删除吗？");
                    builder.setTitle("提示");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            arg0.dismiss();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            MyApplication.dbManager.del(userBeanList.get(position).getId());
                            userBeanList.remove(position);
                            queryUserAdapter.notifyDataSetChanged();
                            total.setText("共计: " + userBeanList.size() + " 人");
                            arg0.dismiss();
                            ToastUtil.showToast(QueryAct.this, "删除成功。");
                        }
                    });
                    builder.create().show();

                    return true;// 这里一定要改为true，代表长按自己消费掉了，若为false，触发长按事件的同时，还会触发点击事件
                }
            });
            outexcel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Excel excel = new Excel(QueryAct.this);
                    excel.initUserData();
                }
            });

            total.setText("共计: " + userBeanList.size() + " 人");
        } else if (request != null && request.equals("2")) {
            outexcel.setVisibility(View.VISIBLE);
            cardNum.setText("签到时间");
            List<RecordBean> list = MyApplication.dbManager.selectRecord();
            QueryRecordAdapter adapter = new QueryRecordAdapter(list, this);
            lv.setAdapter(adapter);

            total.setText("共计: " + list.size() + " 次");
            outexcel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Excel excel = new Excel(QueryAct.this);
                    excel.initData();
                }
            });
        }

    }
}
