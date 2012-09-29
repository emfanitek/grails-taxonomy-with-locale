import com.emfanitek.tagging.instrumentation.DomainClassInstrumentation
import org.codehaus.groovy.grails.commons.GrailsApplication

class TaxonomyWithLocaleGrailsPlugin {
    def groupId='com.emfanitek'
    // the plugin version
    def version = "0.1-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/*",
        "grails-app/controllers/*",
        "grails-app/domain/com/emfanitek/tagging/tests/*",
        "grails-app/services/com/emfanitek/translation/MockTranslationService.groovy",
        "test/*"
    ]

    def loadAfter = ['taxonomy']

    // TODO Fill in these fields
    def title = "Taxonomy With Locale Plugin"
    def author = "Luis Muniz"
    def authorEmail = "luis@emfanitek.com"
    def description = """
Allows to automatically translate tags added to domain objects to the configured set of locales.

This plugin is based on Marc Palmer's excellent taxonomy plugin, and adds an internationalization layer on top of it.

It adds three methods to domain classes:

tagWithLocale(Locale,String) adds an internationalized tag to a domain object and automatically translates it to a list of configured locales
static findAllByTag(Locale,String) returns a list of domain objects of the given class that match the given internationalized tag
static findAllByTags(Locale,Collection<String>) returns a list of domain objects of the given class that match any of the the given internationalized tag

The plugin uses a TranslationService to translate phrases from a source locale to a target locale.

Currently, the default implementation, which can be overridden by providing a suitable Translator in the spring context, is a GoogleTranslationService (needs a paying Google Translate API key)

Following Config entries are currently needed:

taxonomy {
    i18n {
        //only needed if youuse the default translation service
        googleTranslateAPIKey='xxxxx'
        availableLocales=[
            Locale.FRANCE,
            new Locale('fr','BE')
        ]
    }
}
"""

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/taxonomy-with-locale"

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    def organization = [name: "Emfanitek", url: "http://www.emfanitek.com/"]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
    }

    def doWithSpring = {
        //default translation service points to google translate
        springConfig.addAlias "translationService", "googleTranslationService"
    }

    def doWithDynamicMethods = { ctx ->
        applyDynamicMethods(application)
        applyPluginConfigurationOptions(application)
    }

    def doWithApplicationContext = { applicationContext ->
    }

    def onChange = { event ->
        applyDynamicMethods(application)
    }

    def onConfigChange = { event ->
        applyPluginConfigurationOptions(application)
    }

    def onShutdown = { event ->
    }

    void applyDynamicMethods(GrailsApplication application) {
        def ctx = application.mainContext
        def taggingService = ctx.taggingService
        def searchService = ctx.searchService
        def instrumentation = new DomainClassInstrumentation(
            searchService: searchService,
            taggingService: taggingService
        )

        application.domainClasses*.clazz.each { c ->
            instrumentation.instrument(c)
        }
    }

    void applyPluginConfigurationOptions(GrailsApplication application) {
        def ctx = application.mainContext
        def cfg = application.config

        ctx.tagTranslationService.availableLocales = cfg.taxonomy.i18n.availableLocales
    }

}
