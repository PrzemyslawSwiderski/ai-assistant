#!/bin/sh
set -e  # Exit on failure

# Start Ollama server in the background
ollama serve &

# Wait a bit to ensure it's ready
sleep 3

# Enable debug mode to log commands
#set -x

# Pull desired AI model
ollama pull $AI_MODEL

# Check if a file and query are provided as arguments
if [ $# -ne 2 ]; then
    echo "Usage: $0 <context_file> <query>"
    exit 1
fi

# Assign the arguments to variables
context_file="$1"
query="$2"

# Check if the context file exists
if [ ! -f "$context_file" ]; then
    echo "Error: File '$context_file' not found."
    exit 1
fi

# Read the context from the file
context=`cat $context_file`

echo "Loaded context file content:"
echo "$context"

multiline_query="Considering the following XML:
<xml>
  <code>
  $context
  </code>
  <query>
  $query
  </query>
</xml>
Adjust <code> with the query provided by <query>.
Print updated <code> element.
"

echo "$multiline_query"

output_file="output.txt"
ollama run $AI_MODEL "$multiline_query" > "$output_file"

echo "Output"
cat "$output_file"
echo "Output saved to $output_file"
