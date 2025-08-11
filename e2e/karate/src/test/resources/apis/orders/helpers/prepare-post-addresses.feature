Feature: Prepare post address test data

  Scenario: Create 3 post addresses for account_id = 2
    * def DbUtils = Java.type('com.academy.orders.karate.db.DbUtils')
    * def db = new DbUtils(datasource)

    * text sql =
  """
    INSERT INTO post_addresses_v2 (id, city, department, delivery_method, recipient_first_name,
        recipient_last_name, recipient_phone, title, account_id)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

    * def makeArgs =
  """
    function(city, dept, method, fn, ln, phone, title, accountId) {
        var a = [ java.util.UUID.randomUUID(), city, dept, method, fn, ln, phone, title, java.lang.Long.valueOf(accountId) ];
        return Java.to(a, 'java.lang.Object[]');
    }
    """

    * def a1 = makeArgs('Kharkiv','54','NOVA','Jane','Doe','+380960998877','permanent: Friend',2)
    * def a2 = makeArgs('Lviv','22','NOVA','James','Smith','+380960668877','permanent: Family',2)
    * def a3 = makeArgs('Lviv','26','NOVA','James','Smith','+380960668877','temp: 550e8400-e29b-41d4-a716-446655440016',2)

    * eval db.executeUpdateWithParams(sql, a1)
    * eval db.executeUpdateWithParams(sql, a2)
    * eval db.executeUpdateWithParams(sql, a3)