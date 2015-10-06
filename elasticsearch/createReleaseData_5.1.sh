#!/bin/sh

###############################
# Parameter settings          #
###############################

elasticSearchServer=$1

if [ -z "$elasticSearchServer" ] 
then
    echo "ERROR: You must provide an ElasticSearch server!"
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
# Posting data to server      #
###############################

postData() {
  echo Posting data to $elasticSearchServer/$index/$1/
  
  response=`export http_proxy="" && curl --request POST --data $3 --silent $elasticSearchServer/$index/$1/$2`
  echo $response
  case "`echo $response | jshon -k`" in
    *ok*) echo "ok"
          ;;
    *)    echo "ERROR: "
          echo $response | jshon
          ;;
  esac
}

#Create some folderLists

# Search ids for users "cn=dkult" and "cn=Servicestelle" and add them to field users 
#
# ddbelse-t1:
#   ddb-collections: 46a116fe8c1ba0b34724874076de37fe
#
# ddbelse-p1:
#   ddb-collections: af43fc5bfa951c88e8e4b4574b39a65a 7d0e8b57644c9b67a976f0b1630b1c48

postData "folderList" "DdbCollectionsList" "{\"title\":\"ddbcommon.lists.collectionsList\",\"createdAt\":`date +%s`,\"users\":[\"af43fc5bfa951c88e8e4b4574b39a65a\",\"7d0e8b57644c9b67a976f0b1630b1c48\"],\"folders\":[]}"
