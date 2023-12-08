package com.example.modernartui.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.modernartui.MainActivity;
import com.example.modernartui.R;
import com.example.modernartui.Utils.ColorUtils;

public class TableLayoutFragment extends Fragment {
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.modern_table_layout, container, false);

        SeekBar colorSeekBar = ((MainActivity) requireActivity()).getColorSeekBar();

        View rectangle1 = (View) view.findViewById(R.id.Rectangle1);
        View rectangle2 = (View) view.findViewById(R.id.Rectangle2);
        View rectangle3 = (View) view.findViewById(R.id.Rectangle3);
        View rectangle4 = (View) view.findViewById(R.id.Rectangle4);
        View rectangle5 = (View) view.findViewById(R.id.Rectangle5);

        colorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ColorUtils.updateColorRectangles(progress, rectangle1, rectangle2, rectangle3, rectangle4, rectangle5);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Không cần xử lý ở đây
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Không cần xử lý ở đây
            }
        });

        return view;
    }
}