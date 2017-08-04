package risks.yn.a606a.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.YinanSoft.Utils.ToastUtil;

import java.util.List;

import risks.yn.a606a.R;
import risks.yn.a606a.bean.RiskBean;

/**
 * Created by Administrator on 2017/6/3.
 */

public class QueryRiskAdapter extends BaseAdapter {

    private List<RiskBean> list;
    private Context context;
    private LayoutInflater inflater;

    public QueryRiskAdapter(List<RiskBean> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh = null;
        if (v == null) {
            vh = new ViewHolder();
            v = inflater.inflate(R.layout.act_query_item, null);
            vh.id = (TextView) v.findViewById(R.id.query_item_idd);
            vh.name = (TextView) v.findViewById(R.id.query_item_name);
            vh.cardNum = (TextView) v.findViewById(R.id.query_item_cardNum);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        RiskBean riskBean = list.get(position);
        String ids = riskBean.getId() + "";
        vh.id.setText(ids.trim());
        vh.name.setText(riskBean.getName().trim());
        vh.cardNum.setText(riskBean.getCardNum().trim());
        return v;
    }

    class ViewHolder {
        TextView id, name, cardNum;
    }


}
