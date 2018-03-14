package org.opencv.samples.mtcnn;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DownLoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        RelativeLayout download_layout = (RelativeLayout) findViewById(R.id.download_layout);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.download_progress);
        TextView fileNumber = (TextView) findViewById(R.id.file_number);
        new MyDownloadTask(progressBar,fileNumber).execute();

    }

    /**
     * 下载文件的类，异步
     * 更新progressbar，文件下载完毕则启动检测Activity
     */
    public class MyDownloadTask extends AsyncTask<Void, Integer, String> {

        private Socket client;

        private DataInputStream dis;

        private DataOutputStream dos;

        private String dir = Environment.getExternalStorageDirectory() + File.separator + "mtcnn";

        private long fileLength;

        private ProgressBar progressBar;

        private TextView downloadFileNumber;

        private int download_file_number = 1;

        public MyDownloadTask(ProgressBar progressBar,TextView downloadFileNumber) {
            this.progressBar = progressBar;
            this.downloadFileNumber = downloadFileNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                client = new Socket("47.100.106.244", 8888);
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                dos.writeInt(1);
                int fileNumber = dis.readInt();
                for (int i = 0; i < fileNumber; i++) {
                    boolean isOver = false;
                    String fileName = dis.readUTF();
                    fileLength = dis.readLong();
                    Log.i("socket","正在下载" + fileName + " 文件长度" + fileLength);
                    File file = new File(dir + File.separator + fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    int len;
                    int count = 0;
                    byte[] buffer = new byte[8192];
                    while (true) {
                        len = dis.read(buffer);
                        fos.write(buffer, 0, len);
                        fos.flush();
                        count += len;
                        publishProgress(count);
                        if (fileLength == count) {
                            break;
                        }
                    }
                    Log.i("socket","待传输文件长度" + fileLength);
                    Log.i("socket","已传输文件长度" + count);
                    Log.i("socket","下载完成" + fileName);
                    isOver = true;
                    dos.writeBoolean(isOver);
                    Log.i("socket","请求下载下一个文件");
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (dis != null) {
                    dis.close();
                }
                if (dos != null) {
                    dos.close();
                }
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Download Succeed";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //Log.i("socket","" + (int) ((values[0] / (float) fileLength) * 100));
            progressBar.setProgress((int) ((values[0] / (float) fileLength) * 100));
            if (fileLength == (long)values[0]) {
                downloadFileNumber.setText(download_file_number++ + "/3");
                progressBar.setProgress(0);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("Download Succeed")) {
                Intent intent = new Intent(DownLoadActivity.this,Tutorial2Activity.class);
                startActivity(intent);
            }
        }
    }


}
