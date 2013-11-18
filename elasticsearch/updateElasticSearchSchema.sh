#!/bin/sh

###############################
# Parameter settings          #
###############################


elasticSearchServer=$1

if [ -z "$elasticSearchServer" ] 
then
    echo "ERROR: You must provide an ElasticSearch server!"
    echo "       A valid call could be: "
    echo "       shell> updateElasticSearchServer.sh whvmescidev6.fiz-karlsruhe.de:9200"
    echo "       Schema was not updated."
    exit 1 #exit script
fi
echo
echo Using ElasticSearch server
echo --------------------------
echo $elasticSearchServer


###############################
# Configuration settings      #
###############################

index=ddb

echo
echo Using ElasticSearch index
echo -------------------------
echo $index


###############################
# Reading schema files        #
###############################

echo
echo Reading schema files
echo --------------------

contentFolder=`cat folder.json`
contentBookmark=`cat bookmark.json`
contentSavedSearch=`cat savedSearch.json`

echo "folder: " $contentFolder
echo
echo "bookmark: " $contentBookmark
echo
echo "savedSearch: " $contentSavedSearch
echo

###############################
# Validating schema files     #
###############################

echo Validate schema files
echo ---------------------

if [ -z "$contentFolder" ]
then
    echo "ERROR: no content in mandatory file folder.json. Exiting script. ElasticSearch schema was not updated"
    exit 1 #exit script
fi

if [ -z "$contentBookmark" ]
then
    echo "ERROR: no content in mandatory file bookmark.json. Exiting script. ElasticSearch schema was not updated"
    exit 1 #exit script
fi
        
if [ -z "$contentSavedSearch" ]
then
    echo "ERROR: no content in mandatory file savedSearch.json. Exiting script. ElasticSearch schema was not updated"
    exit 1 #exit script
fi
echo "Schema files ok"
echo

###############################
# Posting schemas to server   #
###############################

echo Curling new schema to elasticsearch
echo -----------------------------------
                

echo curl -XPOST $elasticSearchServer/$index/folder/_mapping -d "$contentFolder"
curl -XPOST $elasticSearchServer/$index/folder/_mapping -d "$contentFolder"
echo

echo curl -XPOST $elasticSearchServer/$index/bookmark/_mapping -d "$contentBookmark"
curl -XPOST $elasticSearchServer/$index/bookmark/_mapping -d "$contentBookmark"
echo

echo curl -XPOST $elasticSearchServer/$index/savedSearch/_mapping -d "$contentSavedSearch"
curl -XPOST $elasticSearchServer/$index/savedSearch/_mapping -d "$contentSavedSearch"
echo

echo Script finished. Exit.
echo ----------------------
echo

