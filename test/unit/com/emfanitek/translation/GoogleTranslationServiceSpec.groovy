package com.emfanitek.translation

import spock.lang.Ignore

import static grails.test.MockUtils.mockLogging

import com.emfanitek.tagging.tests.specification.SpecificationSupport
import com.emfanitek.translation.GoogleTranslationService

class GoogleTranslationServiceSpec extends SpecificationSupport {
    def translationService = new GoogleTranslationService()

    def setup() {
        mockLogging(GoogleTranslationService)
    }

    @Ignore
    def 'translation works'() {
        expect:
        ['chamber music,music,piano,lesson,piano lesson': 'música de cámara,música,piano,lección,lección de piano'].every {src, dest ->
            dest == src.split(',').collect {translationService.translate(it, Locale.UK, SPAIN)}.join(',')
        }
    }

}
