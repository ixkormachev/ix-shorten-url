QUICK START with SHORTENED URL SERVICES

SHORTENED URL SERVICES allow to map one url (long one) to other url (short one), and then, when
using a short url, Shortened Url Services redirect the call to the mapped url, e.g:

1. start servervices -  mvn jetty:run
2. map http://ixkormachev-multi-war-test.blogspot.ru/ TO http://[host]/xYswlE
3. browse http://[host]/xYswlE but it will be displayed http://ixkormachev-multi-war-test.blogspot.ru/
page.
4. http://[host]/help - to see services description
5. services can be accessed from different hosts, not only from localhost

NB: To map urls it is necessary to register client (accountId), and every redirection is counted and
goes to statistics of the corresponding client.
There is Basic Authentication Token header is used when register urls.

USED FRAMEWORKS AND TOOLS:
** jetty with org.eclipse.jetty.security.JDBCLoginService
** H2
** spring data rest
** spring security
** spring mock mvc

EXAMPLE SCENARIO

1. Open terminal and switch to this folder, then enter:
  mvn jetty:run

2. Test Account service
  Request:
    curl -X POST -H 'Content-Type: application/json' -d '{ "AccountId" : "myAccountId"}' http://localhost:8080/account
  Response:
  {
  "success" : true,
  "description" : "Your account is opened",
  "password" : "E49AgxzY"
  }

3. Test URL Register service
  Request:
    curl -X POST -H 'Content-Type: application/json' -H 'Authorization: Basic YWNjb3VudGlkMToxMjM0NTY3OA=='  -d '{"url": "http://ya.ru","redirectType":301}' http://localhost:8080/register
  Response:
  {
  "shortUrl" : "http://192.168.1.34:8080/xYswlE"
  }
  
  Repeat for another url:
  Request:
  curl -X POST -H 'Content-Type: application/json' -H 'Authorization: Basic YWNjb3VudGlkMToxMjM0NTY3OA=='  -d '{"url": "http://ixkormachev-multi-war-test.blogspot.ru","redirectType":301}' http://localhost:8080/register
  Response:
  {
    "shortUrl" : "http://192.168.1.34:8080/QKSGmDbA"
  }
  
  4. Test Statistic service
  Request:
  curl -v http://localhost:8080/statistic/accountid1
  Response:
  {
  "redirectStatistics" : {
    "http://ya.ru" : 0,
    "http://ixkormachev-multi-war-test.blogspot.ru/" : 0
  }
  }
  
  5. Redirect (check with curl and with browser)
  curl http://localhost:8080//xYswlE
  curl http://localhost:8080//xYswlE
  
  6. Statistic
  Request:
  curl -v http://localhost:8080/statistic/accountid1
  Response:
  {
  "redirectStatistics" : {
    "http://ya.ru" : 2,
    "http://ixkormachev-multi-war-test.blogspot.ru/" : 0
  }
  }
  
  7. Check help page with browser
  http://localhost:8080/help
  
  
  NB:
  1. Repeat all steps, accesing the services from another computer (not server computer)
  2. Some parts of urls and passwords are random generated values
  3. accountid1, accountid2, accountid3 are predefined users
  
  
  
  
  
  