package com.example.demofilemanager;

import static com.example.demofilemanager.MainActivity.PATH_TO_FRAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    TextView tvName, duration_played, duration_total;
    ImageView btnPrev, btnNext, btnBack, cover_art;
    FloatingActionButton btnPlay_Pause;
    SeekBar seekbar;
    Uri uri;
    Bundle songsExtraData;
    public static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    ArrayList<File> listSongsPlay;
    static int position;
    private Thread playThread, prevThread, nextThread;

    public static int TRANS_SONG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        intiViews();
        getIntentMethod();
        solveEvent();
    }

    private void getIntentMethod() {
        Intent songsExtraData = getIntent();
        listSongsPlay = (ArrayList) songsExtraData.getSerializableExtra("songsList");
        position = (int) songsExtraData.getSerializableExtra("position");
        if (listSongsPlay != null) {
            btnPlay_Pause.setImageResource(R.drawable.pause_circle);
            uri = Uri.parse(listSongsPlay.get(position).getAbsolutePath());
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        rotateAnimation();

        metaData(uri);
        seekbar.setMax(mediaPlayer.getDuration() / 1000);
        hidePrev_NextSong(position);
    }

    public int getDuration(Uri uri) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getApplicationContext(), uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int duration = Integer.parseInt(durationStr);
        return duration;
    }

    private void metaData(Uri uri) {
        int duration = getDuration(uri);
        duration_total.setText(formattedTime(duration / 1000));
        tvName.setText(listSongsPlay.get(position).getName());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(listSongsPlay.get(position).getPath());
        byte[] art = mmr.getEmbeddedPicture();
        if(art != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(cover_art);
        }else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.template_img)
                    .into(cover_art);
        }
    }

    void intiViews() {
        tvName = findViewById(R.id.tvName);
        duration_played = findViewById(R.id.durationPlayed);
        duration_total = findViewById(R.id.durationTotal);

        btnPrev = findViewById(R.id.skip_previous);
        btnNext = findViewById(R.id.skip_next);
        btnBack = findViewById(R.id.back_btn);
        btnPlay_Pause = findViewById(R.id.play_pause);
        cover_art = findViewById(R.id.cover_art);

        seekbar = findViewById(R.id.seekbar_player);
    }

    void solveEvent() {
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null && b) {
                    mediaPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekbar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                    if (mCurrentPosition == getDuration(uri) / 1000) {
                        nextSong();
                        FileAdapter.selectedItem++;
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
                backIntent.putExtra("currentPosition", position);
                backIntent.putExtra("songsList", listSongsPlay);
                if(mediaPlayer.isPlaying()) {
                    backIntent.putExtra("SongStopped", false);
                } else {
                    backIntent.putExtra("SongStopped", true);
                }
                startActivity(backIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        transSongFromMinimize();
        super.onResume();
    }

    public void transSongFromMinimize() {
        if (TRANS_SONG != 0) {
            position = TRANS_SONG;
            uri = Uri.parse(PATH_TO_FRAG);
            metaData(uri);
            installSeekBar();
            btnPlay_Pause.setImageResource(R.drawable.pause_circle);
            hidePrev_NextSong(position);
            rotateAnimation();
            TRANS_SONG = 0;
        }

        if (!mediaPlayer.isPlaying()) {
            btnPlay_Pause.setImageResource(R.drawable.play_circle);
            cover_art.clearAnimation();
        } else {
            btnPlay_Pause.setImageResource(R.drawable.pause_circle);
            cover_art.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
        }
    }

    private void prevThreadBtn() {
//        prevThread = new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                btnPrev.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        prevSong();
//                    }
//                });
//            }
//        };
//        prevThread.start();

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevSong();
            }
        });
    }

    private void prevSong() {
        mediaPlayer.stop();
        mediaPlayer.release();
        position = position - 1;
        uri = Uri.parse(listSongsPlay.get(position).getAbsolutePath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        metaData(uri);
        installSeekBar();
        btnPlay_Pause.setImageResource(R.drawable.pause_circle);
        hidePrev_NextSong(position);
        mediaPlayer.start();
        rotateAnimation();
    }

    private void nextThreadBtn() {
//        nextThread = new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                btnNext.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        nextSong();
//                    }
//                });
//            }
//        };
//        nextThread.start();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextSong();
            }
        });
    }

    private void nextSong() {
        mediaPlayer.stop();
        mediaPlayer.release();
        position = position + 1;
        uri = Uri.parse(listSongsPlay.get(position).getAbsolutePath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        metaData(uri);
        installSeekBar();
        btnPlay_Pause.setImageResource(R.drawable.pause_circle);
        hidePrev_NextSong(position);
        mediaPlayer.start();
        rotateAnimation();
    }

    void hidePrev_NextSong(int currentPosition) {
        if (currentPosition == 0) {
            btnPrev.setVisibility(View.INVISIBLE);
        } else if (currentPosition == listSongsPlay.size() - 1) {
            btnNext.setVisibility(View.INVISIBLE);
        } else {
            btnPrev.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    private void playThreadBtn() {
//        playThread = new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                btnPlay_Pause.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Play_Pause();
//                    }
//                });
//            }
//        };
//        playThread.start();

        btnPlay_Pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Play_Pause();
            }
        });
    }

    void installSeekBar() {
        seekbar.setMax(getDuration(uri) / 1000);
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekbar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    void Play_Pause() {
        if (mediaPlayer.isPlaying()) {
            btnPlay_Pause.setImageResource(R.drawable.play_circle);
            mediaPlayer.pause();
            //installSeekBar();
            cover_art.clearAnimation();
        } else {
            btnPlay_Pause.setImageResource(R.drawable.pause_circle);
            mediaPlayer.start();
            cover_art.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
            //installSeekBar();
        }

    }

    public void rotateAnimation() {
        Animation ani = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        cover_art.setAnimation(ani);
    }

    private String formattedTime(int mCurrentPosition) {
        String totalOut = "", totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }
    }
}