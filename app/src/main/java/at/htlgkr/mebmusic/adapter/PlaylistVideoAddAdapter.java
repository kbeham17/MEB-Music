package at.htlgkr.mebmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import at.htlgkr.mebmusic.R;
import at.htlgkr.mebmusic.playlist.Playlist;

public class PlaylistVideoAddAdapter extends BaseAdapter {

    private Context context;
    private int layoutId;
    private List<Playlist> playlistList;
    private LayoutInflater inflater;

    public PlaylistVideoAddAdapter(Context context, int layoutId, List<Playlist> playlistList){
        this.playlistList = playlistList;
        this.context = context;
        this.layoutId = layoutId;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return playlistList.size();
    }

    @Override
    public Object getItem(int i) {
        return playlistList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Playlist playlist = playlistList.get(i);

        View listItem = (view == null) ? inflater.inflate(this.layoutId, null) : view;

        TextView textPlaylistVideoAdd = listItem.findViewById(R.id.text_playlistvideoadd);
        textPlaylistVideoAdd.setText(playlist.getSnippet().getTitle());


        return listItem;
    }
}
