package com.jedsada.workshop.mqtt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements IMqttActionListener {

    private static final String TOPIC = "UP_Team_A/LED";
    private static final String SERVER_URI = "tcp://mqtt.espert.io:1883";
    private MqttAndroidClient client;

    @Override
    protected void onStart() {
        super.onStart();
        setupMQTT();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (client != null) client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((ToggleButton) findViewById(R.id.tgg_led)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sendMessage(b ? "on" : "off");
            }
        });
        setupMQTT();
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Toast.makeText(this, "Connect success!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void setupMQTT() {
        String clientId = MqttClient.generateClientId();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        client = new MqttAndroidClient(this.getApplicationContext(), SERVER_URI, clientId);
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String msg) {
        MqttMessage message = new MqttMessage();
        message.setPayload(msg.getBytes());
        message.setQos(1);
        message.setRetained(true);
        try {
            client.publish(TOPIC, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}