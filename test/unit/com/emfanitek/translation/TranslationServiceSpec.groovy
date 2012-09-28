package com.emfanitek.translation

import com.emfanitek.tagging.semantics.SemanticLink
import com.emfanitek.tagging.tests.TestDomainClass1
import com.emfanitek.tagging.tests.TestDomainClass2
import com.emfanitek.tagging.tests.di.Initializer
import com.emfanitek.tagging.tests.specification.SpecificationSupport
import com.grailsrocks.taxonomy.Taxon
import com.grailsrocks.taxonomy.TaxonLink
import com.grailsrocks.taxonomy.Taxonomy
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Unroll

import static java.util.Locale.FRANCE
import static java.util.Locale.UK

@TestMixin(GrailsUnitTestMixin)
@Mock([TestDomainClass1, TestDomainClass2, SemanticLink, Taxon, Taxonomy, TaxonLink])
class TranslationServiceSpec extends SpecificationSupport {

    Translator translator
    Translator mockTranslationService

    def setup() {
        new Initializer().initialize(
            ['mockTranslationService'],
            this
        )
        translator = mockTranslationService
    }

    @Unroll("The translation of '#phrase' from #l1 to #l2 is '#translation'")
    def 'translates a phrase from and to various locales'() {
        expect:
        translation == translator.translate(phrase, l1, l2)

        where:
        phrase  | l1    | l2     | translation
        'Hello' | UK    | FRANCE | 'Salut'
        'Hello' | UK    | SPAIN  | 'Hola'
        'World' | UK    | SPAIN  | 'Mundo'
        'Hola'  | SPAIN | UK     | 'Hello'
        'Hola'  | SPAIN | FRANCE | 'Salut'
    }

    def 'unknown translation returns null'() {
        expect:
        translator.translate('Hooooo', UK, FRANCE) == null
    }
}
