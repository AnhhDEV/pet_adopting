package com.tanh.petadopt.util

import com.tanh.petadopt.R

object Util {

    //blob storage azure
    const val CONNECTION_STRING =""
    const val DEFAULT_WEB_CLIENT_ID = "856323978983-aa7m25o3rhbq38rfhdcoj6dl4fo6igeo.apps.googleusercontent.com"
    const val HOME = "Home"
    const val FAVORITE = "Favorite"
    const val ADD = "add"
    const val INBOX = "Inbox"
    const val PROFILE = "Profile"
    const val MY_POST = "mypost"
    const val LOG_IN = "login"
    const val DETAIL = "detail"
    const val MESSENGER = "messenger"

    const val ANIMALS_COLLECTION = "pets"
    const val USERS_COLLECTION = "users"
    const val PREFERENCES_COLLECTION = "favorite_pets"
    const val CHATS_COLLECTION = "chatrooms"
    const val MESSAGE_COLLECTION = "msglist"

    const val CAT_PAW_URL = "https://i.ibb.co/bFS61Pp/cat-removebg-preview.png"

    val categories = listOf(
        "Dogs" to R.drawable.dog to false,
        "Cats" to R.drawable.cat to false,
        "Birds" to R.drawable.bird to false,
        "Fish" to R.drawable.fish to false
    )

    val petCategory = listOf(
        "Dogs",
        "Cats",
        "Birds",
        "Fish"
    )

    val gender = listOf(
        "Male",
        "Female"
    )

}

