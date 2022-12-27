#### WELCOME TO LABORANT!

This app allows you to have total control of labs. Assigning users to lab, delete, edit running command with user based authentication can be done.

#### Java & Maven Environment
Java 17 - Maven 3.8.6 (As given in the mvn wrapper)
#### Database Setup

* Copy .env_example as .env and fill the properties
* run `docker compose up -d`

#### Ldap setup
In resources file, copy ldap.properties_example and rename it to ldap.properties and fill the credentials as you wish.
server.enabled=false or true,  true makes ldap authentication enabled.
