#!/bin/bash

PROGRAM_NAME=`basename $0`
USAGE="*** usage: $PROGRAM_NAME <ElasticSearch server URL>"

# check arguments
if [ $# -ne 1 ]; then
  echo "$USAGE"
  exit 20
fi

elasticSearchServer=$1
index=ddb
type=folderList
id=DdbCollectionsList
export http_proxy=""

addAllUserIds() {
  userIds=""
  while read line; do
    if [ -n "$line" ]; then
      count=`echo "$line" | awk '{print NF}'`
      if [ "${line:0:1}" != "#" ]; then
        if [ $count -ge 1 ]; then
          userId=`echo "$line" | awk '{print $1}'`
           if [ -n "$userIds" ]; then
              userIds+=","
            fi
            userIds+='"'
            userIds+=$userId
            userIds+='"'
        else
          echo "cannot parse line $line"
        fi
      fi
    fi
  done < <(cat $1)
  updateFolderList $userIds
}

updateFolderList() {
  curl --request POST --silent "$elasticSearchServer/$index/$type/$id/_update" -d "
  {
    \"doc\": { \"users\": [$1], \"folders\": [] }
  }" > /dev/null
}

addAllUserIds "collectionListUsers"
