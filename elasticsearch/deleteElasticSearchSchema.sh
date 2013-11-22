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
    echo "       Schema was not deleted."
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


###############################
# Deletng schemas from server #
###############################

deleteSchemaFile() {
  response=`curl --request DELETE --silent $elasticSearchServer/$index/$1/_mapping`
  case "`echo $response | jshon -k`" in
    *ok*) echo "ok"
          ;;
    *)    echo "ERROR: "
          echo $response | jshon
          ;;
  esac
}

echo Deleting schema from elasticsearch
echo ----------------------------------
                

deleteSchemaFile "folder"
echo

deleteSchemaFile "bookmark"
echo

deleteSchemaFile "savedSearch"
echo

echo Script finished. Exit.
echo ----------------------
echo
