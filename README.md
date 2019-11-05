# Validator
> A service for validating replicator correctness.

---

### Overview
```
+-------+
| MySQL |
+-------+ -- BinLog --> +-----------+
                        | Connector | Parses binary logs
                +-------+-----------+
                | sends objects to replicator
                +-----> +------------+ -------> ____
                        | Replicator | Augment  |AS|  
                +-------+------------+ <------- """"
                | Augments to Active Schema and forwards
                +-----> +---------+    +----------------+
                        | Applier | -> | BigTable/Kafka |
                        +---------+    +----------------+
                                                        |
                                                        |
                        +-----------+                   |
                        | Validator |   0.1% write reqs |
                        +-----------+ <-----------------+
                                        Supplier <Kafka>
____                            
|AS| -> Active Schema (Only columns and no rows). Mimics the real-time schema of the tables using a skeleton and applying all table altering operations on it.
""""
+-----------+
| Validator | -> queries BigTable and MySQL for validation
+-----------+ -> Sends discrepancies to reporter <Grafana>
 
```

### Setup/Run instructions
- 

