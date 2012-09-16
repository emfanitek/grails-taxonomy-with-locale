package com.emfanitek.tagging.instrumentation

import com.emfanitek.tagging.tag.TaggingService
import com.emfanitek.tagging.search.SearchService

/**
 * lmuniz (9/15/12 3:06 PM)
 */
class DomainClassInstrumentation {
    TaggingService taggingService
    SearchService searchService

    void instrument(Class domainClass) {
        domainClass.metaClass.'static'.findAllByTag = {Locale locale, String tag ->
            searchService.findAllByTag(domainClass, locale, tag)
        }

        domainClass.metaClass.'static'.findAllByTags = {Locale locale, Collection<String> tags ->
            searchService.findAllByTagDisjunction(domainClass, locale, tags)
        }

        domainClass.metaClass.tagWithLocale = {Locale locale, Collection<String> tags ->
            taggingService.tagAndTranslate(delegate, locale, tags)
        }
    }
}
