package me.yifeiyuan.okhttploaddemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mPb;
    private TextView mTv;
    private TextView mTvProgress;
    private static final String TAG = "MainActivity";

    private ProgressResponseBody.ProgressListener progressListener = new ProgressResponseBody.ProgressListener() {
        @Override
        public void onProgressUpdate(int progress) {
            Log.d(TAG, Thread.currentThread().getName() + ",update: progress:" + progress);
            mPb.setProgress(progress);
            mTvProgress.setText("Progress:"+progress);
        }
    };
    private OkHttpClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mPb = (ProgressBar) findViewById(R.id.pb);
        mTv = (TextView) findViewById(R.id.tv_down);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);


        OkHttpClient.Builder builder  = new OkHttpClient.Builder();

//        builder.networkInterceptors().add(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Response originalResponse = chain.proceed(chain.request());
//                return originalResponse.newBuilder().body(
//                        new ProgressResponseBody(originalResponse.body(), progressListener))
//                        .build();
//            }
//        });

        mClient = builder.build();

        mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                oriDownload();
                 File file = new File(Environment.getExternalStorageDirectory().getPath(),"a.jpg");
                mPb.setProgress(0);
                download("http://img.hb.aicdn.com/ea97b82fe1cc2030d8f2c298126c3e9ae6c9bd0b114e5c-Dlt9zF_fw658",file,progressListener);
//                download("http://img.hb.aicdn.com/33b0070520fcdc6f00cc75a7c9a348453e50325021672-ouc4Pf_fw658",file,progressListener);
            }
        });
    }

    private void oriDownload() {
        Request request = new Request.Builder()
                //下载地址
                .url("http://img.hb.aicdn.com/33b0070520fcdc6f00cc75a7c9a348453e50325021672-ouc4Pf_fw658")
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("failure");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //将返回结果转化为流，并写入文件
                int len;
                byte[] buf = new byte[2048];
                InputStream inputStream = response.body().byteStream();
                //可以在这里自定义路径
                File file1 = new File(Environment.getExternalStorageDirectory().getPath(),"a.jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(file1);

                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }

                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
            }

        });
    }


    private void download(String url,final File file,final ProgressResponseBody.ProgressListener progressListener) {
        Request request = new Request.Builder().url(url).build();
        Call call = mClient.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(
                        new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        }).build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //将返回结果转化为流，并写入文件
                int len;
                byte[] buf = new byte[2048];
                InputStream inputStream = response.body().byteStream();
                //可以在这里自定义路径
//                File file1 = new File(Environment.getExternalStorageDirectory().getPath(),"a.jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }

                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
