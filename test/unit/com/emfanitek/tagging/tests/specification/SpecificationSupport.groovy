package com.emfanitek.tagging.tests.specification

import spock.lang.Specification
import grails.test.mixin.Mock
import com.grailsrocks.taxonomy.Taxon
import com.grailsrocks.taxonomy.Taxonomy
import com.grailsrocks.taxonomy.TaxonLink
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.SimpleLayout
import com.emfanitek.tagging.tests.TestDomainClass1
import com.emfanitek.tagging.tests.TestDomainClass2
import com.emfanitek.tagging.semantics.SemanticLink

/**
 * lmuniz (9/14/12 11:50 AM)
 */
abstract class SpecificationSupport extends Specification {
    protected static final String BY_LOCALE = 'by_locale'
    protected o1
    protected o2
    protected static final T1 = 'T1'
    protected static final T2 = 'T2'
    protected static final T3 = 'T3'
    protected static final T4 = 'T4'
    protected static Locale SPAIN = new Locale('es', 'ES')

    def setup() {
        def logger = Logger.getRootLogger()
        logger.setLevel(Level.INFO)
        def appender=new ConsoleAppender(new SimpleLayout())
        logger.addAppender(appender)

    }

    protected Set allTags(obj) {
        obj.taxonomies.collect {it.name} as Set
    }

    protected setsAreEqual(Collection expected, Collection actual) {
        actual as Set == expected as Set
    }

    protected hasExactlyOneTag(obj, String locale, String tag) {
        obj.hasTaxonomy([BY_LOCALE, locale, tag])
        obj.taxonomies.size() == 1
    }
}
