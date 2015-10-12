#!/bin/bash

###############################
# Parameter settings          #
###############################

elasticSearchServer=$1
debug=$2

if [ -z "$elasticSearchServer" ] 
then
    echo "ERROR: You must provide an ElasticSearch server!"
    echo "       A valid call could be: "
    echo "       shell> $0 http://whvmescidev6.fiz-karlsruhe.de:9200/"
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
  cat $1
}

echo
echo Reading schema files
echo --------------------

contentFolderList=`readSchemaFile folderList.json`
contentFolder=`readSchemaFile folder.json`
contentBookmark=`readSchemaFile bookmark.json`
contentSavedSearch=`readSchemaFile savedSearch.json`

echo "folderList: " $contentFolderList
echo
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
  echo "$1:"
  response=`export http_proxy="" && curl --silent $elasticSearchServer/$index/$1/_mapping | jshon -e $index -e mappings -S`
  diff --brief <(echo "$2") <(echo "$response")
  if [ $? -ne 0 ]; then
    if [ -n "$debug" ]; then
      diff --ignore-space-change --side-by-side <(echo "$2") <(echo "$response") | less
    fi
  else
    echo "no difference found"
  fi
}

echo Comparing current schema with elasticsearch
echo -------------------------------------------
                
compareSchemaFile "folderList" "$contentFolderList"
echo

compareSchemaFile "folder" "$contentFolder"
echo

compareSchemaFile "bookmark" "$contentBookmark"
echo

compareSchemaFile "savedSearch" "$contentSavedSearch"
echo

echo Script finished. Exit.
echo ----------------------
echo
