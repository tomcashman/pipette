# pipette
Simplified log/metrics transportation and filtering

## Goals
The goal of pipette is to provide a simple, stable and high performance log and metric transport mechanism.

## How it works
Inputs receive data and convert it to JSON format. It is then passed through the system as JSON, applying any filters that are configured. Finally, it is sent to the configured outputs, converting the data from JSON to a format suitable for the target output.

## Supported inputs, filters and outputs
The following inputs, filters and outputs are supported by pipette. Click the links below for examples on how to configure them.

| Inputs        | Filters       | Outputs  |
| ------------- | ------------- | -------- |
| File          | Elasticsearch | File |
| Kafka         | NDiff         | Kafka |
|               | Timestamp     | Elasticsearch |

## Contributing
As stated before, simplicity, stability and performance are the goals of pipette.