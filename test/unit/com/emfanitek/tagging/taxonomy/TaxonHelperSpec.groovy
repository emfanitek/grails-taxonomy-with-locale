package com.emfanitek.tagging.taxonomy

import com.emfanitek.tagging.tests.specification.SpecificationSupport

/**
 * lmuniz (9/15/12 8:25 PM)
 */
class TaxonHelperSpec extends SpecificationSupport {
    def 'builds correct taxon path from Locale'() {
        expect:
        [BY_LOCALE, SPAIN.toString(), T1] == TaxonHelper.taxonPath(SPAIN, T1)
    }

    def 'builds correct taxon path from Locale as String'() {
        expect:
        [BY_LOCALE, SPAIN.toString(), T1] == TaxonHelper.taxonPath(SPAIN.toString(), T1)
    }
}
