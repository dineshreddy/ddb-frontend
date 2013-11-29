#!/bin/bash

###############################
# Parameter settings          #
###############################

elasticSearchServer=$1

if [ -z "$elasticSearchServer" ] 
then
    echo "ERROR: You must provide an ElasticSearch server!"
    echo "       A valid call could be: "
    echo "       shell> $0 http://whvmescidev6.fiz-karlsruhe.de:9200/"
    echo "       Schema was not retrieved."
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
echo


##################################
# Retrieving schemas from server #
##################################

retrieveSchemaFile() {
  curl --output $1.json --silent $elasticSearchServer/$index/$1/_mapping
}

echo Retrieving schema from elasticsearch
echo ------------------------------------
                

retrieveSchemaFile "folder"
echo

retrieveSchemaFile "bookmark"
echo

retrieveSchemaFile "savedSearch"
echo

echo Script finished. Exit.
echo ----------------------
echo
