#!/usr/bin/env bash

set -euo pipefail
read -r -s -p "Please enter a password for the key & keystore (default: password):" PASSWORD
PASSWORD=${PASSWORD:=password}
openssl req -x509 -newkey rsa:2048 -utf8 -days 365000 -nodes -config ca-cert.conf -keyout ca-cert.key -out ca-cert.crt
openssl pkcs12 -export -inkey ca-cert.key -in ca-cert.crt -out ca-cert.p12 -password "pass:$PASSWORD"
keytool -importkeystore -deststorepass "$PASSWORD" -destkeypass "$PASSWORD" -srckeystore ca-cert.p12 -srcstorepass "$PASSWORD" -deststoretype jks -destkeystore server_keystore.jks
keytool -import -v -trustcacerts -alias 1 -file ca-cert.crt -keystore client_truststore.jks -keypass "$PASSWORD" -storepass "$PASSWORD" -noprompt
rm ca-cert.key ca-cert.p12 ca-cert.crt