package org.opencv.samples.mtcnn;

import android.os.Environment;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by kongjiahui on 2018/2/12.
 */

public class upLoadTask {

    private File file;

    private static final String IP = "47.100.106.244";

    private static final int PORT = 8888;

    public upLoadTask(File file) {
        this.file = file;
    }

    public void startClient() throws IOException{
        new Thread(new upload(file)).start();
    }

    class upload implements Runnable {

        File file;

        public upload(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            try {
                Socket client = new Socket(IP,PORT);
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                dos.writeInt(2);
                if (!file.exists()) {
                    Log.i("socket","文件不存在");
                    client.close();
                    return;
                }
                FileInputStream fis = new FileInputStream(file);
                dos.writeLong(file.length());
                dos.flush();
                int count = 0;
                int len;
                byte[] buffer = new byte[8192];
                while((len = fis.read(buffer)) != -1) {
                    dos.write(buffer,0,len);
                    dos.flush();
                    count += len;
                }
                Log.i("socket","待上传文件长度" + file.length());
                Log.i("socket","已上传文件长度" + count);
                Log.i("socket","上传完成");
                fis.close();
                dos.close();
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
