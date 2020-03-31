package org.egov.opmscalculator.web.models.opms;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.opmscalculator.web.models.opms.OwnerInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpmsDetail {

	// Common Fields

	@JsonProperty("applicationNumber")
	private String applicationNumber = null;

	@JsonProperty("tenantId")
	private String tenantId = null;

	@JsonProperty("applicationType")
	private String applicationType = null;

	@JsonProperty("financialYear")
	private String financialYear = null;

	// Un-Common Fields

	// Advertisement NOC

	@JsonProperty("isExamptedAdvertisement")
	private String isExamptedAdvertisement = null;

	@JsonProperty("categoryIdAdvertisement")
	private String categoryIdAdvertisement = null;

	// @JsonProperty("subCategotyIdAdvertisement")
	// private String subCategotyIdAdvertisement = null;

	@JsonProperty("durationAdvertisement")
	private String durationAdvertisement = null;

	@JsonProperty("fromDateAdvertisement")
	private String fromDateAdvertisement = null;

	@JsonProperty("toDateAdvertisement")
	private String toDateAdvertisement = null;

	@JsonProperty("squareFeetAdvertisement")
	private String squareFeetAdvertisement = null;

	// RoadCut NOC
	@JsonProperty("amountRoadCut")
	private String amountRoadCut = null;

	@JsonProperty("gstRoadCut")
	private String gstRoadCut = null;

	@JsonProperty("bankPerformanceRoadCut")
	private String bankPerformanceRoadCut = null;

	@NotNull
	@JsonProperty("owners")
	@Valid
	private List<OwnerInfo> owners = new ArrayList<>();

}
