package org.egov.pmcalculator.utils;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pmcalculator.config.OPMSCalculatorConfigs;
import org.egov.pmcalculator.repository.ServiceRequestRepository;
import org.egov.pmcalculator.web.models.AuditDetails;
import org.egov.pmcalculator.web.models.RequestInfoWrapper;
import org.egov.pmcalculator.web.models.opms.OpmsDetail;
import org.egov.pmcalculator.web.models.opms.OpmsDetailResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CalculationUtils {

	@Autowired
	private OPMSCalculatorConfigs config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * Creates tradeLicense search url based on tenantId and applicationNumber
	 * 
	 * @return tradeLicense search url
	 */
	private String getOpmsSearchURL() {
		StringBuilder url = new StringBuilder(config.getOpmsHost());
		url.append(config.getOpmsContextPath());
		url.append(config.getOpmsSearchEndpoint());
		url.append("?");
		url.append("tenantId=");
		url.append("{1}");
		url.append("&");
		url.append("applicationNumber=");
		url.append("{2}");
		return url.toString();
	}

	/**
	 * Creates demand Search url based on tenanatId,businessService and ConsumerCode
	 * 
	 * @return demand search url
	 */
	public String getDemandSearchURL() {
		StringBuilder url = new StringBuilder(config.getBillingHost());
		url.append(config.getDemandSearchEndpoint());
		url.append("?");
		url.append("tenantId=");
		url.append("{1}");
		url.append("&");
		url.append("businessService=");
		url.append("{2}");
		url.append("&");
		url.append("consumerCode=");
		url.append("{3}");
		return url.toString();
	}

	/**
	 * Creates generate bill url using tenantId,consumerCode and businessService
	 * 
	 * @return Bill Generate url
	 */
	public String getBillGenerateURI() {
		StringBuilder url = new StringBuilder(config.getBillingHost());
		url.append(config.getBillGenerateEndpoint());
		url.append("?");
		url.append("tenantId=");
		url.append("{1}");
		url.append("&");
		url.append("consumerCode=");
		url.append("{2}");
		url.append("&");
		url.append("businessService=");
		url.append("{3}");

		return url.toString();
	}

	public AuditDetails getAuditDetails(String by, Boolean isCreate) {
		Long time = System.currentTimeMillis();
		if (isCreate)
			return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
					.build();
		else
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
	}

	/**
	 * Call tl-services to get tradeLicense for the given applicationNumber and
	 * tenantID
	 * 
	 * @param requestInfo
	 *            The RequestInfo of the incoming request
	 * @param applicationNumber
	 *            The applicationNumber whose tradeLicense has to be fetched
	 * @param tenantId
	 *            The tenantId of the tradeLicense
	 * @return The tradeLicense fo the particular applicationNumber
	 */
	/*public OpmsDetail getOmpsDetail(RequestInfo requestInfo, String applicationNumber, String tenantId) {
		String url = getOpmsSearchURL();
		url = url.replace("{1}", tenantId).replace("{2}", applicationNumber);

		Object result = serviceRequestRepository.fetchResult(new StringBuilder(url),
				RequestInfoWrapper.builder().requestInfo(requestInfo).build());

		OpmsDetailResponse response = null;
		try {
			response = mapper.convertValue(result, OpmsDetailResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Error while parsing response of TradeLicense Search");
		}

		if (response == null || CollectionUtils.isEmpty(response.getOpmsDetail()))
			return null;

		return response.getOpmsDetail().get(0);
	}
*/
}
