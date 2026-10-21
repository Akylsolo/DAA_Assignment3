#!/usr/bin/env sh
if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
else
    echo "Error: mvn command not found in PATH." >&2
    exit 1
fi
