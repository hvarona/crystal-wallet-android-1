package cy.agorise.crystalwallet.views.natives.business

import android.R
import android.content.Context
import android.util.AttributeSet
import com.jaredrummler.materialspinner.MaterialSpinner
import cy.agorise.crystalwallet.models.CryptoNetAccount
import cy.agorise.crystalwallet.viewmodels.validators.UIValidatorListener
import cy.agorise.crystalwallet.viewmodels.validators.validationfields.ValidationField
import cy.agorise.crystalwallet.views.CryptoNetAccountAdapter

class CryptoNetAccountsSpinner : MaterialSpinner {

    /*
    * Contains the list of accounts
    * */
    private var cryptoNetAccounts: MutableList<CryptoNetAccount>? = null

    /*
    * Contains the current layout for the rows
    * */
    private var layout:Int? = null

    /*
    * Contains the current adapter
    * */
    private var fromSpinnerAdapter:CryptoNetAccountAdapter? = null

    /*
    * Validation listener
    * */
    private var listener: UIValidatorListener? = null

    private var onValidationField:OnValidationFailed? = null
    private var onValidationSucceeded:OnValidationSucceeded? = null
    private var onAccountNotExists:OnAccountNotExists? = null




    constructor(context: Context?, attrs: AttributeSet?) : super(context,attrs){
    }


    /*
    * Example of implementation
    * */
    fun test(){

        var cryptoNetAccountsSpinner:CryptoNetAccountsSpinner? = null //Simulation of getViewById
        cryptoNetAccountsSpinner?.setLayout(R.layout.simple_spinner_dropdown_item) //Asign the specified list row view
        cryptoNetAccountsSpinner?.initCryptoNetAccountAdapter() //Init the specified adaper
        cryptoNetAccountsSpinner?.onValidationSucceeded(object: OnValidationSucceeded { //Validation
            override fun onValidationSucceeded(field: ValidationField) {

            }
        })
        cryptoNetAccountsSpinner?.onValidationFailed(object : OnValidationFailed{ //Validation
            override fun onValidationFailed(field: ValidationField) {

            }
        })
        cryptoNetAccountsSpinner?.onAccountNotExists(object : OnAccountNotExists{ //Validation
            override fun onAccountNotExists(field: ValidationField) {

            }
        })

    }

    /*
    * Set the current layoutview
    * */
    fun setLayout(layout:Int){
        this.layout = layout
    }

    /*
    * Init the spinner
    * */
    fun initCryptoNetAccountAdapter(){


        fromSpinnerAdapter = CryptoNetAccountAdapter(this.context, this.layout!!, cryptoNetAccounts)
        this.setAdapter(fromSpinnerAdapter!!)
        this.setItems(cryptoNetAccounts!!) //Ad the items
    }

    /*
    * Listener for validations
    * */
    fun onValidationFailed(onValidationField:OnValidationFailed){
        this.onValidationField = onValidationField
    }
    fun onValidationSucceeded(onValidationSucceeded: OnValidationSucceeded){
        this.onValidationSucceeded = onValidationSucceeded
    }
    fun onAccountNotExists(onAccountNotExists: OnAccountNotExists){
        this.onAccountNotExists = onAccountNotExists
    }
    /*
    * End of Listener for validations
    * */

    /*
    * Add the items list to the spinner
    * */
    fun getCryptoAccountsList() : List<CryptoNetAccount>{
        return this.cryptoNetAccounts!!
    }


    /*
    * Interface for validation failed
    * */
    interface OnValidationFailed{
        fun onValidationFailed(field: ValidationField)
    }
    interface OnValidationSucceeded{
        fun onValidationSucceeded(field: ValidationField)
    }
    interface OnAccountNotExists{
        fun onAccountNotExists(field: ValidationField)
    }
}