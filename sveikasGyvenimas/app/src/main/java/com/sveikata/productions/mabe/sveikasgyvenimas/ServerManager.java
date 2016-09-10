package com.sveikata.productions.mabe.sveikasgyvenimas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Benas on 9/7/2016.+-
 */
public class ServerManager extends AsyncTask<String, Void, Void> {

    private String method_type;
    private int response;
    private SharedPreferences sharedPreferences;
    private Context context;

    private String username_login;
    private String password_login;

    public static String SERVER_ADRESS_REGISTER = "http://dvp.lt/android/register.php";
    public static String SERVER_ADRESS_LOGIN = "http://dvp.lt/android/login.php";



    public ServerManager(Context context){
        this.context=context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String...params) {
        method_type = params[0];

        if(method_type.equals("REGISTRATION")){

            String name = params[1];
            String last_name = params[2];
            String username = params[3];
            String password = params[4];
            String mail = params[5];
            String gender = params[6];
            String years = params[7];
            String type = params[8];


            response = register(name,last_name,username,password,mail,gender,years,type);

            if(!type.equals("regular"))
                context.startActivity(new Intent(context, TabActivityLoader.class));

        }
        if(method_type.equals("LOGIN")) {

            username_login = params[1];
            password_login = params[2];

            response = login(username_login,password_login);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (method_type.equals("REGISTRATION")) {
            switch (response) {
                case 0:
                    new AlertDialog.Builder(context)
                            .setMessage("Toks vartotojo vardas arba paštas jau egzistuoja")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    break;
                case 1:
                    context.startActivity(new Intent(context, ActivateAccountActivity.class));
                    break;

                case 3:
                    CheckingUtils.createErrorBox("Norint prisijungti, reikia interneto", context);
                    break;
            }
        }

        if(method_type.equals("LOGIN")){
            switch (response) {
                case 0:
                    sharedPreferences = context.getSharedPreferences("DataPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username_login);
                    editor.putString("password", password_login);
                    editor.commit();
                    context.startActivity(new Intent(context, TabActivityLoader.class));
                    break;
                case 1:
                    CheckingUtils.createErrorBox("Wrong username or password", context);
                    break;

                case 2:
                    context.startActivity(new Intent(context, ActivateAccountActivity.class));
                    break;

                case 3:
                    CheckingUtils.createErrorBox("You need internet connection to do that", context);
                    break;
            }

         }
        super.onPostExecute(aVoid);
        }




    public int register(String name, String last_name, String username, String password, String mail, String gender, String age, String type){

        //Connect to mysql.
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(SERVER_ADRESS_REGISTER);

        //JSON object.
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.putOpt("name", name);
            jsonObject.putOpt("last_name", last_name);
            jsonObject.putOpt("username", username);
            jsonObject.putOpt("password", password);
            jsonObject.putOpt("mail", mail);
            jsonObject.putOpt("gender", gender);
            jsonObject.putOpt("age", age);
            jsonObject.putOpt("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("TEST", jsonObject.toString());


        EntityBuilder entity = EntityBuilder.create();
        entity.setText(jsonObject.toString());
        httpPost.setEntity(entity.build());

        JSONObject responseObject = null;


        try {
            //Getting response
            HttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());
            System.err.println(responseBody);
            responseObject = new JSONObject(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
        }



        try {
            return responseObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }



    }


    private int login(String username, String password) {

        //Connect to mysql.
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(SERVER_ADRESS_LOGIN);


        //JSON object.
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.putOpt("username", username);
            jsonObject.putOpt("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        EntityBuilder entity = EntityBuilder.create();
        entity.setText(jsonObject.toString());
        httpPost.setEntity(entity.build());

        JSONObject responseObject = null;

        try {
            //Getting response
            HttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());
            System.err.println(responseBody);
            responseObject = new JSONObject(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            return responseObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }


    }

}
