package hk.edu.cuhk.ie.iems5722.a2_1155149902.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import hk.edu.cuhk.ie.iems5722.a2_1155149902.R;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navView;
    private String avatar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_chat, R.id.navigation_friends, R.id.navigation_me)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                refreshItemIcon();
//                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
                NavigationUI.onNavDestinationSelected(item, navController);
                switch (item.getItemId()) {
                    case R.id.navigation_chat:
                        item.setIcon(R.drawable.chat_fill);
                        return true;
                    case R.id.navigation_friends:
                        item.setIcon(R.drawable.contacts_fill);
                        return true;
                    case R.id.navigation_me:
                        item.setIcon(R.drawable.account_fill);
                        return true;
                }
                return false;
            }
        });

    }

    public void refreshItemIcon() {
        MenuItem item1 = navView.getMenu().findItem(R.id.navigation_chat);
        item1.setIcon(R.drawable.chat);
        MenuItem item2 = navView.getMenu().findItem(R.id.navigation_friends);
        item2.setIcon(R.drawable.contacts);
        MenuItem item3 = navView.getMenu().findItem(R.id.navigation_me);
        item3.setIcon(R.drawable.account);
    }

    public Bundle toValue() {
        //获取bundle传递的值
        Intent intent = getIntent();
        return intent.getExtras();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}