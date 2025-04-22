#!/bin/bash

# Output file where all classes will be concatenated
OUTPUT_FILE="all_java_classes.txt"

# Check if src directory exists
if [ ! -d "src" ]; then
  echo "Error: 'src' directory not found in the current path."
  exit 1
fi

# Clear the output file if it exists or create a new one
> "$OUTPUT_FILE"

# Find all .java files recursively in src directory and process them
find src -type f -name "*.java" | while read -r file; do
  echo "Processing: $file"
  echo "=== File: $file ===" >> "$OUTPUT_FILE"
  cat "$file" >> "$OUTPUT_FILE"
  echo -e "\n\n" >> "$OUTPUT_FILE"
done

echo "All Java classes have been concatenated into $OUTPUT_FILE"