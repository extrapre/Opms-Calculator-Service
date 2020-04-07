package org.egov.pmcalculator.utils;

public class OPMSCalculatorConstants {


    public static final String MDMS_EGF_MASTER = "egf-master";

    public static final String MDMS_FINANCIALYEAR  = "FinancialYear";

    public static final String MDMS_FINACIALYEAR_PATH = "$.MdmsRes.egf-master.FinancialYear[?(@.code==\"{}\")]";

    public static final String MDMS_STARTDATE  = "startingDate";

    public static final String MDMS_ENDDATE  = "endingDate";

    public static final String MDMS_TAXHEADMASTER = "TaxHeadMaster";

    public static final String MDMS_TAXHEAD_PATH = "$.MdmsRes.BillingService.TaxHeadMaster";

    public static final String MDMS_OPMS_PATH = "$.MdmsRes.BillingService";

    public static final String MDMS_BILLINGSERVICE = "BillingService";

    public static final String MDMS_CALCULATIONTYPE_FINANCIALYEAR= "financialYear";

    public static final String MDMS_CALCULATIONTYPE_TRADETYPE= "tradeType";

    public static final String MDMS_CALCULATIONTYPE_ACCESSORY= "accessory";

    public static final String MDMS_CALCULATIONTYPE_FINANCIALYEAR_PATH = "$.MdmsRes.TradeLicense.CalculationType[?(@.financialYear=='{}')]";

   // public static final String MDMS_ROUNDOFF_TAXHEAD= "TL_ROUNDOFF";




}
