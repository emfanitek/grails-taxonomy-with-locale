package com.emfanitek.tagging.semantics

class SemanticLink {
    static taxonomy = true
    String tag
    String locale

    static constraints = {
        tag nullable: false, unique: ['locale']
        locale nullable: false
    }

    Locale getLocaleObj() {
        new Locale(*locale.split('_'))
    }
}
