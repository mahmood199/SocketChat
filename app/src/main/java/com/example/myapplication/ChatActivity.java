package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatActivity extends AppCompatActivity implements TextWatcher{

    private String name;
    private WebSocket webSocket;
    private String SERVER_PATH = "ws://192.168.0.106:3000";


    private EditText messageEdit;
    private Button sendBtn;
    private RecyclerView recyclerView;

    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        name = getIntent().getStringExtra("username");
        initiateWebSocketConnection();
    }

    private void initiateWebSocketConnection() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                                     .url(SERVER_PATH)
                                     .build();

        webSocket =  client.newWebSocket(request,new SocketListener());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    private class SocketListener extends WebSocketListener{

        @Override
        public void onOpen(@NotNull okhttp3.WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChatActivity.this,"SocketConnection Successful",Toast.LENGTH_SHORT).show();
                    initializeView();
                }
            });
        }


        @Override
        public void onMessage(@NotNull okhttp3.WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject jsonObject = new JSONObject(text);
                        jsonObject.put("isSent",false);
                        messageAdapter.addItem(jsonObject);

                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount()-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    private void initializeView() {

        messageEdit = (EditText) findViewById(R.id.text);
        sendBtn = (Button) findViewById(R.id.send_message);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        messageAdapter = new MessageAdapter(getLayoutInflater());
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();

                try {

                    jsonObject.put("name",name);
                    jsonObject.put("message",messageEdit.getText().toString());
                    jsonObject.put("isSent",true);

                    messageAdapter.addItem(jsonObject);

                    webSocket.send(jsonObject.toString());
                    messageEdit.setText("");
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount()-1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });



    }
}