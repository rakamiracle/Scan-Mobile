package com.example.buddyrobot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {
    private static final String TAG = "BluetoothManager";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private boolean isConnected = false;

    public BluetoothManager() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isBluetoothAvailable() {
        return bluetoothAdapter != null;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public Set<BluetoothDevice> getPairedDevices() {
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.getBondedDevices();
        }
        return null;
    }

    public boolean connect(String deviceAddress) {
        try {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            isConnected = true;
            Log.d(TAG, "Connected to: " + device.getName());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Connection failed", e);
            disconnect();
            return false;
        }
    }

    public void sendData(String data) {
        if (isConnected && outputStream != null) {
            try {
                outputStream.write(data.getBytes());
                outputStream.write('\n'); // Tambahkan newline
                Log.d(TAG, "Sent: " + data);
            } catch (IOException e) {
                Log.e(TAG, "Send failed", e);
                disconnect();
            }
        }
    }

    public void disconnect() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
            isConnected = false;
            Log.d(TAG, "Disconnected");
        } catch (IOException e) {
            Log.e(TAG, "Disconnect error", e);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}