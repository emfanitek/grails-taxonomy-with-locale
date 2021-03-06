package com.emfanitek.tagging.instrumentation

import com.emfanitek.tagging.semantics.SemanticLink
import com.emfanitek.tagging.tests.TestDomainClass2
import com.emfanitek.tagging.tests.specification.SpecificationSupport
import com.emfanitek.tagging.tag.TaggingService
import com.emfanitek.tagging.search.SearchService
import com.emfanitek.tagging.tests.TestDomainClass1
import com.grailsrocks.taxonomy.Taxon
import com.grailsrocks.taxonomy.TaxonLink
import com.grailsrocks.taxonomy.Taxonomy
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

/**
 * lmuniz (9/15/12 8:30 PM)
 */
@TestMixin(GrailsUnitTestMixin)
@Mock([TestDomainClass1, TestDomainClass2, SemanticLink, Taxon, Taxonomy, TaxonLink])
class DomainClassInstrumentationSpec extends SpecificationSupport {
    def mockTaggingService
    def mockSearchService

    def o

    def setup() {
        mockTaggingService = Mock(TaggingService)
        mockSearchService = Mock(SearchService)

        DomainClassInstrumentation domainClassInstrumentation = new DomainClassInstrumentation(
            taggingService: mockTaggingService,
            searchService: mockSearchService
        )
        domainClassInstrumentation.instrument(TestDomainClass1)

        o = new TestDomainClass1()
    }

    def 'tagWithLocale MetaMethod is created in a Domain class'() {
        when:
        o.tagWithLocale(SPAIN, [T1])

        then:
        1 * mockTaggingService.tagAndTranslate(o, SPAIN, [T1])
        0 * _._ // no (more) method call on any mock
    }

    def 'findAllByTag MetaMethod is created in a Domain class'() {
        when:
        TestDomainClass1.findAllByTag(SPAIN, T1)

        then:
        1 * mockSearchService.findAllByTag(TestDomainClass1, SPAIN, T1)
        0 * _._ // no (more) method call on any mock
    }

    def 'findAllByTags MetaMethod is created in a Domain class'() {
        when:
        TestDomainClass1.findAllByTags(SPAIN, [T1])

        then:
        1 * mockSearchService.findAllByTagDisjunction(TestDomainClass1, SPAIN, [T1])
        0 * _._ // no (more) method call on any mock
    }
}
