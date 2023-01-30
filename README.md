# Vault App
### Application 

### Makefile
To run the application locally, run:
```shell
make package
make run-package
```

This will start a local SpringBoot instance

For this too work you will need MongoDB running, to do so run:
```shell
make setup-mongo
```

**NOTE Need to have Docker with Kubernetes running to have MongoDB started**

To have the Vault application running in kubernetes run:
```shell
make build
```