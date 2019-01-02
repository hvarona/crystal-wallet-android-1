package cy.agorise.crystalwallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.thekhaeng.pushdownanim.PushDownAnim;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.application.CrystalApplication;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.network.CryptoNetManager;

public class LicenseActivity extends AppCompatActivity {

    @BindView(R.id.wvEULA) WebView wvEULA;

    @BindView(R.id.btnDisAgree)
    Button btnDisAgree;

    @BindView(R.id.btnAgree)
    Button btnAgree;

    CrystalDatabase db;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        ButterKnife.bind(this);

        /*
         *   Integration of library with button efects
         * */
        PushDownAnim.setPushDownAnimTo(btnDisAgree)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        onDisagree();
                    }
                } );
        PushDownAnim.setPushDownAnimTo(btnAgree)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        onAgree();
                    }
                } );

        db = CrystalDatabase.getAppDatabase(this.getApplicationContext());
        int licenseVersion = getResources().getInteger(R.integer.license_version);
        GeneralSetting generalSettingLastLicenseRead = db.generalSettingDao().getSettingByName(GeneralSetting.SETTING_LAST_LICENSE_READ);

        if ((generalSettingLastLicenseRead != null) && (Integer.parseInt(generalSettingLastLicenseRead.getValue()) >= licenseVersion)) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            wvEULA.loadUrl("file:///android_asset/crystal_eula.html");
        }
    }

    @OnClick(R.id.btnAgree)
    public void onAgree() {
        CrystalDatabase db = CrystalDatabase.getAppDatabase(this.getApplicationContext());
        GeneralSetting lastLicenseReadSetting = new GeneralSetting();
        lastLicenseReadSetting.setName(GeneralSetting.SETTING_LAST_LICENSE_READ);
        lastLicenseReadSetting.setValue(""+getResources().getInteger(R.integer.license_version));

        db.generalSettingDao().deleteByName(GeneralSetting.SETTING_LAST_LICENSE_READ);
        db.generalSettingDao().insertGeneralSetting(lastLicenseReadSetting);

        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btnDisAgree)
    public void onDisagree() {
        finish();
    }
}
