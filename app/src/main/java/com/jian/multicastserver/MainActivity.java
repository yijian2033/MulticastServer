package com.jian.multicastserver;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MainActivity extends AppCompatActivity {

    private static final int PORT = 8080;
    private static final String IP = "224.224.224.224";
//    private static final String IP = "192.168.0.5";
    private boolean isSend = true;
    private TextView tvShow;
    private WifiManager wm;
    private String ip;
    private MulticastSocket socket;
    private InetAddress address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvShow = (TextView) findViewById(R.id.tv);
        wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wm.getConnectionInfo();
        int ipAddress = info.getIpAddress();
        ip = intToIp(ipAddress);

        try {
            socket = new MulticastSocket(PORT);
            address = InetAddress.getByName(IP);
            socket.setTimeToLive(4);
            socket.joinGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void send(View view) {
        isSend = true;
        new SendWorkThread().start();
    }

    public void sendStop(View view) {
        isSend = false;
    }

    private class SendWorkThread extends Thread {
        private DatagramPacket thradPacket = null;

        @Override
        public void run() {
            byte[] data = ip.getBytes();
            thradPacket = new DatagramPacket(data, data.length, address, PORT);
            while (true) {
                try {
                    if (isSend) {
                        socket.send(thradPacket);
                        Thread.sleep(5000);
                        Log.e("John", "再次发送ip地址");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                tvShow.setText("本机ip ：" + ip);
                                Toast.makeText(getApplicationContext(), "再次发送...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }


}
