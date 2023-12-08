package com.example.modernartui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.modernartui.Fragment.LinearLayoutFragment;
import com.example.modernartui.Fragment.RelativeLayoutFragment;
import com.example.modernartui.Fragment.TableLayoutFragment;

public class MainActivity extends AppCompatActivity {
    private SeekBar colorSeekBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, LinearLayoutFragment.class, null)
                .commit();

        findViewById(R.id.menu_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        colorSeekBar = findViewById(R.id.colorSeekBar);
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.relative_layout) {
                    loadFragment(new RelativeLayoutFragment());
                    Toast.makeText(MainActivity.this, "RelativeLayout selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.linear_layout) {
                    loadFragment(new LinearLayoutFragment());
                    Toast.makeText(MainActivity.this, "LinearLayout selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.table_layout) {
                    loadFragment(new TableLayoutFragment());
                    Toast.makeText(MainActivity.this, "TableLayout selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.dialog) {
                    CustomDialog.showDialog(MainActivity.this);
                    Toast.makeText(MainActivity.this, "Dialog selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });

        popupMenu.show();
    }
    public SeekBar getColorSeekBar() {
        return colorSeekBar;
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_view, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}