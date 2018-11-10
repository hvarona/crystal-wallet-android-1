package cy.agorise.crystalwallet.viewmodels.validators.validationfields;

import android.widget.Spinner;

import cy.agorise.crystalwallet.models.CryptoCurrency;

/**
 * Created by Henry Varona on 7/10/2017.
 */

public class AssetValidationField extends ValidationField {

    private Spinner assetField;

    public AssetValidationField(Spinner assetField){
        super(assetField);
        this.assetField = assetField;
    }

    public void validate(){
        if (this.assetField.getSelectedItem() instanceof  CryptoCurrency) {
            final CryptoCurrency cryptoCurrencySelected = (CryptoCurrency) this.assetField.getSelectedItem();
            if (cryptoCurrencySelected != null) {
                final String newValue = "" + cryptoCurrencySelected.getId();
                this.setLastValue(newValue);
                setValidForValue(newValue, true);
            } else {
                final String newValue = "" + -1;
                setMessageForValue(newValue, "Select a currency");
                this.setLastValue(newValue);
                setValidForValue(newValue, false);
            }
        } else {
            final String newValue = "" + -1;
            this.setLastValue(newValue);
            setValidForValue(newValue, true);
        }
    }
}
