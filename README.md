# Banking 420
A RESTful API written using Java as part of the Web Services and API development module (Group O) at NCI.

## Setup

- Create a database called **bank_api** and import the schema from **/sql/schema**.
- Build and run the web server in Netbeans.
- Open the client in **/client** to test the client responses.

**Note**: Tested in Safari. Issues in Chrome due to Cross-Domain requests not being allowed. Also sometimes the server locks after multiple requests. Clean build + restart is required.

## API Entry-Points

- API Name: getBalance
- Description: This allows for the retrieval of a balance for a specified account.
- URI: /balance/
- HTTP verb: GET
- Parameters: ```getBalance(@Context UriInfo info) throws SQLException, NamingException```
- Resource contents: In the case that the account was removed the response is ```{"status":"Error","message":"Cant view the balance of a removed account."}```. Otherwise it will be ```{"account":"f33980f5","balance":"0.0000"}```
- Pre-Conditions: The account must have a ```status``` of one, meaning it has not been removed from the system.
- Post-Conditions: N/A.

---

- API Name: getCustomerByID
- Description: This allows for the retrieval of customer information via the customer ID.
- URI: /customer/id
- HTTP verb: GET
- Parameters: ```getCustomerById(@PathParam("id") int id, @Context UriInfo info) throws SQLException, NamingException```
- Resource contents: In the case that the account was removed the response is ```{"status":"Error","message":"The account has been removed"}```. Otherwise it will be ```[{"customer_id":6,"email":"test2@gmail.com","name":"test2"}```
- Pre-Conditions: The account must have a ```status``` of one, meaning it has not been removed from the system.
- Post-Conditions: N/A.

---

- API Name: addCustomerAccountType
- Description: This allows a user to add an additional account type.
- URI: /customer/add-account
- HTTP verb: POST
- Parameters: ```var params = { account: account, api_key: key, account_type: option };```
- Resource contents: In the case that the user already has the account type they are tying to add ```{"status":"200","message":"You cant add another savings account."}```. Otherwise it will be ```{"status":"200","message":"Account added"}```
- Pre-Conditions: The account must have a ```status``` of one, meaning it has not been removed from the system. The user must also already have the same account type which they are trying to add.
- Post-Conditions: A new row is inserted to the ```account``` table to reflect the account the user has created. A relationship is made to the ```customer``` table.

---

- API Name: getCustomerList
- Description: This allows a user to get a list of returned of all users in the system.
- URI: /customer/
- HTTP verb: GET
- Parameters: N/A.
- Resource contents: An list of user JSON Objects will be returned. ```{"customer_id":5,"email":"test1@gmail.com","name":"test1"},{"customer_id":6,"email":"test2@gmail.com","name":"test2"}```
- Pre-Conditions: The account must have a ```status``` of one, meaning it has not been removed from the system.
- Post-Conditions: N/A.

---

- API Name: createCustomer
- Description: This allows a user to be created through the API.
- URI: /customer/create
- HTTP verb: POST.
- Parameters: ```var params = { name: customer_name, email: email, address: address, password: password, account_type: option, api_key: key };```
- Resource contents: A success message is returned if the account was created ```{"status":"200","message":"Customer created successfully."}```. Otherwise there will be an error depending if no API key is present ```{"status":"500","message":"Invalid API key"}```
- Pre-Conditions: All form fields must have been filled out.
- Post-Conditions: A new account is added to the ```account``` table and a relationship is also added in the ```customer``` table.

---

- API Name: deleteCustomerById
- Description: This allows a user to be deleted from the system via their ID.
- URI: /delete/{id}
- HTTP verb: GET.
- Parameters: ```deleteCustomerById(@PathParam("id") int id, @Context UriInfo info) throws SQLException, NamingException```
- Resource contents: A success message is returned if the account was deleted ```{"status":"200","message":"Account has been removed"}```. Otherwise there will be an error depending if no API key is present ```{"status":"500","message":"Invalid API key"}```
- Pre-Conditions: The account must have a ```status``` of 1 meaning it has not already been deleted.
- Post-Conditions: The ```status``` of the account is flipped from 1 to 0, meaning API operations cannot be performed on the account.

---

- API Name: createLodgement
- Description: This allows a user to be deleted from the system via their ID.
- URI: /lodgement/
- HTTP verb: POST.
- Parameters: ```var params = { account: account, amount: amount, api_key: key };```
- Resource contents: A success message is returned if the lodgement was successful ```{"status":"200","message":"Lodgement omplete"}```. Otherwise there will be an error if the account has been removed ```{"status":"405","message":"The account has been removed and therefore cannot be lodged to."}```
- Pre-Conditions: The account must have a ```status``` of 1 meaning it has not already been deleted.
- Post-Conditions: The balance of the account will be updated.

---

- API Name: makeWidthdrawl
- Description: Allows a user to withdraw from their account.
- URI: /withdrawl/
- HTTP verb: POST.
- Parameters: ```var params = { account: account, amount: amount, api_key: key };```
- Resource contents: A success message is returned ```{"status":"200","message":"Withdrawl complete."}```. Otherwise there will be an error if the account has been removed ```{"status":"405","message":"Can't withdraw from a removed account."}```. Also if there is insufficient funds an error will be returned ```{"status":"500","message":"Insufficient funds for this withdrawal."}````
- Pre-Conditions: The account must have a ```status``` of 1 meaning it has not already been delete and the account must have sufficient funds for the transaction.
- Post-Conditions: The ```balance``` of the account is updated to reflect money being withdrawn.

---

- API Name: createTransfer
- Description: Allows a user to transfer from one account to another.
- URI: /transfer/create
- HTTP verb: POST.
- Parameters: ```var params = { from: from, to: to, amount: amount, api_key: key };```
- Resource contents: A success message is returned ```{"status":"200","message":"Transfer successful."}```. Otherwise there will be an error if the account has been removed ```{"status":"405","message":"Can't withdraw from a removed account."}```. Also if there is insufficient funds an error will be returned ```{"status":"500","message":"Insufficient funds for this withdrawal."}````
- Pre-Conditions: The account must have a ```status``` of 1 meaning it has not already been delete and the account must have sufficient funds for the transaction.
- Post-Conditions: The ```balance``` of the account with money being sent to will be credited while the account sending money will be debited.

## API Request Example

**Note**: This is a JavaScript example.

```javascript
$("#transfer").click(function (e) {
    var from = account_number_here;
    var to = account_number_here;
    var amount = value_of_money_to_send_here;

    var params = {
        from: from,
        to: to,
        amount: amount,
        api_key: key
    };

    ajaxRequest('POST', api + '/api/transfer/create?' + jQuery.param(params));
});
```
