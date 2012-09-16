package com.emfanitek.tagging.tests.di

import com.grailsrocks.taxonomy.TaxonomyService

import static grails.test.MockUtils.mockLogging
import static java.util.Locale.FRANCE
import static java.util.Locale.UK
import com.emfanitek.translation.MockTranslationService
import com.emfanitek.tagging.taxonomy.TaxonomyExtensionService
import com.emfanitek.tagging.tag.TagTranslationService
import com.emfanitek.tagging.tag.TaggingService
import com.emfanitek.tagging.tests.taxonomy.TaxonomyInstrumentation
import com.emfanitek.tagging.search.SearchService
import com.emfanitek.tagging.instrumentation.DomainClassInstrumentation
import com.emfanitek.tagging.tests.TestDomainClass2
import com.emfanitek.tagging.semantics.SemanticLink
import com.emfanitek.tagging.tests.TestDomainClass1

/**
 * lmuniz (9/13/12 12:39 AM)
 */
class Initializer {
    final static Locale SPAIN = new Locale('es', 'ES')

    def taxonomyService
    def taxonomyHelper

    def tagTranslationService
    def taggingService
    def mockTranslationService
    def searchService
    def taxonomyExtensionService
    def domainClassInstrumentator

    void initialize(List props, cut) {
        mockLogging(TaxonomyService)

        mockTranslationService = new MockTranslationService()
        mockTranslationService.init()

        taxonomyService = new TaxonomyService()
        taxonomyService.init()

        taxonomyExtensionService = new TaxonomyExtensionService(taxonomyService: taxonomyService)

        tagTranslationService = new TagTranslationService(
            availableLocales: [FRANCE, SPAIN, UK],
            translationService: mockTranslationService,
            taxonomyService: taxonomyService ,
            taxonomyExtensionService:taxonomyExtensionService
        )

        taggingService = new TaggingService(
            tagTranslationService: tagTranslationService
        )

        taxonomyHelper = new TaxonomyInstrumentation(taxonomyService)
        taxonomyHelper.instrumentTaxonomyMethods([TestDomainClass1, TestDomainClass2, SemanticLink])


        searchService = new SearchService(
            tagTranslationService: tagTranslationService,
            taxonomyService:taxonomyService
        )

        domainClassInstrumentator=new DomainClassInstrumentation(
            taggingService: taggingService,
            searchService: searchService
        )

        props.each {String prop ->
            cut[prop] = this."${prop}"
        }
    }
}
