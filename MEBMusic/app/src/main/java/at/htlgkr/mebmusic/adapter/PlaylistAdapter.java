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
import at.htlgkr.mebmusic.playlist.Playlist;

public class PlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Playlist> playlists;
    private int position;
    private OnPlaylistClickListener mListener;

    public PlaylistAdapter(Context context, List<Playlist> playlists){
        this.context = context;
        this.playlists = playlists;
    }

    public void setOnPlaylistClickListener(OnPlaylistClickListener listener){
        mListener = listener;
    }

    class YoutubeHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        ImageView playlistThumbail;
        TextView title, description, video_count;

        public YoutubeHolder(View itemView, OnPlaylistClickListener listener){
            super(itemView);
            playlistThumbail = itemView.findViewById(R.id.iv_playlist_thumb);
            title = itemView.findViewById(R.id.text_playlist_title);
            description = itemView.findViewById(R.id.text_playlist_description);
            video_count = itemView.findViewById(R.id.text_playlist_count);

            itemView.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onPlaylistClick(position);
                        }
                    }
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.context_playlist_details, Menu.NONE, "Details");
            menu.add(Menu.NONE, R.id.context_playlist_bearbeiten, Menu.NONE, "Edit");
            menu.add(Menu.NONE, R.id.context_playlist_delete, Menu.NONE, "Delete");
        }


        public void setData(Playlist data){
            final String getTitle = data.getSnippet().getTitle();
            int getCount = data.getPlaylistDetails().getItemCount();
            String getThumb = data.getSnippet().getThumbnail().getMedium().getUrl();
            String getDesc = data.getSnippet().getDescription();



            title.setText(getTitle);
            description.setText(getDesc);
            video_count.setText(String.valueOf(getCount));
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
        return new YoutubeHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        YoutubeHolder yth = (YoutubeHolder) holder;
        yth.setData(playlist);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });

    }

    public interface OnPlaylistClickListener{
        void onPlaylistClick(int position);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}




