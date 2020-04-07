package org.egov.pmcalculator.repository;

import java.util.SortedMap;

import org.egov.pmcalculator.repository.builder.CalculationQueryBuilder;
import org.egov.pmcalculator.repository.rowmapper.PriceBookRowMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Repository
public class CalculationRepository {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
	PriceBookRowMapper priceBookRowMapper;
    
    /**
     * Executes the argument query on db
     * @param query The query to be executed
     * @param preparedStmtList The parameter values for the query
     * @return BillingSlabIds
     */
//    public BillingSlabIds getDataFromDB(String query, List<Object> preparedStmtList){
//        BillingSlabIds billingSlabIds = null;
//        try {
//            billingSlabIds = jdbcTemplate.query(query, preparedStmtList.toArray(), calculationRowMapper);
//        }catch(Exception e) {
//            log.error("Exception while fetching from DB: " + e);
//            return billingSlabIds;
//        }
//
//        return billingSlabIds;
//    }

	public SortedMap<Integer, JSONObject> getPriceList(String type, String catrgoryId, String fromDate,
			String isCatagory, String tenantId) {
		return jdbcTemplate.query(CalculationQueryBuilder.GET_PRICE_BOOK,
				new Object[] { type, fromDate, isCatagory, catrgoryId, tenantId }, priceBookRowMapper);
	}


}
