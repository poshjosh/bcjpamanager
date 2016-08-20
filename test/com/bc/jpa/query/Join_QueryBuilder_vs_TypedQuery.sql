--QueryBuilderImpl.join(Productorder.class, joinCol, JoinType.INNER, Orderproduct.class).where(Orderproduct.class, searchCol, variant).build();
SELECT t1.productorderid, t1.datecreated, t1.orderDate, t1.requiredDate, t1.timemodified, t1.buyer, t1.orderstatusid FROM orderproduct t0, orderproduct t2, productorder t1 WHERE (((t2.productvariantid = ?) AND (t0.productvariantid = ?)) AND (t0.productorderid = t1.productorderid))
--bind => [2 parameters bound]

--QueryBuilderImpl2.join(Productorder.class, joinCol, JoinType.INNER, Orderproduct.class).where(Orderproduct.class, searchCol, variant).build();
SELECT t1.productorderid, t1.datecreated, t1.orderDate, t1.requiredDate, t1.timemodified, t1.buyer, t1.orderstatusid FROM orderproduct t0, productorder t1 WHERE ((t0.productvariantid = ?) AND (t0.productorderid = t1.productorderid))
--bind => [1 parameter bound]

--CriteriaBuilder, CriteriaQuery, TypedQuery
SELECT t1.productorderid, t1.datecreated, t1.orderDate, t1.requiredDate, t1.timemodified, t1.buyer, t1.orderstatusid FROM orderproduct t0, productorder t1 WHERE ((t0.productvariantid = ?) AND (t0.productorderid = t1.productorderid))
--bind => [1 parameter bound]
