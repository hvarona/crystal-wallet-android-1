package cy.agorise.crystalwallet.dialogs.material

import android.app.Activity
import cy.agorise.crystalwallet.R


/*
*
* Class to just call simple loading dialog
*
* Sumple Use:
*
* final CrystalLoading crystalLoading = new CrystalLoading(activity);
* crystalLoading.show();
*
* */
open class CrystalLoading : CrystalDialog {

    constructor(activity:Activity) : super(activity) {

        /*
        * Set loading properties only
        * */
        this.progress()
        this.setText(activity.getString(R.string.loading))
    }
}