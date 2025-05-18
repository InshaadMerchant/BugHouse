package com.example.bughouse.DTO

import com.google.gson.annotations.SerializedName

data class GraphMSALResponse(
    @SerializedName("@odata.context") val odataContext: String,
    val businessPhones: List<String>,
    val displayName: String,
    val givenName: String,
    val jobTitle: String?,
    val mail: String?,
    val mobilePhone: String?,
    val officeLocation: String?,
    val preferredLanguage: String?,
    val surname: String,
    val userPrincipalName: String,
    val id: String
)
