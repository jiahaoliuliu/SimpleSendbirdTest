package com.jiahaoliuliu.simplesendbirdtest;

import android.content.Context;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sendbird.android.MessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdEventHandler;
import com.sendbird.android.SendBirdNotificationHandler;
import com.sendbird.android.model.BroadcastMessage;
import com.sendbird.android.model.Channel;
import com.sendbird.android.model.FileLink;
import com.sendbird.android.model.Mention;
import com.sendbird.android.model.Message;
import com.sendbird.android.model.MessageModel;
import com.sendbird.android.model.MessagingChannel;
import com.sendbird.android.model.ReadStatus;
import com.sendbird.android.model.SystemMessage;
import com.sendbird.android.model.TypeStatus;
import com.sendbird.android.shadow.com.google.gson.JsonObject;

import java.security.MessageDigest;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String INTENT_KEY_USER_ID = "UserId";
    public static final String INTENT_KEY_USER_NAME = "UserName";

    // Internal variable
    private Button mChatButton;
    private String mReceiverId;
    private String mReceiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SendBird.registerNotificationHandler(new SendBirdNotificationHandler() {
            @Override
            public void onMessagingChannelUpdated(MessagingChannel messagingChannel) {
                Log.v(TAG, "Messaging channel updated " + messagingChannel.getId());
            }

            @Override
            public void onMentionUpdated(Mention mention) {
                Log.v(TAG, "Mention " + mention);
            }
        });

        SendBird.setEventHandler(new SendBirdEventHandler() {
            @Override
            public void onConnect(Channel channel) {
                Log.v(TAG, "Channel connected " + channel.getId());
            }

            @Override
            public void onError(int code) {
                Log.e("SendBird", "Error code: " + code);
            }

            @Override
            public void onChannelLeft(Channel channel) {
                Log.v(TAG, "Channel left " + channel);
            }

            @Override
            public void onMessageReceived(Message message) {
                Log.v(TAG, "Message received :\n" +
                        "\tSender Id" + message.getSenderId() + ",\n" +
                        "\tSender name: " + message.getSenderName() + ",\n" +
                        "\tSenderImageUrl: " + message.getSenderImageUrl() + ",\n" +
                        "\tMessage Id: " + message.getMessageId() + ",\n" +
                        "\tMessage:" + message.getMessage() + ",\n" +
                        "\tData: " + message.getData() + ",\n" +
                        "\tTmp Id: " + message.getTempId() + ",\n" +
                        "\tChannel Id: " + message.getChannelId() + ",\n" +
                        "\tTimeStamp Id: " + message.getTimestamp() + ",\n");
            }

            @Override
            public void onMutedMessageReceived(Message message) {
                Log.v(TAG, "Muted message received " + message);
            }

            @Override
            public void onSystemMessageReceived(SystemMessage systemMessage) {
                switch (systemMessage.getCategory()) {
                    case SystemMessage.CATEGORY_TOO_MANY_MESSAGES:
                        Log.v(TAG, "Too many messages. Please try later.");
                        break;
                    case SystemMessage.CATEGORY_MESSAGING_USER_BLOCKED:
                        Log.v(TAG, "Blocked.");
                        break;
                    case SystemMessage.CATEGORY_MESSAGING_USER_DEACTIVATED:
                        Log.v(TAG, "Deactivated.");
                        break;
                }

            }

            @Override
            public void onBroadcastMessageReceived(BroadcastMessage broadcastMessage) {
                Log.v(TAG, "Boradcase message received " + broadcastMessage);
            }

            @Override
            public void onFileReceived(FileLink fileLink) {
                Log.v(TAG, "File received");
            }

            @Override
            public void onMutedFileReceived(FileLink fileLink) {
                Log.v(TAG, "Muted file received " + fileLink);
            }

            @Override
            public void onReadReceived(ReadStatus readStatus) {
                Log.v(TAG, "Read status received " + readStatus);
            }

            @Override
            public void onTypeStartReceived(TypeStatus typeStatus) {
                Log.v(TAG, "Type status start received " + typeStatus);
            }

            @Override
            public void onTypeEndReceived(TypeStatus typeStatus) {
                Log.v(TAG, "Type status end received " + typeStatus);
            }

            @Override
            public void onAllDataReceived(SendBird.SendBirdDataType type, int count) {
                Log.v(TAG, "All date received " + type + " " + count);
            }

            @Override
            public void onMessageDelivery(boolean sent, String message, String data, String tempId) {
                Log.v(TAG, "Message deliveried " + sent + " " + message + " " + data + " " + tempId);
            }

            @Override
            public void onMessagingStarted(final MessagingChannel messagingChannel) {
                Log.v(TAG, "Message started " + messagingChannel.getId());

                SendBird.queryMessageList(messagingChannel.getUrl()).load(Long.MAX_VALUE, 30, 10, new MessageListQuery.MessageListQueryResult() {
                    @Override
                    public void onResult(List<MessageModel> messageModels) {
                        for (MessageModel model : messageModels) {
                            Log.v(TAG, "Message model " + model.getMessageId());
                        }

                        SendBird.markAsRead(messagingChannel.getUrl());
                        SendBird.join(messagingChannel.getUrl());
                        SendBird.connect();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error starting the messaging ", e);
                    }
                });
            }

            @Override
            public void onMessagingUpdated(MessagingChannel messagingChannel) {
                Log.v(TAG, "Messaging updated " + messagingChannel);
            }

            @Override
            public void onMessagingEnded(MessagingChannel messagingChannel) {
                Log.v(TAG, "Messageing ended " + messagingChannel);
            }

            @Override
            public void onAllMessagingEnded() {
                Log.v(TAG, "All messaging ended");
            }

            @Override
            public void onMessagingHidden(MessagingChannel messagingChannel) {
                Log.v(TAG, "Messaging hidden " + messagingChannel);
            }

            @Override
            public void onAllMessagingHidden() {
                Log.v(TAG, "All messaging hidden ");
            }

        });

        mChatButton = (Button) findViewById(R.id.chat_button);
        mChatButton.setOnClickListener(mOnClickListener);

        // get the user Id
        mReceiverId = getIntent().getExtras().getString(INTENT_KEY_USER_ID);
        mReceiverName = getIntent().getExtras().getString(INTENT_KEY_USER_NAME);
        Log.v(TAG, "The receiver is " + mReceiverId + ":" + mReceiverName);

        SendBird.startMessaging(mReceiverId);
    }


    private View.OnClickListener mOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.chat_button:
                    Log.v(TAG, "Sending a new message");
                    SendBird.send("Simple text");
                    break;
            }
        }
    };
}
