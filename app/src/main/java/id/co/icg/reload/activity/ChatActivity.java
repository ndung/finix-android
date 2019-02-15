package id.co.icg.reload.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.icg.reload.R;
import id.co.icg.reload.adapter.ChatThreadAdapter;
import id.co.icg.reload.client.ApiUtils;
import id.co.icg.reload.client.Response;
import id.co.icg.reload.client.service.ChatService;
import id.co.icg.reload.model.ChatMessage;
import id.co.icg.reload.model.Reseller;
import id.co.icg.reload.ui.CircleTransformer;
import id.co.icg.reload.util.Global;
import id.co.icg.reload.util.GsonDeserializer;
import id.co.icg.reload.util.NotificationUtils;
import id.co.icg.reload.util.PartUtils;
import id.co.icg.reload.util.Preferences;
import id.co.icg.reload.util.Static;
import okhttp3.MultipartBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatActivity extends BaseActivity {

    private String TAG = ChatActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private ChatThreadAdapter mAdapter;
    private ArrayList<ChatMessage> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText inputMessage;
    private ImageView btnSend;
    private ImageView btnAttach;
    private VideoView videoView;
    private ImageView ivBack;
    private ChatService chatService;
    private TextView tvTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatService = ApiUtils.ChatService(this);

        inputMessage = findViewById(R.id.message);
        btnSend = findViewById(R.id.btn_send);
        ivBack = findViewById(R.id.iv_back);
        recyclerView = findViewById(R.id.recycler_view);
        tvTitle = findViewById(R.id.tv_title);
        videoView = findViewById(R.id.videoView);
        videoView.setVisibility(View.GONE);

        tvTitle.setText("Pesan");

        messageArrayList = new ArrayList<>();

        Reseller user = Preferences.getUser(this);

        mAdapter = new ChatThreadAdapter(this, messageArrayList, user.getId());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Static.PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Static.PUSH_NOTIFICATION));

        btnSend.setOnClickListener(v -> {
            String message = inputMessage.getText().toString();
            if (!TextUtils.isEmpty(message)){
                sendMessage(message);
            } else{
                inputMessage.setError("Masukkan pesan");
            }
        });

        btnAttach = findViewById(R.id.btn_attach);
        btnAttach.setOnClickListener(view -> attach());

        ivBack.setOnClickListener(view -> finish());

        fetchChatThread();

        Preferences.setUnreadMessage(this,false);
    }

    private void attach() {
        TedPermission.with(this)
                .setPermissionListener(
                        new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                pickAttachment();
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {

                            }

                        })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void pickAttachment() {
        EasyImage.openChooserWithDocuments(this, "Pilih", 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImagesPicked(@NonNull List<File> list, EasyImage.ImageSource imageSource, int type) {
                if (list!=null && !list.isEmpty()){
                    uploadFile(list.get(0));
                }
            }


        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // registering the receiver for new notification
        //LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void playVideo(String videoURI){
        videoView.setVisibility(View.VISIBLE);
        showPleaseWaitDialog();

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(videoView);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(videoURI);
            videoView.setMediaController(mediacontroller);
            videoView.setVideoURI(video);

        } catch (Exception e) {
            Log.e(TAG, "Error", e);
        }

        videoView.requestFocus();
        // Close the progress bar and play the video
        videoView.setOnPreparedListener(mp -> {
            dissmissPleaseWaitDialog();
            videoView.start();
        });
        videoView.setOnCompletionListener(mediaPlayer -> {
            dissmissPleaseWaitDialog();
            videoView.setVisibility(View.GONE);
        });
    }

    private void handlePushNotification(Intent intent) {
        ChatMessage message = (ChatMessage) intent.getSerializableExtra("message");
        String chatRoomId = intent.getStringExtra("chat_room_id");

        if (message != null && chatRoomId != null) {
            messageArrayList.add(message);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }
        }
    }

    private void sendMessage(String message){
        showPleaseWaitDialog();

        Reseller user = Preferences.getUser(this);

        Map<String,String> map = new HashMap<>();
        map.put("topicId", user.getId());
        map.put("message", message);

        chatService.sendChatMessage(map).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {

                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        Response body = response.body();
                        JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();

                        ChatMessage chatMessage = gson.fromJson(jsonObject, ChatMessage.class);

                        messageArrayList.add(chatMessage);

                        mAdapter.notifyDataSetChanged();

                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                        inputMessage.setText("");
                    } else if (response.errorBody() != null) {
                        JSONObject jObjError = new JSONObject(response.errorBody().string().trim());
                        showMessage(jObjError.getString("message"));
                    } else {
                        showMessage(Static.SOMETHING_WRONG);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                dissmissPleaseWaitDialog();
                showMessage(t.getMessage());
            }
        });
    }

    private void uploadFile(File path){
        showPleaseWaitDialog();

        Reseller user = Preferences.getUser(this);

        MultipartBody.Part body = PartUtils.prepareFilePart("attachment", path);

        chatService.uploadMessageAttachment(user.getId(), body).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {

                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        Response body = response.body();
                        JsonObject jsonObject = gson.toJsonTree(body.getData()).getAsJsonObject();

                        ChatMessage chatMessage = gson.fromJson(jsonObject, ChatMessage.class);

                        messageArrayList.add(chatMessage);

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }
                    } else if (response.errorBody() != null) {
                        JSONObject jObjError = new JSONObject(response.errorBody().string().trim());
                        showMessage(jObjError.getString("message"));
                    } else {
                        showMessage(Static.SOMETHING_WRONG);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                dissmissPleaseWaitDialog();
                showMessage(t.getMessage());
            }
        });
    }

    private void fetchChatThread(){
        showPleaseWaitDialog();

        Reseller user = Preferences.getUser(this);

        chatService.getChatMessages(user.getId()).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dissmissPleaseWaitDialog();
                try {
                    if (response.isSuccessful()) {
                        messageArrayList.clear();
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDeserializer()).create();
                        JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
                        List<ChatMessage> list = gson.fromJson(jsonObject.getAsJsonArray("data"), new TypeToken<List<ChatMessage>>() {
                        }.getType());

                        for (ChatMessage chatMessage : list){
                            messageArrayList.add(chatMessage);
                        }
                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }
                    } else if (response.errorBody() != null) {
                        JSONObject jObjError = new JSONObject(response.errorBody().string().trim());
                        showMessage(jObjError.getString("message"));
                    } else {
                        showMessage(Static.SOMETHING_WRONG);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                dissmissPleaseWaitDialog();
                showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}