package com.emfanitek.tagging.tests

class TestDomainClass1 {
    String name
    static taxonomy = true

    static constraints = {
        name nullable: false, unique: true
    }

    String toString() {
        "$id:$name"
    }
}
