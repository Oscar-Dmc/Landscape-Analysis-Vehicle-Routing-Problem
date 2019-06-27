#!/bin/bash 

array=( "$@" )
instances_dir="${array[0]}"
nVehicles="${array[1]}"
nMaxCustomers="${array[2]}"
builder="${array[3]}"
environment1="${array[4]}"
environment2="${array[5]}"
environment3="${array[6]}"
environment4="${array[7]}"
environment5="${array[8]}"
environment6="${array[9]}"

for entry in "$instances_dir"/*
do
  echo "Running vns with $entry file"
  echo
  java -jar VNS.jar $entry $nVehicles $nMaxCustomers $builder $environment1 $environment2 $environment3 $environment4 $environment5 $environment6
done