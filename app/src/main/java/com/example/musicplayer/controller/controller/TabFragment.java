package com.example.musicplayer.controller.controller;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.model.Album;
import com.example.musicplayer.controller.model.Artist;
import com.example.musicplayer.controller.model.State;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {
    public static final String ARG_POSITION_TAB = "arg_position_tab";
    public static final int RUNTIME_PERMISSION_CODE = 0;
    private MusicRepository mMusicRepository;
    private itemClickCallBacks mCallBacks;
    private BottomSheetBehavior behavior;
    private TextView mTextViewDuration;
    private RecyclerView mRecyclerView;
    private boolean musicBound = false;
    TextView singerName, musicName;
    private Intent playIntent;
    private View mView, mView2;
    private adapter mAdapter;
    FrameLayout frameLayout;
    private State mTabState;
    ImageView singerImage;

    public static TabFragment newInstance(State state) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_POSITION_TAB, state);
        TabFragment fragment = new TabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidRuntimePermission();
        mMusicRepository = MusicRepository.getInstance(getContext());
        mTabState = (State) getArguments().getSerializable(ARG_POSITION_TAB);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicRepository.MusicBinder binder = (MusicRepository.MusicBinder) iBinder;
            mMusicRepository = binder.getService();
            mMusicRepository.loadMusic();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        if(playIntent == null){
            playIntent = new Intent(getContext() , MusicRepository.class);
            getContext().bindService(playIntent,mServiceConnection,Context.BIND_AUTO_CREATE);
            getContext().startService(playIntent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        initUI(view);
        setAdapter();
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

    private void setAdapter() {

        if (mTabState == State.MUSICS) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAdapter = new adapter(mMusicRepository.getMusic());
            mRecyclerView.setAdapter(mAdapter);
        } else if (mTabState == State.ALBUM) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            mAdapter = new adapter(mMusicRepository.getAlbum());
            mRecyclerView.setAdapter(mAdapter);
        } else if (mTabState == State.SINGERS) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAdapter = new adapter(mMusicRepository.getArtist());
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    private void initUI(View view) {

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTextViewDuration = view.findViewById(R.id.text_view_duration);
        mView2 = view.findViewById(R.id.line_divider2);
        mView = view.findViewById(R.id.line_divider);
        frameLayout = view.findViewById(R.id.bottom_sheet_layout);
        behavior = BottomSheetBehavior.from(frameLayout);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        singerName = view.findViewById(R.id.txt_music);
        musicName = view.findViewById(R.id.txt_singer);
        singerImage = view.findViewById(R.id.singer_picture);

    }

    private class MusicHolder extends RecyclerView.ViewHolder {

        private com.example.musicplayer.controller.model.Music mMusic;
        private TextView mTextViewMusic, mTextViewSinger;
        private ImageView mImageView;

        public MusicHolder(@NonNull View itemView) {
            super(itemView);
            findView(itemView);
        }

        private void findView(@NonNull View itemView) {
            mTextViewMusic = itemView.findViewById(R.id.text_view_music);
            mImageView = itemView.findViewById(R.id.image_view_music);
            mTextViewSinger = itemView.findViewById(R.id.text_view_singer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint({"ResourceAsColor", "WrongConstant"})
                @Override
                public void onClick(View view) {
                    mCallBacks.MusicHolderClick(mTabState, mMusic);
                }
            });
        }

        public void bind(com.example.musicplayer.controller.model.Music music) {
            this.mMusic = music;
            mTextViewMusic.setText(music.getNameMusic());
            if (mMusic.getmAlbumPath()== null){
                mImageView.setImageResource(R.drawable.music_img);

            }else{
                mImageView.setImageBitmap(BitmapFactory.decodeFile(mMusic.getmAlbumPath()));
            }
            mTextViewDuration.setText(TabFragment.this.mMusicRepository.formatDuration(music.getmDuration()));
        }
    }

    private class AlbumHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private Album mAlbum;
        private TextView mTextViewAlbum, mTextViewSinger;
        private ImageView mImageViewAlbum;

        public AlbumHolder(@NonNull View itemView) {
            super(itemView);

            findView(itemView);
        }

        private void findView(@NonNull View itemView) {
            mCardView = itemView.findViewById(R.id.card_view);
            mTextViewAlbum = itemView.findViewById(R.id.text_view_album);
            mImageViewAlbum = itemView.findViewById(R.id.image_view_album);
            mTextViewSinger = itemView.findViewById(R.id.text_view_singer_album);

            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.activity_view_pager, DetailMusicFragment
                                    .newInstance(mAlbum.getAlbumId(), mTabState))
                            .addToBackStack(null).commit();
                }
            });
        }

        public void bind(Album album) {
            this.mAlbum = album;
            mTextViewAlbum.setText(album.getAlbumName());
            mTextViewSinger.setText(album.getArtistName());
            if (mAlbum.getPath() != null){
                mImageViewAlbum.setImageBitmap(BitmapFactory.decodeFile(mAlbum.getPath()));
            }else{
                mImageViewAlbum.setImageResource(R.drawable.music_img);
            }
        }
    }

    private class ArtistHolder extends RecyclerView.ViewHolder {
        private Artist mArtist;
        private TextView mTextViewArtist;
        private ImageView mImageViewArtist;

        public ArtistHolder(@NonNull View itemView) {
            super(itemView);

            findView(itemView);
        }

        private void findView(@NonNull View itemView) {
            mTextViewArtist = itemView.findViewById(R.id.text_view_singer);
            mImageViewArtist = itemView.findViewById(R.id.image_view_singer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.activity_view_pager, DetailMusicFragment.newInstance(mArtist.getIdArtist(), mTabState))
                            .addToBackStack(null).commit();
                }
            });
        }

        public void bind(Artist artist) {
            this.mArtist = artist;
            mTextViewArtist.setText(artist.getArtistName());
            //  mImageView.setImageBitmap(BitmapFactory.decodeFile(mArtist.getPath()));
        }
    }

    private class adapter extends RecyclerView.Adapter {

        private List mItems;

        adapter(List items) {
            mItems = items;
        }

        public int getItemViewType(int position) {
            if (mItems.get(position) instanceof com.example.musicplayer.controller.model.Music) {
                return 0;
            } else if (mItems.get(position) instanceof Album) {
                return 1;
            } else if (mItems.get(position) instanceof Artist)
                return 2;
            return -1;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            RecyclerView.ViewHolder viewHolder;
            switch (viewType) {
                case 0:
                    View view = inflater.inflate(R.layout.list_item_music, parent, false);
                    viewHolder = new MusicHolder(view);
                    break;
                case 1:
                    View view1 = inflater.inflate(R.layout.list_item_album, parent, false);
                    viewHolder = new AlbumHolder(view1);
                    break;
                case 2:
                    View view2 = inflater.inflate(R.layout.list_item_artist, parent, false);
                    viewHolder = new ArtistHolder(view2);
                    break;
                default:
                    viewHolder = null;
                    break;
            }
            return viewHolder;

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (mTabState == State.MUSICS && holder.getItemViewType() == 0) {
                ((MusicHolder) holder).bind((com.example.musicplayer.controller.model.Music) mItems.get(position));
            } else if (mTabState == State.ALBUM && holder.getItemViewType() == 1) {
                ((AlbumHolder) holder).bind((Album) mItems.get(position));
            } else if (mTabState == State.SINGERS && holder.getItemViewType() == 2) {
                ((ArtistHolder) holder).bind((Artist) mItems.get(position));
            }

        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    public void AndroidRuntimePermission() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(getContext());
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(
                                    getActivity(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    RUNTIME_PERMISSION_CODE
                            );
                        }
                    });

                    alert_builder.setNeutralButton("Cancel", null);
                    AlertDialog dialog = alert_builder.create();
                    dialog.show();

                } else {

                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            0);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getActivity(), "permission Denied!", Toast.LENGTH_SHORT).show();
                    AndroidRuntimePermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public interface itemClickCallBacks {
        void MusicHolderClick(State tabState, com.example.musicplayer.controller.model.Music music);
    }


}
