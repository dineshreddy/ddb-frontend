#!/bin/bash

# https://jira.deutsche-digitale-bibliothek.de/browse/DDBTASK-783
# add a list of folderId,institutionId to ElasticSearch

PROGRAM_NAME=`basename $0`
USAGE="*** usage: $PROGRAM_NAME <ElasticSearch server URL>"

# check arguments
if [ $# -ne 1 ]; then
  echo "$USAGE"
  exit 20
fi

elasticSearchServer=$1
index=ddb
type=folder
export http_proxy=""

addAllInstitutionIds() {
  cat $1 | while read line; do
    if [ -n "$line" ]; then
      count=`echo "$line" | awk '{print NF}'`
      if [ "${line:0:1}" != "#" ]; then
        if [ $count -ge 2 ]; then
          folderId=`echo "$line" | awk '{print $1}'`
          institutionIds=""
          for ((i = 2; i <= $count; i++)) do
            if [ -n "$institutionIds" ]; then
              institutionIds+=","
            fi
            institutionIds+='"'
            institutionIds+=`echo "$line" | awk -v i=$i '{print $i}'`
            institutionIds+='"'
          done
          addInstitutionIds $folderId $institutionIds
        else
          echo "cannot parse line $line"
        fi
      fi
    fi
  done
}

addInstitutionIds() {
  curl --request POST --silent $elasticSearchServer/$index/$type/$1/_update --data "{
    \"script\" : \"ctx._source.institutionIds = institutionId\",
    \"params\" : {
        \"institutionId\" : [$2]
    }
  }" > /dev/null
}

deleteAllInstitutionIds() {
  folders=`searchAllFoldersWithInstitutionIds`
  count=`echo $folders | jshon -e hits -e hits -l`
  for ((i = 0; i < $count; i++)) do
    folderId=`echo $folders | jshon -e hits -e hits -e $i -e "_id"`
    # strip quotes
    folderId=`eval "echo $folderId"`
    deleteInstitutionIds $folderId
  done
}

deleteInstitutionIds() {
  curl --request POST --silent $elasticSearchServer/$index/$type/$1/_update --data '{
    "script" : "ctx._source.remove(\"institutionIds\")"
  }' > /dev/null
}

refresh() {
  curl --request POST --silent $elasticSearchServer/$index/_refresh > /dev/null
}

searchAllFoldersWithInstitutionIds() {
  response=`curl --request GET --silent $elasticSearchServer/$index/$type/_search?q=institutionIds:*`
  echo $response
}

deleteAllInstitutionIds
refresh
addAllInstitutionIds "institutions"
