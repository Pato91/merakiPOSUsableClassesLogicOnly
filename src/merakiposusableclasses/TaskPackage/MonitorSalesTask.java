/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package merakiposusableclasses.TaskPackage;

import javafx.concurrent.Task;
import merakiposusableclasses.merakiBusinessDBClass;
import org.json.JSONException;
import org.json.JSONObject;
import queriesList.QueryBuilder;

/**
 *
 * @author meraki
 */
public class MonitorSalesTask extends Task<JSONObject>{
    merakiBusinessDBClass merakiBusinessDBClass = new merakiBusinessDBClass();
    QueryBuilder QueryBuilder = new QueryBuilder();
    
    @Override
    protected JSONObject call() throws ClassNotFoundException, JSONException {
        
        JSONObject selectInventoryItems = new JSONObject();
        selectInventoryItems.put("Values", merakiBusinessDBClass.processSQLSelect(QueryBuilder.getSalesItems()));
        return selectInventoryItems;
    }
}
