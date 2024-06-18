# **Understanding the workflow**

### **Inji Web HomePage**

- Users navigate to the Inji Web portal from their web browser
- The portal features a user-friendly interface accessible to all
- Inji Web Display the list of Issuers supported and sourced from [mimoto-issuers-configuration](https://github.com/mosip/mosip-config/blob/collab1/mimoto-issuers-config.json)

### **Selection of Issuer and credential type:**

- Users can select an Issuer from the list of trusted issuers
- On Clicking the issuer, user will be redirected to credential Types, where user will be displayed with list of credentials supported by the selected issuer.
- Credential Types of the issuers are sourced from the issuers wellknown **"/.well-known/openid-credential-issuer"**
- Users can choose a credential type from the available options provided by the issuers.


### **Authorization**

- When the user selects any credential type, user is redirected to the authorization page for that specific issuer.
- Once authorization is successfull, authorization server return the **"authorizationCode"**
- Inji Web Send the authorization Code to authorization Server through Mimoto to perform the client assertions.
- Once Authorized, Authorization Server issues Token response, which include **access_token**.
- The "access_token" will be used to download the credential through VCI.

### **VC Issuance**

- Inji Web Send the access token to Mimoto.
- Mimoto generates a keypair and signs the credential Request and invokes the VCI endpoint to the issuer's server.
- Then Mimoto Gets the credential back.

### **PDF Download**

- Mimoto uses the download credential data on the [VC PDF template](https://github.com/mosip/mosip-config/blob/collab1/credential-template.html)
- It also applies the issuers wellknown display properties to modify the template text and background colour.
- It also uses order field in wellknown to render the fields in the same order



