package me.kalehv.popmovie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by harshadkale on 4/10/16.
 */
public abstract class ToolbarAppCompatActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) @Nullable Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    protected abstract int getLayoutResourceId();
}
