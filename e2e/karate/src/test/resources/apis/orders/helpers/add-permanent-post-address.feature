Feature: Add permanent post address for the user

  Scenario: Create 1 post address for account_id = 2 with the permanent title
    * def DbUtils = Java.type('com.academy.orders.karate.db.DbUtils')
    * def db = new DbUtils(datasource)

    * text sql =
  """
INSERT INTO post_addresses_v2 (
  id, city, department, delivery_method,
  recipient_first_name, recipient_last_name, recipient_phone,
  title, account_id
)
SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?
WHERE NOT EXISTS (
  SELECT 1 FROM post_addresses_v2
  WHERE account_id = ? AND title = ?
)
    """

    * def makeArgs =
"""
    function(city, dept, method, fn, ln, phone, title, accountId) {
        var id = java.util.UUID.randomUUID();
        var a = [id, city, dept, method, fn, ln, phone, title, java.lang.Long.valueOf(accountId),
            java.lang.Long.valueOf(accountId), title];
        return Java.to(a, 'java.lang.Object[]');
    }
    """

    * def a1 = makeArgs('Kharkiv','54','NOVA','Jane','Doe','+380960998877','permanent: Friend',2)

    * eval db.executeUpdateWithParams(sql, a1)