package com.emfanitek.tagging.taxonomy

/**
 * lmuniz (9/15/12 3:27 PM)
 */
class TaxonHelper {
    static Collection<String> taxonPath(Locale locale, String tag) {
        ['by_locale', locale.toString(), tag]
    }
    static Collection<String> taxonPath(String locale, String tag) {
        ['by_locale', locale, tag]
    }
}
