package com.emfanitek.tagging.tag

import com.emfanitek.tagging.tests.TestDomainClass1
import com.emfanitek.tagging.tests.TestDomainClass2
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
import com.emfanitek.tagging.semantics.SemanticLink

@TestMixin(GrailsUnitTestMixin)
@Mock([TestDomainClass1, TestDomainClass2, SemanticLink, Taxon, Taxonomy, TaxonLink])
class TaggingServiceSpec extends SpecificationSupport {
    def taggingService


    def setup() {
        o1 = new TestDomainClass1(name: 'o1').save()
        o2 = new TestDomainClass1(name: 'o2').save()

        new Initializer().initialize(
            ['taggingService'],
            this
        )
    }

    def 'When an object is tagged, it is kept as a reference tag'() {
        when:
        taggingService.tag(o1, FRANCE, T1)

        then:
        SemanticLink.findByLocaleAndTag(FRANCE.toString(), T1)?.tag == T1
    }

    def 'You can tag and translate an object at the same time, with multiple tags'() {
        when:
        taggingService.tagAndTranslate(o1, locale1, [t1, t2])

        then:
        setsAreEqual(allTags(o1), [
            t1, t2,
            t1_trans1, t2_trans1,
            t1_trans2, t2_trans2
        ])

        where:
        locale1 | locale2 | locale3 | t1      | t2      | t1_trans1 | t2_trans1 | t1_trans2 | t2_trans2
        FRANCE  | UK      | SPAIN   | 'Salut' | 'Monde' | 'Hello'   | 'World'   | 'Hola'    | 'Mundo'
    }

    def 'When an object is tagged twice with the same tag, it is referenced only once'() {
        when:
        taggingService.tag(o1, FRANCE, T1)
        taggingService.tag(o1, FRANCE, T1)

        then:
        SemanticLink.findAllByLocaleAndTag(FRANCE.toString(), T1).size() == 1
    }

    def 'object is tagged and reference is tagged'() {
        when:
        taggingService.tag(o1, FRANCE, T1)
        def ref = SemanticLink.findByLocaleAndTag(FRANCE.toString(), T1)

        then:
        hasExactlyOneTag(o1, FRANCE.toString(), T1)
        hasExactlyOneTag(ref, FRANCE.toString(), T1)
    }

    def 'object can be tagged with multiple tags'() {
        when:
        taggingService.tag(o1, FRANCE, T1)
        taggingService.tag(o1, FRANCE, T2)
        def ref1 = SemanticLink.findByLocaleAndTag(FRANCE.toString(), T1)
        def ref2 = SemanticLink.findByLocaleAndTag(FRANCE.toString(), T2)

        then:
        ref1 != null
        ref2 != null

        [T1, T2].every {tag ->
            o1.hasTaxonomy([BY_LOCALE, 'fr_FR', tag])
        }

        hasExactlyOneTag(ref1, 'fr_FR', T1)
        hasExactlyOneTag(ref2, 'fr_FR', T2)
    }

    def 'you can retrieve all the tags added to an object'() {
        when:
        taggingService.tag(o1, locale1, t1)
        taggingService.tag(o1, locale1, t2)
        taggingService.tag(o1, locale2, t3)

        def allTags = taggingService.getAllTags(o1)

        then:
        setsAreEqual(allTags, [t1, t2, t3])
        assert allTags.size() == 3

        where:
        t1 | t2 | t3 | locale1 | locale2
        T1 | T2 | T3 | FRANCE  | UK
    }

    def 'there can be multiple instances of a tag name if they have been added in different locales'() {
        when:
        taggingService.tag(o1, locale1, t1)
        taggingService.tag(o1, locale1, t2)
        taggingService.tag(o1, locale2, t2)

        def allTags = taggingService.getAllTags(o1)

        then:
        setsAreEqual(allTags, [t1, t2, t2])
        assert allTags.size() == 3
        assert allTags.findAll {it == t2}.size() == 2

        where:
        t1 | t2 | locale1 | locale2
        T1 | T2 | FRANCE  | UK
    }

    def 'you can retrieve all the tags in a single locale'() {
        when:
        taggingService.tag(o1, locale1, t1)
        taggingService.tag(o1, locale1, t2)
        taggingService.tag(o1, locale2, t2)

        def allTagsInL1 = taggingService.getAllTags(o1, locale1)
        def allTagsInL2 = taggingService.getAllTags(o1, locale2)

        then:
        setsAreEqual(allTagsInL1, [t1, t2])
        setsAreEqual(allTagsInL2, [t2])
        assert allTagsInL1.size() == 2
        assert allTagsInL2.size() == 1

        where:
        t1 | t2 | locale1 | locale2
        T1 | T2 | FRANCE  | UK
    }
}
