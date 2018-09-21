package cy.agorise.crystalwallet.dialogs.material

import android.app.Activity

/*
* Dialog material that shows loading gif and and explicit message
* */
open class CrystalDialog : DialogMaterial{

    constructor(activity: Activity) : super(activity) {

        /*
        * Prepare the dialog
        * */
        this.builder.title("")
        this.builder.content("")
    }
}

/*
*   Internal interfaces
* */
interface PositiveResponse{
    fun onPositive()
}
interface NegativeResponse{
    fun onNegative(dialogMaterial:DialogMaterial)
}