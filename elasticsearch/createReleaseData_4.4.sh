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
# Delete old test list: http://whvmescidev6.fiz-karlsruhe.de:9200/ddb/folderList/_query?q=title:*

#Search ids for users ddb-daily ddb-domain and add them to field users 
postData "folderList" "dailyList" '{"title":"ddbnext.lists.dailyList","createdAt":1401173343936,"users":["3fb89f5f12e1f6781b245a1f8db270ec","0edb4f2774e46b72959f9a79ee641746"],"folders":[]}'
