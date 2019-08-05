package com.hims.personal_node.Model

data class Race( var race_key: Int,var race_name: String){
    override fun toString(): String {
        return race_name
    }
}