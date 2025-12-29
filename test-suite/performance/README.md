```
 Get-Content booking-stress-test.js | docker run --rm -i --add-host=host.docker.internal:host-gateway grafana/k6 run -
```