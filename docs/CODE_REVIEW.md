# Code review and refactor notes

## Original findings

| Finding | Effect | Change |
| --- | --- | --- |
| SQL built by concatenating form input | SQL injection and failures on quotes | Prepared statements for every user-supplied value |
| Database credentials repeated in source | Credentials exposed; setup tied to one machine | Environment configuration, embedded H2 default |
| Empty `Conn.java` plus another `Conn` class in `UpdateEmployee.java` | Conflicting definitions and confusing build | One JDBC repository, standard package structure |
| Connections/results not consistently closed | Resource leaks and fragile updates | Try-with-resources in every repository operation |
| Database calls inside UI callbacks | Frozen UI during slow database operations | Background `SwingWorker`, disabled controls while busy |
| Random small numeric identifiers | Collisions possible | UUID primary keys |
| Name validation allowed ASCII letters only | Rejected spaces and international names | Required/length validation supporting Unicode |
| No reproducible build, schema or tests | Difficult to run or verify | Maven build, automatic schema initialization, JUnit tests, CI |
| Destructive action without confirmation | Accidental deletion | Explicit confirmation with selected employee name |

## Refactor boundaries

The project now stores ID, name, email, department and salary. Legacy fields (father's name, national ID, date of birth, address, phone and education) were deliberately omitted to keep this demo focused and reduce unnecessary personal data. There is no automated legacy migration. Existing databases should be preserved separately.

The project demonstrates basic CRUD and quality practices, not payroll, ERP or production security expertise. H2 tests do not establish MySQL compatibility or GUI usability. Manual desktop verification should cover add/edit validation, searching after sorting, cancellation, deletion confirmation, persistence after restart and recovery from a failed connection.

## Verification

Run `mvn clean verify` for compilation, automated validation/database tests and executable JAR packaging. The repository tests use real H2 databases, including a temporary file database to verify persistence across connections. The workflow repeats the Maven verification on Java 17.

Local verification passed: **12 JUnit tests**, executable JAR packaging, and the optional Swing smoke test covering launch, add, edit, sorted selection and deletion cancellation/confirmation. MySQL integration and real desktop accessibility remain unverified. The screenshot uses fictional sample data.

## Further improvements

- Add desktop UI automation and a dedicated MySQL integration-test job.
- Add optimistic locking, schema migrations, pagination and indexes before multi-user use.
- Add role-based access and audit trails only with a defined real-world use case.
- Review database backup, encryption and retention policies before handling personal data.
