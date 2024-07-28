#!/bin/bash

DIRECTORY_DATA=$(pwd)
DIRECTORY_DATA+="/data"
DIRECTORY_RESULT=$(pwd)
DIRECTORY_RESULT="/result"

if [ -d "$DIRECTORY_DATA" ] && [ -d "$DIRECTORY_RESULT" ]; then
  echo "These directories already exist"
  exit 0;
fi

# if the current data directory does not exist create the directory
# Also create the text files that would be inserted into the folders as well
if [ ! -d "$DIRECTORY_DATA" ]; then
  mkdir data
  cd data || { echo "Error folder does not exist"; exit 1; }
  touch hello_world.txt testing.txt data.txt spaces.txt
  printf "Hello World! My name is Chris \n This is an new line" >> hello_world.txt
  printf "testing test\n tester testing test test teeeesssssst\nteeststst test\n" >> testing.txt
  printf "Lorem Ipsum is simply dummy text of the printing and typesetting industry. \n
  Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer \n
  took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, \n
  but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with \n
  the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software \n
  like Aldus PageMaker including versions of Lorem Ipsum." >> data.txt
  printf "     \n    \n     \n\n \t     \t    \n     \n \t\t\t    \n" >> spaces.txt
fi

# if a result directory does not exists then create the directory
if [ ! -d "$DIRECTORY_RESULT" ]; then
  cd ..
  mkdir result
fi
