#!/bin/bash 

array=( "$@" )
instances_dir="${array[0]}"
nVehicles="${array[1]}"
nMaxCustomers="${array[2]}"
builder="${array[3]}"
percent="${array[4]}"
iteration="${array[5]}"
environment1="${array[6]}"
environment2="${array[7]}"
environment3="${array[8]}"
environment4="${array[9]}"
environment5="${array[10]}"
environment6="${array[11]}"

for entry in "$instances_dir"/*
do
  echo "Running lns with $entry file"
  echo
  java -jar LNS.jar $entry $nVehicles $nMaxCustomers $builder $percent $iteration $environment1 $environment2 $environment3 $environment4 $environment5 $environment6
done