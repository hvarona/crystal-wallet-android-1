package cy.agorise.crystalwallet.tests.activities.dialogs

import android.os.Bundle
import android.view.View
import cy.agorise.crystalwallet.R
import cy.agorise.crystalwallet.activities.CustomActivity
import cy.agorise.crystalwallet.dialogs.material.ToastIt
import cy.agorise.crystalwallet.models.CryptoNetAccount
import cy.agorise.crystalwallet.views.natives.spinners.CryptoNetAccountsSpinner
import java.util.ArrayList

/*
*   Test class
* */
class Test_CryptoNetAccountSpinner : CustomActivity(){

    private var cryptoNetAccountsSpinner: CryptoNetAccountsSpinner? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.empty_activity)

        /*
        * Get the field
        * */
        cryptoNetAccountsSpinner = findViewById(R.id.cryptoNetAccountsSpinner)

        /*Make it visible*/
        cryptoNetAccountsSpinner?.visibility= View.VISIBLE

        /*
        * Add models for testing
        * */

        var cryptoNetAccounts: MutableList<CryptoNetAccount> = ArrayList()
        var cryptoNetAccount:CryptoNetAccount = CryptoNetAccount();
        cryptoNetAccount.setAccountIndex(1);
        cryptoNetAccount.setName("David Tadeo Vargas");
        cryptoNetAccount.setId(0);
        cryptoNetAccount.setSeedId(10203024L);
        cryptoNetAccounts.add(cryptoNetAccount);
        cryptoNetAccount = CryptoNetAccount();
        cryptoNetAccount.setAccountIndex(2);
        cryptoNetAccount.setName("Alfredo mercado mendoza");
        cryptoNetAccount.setId(1);
        cryptoNetAccount.setSeedId(10203024L);
        cryptoNetAccounts.add(cryptoNetAccount);
        cryptoNetAccount = CryptoNetAccount();
        cryptoNetAccount.setAccountIndex(3);
        cryptoNetAccount.setName("Alejandra Mendoza Sanchez");
        cryptoNetAccount.setId(2);
        cryptoNetAccount.setSeedId(10203024L);
        cryptoNetAccounts.add(cryptoNetAccount);

        cryptoNetAccountsSpinner?.setAndroidLayout() //Set the defaul simple spinner android
        cryptoNetAccountsSpinner?.setCryptoAccountItems(cryptoNetAccounts)
        cryptoNetAccountsSpinner?.initCryptoNetAccountAdapter() //Init the specified adaper
        cryptoNetAccountsSpinner?.selectFirstItem() //Select the firts item in the spinner
        cryptoNetAccountsSpinner?.onAccountNotExists(object : CryptoNetAccountsSpinner.OnAccountNotExists {
            override fun onAccountNotExists(field: View) {
                globalActivity.runOnUiThread(object: Runnable{
                    override fun run() {
                        ToastIt.showShortToast(globalActivity,"onAccountNotExists")
                    }
                })
            }
        })
        cryptoNetAccountsSpinner?.onAccountExists(object: CryptoNetAccountsSpinner.OnAccountExists{ //validateExistBitsharesAccountRequest
            override fun onAccountExists(field: View) {
                globalActivity.runOnUiThread(object: Runnable{
                    override fun run() {
                        ToastIt.showShortToast(globalActivity,"onAccountExists")
                    }
                })
            }
        })
        cryptoNetAccountsSpinner?.onItemSelectedListener(object: CryptoNetAccountsSpinner.OnItemSelectedListener{
            override fun onItemSelectedListener(cryptoNetAccount: CryptoNetAccount) {

                ToastIt.showShortToast(globalActivity,"onItemSelectedListener") //Comment this to see other toasts

                /*
                * Get the current crypto account selected
                * */
                var cryptoNetAccount:CryptoNetAccount = cryptoNetAccountsSpinner?.getCryptoNetAccountSelected()!!

                ToastIt.showLongToast(globalActivity,"CryptoNetAccount obtained on onItemSelectedListener") //Comment this to see other toasts

                cryptoNetAccountsSpinner?.validateExistBitsharesAccountRequest()
            }
        })
    }
}