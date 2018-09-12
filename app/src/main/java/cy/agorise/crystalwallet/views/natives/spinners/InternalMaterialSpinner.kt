package cy.agorise.crystalwallet.views.natives.spinners

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.jaredrummler.materialspinner.MaterialSpinner
import cy.agorise.crystalwallet.viewmodels.validators.UIValidatorListener
import cy.agorise.crystalwallet.viewmodels.validators.validationfields.ValidationField

open class InternalMaterialSpinner : MaterialSpinner {

    /*
    * Contains the current layout for the rows
    * */
    protected var layout:Int? = null

    /*
    * Listeners
    * */
    private var onItemNotSelected: OnItemNotSelected? = null

    /*
    * Contains the this for interfaces
    * */
    protected var this_: View? = null




    constructor(context: Context?, attrs: AttributeSet?) : super(context,attrs){

        /*
        * Save the current this
        * */
        this_ = this
    }

    /*
    * Select the first item in the spinner
    * */
    fun selectFirstItem(){
        this.selectedIndex = 0
    }

    /*
    * Set the default android layout
    * */
    fun setAndroidLayout(){
        this.layout = android.R.layout.simple_spinner_item
    }

    fun onItemNotSelected(onItemNotSelected:OnItemNotSelected){
        this.onItemNotSelected = onItemNotSelected
    }

    /*
    * Validation with listener for item selected or not
    * */
    fun validateOnItemNotSelected(){
        if(this.selectedIndex == -1){
            if(onItemNotSelected != null){
                onItemNotSelected?.onItemNotSelected()
            }
        }
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
    interface OnItemNotSelected{
        fun onItemNotSelected()
    }
}