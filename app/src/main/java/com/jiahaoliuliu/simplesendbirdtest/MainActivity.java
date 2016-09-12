package com.jiahaoliuliu.simplesendbirdtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String INTENT_KEY_USER_ID = "UserId";
    public static final String INTENT_KEY_USER_NAME = "UserName";

    // The unique identifier for the group channel handler
    private static final String identifier = "SimpleSendbirdTest";

    // All the channels must be distinct. This will allow them to be reused and get previous messages
    private static final boolean IS_DISTINCT = true;

    // Maximum load 30 messages
    private static final int MAXIMUM_MESSAGES_LOAD = 30;

    // Views
    private RecyclerView mMessagesRecyclerView;
    private EditText mMessageBoxEditText;
    private Button mViewButton;

    // Internal variable
    private Context mContext;
    private String mReceiverId;
    private String mReceiverName;
    private List<UserMessage> mMessagesLit;
    private RecyclerView.LayoutManager mLayoutManager;
    private MessagesListAdapter mMessagesListAdapter;
    private GroupChannel mGroupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        registerNotificationsAndEvents();

        // get the user Id
        mReceiverId = getIntent().getExtras().getString(INTENT_KEY_USER_ID);
        mReceiverName = getIntent().getExtras().getString(INTENT_KEY_USER_NAME);
        Log.v(TAG, "The receiver is " + mReceiverId + ":" + mReceiverName);

        // Init the variables
        this.mContext = this;

        // Link the views
        mMessagesRecyclerView = (RecyclerView) findViewById(R.id.messages_recycler_view);
        mMessageBoxEditText = (EditText) findViewById(R.id.messages_box_edit_text);

        mViewButton = (Button) findViewById(R.id.send_button);
        mViewButton.setOnClickListener(mOnClickListener);

        // Set the name of the receiver
        getSupportActionBar().setTitle(mReceiverName);

        // Set the messages list layout
        mLayoutManager = new LinearLayoutManager(this);
        mMessagesRecyclerView.setLayoutManager(mLayoutManager);

        // Set channel handler
        SendBird.addChannelHandler(identifier, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                if (mGroupChannel != null && baseChannel.getUrl().equals(mGroupChannel.getUrl())) {
                    Log.v(TAG, "New message received " + baseMessage.getMessageId());
                    if (mMessagesListAdapter != null && baseMessage instanceof UserMessage) {
                        printUserMesage((UserMessage) baseMessage);
                        mGroupChannel.markAsRead();
                        mMessagesListAdapter.addMessage((UserMessage) baseMessage);
                    }
                }
            }

            @Override
            public void onReadReceiptUpdated(GroupChannel groupChannel) {
                Log.v(TAG, "The read receipt has been updated for the channel " + groupChannel.getUrl());
            }

            @Override
            public void onTypingStatusUpdated(GroupChannel groupChannel) {
                Log.v(TAG, "The typing status has been updated for the channel " + groupChannel.getUrl());
            }

            @Override
            public void onUserJoined(GroupChannel groupChannel, User user) {
                Log.v(TAG, "New user joined to the channel " + groupChannel.getUrl() + ", " + user.getNickname());
            }

            @Override
            public void onUserLeft(GroupChannel groupChannel, User user) {
                Log.v(TAG, "User left on the channel " + groupChannel.getUrl() + ", " + user.getNickname());
            }
        });

        // Create channel
        List<String> userIdsList = new ArrayList<String>();
        userIdsList.add(mReceiverId);
        GroupChannel.createChannelWithUserIds(userIdsList, IS_DISTINCT, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(mContext, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                mGroupChannel = groupChannel;

                // Load messages
                loadMessages(groupChannel);
            }
        });
    }

    private void loadMessages(GroupChannel groupChannel) {
        PreviousMessageListQuery previousMessageListQuery = groupChannel.createPreviousMessageListQuery();
        previousMessageListQuery.load(MAXIMUM_MESSAGES_LOAD, false, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult(List<BaseMessage> baseMessagesList, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(mContext, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                mMessagesLit = new ArrayList<UserMessage>();
                mMessagesListAdapter = new MessagesListAdapter(SendBird.getCurrentUser().getUserId(), mMessagesLit);
                mMessagesRecyclerView.setAdapter(mMessagesListAdapter);

                for (BaseMessage baseMessage : baseMessagesList) {
                    if (baseMessage instanceof UserMessage) {
                        UserMessage userMessage = (UserMessage) baseMessage;
                        printUserMesage(userMessage);
                        mMessagesListAdapter.addMessage(userMessage);
                    }
                }
            }
        });
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
//                case R.id.send_button:
//                    String textToSend = mMessageBoxEditText.getText().toString().trim();
//                    if (!TextUtils.isEmpty(textToSend)) {
//                        SendBird.send(textToSend);
//                        Log.v(TAG, "Sending message to " + mReceiverName + ":" + textToSend);
//                        mMessageBoxEditText.setText("");
//                    }
//                    break;
            }
        }
    };

//    private void registerNotificationsAndEvents() {
//        SendBird.registerNotificationHandler(new SendBirdNotificationHandler() {
//            @Override
//            public void onMessagingChannelUpdated(MessagingChannel messagingChannel) {
//                Log.v(TAG, "Messaging channel updated " + messagingChannel.getId());
//            }
//
//            @Override
//            public void onMentionUpdated(Mention mention) {
//                Log.v(TAG, "Mention " + mention);
//            }
//        });
//
//        SendBird.setEventHandler(new SendBirdEventHandler() {
//            @Override
//            public void onConnect(Channel channel) {
//                Log.v(TAG, "Channel connected " + channel.getId());
//            }
//
//            @Override
//            public void onError(int code) {
//                Log.e("SendBird", "Error code: " + code);
//            }
//
//            @Override
//            public void onChannelLeft(Channel channel) {
//                Log.v(TAG, "Channel left " + channel);
//            }
//
//            @Override
//            public void onMessageReceived(Message message) {
//                Log.v(TAG, "Message received :\n" +
//                        "\tSender Id" + message.getSenderId() + ",\n" +
//                        "\tSender name: " + message.getSenderName() + ",\n" +
//                        "\tSenderImageUrl: " + message.getSenderImageUrl() + ",\n" +
//                        "\tMessage Id: " + message.getMessageId() + ",\n" +
//                        "\tMessage:" + message.getMessage() + ",\n" +
//                        "\tData: " + message.getData() + ",\n" +
//                        "\tTmp Id: " + message.getTempId() + ",\n" +
//                        "\tChannel Id: " + message.getChannelId() + ",\n" +
//                        "\tTimeStamp Id: " + message.getTimestamp() + ",\n");
//
//                mMessagesListAdapter.addMessage(message);
//                // Move the list to the last item
//                mMessagesRecyclerView.smoothScrollToPosition(mMessagesListAdapter.getItemCount());
//            }
//
//            @Override
//            public void onMutedMessageReceived(Message message) {
//                Log.v(TAG, "Muted message received " + message);
//            }
//
//            @Override
//            public void onSystemMessageReceived(SystemMessage systemMessage) {
//                switch (systemMessage.getCategory()) {
//                    case SystemMessage.CATEGORY_TOO_MANY_MESSAGES:
//                        Log.v(TAG, "Too many messages. Please try later.");
//                        break;
//                    case SystemMessage.CATEGORY_MESSAGING_USER_BLOCKED:
//                        Log.v(TAG, "Blocked.");
//                        break;
//                    case SystemMessage.CATEGORY_MESSAGING_USER_DEACTIVATED:
//                        Log.v(TAG, "Deactivated.");
//                        break;
//                }
//
//            }
//
//            @Override
//            public void onBroadcastMessageReceived(BroadcastMessage broadcastMessage) {
//                Log.v(TAG, "Boradcase message received " + broadcastMessage);
//            }
//
//            @Override
//            public void onFileReceived(FileLink fileLink) {
//                Log.v(TAG, "File received");
//            }
//
//            @Override
//            public void onMutedFileReceived(FileLink fileLink) {
//                Log.v(TAG, "Muted file received " + fileLink);
//            }
//
//            @Override
//            public void onReadReceived(ReadStatus readStatus) {
//                Log.v(TAG, "Read status received " + readStatus);
//            }
//
//            @Override
//            public void onTypeStartReceived(TypeStatus typeStatus) {
//                Log.v(TAG, "Type status start received " + typeStatus);
//            }
//
//            @Override
//            public void onTypeEndReceived(TypeStatus typeStatus) {
//                Log.v(TAG, "Type status end received " + typeStatus);
//            }
//
//            @Override
//            public void onAllDataReceived(SendBird.SendBirdDataType type, int count) {
//                Log.v(TAG, "All date received " + type + " " + count);
//            }
//
//            @Override
//            public void onMessageDelivery(boolean sent, String message, String data, String tempId) {
//                Log.v(TAG, "Message deliveried " + sent + " " + message + " " + data + " " + tempId);
//            }
//
//            @Override
//            public void onMessagingStarted(final MessagingChannel messagingChannel) {
//                Log.v(TAG, "Message started " + messagingChannel.getId());
//
//                mMessagesListAdapter = new MessagesListAdapter(SendBird.getUserId(), mMessagesLit);
//                mMessagesRecyclerView.setAdapter(mMessagesListAdapter);
//
//                SendBird.queryMessageList(messagingChannel.getUrl()).load(Long.MAX_VALUE, 30, 10, new MessageListQuery.MessageListQueryResult() {
//                    @Override
//                    public void onResult(List<MessageModel> messageModels) {
//                        for (MessageModel model : messageModels) {
//                            Log.v(TAG, "Message model " + model.getMessageId());
//                            if (model instanceof Message) {
//                                Log.v(TAG, "The message model is also instance of Message" + model.getMessageId());
//                                mMessagesListAdapter.addMessage((Message) model);
//                            } else {
//                                Log.v(TAG, "The message model is not instance of Message " + model.getMessageId());
//                            }
//                        }
//
//                        // Move the list to the last item
//                        mMessagesRecyclerView.smoothScrollToPosition(mMessagesListAdapter.getItemCount());
//
//                        SendBird.markAsRead(messagingChannel.getUrl());
//                        SendBird.join(messagingChannel.getUrl());
//                        SendBird.connect();
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        Log.e(TAG, "Error starting the messaging ", e);
//                    }
//                });
//            }
//
//            @Override
//            public void onMessagingUpdated(MessagingChannel messagingChannel) {
//                Log.v(TAG, "Messaging updated " + messagingChannel);
//            }
//
//            @Override
//            public void onMessagingEnded(MessagingChannel messagingChannel) {
//                Log.v(TAG, "Messageing ended " + messagingChannel);
//            }
//
//            @Override
//            public void onAllMessagingEnded() {
//                Log.v(TAG, "All messaging ended");
//            }
//
//            @Override
//            public void onMessagingHidden(MessagingChannel messagingChannel) {
//                Log.v(TAG, "Messaging hidden " + messagingChannel);
//            }
//
//            @Override
//            public void onAllMessagingHidden() {
//                Log.v(TAG, "All messaging hidden ");
//            }
//        });
//    }

    private void printUserMesage(UserMessage userMessage) {
        Log.v(TAG, "User message {" +
                "Sender: " + userMessage.getSender().getNickname() +
                ", Message: " + userMessage.getMessage() +
                ", Data: " + userMessage.getData() +
                ", RequestId: " + userMessage.getRequestId() +
                ", Created at: " + userMessage.getCreatedAt() +
                "}"
        );
    }
}
