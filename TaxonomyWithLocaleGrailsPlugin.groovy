import com.emfanitek.tagging.instrumentation.DomainClassInstrumentation

class TaxonomyWithLocaleGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/*",
        "grails-app/controllers/*",
        "grails-app/domain/com/emfanitek/i18n/taxonomy/test*",
        "test/*"
    ]

    // TODO Fill in these fields
    def title = "Taxonomy With Locale Plugin"
    def author = "Luis Muniz"
    def authorEmail = "luis@emfanitek.com"
    def description = """\
Allows to automatically translate tags added to domain objects to the configured set of locales.

This plugin is based on Marc Palmer's excellent taxonomy plugin, and adds an internationalization layer on top of it.

It adds three methods to domain classes:

tagWithLocale(Locale,String) adds an internationalized tag to a domain object and automatically translates it to a list of configured locales
static findAllByTag(Locale,String) returns a list of domain objects of the given class that match the given internationalized tag
static findAllByTags(Locale,Collection<String>) returns a list of domain objects of the given class that match any of the the given internationalized tag

The plugin uses a TranslationService to translate phrases from a source locale to a target locale.

Currently, the default implementation, which can be overridden by providing a suitable TranslationService in the spring context, is a GoogleTranslationService (needs a paying Google Translate API key)

Following Config entries are currently needed:

taxonomy {
    i18n {
        //only needed if youuse the default translation service
        googleTranslateAPIKey='xxxxx'
    }
}
"""

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/taxonomy-with-locale"

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    def organization = [ name: "Emfanitek", url: "http://www.emfanitek.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
    }

    def doWithSpring = {

        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        def instrumentation=new DomainClassInstrumentation(
            searchService: ctx.
        )
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
