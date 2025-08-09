Feature: Delete post address test data

  Scenario: Delete 3 post addresses for account_id = 2 (titles permanent: Friend, permanent: Family, temp: 550e8400-e29b-41d4-a716-446655440016)
    * def DbUtils = Java.type('com.academy.orders.karate.db.DbUtils')
    * def db = new DbUtils(datasource)

    * text sql =
"""
DELETE FROM post_addresses_v2
WHERE account_id = ?
  AND title IN (
    'permanent: Friend',
    'permanent: Family',
    'temp: 550e8400-e29b-41d4-a716-446655440016'
  )
    """
    * def args = Java.to([ java.lang.Long.valueOf(2) ], 'java.lang.Object[]')
    * eval db.executeUpdateWithParams(sql, args)