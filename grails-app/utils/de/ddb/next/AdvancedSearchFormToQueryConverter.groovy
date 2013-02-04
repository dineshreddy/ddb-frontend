package de.ddb.next

/**
 * Convert GET-Parameters from 
 * advancedsearch-request to search-query.
 * 
 * @author mih
 *
 */
public class AdvancedSearchFormToQueryConverter {
    //CharacterEncoding of query-String
    private String characterEncoding = "UTF-8"
    
    //Operator (AND/OR) to concatenate the groups
    private String groupOperatorName = "operator"

    //Operator (AND/OR) to concatenate the rows of one group
    private String rowOperatorName = "operator-group-"

    //Name of form-field that holds the facetname to search in(searchfield)
    private String facetName = "facet-"

    //Name of form-field that holds the searchstring
    private String valueName = "value-"

    //Name of form-field that holds the match (ALL/ANY/EXACT)
    private String matchName = "match-"
    private String exactName = "EXACT"
    private String andName = "ALL"
    private String orName = "ANY"

    //Type in facet-result from ddb-backend that indicates that a facet
    //is searchable by an enumeration
    //(select-box instead of textfield for searchstring in searchform)
    def enumSearchType = "ENUM"

    //suffix for facetname(searchfield name) to get values for facet.
    def facetNameSuffix = "_fct"

    //GET-Parameters from request
    def parameters
    
    //Number of search-groups in andvanced search form
    def searchGroupCount
        
    //Number of search-rows per group in andvanced search form
    def searchFieldCount
    
    //names of facet-searchfields. used to get name of selectbox containing facetted values.
    def facetSearchfields

    /**
     * Constructor with number of Groups and rows per group in searchform.
     * 
     * @param parameters GET-Parameters
     * @param searchGroupCount
     * @param searchFieldCount
     * @param searchfields searchfields with facet-names
     */
    public AdvancedSearchFormToQueryConverter(parameters, searchGroupCount, searchFieldCount, searchfields) {
        this.parameters = parameters
        this.searchGroupCount = searchGroupCount
        this.searchFieldCount = searchFieldCount
        this.facetSearchfields = [:]
        for ( searchfield in searchfields ) {
            if (searchfield.searchType.equals(enumSearchType)) {
                facetSearchfields[searchfield.name] = searchfield.name + facetNameSuffix
            }
        }
    }

    /**
     * Convert parameters to Query
     * 
     * @return String Query
     */
    public String convertFormParameters() {
        StringBuilder query = new StringBuilder()
        String groupOperator = parameters.get(groupOperatorName)
        if (groupOperator == null || groupOperator.isEmpty()) {
            return query
        }
        List<String> groupParts = new ArrayList<String>()
        for (int i = 0; i < searchGroupCount; i++) {
            StringBuilder groupQuery = convertGroup(i)
            if (groupQuery.length() > 0) {
                groupParts.add(groupQuery)
            }
        }
        if (groupParts.size() > 0) {
            for (String groupPart : groupParts) {
                if (query.length() > 0) {
                    query.append(" ").append(groupOperator).append(" ")
                }
                if (groupParts.size() > 1) {
                    query.append("(").append(groupPart).append(")")
                }
                else {
                    query.append(groupPart)
                }

            }
        }
        return URLEncoder.encode(query.toString(), characterEncoding);
    }

    /**
     * Convert group in searchform to (sub)query
     * 
     * @param groupId
     * @return (sub)query
     */
    private StringBuilder convertGroup(groupId) {
        StringBuilder groupQuery = new StringBuilder()
        String rowOperator = parameters.get(rowOperatorName + groupId)
        if (rowOperator == null || rowOperator.isEmpty()) {
            return groupQuery
        }
        for (int i = 0; i < searchFieldCount; i++) {
            StringBuilder rowQuery = convertRow(groupId, i)
            if (rowQuery.length() > 0) {
                if (groupQuery.length() > 0) {
                    groupQuery.append(" ").append(rowOperator).append(" ")
                }
                groupQuery.append(rowQuery)
            }
        }
        return groupQuery
    }

    /**
     * Convert row in searchform to (sub)query.
     * Decide where to get the searched value from (textfield or selectbox)
     * 
     * @param groupId
     * @param rowId
     * @return (sub)query
     */
    private StringBuilder convertRow(groupId, rowId) {
        StringBuilder rowQuery = new StringBuilder()
        if (parameters.get(facetName + groupId + "-" + rowId) != null
        && !parameters.get(facetName + groupId + "-" + rowId).isEmpty()) {
            String searchValue = null
            //check if facet-searchfield-name is in facetSearchfields
            if (facetSearchfields.get(parameters.get(facetName + groupId + "-" + rowId)) == null
            || facetSearchfields.get(parameters.get(facetName + groupId + "-" + rowId)).isEmpty()) {
                //no facet searchfield, so use value-group-row as input fieldname
                if (parameters.get(valueName + groupId + "-" + rowId) != null
                && !parameters.get(valueName + groupId + "-" + rowId).isEmpty()) {
                    searchValue = parameters.get(valueName + groupId + "-" + rowId)
                }
            }
            else {
                //facet searchfield, get value from select-box
                String selectboxName = facetSearchfields.get(parameters.get(facetName + groupId + "-" + rowId)) + "-" + groupId + "-" + rowId
                if (parameters.get(selectboxName) != null
                && !parameters.get(selectboxName).isEmpty()) {
                    searchValue = parameters.get(selectboxName)
                }
            }
            if (searchValue != null) {
                rowQuery = getRowQuery(parameters.get(facetName + groupId + "-" + rowId), searchValue, parameters.get(matchName + groupId + "-" + rowId))
            }
        }
        return rowQuery
    }

    /**
     * Convert row in searchform to (sub)query.
     * 
     * @param facet searchfield
     * @param value searchvalue
     * @param match ALL/ANY/EXACT
     * @return (sub)query
     */
    private StringBuilder getRowQuery(String facet, String value, String match) {
        StringBuilder rowQuery = new StringBuilder()
        if (facet == null || facet.isEmpty() || value == null || value.isEmpty() || match == null || match.isEmpty()) {
            return rowQuery
        }
        if (match.equalsIgnoreCase(exactName)) {
            rowQuery.append(facet).append(":(\"").append(value).append("\")")
        }
        else {
            String[] parts = value.trim().split("\\s")
            if (parts != null) {
                rowQuery.append(facet).append(":(")
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i] != null && parts[i].length() > 0) {
                        if (i > 0) {
                            if (match.equalsIgnoreCase(orName)) {
                                rowQuery.append(" OR ")
                            }
                            else if (match.equalsIgnoreCase(andName)) {
                                rowQuery.append(" AND ")
                            }
                            else {
                                return new StringBuilder()
                            }
                        }
                        rowQuery.append(parts[i])
                    }
                }
                rowQuery.append(")")
            }
        }
        return rowQuery
    }

}
