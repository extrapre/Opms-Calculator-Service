package org.egov.opmscalculator.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BillingSlabIds {

    @JsonProperty("consumerCode")
    private String consumerCode;

    @JsonProperty("tradeTypeBillingSlabIds")
    private List<String> tradeTypeBillingSlabIds;

    @JsonProperty("accesssoryBillingSlabIds")
    private List<String> accesssoryBillingSlabIds;

}
