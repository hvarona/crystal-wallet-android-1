package cy.agorise.crystalwallet.views.natives.spinners

import android.R
import android.content.Context
import android.util.AttributeSet
import android.view.View
import cy.agorise.crystalwallet.models.CryptoNetAccount
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequests
import cy.agorise.crystalwallet.requestmanagers.ValidateExistBitsharesAccountRequest
import cy.agorise.crystalwallet.viewmodels.validators.validationfields.ValidationField
import cy.agorise.crystalwallet.views.CryptoNetAccountAdapter

class CryptoNetAccountsSpinner : InternalMaterialSpinner {

    /*
    * Contains the list of accounts
    * */
    private var cryptoNetAccounts: MutableList<CryptoNetAccount>? = null

    /*
    * Contains the current adapter
    * */
    private var fromSpinnerAdapter:CryptoNetAccountAdapter? = null

    /*
    * Listeners
    * */
    private var onValidationField:OnValidationFailed? = null
    private var onValidationSucceeded:OnValidationSucceeded? = null
    private var onAccountNotExists:OnAccountNotExists? = null
    private var onItemSelectedListener:OnItemSelectedListener? = null
    private var onAccountExists:OnAccountExists? = null

    /*
    * Current selected account
    * */
    private var cryptoNetAccount:CryptoNetAccount? = null




    constructor(context: Context?, attrs: AttributeSet?) : super(context,attrs){

        /*
        * Initialy starts with the android layout
        * */
        setAndroidLayout()

        /*
        * When the user changes the item selection save the model
        * */
        setOnItemSelectedListener(OnItemSelectedListener<CryptoNetAccount> { view, position, id, item ->

            /*
            * Save the current model
            * */
            cryptoNetAccount = cryptoNetAccounts?.get(position)

            /*
            *   Deliver response
            * */
            if(onItemSelectedListener != null){
                onItemSelectedListener?.onItemSelectedListener(cryptoNetAccount!!)
            }
        })
    }

    /*
    * Return the current selected cryptonetaccount
    * */
    fun getCryptoNetAccountSelected() : CryptoNetAccount{
        return this.cryptoNetAccount!!
    }


    /*
    * Set the current layoutview
    * */
    fun setLayout(layout:Int){
        this.layout = layout
    }

    /*
    * Init the spinner, before call this method, this list of items should be set
    * */
    fun initCryptoNetAccountAdapter(){

        fromSpinnerAdapter = CryptoNetAccountAdapter(context, this.layout!!, cryptoNetAccounts)
        setAdapter(fromSpinnerAdapter!!)
    }


    fun setCryptoAccountItems(cryptoNetAccounts:MutableList<CryptoNetAccount>){
        this.cryptoNetAccounts = cryptoNetAccounts
    }

    /*
    * Validate if the "selected" account exists
    * */
    fun validateExistBitsharesAccountRequest(){

        if(cryptoNetAccount != null){

            val request = ValidateExistBitsharesAccountRequest(cryptoNetAccount?.name)
            request.setListener {
                if (!request.accountExists) {
                    if(onAccountNotExists != null){
                        onAccountNotExists?.onAccountNotExists(this_!!)
                    }

                }
                else {
                    if(onAccountExists != null){
                        onAccountExists?.onAccountExists(this_!!)
                    }
                }
            }
            CryptoNetInfoRequests.getInstance().addRequest(request)
        }
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
    fun onItemSelectedListener(onItemSelectedListener: OnItemSelectedListener){
        this.onItemSelectedListener = onItemSelectedListener
    }
    fun onAccountExists(onAccountExists: OnAccountExists){
        this.onAccountExists = onAccountExists
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
    interface OnAccountNotExists{
        fun onAccountNotExists(field: View)
    }
    interface OnAccountExists{
        fun onAccountExists(field: View)
    }
    interface OnItemSelectedListener{
        fun onItemSelectedListener(cryptoNetAccount: CryptoNetAccount)
    }
}