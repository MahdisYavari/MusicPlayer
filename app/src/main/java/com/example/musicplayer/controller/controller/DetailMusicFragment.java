package com.example.musicplayer.controller.controller;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.musicplayer.R;
import com.example.musicplayer.controller.model.State;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailMusicFragment extends Fragment {
    public static final String ARG_ID = "arg_id";
    public static final String ARG_MUSIC_NAME = "arg_musicName";
    public static final String ARG_TAB_STATE = "arg_tab_state";
    MusicRepository mMusicRepository;
    private RecyclerView mRecyclerView;
    private MusicAdapter mAdapter;
    private long mId;
    private State mState;
    private itemClickCallBacks mCallBacks;


    public DetailMusicFragment()  {
        // Required empty public constructor
    }

    public static DetailMusicFragment newInstance(long id,State state) {

        Bundle args = new Bundle();
        args.putLong(ARG_ID,id);
        args.putSerializable(ARG_TAB_STATE,state);
        //args.putString(ARG_MUSIC_NAME,musicName);
        DetailMusicFragment fragment = new DetailMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMusicRepository = MusicRepository.getInstance(getContext());
        mId = getArguments().getLong(ARG_ID);
        mState = (State) getArguments().getSerializable(ARG_TAB_STATE);
        // mMusicName = getArguments().getString(ARG_MUSIC_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_music, container, false);
        initUi(view);
        setUpAdapter();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof itemClickCallBacks) {
            mCallBacks = (itemClickCallBacks) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBacks = null;
    }

    private void setUpAdapter() {
        if(State.SINGERS == mState) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mAdapter = new MusicAdapter(mMusicRepository.getMusicOfArtist(mId));
            mRecyclerView.setAdapter(mAdapter);
        }else if(State.ALBUM == mState) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mAdapter = new MusicAdapter(mMusicRepository.getMusicOfAlbum(mId));
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void initUi(View view) {
        mRecyclerView=view.findViewById(R.id.recycler_view_album_detail);

    }
    private class MusicArtistHolder extends RecyclerView.ViewHolder{
        private com.example.musicplayer.controller.model.Music mMusic;
        private ImageView artistImage;
        private TextView singerName,musicName;

        public MusicArtistHolder(@NonNull View itemView) {
            super(itemView);
            artistImage = itemView.findViewById(R.id.image_view_music);
            singerName = itemView.findViewById(R.id.text_view_singer);
            musicName = itemView.findViewById(R.id.text_view_music);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallBacks.musicClick(mMusic);
                }
            });
        }

        public void bind(com.example.musicplayer.controller.model.Music music){
            this.mMusic = music;
            singerName.setText(mMusic.getNameSinger());
            musicName.setText(mMusic.getNameMusic());
            artistImage.setImageBitmap(BitmapFactory.decodeFile(mMusic.getmAlbumPath()));
        }
    }


    private class MusicAdapter extends RecyclerView.Adapter<MusicArtistHolder> {
        List<com.example.musicplayer.controller.model.Music> musicList;

        public MusicAdapter(List<com.example.musicplayer.controller.model.Music> musicList) {
            this.musicList = musicList;
        }

        @NonNull
        @Override
        public MusicArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_music, parent, false);
            return new MusicArtistHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull MusicArtistHolder holder, int position) {
            holder.bind(musicList.get(position));
        }

        @Override
        public int getItemCount() {
            return musicList.size();
        }
    }
    public interface itemClickCallBacks{
        void musicClick(com.example.musicplayer.controller.model.Music music);
    }



}
