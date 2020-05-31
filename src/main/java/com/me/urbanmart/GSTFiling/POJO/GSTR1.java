package com.me.urbanmart.GSTFiling.POJO;

/**
 * @author jaymishr
 *
 * model class
 */

public class GSTR1 {

    private String state;
    private double invoiceAmount;
    private double taxableAmount;
    private double gstAmount;
    private double taxIGST;
    private double taxSGST;
    private double taxCGST;
    private double taxUTGST;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public double getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(double taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public double getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(double gstAmount) {
        this.gstAmount = gstAmount;
    }

    public double getTaxIGST() {
        return taxIGST;
    }

    public void setTaxIGST(double taxIGST) {
        this.taxIGST = taxIGST;
    }

    public double getTaxSGST() {
        return taxSGST;
    }

    public void setTaxSGST(double taxSGST) {
        this.taxSGST = taxSGST;
    }

    public double getTaxCGST() {
        return taxCGST;
    }

    public void setTaxCGST(double taxCGST) {
        this.taxCGST = taxCGST;
    }

    public double getTaxUTGST() {
        return taxUTGST;
    }

    public void setTaxUTGST(double taxUTGST) {
        this.taxUTGST = taxUTGST;
    }

    @Override
    public String toString() {
        return "GSTR1{" +
                "state='" + state + '\'' +
                ", invoiceAmount=" + invoiceAmount +
                ", taxableAmount=" + taxableAmount +
                ", gstAmount=" + gstAmount +
                ", taxIGST=" + taxIGST +
                ", taxSGST=" + taxSGST +
                ", taxCGST=" + taxCGST +
                ", taxUTGST=" + taxUTGST +
                '}';
    }
}
