package risks.yn.a606a.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import risks.yn.a606a.R;


public class SettingAct extends Activity {
    private Button add, query, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        switch (type) {
            case 1:
                setContentView(R.layout.qiyename);
                break;
            case 2:
                setContentView(R.layout.settingtime);
                break;
            case 3:
                setContentView(R.layout.update_pass);
                break;
        }

    }


}
