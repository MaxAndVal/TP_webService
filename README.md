# TP_webService

API Data : https://h2-phpmyadmin.infomaniak.ch/MySQLAdmin/?pma_servername=psqt.myd.infomaniak.com&pma_username=psqt_

Rick and Morty Trading Card Game.

WebService : PHP or Node JS

Funtion :
Trade and Collect cards from all Rick and Morty Universe !! wubba lubba dub dub !!

more functionality are coming soon

WebService :<br/>
https://api-rickandmorty-tcg.herokuapp.com (home page)<br/>
USERS :<br/>
https://api-rickandmorty-tcg.herokuapp.com/users/getAllUsers<br/>
https://api-rickandmorty-tcg.herokuapp.com/users/getUserById{id}<br/>
CARDS :<br/>
https://api-rickandmorty-tcg.herokuapp.com/cards/getAll<br/>
https://api-rickandmorty-tcg.herokuapp.com/cards/randomDeckGenerator?user_id={1}<br/>
Return JSON with 10 cards selected randomly AND add or update in table deck card_id with user_id and amount of this card for this player
