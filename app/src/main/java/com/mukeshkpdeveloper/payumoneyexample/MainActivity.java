package com.mukeshkpdeveloper.payumoneyexample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUBillingCycle;
import com.payu.base.models.PayUPaymentParams;
import com.payu.base.models.PayUSIParams;
import com.payu.base.models.PayuBillingLimit;
import com.payu.base.models.PayuBillingRule;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.models.PayUCheckoutProConfig;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    String saltt = "wia56q6O";//"",g0nGFe03
    String keyy = "gtKFFx";//"",3TnMpV
    String hashServer = "";
    String hashServerDetail = "";
    String transaction_id ="";
    public static RequestQueue mRequestQue;
    private final String surl = "https://payu.herokuapp.com/success";
    private final String furl = "https://payu.herokuapp.com/failure";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRequestQue = Volley.newRequestQueue(MainActivity.this);


        AppCompatButton text = findViewById(R.id.text);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callingApiForHash("1");
            }
        });


    }

    private void callingApiForHash(String amount) {
        Log.e("TAG", "callingApiForHash: yhea" );
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.travier.in/api/wallet/add-amount", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("TAG", "addAmount: " + response);
                    JSONObject rootObject = new JSONObject(response);
                    JSONObject order = rootObject.getJSONObject("order");
                    transaction_id = order.getString("transaction_id");
                    //  showByPlanDialog(transaction_id, order.getString("amount"), order.getString("hash"), order.getString("gst_percent"), order.getString("gst"), amount, order);

                    hashServer =  order.getString("hash");
                    hashServerDetail =  rootObject.getString("detailhash");
                    payNow(amount);
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
    private PayUCheckoutProConfig getCheckoutProConfig() {
        PayUCheckoutProConfig checkoutProConfig = new PayUCheckoutProConfig();
        checkoutProConfig.setMerchantName("Travier");
//        checkoutProConfig.setMerchantLogo(R.drawable.merchant_logo);
        return checkoutProConfig;
    }
    private void payNow(String amount) {//String payment_id, String amount, String hash
        /*String mobile = "7803994667";
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
                .setKey("tzZFP4Je") //here set merchant key
                .setMerchantId("7243406"); ////here set merchant ID
        try {
            paymentParam = builder.build();
            paymentParam.setMerchantHash(hash);
            Log.e("TAG", "payNow: "+hash );
            Log.e("TAG", "payNow: "+hashFromAndroid );
          //  paymentParam.setMerchantHash(hashFromAndroid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, MainActivity.this, R.style.PayumoneyAppTheme1, true);*/

        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(PayUCheckoutProConstants.CP_UDF1, "udf1");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF2, "udf2");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF3, "udf3");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF4, "udf4");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF5, "udf5");

        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(amount)
        .setIsProduction(true)
        .setProductInfo("Travier")
        .setKey(keyy)
        .setPhone("7803994667")
        .setTransactionId(transaction_id)
        .setFirstName("Mukesh_kumar Patel")
        .setEmail("kumarmukeshpatel57@gmail.com")
        .setSurl(surl)
        .setFurl(furl)
        .setAdditionalParams(additionalParams);
        PayUPaymentParams payUPaymentParams = builder.build();

        Log.d("TAG MUKESH", "payNow: "+builder);

        PayUCheckoutPro.open(
                this,
                payUPaymentParams,
                getCheckoutProConfig(),
                new PayUCheckoutProListener() {

                    @Override
                    public void onPaymentSuccess(Object response) {
                        //Cast response object to HashMap
                        HashMap<String,Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                        Log.d("TAG_PAYU", "onPaymentSuccess: "+payuResponse);
                        Log.d("TAG_PAYU", "onPayment_merchantResponse: "+merchantResponse);
                    }

                    @Override
                    public void onPaymentFailure(Object response) {
                        //Cast response object to HashMap
                        HashMap<String,Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                        Log.d("PAYU", "payuResponse: "+payuResponse);
                        Log.d("PAYU", "merchantResponse: "+merchantResponse);
                    }

                    @Override
                    public void onPaymentCancel(boolean isTxnInitiated) {
                        Log.d("PAYU", "CANCEL: ");
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        String errorMessage = errorResponse.getErrorMessage();
                        Log.d("PAYU", "onError: "+errorMessage);
                    }

                    @Override
                    public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                        //For setting webview properties, if any. Check Customized Integration section for more details on this
                    }

                    @Override
                    public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                        String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                        String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                        if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                            //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
//                            String hash = hashServer;
//                            Log.d("TAG", "generateHash: "+hashServer);
//                            HashMap<String, String> dataMap = new HashMap<>();
//                            dataMap.put(hashName, hash);
//                            hashGenerationListener.onHashGenerated(dataMap);

                            // for locale generate hash key
                            String salt = saltt;
                            if (valueMap.containsKey(PayUCheckoutProConstants.CP_POST_SALT))
                                salt = salt + "" + (valueMap.get(PayUCheckoutProConstants.CP_POST_SALT));

                            String hash = null;
                            if (hashName.equalsIgnoreCase(PayUCheckoutProConstants.CP_LOOKUP_API_HASH)) {
                                //Calculate HmacSHA1 HASH for calculating Lookup API Hash
                                ///Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.

                                Log.d("TAG-LOcal Hash: ", "beforegenerateHashhashName: "+hashName);
                                Log.d("TAG-LOcal Hash: ", "beforegenerateHashhashData: "+hashData);
                                hash = calculateHmacSHA1Hash(hashData, keyy);
                            } else {

                                //Calculate SHA-512 Hash here
                                Log.d("TAG-LOcal Hash: ", "beforegenerateHashhashName: "+hashName);
                                Log.d("TAG-LOcal Hash: ", "beforegenerateHashhashData: "+hashData);
                                hash = calculateHash(hashData + salt);
                            }

                            Log.d("TAG-LOcal Hash: ", "generateHash: "+hash);

                            HashMap<String, String> dataMap = new HashMap<>();
                            dataMap.put(hashName, hash);
                            hashGenerationListener.onHashGenerated(dataMap);
                        }
                    }
                }
        );

    }

    private String calculateHash(String hashString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            Log.d("TAG-LOcal Hash: ", "hashString.getBytes(): "+hashString);
            return getHexString(mdbytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getHexString(byte[] array) {
        StringBuilder hash = new StringBuilder();
        for (byte hashByte : array) {
            hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
        }
        return hash.toString();
    }

    private String calculateHmacSHA1Hash(String data, String key) {
        String HMAC_SHA1_ALGORITHM = "HmacSHA1";
        String result = null;

        try {
            Key signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = getHexString(rawHmac);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}