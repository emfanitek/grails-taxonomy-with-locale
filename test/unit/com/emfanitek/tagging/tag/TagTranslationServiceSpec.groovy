package com.emfanitek.tagging.tag

import static java.util.Locale.*

import com.emfanitek.tagging.tests.di.Initializer
import com.emfanitek.tagging.tests.specification.SpecificationSupport
import com.emfanitek.tagging.semantics.SemanticLink

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
class TagTranslationServiceSpec extends SpecificationSupport {
    final static String T1_GERMAN = 'Hallo'
    final static String T1_UK = 'Hello'
    final static String T2_UK = 'World'
    final static String T1_FR = 'Salut'
    final static String T2_FR = 'Monde'
    final static String T1_ES = 'Hola'
    final static String T2_ES = 'Mundo'
    final static Locale SPAIN = new Locale('es', 'ES')

    def tagTranslationService
    def taggingService

    void setup() {
        new Initializer().initialize(
            ['tagTranslationService','taggingService'],
            this
        )
    }

    def 'translates a tag to available locales and adds the translation to the object'() {
        setup:
        taggingService.tag(o1, UK, T1_UK)

        when:
        tagTranslationService.translateTag(o1, UK, T1_UK)
        def ref = SemanticLink.findByLocaleAndTag(UK.toString(), T1_UK)

        then:
        def expected = [T1_UK, T1_ES, T1_FR] as Set
        allTags(ref) == expected
        allTags(o1) == expected
    }

    def 'translates all tags to available locales'() {
        setup:
        taggingService.tag(o1, UK, T1_UK)
        taggingService.tag(o1, UK, T2_UK)

        when:
        tagTranslationService.translateAllTags(o1)

        then:
        setsAreEqual(
            [
                T1_UK, T1_ES, T1_FR,
                T2_UK, T2_ES, T2_FR
            ],
            allTags(o1)
        )
    }

    def 'translation attempt of unknown tag fails'() {
        setup:
        taggingService.tag(o1, GERMANY, T1_GERMAN)

        when:
        tagTranslationService.translateTag(o1, GERMANY, T1_GERMAN)

        then:
        setsAreEqual(allTags(o1), [T1_GERMAN])
    }

    def 'translating reuses previously created tag references taxonomies'() {
        setup: 'if a book b1 has already been tagged and translated'
        taggingService.tag(o1, UK, T1_UK)
        taggingService.tag(o1, UK, T2_UK)
        tagTranslationService.translateAllTags(o1)

        when: 'and a second book o2 is tagged with a tag that has been translated for b1'
        taggingService.tag(o2, FRANCE, T1_FR)

        then: 'all relevant translated tags from b1 are applied to o2 without asking to translate'
        setsAreEqual(allTags(o2), [T1_ES, T1_FR, T1_UK])
        //only one tag reference exists that contains translated tags of the translation tree of T1
        //even if the translation process has been called twice
        SemanticLink.findAllByTaxonomyExact([BY_LOCALE, SPAIN, T1_ES]).size() == 1
    }

    def 'translating with the wrong reference locale results in failure'() {
        setup:
        taggingService.tag(o1, UK, T1_UK)

        when:
        tagTranslationService.translateTag(o1, FRANCE, T1_FR)

        then:
        thrown(NullPointerException)
    }
}
