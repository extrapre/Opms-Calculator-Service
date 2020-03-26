package org.egov.opmscalculator.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

import org.egov.common.contract.request.RequestInfo;
import org.egov.opmscalculator.config.OPMSCalculatorConfigs;
import org.egov.opmscalculator.kafka.broker.OPMSCalculatorProducer;
import org.egov.opmscalculator.repository.CalculationRepository;
import org.egov.opmscalculator.repository.ServiceRequestRepository;
import org.egov.opmscalculator.utils.CalculationUtils;
import org.egov.opmscalculator.web.models.Calculation;
import org.egov.opmscalculator.web.models.CalculationReq;
import org.egov.opmscalculator.web.models.CalculationRes;
import org.egov.opmscalculator.web.models.CalulationCriteria;
import org.egov.opmscalculator.web.models.demand.Category;
import org.egov.opmscalculator.web.models.demand.TaxHeadEstimate;
import org.egov.opmscalculator.web.models.opms.EstimatesAndSlabs;
import org.egov.opmscalculator.web.models.opms.OpmsDetail;
import org.egov.tracer.model.CustomException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalculationService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private OPMSCalculatorConfigs config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private CalculationUtils utils;

	@Autowired
	private DemandService demandService;

	@Autowired
	private OPMSCalculatorProducer producer;

	@Autowired
	private MDMSService mdmsService;

	@Autowired
	private CalculationRepository calculationRepository;

	public List<Calculation> calculate(CalculationReq calculationReq) {

		String tenantId = calculationReq.getCalulationCriteria().get(0).getTenantId();

		Object mdmsData = mdmsService.mDMSCall(calculationReq.getRequestInfo(), tenantId);

		List<Calculation> calculations = getCalculation(calculationReq.getRequestInfo(),
				calculationReq.getCalulationCriteria(), mdmsData);

		demandService.generateDemand(calculationReq.getRequestInfo(), calculations, mdmsData);

		CalculationRes calculationRes = CalculationRes.builder().calculations(calculations).build();
		producer.push(config.getSaveTopic(), calculationRes);
		return calculations;

	}

	public List<Calculation> getCalculation(RequestInfo requestInfo, List<CalulationCriteria> criterias,
			Object mdmsData) {

		List<Calculation> calculations = new LinkedList<>();
		for (CalulationCriteria criteria : criterias) {
			OpmsDetail opms;
			if (criteria.getOpmsDetail() != null && criteria.getApplicationNumber() != null) {
				// opms = utils.getOmpsDetail(requestInfo, criteria.getApplicationNumber(),
				// criteria.getTenantId());

				opms = criteria.getOpmsDetail();
				criteria.setOpmsDetail(opms);
			} else {
				new CustomException("OPMS_DETAILS", "No OPMS details found");
			}

			EstimatesAndSlabs estimatesAndSlabs = getTaxHeadEstimates(criteria, requestInfo, mdmsData);
			List<TaxHeadEstimate> taxHeadEstimates = estimatesAndSlabs.getEstimates();

			Calculation calculation = new Calculation();
			calculation.setApplicationNumber(criteria.getApplicationNumber());
			calculation.setOpmsDetail(criteria.getOpmsDetail());
			calculation.setTenantId(criteria.getTenantId());
			calculation.setTaxHeadEstimates(taxHeadEstimates);
			calculations.add(calculation);
		}
		return calculations;
	}

	private EstimatesAndSlabs getTaxHeadEstimates(CalulationCriteria calulationCriteria, RequestInfo requestInfo,
			Object mdmsData) {
		// 1. Test Base Tax from MDMS -
		// 2. Get calculated amount for types
		List<TaxHeadEstimate> taxHeadEstimates = new ArrayList<>();

		EstimatesAndSlabs estimatesAndSlabs = getBaseTax(calulationCriteria, requestInfo, mdmsData);
		taxHeadEstimates.addAll(estimatesAndSlabs.getEstimates());

		TaxHeadEstimate estimate = new TaxHeadEstimate();
		estimate.setCategory(Category.FEE);

		if (calulationCriteria.getOpmsDetail().getApplicationType().equals("ADVERTISEMENTNOC")) {
			estimate.setTaxHeadCode(config.getBaseAdvertisementFeeHead());
			estimate.setEstimateAmount(getNocFee(calulationCriteria.getOpmsDetail()));

		} else if (calulationCriteria.getOpmsDetail().getApplicationType().equals("ROADCUTNOC")) {
			estimate.setTaxHeadCode(config.getBaseRoadCutFeeHead());
			estimate.setEstimateAmount(new BigDecimal(calulationCriteria.getOpmsDetail().getAmountRoadCut()));

			TaxHeadEstimate estimate2 = new TaxHeadEstimate();
			estimate2.setCategory(Category.FEE);
			estimate2.setTaxHeadCode(config.getBaseRoadCutFeeBankHead());
			estimate2.setEstimateAmount(new BigDecimal(calulationCriteria.getOpmsDetail().getBankPerformanceRoadCut()));

			taxHeadEstimates.add(estimate2);
		} else if (calulationCriteria.getOpmsDetail().getApplicationType().equals("PETNOC")) {
			estimate.setTaxHeadCode(config.getBasePetFeeHead());
			estimate.setEstimateAmount(getNocFee(calulationCriteria.getOpmsDetail()));
		}

		taxHeadEstimates.add(estimate);
		estimatesAndSlabs.setEstimates(taxHeadEstimates);

		return estimatesAndSlabs;
	}

	private EstimatesAndSlabs getBaseTax(CalulationCriteria calulationCriteria, RequestInfo requestInfo,
			Object mdmsData) {

		// This will get all taxes from mdm

		EstimatesAndSlabs estimatesAndSlabs = new EstimatesAndSlabs();
		TaxHeadEstimate estimate = new TaxHeadEstimate();

		// Need add MDM Tax data
		BigDecimal totalTax = mdmsService.getTaxAmount(requestInfo, calulationCriteria.getOpmsDetail(), mdmsData);

		// add type wise tax head
		if (calulationCriteria.getOpmsDetail().getApplicationType().equals("ADVERTISEMENTNOC")) {
			estimate.setTaxHeadCode(config.getBaseAdvertisementTaxHead());
		} else if (calulationCriteria.getOpmsDetail().getApplicationType().equals("ROADCUTNOC")) {
			estimate.setTaxHeadCode(config.getBaseRoadCutTaxHead());
		} else if (calulationCriteria.getOpmsDetail().getApplicationType().equals("PETNOC")) {
			estimate.setTaxHeadCode(config.getBasePetTaxHead());
		}

		estimate.setCategory(Category.TAX);
		estimate.setEstimateAmount(totalTax);
		estimatesAndSlabs.setEstimates(Collections.singletonList(estimate));
		return estimatesAndSlabs;
	}

	private BigDecimal getNocFee(OpmsDetail opmsDetail) {

		BigDecimal results = BigDecimal.ZERO;

		if (opmsDetail.getApplicationType().equals("ADVERTISEMENTNOC")) {

			SortedMap<Integer, JSONObject> calData = calculationRepository.getPriceList(opmsDetail.getApplicationType(),
					opmsDetail.getCategoryIdAdvertisement(), opmsDetail.getFromDateAdvertisement(), "true",
					opmsDetail.getTenantId());

			if (calData != null && !calData.isEmpty()) {
				try {

					String squareFeet = (opmsDetail.getSquareFeetAdvertisement() == null ? ""
							: opmsDetail.getSquareFeetAdvertisement());
					String calculateByPer = (opmsDetail.getDurationAdvertisement() == null ? ""
							: opmsDetail.getDurationAdvertisement());

					String dateBefore = (opmsDetail.getFromDateAdvertisement() == null ? ""
							: opmsDetail.getFromDateAdvertisement());
					String dateAfter = (opmsDetail.getToDateAdvertisement() == null ? ""
							: opmsDetail.getToDateAdvertisement());

					JSONObject jsonObjectType = calData.get(1);
					String calculationType = jsonObjectType.get("calculation_type").toString();

					String subCatagoryId = jsonObjectType.get("sub_category_id").toString();

					// Parsing the date 2020-05-12
					LocalDate fromDate = LocalDate.parse(dateBefore);
					LocalDate toDate = LocalDate.parse(dateAfter);
					BigDecimal sqFeets = new BigDecimal(squareFeet);
					BigDecimal duration = BigDecimal.ZERO;

					if (calculateByPer.equalsIgnoreCase("Daily")) {
						calculateByPer = "perday_price";
						duration = new BigDecimal(ChronoUnit.DAYS.between(fromDate, toDate) + 1);
					} else if (calculateByPer.equalsIgnoreCase("Weekly")) {
						calculateByPer = "perweek_price";
						duration = new BigDecimal(ChronoUnit.WEEKS.between(fromDate, toDate) + 1);
					} else if (calculateByPer.equalsIgnoreCase("Monthly")) {
						calculateByPer = "permonth_price";
						duration = new BigDecimal(ChronoUnit.MONTHS.between(fromDate, toDate) + 1);
					} else if (calculateByPer.equalsIgnoreCase("Annual")) {
						calculateByPer = "annual_price";
						duration = new BigDecimal(ChronoUnit.YEARS.between(fromDate, toDate) + 1);
					} else {
						throw new CustomException("BILLING ERROR",
								"No Found BillingSlabs for the given application type or duration");
					}

					if (calculationType.isEmpty() || duration.intValue() < 0) {
						throw new CustomException("BILLINGS ERROR",
								"No Found BillingSlabs for the given application type or calculation type");
					}

					int size = calData.size();
					if (calculationType.equalsIgnoreCase("range")) {

						for (int i = 1; i <= size; i++) {
							if (i != size) {

								JSONObject jsonObject1 = calData.get(i);
								BigDecimal min = new BigDecimal(jsonObject1.get("min_sqft").toString());
								BigDecimal max = new BigDecimal(jsonObject1.get("max_sqft").toString());

								BigDecimal rate = new BigDecimal(((jsonObject1.get(calculateByPer) == null
										|| jsonObject1.get(calculateByPer) != null
												&& jsonObject1.get(calculateByPer).toString().isEmpty()) ? "0"
														: jsonObject1.get(calculateByPer).toString()));
								max = max.subtract(min);
								sqFeets = sqFeets.subtract(max);
								results = results.add(rate.multiply(duration));

								if (sqFeets.intValue() <= 0)
									break;

							} else {
								JSONObject jsonObject1 = calData.get(i);
								BigDecimal min = new BigDecimal(jsonObject1.get("min_sqft").toString());
								BigDecimal max = new BigDecimal(jsonObject1.get("max_sqft").toString());

								BigDecimal rate = new BigDecimal(((jsonObject1.get(calculateByPer) == null
										|| jsonObject1.get(calculateByPer) != null
												&& jsonObject1.get(calculateByPer).toString().isEmpty()) ? "0"
														: jsonObject1.get(calculateByPer).toString()));

								max = max.subtract(min);
								sqFeets = sqFeets.subtract(max);
								results = results.add(rate.multiply(duration));

								while (sqFeets.intValue() > 0) {
									sqFeets = sqFeets.subtract(max);
									results = results.add(rate.multiply(duration));
								}
							}
						}
					} else if (calculationType.equalsIgnoreCase("units")) {
						for (int i = 1; i <= size; i++) {
							JSONObject jsonObject1 = calData.get(i);

							if (jsonObject1.get("sub_category_id").equals(subCatagoryId)) {
								BigDecimal rate = new BigDecimal(((jsonObject1.get(calculateByPer) == null
										|| jsonObject1.get(calculateByPer) != null
												&& jsonObject1.get(calculateByPer).toString().isEmpty()) ? "0"
														: jsonObject1.get(calculateByPer).toString()));

								results = results.add(rate.multiply(duration));
								break;
							}
						}
					} else if (calculationType.equalsIgnoreCase("days")) {
						for (int i = 1; i <= size; i++) {
							if (i != size) {

								JSONObject jsonObject1 = calData.get(i);
								BigDecimal min = new BigDecimal(jsonObject1.get("min_sqft").toString());
								BigDecimal max = new BigDecimal(jsonObject1.get("max_sqft").toString());

								BigDecimal rate = new BigDecimal(((jsonObject1.get(calculateByPer) == null
										|| jsonObject1.get(calculateByPer) != null
												&& jsonObject1.get(calculateByPer).toString().isEmpty()) ? "0"
														: jsonObject1.get(calculateByPer).toString()));

								max = max.subtract(min);
								results = results.add(rate.multiply(sqFeets).multiply(max));
								duration = duration.subtract(max);

								if (duration.intValue() <= 0)
									break;

							} else {
								JSONObject jsonObject1 = calData.get(i);
								BigDecimal min = new BigDecimal(jsonObject1.get("min_sqft").toString());
								BigDecimal max = new BigDecimal(jsonObject1.get("max_sqft").toString());

								BigDecimal rate = new BigDecimal(((jsonObject1.get(calculateByPer) == null
										|| jsonObject1.get(calculateByPer) != null
												&& jsonObject1.get(calculateByPer).toString().isEmpty()) ? "0"
														: jsonObject1.get(calculateByPer).toString()));

								max = max.subtract(min);
								results = results.add(rate.multiply(sqFeets).multiply(max));
								duration = duration.subtract(max);

								while (duration.intValue() > 0) {
									results = results.add(rate.multiply(sqFeets).multiply(max));
									duration = duration.subtract(max);
								}
							}
						}
					}
				} catch (Exception e) {
					throw new CustomException("BILLINGSLAB ERROR",
							"No Found BillingSlabs for the given application type or category");
				}
			} else {
				throw new CustomException("BILLINGSLAB ERROR",
						"No Found BillingSlabs for the given application type or category");
			}
		} else if (opmsDetail.getApplicationType().equals("PETNOC")) {
			LocalDate date = LocalDate.now();
			String dateString = date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth();

			SortedMap<Integer, JSONObject> calData = calculationRepository.getPriceList(opmsDetail.getApplicationType(),
					"", dateString, "false", opmsDetail.getTenantId());
			if (!calData.isEmpty() && calData.size() == 1) {
				results = new BigDecimal(calData.get(0).get("fixed_price").toString());
			}
		}

		return results;
	}
}
