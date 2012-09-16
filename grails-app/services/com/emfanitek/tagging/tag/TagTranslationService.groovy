package com.emfanitek.tagging.tag

import com.grailsrocks.taxonomy.Taxon
import com.emfanitek.tagging.semantics.SemanticLink


class TagTranslationService {
    def availableLocales
    def translationService
    def taxonomyService
    def taxonomyExtensionService

    static transactional = true

    void translateTag(obj, Locale referenceLocale, String tag) {
        SemanticLink semanticLink = findSemanticLink(referenceLocale, tag)
        translateTag(semanticLink)

        reuseAllTagsOfSemanticLink(semanticLink, obj)
    }

    void translateAllTags(obj) {
        obj.taxonomies.each {Taxon t ->
            def tagRef = findSemanticLink(t)
            translateTag(obj, tagRef.localeObj, tagRef.tag)
        }
    }

    void reuseAllTagsOfSemanticLink(SemanticLink semanticLink, obj) {
        copyTags(semanticLink, obj)
    }

    private void translateTag(SemanticLink semanticLink) {
        Collection targetLocales = findMissingLocales(semanticLink)
        targetLocales.each {Locale target ->
            String translated = translationService.translate(semanticLink.tag, semanticLink.localeObj, target)
            if (translated != null) {
                semanticLink.addToTaxonomy(taxotests.taxonomy.TaxonHelper.taxonPath(target, translated))
            }
        }
    }

    public <T> Collection<T> findAllObjectsByTags(Class<T> objClass, Locale locale, Collection<String> tags) {
        def taxonIdList = tags.collect {t ->
            String localeAsString = locale.toString()
            taxonomyService.resolveTaxon(taxotests.taxonomy.TaxonHelper.taxonPath(localeAsString, t))?.id
        }

        taxonomyExtensionService.getObjectsForTaxonIds(objClass, taxonIdList, [:])
    }

    SemanticLink createSemanticLink(Locale locale, String tag) {
        def semanticLink = new SemanticLink(locale: locale.toString(), tag: tag).save()
        semanticLink.addToTaxonomy(taxotests.taxonomy.TaxonHelper.taxonPath(locale, tag))
        semanticLink
    }

    void copyTags(srcDomainClass, destDomainClass) {
        srcDomainClass.taxonomies.each {taxo ->
            destDomainClass.addToTaxonomy(taxo)
        }
    }

    SemanticLink findSemanticLink(Locale referenceLocale, String tag) {
        def taxon = taxonomyService.resolveTaxon(taxotests.taxonomy.TaxonHelper.taxonPath(referenceLocale, tag))
        if (taxon == null) {
            null
        } else {
            findSemanticLink(taxon)
        }
    }

    private SemanticLink findSemanticLink(Taxon taxon) {
        List relatedSemanticLinks = taxonomyService.findObjectsByTaxon(SemanticLink, taxotests.taxonomy.TaxonHelper.taxonPath(taxon.parent.name, taxon.name))
        if (relatedSemanticLinks.empty) {
            null
        } else {
            assert relatedSemanticLinks.size() == 1
            relatedSemanticLinks[0]
        }
    }

    private Collection findMissingLocales(SemanticLink semanticLink) {
        def existingTranslations = semanticLink.taxonomies
        def translatedLocales = findTranslatedLocales(existingTranslations)
        def targetLocales = findTargetLocales(translatedLocales)
        targetLocales
    }

    private Set<String> findTranslatedLocales(existingTranslations) {
        existingTranslations.collect {Taxon t -> t.parent.name} as Set
    }

    private Collection findTargetLocales(translatedLocales) {
        availableLocales.findAll {Locale l ->
            !(l.toString() in translatedLocales)
        }
    }
}
