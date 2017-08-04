package risks.yn.a606a.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import risks.yn.a606a.R;
import risks.yn.a606a.bean.RecordBean;


/**
 * Created by Administrator on 2017/4/18.
 */

public class QueryRecordAdapter extends BaseAdapter {
    private List<RecordBean> list;
    private Context context;
    private LayoutInflater inflater;

    public QueryRecordAdapter(List<RecordBean> list, Context mContext) {
        this.list = list;
        context = mContext;
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
        RecordBean userBean = list.get(position);

//        Log.i("Log", "_id=" + userBean.getId() + ",name=" + userBean.getName() + ",cardNum=" + userBean.getCardNum() + "\n");
        vh.id.setText(userBean.getId() + "");
        vh.name.setText(userBean.getName());
        vh.cardNum.setText(userBean.getTime());
        return v;
    }

    class ViewHolder {
        TextView id, name, cardNum;
    }
}
