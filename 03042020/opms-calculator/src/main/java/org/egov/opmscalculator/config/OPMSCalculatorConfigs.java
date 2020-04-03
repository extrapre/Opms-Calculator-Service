package org.egov.opmscalculator.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@Data
public class OPMSCalculatorConfigs {

	@Value("${egov.billingservice.host}")
	private String billingHost;

	@Value("${egov.taxhead.search.endpoint}")
	private String taxHeadSearchEndpoint;

	@Value("${egov.taxperiod.search.endpoint}")
	private String taxPeriodSearchEndpoint;

	@Value("${egov.demand.create.endpoint}")
	private String demandCreateEndpoint;

	@Value("${egov.demand.update.endpoint}")
	private String demandUpdateEndpoint;

	@Value("${egov.demand.search.endpoint}")
	private String demandSearchEndpoint;

	@Value("${egov.bill.gen.endpoint}")
	private String billGenerateEndpoint;

	@Value("${egov.demand.minimum.payable.amount}")
	private BigDecimal minimumPayableAmount;

	@Value("${egov.demand.businessservice}")
	private String businessService;

	// opms service Registry
	@Value("${egov.opmsservice.host}")
	private String opmsHost;

	@Value("${egov.opmsservice.context.path}")
	private String opmsContextPath;

	//@Value("${egov.opmsservice.create.endpoint}")
	//private String opmsCreateEndpoint;

	//@Value("${egov.opmsservice.update.endpoint}")
	//private String opmsUpdateEndpoint;

	@Value("${egov.opmsservice.search.endpoint}")
	private String opmsSearchEndpoint;

	// TaxHeads
	@Value("${egov.taxhead.baseadvertisementtax}")
	private String baseAdvertisementTaxHead;

	@Value("${egov.taxhead.baseadvertisementfee}")
	private String baseAdvertisementFeeHead;

	@Value("${egov.taxhead.basepettax}")
	private String basePetTaxHead;

	@Value("${egov.taxhead.basepetfee}")
	private String basePetFeeHead;

	@Value("${egov.taxhead.baseroadcuttax}")
	private String baseRoadCutTaxHead;

	@Value("${egov.taxhead.baseroadcutfee}")
	private String baseRoadCutFeeHead;

	@Value("${egov.taxhead.baseroadcutfeebank}")
	private String baseRoadCutFeeBankHead;

	// MDMS
	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsSearchEndpoint;

	// Kafka Topics
	@Value("${persister.save.opms.calculation.topic}")
	private String saveTopic;

	// CalculaterType Default Values
	@Value("${egov.tl.calculationtype.tradetype.default}")
	private String defaultTradeUnitCalculationType;

	@Value("${egov.tl.calculationtype.accessory.default}")
	private String defaultAccessoryCalculationType;

}
