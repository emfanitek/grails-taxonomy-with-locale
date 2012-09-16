package com.emfanitek.tagging.tag

import com.grailsrocks.taxonomy.Taxon

import static com.emfanitek.tagging.taxonomy.TaxonHelper.taxonPath

class TaggingService {
    def tagTranslationService

    void tag(obj, Locale locale, String tag) {
        def semanticLink = tagTranslationService.findSemanticLink(locale, tag)

        if (semanticLink == null) {
            tagObjectAndCreateSemanticLink(locale, tag, obj)
        } else {
            tagTranslationService.reuseAllTagsOfSemanticLink(semanticLink, obj)
        }
    }

    private void tagObjectAndCreateSemanticLink(Locale locale, String tag, obj) {
        def taxonPath = taxonPath(locale, tag)
        obj.addToTaxonomy(taxonPath)
        tagTranslationService.createSemanticLink(locale, tag)
    }

    void tagAndTranslate(obj, Locale locale, Collection<String> tags) {
        tags.each {tag(obj, locale, it)}
        tagTranslationService.translateAllTags(obj)
    }

    Collection<String> getAllTags(obj, Locale locale) {
        Closure matchingLocale = this.&matchesLocale.rcurry(locale.toString())
        getAllTagsInTaxonList(obj.taxonomies.findAll(matchingLocale))
    }

    Collection<String> getAllTags(obj) {
        getAllTagsInTaxonList(obj.taxonomies)
    }

    private Collection<String> getAllTagsInTaxonList(Collection<Taxon> taxons) {
        taxons.collect {Taxon t -> t.name}
    }

    private boolean matchesLocale(Taxon t, String localeAsString) {
        t.parent.name == localeAsString
    }
}
