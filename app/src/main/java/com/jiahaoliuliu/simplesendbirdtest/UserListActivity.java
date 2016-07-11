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

        // Get the list of users
        mUserListQuery = SendBird.queryUserList();
        mUserListQuery.setLimit(30);

        if(mUserListQuery != null && mUserListQuery.hasNext() && !mUserListQuery.isLoading()) {
            mUserListQuery.next(new UserListQuery.UserListQueryResult() {
                @Override
                public void onResult(final List<User> users) {
                    // Get the user list
                    Log.v(TAG, "The list of users is ");
                    for (User user : users) {
                        Log.v(TAG, user.getId() + ":" + user.getName());
                    }

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
                            User userSelected = mUsersList.get(i);
                            Log.v(TAG, "The user has selected " + userSelected.getId() + ":" + userSelected.getName());

                            Intent intent = new Intent(mContext, MainActivity.class);
                            intent.putExtra(MainActivity.INTENT_KEY_USER_ID, userSelected.getId());
                            intent.putExtra(MainActivity.INTENT_KEY_USER_NAME, userSelected.getName());
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
            Log.e(TAG, "Error generating the devices UUID", e);
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(String.format("%02X", b));
        }

        return sb.toString();
    }

}
