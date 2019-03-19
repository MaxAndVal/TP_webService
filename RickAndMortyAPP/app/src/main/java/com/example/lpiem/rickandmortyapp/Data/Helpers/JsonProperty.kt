package com.example.lpiem.rickandmortyapp.Data.Helpers

enum class JsonProperty(var dbField: String)  {
    UserEmail("user_email"),
    UserName("user_name"),
    UserID("user_id"),
    UserPassword("user_password"),
    UserImage("user_image"),
    ExternalID("external_id"),
    NewWallet("newWallet"),
    NewDate("newDate"),
    SessionToken("session_token"),
    CardName("card_name"),
    Price("price"),
    ListOfCard("listOfCards"),
    UserOldPassword("user_old_password"),
    UserNewPassword("user_new_password"),
    UserCode("user_code"),
    DeckNumber("deckNumber")
}

