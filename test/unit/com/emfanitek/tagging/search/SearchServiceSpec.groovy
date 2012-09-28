package com.emfanitek.tagging.search

import com.emfanitek.tagging.semantics.SemanticLink
import com.grailsrocks.taxonomy.Taxon
import com.grailsrocks.taxonomy.TaxonLink
import com.grailsrocks.taxonomy.Taxonomy
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

import static java.util.Locale.FRANCE
import static java.util.Locale.UK
import com.emfanitek.tagging.tests.di.Initializer

import com.emfanitek.tagging.tests.specification.SpecificationSupport
import com.emfanitek.tagging.tests.TestDomainClass2
import com.emfanitek.tagging.tests.TestDomainClass1

@TestMixin(GrailsUnitTestMixin)
@Mock([TestDomainClass1, TestDomainClass2, SemanticLink, Taxon, Taxonomy, TaxonLink])
class SearchServiceSpec extends SpecificationSupport {
    SearchService searchService
    def taggingService

    def setup() {
        o1 = new TestDomainClass1(name: 'o1').save()
        o2 = new TestDomainClass1(name: 'o2').save()

        new Initializer().initialize(
            ['searchService', 'taggingService'],
            this
        )
    }

    def 'you can find any tagged object by searching by tag'() {
        setup:
        taggingService.tag(o1, locale, tag1)

        when:
        Collection foundObjectsOfClass1ForTag1 = searchService.findAllByTag(o1.getClass(), locale, tag1)

        then:
        setsAreEqual([o1], foundObjectsOfClass1ForTag1)

        where:
        locale | tag1
        UK     | T1
        FRANCE | T2
    }

    //Bug in taxonomy plugin
    //see http://jira.grails.org/browse/GPTAXONOMY-6
    def 'you will not find an untagged object'() {
        expect:
        searchService.findAllByTag(o1.getClass(), UK, 'randomTag').empty

/*
        expect:
        searchService.findAllByTag(o1.getClass(), UK, 'randomTag').empty
        then:
        thrown(NullPointerException)
*/
    }

    def 'you will only find matching tagged objects of same class by searching by tag'() {
        setup:
        def o3 = new TestDomainClass2(name: 'c2_o3').save()
        def o4 = new TestDomainClass2(name: 'c2_o4').save()
        taggingService.tag(o1, locale, tag1)
        taggingService.tag(o2, locale, tag2)
        taggingService.tag(o3, locale, tag1)
        taggingService.tag(o3, locale, tag2)
        taggingService.tag(o4, locale, tag2)

        def class1 = o1.getClass()
        def class2 = o3.getClass()

        when:
        Collection foundObjectsOfClass1ForTag1 = searchService.findAllByTag(class1, locale, tag1)
        Collection foundObjectsOfClass1ForTag2 = searchService.findAllByTag(class1, locale, tag2)
        Collection foundObjectsOfClass2ForTag1 = searchService.findAllByTag(class2, locale, tag1)
        Collection foundObjectsOfClass2ForTag2 = searchService.findAllByTag(class2, locale, tag2)

        then:
        setsAreEqual([o1], foundObjectsOfClass1ForTag1)
        setsAreEqual([o2], foundObjectsOfClass1ForTag2)
        setsAreEqual([o3], foundObjectsOfClass2ForTag1)
        setsAreEqual([o3, o4], foundObjectsOfClass2ForTag2)

        where:
        locale | tag1 | tag2
        UK     | T1   | T2
        FRANCE | T2   | T1
    }

    def "you will find an object whose tags have been translated, by searching for a tag's translation"() {
        setup:
        taggingService.tagAndTranslate(o1, srcLocale, [t1])

        when:
        Collection foundObjects = searchService.findAllByTag(o1.getClass(), dstLocale, t1_trans)

        then:
        setsAreEqual(foundObjects, [o1])

        where:
        t1      | t1_trans | srcLocale | dstLocale
        'Hello' | 'Salut'  | UK        | FRANCE
    }

    def 'you will find several objects by a translated tag'() {
        setup:
        [o1, o2].each {o ->
            taggingService.tagAndTranslate(o, locale, [tag])
        }
        def o3 = new TestDomainClass1(name: 'c1_o3').save()
        taggingService.tagAndTranslate(o3, locale, [irrelevantTag])

        when:
        Collection allObjs = searchService.findAllByTag(o1.getClass(), translationLocale, translatedTag)
        assert allObjs.size() == 2

        then:
        setsAreEqual([o1, o2], allObjs)

        where:
        locale | tag     | translationLocale | translatedTag | irrelevantTag
        UK     | 'Hello' | FRANCE            | 'Salut'       | 'World'
        UK     | 'Hello' | SPAIN             | 'Hola'        | 'World'
        UK     | 'World' | SPAIN             | 'Mundo'       | 'Hello'
    }

    def 'you will find objects matching any of the tags you specify in the search, including non-existent tags'() {
        setup:
        def o3 = new TestDomainClass1(name: 'c1_o3').save()
        taggingService.tag(o1, locale, t1)
        taggingService.tag(o1, locale, t2)
        taggingService.tag(o2, locale, t2)
        taggingService.tag(o3, locale, t3)

        when:
        def found = searchService.findAllByTagDisjunction(o1.getClass(), locale, [t1, t2, t4])

        then:
        setsAreEqual(found, [o2, o1])
        assert found.size() == 2

        where:
        locale | t1 | t2 | t3 | t4
        UK     | T1 | T2 | T3 | T4
    }

    def 'searching for non-existent tags will return an empty Collection'() {
        setup:
        taggingService.tag(o1, locale, t1)

        when:
        def found = searchService.findAllByTagDisjunction(o1.getClass(), locale, [t2, t3])

        then:
        assert found.size() == 0

        where:
        locale | t1 | t2 | t3
        UK     | T1 | T2 | T3
    }
}
