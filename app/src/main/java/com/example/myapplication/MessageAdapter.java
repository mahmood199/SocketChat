package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_MESSAGE_SENT = 0;
    private final static int TYPE_MESSAGE_RECIEVED = 1;
    private List<JSONObject> messages = new ArrayList<>();
    LayoutInflater inflater;

    public MessageAdapter(LayoutInflater layoutInflater) {
        this.inflater = layoutInflater;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        switch(viewType)
        {
            case TYPE_MESSAGE_SENT:
                view = inflater.inflate(R.layout.item_sent_message,parent,false);
                return new SentMessageholder(view);

            case TYPE_MESSAGE_RECIEVED:
                view = inflater.inflate(R.layout.item_received_message,parent,false);
                return new RecievedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        JSONObject message = messages.get(position);
        try {
            if(message.getBoolean("isSent"))
            {
                SentMessageholder messageholder = (SentMessageholder) holder;
                messageholder.messageTxt.setText(message.getString("message"));
            }
            else
            {
                RecievedMessageHolder messageholder = (RecievedMessageHolder) holder;
                messageholder.nameTxt.setText(message.getString("name"));
                messageholder.messageTxt.setText(message.getString("message"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        JSONObject message = messages.get(position);

        try {
            if(message.getBoolean("isSent"))
                return TYPE_MESSAGE_SENT;
            else
                return TYPE_MESSAGE_RECIEVED;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public class SentMessageholder extends RecyclerView.ViewHolder{
        TextView messageTxt;

        public SentMessageholder(@NonNull View itemView) {
            super(itemView);

            messageTxt = itemView.findViewById(R.id.sentTxt);
        }
    }


    public class RecievedMessageHolder extends RecyclerView.ViewHolder{
        TextView nameTxt;
        TextView messageTxt;

        public RecievedMessageHolder(@NonNull View itemView) {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.nameTxt);
            messageTxt = itemView.findViewById(R.id.receivedTxt);

        }
    }

    public void addItem(JSONObject jsonObject)
    {
        messages.add(jsonObject);
        notifyDataSetChanged();
    }
}
