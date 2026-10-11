package com.hendy.cybertech.bot

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    var name: String,
    var balance: Long = 50000,
    val history: MutableList<String> = mutableListOf("Khởi tạo tài khoản (+50,000 VNĐ)"),
    val wonCodes: MutableList<String> = mutableListOf(),
    val linkedAccounts: MutableMap<String, MutableList<String>> = mutableMapOf(
        "SC88" to mutableListOf(), "C168" to mutableListOf(), "CM88" to mutableListOf(),
        "F8BET" to mutableListOf(), "ABCVIP" to mutableListOf(), "KJC_CU" to mutableListOf()
    ),
    val accountKho: MutableMap<String, MutableList<AccountItem>> = mutableMapOf(
        "RR88" to mutableListOf(), "MM88" to mutableListOf(), "GG88" to mutableListOf(),
        "U888" to mutableListOf(), "J88" to mutableListOf(), "88CLB" to mutableListOf(),
        "ABC8" to mutableListOf(), "XX8" to mutableListOf(), "ABCVIP" to mutableListOf(), "KJC_CU" to mutableListOf()
    )
)

@Serializable
data class AccountItem(val acc: String, val checked: Boolean = true)

@Serializable
data class UserState(val action: String, val brand: String? = null, val platform: String? = null)
