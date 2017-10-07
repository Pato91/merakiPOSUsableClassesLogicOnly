/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analytics;

import merakiposusableclasses.merakiBusinessDBClass;
import org.json.JSONException;
import org.json.JSONObject;
import queriesList.QueryBuilder;

/**
 *
 * @author meraki
 */
public class SalesAnalytics {

    QueryBuilder QueryBuilder = new QueryBuilder();
    merakiBusinessDBClass merakiBusinessDBClass = new merakiBusinessDBClass();

    /**
     * @param filterVals is a JSONObject formated as {"itemId" : "bar code",
     * "itemName" : "item name", "description" : "description", "type" : "type",
     * "startQty" : "start quantity", "endQty" : "end quantity", "startAmount" :
     * "start amount", "endAmount" : "end amount", "startDate" : "start date",
     * "endDate" : "end date"}
     * @return JSONObject
     * @throws org.json.JSONException
     * @throws java.lang.ClassNotFoundException
     */
    public JSONObject trendingItems(JSONObject filterVals) throws JSONException,
            ClassNotFoundException {
        JSONObject filterList = new JSONObject();
        String itemId = null;
        String itemName = null;
        String description = null;
        String type = null;
        String startQty = null;
        String endQty = null;
        String startAmount = null;
        String endAmount = null;
        String startDate = null;
        String endDate = null;
        if (filterVals != null) {
            if (filterVals.has("itemId")) {
                if (filterVals.getString("itemId") != null) {
                    itemId = filterVals.getString("itemId");
                }
            }
            if (filterVals.has("itemName")) {
                if (filterVals.getString("itemName") != null) {
                    itemName = filterVals.getString("itemName");
                }
            }

            if (filterVals.has("description")) {
                if (filterVals.getString("description") != null) {
                    description = filterVals.getString("description");
                }
            }
            if (filterVals.has("type")) {
                if (filterVals.getString("type") != null) {
                    type = filterVals.getString("type");
                }
            }
            
            if (filterVals.has("startQty")) {
                if (filterVals.getString("startQty") != null) {
                    startQty = filterVals.getString("startQty");
                }
            }
            if (filterVals.has("endQty")) {
                if (filterVals.getString("endQty") != null) {
                    endQty = filterVals.getString("endQty");
                }
            }

            if (filterVals.has("startAmount")) {
                if (filterVals.getString("startAmount") != null) {
                    startAmount = filterVals.getString("startAmount");
                }
            }
            if (filterVals.has("endAmount")) {
                if (filterVals.getString("endAmount") != null) {
                    endAmount = filterVals.getString("endAmount");
                }
            }
            if (filterVals.has("startDate")) {
                if (filterVals.getString("startDate") != null) {
                    startDate = filterVals.getString("startDate");
                }
            }
            if (filterVals.has("endDate")) {
                if (filterVals.getString("endDate") != null) {
                    endDate = filterVals.getString("endDate");
                }
            }

            System.out.println(QueryBuilder.getTrendingSales(
                            itemId,
                            itemName,
                            description,
                            type,
                            startQty,
                            endQty,
                            startAmount,
                            endAmount,
                            startDate,
                            endDate));
            
            return merakiBusinessDBClass.processSQLSelect(
                    QueryBuilder.getTrendingSales(
                            itemId,
                            itemName,
                            description,
                            type,
                            startQty,
                            endQty,
                            startAmount,
                            endAmount,
                            startDate,
                            endDate));
        }

        return filterList;
    }
}
