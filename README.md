QUICK START with SHORTENED URL SERVICES

SHORTENED URL SERVICES allow to map one url (long one) to other url (short one), and then, when
using a short url, Shortened Url Services redirect the call to the mapped url, e.g:

1. start servervices -  java -jar target\ix-shorten-url-0.0.1-SNAPSHOT.jar

2. try to register already registered default user:

curl -H "Content-Type: application/json"  -X POST -d '{"id":null,"email":"test2@test.com","password":"password2","nam
e":"user2","lastName":"lastName2","active":1,"roles":["ADMIN"]}' http://localhost:8080/register/user
{
  "result" : "There is already a user registered with the email provided"
}

3. try to register a new user

curl -H "Content-Type: application/json"  -X POST -d '{"id":null,"email":"test3@test.com","password":"password2","nam
e":"user2","lastName":"lastName2","active":1,"roles":["ADMIN"]}' http://localhost:8080/register/user
{
  "result" : "The user has been registrated"
}

4. add an url and get a shortcut for it
$ curl -H "Content-Type: application/json" -H "Authorization: Basic dGVzdDJAdGVzdC5jb206cGFzc3dvcmQy" -X POST -d '{"url
":"http://ya.ru"}' http://localhost:8080/register/url
{
  "result" : "[host]:8080/IbiKHAVA"
}
  