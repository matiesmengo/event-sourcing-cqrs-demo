# 📊 Observability Stack

> ⚡ **Full observability for the Mengo microservices ecosystem**  
> Centralized logging, metrics, and distributed tracing for production-like monitoring and debugging.

---

## 🎯 Purpose

The Observability Stack provides a **single source of truth** for monitoring all services:

- **Metrics**: Track system health, performance, and business KPIs.
- **Traces**: Observe request flows and latency across distributed services.
- **Logs**: Collect structured logs for auditing, debugging, and root cause analysis.
- **Dashboards & Alerts**: Visualize metrics, traces, and logs in real-time.

> This stack ensures that developers and operators can quickly identify, debug, and understand system behavior in a microservices environment.

---

## 📌 Key Features

### Full Distributed Tracing
- Automatically propagated via `architecture-starter` interceptors.
- Captures SAGA steps, HTTP calls, and Kafka events.

### Centralized Metrics
- Prometheus scrapes Spring Boot Actuator metrics for all microservices.
- Custom metrics for SAGA success/failure, queue lag, and throughput.

### Structured Logging
- Logs include metadata: `traceId`, `correlationId`, `sagaId`.
- Enables direct correlation between logs, metrics, and traces.

### Pre-configured Dashboards
- Ready-made dashboards for microservices health.
- Metrics for latency, error rate, and throughput.
- Logs and traces are linked directly in Grafana.

### Extensible
- Easily add new services by updating Prometheus scrape targets and Promtail paths.
- Supports future integration with alerting tools like Alertmanager.

---

## 🛠️ Components

### 1️⃣ Jaeger (Distributed Tracing)

**Image:** `jaegertracing/all-in-one:1.74.0`

- Collects **traces across services**.
- Supports **gRPC (4317)**, **HTTP (4318)**, and **Thrift/UDP (6831/6832)**.
- Web UI available on port **16686**.
- Configured with **OTLP** and metrics collection for Prometheus.

**Purpose:**  
Track end-to-end request flows and measure latency per SAGA step or HTTP/Kafka request.

---

### 2️⃣ Prometheus (Metrics)

**Image:** `prom/prometheus:v3.7.2`

- Scrapes **Spring Boot actuator metrics** (`/actuator/prometheus`) for all services.
- Collects Kafka, database, and service-level metrics.
- Runs on port **9090**.
- Configured via `prometheus.yml`.

**Purpose:**  
Aggregate real-time metrics to monitor service health, throughput, error rates, and latency.

---

### 3️⃣ Grafana (Dashboards)

**Image:** `grafana/grafana:12.2`

- Connects to **Prometheus**, **Loki**, and **Jaeger**.
- Pre-configured dashboards for metrics, logs, and traces.
- Web UI accessible on port **3000**.
- Default credentials: `admin/admin` (anonymous access enabled).

**Purpose:**  
Visualize system state in real-time, create alerts, and correlate metrics, logs, and traces.

---

### 4️⃣ Loki (Centralized Logs)

**Image:** `grafana/loki:3.3.4`

- Collects **structured logs** from all services.
- Stores logs in **chunks** on the filesystem.
- Exposes API on port **3100**.
- Configured via `loki-config.yaml`.

**Purpose:**  
Provide centralized log storage, enabling query-based log inspection and correlation with traces and metrics.

---

### 5️⃣ Promtail (Log Forwarder)

**Image:** `grafana/promtail:3.3.4`

- Watches local log directories (e.g., `/host/logs/*.log`).
- Parses logs with **regex and structured labels** (`level`, `service`, `logger`, `thread`).
- Forwards logs to **Loki**.
- Supports timestamps and structured log extraction.

**Purpose:**  
Bridge local service logs into the centralized Loki system.

---

## 📊 Access Dashboards

- **Grafana:** [http://localhost:3000](http://localhost:3000)
- **Prometheus:** [http://localhost:9090](http://localhost:9090)
- **Jaeger:** [http://localhost:16686](http://localhost:16686)
- **Loki logs:** Available via Grafana Explore UI

---

## Local Logs for Debugging

To debug services and observability locally, you need to **share the local logs directory with Docker**.  
  This allows Promtail/Loki to access your service logs for structured logging and correlation.

Example local path on Windows:
> C:\Users\xxxxx\event-sourcing-cqrs-demo\path\to\logs

This setup ensures:

- Loki can read all log files.
- Grafana Explore shows structured logs with traceId, correlationId, and sagaId.
- End-to-end observability is fully functional during local development.

---


