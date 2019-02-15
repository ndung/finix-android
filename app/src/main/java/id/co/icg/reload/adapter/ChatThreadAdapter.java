package id.co.icg.reload.adapter;

import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import id.co.icg.reload.R;
import id.co.icg.reload.activity.ChatActivity;
import id.co.icg.reload.model.ChatMessage;
import id.co.icg.reload.ui.TouchImageView;
import id.co.icg.reload.util.Static;

public class ChatThreadAdapter extends RecyclerView.Adapter<ChatThreadAdapter.ViewHolder> {

    private static String TAG = ChatThreadAdapter.class.getSimpleName();

    private String userId;
    private int SELF = 100;
    private static String today;

    private ChatActivity mContext;
    private ArrayList<ChatMessage> messageArrayList;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp;
        ImageView imageView, videoView;

        public ViewHolder(View view) {
            super(view);
            message = itemView.findViewById(R.id.message);
            timestamp = itemView.findViewById(R.id.timestamp);
            imageView = itemView.findViewById(R.id.img);
            videoView = itemView.findViewById(R.id.video);
        }
    }


    public ChatThreadAdapter(ChatActivity mContext, ArrayList<ChatMessage> messageArrayList, String userId) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.userId = userId;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.IMAGE_DIRECTORY_NAME);
        //if (!file.exists()) {
        //    file.mkdirs();
        //}
        //path = file.getPath();
    }

    @Override
    public ChatThreadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chat_item_self, parent, false);
        } else {
            // others message
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chat_item_other, parent, false);
        }


        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageArrayList.get(position);
        if (message.getUserId().equals(userId)) {
            return SELF;
        }

        return position;
    }

    @Override
    public void onBindViewHolder(final ChatThreadAdapter.ViewHolder holder, int position) {
        final ChatMessage message = messageArrayList.get(position);

        boolean imageFile = message.getMessage().toLowerCase().endsWith(".jpg") || message.getMessage().toLowerCase().endsWith(".png") ||
                message.getMessage().toLowerCase().endsWith(".jpeg") || message.getMessage().toLowerCase().endsWith(".gif") ||
                message.getMessage().toLowerCase().endsWith(".tif") || message.getMessage().toLowerCase().endsWith(".bmp");

        boolean videoFile = message.getMessage().toLowerCase().endsWith(".mkv") || message.getMessage().toLowerCase().endsWith(".mp4") ||
                message.getMessage().toLowerCase().endsWith(".mpg") || message.getMessage().toLowerCase().endsWith(".avi") ||
                message.getMessage().toLowerCase().endsWith(".mov") || message.getMessage().toLowerCase().endsWith(".mpeg");

        boolean otherFileExt = message.getMessage().toLowerCase().endsWith(".pdf") || message.getMessage().toLowerCase().endsWith(".doc") ||
                message.getMessage().toLowerCase().endsWith(".docx") || message.getMessage().toLowerCase().endsWith(".xls") ||
                message.getMessage().toLowerCase().endsWith(".xlsx") || message.getMessage().toLowerCase().endsWith(".txt");

        if (imageFile) {
            String url = Static.BASE_URL+"files/"+message.getMessage().trim();

            //String uriString = path + File.separator + fileName;

            holder.imageView.setVisibility(View.VISIBLE);

            //final File imgFile = new File(uriString);
            //final boolean isFileExist = imgFile.exists();

            //if (isFileExist) {
            //    Picasso.with(mContext).load(imgFile).into(holder.imageView);
            //} else {
                Picasso.with(mContext).load(url).into(holder.imageView);
            //}
            holder.message.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.GONE);
            holder.imageView.setOnClickListener(view -> {
                final Dialog nagDialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                nagDialog.setCancelable(false);
                nagDialog.setContentView(R.layout.activity_preview_image);
                TouchImageView ivPreview = nagDialog.findViewById(R.id.iv_preview_image);
                //if (isFileExist) {
                //    Picasso.with(mContext).load(imgFile).into(ivPreview);
                //} else {
                    Picasso.with(mContext).load(url).into(ivPreview);
                //}
                ivPreview.setOnClickListener(arg0 -> nagDialog.dismiss());
                nagDialog.show();
            });
        } else if (videoFile) {
            String str[] = message.getMessage().trim().split("\\;");
            String msgText = str[0];
            String thumbnail = str[1];
            final String videoURL = str[2];
            holder.message.setVisibility(View.VISIBLE);
            holder.message.setText(msgText);
            holder.videoView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(thumbnail).into(holder.imageView);
            holder.imageView.setOnClickListener(view -> mContext.playVideo(videoURL));
        } else {
            String msg = message.getMessage();
            if (otherFileExt){
                msg = Static.BASE_URL+"files/"+message.getMessage().trim()+msg;
            }
            holder.message.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.message.setText(msg);
        }
        String timestamp = getTimeStamp(message.getCreated());

        if (message.getUserId() != null)
            timestamp = message.getUserId() + ", " + timestamp;

        holder.timestamp.setText(timestamp);
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public static String getTimeStamp(Date date) {
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
        String dateToday = todayFormat.format(date);
        SimpleDateFormat format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
        String date1 = format.format(date);
        timestamp = date1.toString();


        return timestamp;
    }
}