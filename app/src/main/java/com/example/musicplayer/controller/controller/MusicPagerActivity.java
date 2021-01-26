package com.example.musicplayer.controller.controller;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.model.State;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MusicPagerActivity extends AppCompatActivity implements MusicRepository.BitBoxCallBacks,
        TabFragment.itemClickCallBacks,DetailMusicFragment.itemClickCallBacks {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private BottomSheetBehavior behavior;
    private ImageView imageButtonPrevious, imageButtonNext,
            imageButtonPlay, imageShuffle, imageRepeatAll,singerImage;
    private MusicRepository mMusicRepository;
    boolean flagRepeat, flagShuffle;
    private FrameLayout frameLayout;
    private TextView singerName, musicName;
    Handler handler = new Handler();
    private SeekBar mSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_pager_activity);
        mMusicRepository = MusicRepository.getInstance(this);

        initUi();
        initListeners();
        handler.postDelayed(UpdateSongTime, 100);

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            ArrayList<String> mStateList = new ArrayList<>();

            {
                mStateList.add("MUSICS");
                mStateList.add("ALBUM");
                mStateList.add("SINGERS");
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return TabFragment.newInstance(State.valueOf(mStateList.get(position)));

            }

            @Override
            public int getCount() {
                return mStateList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mStateList.get(position);
            }
        });
    }
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            int time = mMusicRepository.getMediaPlayer().getCurrentPosition();
            handler.postDelayed(this, 100);
            mSeekBar.setMax(mMusicRepository.getMediaPlayer().getDuration());
            mSeekBar.setProgress(time);
        }
    };

    private void initListeners() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMusicRepository.getMediaPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mMusicRepository.getMediaPlayer().isPlaying()) {
                    mMusicRepository.getMediaPlayer().pause();
                    imageButtonPlay.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.image_music, null));
                } else {
                    mMusicRepository.getMediaPlayer().start();
                    imageButtonPlay.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.image_pause, null));
                }
            }
        });
        imageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flagRepeat == true) {
                    mMusicRepository.repeatOne();
                } else if (flagRepeat == false && flagShuffle == true) {
                    mMusicRepository.shuffleMusic();
                } else
                    mMusicRepository.nextMusic();
            }
        });

        imageShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flagShuffle == false)
                    flagShuffle = true;
                else
                    flagShuffle = false;
            }
        });
        imageButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flagRepeat == true) {
                    mMusicRepository.repeatOne();
                } else if (flagRepeat == false && flagShuffle == true) {
                    mMusicRepository.shuffleMusic();
                } else
                    mMusicRepository.prevMusic();
            }
        });
        imageRepeatAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flagRepeat == false) {
                    imageRepeatAll.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.image_repeat_one, null));
                    flagRepeat = true;
                } else {
                    imageRepeatAll.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.image_repeat_all, null));
                    flagRepeat = false;
                }
            }
        });
    }

    private void initUi() {
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        imageButtonNext = findViewById(R.id.next_button);
        imageButtonPrevious = findViewById(R.id.previous_button);
        imageShuffle = findViewById(R.id.shuffle_music);
        imageRepeatAll = findViewById(R.id.image_view_repeatAll);
        imageButtonPlay = findViewById(R.id.play_button);
        frameLayout = findViewById(R.id.bottom_sheet_layout);
        behavior = BottomSheetBehavior.from(frameLayout);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        singerName =findViewById(R.id.txt_music);
        musicName =findViewById(R.id.txt_singer);
        singerImage = findViewById(R.id.singer_picture);
        mSeekBar = findViewById(R.id.seek_bar);
        mMusicRepository.setCallBacks(this);

    }

    @Override
    public void setUi(com.example.musicplayer.controller.model.Music music) {
        musicName.setText(music.getNameMusic());
        singerImage.setImageBitmap(BitmapFactory.decodeFile(music.getmAlbumPath()));
    }

    @Override
    public void MusicHolderClick(State tabState, com.example.musicplayer.controller.model.Music music) {
        if(tabState ==State.MUSICS){
            playMusic(music);
        }
    }

    @Override
    public void musicClick(com.example.musicplayer.controller.model.Music music) {
        playMusic(music);
    }

    private void playMusic(com.example.musicplayer.controller.model.Music music) {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        singerImage.setImageBitmap(BitmapFactory.decodeFile(music.getmAlbumPath()));
        mMusicRepository.play(music);
        musicName.setText(music.getNameMusic());
        singerName.setText(music.getNameSinger());
        if (mMusicRepository.getMediaPlayer().isPlaying()) {
            mMusicRepository.getMusicUri(music);
            imageButtonPlay.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.image_pause, null));
        } else {
            imageButtonPlay.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.image_music, null));
        }
    }
}
