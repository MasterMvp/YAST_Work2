package attendance.yn.a606a.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import attendance.yn.a606a.R;

/**
 * Created by Administrator on 2017/5/2.
 */

public class Main2Adapter extends BaseAdapter {
    String[] title_res;
    int[] img_res;
    Context context;
    LayoutInflater inflater;

    public Main2Adapter(String[] title_res, int[] img_res, Context context) {
        this.title_res = title_res;
        this.img_res = img_res;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return img_res.length;
    }

    @Override
    public Object getItem(int position) {
        return img_res[position];
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
            v = inflater.inflate(R.layout.main2_item, null);
            vh.img = (ImageView) v.findViewById(R.id.main2_item_img);
            vh.tv = (TextView) v.findViewById(R.id.main2_item_tv);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        vh.img.setImageResource(img_res[position]);
        vh.tv.setText(title_res[position]);
        return v;
    }

    class ViewHolder {
        ImageView img;
        TextView tv;
    }
}
