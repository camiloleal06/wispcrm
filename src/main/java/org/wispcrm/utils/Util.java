package org.wispcrm.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class Util {

    public Util() {
        // TODO Auto-generated constructor stub
    }

    public static Object formatearMoneda(Long numero) {
        Locale locale = new Locale("es", "CO");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        if (null != numero) {
            return currencyFormatter.format(numero.doubleValue());
        } else {
            return currencyFormatter.format(0.0);
        }
    }
}
