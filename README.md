# Validator
> A service for ensuring Data Correctness

`Java 8` ☕️

---

### Overview
- Validator was initially built to ensure Data Correctness in [Replicator](https://github.com/mysql-time-machine/replicator).
- Can be used for validating data between any two data sources.

### Configuration
- Sample validator configuration is in `validator-conf.yaml`
- Config options:
    - **data_sources**: Currently supports `mysql`/`hbase`/`bigtable`
    - **task_supplier**: Currently supports `kafka`
    - **reporter**: Currently supports `graphite`
    - **discrepancy_sink**: Currently supports `kafka`
    - **retry_policy**: Specifies delay for retry in case of mismatch. (in milliseconds)
- Detailed configuration instructions can be found [here](https://github.com/mysql-time-machine/validator/tree/master/docs/CONFIGURATION.md).

### Setup/Run instructions
`LOCAL`
- Clone this repository and `$ cd validator/`
- `$ mvn clean package` Builds a JAR from the source
- `$ ./run.sh` Runs the validator using **validator-conf.yaml**

`DOCKER`
- `TODO`