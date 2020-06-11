package at.htlgkr.mebmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.videos.Video;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Video> videoList;
    private int position;

    public VideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    class YoutubeHolder extends RecyclerView.ViewHolder{
        ImageView thumbnail;
        TextView title, date;

        public YoutubeHolder(View itemView){
            super(itemView);
            thumbnail = itemView.findViewById(R.id.iv_video_thumb);
            title = itemView.findViewById(R.id.text_video_title);
            date = itemView.findViewById(R.id.text_video_date);
        }

        public void setData(Video data){
            final String getTitle = data.getSnippet().getTitle();
            String getDate = data.getSnippet().getPublishedAt();
            String getThumb = data.getSnippet().getThumbnail().getMedium().getUrl();

            title.setText(getTitle);
            date.setText(getDate);
            Picasso.get()
                    .load(getThumb)
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    .centerCrop()
                    .into(thumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                        }
                    });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_item_video, parent, false);
        return new YoutubeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Video video = videoList.get(position);
        YoutubeHolder yth = (YoutubeHolder) holder;
        yth.setData(video);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public int getPosition() {
        return position;
    }


}
