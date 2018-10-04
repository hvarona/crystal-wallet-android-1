package cy.agorise.crystalwallet.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import java.util.Date;

/**
 * Represents a Bitcoin alike Transaction
 *
 * Created by Henry Varona on 10/2/2018.
 */
@Entity(
    tableName="bitcoin_transaction",
    primaryKeys = {"crypto_coin_transaction_id"},
    foreignKeys = {
        @ForeignKey(
            entity = CryptoCoinTransaction.class,
            parentColumns = "id",
            childColumns = "crypto_coin_transaction_id",
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class BitcoinTransaction {

    /**
     * The id of the base transaction
     */
    @ColumnInfo(name="crypto_coin_transaction_id")
    protected long cryptoCoinTransactionId;


    /**
     * The transaction id in the blockchain
     */
    @ColumnInfo(name="tx_id")
    @NonNull protected String txId;

    /**
     * The block id in the blockchain
     */
    @ColumnInfo(name="block")
    protected long block;

    /**
     * The fee of the transaction
     */
    @ColumnInfo(name="fee")
    protected long fee;
    /**
     * The confirmations of the transaction in the blockchain
     */
    @ColumnInfo(name="confirmations")
    protected int confirmations;

    public BitcoinTransaction(long cryptoCoinTransactionId, String txId, long block, long fee, int confirmations) {
        this.cryptoCoinTransactionId = cryptoCoinTransactionId;
        this.txId = txId;
        this.block = block;
        this.fee = fee;
        this.confirmations = confirmations;
    }

    public long getCryptoCoinTransactionId() {
        return cryptoCoinTransactionId;
    }

    public void setCryptoCoinTransactionId(long cryptoCoinTransactionId) {
        this.cryptoCoinTransactionId = cryptoCoinTransactionId;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public long getBlock() {
        return block;
    }

    public void setBlock(long block) {
        this.block = block;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }
}
