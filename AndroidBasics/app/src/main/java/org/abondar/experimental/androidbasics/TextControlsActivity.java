package org.abondar.experimental.androidbasics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class TextControlsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv;
    private TextView tv1;
    private TextView tv2;
    private EditText et;
    private AutoCompleteTextView actv;
    private MultiAutoCompleteTextView mactv;

    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_controlls);


        tv = (TextView) this.findViewById(R.id.tv);

        et = (EditText) this.findViewById(R.id.et);


        tv1 = (TextView) this.findViewById(R.id.tv1);

        tv2 = (TextView) this.findViewById(R.id.tv2);


        actv = (AutoCompleteTextView) this.findViewById(R.id.actv);

        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"Java", "Go", "C++", "Python"});

        actv.setAdapter(aa);

        mactv = (MultiAutoCompleteTextView) this.findViewById(R.id.mactv);
        ArrayAdapter<String> aa1 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"BMW", "Audi", "Porsche", "Mercedes"});
        mactv.setAdapter(aa1);
        mactv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        submit = (Button) this.findViewById(R.id.submitButton);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (!et.getText().toString().isEmpty()) {
            tv.setText(et.getText().toString());
        }

        if (!actv.getText().toString().isEmpty()) {
            tv1.setText(actv.getText().toString());
        }
        if (!mactv.getText().toString().isEmpty()) {
            tv2.setText(mactv.getText().toString());
        }

    }

}
