package util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatMontoUtil {

    public String formatMonto(BigDecimal monto) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        return nf.format(monto);
    }
}
