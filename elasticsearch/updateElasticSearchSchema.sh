#!/bin/sh

###############################
# Parameter settings          #
###############################

elasticSearchServer=$1

if [ -z "$elasticSearchServer" ] 
then
    echo "ERROR: You must provide an ElasticSearch server!"
    echo "       A valid call could be: "
    echo "       shell> $0 http://whvmescidev6.fiz-karlsruhe.de:9200/"
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

contentBookmark=`readSchemaFile bookmark.json`
contentFolder=`readSchemaFile folder.json`
contentFolderList=`readSchemaFile folderList.json`
contentNewsletter=`readSchemaFile newsletter.json`
contentSavedSearch=`readSchemaFile savedSearch.json`

echo "bookmark: " $contentBookmark
echo
echo "folder: " $contentFolder
echo
echo "FolderList: " $contentFolderList
echo
echo "newsletter: " $contentNewsletter
echo
echo "savedSearch: " $contentSavedSearch
echo

echo "Schema files ok"
echo

###############################
# Posting schemas to server   #
###############################

postSchemaFile() {
  response=`export http_proxy="" && curl --request POST --data $2 --silent $elasticSearchServer/$index/$1/_mapping`
  case "`echo $response | jshon -k`" in
    *ok*) echo "ok"
          ;;
    *)    echo "ERROR: "
          echo $response | jshon
          ;;
  esac
}

echo Curling new schema to elasticsearch
echo -----------------------------------

postSchemaFile "bookmark" "$contentBookmark"
echo

postSchemaFile "folder" "$contentFolder"
echo

postSchemaFile "folderList" "$contentFolderList"
echo

postSchemaFile "newsletter" "$contentNewsletter"
echo

postSchemaFile "savedSearch" "$contentSavedSearch"
echo

echo Script finished. Exit.
echo ----------------------
echo
