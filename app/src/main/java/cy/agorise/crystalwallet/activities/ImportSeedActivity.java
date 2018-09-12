package cy.agorise.crystalwallet.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.enums.SeedType;
import cy.agorise.crystalwallet.manager.BitsharesAccountManager;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.models.GrapheneAccount;
import cy.agorise.crystalwallet.models.GrapheneAccountInfo;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestListener;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequests;
import cy.agorise.crystalwallet.requestmanagers.ImportBackupRequest;
import cy.agorise.crystalwallet.requestmanagers.ValidateImportBitsharesAccountRequest;
import cy.agorise.crystalwallet.viewmodels.AccountSeedViewModel;
import cy.agorise.crystalwallet.viewmodels.CryptoNetAccountViewModel;
import cy.agorise.crystalwallet.viewmodels.GrapheneAccountInfoViewModel;
import cy.agorise.crystalwallet.viewmodels.validators.ImportSeedValidator;
import cy.agorise.crystalwallet.viewmodels.validators.UIValidatorListener;
import cy.agorise.crystalwallet.viewmodels.validators.validationfields.ValidationField;

public class ImportSeedActivity extends AppCompatActivity implements UIValidatorListener {

    AccountSeedViewModel accountSeedViewModel;
    ImportSeedValidator importSeedValidator;

    @BindView(R.id.etPin)
    EditText etPin;
    @BindView(R.id.tvPinError)
    TextView tvPinError;

    @BindView(R.id.etPinConfirmation)
    EditText etPinConfirmation;
    @BindView(R.id.tvPinConfirmationError)
    TextView tvPinConfirmationError;

    @BindView(R.id.etSeedWords)
    EditText etSeedWords;
    @BindView(R.id.tvSeedWordsError)
    TextView tvSeedWordsError;

    @BindView (R.id.etAccountName)
    EditText etAccountName;
    @BindView(R.id.tvAccountNameError)
    TextView tvAccountNameError;

    @BindView(R.id.btnImport)
    Button btnImport;

    @BindView(R.id.btnCancel)
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_seed);

        ButterKnife.bind(this);

        btnImport.setEnabled(false);
        accountSeedViewModel = ViewModelProviders.of(this).get(AccountSeedViewModel.class);
        importSeedValidator = new ImportSeedValidator(this.getApplicationContext(),etPin,etPinConfirmation,etAccountName,etSeedWords);
        importSeedValidator.setListener(this);
    }

    @OnTextChanged(value = R.id.etPin,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterPinChanged(Editable editable) {
        this.importSeedValidator.validate();
    }

    @OnTextChanged(value = R.id.etPinConfirmation,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterPinConfirmationChanged(Editable editable) {
        this.importSeedValidator.validate();
    }

    @OnTextChanged(value = R.id.etSeedWords,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterSeedWordsChanged(Editable editable) {
        this.importSeedValidator.validate();
    }


    @OnTextChanged(value = R.id.etAccountName,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterAccountNameChanged(Editable editable) {
        this.importSeedValidator.validate();
    }

    @OnClick(R.id.btnCancel)
    public void cancel(){
        this.finish();
    }

    @OnClick(R.id.btnImport)
    public void importSeed(){
        final ImportSeedActivity thisActivity = this;

        if (this.importSeedValidator.isValid()) {

            final ValidateImportBitsharesAccountRequest validatorRequest =
                    new ValidateImportBitsharesAccountRequest(etAccountName.getText().toString(), etSeedWords.getText().toString(), getApplicationContext(), true);

            validatorRequest.setListener(new CryptoNetInfoRequestListener() {
                @Override
                public void onCarryOut() {
                    if (!validatorRequest.getStatus().equals(ValidateImportBitsharesAccountRequest.StatusCode.SUCCEEDED)) {
                        String errorText = "An error ocurred attempting to import the account";

                        switch (validatorRequest.getStatus()){
                            case PETITION_FAILED:
                            case NO_INTERNET:
                            case NO_SERVER_CONNECTION:
                                errorText = "There was an error with the connection. Try again later";
                                break;
                            case ACCOUNT_DOESNT_EXIST:
                                errorText = "The account doesn't exists";
                                break;
                            case BAD_SEED:
                                errorText = "The seed is not valid";
                                break;
                            case NO_ACCOUNT_DATA:
                                errorText = "The account doesn't have any data";
                                break;
                        }

                        Toast.makeText(thisActivity.getApplicationContext(),errorText,Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(thisActivity, IntroActivity.class);
                        startActivity(intent);
                    }
                }
            });
            /*CryptoNetInfoRequests.getInstance().addRequest(validatorRequest);

            AccountSeed seed = new AccountSeed();

            //TODO verify if words are already in the db
            //TODO check if name has been asigned to other seed
            seed.setMasterSeed(etSeedWords.getText().toString());
            seed.setName(etAccountName.getText().toString());
            seed.setType(SeedType.BRAINKEY);

            accountSeedViewModel.addSeed(seed);

            CryptoNetAccountViewModel cryptoNetAccountViewModel = ViewModelProviders.of(this).get(CryptoNetAccountViewModel.class);
            GrapheneAccountInfoViewModel grapheneAccountInfoViewModel = ViewModelProviders.of(this).get(GrapheneAccountInfoViewModel.class);
            CryptoNetAccount cryptoNetAccount = new CryptoNetAccount();
            cryptoNetAccount.setSeedId(seed.getId());
            cryptoNetAccount.setAccountIndex(0);
            cryptoNetAccount.setCryptoNet(cy.agorise.crystalwallet.enums.CryptoNet.BITSHARES);
            cryptoNetAccountViewModel.addCryptoNetAccount(cryptoNetAccount);
            GrapheneAccountInfo grapheneAccountInfo = new GrapheneAccountInfo(cryptoNetAccount.getId());
            grapheneAccountInfo.setName(etAccountName.getText().toString());
            grapheneAccountInfoViewModel.addGrapheneAccountInfo(grapheneAccountInfo);

            this.finish();*/
        }
    }

    @Override
    public void onValidationSucceeded(final ValidationField field) {
        final ImportSeedActivity activity = this;

        activity.runOnUiThread(new Runnable() {
            public void run() {

                if (field.getView() == etPin) {
                    tvPinError.setText("");
                } else if (field.getView() == etPinConfirmation){
                    tvPinConfirmationError.setText("");
                } else if (field.getView() == etAccountName){
                    tvAccountNameError.setText("");
                } else if (field.getView() == etSeedWords){
                    tvSeedWordsError.setText("");
                }

                if (activity.importSeedValidator.isValid()){
                    btnImport.setEnabled(true);
                } else {
                    btnImport.setEnabled(false);
                }

            }
        });
    }

    @Override
    public void onValidationFailed(ValidationField field) {
        if (field.getView() == etPin) {
            tvPinError.setText(field.getMessage());
        } else if (field.getView() == etPinConfirmation){
            tvPinConfirmationError.setText(field.getMessage());
        } else if (field.getView() == etAccountName){
            tvAccountNameError.setText(field.getMessage());
        } else if (field.getView() == etSeedWords){
            tvSeedWordsError.setText(field.getMessage());
        }
    }
}
