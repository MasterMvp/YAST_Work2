package attendance.yn.a606a.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import attendance.yn.a606a.R;


/**
 * Created by sunjie on 2016/10/8.
 */
public class CustomDialog extends Dialog {
    private LayoutInflater layoutInflater;
    private View view;
    private Context context;
    private ClickListenerInterface clickListenerInterface;
    private EditText editText;
    private TextView textView;

    public CustomDialog(Context context) {
        super(context);
        CustomDialog.this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.custom_dialog, null);
        CustomDialog.this.setContentView(view);
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView = (TextView) view.findViewById(R.id.custom_dialog_title);
        textView.setText("请输入密码");
        editText = (EditText) view.findViewById(R.id.custom_dialog_home_number);
        editText.requestFocus();
        TextView textView = (TextView) view.findViewById(R.id.custom_dialog_upload);
        textView.setOnClickListener(new ClickListener());
        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(R.drawable.dialog_backgound_shape);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.8);
        dialogWindow.setAttributes(lp);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.custom_dialog_upload:
//                    String homeNumber = editText.getText().toString();
                    clickListenerInterface.upload(editText);
                    break;
            }
        }
    }

    public interface ClickListenerInterface {
        public void upload(EditText editText);
    }
}
