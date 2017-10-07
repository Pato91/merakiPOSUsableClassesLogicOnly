/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package merakiposusableclasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author meraki
 */
public class merakiBusinessDBClass {

    Connection connection;
    Statement statement;
    String driver;
    String url;
    String username;
    String password;
    JSONObject selectQRTemp;
    JSONObject selectQResults;
    ArrayList<ArrayList<String>> outPutArrayList;

    public void createDataBase() throws SQLException {
        driver = "com.mysql.jdbc.Driver";
        url = "jdbc:mysql://localhost/?autoReconnect=true&useSSL=false";
        username = "root";
        password = "Ark2016!?veron";

        connection = DriverManager.getConnection(url, username, password);

        statement = connection.createStatement();

        statement.addBatch(
                "SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0");
        statement.addBatch(
                "SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0");
        statement.addBatch(
                "SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES'");
        statement.addBatch(
                "CREATE SCHEMA IF NOT EXISTS `merakiBusinessDB` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci");
        statement.addBatch("USE `merakiBusinessDB`");
        statement.addBatch(
                "CREATE TABLE IF NOT EXISTS `merakiBusinessDB`.`accessLog` ("
                + "  `accessId` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "  `userId` INT UNSIGNED NULL,"
                + "  `logOnTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  `logOffTime` TIMESTAMP NULL,"
                + "  PRIMARY KEY (`accessId`))"
                + "ENGINE = InnoDB");

        statement.addBatch(
                "CREATE TABLE IF NOT EXISTS `merakiBusinessDB`.`businessDetails` ("
                + "`detailId` INT NOT NULL AUTO_INCREMENT,"
                + "`businessName` VARCHAR(200) NOT NULL,"
                + "`primaryContact` VARCHAR(200) NOT NULL,"
                + "`email` VARCHAR(200) NOT NULL,"
                + "`contact` VARCHAR(45) NULL,"
                + "`registrationStatus` INT NULL DEFAULT 1,"
                + "`registrationDate` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (`detailId`))"
                + "ENGINE = InnoDB");
        statement.addBatch(
                "CREATE TABLE IF NOT EXISTS `merakiBusinessDB`.`users` ("
                + "`userId` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "`firstName` VARCHAR(200) NOT NULL,"
                + "`otherNames` VARCHAR(200) NULL,"
                + "`username` VARCHAR(200) NOT NULL,"
                + "`password` TEXT NULL,"
                + "`email` VARCHAR(200) NOT NULL,"
                + "`contact` VARCHAR(45) NULL,"
                + "`status` INT NOT NULL DEFAULT 1,"
                + "`isAdmin` INT NULL DEFAULT 0,"
                + "`createdBy` VARCHAR(200) NOT NULL,"
                + "`createdOn` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,"
                + "`invalidatedOn` NVARCHAR(45) NULL,"
                + "`businessDetails_detailId` INT NOT NULL,"
                + "`isCreator` INT NULL DEFAULT 0,"
                + "PRIMARY KEY (`userId`, `businessDetails_detailId`),"
                + "INDEX `fk_users_businessDetails1_idx` (`businessDetails_detailId` ASC),"
                + "CONSTRAINT `fk_users_businessDetails1`"
                + "  FOREIGN KEY (`businessDetails_detailId`)"
                + "  REFERENCES `merakiBusinessDB`.`businessDetails` (`detailId`)"
                + "  ON DELETE NO ACTION"
                + "  ON UPDATE NO ACTION)"
                + "ENGINE = InnoDB");
        statement.addBatch(
                "CREATE TABLE IF NOT EXISTS `merakiBusinessDB`.`licenses` ("
                + "`licenseId` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "`license` VARCHAR(200) NOT NULL,"
                + "`status` INT NULL DEFAULT 1,"
                + "`business` VARCHAR(200) NULL,"
                + "`consumeDate` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,"
                + "`expiryDate` TIMESTAMP NULL,"
                + "`businessDetails_detailId` INT NOT NULL,"
                + "PRIMARY KEY (`licenseId`, `businessDetails_detailId`),"
                + "INDEX `fk_licenses_businessDetails1_idx` (`businessDetails_detailId` ASC),"
                + "CONSTRAINT `fk_licenses_businessDetails1`"
                + "  FOREIGN KEY (`businessDetails_detailId`)"
                + "  REFERENCES `merakiBusinessDB`.`businessDetails` (`detailId`)"
                + "  ON DELETE NO ACTION"
                + "  ON UPDATE NO ACTION)"
                + "ENGINE = InnoDB");
        statement.addBatch(
                "CREATE TABLE IF NOT EXISTS `merakiBusinessDB`.`inventory` ("
                + "`itemId` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "`barcode_ID` VARCHAR(200) NOT NULL,"
                + "`itemName` VARCHAR(200) NOT NULL,"
                + "`description` TEXT NULL,"
                + "`type` VARCHAR(200) NULL,"
                + "`inputQuantity` INT NULL,"
                + "`remainingQuantity` INT NULL,"
                + "`unitQuantity` INT NULL,"
                + "`purchasePrice` DECIMAL(20,2) NULL DEFAULT 0.00,"
                + "`unitPrice` DECIMAL(20,2) NULL DEFAULT 0.00,"
                + "`createdBy` VARCHAR(200) NULL,"
                + "`createdOn` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,"
                + "`fullyConsumedOn` TIMESTAMP NULL,"
                + "`wholeSellPrice` DECIMAL(20,2) NULL DEFAULT 0.00,"
                + "`minWSQty` INT NULL,"
                + "PRIMARY KEY (`itemId`))"
                + "ENGINE = InnoDB");
        statement.addBatch(
                "CREATE TABLE IF NOT EXISTS `merakiBusinessDB`.`retialInventory` ("
                + "`retialId` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "`packetQty` INT NOT NULL,"
                + "`remainingQty` INT NOT NULL,"
                + "`unitPrice` DECIMAL(20,2) NULL DEFAULT 0.00,"
                + "`packetValue` DECIMAL(20,2) NULL DEFAULT 0.00,"
                + "`retailedBy` VARCHAR(200) NOT NULL,"
                + "`retailedOn` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,"
                + "`inventory_itemId` BIGINT UNSIGNED NOT NULL,"
                + "PRIMARY KEY (`retialId`, `inventory_itemId`),"
                + "INDEX `fk_retialInventory_inventory_idx` (`inventory_itemId` ASC),"
                + "CONSTRAINT `fk_retialInventory_inventory`"
                + "  FOREIGN KEY (`inventory_itemId`)"
                + "  REFERENCES `merakiBusinessDB`.`inventory` (`itemId`)"
                + "  ON DELETE NO ACTION"
                + "  ON UPDATE NO ACTION)"
                + "ENGINE = InnoDB");

        statement.addBatch(
                "CREATE TABLE IF NOT EXISTS `merakiBusinessDB`.`sales` ("
                + "`saleId` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "`barcodeID` VARCHAR(200) NULL,"
                + "`itemName` VARCHAR(200) NULL,"
                + "`description` TEXT NULL,"
                + "`type` VARCHAR(200) NULL,"
                + "`quantity` INT NULL DEFAULT 0,"
                + "`unitPrice` DECIMAL(20,2) NULL DEFAULT 0.00,"
                + "`totalAmount` DECIMAL(20,2) NULL DEFAULT 0.00,"
                + "`saleDate` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,"
                + "`saleType` INT NOT NULL,"
                + "`purchasePrice` DECIMAL(20,2) NULL DEFAULT 0.00 ,"
                + "`users_userId` BIGINT UNSIGNED NOT NULL,"
                + "PRIMARY KEY (`saleId`, `users_userId`),"
                + "INDEX `fk_sales_users1_idx` (`users_userId` ASC),"
                + "CONSTRAINT `fk_sales_users1`"
                + "  FOREIGN KEY (`users_userId`)"
                + "  REFERENCES `merakiBusinessDB`.`users` (`userId`)"
                + "  ON DELETE NO ACTION ON UPDATE NO ACTION)"
                + "ENGINE = InnoDB");
        statement.addBatch(
                "CREATE TABLE IF NOT EXISTS `merakiBusinessDB`.`expenses` ("
                + "`expenseId` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "`productService` VARCHAR(200) NOT NULL,"
                + "`rate` INT NULL,"
                + "`quantity` INT NULL,"
                + "`paidTo` VARCHAR(200) NULL,"
                + "`dueDate` TIMESTAMP NULL,"
                + "`unitPayment` DECIMAL(20,2) NULL DEFAULT 0.00,"
                + "`totalValue` DECIMAL(20,2) NULL DEFAULT 0.00,"
                + "`createdOn` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,"
                + "`users_userId` BIGINT UNSIGNED NOT NULL,"
                + "PRIMARY KEY (`expenseId`, `users_userId`),"
                + "CONSTRAINT `fk_expenses_users1`"
                + "  FOREIGN KEY (`users_userId`)"
                + "  REFERENCES `merakiBusinessDB`.`users` (`userId`)"
                + "  ON DELETE NO ACTION"
                + "  ON UPDATE NO ACTION)"
                + "ENGINE = InnoDB");

        statement.addBatch(
                "CREATE TABLE IF NOT EXISTS `merakibusinessdb`.`receiptnumbers` ("
                + "`receiptId` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                + "`receiptnumber` BIGINT UNSIGNED NULL DEFAULT 0,"
                + "  PRIMARY KEY (`receiptId`),"
                + "  UNIQUE INDEX `receiptnumber_UNIQUE` (`receiptnumber` ASC))"
                + "ENGINE = InnoDB");
        statement.addBatch("SET SQL_MODE=@OLD_SQL_MODE");
        statement.addBatch("SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS");
        statement.addBatch("SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS");

        statement.executeBatch();
        connection.close();
    }

    //SQL select statement processor
    public JSONObject processSQLSelect(String sqlCommand) throws
            ClassNotFoundException, JSONException {
        try {

            driver = "com.mysql.jdbc.Driver";
            url = "jdbc:mysql://localhost/merakiBusinessDB?autoReconnect=true&useSSL=false";
            username = "root";
            password = "Ark2016!?veron";

            selectQResults = new JSONObject();

            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);

            //Get a new statement for the current connection
            statement = connection.createStatement();

            //Executing SQL command
            ResultSet resultSet = statement.executeQuery(sqlCommand);

            while (resultSet.next()) {
                selectQRTemp = new JSONObject();
                for (int x = 1; x <= resultSet.getMetaData().getColumnCount();
                        x++) {
                    if (resultSet.getString(x).isEmpty()) {
                        selectQRTemp.put(
                                resultSet.getMetaData().getColumnLabel(x), "");
                    } else if (!resultSet.getString(x).isEmpty()) {
                        selectQRTemp.put(
                                resultSet.getMetaData().getColumnLabel(x),
                                resultSet.getString(x));
                    }
                }

                selectQResults.put("Row " + resultSet.getRow(), selectQRTemp);

            }

            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Alert exceptionAlert = new Alert(Alert.AlertType.WARNING, ex + "",
                    ButtonType.OK);
            exceptionAlert.showAndWait();
        }

        return selectQResults;
    }

    public int processSQLInsert(String insertStatement) throws
            ClassNotFoundException {
        int status = 0;
        try {
            driver = "com.mysql.jdbc.Driver";
            url = "jdbc:mysql://localhost/merakiBusinessDB?autoReconnect=true&useSSL=false";
            username = "root";
            password = "Ark2016!?veron";

            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);

            //Get a new statement for the current connection
            statement = connection.createStatement();

            status = statement.executeUpdate(insertStatement);
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Alert exceptionAlert = new Alert(Alert.AlertType.WARNING, ex + "",
                    ButtonType.OK);
            exceptionAlert.showAndWait();
        }
        return status;
    }

    public int processSQLUpdate(String updateStatement) throws
            ClassNotFoundException {
        int status = 0;
        try {
            driver = "com.mysql.jdbc.Driver";
            url = "jdbc:mysql://localhost/merakiBusinessDB?autoReconnect=true&useSSL=false";
            username = "root";
            password = "Ark2016!?veron";

            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);

            //Get a new statement 
            statement = connection.createStatement();

            status = statement.executeUpdate(updateStatement);

            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Alert exceptionAlert = new Alert(Alert.AlertType.WARNING, ex + "",
                    ButtonType.OK);
            exceptionAlert.showAndWait();
        }
        return status;
    }
}
