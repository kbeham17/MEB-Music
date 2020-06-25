package at.htlgkr.mebmusic.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
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

public class SearchVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Video> videoList;
    private int position;
    private SearchVideoAdapter.OnVideoClickListener mListener;

    public SearchVideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    public void setOnVideoClickListener(SearchVideoAdapter.OnVideoClickListener listener){
        mListener = listener;
    }

    class YoutubeHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        ImageView thumbnail;
        TextView title, date;

        public YoutubeHolder(View itemView, SearchVideoAdapter.OnVideoClickListener listener){
            super(itemView);
            thumbnail = itemView.findViewById(R.id.iv_search_video_thumb);
            title = itemView.findViewById(R.id.text_search_video_title);
            date = itemView.findViewById(R.id.text_search_video_date);

            itemView.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onVideoClick(position);
                        }
                    }
                }
            });
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

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.context_search_video_add, Menu.NONE, "Add to Playlist");
            menu.add(Menu.NONE, R.id.context_search_video_details, Menu.NONE, "Details");
            menu.add(Menu.NONE, R.id.context_search_video_comment, Menu.NONE, "Comment");

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_item_search_video, parent, false);
        return new YoutubeHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Video video = videoList.get(position);
        YoutubeHolder yth = (YoutubeHolder) holder;
        yth.setData(video);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });
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

    public void setPosition(int position) {
        this.position = position;
    }

    public interface OnVideoClickListener{
        void onVideoClick(int position);
    }
}
