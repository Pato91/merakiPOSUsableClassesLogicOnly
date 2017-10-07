/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queriesList;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import java.sql.Date;
import java.util.Iterator;
import javafx.scene.control.TextArea;
import merakiposusableclasses.merakiBusinessDBClass;
import org.json.JSONException;
import org.json.JSONObject;
import sortsAndFilters.wordFrequency;

/**
 *
 * @author meraki
 */
public class QueryBuilder {

    wordFrequency wordFrequency = new wordFrequency();
    merakiBusinessDBClass merakiBusinessDBClass = new merakiBusinessDBClass();

    public String getRetailItems() {
        return "SELECT b.barcode_ID as barcode, b.itemName as name, b.description, b.type, a.packetQty, "
                + "ROUND(SUM(a.remainingQty), 0) as remQty, a.unitPrice, a.retailedBy FROM retialinventory "
                + "AS a LEFT JOIN inventory AS b ON a.inventory_itemId = b.itemId  WHERE a.remainingQty > 0 "
                + "GROUP BY b.barcode_ID, b.itemName, b.description, b.type, a.packetQty, a.unitPrice, a.retailedBy "
                + "ORDER BY b.itemName ASC;";
    }

    public String getSingleRetailItem(String itemID, String barcode,
            String itemName) {
        String query = null;
        if (itemID != null) {
            TextArea subQuery = new TextArea();
            subQuery.appendText(
                    "SELECT a.retialId as itemId, a.inventory_itemId as itemIdFk, b.barcode_ID as barcode,"
                    + " b.itemName as name, b.description as description, b.type as type, ");
            subQuery.appendText(
                    "ROUND(SUM(a.remainingQty), 0) as remQty, a.unitPrice as sPrice,"
                    + " ROUND((b.purchasePrice/b.unitQuantity), 2) as pPrice FROM retialinventory ");
            subQuery.appendText(
                    "AS a LEFT JOIN inventory AS b ON a.inventory_itemId = b.itemId  WHERE a.remainingQty > 0 ");
            subQuery.appendText("AND a.inventory_itemId = '" + itemID + "' ");
            if (itemName != null && !itemName.isEmpty()) {
                subQuery.appendText("AND b.itemName = '" + itemName + "' ");
            }

            if (barcode != null && !barcode.isEmpty()) {
                subQuery.appendText("AND b.barcode_ID = '" + barcode + "' ");
            }

            subQuery.appendText(
                    "GROUP BY a.retialId, barcode, name, description, type, sPrice, pPrice ");
            subQuery.appendText("ORDER BY name ASC LIMIT 1;");
            query = subQuery.getText();
            return query;
        }

        return query;
    }

    public String getRetailInputQty(String itemID, String itemName) {
        if (itemID != null) {
            TextArea subQuery = new TextArea();
            subQuery.appendText(
                    "SELECT a.packetQty AS packetQty FROM retialinventory AS a LEFT JOIN inventory AS b ON a.inventory_itemId = b.itemId WHERE ");
            subQuery.appendText(
                    "a.remainingQty > 0 AND a.inventory_itemId = " + parseInt(
                            itemID) + " ");

            if (itemName != null) {
                subQuery.appendText("AND b.itemName = '" + itemName + "' ");
            }

            subQuery.appendText(";");

            return subQuery.getText();
        }
        return null;
    }

    public String updateRetailInventoryItem(String itemId, String remQty,
            String unitPrice) throws ClassNotFoundException, JSONException {
        TextArea query = new TextArea();
        query.appendText("");

        if (itemId != null) {
            query.appendText("UPDATE retialinventory SET ");

            if (remQty != null) {
                //{"Row 1":{"inputQuantity":"2"}}
                JSONObject inputQty = merakiBusinessDBClass.processSQLSelect(
                        this.getRetailInputQty(itemId, null));
                Iterator<?> keys = inputQty.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    JSONObject inputQtyValue = inputQty.getJSONObject(key);
                    if (parseInt(inputQtyValue.getString("packetQty")) < parseInt(
                            remQty)) {
                        query.appendText(
                                " packetQty = " + parseInt(remQty) + ",");
                    }
                }
                query.appendText(
                        " remainingQty = " + parseInt(remQty) + ",");
            }

            if (unitPrice != null) {
                query.appendText(" unitPrice = " + parseDouble(unitPrice) + ",");
            }

            query.appendText(
                    " WHERE inventory_itemId = " + parseInt(itemId) + ";");

            //this piece of code removes the comma before the where clause
            if (query.getText().contains(", WHERE")) {
                String updateQuery = query.getText();
                query.clear();
                query.setText(updateQuery.replaceAll(", WHERE", " WHERE"));
            }

            return query.getText();
        }
        return query.getText();
    }

    public String getSalesItems() {
        return "SELECT saleId, barcodeID, itemName, description, type, quantity, purchasePrice, unitprice, totalAmount,"
                + " ROUND((totalAmount - (quantity * purchasePrice)), 2) as profit, saleDate, saleType, users_userId "
                + "FROM sales;";
    }

    public String getRemInventoryItems() {
        return "SELECT barcode_ID as barcode, itemName as name, description, type, ROUND(SUM(remainingQuantity), 0)"
                + " as remQty, purchasePrice as pPrice, unitPrice as sPrice FROM inventory WHERE remainingQuantity > 0"
                + " GROUP BY barcode_ID, itemName, description, type, purchasePrice, unitPrice ORDER BY itemName ASC;";
    }

    public String getSingleInventoryItem(String barcode, String itemName) {
        if (barcode != null) {
            TextArea query = new TextArea();
            query.appendText(
                    "SELECT itemId, barcode_ID as barcode, itemName as name, description, type, remainingQuantity"
                    + " as remQty, purchasePrice as pPrice, unitPrice as sPrice FROM inventory WHERE barcode_ID = '" + barcode + "'");
            if (itemName != null) {
                query.appendText(" AND itemName = '" + itemName + "'");
            }
            query.appendText(
                    " AND remainingQuantity > 0 ORDER BY itemId ASC LIMIT 1;");
            return query.getText();
        }
        return null;
    }

    public String updateInventoryItem(String itemId, String itemName,
            String description,
            String type, String remQty, String unitQty, String purchasePrice,
            String unitPrice) throws ClassNotFoundException, JSONException {
        TextArea query = new TextArea();
        query.appendText("");

        if (itemId != null) {
            query.appendText("UPDATE inventory SET ");

            if (itemName != null) {
                query.appendText("itemName = '" + itemName + "',");
            }

            if (description != null) {
                query.appendText(" description = '" + description + "',");
            }

            if (type != null) {
                query.appendText(" type = '" + type + "',");
            }

            if (remQty != null) {
                //{"Row 1":{"inputQuantity":"2"}}
                JSONObject inputQty = merakiBusinessDBClass.processSQLSelect(
                        "SELECT inputQuantity FROM inventory WHERE itemId = " + parseInt(
                                itemId) + "");
                Iterator<?> keys = inputQty.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    JSONObject inputQtyValue = inputQty.getJSONObject(key);
                    if (parseInt(inputQtyValue.getString("inputQuantity")) < parseInt(
                            remQty)) {
                        query.appendText(
                                " inputQuantity = " + parseInt(remQty) + ",");
                    }
                }
                query.appendText(
                        " remainingQuantity = " + parseInt(remQty) + ",");
            }

            if (unitQty != null) {
                query.appendText(" unitQuantity = " + parseInt(unitQty) + ",");
            }

            if (purchasePrice != null) {
                query.appendText(
                        " purchasePrice = " + parseDouble(purchasePrice) + ",");
            }

            if (unitPrice != null) {
                query.appendText(" unitPrice = " + parseDouble(unitPrice) + ",");
            }

            query.appendText(" WHERE itemId = " + parseInt(itemId) + ";");

            //this piece of code removes the comma before the where clause
            if (query.getText().contains(", WHERE")) {
                String updateQuery = query.getText();
                query.clear();
                query.setText(updateQuery.replaceAll(", WHERE", " WHERE"));
            }

            return query.getText();
        }
        return query.getText();
    }

    public String getRemWhlSlInventoryItems() {
        return "SELECT barcode_ID as barcode, itemName as name, description, type, ROUND(SUM(remainingQuantity), 0)"
                + " as remQty, purchasePrice as pPrice, unitPrice as sPrice FROM inventory WHERE remainingQuantity > 0"
                + " AND (wholeSellPrice > 0.00 AND minWSQty > 0) GROUP BY barcode_ID, itemName, description, type, "
                + "purchasePrice, unitPrice ORDER BY itemName ASC;";
    }

    /**
     *
     * @param item takes the 'barcode' or unique item identifier string
     * @param type takes the item type string
     * @param minSellingQty takes the minimum quantity string
     * @param maxSellingQty takes the maximum quantity string
     * @param user takes a users_userId string
     * @param startDate takes a starting date string
     * @param endDate takes an ending date string
     * @param minPurchasePrice takes a minimum purchase price string
     * @param maxPurchasePrice takes a maximum purchase price string
     * @param minUnitPrice takes a minimum selling price string
     * @param maxUnitPrice takes a maximum selling price string
     * @return 
     *
     */
    public String getSalesTotal(String item, String type, String minSellingQty,
            String maxSellingQty, String user, String startDate,
            String endDate, String minPurchasePrice, String maxPurchasePrice,
            String minUnitPrice, String maxUnitPrice) {
        String query;
        TextArea subQuery = new TextArea();
        subQuery.appendText("SELECT ");
        if (item != null || minPurchasePrice != null || maxPurchasePrice != null || minUnitPrice != null || maxUnitPrice != null) {
            subQuery.appendText("barcodeID, ");
        }
        if (type != null) {
            subQuery.appendText("type, ");
        }

        if (minPurchasePrice != null || maxPurchasePrice != null) {
            subQuery.appendText("purchasePrice, ");
        }

        if (minUnitPrice != null || maxUnitPrice != null) {
            subQuery.appendText("unitPrice, ");
        }

        if (item != null || minSellingQty != null || maxSellingQty != null) {
            subQuery.appendText("ROUND(SUM(quantity), 0) as totalQty, ");
        }

        subQuery.appendText("ROUND(SUM(totalAmount), 2) as totalAmt, ");

        if (user != null) {
            subQuery.appendText("users_userId, ");
        }

        if (startDate != null || endDate != null) {
            subQuery.appendText("saleDate, ");
        }

        subQuery.appendText("saleType FROM sales ");
        if (item != null) {
            if (!subQuery.getText().contains("WHERE")) {
                subQuery.appendText("WHERE ");
            } else if (subQuery.getText().contains("WHERE")) {
                subQuery.appendText("AND ");
            }
            subQuery.appendText("barcodeID = '" + item + "' ");
        }
        if (type != null) {
            if (!subQuery.getText().contains("WHERE")) {
                subQuery.appendText("WHERE ");
            } else if (subQuery.getText().contains("WHERE")) {
                subQuery.appendText("AND ");
            }
            subQuery.appendText("type = '" + type + "' ");
        }

        ///minimum selling qty
        if (minSellingQty != null) {
            if (!subQuery.getText().contains("WHERE")) {
                subQuery.appendText("WHERE ");
            } else if (subQuery.getText().contains("WHERE")) {
                subQuery.appendText("AND ");
            }
            subQuery.appendText("quantity >= " + parseInt(minSellingQty) + " ");
        }

        if (maxSellingQty != null) {
            if (!subQuery.getText().contains("WHERE")) {
                subQuery.appendText("WHERE ");
            } else if (subQuery.getText().contains("WHERE")) {
                subQuery.appendText("AND ");
            }
            subQuery.appendText("quantity <= " + parseInt(maxSellingQty) + " ");
        }

        if (minPurchasePrice != null) {
            if (!subQuery.getText().contains("WHERE")) {
                subQuery.appendText("WHERE ");
            } else if (subQuery.getText().contains("WHERE")) {
                subQuery.appendText("AND ");
            }
            subQuery.appendText("purchasePrice >= " + parseDouble(
                    minPurchasePrice) + " ");
        }

        if (maxPurchasePrice != null) {
            if (!subQuery.getText().contains("WHERE")) {
                subQuery.appendText("WHERE ");
            } else if (subQuery.getText().contains("WHERE")) {
                subQuery.appendText("AND ");
            }
            subQuery.appendText("purchasePrice <= " + parseDouble(
                    maxPurchasePrice) + " ");
        }

        if (minUnitPrice != null) {
            if (!subQuery.getText().contains("WHERE")) {
                subQuery.appendText("WHERE ");
            } else if (subQuery.getText().contains("WHERE")) {
                subQuery.appendText("AND ");
            }
            subQuery.appendText(
                    "unitPrice >= " + parseDouble(minUnitPrice) + " ");
        }

        if (maxUnitPrice != null) {
            if (!subQuery.getText().contains("WHERE")) {
                subQuery.appendText("WHERE ");
            } else if (subQuery.getText().contains("WHERE")) {
                subQuery.appendText("AND ");
            }
            subQuery.appendText(
                    "unitPrice <= " + parseDouble(maxUnitPrice) + " ");
        }

        if (user != null) {
            if (!subQuery.getText().contains("WHERE")) {
                subQuery.appendText("WHERE ");
            } else if (subQuery.getText().contains("WHERE")) {
                subQuery.appendText("AND ");
            }
            subQuery.appendText("users_userId = " + parseInt(user) + " ");
        }

        if (startDate != null) {
            if (!subQuery.getText().contains("WHERE")) {
                subQuery.appendText("WHERE ");
            } else if (subQuery.getText().contains("WHERE")) {
                subQuery.appendText("AND ");
            }
            subQuery.appendText("saleDate >= '" + java.sql.Date.valueOf(
                    startDate) + "' ");
        }

        if (endDate != null) {
            if (!subQuery.getText().contains("WHERE")) {
                subQuery.appendText("WHERE ");
            } else if (subQuery.getText().contains("WHERE")) {
                subQuery.appendText("AND ");
            }
            subQuery.appendText(
                    "saleDate <= '" + java.sql.Date.valueOf(endDate) + "' ");
        }

        subQuery.appendText(" GROUP BY ");
        if (item != null || subQuery.getText().contains("barcodeID")) {
            subQuery.appendText("barcodeID, ");
        }
        if (type != null) {
            subQuery.appendText("type, ");
        }

        if (minSellingQty != null || maxSellingQty != null) {
            subQuery.appendText("quantity, ");
        }

        if (minPurchasePrice != null || maxPurchasePrice != null) {
            subQuery.appendText("purchasePrice, ");
        }

        if (minUnitPrice != null || maxUnitPrice != null) {
            subQuery.appendText("unitPrice, ");
        }

        if (startDate != null || endDate != null) {
            subQuery.appendText("saleDate, ");
        }

        if (user != null) {
            subQuery.appendText("users_userId, ");
        }

        subQuery.appendText("saleType ORDER BY saleType ASC;");

        query = subQuery.getText();
        return query;
    }

    public String getTrendingSales(String barcode, String itemName,
            String description, String type, String startQty, String endQty,
            String startAmount, String endAmount, String startDate,
            String endDate) {
        TextArea query = new TextArea();

        query.appendText(
                "SELECT barcodeID as barcode, itemName as name, description, type, "
                + "ROUND(SUM(quantity), 0) AS quantity, ROUND(SUM(totalAmount), 2) AS totalAmount, "
                + "saleType, ROUND(SUM(purchasePrice * quantity), 2) AS totalPAmount FROM sales ");
        
        if (barcode != null) {
            if (!query.getText().contains("WHERE")) {
                query.appendText(
                        "WHERE barcodeID = '" + barcode + "' ");
            } else if (query.getText().contains("WHERE")) {
                query.appendText(
                        "AND barcodeID = '" + barcode + "' ");
            }
        }

        if (itemName != null) {
            if (!query.getText().contains("WHERE")) {
                query.appendText(
                        "WHERE itemName = '" + itemName + "' ");
            } else if (query.getText().contains("WHERE")) {
                query.appendText(
                        "AND itemName = '" + itemName + "' ");
            }
        }
        
        if (description != null) {
            if (!query.getText().contains("WHERE")) {
                query.appendText(
                        "WHERE description = '" + description + "' ");
            } else if (query.getText().contains("WHERE")) {
                query.appendText(
                        "AND description = '" + description + "' ");
            }
        }

        if (type != null) {
            if (!query.getText().contains("WHERE")) {
                query.appendText(
                        "WHERE type = '" + type + "' ");
            } else if (query.getText().contains("WHERE")) {
                query.appendText(
                        "AND type = '" + type + "' ");
            }
        }


        if (description != null) {
            if (!query.getText().contains("WHERE")) {
                query.appendText(
                        "WHERE description = '" + description + "' ");
            } else if (query.getText().contains("WHERE")) {
                query.appendText(
                        "AND description = '" + description + "' ");
            }
        }

        if (endDate != null) {
            if (!query.getText().contains("WHERE")) {
                query.appendText(
                        "WHERE saleDate <= '" + Date.valueOf(endDate) + "' ");
            } else if (query.getText().contains("WHERE")) {
                query.appendText(
                        "AND saleDate <= '" + Date.valueOf(endDate) + "' ");
            }
        }

        query.appendText(
                "GROUP BY barcodeID, itemName, description, type, saleType ");

        if (startQty != null) {
            if (!query.getText().contains("HAVING")) {
                query.appendText(
                        "HAVING quantity >= " + parseInt(startQty) + " ");
            } else if (query.getText().contains("HAVING")) {
                query.appendText("AND quantity >= " + parseInt(startQty) + " ");
            }
        }

        if (endQty != null) {
            if (!query.getText().contains("HAVING")) {
                query.appendText("HAVING quantity <= " + parseInt(endQty) + " ");
            } else if (query.getText().contains("HAVING")) {
                query.appendText("AND quantity <= " + parseInt(endQty) + " ");
            }
        }

        if (startAmount != null) {
            if (!query.getText().contains("HAVING")) {
                query.appendText("HAVING totalAmount >= " + parseDouble(
                        startAmount) + " ");
            } else if (query.getText().contains("HAVING")) {
                query.appendText(
                        "AND totalAmount >= " + parseDouble(startAmount) + " ");
            }
        }

        if (endAmount != null) {
            if (!query.getText().contains("HAVING")) {
                query.appendText("HAVING totalAmount <= " + parseDouble(
                        endAmount) + " ");
            } else if (query.getText().contains("HAVING")) {
                query.appendText(
                        "AND totalAmount <= " + parseDouble(endAmount) + " ");
            }
        }

        query.appendText("ORDER BY quantity DESC, totalAmount DESC;");
        return query.getText();
    }

    public String makeSale(String barcode, String name, String description,
            String type, String quantity, String unitPrice, String totalAmount,
            String saleType, String purchasePrice, String userId) throws
            ClassNotFoundException {
        String status = null;
        int statusVal = 0;
        if (barcode != null && name != null && quantity != null && unitPrice != null && userId != null) {
            statusVal = merakiBusinessDBClass.processSQLInsert(
                    "INSERT INTO sales (barcodeID, itemName, description, "
                    + "type, quantity, unitPrice, totalAmount, saleType, purchasePrice, users_userId)"
                    + " VALUES ('" + barcode + "', '" + name + "', '" + description + "', '" + type + "', '" + quantity + "'"
                    + ", '" + unitPrice + "', '" + totalAmount + "', " + parseInt(
                            saleType) + ", " + parseDouble(purchasePrice) + ","
                    + " " + parseInt(userId) + ")");
            if (statusVal == 1) {
                status = "SUCCESS, OK!";
            }
        }
        return status;
    }
}
