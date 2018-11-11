# TP_webService

API Data : https://h2-phpmyadmin.infomaniak.ch/MySQLAdmin/?pma_servername=psqt.myd.infomaniak.com&pma_username=psqt_

Rick and Morty Trading Card Game.

WebService : PHP or Node JS

Funtion :
Trade and Collect cards from all Rick and Morty Universe !! wubba lubba dub dub !!

more functionality are coming soon

WebService :<br/>
https://api-rickandmorty-tcg.herokuapp.com (root)<br/>
USERS :<br/>
/users/getAllUsers<br/>
/users/getUserById{id}<br/>

/users/userConnection<br/>
key : user_email (truc@test.fr) user_password (truc123)<br/>
/users/userConnect<br/>
key : userName, userEmail, userPassword<br/>

CARDS :<br/>
/cards/getAll<br/>
/cards/randomDeckGenerator?user_id={1}<br/>
Return JSON with 10 cards selected randomly AND add or update in table deck card_id with user_id and amount of this card for this player
