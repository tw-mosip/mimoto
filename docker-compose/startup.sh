download_file() {
    local OUTPUT_FILE="$1"
    local URL="$2"

    wget -O "$OUTPUT_FILE" "$URL"
    if [ $? -eq 0 ]; then
        echo "File downloaded successfully: $OUTPUT_FILE"
    else
        echo "Failed to download file: $URL"
    fi
}

update_cert_property_value() {
  PROPERTY_FILE="cert.properties"
  KEY="$1"
  NEW_VALUE="$2"
  awk -F "=" -v key="$KEY" -v new_value="$NEW_VALUE" ' { if ($1 == key) { print $1 "=" new_value } else { print $0 } }' $PROPERTY_FILE > tmp.properties
  mv tmp.properties $PROPERTY_FILE
  echo "Updated $KEY to $NEW_VALUE in $PROPERTY_FILE"
}

get_csrf_token() {
  local RESPONSE=$(curl --location 'http://localhost:8088/v1/esignet/csrf/token')
  local TOKEN="$(echo "$RESPONSE" | grep -o '"token":"[^"]*' | sed 's/"token":"//')"
  echo "$TOKEN"
}

create_oidc_client() {
  local RESPONSE=$(curl --location 'http://localhost:8088/v1/esignet/client-mgmt/oidc-client' \
                        --header 'X-XSRF-TOKEN: $1' \
                        --header 'Content-Type: application/json' \
                        --header 'Authorization: Bearer $1' \
                        --header 'Cookie: XSRF-TOKEN=$1' \
                        --data "{
                            \"requestTime\": \"$(date -u +"%Y-%m-%dT%H:%M:%S").$(printf '%03d' $((RANDOM % 1000)))Z\",
                            \"request\": {
                                \"clientId\": \"$2\",
                                \"clientName\": \"$2\",
                                \"publicKey\": $3,
                                \"relyingPartyId\": \"mpartner-default-esignet\",
                                \"userClaims\": [
                                    \"name\",
                                    \"email\",
                                    \"gender\",
                                    \"phone_number\",
                                    \"picture\",
                                    \"birthdate\"
                                ],
                                \"authContextRefs\": [
                                    \"mosip:idp:acr:generated-code\",
                                    \"mosip:idp:acr:biometrics\",
                                    \"mosip:idp:acr:linked-wallet\"
                                ],
                                \"logoUri\": \"https://avatars.githubusercontent.com/u/60199888\",
                                \"redirectUris\": [
                                     \"https://healthservices.dev.mosip.com/userprofile\",
                                     \"io.mosip.residentapp://oauth\"
                                ],
                                \"grantTypes\": [
                                    \"authorization_code\"
                                ],
                                \"clientAuthMethods\": [
                                    \"private_key_jwt\"
                                ]
                            }
                        }"
)
  echo $RESPONSE
}

LOADER_PATH="loader_path"
mkdir $LOADER_PATH

URL="https://repo1.maven.org/maven2/io/mosip/kernel/kernel-auth-adapter/1.2.0.1/kernel-auth-adapter-1.2.0.1.jar"
OUTPUT_FILE="$LOADER_PATH/kernel-auth-adapter.jar"
download_file $OUTPUT_FILE $URL

# CERTGEN
CERTS_PATH="certs"
#CERTS_URL=""
CERTS_OUTPUT_FILE="certgen.zip"
CERTS_PROP_PARTNER_NAME="custom2-oidc-client"
KEY_STORE_P12_FILENAME="oidckeystore.p12"

mkdir $CERTS_PATH
#download_file "$CERTS_PATH/$CERTS_OUTPUT_FILE" "$CERTS_URL"
unzip "$CERTS_OUTPUT_FILE"
#unzip "$CERTS_PATH/$CERTS_OUTPUT_FILE"

cd certgen
update_cert_property_value "partner_name" "$CERTS_PROP_PARTNER_NAME"
update_cert_property_value "path" "$(pwd)/certs"
chmod 777 certgen.sh
sed -i '' 's/\r//g' cert.properties
sed -i '' 's/\r//g' certgen.sh
./certgen.sh
cd ..

chmod 777 "certgen/certs/$CERTS_PROP_PARTNER_NAME/keystore.p12"

cp "certgen/certs/$CERTS_PROP_PARTNER_NAME/keystore.p12" $CERTS_PATH
cd $CERTS_PATH
mv "keystore.p12" $KEY_STORE_P12_FILENAME

CSRF_TOKEN=$(get_csrf_token)

PUBKEY_JWK=$(cat "certgen/certs/$CERTS_PROP_PARTNER_NAME/pubkey.jwk")

OIDC_CLIENT_ID=$(create_oidc_client $CSRF_TOKEN $CERTS_PROP_PARTNER_NAME $PUBKEY_JWK)

docker-compose up -d



