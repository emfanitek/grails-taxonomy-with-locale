package com.emfanitek.translation

import grails.plugins.rest.client.RestBuilder
import org.codehaus.groovy.grails.plugins.codecs.HTMLCodec

class GoogleTranslationService implements TranslationService {
    static transactional = false
    String googleKey = 'AIzaSyCeUlsaB1k3a5BDb7YDbSYK1ODxBub8CHE'

    String translate(String phrase, Locale srcLocale, Locale targetLocale) {
        RestBuilder rest = new RestBuilder()

        def response = rest.get("https://www.googleapis.com/language/translate/v2?key=${googleKey}&source=${srcLocale.language}&target=${targetLocale.language}&q=${HTMLCodec.encode(phrase)}")
        log.info("Response:${response.json}")
        response.json.data.translations.translatedText[0]
//        response
    }
}
