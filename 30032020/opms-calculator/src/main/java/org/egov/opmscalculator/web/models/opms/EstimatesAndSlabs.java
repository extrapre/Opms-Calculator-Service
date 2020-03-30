package org.egov.opmscalculator.web.models.opms;

import java.util.List;

import org.egov.opmscalculator.web.models.demand.TaxHeadEstimate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EstimatesAndSlabs {

    @JsonProperty("estimates")
    private List<TaxHeadEstimate> estimates;

//    @JsonProperty("nocTypeFeeAndBillingSlabIds")
//    private FeeAndBillingSlabIds nocTypeFeeAndBillingSlabIds;

//    @JsonProperty("accessoryFeeAndBillingSlabIds")
//    private FeeAndBillingSlabIds accessoryFeeAndBillingSlabIds;

 

}
