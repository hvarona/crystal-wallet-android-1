package cy.agorise.crystalwallet;

import android.arch.lifecycle.LifecycleActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class IntroActivity extends LifecycleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }
}
