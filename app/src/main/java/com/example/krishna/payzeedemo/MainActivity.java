package com.example.krishna.payzeedemo;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krishna.payzeedemo.databinding.ActivityMainBinding;
import com.google.common.util.concurrent.ExecutionError;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding dataBind;
    ProgressDialog pd;
    String amt,type,name,cardno,exp,cvv;
    TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBind = DataBindingUtil.setContentView(this,R.layout.activity_main);


        dataBind.txtpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dataBind.txtStatus.setText("Processing");
                amt = dataBind.etAmount.getText().toString();
                type =        dataBind.etCardTye.getText().toString();
                name =        dataBind.etHolderName.getText().toString();
                cardno=       dataBind.etCardNo.getText().toString();
                exp =       dataBind.etExp.getText().toString();
                cvv =     dataBind.etCVV.getText().toString();

                try {
                    String hostURL = "https://api-cert.payeezy.com/v1/transactions";

                    String result = new CallAPI().execute(hostURL).get();

                    JSONObject mainObj = new JSONObject(result.toString());
                    if (mainObj.getString("transaction_status").equalsIgnoreCase("approved")) {
                        dataBind.txtStatus.setText("Transaction successful.");
                        dataBind.txtStatus.setTextColor(Color.GREEN);
                    }else{

                        JSONObject error = mainObj.getJSONObject("Error");
                        JSONArray mesageErrorAray = error.getJSONArray("messages");
                        JSONObject finalerror = mesageErrorAray.getJSONObject(0);

                        dataBind.txtStatus.setText("Transaction Fail :"+finalerror.get("description"));
                        dataBind.txtStatus.setTextColor(Color.RED);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }





//                doEmotionAPICall(dataBind.etAmount.getText().toString(),
//                        dataBind.etCardTye.getText().toString(),
//                        dataBind.etHolderName.getText().toString(),
//                        dataBind.etCardNo.getText().toString(),
//                        dataBind.etExp.getText().toString(),
//                        dataBind.etCVV.getText().toString());
            }
        });

    }

  //  private  void doEmotionAPICall(final String amt,final String type,final String name,
   //                                final String cardno,final String exp,final String cvv) {



        public class CallAPI extends AsyncTask<String, String, String> {
            String output="";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
               // pd = ProgressDialog.show(MainActivity.this, "Doing Payment", "Please wait...", true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
              //  pd.dismiss();
            }



            @Override
            public String doInBackground(String... params) {
                try {

                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("https://api-cert.payeezy.com/v1/transactions");

                    JSONObject userObject = new JSONObject();
                    userObject.put("merchant_ref", "Astonishing-Sale");
                    userObject.put("transaction_type", "authorize");
                    userObject.put("method", "credit_card");
                    userObject.put("amount", ""+amt);
                    userObject.put("currency_code", "USD");

                    JSONObject card = new JSONObject();
                    card.put("type", ""+type);
                    card.put("cardholder_name", ""+name);
                    card.put("card_number", ""+cardno);
                    card.put("exp_date", ""+exp);
                    card.put("cvv", ""+cvv);


                    userObject.put("credit_card", card);

                    StringEntity se = new StringEntity(userObject.toString());

                    //se.setContentEncoding("UTF-8");
                    se.setContentType("application/json");
                    post.setEntity(se);

                    post.setHeader("apikey", "y6pWAJNyJyjGv66IsVuWnklkKUPFbb0a");
                    post.setHeader("Authorization", "HMAC Signature");
                    post.setHeader("token", "fdoa-a480ce8951daa73262734cf102641994c1e55e7cdf4c02b6");
                    post.setHeader("Host", "api-cert.payeezy.com");

                    HttpResponse response = client.execute(post);

                    String result = EntityUtils.toString(response.getEntity());
                    Log.e("RESULT",result);


                    output = result;
                } catch (Exception e) {
                    e.printStackTrace();
                    //pd.dismiss();
                }

                return output;
            }

    }
}
