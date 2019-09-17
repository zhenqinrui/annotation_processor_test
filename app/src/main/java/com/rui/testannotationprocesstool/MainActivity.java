package com.rui.testannotationprocesstool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.rui.apt_annotation.BindView;
import com.rui.library.BindViewTools;


public class MainActivity extends AppCompatActivity {

    @BindView(id = R.id.tv)
    TextView tv;

    @BindView(id = R.id.btn)
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindViewTools.bind(this);
        Log.i("zqr", "tv=" + tv + ",btn=" + btn);
    }
}
