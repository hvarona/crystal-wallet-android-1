package cy.agorise.crystalwallet.viewmodels.validators.validationfields;

import android.widget.EditText;
import android.widget.Spinner;

import com.jaredrummler.materialspinner.MaterialSpinner;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestListener;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequests;
import cy.agorise.crystalwallet.requestmanagers.ValidateExistBitsharesAccountRequest;

/**
 * Created by Henry Varona on 7/10/2017.
 */

public class ToValidationField extends ValidationField {

    private Spinner fromField;
    private EditText toField;

    public ToValidationField(Spinner fromField, EditText toField){
        super(toField);
        this.fromField = fromField;
        this.toField = toField;
    }

    public void validate(){
        final String fromNewValue;
        CryptoNetAccount cryptoNetAccount = null;
        if (fromField.getSelectedItem() instanceof CryptoNetAccount){
            cryptoNetAccount = (CryptoNetAccount) fromField.getSelectedItem();
            fromNewValue = cryptoNetAccount.getName();
        } else {
            fromNewValue = "";
        }
        final String toNewValue = toField.getText().toString();
        final String mixedValue = fromNewValue+"_"+toNewValue;
        this.setLastValue(mixedValue);
        this.startValidating();
        final ValidationField field = this;

        if (fromNewValue.equals(toNewValue)){
            setMessageForValue(mixedValue,validator.getContext().getResources().getString(R.string.warning_msg_same_account));
            setValidForValue(mixedValue, false);
        } else {

            if (cryptoNetAccount != null) {
                if (cryptoNetAccount.getCryptoNet() == CryptoNet.BITSHARES) {
                    final ValidateExistBitsharesAccountRequest request = new ValidateExistBitsharesAccountRequest(toNewValue);
                    request.setListener(new CryptoNetInfoRequestListener() {
                        @Override
                        public void onCarryOut() {
                            if (!request.getAccountExists()) {
                                setMessageForValue(mixedValue, validator.getContext().getResources().getString(R.string.account_name_not_exist, "'" + toNewValue + "'"));
                                setValidForValue(mixedValue, false);
                            } else {
                                setValidForValue(mixedValue, true);
                            }
                        }
                    });
                    CryptoNetInfoRequests.getInstance().addRequest(request);
                } else {
                    //if (addressIsValid(toNewValue)) {
                        setValidForValue(mixedValue, true);
                    //} else {
                    //    setMessageForValue(mixedValue, "Is not a valid address");
                    //    setValidForValue(mixedValue, false);
                    //}
                }
            }
        }
    }
}
