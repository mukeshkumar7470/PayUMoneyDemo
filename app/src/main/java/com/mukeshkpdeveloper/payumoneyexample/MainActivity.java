package com.mukeshkpdeveloper.payumoneyexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    PayUmoneySdkInitializer.PaymentParam paymentParam;
    String hash = "";
    String transaction_id ="";
    public static RequestQueue mRequestQue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRequestQue = Volley.newRequestQueue(MainActivity.this);
        callingApiForHash("10");

        TextView text = findViewById(R.id.text);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payNow("10");
            }
        });


    }

    private void callingApiForHash(String amount) {
        Log.e("TAG", "callingApiForHash: yhea" );
        StringRequest request = new StringRequest(Request.Method.POST, "API_URL", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("TAG", "addAmount: " + response);
                    JSONObject rootObject = new JSONObject(response);
                    JSONObject order = rootObject.getJSONObject("order");
                    transaction_id = order.getString("transaction_id");
                    //  showByPlanDialog(transaction_id, order.getString("amount"), order.getString("hash"), order.getString("gst_percent"), order.getString("gst"), amount, order);

                    hash =  order.getString("hash");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "addAmount: Error");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + "mAN0zgrqjVeZjMv2bmXp9wyNFQ6y1Zd25vyixz3qcMEZXG3geDDgfK9HD1N1whQKlYbhWIjaRlU55Sz57JqRDxHWVsRLSjIAC3x0");
                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("amount", MyUtils.changeAmount(amount + ""));
                params.put("this_type", "request");
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mRequestQue.add(request);
    }

    private void payNow(String amount) {//String payment_id, String amount, String hash
        String mobile = "7803994667";
        String name = "Mukesh_kumar Patel";
        String email1 = "kumarmukeshpatel57@gmail.com";
       // String api = MyApplication.sharedPreferences.getKey(SPCsnstants.login_token);
       // pre_hash = hash;
        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        String hashSequence = "merchant key" + "|" + transaction_id + "|" + amount + "|" + "Demo" + "|" + name
                + "|" + email1 + "|" + "udf1" + "|" + "udf2" + "|" + "udf3" + "|" + "udf4" + "|" + "udf5" + "||||||Salt";
        String hashFromAndroid = MyUtils.hashCal("sha512", hashSequence);
        Log.e("TAG", "hashFromAndroid: " + hashFromAndroid);
        builder.setAmount(amount)
                .setTxnId(transaction_id)
                .setPhone(mobile)
                .setProductName("Travier")
                .setFirstName(name)
                .setEmail(email1)
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")
                .setUdf1("udf1")
                .setUdf2("udf2")
                .setUdf3("udf3")
                .setUdf4("udf4")
                .setUdf5("udf5")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(true) // Integration environment - true (Debug)/ false(Production)
                .setKey("merchant key") //here set merchant key
                .setMerchantId("merchant id"); ////here set merchant ID
        try {
            paymentParam = builder.build();
            paymentParam.setMerchantHash(hash);
            Log.e("TAG", "payNow: "+hash );
            Log.e("TAG", "payNow: "+hashFromAndroid );
          //  paymentParam.setMerchantHash(hashFromAndroid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, MainActivity.this, R.style.PayumoneyAppTheme1, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    Toast.makeText(this, transactionResponse.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("TAG", "onActivityResult2: " + transactionResponse.getPayuResponse());

                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
                    //Failed Transaction
                    Log.e("TAG", "onActivityResult1: F" + transactionResponse.getMessage());
                    Log.e("TAG", "onActivityResult2: F" + transactionResponse.getPayuResponse());
                    Log.e("TAG", "onActivityResult3: F" + transactionResponse.getTransactionDetails());
                    Log.e("TAG", "onActivityResult4: F" + transactionResponse.getTransactionStatus());
                  //  paymentConfirmDialog("f", "Payment Failed", null);
                }
            } else if (resultModel != null && resultModel.getError() != null) {
                Log.e("Failed resultModel 1: ", resultModel.getError().getTransactionResponse() + "");
            } else {
                Log.e("Failed resultModel 2", "Both objects are null!");
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_LONG).show();
        } else {
            Log.e("TAG", "onActivityResult: " + "Sorry for inconvenience! We are unable to process your payment at this time,please try again later or pay by our web portal.");
        }
    }
}