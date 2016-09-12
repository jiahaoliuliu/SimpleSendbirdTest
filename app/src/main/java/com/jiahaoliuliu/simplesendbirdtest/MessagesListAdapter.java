package com.jiahaoliuliu.simplesendbirdtest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jiahaoliuliu on 7/11/16.
 */
public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.ViewHolder> {

    // View holder for the view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mMessagesTextView;
        public TextView mDateTextView;

        public ViewHolder(View view) {
            super(view);
            this.mMessagesTextView = (TextView) view.findViewById(R.id.message_text_view);
            this.mDateTextView = (TextView) view.findViewById(R.id.date_text_view);
        }
    }

//    // Messages type
//    private static final int MESSAGE_TYPE_SENT = 1;
//    private static final int MESSAGE_TYPE_RECEIVED = 2;
//
//    private static final String DATE_FORMAT = "dd/MM HH:mm";
//
//    // Internal data
//    private List<Message> mMessagesList;
//    private String mMyUserId;
//    private SimpleDateFormat mDateFormatter;
//
//    public MessagesListAdapter(String myUserId, List<Message> messagesList) {
//        this.mMyUserId = myUserId;
//        this.mMessagesList = messagesList;
//        mDateFormatter = new SimpleDateFormat(DATE_FORMAT);
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        Message message = mMessagesList.get(position);
//        if (mMyUserId.equals(message.getSenderId())) {
//            return MESSAGE_TYPE_SENT;
//        } else {
//            return MESSAGE_TYPE_RECEIVED;
//        }
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
//        return mMessagesList.size();
        return 0;
    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        int layoutId;
//        switch (viewType) {
//            case MESSAGE_TYPE_SENT:
//                layoutId = R.layout.sent_message_layout;
//                break;
//            default:
//            case MESSAGE_TYPE_RECEIVED:
//                layoutId = R.layout.received_message_layout;
//                break;
//        }
//
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(layoutId, parent, false);
//        ViewHolder vh = new ViewHolder(v);
//        return vh;
//
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        Message message = mMessagesList.get(position);
//        holder.mMessagesTextView.setText(message.getMessage());
//        holder.mDateTextView.setText(mDateFormatter.format(new Date(message.getTimestamp())));
//    }
//
//    public void addMessage(Message message) {
//        if (message.isPast()) {
//            mMessagesList.add(0, message);
//        } else {
//            mMessagesList.add(message);
//        }
//
//        notifyDataSetChanged();
//    }
}
