package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String url = "http://data.fixer.io/api/latest?access_key=bec84125172000c066a789f22e6021d1";

    TextView topTextView, bottomTextView;
    MaterialButton cancelButton, openBracketButton, closeBracketButton, clearAllButton, dotButton, equalsButton;
    MaterialButton divideButton, multiplyButton,addButton, subtractButton;
    MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    
    String[] available_currency = new String[]{"AED","AFN","ALL","AMD","ANG","AOA","ARS","AUD","AWG","AZN","BAM","BBD","BDT","BGN","BHD","BIF","BMD","BND","BOB","BRL","BSD","BTC","BTN","BWP","BYN","BYR","BZD","CAD","CDF","CHF","CLF","CLP","CNY","COP","CRC","CUC","CUP","CVE","CZK","DJF","DKK","DOP","DZD","EGP","ERN","ETB","EUR","FJD","FKP","GBP","GEL","GGP","GHS","GIP","GMD","GNF","GTQ","GYD","HKD","HNL","HRK","HTG","HUF","IDR","ILS","IMP","INR","IQD","IRR","ISK","JEP","JMD","JOD","JPY","KES","KGS","KHR","KMF","KPW","KRW","KWD","KYD","KZT","LAK","LBP","LKR","LRD","LSL","LTL","LVL","LYD","MAD","MDL","MGA","MKD","MMK","MNT","MOP","MRU","MUR","MVR","MWK","MXN","MYR","MZN","NAD","NGN","NIO","NOK","NPR","NZD","OMR","PAB","PEN","PGK","PHP","PKR","PLN","PYG","QAR","RON","RSD","RUB","RWF","SAR","SBD","SCR","SDG","SEK","SGD","SHP","SLE","SLL","SOS","SRD","STD","SVC","SYP","SZL","THB","TJS","TMT","TND","TOP","TRY","TTD","TWD","TZS","UAH","UGX","USD","UYU","UZS","VEF","VES","VND","VUV","WST","XAF","XAG","XAU","XCD","XDR","XOF","XPF","YER","ZAR","ZMK","ZMW","ZWL"
    };
    AutoCompleteTextView currency_list;
    ArrayAdapter<String> adapterItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestQueue request_queue = Volley.newRequestQueue(this);
        request_queue.add(makeRequest(url));

        currency_list = findViewById(R.id.currency_list);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, available_currency);
        currency_list.setAdapter(adapterItems);

        topTextView = findViewById(R.id.top_text);
        bottomTextView = findViewById(R.id.bottom_text);

        initButton(cancelButton, R.id.button_cancel);
        initButton(openBracketButton, R.id.button_open_bracket);
        initButton(closeBracketButton, R.id.button_close_bracket);
        initButton(clearAllButton, R.id.button_all_clear);
        initButton(dotButton, R.id.button_dot);
        initButton(equalsButton, R.id.button_equals);

        initButton(divideButton, R.id.button_divide);
        initButton(multiplyButton, R.id.button_multiply);
        initButton(addButton, R.id.button_add);
        initButton(subtractButton, R.id.button_subtract);

        initButton(button0, R.id.button_0);
        initButton(button1, R.id.button_1);
        initButton(button2, R.id.button_2);
        initButton(button3, R.id.button_3);
        initButton(button4, R.id.button_4);
        initButton(button5, R.id.button_5);
        initButton(button6, R.id.button_6);
        initButton(button7, R.id.button_7);
        initButton(button8, R.id.button_8);
        initButton(button9, R.id.button_9);

        currency_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MaterialTextView temp = (MaterialTextView) view;
                String currency_name = temp.getText().toString();

                String top_text = topTextView.getText().toString();
                double top_text_value;
                if(!top_text.isEmpty())
                    top_text_value = Double.parseDouble(top_text);
                else
                    return;

                String api_response = readFromFile("currency.json");
                try {
                    JSONObject all_currency_rates = new JSONObject(api_response);
                    double rate = all_currency_rates.getDouble(currency_name);
                    top_text_value *= rate;
                    topTextView.setText(Double.toString(top_text_value));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }


    @Override
    public void onClick(View v) {
        MaterialButton clicked_button = (MaterialButton) v;
        String button_text = clicked_button.getText().toString();
        bottomTextView.setText(button_text);
        String top_text = topTextView.getText().toString();


        if(button_text.equals("AC")){
            topTextView.setText("");
            bottomTextView.setText("0");
            return;
        }
        else if (button_text.equals("C") && !top_text.isEmpty()) {
            top_text = top_text.substring(0, top_text.length()-1);
        }
        else if(button_text.equals("=")){
            DoubleEvaluator eval = new DoubleEvaluator();
            Double result = eval.evaluate(top_text);

            if(!result.isNaN() && !result.isInfinite() && (result % 1) == 0)
                top_text = Integer.toString(result.intValue());
            else
                top_text = result.toString();
        }
        else if(!button_text.equals("C")){
            top_text += button_text;
        }
        topTextView.setText(top_text);
    }

    void initButton(MaterialButton btn, int id){
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    JsonObjectRequest makeRequest(String url){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
        new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject currency_obj = response.getJSONObject("rates");
                    Writer output;
                    String path = getApplicationContext().getFilesDir().toString() + "/currency.json";
                    File file = new File(path);
                    output = new BufferedWriter(new FileWriter(file));
                    output.write(currency_obj.toString());
                    output.close();

                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("error");
            }
        });

        return request;
    }

    String readFromFile(String file_name){
        File path = getApplicationContext().getFilesDir();
        File readFrom = new File(path, file_name);
        byte[] contents = new byte[(int) readFrom.length()];

        try{
            FileInputStream stream = new FileInputStream(readFrom);
            stream.read(contents);
            return new String(contents);
        }
        catch (IOException e){
            e.printStackTrace();
            return e.toString();
        }
    }
}