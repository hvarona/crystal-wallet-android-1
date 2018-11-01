package cy.agorise.crystalwallet.views;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.CryptoCoinBalance;
import cy.agorise.crystalwallet.models.CryptoCurrency;
import cy.agorise.crystalwallet.models.CryptoCurrencyEquivalence;
import cy.agorise.crystalwallet.models.CryptoNetSelection;
import cy.agorise.crystalwallet.models.GeneralSetting;

/**
 * Created by Henry Varona on 10/30/2018.
 *
 */

public class CryptoNetSelectionViewHolder extends RecyclerView.ViewHolder {
    /*
     * the view holding the crypto coin name
     */
    private TextView cryptoNetName;

    private CheckBox cryptoNetCheckBox;

    private Context context;

    public CryptoNetSelectionViewHolder(View itemView) {
        super(itemView);
        //TODO: use ButterKnife to load this
        cryptoNetName = (TextView) itemView.findViewById(R.id.tvCryptoNetName);
        cryptoNetCheckBox = (CheckBox) itemView.findViewById(R.id.cbCryptoNetSelected);
        this.context = itemView.getContext();

    }

    /*
     * Clears the information in this element view
     */
    public void clear(){
        cryptoNetName.setText("");
        cryptoNetCheckBox.setSelected(false);
    }

    /*
     * Binds this view with the data of an element of the list
     */
    public void bindTo(final CryptoNetSelection cryptoNetSelection) {
        if (cryptoNetSelection == null){
            this.clear();
        } else {
            cryptoNetName.setText(cryptoNetSelection.getCryptoNet().getLabel());
            cryptoNetCheckBox.setSelected(cryptoNetSelection.getSelected());
        }
    }
}
