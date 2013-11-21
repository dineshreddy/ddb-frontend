#!/bin/bash

###############################
# Parameter settings          #
###############################

elasticSearchServer=$1

if [ -z "$elasticSearchServer" ] 
then
    echo "ERROR: You must provide an ElasticSearch server!"
    echo "       A valid call could be: "
    echo "       shell> $0 whvmescidev6.fiz-karlsruhe.de:9200"
    echo "       Schema was not compared."
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

readSchemaFile() {
  if [ ! -f "$1" ]; then
    echo "ERROR: no content in mandatory file $1. Exiting script. ElasticSearch schema was not updated"
    exit 1 #exit script
  fi
  cat $1 | jshon
}

echo
echo Reading schema files
echo --------------------

contentFolder=`readSchemaFile folder.json`
contentBookmark=`readSchemaFile bookmark.json`
contentSavedSearch=`readSchemaFile savedSearch.json`

echo "folder: " $contentFolder
echo
echo "bookmark: " $contentBookmark
echo
echo "savedSearch: " $contentSavedSearch
echo

echo "Schema files ok"
echo

#################################
# Comparing schemas from server #
#################################

compareSchemaFile() {
  response=`curl --silent $elasticSearchServer/$index/$1/_mapping | jshon`
  diff --brief <(echo "$2") <(echo "$response")
  if [ $? -ne 0 ]; then
    diff --side-by-side <(echo "$2") <(echo "$response") | less
  else
    echo "no difference found"
  fi
}

echo Curling new schema to elasticsearch
echo -----------------------------------
                

compareSchemaFile "folder" "$contentFolder"
echo

compareSchemaFile "bookmark" "$contentBookmark"
echo

compareSchemaFile "savedSearch" "$contentSavedSearch"
echo

echo Script finished. Exit.
echo ----------------------
echo
