package cy.agorise.crystalwallet.dialogs.material

import android.app.Activity
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import cy.agorise.crystalwallet.R



/*
* This class is used to show a question dialog
*
*
* Use example:
*
*
* var questionDialog:QuestionDialog = QuestionDialog(globalActivity)
        questionDialog.setText(getString(R.string.continue_question))
        questionDialog.setOnNegative(object : NegativeResponse{
            override fun onNegative(dialogMaterial: DialogMaterial) {
                var test:String = ""
                test = ""
                dialogMaterial.dismiss()
            }
        })
        questionDialog.setOnPositive(object : PositiveResponse{
            override fun onPositive() {
                var test:String = ""
                test = ""
            }
        })
        questionDialog.show()

* */
class QuestionDialog : CrystalDialog {

    constructor(activity: Activity) : super(activity) {

        /*
        * Create the buttons needed
        * */
        this.builder.positiveText(activity.getString(R.string.ok))
        this.builder.negativeText(activity.getString(R.string.cancel))
        this.builder.onPositive(object : MaterialDialog.SingleButtonCallback{
            override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                if(positiveResponse != null){
                    positiveResponse!!.onPositive()
                }
            }

        })
        this.builder.onNegative(object : MaterialDialog.SingleButtonCallback {
            override fun onClick(dialog: MaterialDialog, which: DialogAction) {
                if(negativeResponse != null){
                    negativeResponse!!.onNegative(dialogMaterial)
                }
            }

        })
    }
}