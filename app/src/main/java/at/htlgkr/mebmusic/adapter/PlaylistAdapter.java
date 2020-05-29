package at.htlgkr.mebmusic.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.playlist.Playlist;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class PlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Playlist> playlists;

    public PlaylistAdapter(Context context, List<Playlist> playlists){
        this.context = context;
        this.playlists = playlists;
    }

    class YoutubeHolder extends RecyclerView.ViewHolder{
        ImageView playlistThumbail;
        TextView title, video_count1, video_count2;

        public YoutubeHolder(View itemView){
            super(itemView);
            playlistThumbail = itemView.findViewById(R.id.iv_playlist_thumb);
            title = itemView.findViewById(R.id.text_playlist_title);
            video_count1 = itemView.findViewById(R.id.text_video_count1);
            video_count2 = itemView.findViewById(R.id.text_video_count2);
        }

        public void setData(Playlist data){
            final String getTitle = data.getSnippet().getTitle();
            int getCount = data.getPlaylistDetails().getItemCount();
            String getThumb = data.getSnippet().getThumbnail().getMedium().getUrl();

            itemView.setOnClickListener((v) ->{
                Toast.makeText(context, getTitle, Toast.LENGTH_SHORT).show();
            });

            title.setText(getTitle);
            video_count1.setText(String.valueOf(getCount)+ " videos");
            video_count2.setText(String.valueOf(getCount));
            Picasso.get()
                    .load(getThumb)
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    .centerCrop()
                    .into(playlistThumbail, new Callback() {
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
        LayoutInflater infalter = LayoutInflater.from(context);
        View view = infalter.inflate(R.layout.row_item_playlist, parent, false);
        return new YoutubeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        YoutubeHolder yth = (YoutubeHolder) holder;
        yth.setData(playlist);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }
}




