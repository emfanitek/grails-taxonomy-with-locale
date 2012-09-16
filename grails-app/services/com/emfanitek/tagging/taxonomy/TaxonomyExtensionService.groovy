package com.emfanitek.tagging.taxonomy

import com.grailsrocks.taxonomy.TaxonLink
import com.grailsrocks.taxonomy.Taxon

/**
 * Contains extension to the core taxonomy plugin, that I wish would be in TaxonomyService itself.
 */
class TaxonomyExtensionService {
    def taxonomyService

    public <T> Collection<T> getObjectsForTaxons(Class<T> objClass, Collection<Taxon> taxonList, Map params) {
        getObjectsForTaxonIds(objClass, taxonList.collect {it.id}, params)
    }

    public <T> Collection<T> getObjectsForTaxonIds(Class<T> objClass, Collection<Long> taxonIdList, Map params) {
        if (taxonIdList) {
            Collection<Long> ids = TaxonLink.withCriteria {
                projections {
                    property('objectId')
                }

                taxon {
                    if (taxonIdList.size() == 1) {
                        eq('id', taxonIdList[0])
                    } else {
                        inList('id', taxonIdList)
                    }
                }

                eq('className', objClass.name)
            }
            // Bug in Grails 1.2M2, inList dies if id list is empty
            if (log.debugEnabled) {
                log.debug("getObjectIdsForTaxons found object ids $ids")
            }
            if (ids) {
                return objClass.findAllByIdInList(ids, params)
            }
        }
        return Collections.EMPTY_LIST
    }
}
