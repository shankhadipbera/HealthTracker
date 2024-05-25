package com.shankha.healthtracker;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Handler;


public class ConnectedThread extends Thread {

    private static final String TAG = "FrugalLogs";
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler handler;
    private static final int MESSAGE_READ = 2;
    private static final int ERROR_READ = 0;
    private String valueRead;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.handler=handler;

        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }


    public void run() {

        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                if(mmInStream != null) {

                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, readMessage);
                    Message readMsg = handler.obtainMessage(MESSAGE_READ, readMessage);
                    readMsg.sendToTarget();

                }
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                handler.obtainMessage(ERROR_READ, "Error reading data").sendToTarget();
                break;
            }
        }

    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

}



