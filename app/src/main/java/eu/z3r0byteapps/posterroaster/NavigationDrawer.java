package eu.z3r0byteapps.posterroaster;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class NavigationDrawer {
    Drawer drawer;

    AppCompatActivity activity;
    Toolbar toolbar;
    String selection;

    public NavigationDrawer(AppCompatActivity activity, Toolbar toolbar, String selection) {
        this.activity = activity;
        this.toolbar = toolbar;
        this.selection = selection;
    }


    static PrimaryDrawerItem homeItem = new PrimaryDrawerItem().withName("Home")
            .withIcon(GoogleMaterial.Icon.gmd_home);
    static PrimaryDrawerItem voteItem = new PrimaryDrawerItem().withName("Stemmen")
            .withIcon(GoogleMaterial.Icon.gmd_favorite);
    static PrimaryDrawerItem imageItem = new PrimaryDrawerItem().withName("Poster")
            .withIcon(GoogleMaterial.Icon.gmd_portrait);


    public void SetupNavigationDrawer() {

        final AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(activity)
                //.withHeaderBackground(R.drawable.header_bg)
                .addProfiles(
                        new ProfileDrawerItem().withName("PosterRoaster")
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        drawer.closeDrawer();
                        return false;
                    }
                })
                .withSelectionListEnabledForSingleProfile(false)
                .build();


        drawer = new DrawerBuilder()
                .withAccountHeader(accountHeader)
                .withActivity(activity)
                .withToolbar(toolbar)
                .addDrawerItems(
                        homeItem,
                        voteItem,
                        imageItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == homeItem && selection != "home") {
                            closeActivity();
                            drawer.closeDrawer();
                        } else if (drawerItem == voteItem && selection != "vote") {
                            activity.startActivity(new Intent(activity, VoteActivity.class));
                            closeActivity();
                            drawer.closeDrawer();
                        } else if (drawerItem == imageItem && selection != "image") {
                            activity.startActivity(new Intent(activity, ImageActivity.class));
                            closeActivity();
                            drawer.closeDrawer();
                        }
                        return true;
                    }
                })
                .build();

        setSelection(selection);
    }

    private void setSelection(String selection) {
        switch (selection) {
            case "home":
                drawer.setSelection(homeItem);
                break;
            case "vote":
                drawer.setSelection(voteItem);
                break;
            case "image":
                drawer.setSelection(imageItem);
                break;
            case "":
                drawer.setSelection(-1);
                break;
        }
    }

    private void closeActivity() {
        if (selection != "home") {
            activity.finish();
        } else {
            drawer.setSelection(homeItem);
        }
    }
}
