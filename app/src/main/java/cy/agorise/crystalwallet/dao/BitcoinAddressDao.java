package cy.agorise.crystalwallet.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import cy.agorise.crystalwallet.models.BitcoinAddress;
import cy.agorise.crystalwallet.models.BitcoinTransaction;
import cy.agorise.crystalwallet.models.BitcoinTransactionExtended;

/**
 * Created by Henry Varona on 10/17/2018.
 */
@Dao
public interface BitcoinAddressDao {

    @Query("SELECT * FROM bitcoin_address")
    LiveData<BitcoinAddress> getAll();

    @Query("SELECT COUNT(*) FROM bitcoin_address ba WHERE ba.address = :address")
    Boolean addressExists(String address);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertBitcoinAddresses(BitcoinAddress... addresses);
}
