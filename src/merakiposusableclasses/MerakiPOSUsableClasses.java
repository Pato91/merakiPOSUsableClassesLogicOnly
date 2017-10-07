/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package merakiposusableclasses;

import analytics.SalesAnalytics;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import merakiposusableclasses.TaskPackage.MonitorInventoryTask;
import org.json.JSONException;
import org.json.JSONObject;
import queriesList.QueryBuilder;
import transactions.SellItemsClass;

/**
 *
 * @author meraki
 */
public class MerakiPOSUsableClasses extends Application {

    @Override
    public void start(Stage primaryStage) throws JSONException,
            ClassNotFoundException {
        TextArea btn = new TextArea();
        btn.setText("Say 'Hello World'");
        

        QueryBuilder QueryBuilder = new QueryBuilder();
        merakiBusinessDBClass merakiBusinessDBClass = new merakiBusinessDBClass();
        SellItemsClass SellItemsClass = new SellItemsClass();
        SalesAnalytics SalesAnalytics = new SalesAnalytics();

        ScheduledService<JSONObject> svc = new ScheduledService<JSONObject>() {
            @Override
            protected Task<JSONObject> createTask() {
                return new MonitorInventoryTask();
            }
        };
        svc.setRestartOnFailure(true);
        svc.setPeriod(Duration.seconds(2.0));
        
        JSONObject json = new JSONObject();
        String nullString = null;
        json.put("itemId", "8906036440150");
        json.put("itemName", "CONTUS PLUS");
        json.put("description", nullString);
        json.put("type", nullString);
        json.put("startQty", "1");
        json.put("endQty", "1");
        json.put("startAmount", "5000");
        json.put("endAmount", "7000");
        json.put("startDate", "2016-09-12");
        json.put("endDate", "2016-12-12");
        
        System.out.println(SalesAnalytics.trendingItems(json));
        
//        svc.setOnSucceeded((WorkerStateEvent t) -> {
//            if (!svc.getValue().equals(svc.getLastValue())) {
//                JSONObject svcVal = svc.getValue();
//                Iterator<?> keys = svcVal.keys();
//
//                while (keys.hasNext()) {
//                    try {
//                        String key = (String) keys.next();
//                        JSONObject jsonObj1 = svcVal.getJSONObject(key);
//                        Iterator<?> keys1 = jsonObj1.keys();
//                        btn.appendText("{");
//                        while (keys1.hasNext()) {
//                            String keys11 = (String) keys1.next();
//                            JSONObject jsonObj2 = jsonObj1.getJSONObject(keys11);
//                            Iterator<?> keys2 = jsonObj2.keys();
//                            btn.appendText(keys11 + " {");
//                            while (keys2.hasNext()) {
//                                String keys21 = (String) keys2.next();
//                                btn.appendText(
//                                        keys21 + " : " + jsonObj2.get(keys21));
//                            }
//                            btn.appendText("} \n");
//                        }
//                        btn.appendText("}");
//                    } catch (JSONException ex) {
//                        Logger.getLogger(MerakiPOSUsableClasses.class.getName()).log(
//                                Level.SEVERE, null, ex);
//                    }
//                }
//            }
//        });

        svc.start();
        svc.restart();
        svc.restart();
        svc.restart();
        svc.restart();
        svc.restart();
        svc.restart();
        svc.restart();
        
//        JSONObject salesItems = new JSONObject();
//        
//        JSONObject saleItem1 = new JSONObject();
        
//        saleItem1.put("itemID", "265");
//        saleItem1.put("barcode", "");
//        saleItem1.put("itemName", "");
//        saleItem1.put("quantity", "3");
//        saleItem1.put("saleType", "2");
        
//        JSONObject saleItem2 = new JSONObject();
//        
//        saleItem2.put("itemID", "BYQ76");
//        saleItem2.put("itemName", "MODEM");
//        saleItem2.put("quantity", "3");
//        saleItem2.put("saleType", "1");
//        
//        salesItems.put("1", saleItem1);
//        salesItems.put("2", saleItem2);
        
//        JSONObject statusReport = SellItemsClass.sellRetailInventoryItems(salesItems, "1000000",
//                "1");
        
//        System.out.println(statusReport); 
       

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
