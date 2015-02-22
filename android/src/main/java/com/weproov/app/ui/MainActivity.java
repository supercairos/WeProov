package com.weproov.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.weproov.app.R;
import com.weproov.app.logic.services.GcmRegisterService;
import com.weproov.app.models.NavItem;
import com.weproov.app.ui.fragments.AboutFragment;
import com.weproov.app.ui.fragments.CameraFragment;
import com.weproov.app.ui.fragments.NavigationFragment;
import com.weproov.app.ui.fragments.SignatureFragment;
import com.weproov.app.ui.ifaces.ActionBarIface;
import com.weproov.app.utils.CameraUtils;
import com.weproov.app.utils.PlayServicesUtils;
import com.weproov.app.utils.PrefUtils;
import com.weproov.app.utils.constants.Constants;


public class MainActivity extends BaseActivity implements NavigationFragment.OnNavigationInteractionListener, ActionBarIface {

    @InjectView(R.id.action_bar)
    Toolbar mActionBar;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.left_frame)
    FrameLayout mDrawerNavigation;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CameraUtils.checkCameraHardware(this)) {
            // Toast.makeText(this, R.string.camera_is_required, Toast.LENGTH_SHORT).show();
            // finish();
        }

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(mActionBar);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            onNavItemSelected(NavItem.getNavItems().get(0));
        }

        if (PlayServicesUtils.checkPlayServices(this)) {
            if (TextUtils.isEmpty(PlayServicesUtils.getRegistrationId(this))) {
                startService(new Intent(this, GcmRegisterService.class));
            }
        } else {
            Log.i("Test", "No valid Google Play Services APK found.");
        }

    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private static void logout(Activity ctx) {
        Log.d("Test", "Loging out");
        PrefUtils.remove(Constants.KEY_DISPLAY_NAME);
        PrefUtils.remove(Constants.KEY_EMAIL);
        Intent intent = new Intent(ctx, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
        ctx.finish();
    }

    @Override
    public void onNavItemSelected(NavItem item) {

        if (item.id == NavItem.NAV_LOGOUT) {
            // Handle Logout;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to logout ?")
                    .setTitle("Logout")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            logout(MainActivity.this);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

            builder.create().show();
            return;
        }


        Fragment fragment = null;
        switch (item.id) {
            case NavItem.NAV_WEPROOV:
                fragment = new SignatureFragment();
                break;
            case NavItem.NAV_MY_DOCUMENTS:
                fragment = new CameraFragment();
                break;
            case NavItem.NAV_ABOUT:
                fragment = new AboutFragment();
                break;
            default:
                throw new IllegalArgumentException();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, fragment).commit();
        // update selected item and title, then close the drawer
        setTitle(item.getLabel());
        mDrawerLayout.closeDrawer(mDrawerNavigation);
    }

    public boolean isActionBarShowing() {
        return mActionBar.getVisibility() == View.VISIBLE;
    }

    public void showActionBar() {
        if (mActionBar != null) {
            mActionBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideActionBar() {
        if (mActionBar != null) {
            mActionBar.setVisibility(View.GONE);
        }
    }

    public boolean isNavigationDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(mDrawerNavigation);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mDrawerNavigation);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mDrawerNavigation);
    }



}
