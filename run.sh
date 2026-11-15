#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="$SCRIPT_DIR/src"
OUT_DIR="$SCRIPT_DIR/out"

mkdir -p "$OUT_DIR"

mapfile -t JAVA_FILES < <(find "$SRC_DIR" -name "*.java" | sort)
if [ "${#JAVA_FILES[@]}" -eq 0 ]; then
    echo "No Java source files found in $SRC_DIR" >&2
    exit 1
fi

javac -d "$OUT_DIR" "${JAVA_FILES[@]}"
exec java -cp "$OUT_DIR" marsops.Main "$@"
