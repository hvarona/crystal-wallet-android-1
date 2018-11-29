package cy.agorise.crystalwallet;

import org.bitcoinj.core.Base58;
import org.junit.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

import cy.agorise.graphenej.Invoice;
import cy.agorise.graphenej.LineItem;
import cy.agorise.graphenej.Util;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    //static String qrCode = "CQEZu91fWYH9dr2kRx2SxfBABxaKXWF7wwAn5Y4oeoe5aXoouAahm1P7a42eog7HtihvPhwHqFzeMBScuxgEonfq1euo9sZwUte9Zy5tbVK9tr3WR8kFXShCCantyM2iXGuucSXLsvpCdgNBUL5tEdZm27Lws71mhTHwy13WutDJBHDGpTn68His6f69F4kTQcZG9Ri9L1PXm";
    static String qrCode = "Y1cyE5NiEfp6icMmzP1VFiQEYuXgX3xHjTBkVBZquZZxxbZWPrefG4D8f1zPEEhmuvcJpcgWEn5Fj2YsKzvzUViRQy3r1SY3usSK6Y1vuWpAcZDZkJxDUKnhPchTj7yT3jXQmnz2EU1fvzgpN12nF2bkRvnYmo9wtCVEECgaXwvVyzz92ZAo5ju71dh";

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        Invoice invoice = Invoice.fromQrCode(qrCode);
        System.out.println("To : " + invoice.getTo());
        System.out.println("Currency : " +invoice.getCurrency());
        System.out.println("Memo : " +invoice.getMemo());


        double amount = 0.0;
        for (LineItem nextItem : invoice.getLineItems()) {
            amount += nextItem.getQuantity() * nextItem.getPrice();
        }
        DecimalFormat df = new DecimalFormat("####.####");
        df.setRoundingMode(RoundingMode.CEILING);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        System.out.println("Amount : " +amount);

        System.out.println(new String(Util.decompress(Base58.decode(qrCode),Util.LZMA)));
    }
}