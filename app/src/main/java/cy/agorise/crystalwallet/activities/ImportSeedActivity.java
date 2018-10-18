package cy.agorise.crystalwallet.activities;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thekhaeng.pushdownanim.PushDownAnim;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.dialogs.material.CrystalLoading;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestListener;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequests;
import cy.agorise.crystalwallet.requestmanagers.ValidateImportBitsharesAccountRequest;
import cy.agorise.crystalwallet.viewmodels.AccountSeedViewModel;
import cy.agorise.crystalwallet.viewmodels.validators.ImportSeedValidator;
import cy.agorise.crystalwallet.viewmodels.validators.UIValidatorListener;
import cy.agorise.crystalwallet.viewmodels.validators.validationfields.ValidationField;

public class ImportSeedActivity extends AppCompatActivity implements UIValidatorListener {

    AccountSeedViewModel accountSeedViewModel;
    ImportSeedValidator importSeedValidator;

    @BindView(R.id.etPin)
    EditText etPin;

    @BindView(R.id.txtErrorPIN)
    TextView txtErrorPIN;

    @BindView(R.id.txtErrorAccount)
    TextView txtErrorAccount;

    //@BindView(R.id.tvPinError)
    //TextView tvPinError;

    @BindView(R.id.etPinConfirmation)
    EditText etPinConfirmation;
    //@BindView(R.id.tvPinConfirmationError)
    //TextView tvPinConfirmationError;

    @BindView(R.id.etSeedWords)
    EditText etSeedWords;
    //@BindView(R.id.tvSeedWordsError)
    //TextView tvSeedWordsError;

    @BindView (R.id.etAccountName)
    EditText etAccountName;
    //@BindView(R.id.tvAccountNameError)
    //TextView tvAccountNameError;

    @BindView(R.id.btnImport)
    Button btnImport;

    @BindView(R.id.btnCancel)
    Button btnCancel;

    final Activity activity = this;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_seed);

        ButterKnife.bind(this);

        /*
         *   Integration of library with button efects
         * */
        PushDownAnim.setPushDownAnimTo(btnCancel)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        cancel();
                    }
                } );
        PushDownAnim.setPushDownAnimTo(btnImport)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        importSeed();
                    }
                } );

        /*
        * Initially the button CREATE WALLET should be disabled
        * */
        disableCreate();

        /*
        * When a text change in any of the fields
        * */
        etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                /*
                 * If all is ready to continue enable the button, contrarie case disable it
                 * */
                if(allFieldsAreFill()){
                    enableCreate();
                }
                else{
                    disableCreate();
                }

                /*
                * Validate that PINs are equals
                * */
                validatePINS();
            }
        });
        etPinConfirmation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                /*
                 * If all is ready to continue enable the button, contrarie case disable it
                 * */
                if(allFieldsAreFill()){
                    enableCreate();
                }
                else{
                    disableCreate();
                }

                /*
                 * Validate that PINs are equals
                 * */
                validatePINS();
            }
        });
        etSeedWords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                /*
                 * If all is ready to continue enable the button, contrarie case disable it
                 * */
                if(allFieldsAreFill()){
                    enableCreate();
                }
                else{
                    disableCreate();
                }

                /*
                * Hide error field
                * */
                txtErrorAccount.setVisibility(View.INVISIBLE);
            }
        });
        etAccountName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                /*
                 * If all is ready to continue enable the button, contrarie case disable it
                 * */
                if(allFieldsAreFill()){
                    enableCreate();
                }
                else{
                    disableCreate();
                }
            }
        });

        accountSeedViewModel = ViewModelProviders.of(this).get(AccountSeedViewModel.class);
        importSeedValidator = new ImportSeedValidator(this.getApplicationContext(),etPin,etPinConfirmation,etAccountName,etSeedWords);
        importSeedValidator.setListener(this);
    }

    /*
    * Validate that PINs are equals
    * */
    private void validatePINS(){

        final String pin = etPin.getText().toString().trim();
        final String confirmoPIN = etPinConfirmation.getText().toString().trim();
        if(!pin.isEmpty() && !confirmoPIN.isEmpty()){
            if(pin.compareTo(confirmoPIN)!=0){
                txtErrorPIN.setVisibility(View.VISIBLE);
            }
            else{
                txtErrorPIN.setVisibility(View.INVISIBLE);
            }
        }
        else{
            txtErrorPIN.setVisibility(View.INVISIBLE);
        }
    }


    /*
    *   Method to validate if all the fields are fill
    * */
    private boolean allFieldsAreFill(){

        boolean complete = false;
        if( etPin.getText().toString().trim().compareTo("")!=0 &&
            etPinConfirmation.getText().toString().trim().compareTo("")!=0 &&
                etSeedWords.getText().toString().trim().compareTo("")!=0 &&
                etAccountName.getText().toString().trim().compareTo("")!=0){
            complete = true;
        }
        return complete;
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


            /*
            * Loading dialog
            * */
            final CrystalLoading crystalLoading = new CrystalLoading(activity);
            crystalLoading.show();

            /*
            * Validate mnemonic with the server
            * */
            final ValidateImportBitsharesAccountRequest request = new ValidateImportBitsharesAccountRequest(etAccountName.getText().toString().trim(),etSeedWords.getText().toString().trim(),this);
            request.setListener(new CryptoNetInfoRequestListener() {
                @Override
                public void onCarryOut() {
                    if(request.getStatus().equals(ValidateImportBitsharesAccountRequest.StatusCode.SUCCEEDED)){

                        //Correct

                        /*
                        * Final service connection
                        * */
                        finalStep(crystalLoading);

                    }
                    else{

                        crystalLoading.dismiss();

                        txtErrorAccount.setVisibility(View.VISIBLE);
                        txtErrorAccount.setText(activity.getResources().getString(R.string.error_invalid_account));
                    }
                }
            });
            CryptoNetInfoRequests.getInstance().addRequest(request);


        }
    }




    private void finalStep(final CrystalLoading crystalLoading){

        final ImportSeedActivity thisActivity = this;

        final ValidateImportBitsharesAccountRequest validatorRequest =
                new ValidateImportBitsharesAccountRequest(etAccountName.getText().toString(), etSeedWords.getText().toString(), getApplicationContext(), true);

        validatorRequest.setListener(new CryptoNetInfoRequestListener() {
            @Override
            public void onCarryOut() {

                /*
                 * Hide the loading dialog
                 * */
                crystalLoading.dismiss();

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
                    Intent intent = new Intent(thisActivity, BoardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        CryptoNetInfoRequests.getInstance().addRequest(validatorRequest);
    }

    @Override
    public void onValidationSucceeded(final ValidationField field) {
        final ImportSeedActivity activity = this;

        activity.runOnUiThread(new Runnable() {
            public void run() {

                if (field.getView() == etPin) {
                    //tvPinError.setText("");
                } else if (field.getView() == etPinConfirmation){
                    //tvPinConfirmationError.setText("");
                } else if (field.getView() == etAccountName){
                    //tvAccountNameError.setText("");
                } else if (field.getView() == etSeedWords){
                    //tvSeedWordsError.setText("");
                }

                if (activity.importSeedValidator.isValid()){
                    enableCreate();
                } else {
                    disableCreate();
                }

            }
        });
    }

    @Override
    public void onValidationFailed(ValidationField field) {
        if (field.getView() == etPin) {
            //tvPinError.setText(field.getMessage());
        } else if (field.getView() == etPinConfirmation){
            //tvPinConfirmationError.setText(field.getMessage());
        } else if (field.getView() == etAccountName){
            //tvAccountNameError.setText(field.getMessage());
        } else if (field.getView() == etSeedWords){
            //tvSeedWordsError.setText(field.getMessage());
        }
    }


    /*
     * Enable create button
     * */
    private void enableCreate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //btnImport.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnImport.setEnabled(true);
            }
        });
    }

    /*
     * Disable create button
     * */
    private void disableCreate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnImport.setEnabled(false);
                //btnImport.setBackground(getDrawable(R.drawable.disable_style));
            }
        });
    }
}
