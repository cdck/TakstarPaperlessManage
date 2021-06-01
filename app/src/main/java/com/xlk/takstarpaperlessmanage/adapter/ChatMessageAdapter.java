package com.xlk.takstarpaperlessmanage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.bean.ChatMessage;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> data;
    private final Context context;

    public ChatMessageAdapter(Context context, List<ChatMessage> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new LeftViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false));
        } else {
            return new RightViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //根据
        ChatMessage message = data.get(position);
        if (holder instanceof LeftViewHolder) {
            String msg = message.getContent();
            String name = message.getMemberName();
            ((LeftViewHolder) holder).i_m_c_l_message_title.setText(name);
            ((LeftViewHolder) holder).i_m_c_l_message_content.setText(msg);
        } else {
            String title = context.getResources().getString(R.string.me);
            ((RightViewHolder) holder).i_m_c_r_message_title.setText(title);
            ((RightViewHolder) holder).i_m_c_r_message_content.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return data!=null?data.size():0;
    }
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = data.get(position);
        //=0为接收的消息，=1为发送的消息
        int type = message.getType();
        return type;
    }


    //接收 --->>> 其他人发送的消息
    class LeftViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView i_m_c_l_message_title;
        TextView i_m_c_l_message_content;

        public LeftViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            i_m_c_l_message_title = itemView.findViewById(R.id.i_m_c_l_message_title);
            i_m_c_l_message_content = itemView.findViewById(R.id.i_m_c_l_message_content);
        }
    }

    //发送 --->>> 第一人称发送的消息
    class RightViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView i_m_c_r_message_title;
        TextView i_m_c_r_message_content;

        public RightViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            i_m_c_r_message_title = itemView.findViewById(R.id.i_m_c_r_message_title);
            i_m_c_r_message_content = itemView.findViewById(R.id.i_m_c_r_message_content);
        }
    }
}
