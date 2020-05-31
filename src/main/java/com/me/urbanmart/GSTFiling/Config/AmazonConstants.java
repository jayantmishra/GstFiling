package com.me.urbanmart.GSTFiling.Config;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * @author jaymishr
 */

public class AmazonConstants {

    // These values might change with time, hence keeping them in one place

    public static final String STATE_COLUMN = "Ship To State";
    public static final String TAXABLE_AMOUNT_COLUMN = "Tax Exclusive Gross";
    public static final String TAX_RATE_COLOUMN = "Igst Rate";
    public static final String TRANSACTION_COLOUMN = "Transaction Type";
    public static final String INVOICE_AMOUNT_COLOUMN = "Invoice Amount";
    public static final String GST_AMOUNT_COLOUMN = "Total Tax Amount";


    // Transaction type constants
    public static final String CANCELLED_ORDER = "Cancel";
    public static final String REFUND_ORDER = "Refund";
    public static final String SHIPMENT_ORDER = "MFNShipment";

    //
    public static final String B2B = "B2B";
    public static final String B2C = "B2C";
    public static final String DASH = "-";
    public static final String UNDERSCORE = "_";



    public static final int CANCELLED_ORDER_INDEX = 3;
    public static final int STATE_COLUMN_INDEX =  24;
    public static final int INVOICE_AMOUNT_INDEX = 27;
    public static final int TAXABLE_AMOUNT_INDEX = 28;
    public static final int TOTAL_TAX_INDEX = 29;

    public static final int GST_CGST_INDEX= 30;
    public static final int GST_SGST_INDEX = 31;
    public static final int GST_UTGST_INDEX=32;
    public static final int GST_IGST_INDEX = 33;

}
