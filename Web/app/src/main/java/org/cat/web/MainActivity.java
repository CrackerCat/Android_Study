package org.cat.web;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xml.sax.*;
import org.xml.sax.ContentHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParserFactory;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView responseText = null;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        //获取WebView实例
//        WebView webView = (WebView) findViewById(R.id.web_view);
//        //设置WebView支持javaScript属性
//        webView.getSettings().setJavaScriptEnabled(true);
//        //设置网页在当前的WebView中显示
//        webView.setWebViewClient(new WebViewClient());
//        //传入网址
//        webView.loadUrl("http://www.baidu.com");

        Button sendRequest = (Button) findViewById(R.id.send_request);
        responseText = (TextView) findViewById(R.id.response_text);
        sendRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send_request){
            //sendRequsetWithHttpURLConnection();
            sendRequestWithOkHttp();
        }
    }

    private void sendRequsetWithHttpURLConnection(){
        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    //获取HttpURLConnection实例
                    URL url = new URL("http://www.baidu.com");
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    connection.setRequestMethod("GET");

                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    //获取服务器返回的输入流
                    InputStream in = connection.getInputStream();

                    //对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    showResponse(response.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (reader != null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null){
                        //关闭连接
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //将结果显示到界面上
                responseText.setText(response);
            }
        });
    }

    void PostConnectWithHttpURLConnection(HttpURLConnection connection){
        try {
            connection.setRequestMethod("POST");
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            //数据与数据之间用&隔开
            out.writeBytes("username=admin&password=123456");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    //创建一个OkHttpClient实例
                    OkHttpClient client = new OkHttpClient();
                    //创建一个Request对象
                    Request request = new Request.Builder().url("http://10.0.2.2/get_data.xml").build();
                    Response response = null;
                    //发送请求并获取数据
                    response = client.newCall(request).execute();
                    //获取具体的内容
                    String responseData = response.body().string();
                    //showResponse(responseData);

                    //通过Pull解析xml文件
                    //parseXMLWithPull(responseData);

                    //通过SAX解析xml文件
                    parseXNLWithSAX(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    private void parseXMLWithPull(String xmlData){
        XmlPullParserFactory xmlPullParserFactory = null;
        try {
            //获取xmlPullParserFactory实例
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            //获取XmlPullParser对象
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            //解析xml数据
            xmlPullParser.setInput(new StringReader(xmlData));
            //获取当前解析事件
            int eventType = xmlPullParser.getEventType();
            String id = "";
            String name = "";
            String version = "";

            while(eventType != XmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();

                switch (eventType){
                    //开始解析某个节点
                    case XmlPullParser.START_TAG:{
                        if ("id".equals(nodeName)){
                            //获取节点的具体内容
                            id = xmlPullParser.nextText();
                        }else if ("name".equals(nodeName)){
                            name = xmlPullParser.nextText();
                        }else if ("version".equals(nodeName)){
                            version = xmlPullParser.nextText();
                        }
                        break;
                    }
                    //完成某个节点解析
                    case XmlPullParser.END_TAG:{
                        if ("app".equals(nodeName)){
                            Log.d(TAG, "parseXMLWithPull: id is " + id);
                            Log.d(TAG, "parseXMLWithPull: name is " + name);
                            Log.d(TAG, "parseXMLWithPull: version is " + version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                //获取下一个解析事件
                eventType = xmlPullParser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseXNLWithSAX(String xmlData){

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            MyContentHandler handler = new MyContentHandler();
            //将ConTentHandler的实例设置到xmlReader中
            xmlReader.setContentHandler(handler);
            //开始解析
            xmlReader.parse(new InputSource(new StringReader(xmlData)));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void PostConnectWithOkhttp(){
        //使用RequestBody存放待提交的参数
        RequestBody requestBody = new FormBody.Builder()
                .add("username", "admin")
                .add("password", "123456")
                .build();
        //调用post方法
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .post(requestBody)
                .build();
    }
}
