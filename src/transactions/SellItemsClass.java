/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactions;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import java.util.Iterator;
import javafx.geometry.Point2D;
import javafx.scene.control.TextArea;
import merakiposusableclasses.merakiBusinessDBClass;
import org.json.JSONException;
import org.json.JSONObject;
import queriesList.QueryBuilder;

/**
 *
 * @author meraki
 */
public class SellItemsClass {

    merakiBusinessDBClass merakiBusinessDBClass = new merakiBusinessDBClass();

    QueryBuilder QueryBuilder = new QueryBuilder();

    /**
     * @param JSONItems -This is a JSON Object that takes an array of JSON
     * Objects with 4 keys i.e. {"Values" : {"Object 1" : {"itemID" : "bar
     * code", "itemName" : "name", "quantity" : "quantity" , "saleType" :
     * "saleType"}, "Object 2" : {}, ...}}
     *
     * @param totalAmountPaid This takes a String containing the total amount
     * paid to compute the change
     *
     * @param users_userId This takes a String containing the user making the
     * transaction, it cannot be null for proper results
     * @return This returns a JSON Object containing the sell status, change and
     * a receipt template
     * @throws org.json.JSONException
     * @throws java.lang.ClassNotFoundException
     */
    public JSONObject sellInventoryItems(JSONObject JSONItems,
            String totalAmountPaid, String users_userId) throws
            JSONException, ClassNotFoundException {
        JSONObject saleStats = new JSONObject();
        TextArea statuses = new TextArea();
        statuses.appendText("");

        if (JSONItems != null) {
            Iterator<?> keys = JSONItems.keys();
            while (keys.hasNext()) {
                String keys21 = (String) keys.next();
                JSONObject singleItem = JSONItems.getJSONObject(keys21);
                String itemID = singleItem.getString("itemID");
                String itemName = null;
                String salesQty = singleItem.getString("quantity");
                String quantity = singleItem.getString("quantity");
                String saleType = singleItem.getString("saleType");
                String userId = null;

                if (users_userId != null) {
                    userId = users_userId;
                }

                if (singleItem.getString("itemName") != null) {
                    itemName = singleItem.getString("itemName");
                }

                //{"Row 1":{"itemId":"1","pPrice":"2000.00","remQty":"2","name":"KABUUTI HERBAL COUGH SYRUP",
                //"description":"COUGH SYRUP 100ML","type":"SYRUP","sPrice":"4000.00","barcode":"250716"}}
                JSONObject dbItem = merakiBusinessDBClass.processSQLSelect(
                        QueryBuilder.getSingleInventoryItem(itemID, itemName));

                String inventoryStatusOutPut = "";

                Iterator<?> itemKey = dbItem.keys();
                while (itemKey.hasNext()) {
                    String keyedItem = (String) itemKey.next();
                    JSONObject itemValues = dbItem.getJSONObject(keyedItem);
                    if (parseInt(itemValues.getString("remQty")) < parseInt(
                            quantity) && parseInt(quantity) > 0) {
                        int iterableQty = parseInt(quantity);
                        
                        while (iterableQty > 0) {
                            
                            JSONObject secondDbItem = merakiBusinessDBClass.processSQLSelect(
                                    QueryBuilder.getSingleInventoryItem(itemID,
                                            itemName));
                            Iterator<?> secondItemKey = secondDbItem.keys();
                            while (secondItemKey.hasNext()) {
                                
                                String secondKeyedItem = (String) secondItemKey.next();
                                JSONObject secondItemValues = secondDbItem.getJSONObject(
                                        secondKeyedItem);
                                int dbQty = parseInt(secondItemValues.getString("remQty"));
                                if (parseInt(
                                        secondItemValues.getString("remQty")) < iterableQty) {
                                    int status = merakiBusinessDBClass.processSQLUpdate(
                                            QueryBuilder.updateInventoryItem(
                                                    secondItemValues.getString(
                                                            "itemId"),
                                                    null, null, null, "0",
                                                    null, null, null));
                                    if (status == 1) {
                                        iterableQty = Math.subtractExact(iterableQty, dbQty);
                                        inventoryStatusOutPut = inventoryStatusOutPut + " 1,";
                                    }
                                } else if (parseInt(secondItemValues.getString(
                                        "remQty")) >= iterableQty) {
                                    int remainingQty = Math.subtractExact(
                                            parseInt(
                                                    secondItemValues.getString(
                                                            "remQty")),
                                                    iterableQty);
                                    int status = merakiBusinessDBClass.processSQLUpdate(
                                            QueryBuilder.updateInventoryItem(
                                                    secondItemValues.getString(
                                                            "itemId"),
                                                    null, null, null, remainingQty+"",
                                                    null, null, null));
                                    if (status == 1) {
                                        iterableQty = Math.subtractExact(iterableQty, dbQty);;
                                        inventoryStatusOutPut = inventoryStatusOutPut + " 1,";
                                    }
                                }
                            }

                        }
                    } else if (parseInt(itemValues.getString("remQty")) >= parseInt(
                            quantity) && parseInt(quantity) > 0) {

                        int remainingQty = Math.subtractExact(parseInt(
                                itemValues.getString("remQty")), parseInt(
                                quantity));
                        int status = merakiBusinessDBClass.processSQLUpdate(
                                QueryBuilder.updateInventoryItem(
                                        itemValues.getString("itemId"),
                                        null, null, null, remainingQty + "",
                                        null, null, null));

                        if (status == 1) {
                            int reduceQuantity = 0;
                            quantity = reduceQuantity + "";
                            inventoryStatusOutPut = inventoryStatusOutPut + " 1,";
                        }
                    } else {
                        inventoryStatusOutPut = inventoryStatusOutPut + " 0,";
                    }

                    if (!inventoryStatusOutPut.contains(" 0,")) {
                        JSONObject item = dbItem.getJSONObject(keyedItem);
                        double sPrice = parseDouble(item.getString("sPrice"));
                        int sQuantity = parseInt(salesQty);
                        double totalAmount = sPrice * sQuantity;

                        String insertStatus = QueryBuilder.makeSale(itemID,
                                itemName,
                                item.getString("description"),
                                item.getString("type"), salesQty,
                                item.getString("sPrice"),
                                totalAmount + "",
                                saleType, item.getString("pPrice"),
                                userId);
                        if (insertStatus.equals("SUCCESS, OK!")) {
                            statuses.appendText("1, ");
                        }

                    }

                }

            }
        }

        if (statuses.getText() != null && statuses.getText().contains("0")) {
            saleStats.put("SALE STATUS", "NOT ALL TRANSACTIONS WERE A SUCCESS");
        } else if (statuses.getText() != null && !statuses.getText().contains(
                "0")) {
            saleStats.put("SALE STATUS", "ALL TRANSACTIONS WERE A SUCCESS");
        }

        return saleStats;
    }
    
    
    /**
     * @param JSONItems -This is a JSON Object that takes an array of JSON
     * Objects with 4 keys i.e. {"Values" : {"Object 1" : {"itemID" : "inventory_itemId", "itemName" : "name", "quantity" : "quantity" , "saleType" :
     * "saleType", "bar code" : "item bar code"}, "Object 2" : {}, ...}}
     *
     * @param totalAmountPaid This takes a String containing the total amount
     * paid to compute the change
     *
     * @param users_userId This takes a String containing the user making the
     * transaction, it cannot be null for proper results
     * @return This returns a JSON Object containing the sell status, change and
     * a receipt template
     * @throws org.json.JSONException
     * @throws java.lang.ClassNotFoundException
     */
    public JSONObject sellRetailInventoryItems(JSONObject JSONItems,
            String totalAmountPaid, String users_userId) throws
            JSONException, ClassNotFoundException {
        JSONObject saleStats = new JSONObject();
        TextArea statuses = new TextArea();
        statuses.appendText("");

        if (JSONItems != null) {
            Iterator<?> keys = JSONItems.keys();
            while (keys.hasNext()) {
                String keys21 = (String) keys.next();
                JSONObject singleItem = JSONItems.getJSONObject(keys21);
                String itemID = singleItem.getString("itemID");
                String itemName = null;
                String salesQty = singleItem.getString("quantity");
                String quantity = singleItem.getString("quantity");
                String saleType = singleItem.getString("saleType");
                String userId = null;

                if (users_userId != null) {
                    userId = users_userId;
                }

                if (singleItem.getString("itemName") != null) {
                    itemName = singleItem.getString("itemName");
                }

                //{"Row 1":{"itemId":"1","pPrice":"2000.00","remQty":"2","name":"KABUUTI HERBAL COUGH SYRUP",
                //"description":"COUGH SYRUP 100ML","type":"SYRUP","sPrice":"4000.00","barcode":"250716"}}
                JSONObject dbItem = merakiBusinessDBClass.processSQLSelect(
                        QueryBuilder.getSingleRetailItem(itemID, null, itemName));

                String inventoryStatusOutPut = "";

                Iterator<?> itemKey = dbItem.keys();
                while (itemKey.hasNext()) {
                    String keyedItem = (String) itemKey.next();
                    JSONObject itemValues = dbItem.getJSONObject(keyedItem);
                    if (parseInt(itemValues.getString("remQty")) < parseInt(
                            quantity) && parseInt(quantity) > 0) {
                        int iterableQty = parseInt(quantity);
                        
                        while (iterableQty > 0) {
                            
                            JSONObject secondDbItem = merakiBusinessDBClass.processSQLSelect(
                                    QueryBuilder.getSingleRetailItem(itemID, null, itemName));
                            Iterator<?> secondItemKey = secondDbItem.keys();
                            while (secondItemKey.hasNext()) {
                                
                                String secondKeyedItem = (String) secondItemKey.next();
                                JSONObject secondItemValues = secondDbItem.getJSONObject(
                                        secondKeyedItem);
                                int dbQty = parseInt(secondItemValues.getString("remQty"));
                                if (parseInt(
                                        secondItemValues.getString("remQty")) < iterableQty) {
                                    int status = merakiBusinessDBClass.processSQLUpdate(
                                            QueryBuilder.updateRetailInventoryItem(
                                                    secondItemValues.getString(
                                                            "itemIdFk"), "0",
                                                    null));
                                    if (status == 1) {
                                        iterableQty = Math.subtractExact(iterableQty, dbQty);
                                        inventoryStatusOutPut = inventoryStatusOutPut + " 1,";
                                    }
                                } else if (parseInt(secondItemValues.getString(
                                        "remQty")) >= iterableQty) {
                                    int remainingQty = Math.subtractExact(
                                            parseInt(
                                                    secondItemValues.getString(
                                                            "remQty")),
                                                    iterableQty);
                                    int status = merakiBusinessDBClass.processSQLUpdate(
                                            QueryBuilder.updateRetailInventoryItem(
                                                    secondItemValues.getString(
                                                            "itemIdFk"), remainingQty+"",
                                                    null));
                                    if (status == 1) {
                                        iterableQty = Math.subtractExact(iterableQty, dbQty);;
                                        inventoryStatusOutPut = inventoryStatusOutPut + " 1,";
                                    }
                                }
                            }

                        }
                    } else if (parseInt(itemValues.getString("remQty")) >= parseInt(
                            quantity) && parseInt(quantity) > 0) {

                        int remainingQty = Math.subtractExact(parseInt(
                                itemValues.getString("remQty")), parseInt(
                                quantity));
                        int status = merakiBusinessDBClass.processSQLUpdate(
                                            QueryBuilder.updateRetailInventoryItem(
                                                    itemValues.getString(
                                                            "itemIdFk"), remainingQty+"",
                                                    null));

                        if (status == 1) {
                            int reduceQuantity = 0;
                            quantity = reduceQuantity + "";
                            inventoryStatusOutPut = inventoryStatusOutPut + " 1,";
                        }
                    } else {
                        inventoryStatusOutPut = inventoryStatusOutPut + " 0,";
                    }

                    if (!inventoryStatusOutPut.contains(" 0,")) {
                        JSONObject item = dbItem.getJSONObject(keyedItem);
                        double sPrice = parseDouble(item.getString("sPrice"));
                        int sQuantity = parseInt(salesQty);
                        double totalAmount = sPrice * sQuantity;

                        String insertStatus = QueryBuilder.makeSale(itemID,
                                item.getString("name"),
                                item.getString("description"),
                                item.getString("type"), salesQty,
                                item.getString("sPrice"),
                                totalAmount + "",
                                saleType, item.getString("pPrice"),
                                userId);
                        if (insertStatus.equals("SUCCESS, OK!")) {
                            statuses.appendText("1, ");
                        }

                    }

                }

            }
        }

        if (statuses.getText() != null && statuses.getText().contains("0")) {
            saleStats.put("SALE STATUS", "NOT ALL TRANSACTIONS WERE A SUCCESS");
        } else if (statuses.getText() != null && !statuses.getText().contains(
                "0")) {
            saleStats.put("SALE STATUS", "ALL TRANSACTIONS WERE A SUCCESS");
        }

        return saleStats;
    }
}
