package org.egov.opmscalculator.repository.builder;

import org.egov.opmscalculator.web.models.CalculationSearchCriteria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalculationQueryBuilder {

	public static final String GET_PRICE_BOOK = "SELECT pb.calculation_sequence,pb.category_id,pb.sub_category_id,pb.sub_category_id, calculation_type, to_date(pb.effective_from_date::varchar, 'YYYY-MM-DD') effective_from_date,to_date(pb.effective_to_date::varchar, 'YYYY-MM-DD') effective_to_date,pb.min_sqft,pb.max_sqft,pb.perday_price,pb.perweek_price,pb.permonth_price,pb.annual_price, pb.fixed_price FROM egpm_noc_price_book pb WHERE pb.application_type=? AND to_date(?, 'YYYY-MM-DD') between pb.effective_from_date::date and COALESCE(pb.effective_to_date, current_date + 30000)::date AND CASE WHEN ?='true' THEN pb.category_id=? ELSE true=true END and pb.tenant_id=? ORDER BY pb.calculation_sequence";
	
    private static final String INNER_JOIN_STRING = " INNER JOIN ";
    private static final String LEFT_OUTER_JOIN_STRING = " LEFT OUTER JOIN ";

    private static final String QUERY = "SELECT tp.*,acc.*,tp.consumercode as tp_consumercode FROM eg_tl_calculator_tradetype tp " +
               LEFT_OUTER_JOIN_STRING +
            " eg_tl_calculator_accessory acc ON acc.consumercode = tp.consumercode " +
            " WHERE ";


    /**
     * Creates query to search billingSlabs based on tenantId and consumerCode ordered by lastModifiedTime
     * @param criteria The Search criteria
     * @param preparedStmtList The list of object containing the query parameter values
     * @return Search query for billingSlabs
     */
    public String getSearchQuery(CalculationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder builder = new StringBuilder(QUERY);

        builder.append(" tp.tenantid=? ");
        preparedStmtList.add(criteria.getTenantId());

        builder.append(" AND tp.consumercode=? ");
        preparedStmtList.add(criteria.getAplicationNumber());

        builder.append("ORDER BY tp.lastmodifiedtime DESC,acc.lastmodifiedtime DESC LIMIT 1");

        return builder.toString();
    }


}
