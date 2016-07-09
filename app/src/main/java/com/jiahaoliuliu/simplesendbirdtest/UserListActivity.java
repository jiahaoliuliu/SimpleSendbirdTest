package com.jiahaoliuliu.simplesendbirdtest;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.sendbird.android.MessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdEventHandler;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBirdNotificationHandler;
import com.sendbird.android.UserListQuery;
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
import com.sendbird.android.model.User;

import java.security.MessageDigest;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private static final String TAG = "UserListActivity";

    // Views
    private ListView mUsersListView;

    // Internal variables
    private Context mContext;
    private String mUserId;
    private UserListQuery mUserListQuery;

    private List<User> mUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        // Init variables
        this.mContext = this;

        // Link the views
        mUsersListView = (ListView) findViewById(R.id.users_list_view);

        mUserId = generateDeviceUUID(mContext);
        Log.v(TAG, "The user id is " + mUserId);
        final String userName = "User-" + mUserId.substring(0, 5);
        SendBird.init(mContext, APIKeys.SEND_BIRD_APP_ID);
        SendBird.login(SendBird.LoginOption.build(mUserId).setUserName(userName).setGCMRegToken(""));

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

        // Get the list of users
        mUserListQuery = SendBird.queryUserList();
        mUserListQuery.setLimit(30);

        if(mUserListQuery != null && mUserListQuery.hasNext() && !mUserListQuery.isLoading()) {
            mUserListQuery.next(new UserListQuery.UserListQueryResult() {
                @Override
                public void onResult(List<User> users) {
                    Log.v(TAG, "This is the list of users " + users);
                    // Get the list of users
                    mUsersList = users;
                    String [] userNames = new String[users.size()];
                    for(int i = 0; i < userNames.length; i++) {
                        userNames[i] = users.get(i).getName();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                            android.R.layout.simple_list_item_1, android.R.id.text1,
                            userNames);
                    mUsersListView.setAdapter(adapter);

                    mUsersListView.setOnItemClickListener(new ListView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(mContext, MainActivity.class);
                            intent.putExtra(MainActivity.INTENT_KEY_USER_ID, mUsersList.get(i).getId());
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onError(SendBirdException e) {
                    Log.e(TAG, "Error on getting the users list ", e);
                }
            });
        }
    }

    public String generateDeviceUUID(Context context) {
        String serial = android.os.Build.SERIAL;
        String androidID = Settings.Secure.ANDROID_ID;
        String deviceUUID = serial + androidID;

        /*
         * SHA-1
         */
        MessageDigest digest;
        byte[] result;
        try {
            digest = MessageDigest.getInstance("SHA-1");
            result = digest.digest(deviceUUID.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(String.format("%02X", b));
        }


        return sb.toString();
    }

}
