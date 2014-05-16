#!/bin/bash

# $id$
# compare all schemas of all elastic search instances with the saved schema files

cat .instances | while read instance; do
  if [ -n $instance -a ${instance:0:1} != "#" ]; then
    ./compareElasticSearchSchema.sh $instance
  fi
done
