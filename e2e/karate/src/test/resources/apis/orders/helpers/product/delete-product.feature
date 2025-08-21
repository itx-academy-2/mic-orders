Feature: Cleanup DB
  Scenario: delete product by id
    * def DbUtils = Java.type('com.academy.orders.karate.db.DbUtils')
    * def db = new DbUtils(datasource)
    * eval db.deleteProductById(id)

