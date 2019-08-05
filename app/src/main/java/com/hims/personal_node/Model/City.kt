package com.hims.personal_node.Model

data class City(var city_no:Int, var city_name:String, var admin_no:Int){
    @Override
    override fun toString(): String {
        return city_name
    }
}