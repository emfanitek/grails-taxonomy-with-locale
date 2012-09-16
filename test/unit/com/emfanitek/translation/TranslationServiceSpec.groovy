package com.emfanitek.translation

import spock.lang.Unroll

import static java.util.Locale.FRANCE
import static java.util.Locale.UK
import com.emfanitek.tagging.tests.specification.SpecificationSupport
import com.emfanitek.tagging.tests.di.Initializer
import com.emfanitek.translation.TranslationService

class TranslationServiceSpec extends SpecificationSupport {

    TranslationService translationService
    TranslationService mockTranslationService

    def setup() {
        new Initializer().initialize(
            ['mockTranslationService'],
            this
        )
        translationService = mockTranslationService
    }

    @Unroll("The translation of '#phrase' from #l1 to #l2 is '#translation'")
    def 'translates a phrase from and to various locales'() {
        expect:
        translation == translationService.translate(phrase, l1, l2)

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
        translationService.translate('Hooooo', UK, FRANCE) == null
    }
}
