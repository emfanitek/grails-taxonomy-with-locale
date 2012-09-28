package com.emfanitek.translation

import static java.util.Locale.UK as L_UK
import static java.util.Locale.FRANCE as L_FR

class MockTranslationService implements Translator {
    private static Locale L_SPAIN = new Locale('es', 'ES')
    private static String SPAIN = L_SPAIN.toString()
    private static String UK = L_UK.toString()
    private static String FRANCE = L_FR.toString()

    static transactional = false

    def z = [
        (UK): [
            'Hello': [
                (SPAIN): 'Hola',
                (FRANCE): 'Salut'
            ],
            'World': [
                (SPAIN): 'Mundo',
                (FRANCE): 'Monde'
            ]
        ]
    ]
    def translationTable = new ConfigObject()

    void init() {
        z.each {String locale1, phrases ->
            phrases.each {String phrase, translations ->
                translations.each {String locale2, String translation ->
                    translationTable[locale1][phrase][locale2] = translation
                    translationTable[locale2][translation][locale1] = phrase
                }
            }
        }
        z.each {String origLocale, phrases ->
            phrases.each {String phrase, translations ->
                translations.each {String locale1, String translation1 ->
                    translations.each {String locale2, String translation2 ->
                        if (locale1 != locale2) {
                            translationTable[locale1][translation1][locale2] = translation2
                        }
                    }
                }
            }
        }
    }

    String translate(String phrase, Locale srcLocale, Locale targetLocale) {
        def translated = translationTable[srcLocale.toString()][phrase][targetLocale.toString()]
        if (translated instanceof String) {
            translated
        } else {
            null
        }
    }

}
