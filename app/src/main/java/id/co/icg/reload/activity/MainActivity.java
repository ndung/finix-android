package id.co.icg.reload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import id.co.icg.reload.R;
import id.co.icg.reload.fragment.AgentsFragment;
import id.co.icg.reload.fragment.HomeFragment;
import id.co.icg.reload.fragment.OtherFragment;
import id.co.icg.reload.fragment.PriceFragment;
import id.co.icg.reload.fragment.TransactionsFragment;

public class MainActivity extends BaseActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        loadFragment(new HomeFragment());
                        return true;
                    case R.id.navigation_history:
                        loadFragment(new TransactionsFragment());
                        return true;
                    case R.id.navigation_downlines:
                        loadFragment(new AgentsFragment());
                        return true;
                    case R.id.navigation_prices:
                        loadFragment(new PriceFragment());
                        return true;
                    case R.id.navigation_other:
                        loadFragment(new OtherFragment());
                        return true;
                }
                return false;
            };

    private Fragment currentFragment;

    private void loadFragment(Fragment fragment) {
        this.currentFragment = fragment;
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        currentFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            // set your height here
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, displayMetrics);
            // set your width here
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }

        loadFragment(new HomeFragment());
    }


    private static final int TIME_DELAY = 2000;
    private static boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }
        loadFragment(new HomeFragment());
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan tombol back sekali lagi untuk keluar aplikasi", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, TIME_DELAY);

    }
}
