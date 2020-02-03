# Configuration
---

### Data Sources 
Validator can fetch data from these sources. Add your custom Data Source implementation to `data/`

- Supports:
    - constant
    - MySQL
    - HBase
    - BigTable

### Task Supplier
This supplies the actual tasks to the validator service. Each task should contain a source and target for comparison.
The messages in the task supplier differ based on the Data Source. 
- `TODO` Sample task message

- Supports:
    - Kafka

### Reporter
This publishes metrics to a time-series database for monitoring. 

- Supports:
    - Graphite

### Discrepancy Sink
This publishes discrepancies that are confirmed after retries to a sink.

- Supports:
    - Kafka
    - Console

### Retry Policy
This governs when and how many times to retry in case of mismatches.