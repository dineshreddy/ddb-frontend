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


#Create some folders
postData "folder" "folder1" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"Erster%20Folder","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712}'
postData "folder" "folder2" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"Zweiter%20Folder","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712}'
postData "folder" "folder3" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"Dritter%20Folder","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712}'
postData "folder" "folder4" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"Vierter%20Folder","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712}'
postData "folder" "folder5" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"Fuenfter%20Folder","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712}'

#Create some bookmarks
postData "bookmark" "bookmark1" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","folder":["folder1"],"item":"6L7UG35FLDGWZWZSE3F62FN4YNDYNQ3W","createdAt":1297651865531,"type":"CULTURAL_ITEM","description":"","updatedAt":1397651865538}'
postData "bookmark" "bookmark2" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","folder":["folder3"],"item":"EBX4XQNBKFVNYPI7GK2C6IQMZHUJMS6J","createdAt":1297651865531,"type":"CULTURAL_ITEM","description":"","updatedAt":1397651865538}'
postData "bookmark" "bookmark3" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","folder":["folder1"],"item":"OGM7UE7BAJSJW5SECNI7UZ66DVCMMBTH","createdAt":1397651865531,"type":"CULTURAL_ITEM","description":"","updatedAt":1397651865538}'
postData "bookmark" "bookmark4" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","folder":["folder3"],"item":"PVBUZFYUVQ6FH4A4TL2YKMXEW5MG7MUD","createdAt":1397651865531,"type":"CULTURAL_ITEM","description":"","updatedAt":1397651865538}'
postData "bookmark" "bookmark5" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","folder":["folder1"],"item":"KZXBLP77CWL4WFAX6HGYWVZF2PJDFCLP","createdAt":1297651865531,"type":"CULTURAL_ITEM","description":"","updatedAt":1397651865538}'


#Create some folderLists
postData "folderList" "folderList1" '{"title":"List1Benutzer","createdAt":1397648865712,"users":["5e2d10109f9011b02ae381f2fc2731ef"],"folders":[]}'
postData "folderList" "folderList2" '{"title":"List2Ordner","createdAt":1397648865712,"users":[],"folders":["folder1","folder3"]}'
