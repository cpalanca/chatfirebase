package org.izv.pgc.firebaserealtimedatabase.view;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.izv.pgc.firebaserealtimedatabase.R;
import org.izv.pgc.firebaserealtimedatabase.model.Message;

import java.util.List;

public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewAdapter.ItemHolder> {
    private LayoutInflater inflater;
    private List<Message> messageList;
    private onItemClickListener listener;

    public ChatViewAdapter(List<Message> messageList, onItemClickListener listener) {
        this.messageList = messageList;
        this.listener = listener;
    }

    public interface onItemClickListener{
        void onItemClick(Message message);
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.message, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {

        Message message = messageList.get(position);
        if(message.getFrom())
        {
            holder.messageLinearLayout.setGravity(Gravity.END);
            holder.messageLinearLayout.setBackgroundResource(R.drawable.bubble_outcoming);
        }
        else
        {
            holder.messageLinearLayout.setGravity(Gravity.START);
            holder.messageLinearLayout.setBackgroundResource(R.drawable.bubble_incoming);
        }
        holder.tvMessage.setText(message.getMessage());
        holder.tvTime.setText(message.getTime());

    }

    @Override
    public int getItemCount() { return messageList == null ? 0 : messageList.size(); }

    public void setData(List<Message> messageList){
        this.messageList = messageList;
        notifyDataSetChanged(); //actualizar la listas
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private final TextView tvMessage, tvTime;
        private final LinearLayout messageLinearLayout;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            messageLinearLayout = itemView.findViewById(R.id.messageLinearLayout);
        }
    }
}
