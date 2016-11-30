package org.abondar.experimental.org.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText number1EditText;
    private EditText number2EditText;
    private EditText resultEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gatherControls();
        setupButtons();

    }


    @Override
    public void onClick(View view) {
        String sNum1 = number1EditText.getText().toString();
        String sNum2 = number2EditText.getText().toString();

        double num1 = getDouble(sNum1);
        double num2 = getDouble(sNum2);

        Button b = (Button) view;

        double value = 0;


        if (b.getId() == R.id.plusButton) {
            value = plus(num1, num2);
        } else if (b.getId() == R.id.minusButton) {
            value = minus(num1, num2);
        } else if (b.getId() == R.id.multiplyButton) {
            value = multiply(num1, num2);
        } else if (b.getId() == R.id.divideButton) {
            value = divide(num1, num2);
        }

        resultEditText.setText(Double.toString(value));

    }

    private void setupButtons() {
        Button b = (Button) this.findViewById(R.id.plusButton);
        b.setOnClickListener(this);

        Button b1 = (Button) this.findViewById(R.id.minusButton);
        b1.setOnClickListener(this);

        Button b2 = (Button) this.findViewById(R.id.multiplyButton);
        b2.setOnClickListener(this);

        Button b3 = (Button) this.findViewById(R.id.divideButton);
        b3.setOnClickListener(this);

    }

    private void gatherControls() {
        number1EditText = (EditText) this.findViewById(R.id.editText1);
        number2EditText = (EditText) this.findViewById(R.id.editText2);
        resultEditText = (EditText) this.findViewById(R.id.resultText);
        number2EditText.requestFocus();
    }


    private double getDouble(String s) {
        if (validString(s)) {
            return Double.parseDouble(s);
        }

        return 0;
    }

    private boolean validString(String s) {
        return s != null && !s.trim().equalsIgnoreCase("");
    }

    private boolean invalidString(String s){
        return !validString(s);
    }


    private double plus(double num1, double num2) {
        return num1 + num2;
    }

    private double minus(double num1, double num2) {
        return num1 - num2;
    }

    private double divide(double num1, double num2) {
        if (num2 == 0) {
            return 0;
        }
        return num1 / num2;
    }

    private double multiply(double num1, double num2) {
        return num1 * num2;
    }


}
