package com.example.buddyrobot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.transition.TransitionManager;
import android.transition.AutoTransition;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private TextView statusText;
    private View statusDot;
    private MaterialButton connectButton;
    private MaterialButton btnHeadUp, btnHeadDown, btnHeadLeft, btnHeadRight, btnHeadCenter;
    private Chip btnHappy, btnSad, btnSurprise, btnAngry, btnLove, btnSleepy;
    private Chip btnExcited, btnConfused, btnCool, btnShy, btnThinking, btnLaughing;
    private FaceView faceView;
    private MaterialCardView faceCard;
    private FloatingActionButton fabMinimize;
    private View statusContainer;

    // Minimize/Maximize state
    private boolean isMinimized = false;
    private int originalHeight = 0;
    private int minimizedHeight = 120; // Height when minimized (dp)

    // Connection state
    private boolean isConnected = false;

    // Bluetooth (COMMENTED FOR NOW)
    // private BluetoothManager bluetoothManager;
    // private static final int REQUEST_BLUETOOTH_PERMISSION = 1;

    // Auto expression cycling
    private Handler expressionHandler;
    private Runnable expressionRunnable;
    private int currentExpressionIndex = 0;
    private String[] expressions = {
            "HAPPY", "SAD", "SURPRISE", "ANGRY", "LOVE", "SLEEPY",
            "EXCITED", "CONFUSED", "COOL", "SHY", "THINKING", "LAUGHING"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // BLUETOOTH INITIALIZATION COMMENTED
            /*
            // Initialize Bluetooth Manager
            bluetoothManager = new BluetoothManager();

            // Check Bluetooth availability
            if (!bluetoothManager.isBluetoothAvailable()) {
                showToast("Bluetooth not available on this device");
                finish();
                return;
            }

            // Check permissions
            checkBluetoothPermissions();
            */

            // Initialize UI components
            initializeViews();

            // Setup button listeners
            setupButtonListeners();

            // Setup minimize/maximize functionality
            setupMinimizeMaximize();

            // Start auto expression cycling
            startAutoExpressionCycle();

        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error initializing: " + e.getMessage());
        }
    }

    // BLUETOOTH METHODS COMMENTED
    /*
    private void checkBluetoothPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Android 12+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN
                        },
                        REQUEST_BLUETOOTH_PERMISSION);
            }
        } else {
            // Android 11 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_BLUETOOTH_PERMISSION);
            }
        }
    }
    */

    private void initializeViews() {
        // Find views with null checks
        statusText = findViewById(R.id.statusText);
        statusDot = findViewById(R.id.statusDot);
        connectButton = findViewById(R.id.connectButton);
        faceView = findViewById(R.id.faceView);
        faceCard = findViewById(R.id.faceCard);
        fabMinimize = findViewById(R.id.fabMinimize);
        statusContainer = findViewById(R.id.statusContainer);

        // Movement buttons
        btnHeadUp = findViewById(R.id.btnHeadUp);
        btnHeadDown = findViewById(R.id.btnHeadDown);
        btnHeadLeft = findViewById(R.id.btnHeadLeft);
        btnHeadRight = findViewById(R.id.btnHeadRight);
        btnHeadCenter = findViewById(R.id.btnHeadCenter);

        // Expression chips
        btnHappy = findViewById(R.id.btnHappy);
        btnSad = findViewById(R.id.btnSad);
        btnSurprise = findViewById(R.id.btnSurprise);
        btnAngry = findViewById(R.id.btnAngry);
        btnLove = findViewById(R.id.btnLove);
        btnSleepy = findViewById(R.id.btnSleepy);
        btnExcited = findViewById(R.id.btnExcited);
        btnConfused = findViewById(R.id.btnConfused);
        btnCool = findViewById(R.id.btnCool);
        btnShy = findViewById(R.id.btnShy);
        btnThinking = findViewById(R.id.btnThinking);
        btnLaughing = findViewById(R.id.btnLaughing);

        // Check for null views
        if (statusText == null || connectButton == null || faceView == null) {
            throw new RuntimeException("Critical views not found in layout");
        }

        // Set initial status as demo mode
        statusText.setText("Demo Mode - Auto Expressions");
        statusText.setTextColor(getResources().getColor(R.color.pink_primary, getTheme()));

        // Store original height after layout is ready
        faceCard.post(new Runnable() {
            @Override
            public void run() {
                originalHeight = faceCard.getHeight();
            }
        });
    }

    // ========== MINIMIZE/MAXIMIZE FUNCTIONALITY ==========

    private void setupMinimizeMaximize() {
        if (fabMinimize != null) {
            fabMinimize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleMinimizeMaximize();
                }
            });
        }
    }

    private void toggleMinimizeMaximize() {
        if (faceCard == null) return;

        // Enable smooth transition
        TransitionManager.beginDelayedTransition(
                (ViewGroup) faceCard.getParent(),
                new AutoTransition().setDuration(300)
        );

        ViewGroup.LayoutParams params = faceCard.getLayoutParams();

        if (isMinimized) {
            // MAXIMIZE - Restore to original size
            if (originalHeight > 0) {
                params.height = originalHeight;
            } else {
                params.height = dpToPx(320); // Default height
            }

            // Show face view and status
            if (faceView != null) faceView.setVisibility(View.VISIBLE);
            if (statusContainer != null) statusContainer.setVisibility(View.VISIBLE);

            // Change FAB icon to minimize (X icon)
            fabMinimize.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);

            isMinimized = false;
            showToast("Maximized");

        } else {
            // MINIMIZE - Reduce to small size
            params.height = dpToPx(minimizedHeight);

            // Hide face view but keep status visible
            if (faceView != null) faceView.setVisibility(View.GONE);

            // Change FAB icon to maximize (more icon)
            fabMinimize.setImageResource(android.R.drawable.ic_menu_more);

            isMinimized = true;
            showToast("Minimized");
        }

        faceCard.setLayoutParams(params);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // ========== BUTTON LISTENERS ==========

    private void setupButtonListeners() {
        // Connect button (MODIFIED - Show message)
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Bluetooth connection temporarily disabled for demo");
                // handleConnect(); // UNCOMMENT WHEN YOU WANT TO ENABLE BLUETOOTH
            }
        });

        // Movement buttons - NOW WORKS WITHOUT CONNECTION
        if (btnHeadUp != null) {
            btnHeadUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendCommand("HEAD:UP");
                }
            });
        }

        if (btnHeadDown != null) {
            btnHeadDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendCommand("HEAD:DOWN");
                }
            });
        }

        if (btnHeadLeft != null) {
            btnHeadLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendCommand("HEAD:LEFT");
                }
            });
        }

        if (btnHeadRight != null) {
            btnHeadRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendCommand("HEAD:RIGHT");
                }
            });
        }

        if (btnHeadCenter != null) {
            btnHeadCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendCommand("HEAD:CENTER");
                }
            });
        }

        // Expression chips - NOW STOPS AUTO CYCLE WHEN PRESSED
        if (btnHappy != null) {
            btnHappy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAutoExpressionCycle();
                    sendCommand("EXPR:HAPPY");
                }
            });
        }

        if (btnSad != null) {
            btnSad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAutoExpressionCycle();
                    sendCommand("EXPR:SAD");
                }
            });
        }

        if (btnSurprise != null) {
            btnSurprise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAutoExpressionCycle();
                    sendCommand("EXPR:SURPRISE");
                }
            });
        }

        if (btnAngry != null) {
            btnAngry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAutoExpressionCycle();
                    sendCommand("EXPR:ANGRY");
                }
            });
        }

        if (btnLove != null) {
            btnLove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAutoExpressionCycle();
                    sendCommand("EXPR:LOVE");
                }
            });
        }

        if (btnSleepy != null) {
            btnSleepy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAutoExpressionCycle();
                    sendCommand("EXPR:SLEEPY");
                }
            });
        }

        if (btnExcited != null) {
            btnExcited.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAutoExpressionCycle();
                    sendCommand("EXPR:EXCITED");
                }
            });
        }

        if (btnConfused != null) {
            btnConfused.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAutoExpressionCycle();
                    sendCommand("EXPR:CONFUSED");
                }
            });
        }

        if (btnCool != null) {
            btnCool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAutoExpressionCycle();
                    sendCommand("EXPR:COOL");
                }
            });
        }

        if (btnShy != null) {
            btnShy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAutoExpressionCycle();
                    sendCommand("EXPR:SHY");
                }
            });
        }

        if (btnThinking != null) {
            btnThinking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAutoExpressionCycle();
                    sendCommand("EXPR:THINKING");
                }
            });
        }

        if (btnLaughing != null) {
            btnLaughing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAutoExpressionCycle();
                    sendCommand("EXPR:LAUGHING");
                }
            });
        }
    }

    // ========== AUTO EXPRESSION CYCLING ==========

    private void startAutoExpressionCycle() {
        expressionHandler = new Handler();
        expressionRunnable = new Runnable() {
            @Override
            public void run() {
                // Change to next expression
                String expression = expressions[currentExpressionIndex];

                if (faceView != null) {
                    faceView.setExpression(expression);
                    showToast("Expression: " + expression);
                }

                // Move to next expression
                currentExpressionIndex = (currentExpressionIndex + 1) % expressions.length;

                // Schedule next change (3 seconds)
                expressionHandler.postDelayed(this, 3000);
            }
        };

        // Start the cycle after 1 second
        expressionHandler.postDelayed(expressionRunnable, 1000);
    }

    private void stopAutoExpressionCycle() {
        if (expressionHandler != null && expressionRunnable != null) {
            expressionHandler.removeCallbacks(expressionRunnable);
            showToast("Auto-cycle stopped. Press expression buttons to resume.");
        }
    }

    // BLUETOOTH CONNECTION METHODS COMMENTED
    /*
    private void handleConnect() {
        if (!isConnected) {
            if (!bluetoothManager.isBluetoothEnabled()) {
                showToast("Please enable Bluetooth first");
                return;
            }

            // Show device selection dialog
            showDeviceSelectionDialog();
        } else {
            // Disconnect
            bluetoothManager.disconnect();
            isConnected = false;
            updateConnectionStatus(false);
            showToast("Disconnected");
        }
    }

    private void showDeviceSelectionDialog() {
        try {
            Set<BluetoothDevice> pairedDevices = bluetoothManager.getPairedDevices();

            if (pairedDevices == null || pairedDevices.isEmpty()) {
                showToast("No paired devices found. Please pair your ESP32 first.");
                return;
            }

            // Convert to list for dialog
            final ArrayList<BluetoothDevice> deviceList = new ArrayList<>(pairedDevices);
            String[] deviceNames = new String[deviceList.size()];

            for (int i = 0; i < deviceList.size(); i++) {
                String name = deviceList.get(i).getName();
                String address = deviceList.get(i).getAddress();
                deviceNames[i] = (name != null ? name : "Unknown") + "\n" + address;
            }

            // Show selection dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select BUDDY Robot");
            builder.setItems(deviceNames, (dialog, which) -> {
                BluetoothDevice device = deviceList.get(which);
                connectToDevice(device.getAddress());
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();

        } catch (SecurityException e) {
            showToast("Bluetooth permission denied");
            e.printStackTrace();
        } catch (Exception e) {
            showToast("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void connectToDevice(String address) {
        showToast("Connecting...");

        // Connect in background thread
        new Thread(() -> {
            boolean success = bluetoothManager.connect(address);

            runOnUiThread(() -> {
                if (success) {
                    isConnected = true;
                    updateConnectionStatus(true);
                    showToast("Connected to BUDDY!");
                } else {
                    showToast("Connection failed. Try again.");
                }
            });
        }).start();
    }

    private void updateConnectionStatus(boolean connected) {
        try {
            if (connected) {
                statusText.setText("Connected");
                statusText.setTextColor(getResources().getColor(R.color.success, getTheme()));

                // Update status dot color if available
                if (statusDot != null && statusDot.getBackground() != null) {
                    statusDot.setBackgroundColor(getResources().getColor(R.color.success, getTheme()));
                }

                connectButton.setText("Disconnect");
                connectButton.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel, getTheme()));
            } else {
                statusText.setText("Disconnected");
                statusText.setTextColor(getResources().getColor(R.color.error, getTheme()));

                // Update status dot color if available
                if (statusDot != null && statusDot.getBackground() != null) {
                    statusDot.setBackgroundColor(getResources().getColor(R.color.error, getTheme()));
                }

                connectButton.setText("Connect to BUDDY");
                connectButton.setIcon(getResources().getDrawable(android.R.drawable.stat_sys_data_bluetooth, getTheme()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    private void sendCommand(String command) {
        // BLUETOOTH SENDING COMMENTED
        /*
        if (!isConnected) {
            showToast("Please connect to BUDDY first");
            return;
        }
        */

        try {
            // BLUETOOTH SEND COMMENTED
            // bluetoothManager.sendData("<" + command + ">");

            showToast("Command: " + command);

            // Update face expression locally
            if (command.startsWith("EXPR:") && faceView != null) {
                String expression = command.substring(5); // Remove "EXPR:"
                faceView.setExpression(expression);
            }
        } catch (Exception e) {
            showToast("Error sending command");
            e.printStackTrace();
        }
    }

    private void showToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // BLUETOOTH PERMISSION CALLBACK COMMENTED
    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Bluetooth permission granted");
            } else {
                showToast("Bluetooth permission denied. App may not work properly.");
            }
        }
    }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop auto expression cycle
        stopAutoExpressionCycle();

        // BLUETOOTH CLEANUP COMMENTED
        /*
        try {
            if (bluetoothManager != null) {
                bluetoothManager.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
}