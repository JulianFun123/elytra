# Elytra
## Simple docker-based Minecraft server manager


## Example Setup
### Folder Structure
```
- templates.yml
- proxies.yml
- groups.yml
- servers/
  - server1/
    - server.properties
    - ops.json
    - ...
- templates/
  - lobby/
    - plugins/
    - ...
```

### templates.yml
```yaml
---
templates:
  - name: "lobby"
    dir: "templates/lobby"
    type: "PAPER"
    version: "1.21.4"
    environment:
      MAX_PLAYERS: "20"
    network: "mcserver"
  - name: "build"
    dir: "templates/lobby"
    type: "PAPER"
    version: "1.21.4"
    environment:
      MAX_PLAYERS: "20"
    network: "mcserver"
```

### groups.yml
```yaml
---
groups:
  - name: "lobby"
    templateName: "lobby"
    type: "DYNAMIC"
    lobby: true
    lifecycle:
      scaling:
        min: 1
        max: 3
        checkIntervalSeconds: 5
  - name: "build"
    templateName: "build"
    type: "STATIC"
    labels:
      type: "build"
    lobby: false
    environment:
      MOTD: "A {{labels.type}} server by Elytra tmpl: {{templateName}}"
    lifecycle:
      autostart: true
```

### proxies.yml
```yaml
---
proxies:
  - name: "proxy"
    templateName: "default-proxy"
    autostart: true
    portMapping:
      - 25565:25565
    groups:
      - "lobby"
templates:
  - name: "default-proxy"
    dir: "proxies/default-proxy"
    type: "VELOCITY"
    version: "LATEST"
    port: 25565
    memory: 1024
    environment: { }
    config: { }
    network: "mcserver"
```