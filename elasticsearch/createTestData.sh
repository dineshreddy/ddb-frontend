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
postData "folder" "folder1" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"Folder 1","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder2" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder2","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder3" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder3","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder4" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder4","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder5" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder5","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder6" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder6","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder7" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder7","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder8" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder8","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder9" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder9","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder10" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder10","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder11" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder11","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder12" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder12","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder13" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder13","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder14" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder14","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder15" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder15","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder16" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder16","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder17" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder17","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder18" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder18","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder19" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder19","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder20" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder20","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder21" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder21","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder22" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder22","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder23" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder23","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder24" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder24","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder25" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder25","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder26" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder26","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder27" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder27","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder28" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder28","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder29" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder29","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder30" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder30","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder31" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder31","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder32" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder32","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder33" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder33","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder34" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder34","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder35" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder35","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder36" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder36","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder37" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder37","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder38" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder38","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1297648865712,"updatedAt":1397648865712}'
postData "folder" "folder39" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder39","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":false,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder40" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder40","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":true,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder41" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder41","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":true,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'
postData "folder" "folder42" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","title":"folder42","description":"Das%20ist%20ein%20neuer%20Folder","isPublic":true,"publishingName":"boz1","isBlocked":true,"blockingToken":"","createdAt":1397648865712,"updatedAt":1397648865712}'


#Create some bookmarks
postData "bookmark" "bookmark1" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","folder":["folder1"],"item":"6L7UG35FLDGWZWZSE3F62FN4YNDYNQ3W","createdAt":1297651865531,"type":"CULTURAL_ITEM","description":"","updatedAt":1397651865538}'
postData "bookmark" "bookmark2" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","folder":["folder3"],"item":"EBX4XQNBKFVNYPI7GK2C6IQMZHUJMS6J","createdAt":1297651865531,"type":"CULTURAL_ITEM","description":"","updatedAt":1397651865538}'
postData "bookmark" "bookmark3" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","folder":["folder1"],"item":"OGM7UE7BAJSJW5SECNI7UZ66DVCMMBTH","createdAt":1397651865531,"type":"CULTURAL_ITEM","description":"","updatedAt":1397651865538}'
postData "bookmark" "bookmark4" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","folder":["folder3"],"item":"PVBUZFYUVQ6FH4A4TL2YKMXEW5MG7MUD","createdAt":1397651865531,"type":"CULTURAL_ITEM","description":"","updatedAt":1397651865538}'
postData "bookmark" "bookmark5" '{"user":"5e2d10109f9011b02ae381f2fc2731ef","folder":["folder1"],"item":"KZXBLP77CWL4WFAX6HGYWVZF2PJDFCLP","createdAt":1297651865531,"type":"CULTURAL_ITEM","description":"","updatedAt":1397651865538}'


#Create some folderLists
postData "folderList" "folderList1" '{"title":"List1Benutzer","createdAt":1397648865712,"users":["5e2d10109f9011b02ae381f2fc2731ef"],"folders":[]}'
postData "folderList" "folderList2" '{"title":"List2Ordner","createdAt":1397648865712,"users":[],"folders":["folder1","folder3"]}'
