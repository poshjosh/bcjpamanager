--QueryBuilder#where(String[], LIKE, ?, OR)
--[EL Fine]: sql: 2016-07-12 20:44:39.019--ServerSession(2188237)--Connection(13950346)--
SELECT productid, productName, price FROM product WHERE ((((productName LIKE ? OR description LIKE ?) OR keywords LIKE ?) OR model LIKE ?) AND (productid >= ?)) ORDER BY productid DESC
--bind => [5 parameters bound]
--Found: 5 results

--QueryBuilder#search(String[], ?)
--[EL Fine]: sql: 2016-07-12 20:44:39.195--ServerSession(2188237)--Connection(13950346)--
SELECT productid, productName, price FROM product WHERE ((((productName LIKE ? OR description LIKE ?) OR keywords LIKE ?) OR model LIKE ?) AND (productid >= ?)) ORDER BY productid DESC
--bind => [5 parameters bound]
--Found: 5 results

--CriteriaBuilder and CriteriaQuery
--[EL Fine]: sql: 2016-07-12 20:44:39.367--ServerSession(2188237)--Connection(13950346)--
SELECT productid, productName, price FROM product WHERE ((((productName LIKE ? OR description LIKE ?) OR keywords LIKE ?) OR model LIKE ?) AND (productid >= ?)) ORDER BY productid DESC
--bind => [5 parameters bound]
--Found: 5 results