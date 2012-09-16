package com.emfanitek.tagging.tests.taxonomy

import com.grailsrocks.taxonomy.TaxonomyService
import com.grailsrocks.taxonomy.Taxon;

/**
 * lmuniz (9/9/12 5:39 PM)
 */
public class TaxonomyInstrumentation {
    TaxonomyService taxoService

    TaxonomyInstrumentation(TaxonomyService taxonomyService) {
        this.taxoService = taxonomyService
    }

    void instrumentTaxonomyMethods(List<Class> classes) {
        //copied from taxonomies plugin in TaxonomyGrailsPlugin.groovy
        classes.each {c->
            c.metaClass.'static'.findByTaxonomyFamily = { nodeOrPath, Map params = null ->
                if (!params) {
                    params = [max:1]
                } else {
                    params.max = 1
                }
                def o = taxoService.findObjectsByFamily(delegate, nodeOrPath, params)
                return o.size() ? o.get(0) : null
            }
            // family can include "taxonomy" arg, string/Taxonomy instance
            c.metaClass.'static'.findAllByTaxonomyFamily = { nodeOrPath, Map params = null ->
                taxoService.findObjectsByFamily(delegate, nodeOrPath, params)
            }
            // family can include "taxonomy" arg, string/Taxonomy instance
            c.metaClass.'static'.findByTaxonomyExact = { nodeOrPath, Map params = null ->
                if (!params) {
                    params = [max:1]
                } else {
                    params.max = 1
                }
                def o = taxoService.findObjectsByTaxon(delegate, nodeOrPath, params)
                return o.size() ? o.get(0) : null
            }
            // family can include "taxonomy" arg, string/Taxonomy instance
            c.metaClass.'static'.findAllByTaxonomyExact = { nodeOrPath, Map params = null ->
                taxoService.findObjectsByTaxon(delegate, nodeOrPath, params)
            }
            c.metaClass.addToTaxonomy = { nodeOrPath, taxonomy = null ->
                def link = taxoService.findLink(delegate, nodeOrPath, taxonomy)
                if (!link) {
                    if (!(nodeOrPath instanceof Taxon)) {
                        nodeOrPath = taxoService.createTaxonomyPath(nodeOrPath, taxonomy)
                    }
                    taxoService.saveNewLink(delegate, nodeOrPath)
                }
            }
            c.metaClass.clearTaxonomies = { ->
                taxoService.removeAllLinks(delegate)
            }
            c.metaClass.getTaxonomies = { ->
                taxoService.findAllLinks(delegate)*.taxon
            }
            c.metaClass.hasTaxonomy = { nodeOrPath, taxonomy = null ->
                taxoService.hasLink(delegate, nodeOrPath, taxonomy)
            }
            c.metaClass.removeTaxonomy = { nodeOrPath, taxonomy = null ->
                taxoService.removeLink(delegate, nodeOrPath, taxonomy)
            }
        }
    }


    def getTaxonomyMembers(List<String> path) {
        path.inject(taxonomies) {remainingTaxonomies, pathElement ->
            if (remainingTaxonomies == null) {
                null
            } else {
                remainingTaxonomies[pathElement]
            }
        }
    }

    List getTaxonomyFamilyMembers(List<String> path) {
        def taxonomies = getTaxonomyMembers(path)
        getDescendants(taxonomies)
    }

    private List getDescendants(taxonomies) {
        if (taxonomies == null) {
            return []
        } else if (taxonomies instanceof List) {
            taxonomies
        } else {
            taxonomies.collect {taxo, members ->
                getDescendants(members)
            }.flatten()
        }
    }

    List getDescendentTags(obj, List<String> path) {

    }
}
