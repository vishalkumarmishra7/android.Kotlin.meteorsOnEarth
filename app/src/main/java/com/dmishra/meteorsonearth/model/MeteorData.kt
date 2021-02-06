package com.dmishra.meteorsonearth.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MeteorData : RealmObject() {
    @PrimaryKey
    var id: String? = null
    var mass = 0.0
    var name: String? = null
    var reclat: String? = null
    var reclong: String? = null
    var year: String? = null

}