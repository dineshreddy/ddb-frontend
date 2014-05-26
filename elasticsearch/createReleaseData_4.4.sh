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
postData "folderList" "sectorList" '{"title":"ddbnext.lists.sectorList","createdAt":1397648865712,"users":["5e2d10109f9011b02ae381f2fc2731ef"],"folders":[]}'
postData "folderList" "dailyList" '{"title":"ddbnext.lists.dailyList","createdAt":1397648865712,"users":["5e2d10109f9011b02ae381f2fc2731ef"],"folders":[]}'
