package cy.agorise.crystalwallet.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sjaramillo10.animatedtablayout.AnimatedTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.fragments.BackupsSettingsFragment;
import cy.agorise.crystalwallet.fragments.GeneralSettingsFragment;
import cy.agorise.crystalwallet.fragments.SecuritySettingsFragment;

/**
 * Created by seven on 12/28/17.
 *
 */

public class SettingsActivity extends AppCompatActivity{

    @BindView(R.id.ivGoBack)
    public ImageView ivGoBack;

    @BindView(R.id.tabLayout)
    public AnimatedTabLayout tabLayout;

    @BindView(R.id.pager)
    public ViewPager mPager;

    public SettingsPagerAdapter settingsPagerAdapter;

    @BindView(R.id.tvBuildVersion)
    public TextView tvBuildVersion;

    private SecuritySettingsFragment securitySettingsFragment;

    @BindView(R.id.ivAppBarAnimation)
    ImageView ivAppBarAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Sets AppBar animation
        Glide.with(this)
                .load(R.drawable.appbar_background)
                .apply(new RequestOptions().centerCrop())
                .into(ivAppBarAnimation);

        settingsPagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(settingsPagerAdapter);
        tabLayout.setupWithViewPager(mPager);
    }

    private class SettingsPagerAdapter extends FragmentStatePagerAdapter {
        SettingsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        int[] tabTitles = {R.string.general, R.string.security, R.string.backups,
                R.string.accounts};

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new GeneralSettingsFragment();
                case 1:
                    securitySettingsFragment = new SecuritySettingsFragment();
                    return securitySettingsFragment;
                case 2:
                    return new BackupsSettingsFragment();
                //case 3:
                //    return new AccountsSettingsFragment();
            }


            return null; //new OnConstructionFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(tabTitles[position]);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @OnClick(R.id.ivGoBack)
    public void goBack(){
        onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (this.securitySettingsFragment != null){
            this.securitySettingsFragment.onNewIntent(intent);
        }
    }
}
