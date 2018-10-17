package cy.agorise.crystalwallet.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.application.CrystalSecurityMonitor;
import cy.agorise.crystalwallet.dialogs.material.DialogMaterial;
import cy.agorise.crystalwallet.dialogs.material.NegativeResponse;
import cy.agorise.crystalwallet.dialogs.material.PositiveResponse;
import cy.agorise.crystalwallet.dialogs.material.QuestionDialog;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.util.PasswordManager;
import cy.agorise.crystalwallet.viewmodels.GeneralSettingListViewModel;

public class PinRequestActivity extends AppCompatActivity {
    private String passwordEncrypted;

    @BindView(R.id.btnOK)
    Button btnOK;




    @Override
    public void onBackPressed() {
        //Do nothing to prevent the user to use the back button
    }

    @BindView(R.id.etPassword)
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_request);
        ButterKnife.bind(this);

        /*
        * Initially the button is disabled till the user type a valid PIN
        * */
        btnOK.setEnabled(false);

        GeneralSettingListViewModel generalSettingListViewModel = ViewModelProviders.of(this).get(GeneralSettingListViewModel.class);
        LiveData<List<GeneralSetting>> generalSettingsLiveData = generalSettingListViewModel.getGeneralSettingList();
        generalSettingsLiveData.observe(this, new Observer<List<GeneralSetting>>() {
            @Override
            public void onChanged(@Nullable List<GeneralSetting> generalSettings) {
                passwordEncrypted = "";

                if (generalSettings != null){
                    for (GeneralSetting generalSetting:generalSettings) {
                        if (generalSetting.getName().equals(GeneralSetting.SETTING_PASSWORD)){
                            if (!generalSetting.getValue().isEmpty()){
                                passwordEncrypted = generalSetting.getValue();
                            }
                            break;
                        }
                    }
                }
            }
        });
    }


    @OnClick(R.id.btnOK)
    void okClic(final View view) {

        if (PasswordManager.checkPassword(passwordEncrypted, etPassword.getText().toString())) {
            if (CrystalSecurityMonitor.getInstance(null).is2ndFactorSet()) {
                CrystalSecurityMonitor.getInstance(null).call2ndFactor(this);
            } else {
                this.finish();
            }
        }
        else{
            Toast.makeText(getBaseContext(),this.getResources().getString(R.string.invalid_pin),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @OnTextChanged(value = R.id.etPassword,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterPasswordChanged(Editable editable) {

        /*
        * If it is valid length enable button
        * */
        if(etPassword.getText().length()>=6){
            btnOK.setEnabled(true);
        }
        else{
            btnOK.setEnabled(false);
        }
    }
}


