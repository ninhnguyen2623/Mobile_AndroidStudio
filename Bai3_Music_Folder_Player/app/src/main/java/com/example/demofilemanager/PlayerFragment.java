package com.example.demofilemanager;

import static com.example.demofilemanager.MainActivity.PATH_TO_FRAG;
import static com.example.demofilemanager.MainActivity.IS_STOPPED;
import static com.example.demofilemanager.PlayerActivity.TRANS_SONG;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlayerFragment extends Fragment {

    ImageView img_bottom, btnNext_bottom, btnPrev_bottom;
    TextView tvName_bottom;
    FloatingActionButton btnPlay_Pause_bottom;
    View view;
    static MediaPlayer mediaPlayer = new MediaPlayer();
    int positionFrag;
    ArrayList<File> musicFiles;
    Uri uri;

    public interface setDataChanged {
        void changeDataAdapter();
    }

    private setDataChanged dataChanged;

    public void setDataChangedListener(setDataChanged dataChanged) {
        this.dataChanged = dataChanged;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_player, container, false);
        img_bottom = (ImageView) view.findViewById(R.id.img_bottom);
        btnNext_bottom = view.findViewById(R.id.skip_next_bottom);
        btnPrev_bottom = view.findViewById(R.id.skip_previous_bottom);
        btnPlay_Pause_bottom = view.findViewById(R.id.play_pause_bottom);
        tvName_bottom = (TextView) view.findViewById(R.id.tvName_bottom);

        mediaPlayer = PlayerActivity.mediaPlayer;
        View layoutFrag = view.findViewById(R.id.card_bottom);
        layoutFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        btnPlay_Pause_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_pause();
            }
        });

        btnPrev_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevSong();
            }
        });

        btnNext_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextSong();
            }
        });

        return view;
    }

    private void nextSong() {
        if(dataChanged != null) {
            ++FileAdapter.selectedItem;
            dataChanged.changeDataAdapter();
        }
        positionFrag = positionFrag + 1;
        transSong(positionFrag);

    }

    private void prevSong() {
        if(dataChanged != null) {
            --FileAdapter.selectedItem;
            dataChanged.changeDataAdapter();
        }
        positionFrag = positionFrag - 1;
        transSong(positionFrag);
    }

    void transSong(int position){
        mediaPlayer.stop();
        PlayerActivity.mediaPlayer.stop();
        if (position == 0) {
            btnPrev_bottom.setVisibility(view.INVISIBLE);
        } else if (position == musicFiles.size() - 1) {
            btnNext_bottom.setVisibility(view.INVISIBLE);
        } else {
            btnPrev_bottom.setVisibility(view.VISIBLE);
            btnNext_bottom.setVisibility(view.VISIBLE);
        }
        PATH_TO_FRAG = musicFiles.get(position).getAbsolutePath();
        String tempPath = PATH_TO_FRAG;
        setAlbumArt(tempPath);
        setNameSong(tempPath);
        TRANS_SONG = position;
        uri = Uri.parse(tempPath);
        mediaPlayer = MediaPlayer.create(getContext(), uri);
        mediaPlayer.start();
        PlayerActivity.mediaPlayer = mediaPlayer;
        btnPlay_Pause_bottom.setImageResource(R.drawable.pause_circle);
    }

    private void play_pause() {
        if (mediaPlayer.isPlaying()) {
            btnPlay_Pause_bottom.setImageResource(R.drawable.play_circle);
            mediaPlayer.pause();
        } else {
            btnPlay_Pause_bottom.setImageResource(R.drawable.pause_circle);
            mediaPlayer.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setNameSong(PATH_TO_FRAG);
        setAlbumArt(PATH_TO_FRAG);
        setIconPlay_Pause(IS_STOPPED);
    }


    private void setIconPlay_Pause(boolean isStopped) {
        if (isStopped) {
            btnPlay_Pause_bottom.setImageResource(R.drawable.play_circle);
        } else {
            btnPlay_Pause_bottom.setImageResource(R.drawable.pause_circle);
        }

        File filePath = new File(PATH_TO_FRAG);
        String folderPath = filePath.getParent();
        String fileName = filePath.getName();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        musicFiles = getMusicFiles(files);
        for (int i = 0; i < musicFiles.size(); i++) {
            if(musicFiles.get(i).getName().equals(fileName)){
                positionFrag = i;
                break;
            }
        }


        if (positionFrag == 0) {
            btnPrev_bottom.setVisibility(view.INVISIBLE);
        } else if (positionFrag == musicFiles.size() - 1) {
            btnNext_bottom.setVisibility(view.INVISIBLE);
        } else {
            btnPrev_bottom.setVisibility(view.VISIBLE);
            btnNext_bottom.setVisibility(view.VISIBLE);
        }

    }

    public void setAlbumArt(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        byte[] art = mmr.getEmbeddedPicture();
        if (art != null) {
            Glide.with(getContext())
                    .asBitmap()
                    .load(art)
                    .into(img_bottom);
        } else {
            Glide.with(getContext())
                    .asBitmap()
                    .load(R.drawable.template_img)
                    .into(img_bottom);
        }
    }

    public void setNameSong(String path) {
        File temp = new File(path);
        int lastDot = temp.getName().lastIndexOf(".");
        tvName_bottom.setText(temp.getName().substring(0, lastDot));
    }

    private ArrayList<File> getMusicFiles(File[] files){
        ArrayList<File> musicFiles = new ArrayList<>();
        for(File currentFile : files) {
            if(currentFile.getName().toLowerCase().endsWith(".mp3")) {
                musicFiles.add(currentFile);
            }
        }
        return musicFiles;
    }
}