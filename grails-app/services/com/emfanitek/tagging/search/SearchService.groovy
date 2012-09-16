package com.emfanitek.tagging.search

import static com.emfanitek.tagging.taxonomy.TaxonHelper.taxonPath

class SearchService {
    static transactional = false

    def tagTranslationService
    def taxonomyService

    //this is a workaround for http://jira.grails.org/browse/GPTAXONOMY-6
    public <T> Collection<T> findAllByTag(Class<T> objClass, Locale locale, String tag) {
        def taxon = taxonomyService.resolveTaxon(taxonPath(locale, tag))
        if (taxon != null) {
            taxonomyService.findObjectsByFamily(objClass, taxon)
        } else {
            Collections.EMPTY_LIST
        }
    }

    public <T> Collection<T> findAllByTagDisjunction(Class<T> objClass, Locale locale, Collection<String> tags) {
        tagTranslationService.findAllObjectsByTags(objClass, locale, tags)
    }
}
