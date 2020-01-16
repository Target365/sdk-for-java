## Target365 SDK for Java
[![License](https://img.shields.io/github/license/Target365/sdk-for-java.svg?style=flat)](https://opensource.org/licenses/MIT)

### Getting started
To get started please send us an email at <support@target365.no> containing your EC public key in PEM-format.
You can generate your EC public/private key-pair using openssl like this:
```
openssl ecparam -name prime256v1 -genkey -noout -out private.pem
```
Use openssl to convert it to pk8 format which Java uses.
```
openssl pkcs8 -topk8 -inform pem -in private.pem -outform pem -nocrypt -out private.key
```
The file `private.key` should look something like this:
```
-----BEGIN PRIVATE KEY-----
MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgzNTTnuXqcrI5uSEa
V6REzZG7hU+TzRl0Phe56k9/gPWhRANCAAQwB42Sozmtci4mDjnegx003FBV+9PQ
eYBRvK7GScuDQo2+DjEn4hUsnKDZw9o4y+xRat+ItUGKcvVCMW8Swod5
-----END PRIVATE KEY-----
```

Use this openssl command to extract the public key:
```
openssl ec -in private.key -pubout -out public.key
```
You can then send us the `public.key` file. The file should look something like this:
```
-----BEGIN PUBLIC KEY-----
MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEuVHnFqJxiBC9I5+8a8Sx66brBCz3
Flt70WN9l7WZ8VQVN9DZt0kW5xpiO5aG7qd5K8OcHZeoJRprFJOkBwW4Fg==
-----END PUBLIC KEY-----
```

For more details on using the SDK we strongly suggest you check out our [Java User Guide](USERGUIDE.md).

### Maven
```Xml
<dependency>
  <groupId>com.github.target365</groupId>
  <artifactId>target-365-sdk</artifactId>
  <version>1.3.5</version>
</dependency>
```

### Gradle
```
implementation 'com.github.target365:target-365-sdk:1.3.5'
```
[![Maven Central](https://img.shields.io/maven-central/v/com.github.target365/target-365-sdk.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.target365%22%20AND%20a:%22target-365-sdk%22)

### Test Environment
Our test-environment acts as a sandbox that simulates the real API as closely as possible. This can be used to get familiar with the service before going to production. Please be ware that the simulation isn't perfect and must not be taken to have 100% fidelity.

#### Url: https://test.target365.io/

### Production Environment
Our production environment is a mix of per-tenant isolated environments and a shared common environment. Contact <support@target365.no> if you're interested in an isolated per-tenant environment.

#### Url: https://shared.target365.io/

### Authors and maintainers
Target365 (<support@target365.no>)

### Issues / Bugs / Questions
Please feel free to raise an issue against this repository if you have any questions or problems.

### Contributing
New contributors to this project are welcome. If you are interested in contributing please
send an email to support@target365.no.

### License
This library is released under the MIT license.
